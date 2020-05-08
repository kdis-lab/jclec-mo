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

import net.sf.jclec.algorithm.PopulationAlgorithm;
import net.sf.jclec.fitness.ValueFitnessComparator;
import net.sf.jclec.mo.IMOAlgorithm;
import net.sf.jclec.mo.IMOEvaluator;
import net.sf.jclec.mo.command.NonDominatedSolutionsExtractor;
import net.sf.jclec.mo.comparator.fcomparator.MOFitnessComparator;
import net.sf.jclec.mo.comparator.fcomparator.ParetoComparator;
import net.sf.jclec.mo.evaluation.Objective;
import net.sf.jclec.mo.strategy.MOEAD;
import net.sf.jclec.mo.strategy.MOStrategy;
import net.sf.jclec.mo.strategy.MOStrategyContext;
import net.sf.jclec.util.random.IRandGenFactory;
import net.sf.jclec.IConfigure;
import net.sf.jclec.IEvaluator;
import net.sf.jclec.IFitness;
import net.sf.jclec.IIndividual;
import net.sf.jclec.IProvider;
import net.sf.jclec.ISpecies;

/**
 * An abstract algorithm to solve multi-objective problems. It implements a general
 * evolutionary process, where selection and replacement are delegated on an
 * multi-objective strategy. The creation of offspring should be implemented by
 * each specific evolutionary model (e.g. GA, EP, ES, GP).
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
 * @see PopulationAlgorithm
 * @see IMOAlgorithm
 * @see MOStrategy
 * */

public abstract class MOECAlgorithm extends PopulationAlgorithm implements IMOAlgorithm{

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -3125728085547485677L;

	/** Multi-objective strategy */
	protected MOStrategy strategy;

	/** Archive */
	protected List<IIndividual> archive;

	/** Initial execution time */
	protected long initTime;

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	// PopulationAlgorithm methods
		
	/**
	 * Set the species. If the context exists, it is also updated.
	 * @param species The species object that has to be set.
	 * */
	@Override
	public void setSpecies(ISpecies species) {
		this.species = species;
		if(getContext()!=null)
			getContext().setSpecies(this.species);
	}
	
	/**
	 * Set the evaluator. If the context exists, it is also updated.
	 * @param evaluator The evaluator object that has to be set.
	 * */
	@Override
	public void setEvaluator(IEvaluator evaluator) {
		this.evaluator = evaluator;
		if(getContext()!=null)
			getContext().setEvaluator(this.evaluator);
	}
	
	/**
	 * Set the generator of random numbers. If the context exists, it is also updated.
	 * @param randGenFactory The generator object that has to be set.
	 * */
	@Override
	public void setRandGenFactory(IRandGenFactory randGenFactory) {
		this.randGenFactory = randGenFactory;
		if(getContext()!=null)
			getContext().setRanGenFactory(this.randGenFactory.createRandGen());
	}

	/**
	 * Create and evaluate the initial population.
	 * The strategy is also initialized, so the initial
	 * archive will be created.
	 * */
	@Override
	protected void doInit(){

		// Start counting the execution time
		this.initTime = System.currentTimeMillis();

		// If strategy is MOEA/D, population size should be updated
		if(this.strategy instanceof MOEAD){
			int size = ((MOEAD)this.strategy).getNumberOfVectors();
			super.setPopulationSize(size);
			this.strategy.getContext().setPopulationSize(size);
		}

		// Initialize the population
		this.bset = provider.provide(this.populationSize);
		this.evaluator.evaluate(this.bset);

		// Initialize strategy and store the initial archive
		this.archive = this.strategy.initialize(this.bset);

		// Set the populations in context
		this.strategy.getContext().setInhabitants(this.bset);
		this.strategy.getContext().setArchive(this.archive);
		
		// Check stopping criteria
		doControl();
	}

	/**
	 * {@inheritDoc}
	 * <p>Here, parents are selected by the mating selection
	 * method defined in the multi-objective strategy.</p>
	 * */
	@Override
	protected void doSelection() {

		// Update the generation in the context
		this.strategy.getContext().setGeneration(this.generation);

		// Select parents from the current population
		this.pset = this.strategy.matingSelection(this.bset, this.archive);
	}

	/**
	 * {@inheritDoc}
	 * <p>This method should be implemented by the
	 * specific evolutionary paradigm.</p>
	 * */
	@Override
	protected abstract void doGeneration();

	/**
	 * Select the survivors using the environmental selection 
	 * method defined in the multi-objective strategy.
	 * */
	@Override
	protected void doReplacement() {
		// Select survivors
		this.rset = this.strategy.environmentalSelection(this.bset, this.cset, this.archive);
	}

	/**
	 * Update the archive of solutions and set the survivors
	 * as the new population.
	 * */
	@Override
	protected void doUpdate() {

		// Update archive
		this.archive = this.strategy.updateArchive(this.bset, this.cset, this.archive);

		// Update current population
		this.bset = this.rset;

		// Clean pset, cset, rset
		this.pset = null;
		this.cset = null;
		this.rset = null;

		// Update strategy
		this.strategy.update();

		// Update populations in context
		this.strategy.getContext().setInhabitants(this.bset);
		this.strategy.getContext().setArchive(this.archive);
	}

	/**
	 * {@inheritDoc}
	 * <p>Specific parameters for <code>MOAlgorithm</code>:
	 * <ul>
	 * 	<li>mo-strategy: <code>MOStrategy</code> (complex)
	 * <p>Multi-objective evolutionary strategy</li>
	 * </ul>
	 * */
	@Override
	public void configure(Configuration settings) {

		// Evaluator is configured in the super class
		super.configure(settings);

		// Configure the multi-objective strategy
		setStrategySettings(settings);

		// Set the context in provider
		this.provider.contextualize(getContext());

		// Set the fitness comparator and the fitness prototype in the evaluator
		if(this.evaluator instanceof IMOEvaluator){
			((IMOEvaluator)this.evaluator).setComparator(this.strategy.getSolutionComparator().getFitnessComparator());
			((IMOEvaluator)this.evaluator).setFitnessPrototype(this.strategy.getContext().getFitnessPrototype());
		}
		else
			throw new IllegalArgumentException("Evaluator should extend IMOEvaluator interface");
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
		NonDominatedSolutionsExtractor command = new NonDominatedSolutionsExtractor(this.bset, comparator);
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
		this.strategy = strategy;
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
							this.species, this.evaluator, this.strategy.getSolutionComparator(), this.populationSize);
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
		if(this.evaluator!=null && this.evaluator instanceof IMOEvaluator){
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
	 * Configure the random number generator without contextualizing it.
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
			Class<? extends MOStrategy> objClass = (Class<? extends MOStrategy>) Class.forName(classname);
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
