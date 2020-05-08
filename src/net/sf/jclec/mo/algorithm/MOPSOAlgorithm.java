/*
This file belongs to JCLEC-MO, a Java library for the
application and development of metaheuristic algorithms 
for the resolution of multi-objective and many-objective 
optimization problems.

Copyright (C) 2018.  A. Ramirez, J.R. Romero, S. Ventura.
Knowledge Discovery and Intelligent Systems Research Group.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <https://www.gnu.org/licenses/>.
*/

package net.sf.jclec.mo.algorithm;

import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationRuntimeException;

import net.sf.jclec.IConfigure;
import net.sf.jclec.IEvaluator;
import net.sf.jclec.IFitness;
import net.sf.jclec.IIndividual;
import net.sf.jclec.IProvider;
import net.sf.jclec.ISpecies;
import net.sf.jclec.algorithm.PSOAlgorithm;
import net.sf.jclec.fitness.ValueFitnessComparator;
import net.sf.jclec.mo.IMOAlgorithm;
import net.sf.jclec.mo.IMOEvaluator;
import net.sf.jclec.mo.command.NonDominatedSolutionsExtractor;
import net.sf.jclec.mo.comparator.fcomparator.MOFitnessComparator;
import net.sf.jclec.mo.comparator.fcomparator.ParetoComparator;
import net.sf.jclec.mo.evaluation.Objective;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;
import net.sf.jclec.mo.strategy.MOPSOStrategy;
import net.sf.jclec.mo.strategy.MOStrategy;
import net.sf.jclec.mo.strategy.MOStrategyContext;
import net.sf.jclec.pso.Particle;
import net.sf.jclec.util.random.IRandGenFactory;

/**
 * A generic PSO algorithm to solve multi-objective problems. It implements the general
 * optimization process, where the selection of leaders, the management of the archive, 
 * the update of the velocities and positions and the execution of the optional turbulence 
 * mechanism are delegated to the multi-objective strategy.
 * 
 * <p>HISTORY:
 * <ul>
 * 	<li>(AR|JRR|SV, 1.0, March 2018)		First release.</li>
 * </ul>
 * </p>
 * 
 * @version 1.0
 * 
 * @author Aurora Ramirez (AR)
 * @author Jose Raul Romero (JRR)
 * @author Sebastian Ventura (SV)
 * 
 * <p>Knowledge Discovery and Intelligent Systems (KDIS) Research Group: 
 * {@link http://www.uco.es/grupos/kdis}</p>
 * 
 * @see IMOAlgorithm
 * @see PSOAlgorithm
 * @see MOPSOStrategy
 * */
public class MOPSOAlgorithm extends PSOAlgorithm implements IMOAlgorithm{

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 6968157711915377786L;

	/** Multi-objective strategy */
	protected MOPSOStrategy strategy;

	/** The external archive */
	protected List<IIndividual> archive;

	/** Initial execution time */
	protected long initTime;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public  MOPSOAlgorithm() {
		super();
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	// PSOAlgorithm methods

	/**
	 * Set the provider and contextualize it.
	 * @param provider The provider that has to be set.
	 * */
	@Override
	public void setProvider(IProvider provider) {	
		// Set provider
		this.provider = provider;
		this.provider.contextualize(getContext());
	}
	
	/**
	 * Set the species. The context is also updated.
	 * @param species The species object that has to be set.
	 * */
	@Override
	public void setSpecies(ISpecies species) {
		this.species = species;
		getContext().setSpecies(this.species);
	}
	
	/**
	 * Set the evaluator. The context is also updated.
	 * @param evaluator The evaluator object that has to be set.
	 * */
	@Override
	public void setEvaluator(IEvaluator evaluator) {
		this.evaluator = evaluator;
		getContext().setEvaluator(this.evaluator);
	}
	
	/**
	 * Set the generator of random numbers. The context is also updated.
	 * @param randGenFactory The generator object that has to be set.
	 * */
	@Override
	public void setRandGenFactory(IRandGenFactory randGenFactory) {
		this.randGenFactory = randGenFactory;
		getContext().setRanGenFactory(this.randGenFactory.createRandGen());
	}

	/**
	 * {@inheritDoc}
	 * <p>The multi-objective strategy is also initialized.</p>
	 * */
	@Override
	protected void doInit(){

		// Start counting the execution time
		this.initTime = System.currentTimeMillis();

		// Initialize the swarm
		this.swarm = this.provider.provide(this.swarmSize);

		// Evaluate particles
		this.evaluator.evaluate(this.swarm);

		// Initialize the local memory of each particle
		for(int i=0; i<swarmSize; i++){
			((Particle)this.swarm.get(i)).setBestFitness(this.swarm.get(i).getFitness());
		}

		// Update global memories
		updateMemories();

		// Initialize strategy and store the initial archive
		this.archive = this.strategy.initialize(this.swarm);

		// Update leaders
		updateLeaders();
		
		// Set the populations in context
		this.strategy.getContext().setInhabitants(this.swarm);
		this.strategy.getContext().setArchive(this.leaders);

		// Do Control
		doControl();
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	protected void doMovement(){

		// Update the generation in the context
		this.strategy.getContext().setGeneration(this.generation);

		// Execute the normal PSO iteration
		super.doMovement();
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	protected void doReplacement() {
		this.survivors = this.strategy.environmentalSelection(this.swarm, this.disturbedSwarm, this.archive);
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	protected void doUpdate(){

		// Update archive
		this.archive = this.strategy.updateArchive(this.swarm, this.disturbedSwarm, this.archive);

		// Update swarm
		this.swarm = this.survivors;

		// Select leaders for the next generation
		updateLeaders();

		// Update memories
		updateMemories();

		// Clean intermediate sets
		this.disturbedSwarm = null;
		this.survivors = null;

		// Update strategy
		this.strategy.update();

		// Update populations in context
		this.strategy.getContext().setInhabitants(this.swarm);
		this.strategy.getContext().setArchive(this.archive);
	}

	/**
	 * {@inheritDoc}
	 * <p>Specific parameters for <code>MOAlgorithm</code>:
	 * <ul>
	 * 	<li>mo-strategy: <code>MOStrategy</code> (complex)
	 * <p>Multi-objective evolutionary strategy</li>
	 * </ul>
	 * </p>
	 * */
	@Override
	public void configure(Configuration settings) {

		// Evaluator is configured in the super class
		super.configure(settings);

		// Configure the evolutionary strategy
		setStrategySettings(settings);

		// Set context in provider
		this.provider.contextualize(getContext());

		// Set the fitness comparator and the fitness type in the evaluator
		if(this.evaluator instanceof IMOEvaluator){
			((IMOEvaluator)this.evaluator).setComparator(this.strategy.getSolutionComparator().getFitnessComparator());
			((IMOEvaluator)this.evaluator).setFitnessPrototype(this.strategy.getContext().getFitnessPrototype());
		}
		else
			throw new IllegalArgumentException("Evaluator should extend IMOEvaluator interface");
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	protected void updateVelocities(){
		this.strategy.updateVelocities(this.swarm, this.leaders);
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	protected void updatePositions(){
		this.strategy.updatePositions(this.swarm);
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	protected void doVariation(){

		// Apply the turbulence mechanism
		this.disturbedSwarm = ((MOPSOStrategy)this.strategy).turbulence(this.swarm);

		// Evaluate the new solutions
		this.evaluator.evaluate(this.disturbedSwarm);

		// Set the best position and fitness
		Particle particle;
		int size = this.disturbedSwarm.size();
		for(int i=0; i<size; i++){
			particle = (Particle)this.disturbedSwarm.get(i);
			particle.setBestPosition(particle.getPosition());
			particle.setBestFitness(particle.getFitness());
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>In MOPSO, leaders are selected by the strategy.</p>
	 * */
	@Override
	protected void updateLeaders(){
		// Select leaders
		this.leaders = this.strategy.matingSelection(this.swarm, this.archive);
	}

	/**
	 * {@inheritDoc}
	 * <p>The current position and the best position are compared according to the
	 * configured comparator of solutions.</p>
	 * */
	@Override
	protected void updateMemories(){
		Particle particle;
		MOFitness currentFitness, bestFitness;
		MOFitnessComparator comparator = getStrategy().getSolutionComparator().getFitnessComparator();
		for(int i=0; i<this.swarmSize; i++){
			particle = (Particle)this.swarm.get(i);
			currentFitness = (MOFitness) particle.getFitness();
			bestFitness = (MOFitness) particle.getBestFitness();
			// 1: i dominates j, -1: j dominates i, 0: non-dominated
			if(comparator.compare(currentFitness, bestFitness) >= 0){
				particle.setBestFitness(currentFitness);
				particle.setBestPosition(particle.getPosition());
			}
		}
	}

	// IMOAlgorithm interface methods

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public long executionTime(){
		return (System.currentTimeMillis() - this.initTime);
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public List<IIndividual> getArchive(){
		return this.archive;
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public List<IIndividual> getNonDominatedSolutions(){
		MOFitnessComparator fcomparator = (MOFitnessComparator)this.evaluator.getComparator();
		ParetoComparator comparator = new ParetoComparator(fcomparator.getComponentComparators());
		NonDominatedSolutionsExtractor command = new NonDominatedSolutionsExtractor(this.swarm, comparator);
		command.execute();
		return command.getNonDominatedSolutions();
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public List<IIndividual> getNonDominatedSolutionsFromArchive(){
		if(this.archive != null){
			MOFitnessComparator fcomparator = (MOFitnessComparator)this.evaluator.getComparator();
			ParetoComparator comparator = new ParetoComparator(fcomparator.getComponentComparators());
			NonDominatedSolutionsExtractor command = new NonDominatedSolutionsExtractor(this.archive, comparator);
			command.execute();
			return command.getNonDominatedSolutions();
		}
		else 
			return null;
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public MOStrategy getStrategy(){
		return this.strategy;
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void setStrategy(MOStrategy strategy){
		this.strategy = (MOPSOStrategy)strategy;
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public MOStrategyContext getContext(){
		if(this.strategy!=null)
			return this.strategy.getContext();
		else
			return null;
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void contextualizeStrategy(){
		if(this.strategy!=null){
			MOStrategyContext context = 
					new MOStrategyContext(this.randGenFactory.createRandGen(), 
							this.species, this.evaluator, this.strategy.getSolutionComparator(), this.swarmSize);
			context.setMaxGenerations(this.maxOfGenerations);
			context.setMaxEvaluations(this.maxOfEvaluations);
			this.strategy.setContext(context);
		}
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public Comparator<IFitness>[] createComponentsComparator() {
		Comparator<IFitness> componentComparators [] = null;
		if(this.evaluator != null && this.evaluator instanceof IMOEvaluator){
			List<Objective> objectives = ((IMOEvaluator)this.evaluator).getObjectives();
			int nObj = objectives.size();
			componentComparators = new ValueFitnessComparator[nObj];
			Objective obj;
			for(int i=0; i<nObj; i++){
				obj = objectives.get(i);
				componentComparators[i] = new ValueFitnessComparator(!obj.isMaximized());
			}
		}
		return componentComparators;
	}

	/////////////////////////////////////////////////////////////////
	// -------------------------------------------- Protected methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Configure the generator of random numbers without contextualizing it.
	 * @param settings Configuration object.
	 * */
	@SuppressWarnings("unchecked")
	protected void setRandGenSettings(Configuration settings) {
		try {
			// Get the class name
			String randGenFactoryClassname = settings.getString("rand-gen-factory[@type]");
			Class<? extends IRandGenFactory> randGenFactoryClass = 
				(Class<? extends IRandGenFactory>) Class.forName(randGenFactoryClassname);
			// Create the instance
			this.randGenFactory = randGenFactoryClass.getDeclaredConstructor().newInstance();
			// Configure the factory
			if (this.randGenFactory instanceof IConfigure) {
				((IConfigure) this.randGenFactory).configure
					(settings.subset("rand-gen-factory"));
			}
		} 
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new ConfigurationRuntimeException("Problems creating an instance of random generators factory", e);
		}
	}
	
	/**
	 * Configure the species without contextualizing it.
	 * @param settings Configuration object.
	 * */
	@SuppressWarnings("unchecked")
	protected void setSpeciesSettings(Configuration settings) {
		try {
			// Get the class name
			String speciesClassname = settings.getString("species[@type]");
			Class<? extends ISpecies> speciesClass = (Class<? extends ISpecies>) Class.forName(speciesClassname);
			// Create the instance
			this.species = speciesClass.getDeclaredConstructor().newInstance();
			// Configure the species
			if (this.species instanceof IConfigure) {
				((IConfigure) this.species).configure(settings.subset("species"));
			}
		} 
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new ConfigurationRuntimeException("Problems creating an instance of species", e);
		}
	}
	
	/**
	 * Configure the species without contextualizing it.
	 * @param settings Configuration object.
	 * */
	@SuppressWarnings("unchecked")
	protected void setEvaluatorSettings(Configuration configuration) {
		try {
			// Get the class
			String evaluatorClassname = configuration.getString("evaluator[@type]");
			Class<? extends IEvaluator> evaluatorClass = (Class<? extends IEvaluator>) Class.forName(evaluatorClassname);
			// Create the instance
			this.evaluator = evaluatorClass.getDeclaredConstructor().newInstance();
			// Configure evaluator
			if (this.evaluator instanceof IConfigure) {
				((IConfigure) this.evaluator).configure(configuration.subset("evaluator"));
			}
		} 
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new ConfigurationRuntimeException("Problems creating an instance of evaluator", e);
		}
	}
	
	/**
	 * Configure the provider without contextualizing it.
	 * @param settings Configuration object.
	 * */
	@SuppressWarnings("unchecked")
	protected void setProviderSettings(Configuration settings) {
		try {
			// Provider classname
			String providerClassname = settings.getString("provider[@type]");
			// Provider class
			Class<? extends IProvider> providerClass = (Class<? extends IProvider>) Class.forName(providerClassname);
			// Provider instance
			this.provider = providerClass.getDeclaredConstructor().newInstance();
			// Configure provider
			if (this.provider instanceof IConfigure) {
				((IConfigure) this.provider).configure(settings.subset("provider"));
			}
		} 
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new ConfigurationRuntimeException("Problems creating an instance of provider", e);
		}
	}
	
	/**
	 * Configure the strategy.
	 * @param settings Configuration object.
	 * */
	@SuppressWarnings("unchecked")
	protected void setStrategySettings(Configuration settings){
		// Configure the evolutionary strategy
		try {
			String classname = settings.getString("mo-strategy[@type]");
			Class<? extends MOPSOStrategy> objClass = (Class<? extends MOPSOStrategy>) Class.forName(classname);
			MOStrategy strategy = objClass.getDeclaredConstructor().newInstance();

			// Set the comparators for objectives
			strategy.createSolutionComparator(createComponentsComparator());
			setStrategy(strategy);

			// Configure the execution context
			contextualizeStrategy();

			// Continue with the specific configuration of the selected strategy
			if(strategy instanceof IConfigure){
				((IConfigure)strategy).configure(settings.subset("mo-strategy"));
			}
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new ConfigurationRuntimeException("Problems creating an instance of the strategy", e);
		}
	}
}
