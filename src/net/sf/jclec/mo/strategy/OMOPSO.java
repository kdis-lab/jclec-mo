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

package net.sf.jclec.mo.strategy;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationRuntimeException;

import net.sf.jclec.IConfigure;
import net.sf.jclec.IFitness;
import net.sf.jclec.IIndividual;
import net.sf.jclec.IMutator;
import net.sf.jclec.fitness.SimpleValueFitness;
import net.sf.jclec.mo.IMOEvaluator;
import net.sf.jclec.mo.command.CrowdingDistanceCalculator;
import net.sf.jclec.mo.command.NonDominatedSolutionsExtractor;
import net.sf.jclec.mo.command.PopulationShuffler;
import net.sf.jclec.mo.command.PopulationSorter;
import net.sf.jclec.mo.comparator.CrowdingDistanceComparator;
import net.sf.jclec.mo.comparator.MOSolutionComparator;
import net.sf.jclec.mo.comparator.fcomparator.EpsilonDominanceComparator;
import net.sf.jclec.mo.comparator.fcomparator.MOFitnessComparator;
import net.sf.jclec.mo.comparator.fcomparator.ParetoComparator;
import net.sf.jclec.mo.evaluation.Objective;
import net.sf.jclec.mo.evaluation.fitness.ICrowdingDistanceMOFitness;
import net.sf.jclec.mo.evaluation.fitness.IHypercubeMOFitness;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;
import net.sf.jclec.mo.strategy.util.Hypercube;
import net.sf.jclec.pso.Particle;
import net.sf.jclec.pso.ParticleSpecies;
import net.sf.jclec.util.random.IRandGen;
import net.sf.jclec.util.range.Interval;

/**
 * OMOPSO strategy.
 * 
 * <p>The OMOPSO strategy considers a fixed-size archive of external solutions and the use of two mutation
 * operators to have a balance between explotation and exploration. The e-dominance 
 * principle and the crowding distance proposed by NSGA-II are used to discard solutions if the
 * number of non-dominated solutions exceeds the archive size.</p>
 * 
 * <p><i>Paper</i>: M. Reyes-Sierra, C.A. Coello Coello, 
 * “Improving PSO-based Multi-Objective Optimization using Crowding, Mutation and e-dominance". 
 *  Evolutionary Multi-Criterion Optimization, vol. 3410 LNCS, pp. 505-519, 2005.</p>
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
 * @see MOPSOStrategy
 * */

public class OMOPSO extends MOPSOStrategy {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 6876842792956324074L;

	/** Epsilon values: the lengths for each objective */
	protected double [] epsilon;

	/** Number of hypercubes (adaptive version) */
	private int nHypercubes;

	/** Archive maximum size */
	protected int archiveMaxSize;

	/** Nondominated command extractor */
	protected NonDominatedSolutionsExtractor extractor;

	/** Crowding distance calculator */
	protected CrowdingDistanceCalculator dCalculator;

	/** Command to sort particles */
	protected PopulationSorter sortCommand;

	/** Command to shuffle particles */
	protected PopulationShuffler suffleCommand;

	/** The comparator to be used in the archive update */
	protected MOSolutionComparator archiveComparator;

	/** A comparator based on the crowding distance to sort solutions */
	protected CrowdingDistanceComparator dComparator;

	/** Uniform mutation */
	protected IMutator uniformMutation;

	/** Non uniform mutation */
	protected IMutator nonUniformMutation;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public OMOPSO(){
		super();
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Get the maximum archive size.
	 * @return Maximum archive size.
	 * */
	public int getArchiveMaxSize(){
		return this.archiveMaxSize;
	}

	/**
	 * Set the maximum archive size.
	 * @param size The size that has to be used.
	 * */
	protected void setArchiveMaxSize(int size){
		this.archiveMaxSize = size;
	}

	/** 
	 * Get the array of epsilon values.
	 * @return epsilon values.
	 */
	public double[] getEpsilonValues(){
		return this.epsilon;
	}

	/**
	 * Set the array of epsilon values.
	 * @param epsilon The values that have to be set.
	 */
	protected void setEpsilonValues(double [] epsilon){
		this.epsilon = epsilon;
	}

	/**
	 * Get the uniform mutator.
	 * @return The configured uniform mutator.
	 * */
	public IMutator getUniformMutator(){
		return this.uniformMutation;
	}

	/**
	 * Set the uniform mutator.
	 * @param mutator The mutator that has to be set.
	 * */
	protected void setUniformMutator(IMutator mutator){
		this.uniformMutation = mutator;
		this.uniformMutation.contextualize(getContext());
	}

	/**
	 * Get the non-uniform mutator.
	 * @return The configured non-uniform mutator.
	 * */
	public IMutator getNonUniformMutator(){
		return this.nonUniformMutation;
	}

	/**
	 * Set the non-uniform mutator.
	 * @param mutator The mutator that has to be set.
	 * */
	protected void setNonUniformMutator(IMutator mutator){
		this.nonUniformMutation = mutator;
		this.nonUniformMutation.contextualize(getContext());
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>In OMOPSO, the archive is created considering the set of non-dominated
	 * solutions within the initial population. To decide whether a solution should
	 * be added or not, the e-dominance and the crowding distance are computed by invoking
	 * the fitness assignment method.</p>
	 * */
	@Override
	public List<IIndividual> initialize(List<IIndividual> swarm) {

		// Create commands
		createCommands();

		// Create the distance comparator
		this.dComparator = new CrowdingDistanceComparator();

		// Set the epsilon values if required
		if(this.epsilon==null & this.nHypercubes>0)
			setEpsilonValues();

		// Evaluate properties in the archive
		fitnessAssignment(swarm, null);	

		// Create the first archive (leaders)
		this.extractor.setPopulation(swarm);
		this.extractor.execute();
		List<IIndividual> archive = this.extractor.getNonDominatedSolutions();

		// If the archive exceed the archive size, reduce it
		if(archive.size() > archiveMaxSize){
			reduceArchive(archive);
		}

		return archive;
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void update() {
		// do nothing
	}

	/**
	 * {@inheritDoc}
	 * <p>OMOPSO uses e-dominance.</p>
	 * */
	@Override
	public void createSolutionComparator(Comparator<IFitness>[] components) {

		// Archive comparator
		MOFitnessComparator fcomparator = new EpsilonDominanceComparator(components);
		this.archiveComparator = new MOSolutionComparator(fcomparator);

		// Individuals comparator
		fcomparator = new ParetoComparator(components);
		MOSolutionComparator comparator = new MOSolutionComparator(fcomparator);
		setSolutionComparator(comparator);
	}

	/**
	 * {@inheritDoc}
	 * <p>Specific parameters for OMOPSO are:
	 * <ul>
	 * 	<li>uniform-mutator: <code>IMutator</code> (complex)
	 * 	<p>The uniform mutator to be applied.</p>
	 * 	</li>
	 * 	<li>non-uniform-mutator: <code>IMutator</code> (complex)
	 * 	<p>The non-uniform mutator to be applied.</p>
	 * 	</li>	
	 *  <li>archive-size: (<code>integer</code>)
	 * 	<p>The maximum archive size.</p>
	 * 	</li>
	 * 	<li>epsilon-values (<code>List</code>): 
	 * 	<p>List of epsilon values to define the landscape partition
	 * 	(one for each objective function configure). If only
	 * 	one value is set, then it will be applied to all the objectives.</p></li>
	 * 
	 * 	<li>number-of-hypercubes (<code>integer</code>): 
	 * 	<p>Number of hypercubes.
	 * 	Default value is 10. This element is only used if the list of
	 * 	epsilon values is not provided.</p></li>
	 * 	</ul>
	 * </p>
	 * */
	@Override
	public void configure(Configuration settings){

		// Call super implementation
		super.configure(settings);

		// Configure mutation
		setMutationSettings(settings);
		
		// The archive size, the population size by default
		int archiveSize = settings.getInt("archive-size", getContext().getPopulationSize());
		setArchiveMaxSize(archiveSize);

		// Set epsilon values
		List<Object> values = settings.subset("epsilon-values").getList("epsilon-value");
		int size = values.size();
		double [] epsilon = null;
		double value;

		// Check if the number of values is the same than the number of objectives
		int nObj = ((IMOEvaluator)getContext().getEvaluator()).numberOfObjectives();
		if(nObj==size){
			epsilon = new double[size];
			for(int i=0; i<size; i++){
				epsilon[i]= Double.valueOf(values.get(i).toString());
			}
			setEpsilonValues(epsilon);
		}

		// Only one value, set it for all the objectives
		else if(size==1){
			epsilon = new double[nObj];
			value = Double.valueOf(values.get(0).toString());
			for(int i=0; i<nObj; i++){
				epsilon[i]=value;
			}
			setEpsilonValues(epsilon);
		}

		// Try to get the number of hypercubes
		else{
			this.nHypercubes = settings.getInt("number-of-hypercubes",10);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>In OMOPSO, leaders are selected from the archive performing a
	 * binary tournament based on the crowding value.</p>
	 * */
	@Override
	public List<IIndividual> matingSelection(List<IIndividual> swarm, List<IIndividual> archive) {

		List<IIndividual> leaders = new ArrayList<IIndividual>();
		int size = swarm.size();
		int archiveSize = archive.size();
		IIndividual leader1, leader2;
		int rnd;
		IRandGen randgen = getContext().createRandGen();
		double crowding1, crowding2;

		// For each particle in the swarm, choose a leader from the archive
		for(int i=0; i<size; i++){

			// Choose one particle at random
			rnd = randgen.choose(0, archiveSize);
			leader1 = archive.get(rnd);

			// Choose another particle at random
			rnd = randgen.choose(0, archiveSize);
			leader2 = archive.get(rnd);

			// Compare the crowding value
			crowding1 = ((ICrowdingDistanceMOFitness)leader1.getFitness()).getCrowdingDistance();
			crowding2 = ((ICrowdingDistanceMOFitness)leader2.getFitness()).getCrowdingDistance();

			// First candidate is better
			if(crowding1 > crowding2){
				leaders.add(leader1);
			}
			// Second candidate is better
			else if (crowding1 < crowding2){
				leaders.add(leader2);
			}
			// Equal crowding values, then choose at random
			else if(randgen.coin()){
				leaders.add(leader1);
			}
			else{
				leaders.add(leader2);
			}
		}

		return leaders;
	}

	/**
	 * {@inheritDoc}
	 * <p>In OMOPSO, the swarm is completely replaced by the disturbed swarm.</p>
	 * */
	@Override
	public List<IIndividual> environmentalSelection(List<IIndividual> swarm, 
			List<IIndividual> disturbedSwarm, List<IIndividual> archive) {
		return disturbedSwarm;
	}

	/**
	 * {@inheritDoc}
	 * <p>In OMOPSO, non-dominated solutions are included in the archive (using
	 * the e-dominance). If the archive exceeds the maximum size, it is reduced
	 * considering the crowding value.</p>
	 * */
	@Override
	public List<IIndividual> updateArchive(List<IIndividual> swarm,
			List<IIndividual> disturbedSwarm, List<IIndividual> archive) {

		// Add all the solutions
		List<IIndividual> allSolutions = new ArrayList<IIndividual>();
		allSolutions.addAll(swarm);
		allSolutions.addAll(disturbedSwarm);
		allSolutions.addAll(archive);

		// Evaluate properties for all the solutions
		fitnessAssignment(allSolutions, null);

		// Extract non dominated solutions
		this.extractor.setPopulation(allSolutions);
		this.extractor.execute();
		List<IIndividual> newArchive = this.extractor.getNonDominatedSolutions();

		// If the archive exceed the archive size, reduce it
		if(newArchive.size() > this.archiveMaxSize){
			reduceArchive(newArchive);
		}
		return newArchive;
	}

	/**
	 * {@inheritDoc}
	 * <p>In OMOPSO, a crowding value is assigned to the archive members.</p>
	 * */
	@Override
	protected void fitnessAssignment(List<IIndividual> swarm, List<IIndividual> archive) {

		// Add all the particles
		List<IIndividual> allSolutions = new ArrayList<IIndividual>();
		if(swarm!=null)
			allSolutions.addAll(swarm);
		if(archive!=null)
			allSolutions.addAll(archive);

		// Compute the hypercube for each particle
		int size = allSolutions.size();
		Hypercube hypercube;
		for(int i=0; i<size; i++){
			hypercube = computeHypercube(allSolutions.get(i));
			((IHypercubeMOFitness)allSolutions.get(i).getFitness()).setHypercube(hypercube);
		}	

		// Compute the crowding distance using the command
		this.dCalculator.setPopulation(allSolutions);
		this.dCalculator.execute();
	}

	/**
	 * {@inheritDoc}
	 * <p>OMOPSO proposes its own mechanism to update the velocities, fixing
	 * the bounds for the random weights.</p>
	 * */
	@Override
	public void updateVelocities(List<IIndividual> swarm, List<IIndividual> leaders) {
		int swarmSize = swarm.size();
		IRandGen randgen = getContext().createRandGen();
		double w = randgen.uniform(0.1, 0.5);
		double c1 = randgen.uniform(1.5, 2.0);
		double c2 = randgen.uniform(1.5, 2.0);
		double r1 = randgen.uniform(0.0, 1.0);
		double r2 = randgen.uniform(0.0, 1.0);
		Particle particle;
		double [] velocity;
		double [] leaderBestPosition;
		double [] currentPosition;
		double [] bestPosition;

		for(int i=0; i<swarmSize; i++){
			particle = (Particle)swarm.get(i);
			velocity = particle.getVelocity();
			currentPosition = particle.getPosition();
			bestPosition = particle.getBestPosition();
			leaderBestPosition = ((Particle)leaders.get(i)).getBestPosition();

			for(int j=0; j<velocity.length; j++){
				velocity[j] = w*velocity[j] + c1*r1*(bestPosition[j]-currentPosition[j]) 
						+ c2*r2*(leaderBestPosition[j]-currentPosition[j]);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>After updating the positions using the new velocities, 
	 * OMOPSO checks if the new positions exceed the bounds.</p>
	 * */
	@Override
	public void updatePositions(List<IIndividual> swarm){
		// Update the positions
		int swarmSize = swarm.size();
		Particle particle;
		double [] position;
		double [] velocity;
		for(int i=0; i<swarmSize; i++){
			particle = (Particle)swarm.get(i);
			position = particle.getPosition();
			velocity = particle.getVelocity();
			for(int j=0; j<position.length; j++){
				position[j] = position[j]+velocity[j];
			}
			particle.setPosition(position);
		}

		// Fix the positions
		fixPositions(swarm);
	}

	/**
	 * {@inheritDoc}
	 * <p>In OMOPSO, the swarm is divided into three parts. The uniform mutation
	 * is applied to the first one, whilst the non-uniform mutation is applied
	 * to the second one. The third partition is not mutated.</p>
	 * */
	@Override
	public List<IIndividual> turbulence(List<IIndividual> swarm) {

		List<IIndividual> disturbedSwarm = new ArrayList<IIndividual>();
		int swarmSize = swarm.size();

		// Divide the swarm in three parts
		int step = (int)swarmSize/3;
		List<IIndividual> swarm1 = swarm.subList(0, step);
		List<IIndividual> swarm2 = swarm.subList(step, step*2);

		// Apply mutation to each swarm
		List<IIndividual> result1 = this.uniformMutation.mutate(swarm1);
		List<IIndividual> result2 = this.nonUniformMutation.mutate(swarm2);

		// Add to the disturbed swarm all the new particles
		disturbedSwarm = new ArrayList<IIndividual>();
		int size = result1.size();
		for(int i=0; i<size; i++){
			disturbedSwarm.add(result1.get(i).copy());
		}
		size = result2.size();
		for(int i=0; i<size; i++){
			disturbedSwarm.add(result2.get(i).copy());
		}

		// Add the third part of the original swarm, that are not mutated
		for(int i=step*2; i<swarmSize; i++){
			disturbedSwarm.add(swarm.get(i).copy());
		}

		// Fix the positions
		fixPositions(disturbedSwarm);

		return disturbedSwarm;
	}

	/////////////////////////////////////////////////////////////////
	// -------------------------------------------- Protected methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Configure the mutation operators.
	 * @param settings Configuration object.
	 * */
	@SuppressWarnings("unchecked")
	protected void setMutationSettings(Configuration settings){
		// Configure uniform mutator 
		String mutatorClassname;
		Class<? extends IMutator> mutatorClass;
		IMutator mutator;

		try {
			mutatorClassname = settings.getString("uniform-mutator[@type]");
			mutatorClass = (Class<? extends IMutator>) Class.forName(mutatorClassname);
			mutator = mutatorClass.getDeclaredConstructor().newInstance();
			if (mutator instanceof IConfigure) {
				Configuration mutatorConfiguration = settings.subset("uniform-mutator");
				((IConfigure) mutator).configure(mutatorConfiguration);
			}
			setUniformMutator(mutator);
		} 
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new ConfigurationRuntimeException("Problems creating an instance of mutator", e);
		}

		// Configure non-uniform mutator 
		try {
			mutatorClassname = settings.getString("non-uniform-mutator[@type]");
			mutatorClass = (Class<? extends IMutator>) Class.forName(mutatorClassname);
			mutator = mutatorClass.getDeclaredConstructor().newInstance();
			if (mutator instanceof IConfigure) {
				Configuration mutatorConfiguration = settings.subset("non-uniform-mutator");
				((IConfigure) mutator).configure(mutatorConfiguration);
			}
			setNonUniformMutator(mutator);
		} 
		catch (ClassNotFoundException|InstantiationException|IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new ConfigurationRuntimeException("Problems creating an instance of mutator", e);
		}
	}

	/**
	 * Auxiliary method to create and configure the required commands.
	 * */
	protected void createCommands(){
		// Create the extractor command
		this.extractor = new NonDominatedSolutionsExtractor();
		this.extractor.setComparator((ParetoComparator)this.archiveComparator.getFitnessComparator());

		// Create the crowding distance calculator command
		this.dCalculator = new CrowdingDistanceCalculator();
		this.dCalculator.setComparator(getSolutionComparator().getFitnessComparator());
		List<Objective> objectives = ((IMOEvaluator)getContext().getEvaluator()).getObjectives();
		int size = objectives.size();
		double [] minValues = new double[size];
		double [] maxValues = new double[size];
		for(int i=0; i<size; i++){
			minValues[i] = ((IMOEvaluator)getContext().getEvaluator()).getObjectives().get(i).getMinimum();
			maxValues[i] = ((IMOEvaluator)getContext().getEvaluator()).getObjectives().get(i).getMaximum();
		}
		this.dCalculator.setMinValues(minValues);
		this.dCalculator.setMaxValues(maxValues);

		// Create the auxiliary commands
		this.sortCommand = new PopulationSorter();
		this.sortCommand.setComparator(new CrowdingDistanceComparator());
		this.sortCommand.setInverse(true); // order from max distance to min distance
		this.suffleCommand = new PopulationShuffler();
		this.suffleCommand.setRandGen(getContext().createRandGen());
	}

	/**
	 * Remove the solutions with less 
	 * crowding distance from the given archive.
	 * @param archive The archive to be reduced.
	 * */
	protected void reduceArchive(List<IIndividual> archive){

		// Sort the archive according to the crowding value
		this.sortCommand.setPopulation(archive);
		this.sortCommand.execute();

		// Remove the solutions with small crowding distance
		int size = archive.size();
		for(int i=size-1; i>=archiveMaxSize; i--){
			archive.remove(i);
		}

		// Shuffle the archive
		this.suffleCommand.setPopulation(archive);
		this.suffleCommand.execute();
	}

	/**
	 * Compute the hypercubes for the given individual
	 * Each component, j, of the hypercube (identification array) 
	 * is obtained from the correspondent objective value (f_j) as:
	 * <p>B_j = floor((f_j-f_j_min)/epsilon_j) if f_j should be minimized</p>
	 * <p>B_j = ceil((f_j-f_j_min)/epsilon_j) if f_j should be maximized</p>
	 * @param particle The particle.
	 * @return Corresponding hypercubes.
	 * */
	protected Hypercube computeHypercube(IIndividual particle){

		List<Objective> objectives = ((IMOEvaluator)getContext().getEvaluator()).getObjectives();
		int nObj = objectives.size();
		MOFitness fitness = (MOFitness)particle.getFitness();
		IFitness values [] = new IFitness[nObj];
		Objective obj;
		double value;
		double epsilon [] = getEpsilonValues();
		// Compute the hypercube for each objetive
		for(int i=0; i<nObj; i++){
			obj = objectives.get(i);
			try {
				value = (fitness.getObjectiveDoubleValue(i)-obj.getMinimum())/epsilon[i];
				if(obj.isMaximized()){
					values[i] = new SimpleValueFitness(Math.ceil(value));
				}
				else{
					values[i] = new SimpleValueFitness(Math.floor(value));
				}
			} catch (IllegalAccessException | IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		Hypercube h = new Hypercube(values);
		return h;
	}

	/**
	 * Fix the positions and velocities of the given swarm.
	 * @param swarm The swarm whose positions have to be fixed.
	 * */
	protected void fixPositions(List<IIndividual> swarm){
		int size = swarm.size();
		Particle particle;
		ParticleSpecies species = (ParticleSpecies)getContext().getSpecies();
		double lenght = species.getGenotypeLength();
		double min, max;

		for(int i=0; i<size; i++){
			particle = (Particle)swarm.get(i);
			for(int j=0; j<lenght; j++){
				min = ((Interval)species.getGenotypeSchema()[j]).getLeft();
				max = ((Interval)species.getGenotypeSchema()[j]).getRight();
				if(particle.getPosition()[j] < min){
					particle.getPosition()[j] = min;
					particle.getVelocity()[j] = -1.0*particle.getVelocity()[j];
				}
				if(particle.getPosition()[j] > max){
					particle.getPosition()[j] = max;
					particle.getVelocity()[j] = -1.0*particle.getVelocity()[j];
				}
			}
		}
	}

	/**
	 * Set the epsilon values (width of the hypercubes) using the bounds in the
	 * objective and the desired number of hypercubes.
	 * */
	private void setEpsilonValues() {
		List<Objective> objectives = ((IMOEvaluator)getContext().getEvaluator()).getObjectives();
		int size = objectives.size();
		this.epsilon = new double[size];
		Objective obj;
		for(int i=0; i<size; i++){
			obj=objectives.get(i);
			this.epsilon[i] = (obj.getMaximum()-obj.getMinimum())/this.nHypercubes;
		}
	}
}
