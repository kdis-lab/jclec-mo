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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.configuration.Configuration;

import net.sf.jclec.IFitness;
import net.sf.jclec.IIndividual;
import net.sf.jclec.mo.IMOEvaluator;
import net.sf.jclec.mo.command.NonDominatedSolutionsExtractor;
import net.sf.jclec.mo.command.UniformVectorGenerator;
import net.sf.jclec.mo.comparator.MOSolutionComparator;
import net.sf.jclec.mo.comparator.fcomparator.ParetoComparator;
import net.sf.jclec.mo.evaluation.Objective;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;
import net.sf.jclec.mo.strategy.util.MOEADNeighbor;
import net.sf.jclec.mo.strategy.util.MOEADNeighborsComparator;
import net.sf.jclec.util.random.IRandGen;

/**
 * MOEA/D strategy
 * 
 * <p>The <b>M</b>ulti <b>O</b>bjective<b> E</b>volutionary <b>A</b>lgorithm
 * based on <b>D</b>ecomposition simultaneously performs an optimization of
 * diverse scalar subproblems. MOEA/D assigns a weight vector to every individual
 * in the population, each one being focused on the resolution of the subproblem
 * represented by a weight vector. Different approaches can be used to assign
 * the fitness value, so this class is defined as an abstract strategy. Subclasses
 * should define the fitness function to be used. Neighborhood information between 
 * subproblems is required for mating and environmental selection. Pareto dominance 
 * is used to update the archive.</p>
 * 
 * <p>Some preconditions exist in the type of objective functions: they can be minimized 
 * or maximized, but combining both types is not allowed.</p>
 * 
 * <p>Note: the population size is adapted to the number of generated weight vectors.</p>
 * 
 * <p><i>Paper</i>: Q. Zhang and H. Li, “MOEA/D: A Multiobjective Evolutionary 
 * Algorithm Based on Decomposition”, IEEE Transactions on Evolutionary Computation,
 * vol. 11, no. 6, pp. 712–731, 2007.</p>
 * 
 * <p>HISTORY:
 * <ul>
 *  <li>(AR|JRR|SV, 1.0, March 2018)		First release.</li>
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
 * @see MOStrategy
 * */
public abstract class MOEAD extends MOStrategy {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -3302181140632993868L;

	/** H parameter for the number of lambda vector */
	protected int H;

	/** Uniform spread of weight vector (one for each subproblem) */
	protected List<double []> weights;

	/** The neighborhood size for weight vectors */
	protected int neighborhoodSize;

	/** Use the archive? */
	protected boolean useArchive;

	/** 
	 * The neighborhood size for maximum individual replacement.
	 * It is considered in the improvement version of the algorithm
	 * to prevent excessive convergence when the new descendant 
	 * replace all worst individuals in the next population.
	 *  */
	private int maxNeighborsRepl;

	/** Reference point */
	private double [] refPoint;

	/** Problem neighbors */
	private List<ArrayList<MOEADNeighbor>> problemNeighbors;

	/** Fitness comparator */
	protected MOSolutionComparator fitnessFunctionComparator;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public MOEAD(){
		super();
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Get the value of parameter H.
	 * @return H value
	 * */
	public int getH() {
		return H;
	}

	/**
	 * Set the value of parameter H.
	 * @return H New H value
	 * */
	protected void setH(int H) {
		this.H = H;
	}

	/**
	 * Get the weights vectors.
	 * @return The list of weights vectors.
	 * */
	public List<double[]> getWeights() {
		return weights;
	}

	/**
	 * Set the weights vectors.
	 * @param weights The list of weights vectors.
	 * */
	protected void setWeights(List<double []> weights) {
		this.weights = weights;
	}

	/**
	 * Get the value of parameter tau.
	 * @return tau value.
	 * */
	public int getNeighborhoodSize() {
		return neighborhoodSize;
	}

	/**
	 * Set the value of parameter tau.
	 * @param neighborhoodSize new neighborhood size value.
	 * */
	protected void setNeighborhoodSize(int neighborhoodSize) {
		this.neighborhoodSize = neighborhoodSize;
	}

	/**
	 * Get whether the strategy uses
	 * the external population or not.
	 * @return True if the strategy uses the external population,
	 * false otherwise.
	 * */
	public boolean useArchive() {
		return this.useArchive;
	}

	/**
	 * Set whether the strategy will use the external
	 * population or not.
	 * @param useArchive New value.
	 * */
	protected void setUseArchive(boolean useArchive) {
		this.useArchive = useArchive;
	}

	/**
	 * Get the value of maximum number of replacements.
	 * @return maxNeighborsRepl value.
	 * */
	public int getMaxNeighborsReplacement() {
		return maxNeighborsRepl;
	}

	/**
	 * Set the value the value of maximum number of replacements.
	 * @param maxNeighborsRepl New maxNeighborsRepl value.
	 * */
	protected void setMaxNeighborsReplacement(int maxNeighborsRepl) {
		this.maxNeighborsRepl = maxNeighborsRepl;
	}

	/**
	 * Get the reference point.
	 * @return The current reference point.
	 * */
	public double [] getReferencePoint() {
		return refPoint;
	}

	/**
	 * Set the reference point.
	 * @param refPoint The new reference point.
	 * */
	protected void setReferencePoint(double [] refPoint) {
		this.refPoint = refPoint;
	}

	/**
	 * Set an specific value in the
	 * reference point.
	 * @param value The new value.
	 * @param index The position index.
	 * */
	protected void setRefPointValue(double value, int index) {
		this.refPoint[index] = value;
	}

	/**
	 * Get the number of weight vectors (new population size).
	 * @return Number of weight vectors.
	 * */
	public int getNumberOfVectors(){
		return this.weights.size();
	}

	/**
	 * Get the problem neighbors.
	 * @return List containing the problem neighbors.
	 * */
	public List<ArrayList<MOEADNeighbor>> getProblemNeighbors() {
		return this.problemNeighbors;
	}

	/**
	 * Get comparator for the fitness function.
	 * @return A comparator that uses the fitness function criterion.
	 * */
	protected MOSolutionComparator getFitnessFunctionComparator(){
		return this.fitnessFunctionComparator;
	}

	/**
	 * Set the comparator for the fitness function.
	 * @param comparator New comparator.
	 * */
	protected void setFitnessFunctionComparator(MOSolutionComparator comparator){
		this.fitnessFunctionComparator = comparator;
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>Parameters for MOEA/D are:
	 * <ul>
	 * 	<li>t (<code>int</code>): <p>Neighborhood size. Default value is 10.</p></li>
	 * 	<li>nr (<code>int</code>): <p>Maximum number of replacements. Default value is 2.</p></li>
	 * 	<li>h (<code>int</code>): <p>It controls the generation of uniform weights
	 * for a specific number of subproblems</p></li>
	 * <li>external-pop (<code>boolean</code>): <p>Set if the strategy will
	 * use the external population. Default value is true.</p></li>
	 * </ul>
	 * </p>
	 * */
	@Override
	public void configure(Configuration settings){

		// Call super configuration
		super.configure(settings);
		
		// Specific parameters
		int tau = settings.getInt("t",10);
		setNeighborhoodSize(tau);

		if(tau>getContext().getPopulationSize())
			throw new IllegalArgumentException("The neighborhoord size (t) must be less than the population size");

		int nr = settings.getInt("nr",2);
		setMaxNeighborsReplacement(nr);

		if(nr>tau)
			throw new IllegalArgumentException("The number of maximum replacement (nr) must be less than the " +
					"neighborhoord size (t)");

		// Set lambda vector
		int h = settings.getInt("h");

		// Check relation between the number of objectives and the 
		// objective space partition. Based on combinatorial numbers:
		// Number of combinations = C_{k}_{n} = n!/(n-k)!*k!, then n>=k
		// In this case: k=#objectives-1, n=H+#objectives-1 
		int nObj = ((IMOEvaluator)getContext().getEvaluator()).numberOfObjectives();
		if(h<=0 || (nObj-1)>(h+nObj-1)){
			throw new IllegalArgumentException("The space partition is not valid for the number of objectives");
		}
		setH(h);
		
		// Generate uniform vectors
		UniformVectorGenerator command = new UniformVectorGenerator(nObj,h);
		command.execute();
		this.weights = command.getUniformVectors();

		// Check types of objectives
		if(isMaximized()==null){
			System.err.println("All objectives should be maximized or minimized");
			System.exit(-1);
		}

		// Set the configuration regarding the external population
		boolean hasExternalPop = settings.getBoolean("external-pop", true);
		this.setUseArchive(hasExternalPop);

		// Create the comparator (specific for the MOEA/D variant used)
		setFitnessFunctionComparator(createFitnessFunctionComparator());
	}

	/**
	 * {@inheritDoc}
	 * <p>In MOEA/D, required initialization steps are:
	 * <ol>
	 * <li>To initialize the reference point</li>
	 * <li>To update the reference point using the initial population</li>
	 * <li>To compute the fitness for each individual</li>
	 * <li>To assign the problem neighbors</li> 
	 * </ol>
	 * </p>
	 * */
	@Override
	public List<IIndividual> initialize(List<IIndividual> population) {

		// Set reference point
		setReferencePoint(createReferencePoint());

		// Update reference point
		for(IIndividual ind: population)
			updateReferencePoint(ind);

		// Compute fitness on each individual using the lambda vectors
		fitnessAssignment(population, null);

		// Set the neighbors of each problem weight
		setProblemNeighbors();

		// The strategy has been configured to use the external population,
		// then return the set of non-dominated solution in the initial population
		if(useArchive()){
			NonDominatedSolutionsExtractor command = createExtractor();
			command.setPopulation(population);
			command.execute();
			return command.getNonDominatedSolutions();
		}

		// The archive is not used
		else{
			return null;
		}
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void update() {
		// Do nothing
	}

	/**
	 * {@inheritDoc}
	 * <p>MOEA/D requires a Pareto comparator.</p>
	 * */
	@Override
	public void createSolutionComparator(
			Comparator<IFitness>[] components) {
		// Fitness comparator
		ParetoComparator fcomparator = new ParetoComparator(components);
		// Individuals comparator
		MOSolutionComparator comparator = new MOSolutionComparator(fcomparator);
		setSolutionComparator(comparator);
	}

	/**
	 * {@inheritDoc}
	 * <p>MOEA/D selection procedure choose two individuals
	 * in the neighborhood of each problem to acts as parents.</p>
	 * */
	@Override
	public List<IIndividual> matingSelection(List<IIndividual> population, List<IIndividual> archive) {

		int index0,index1;
		int problemID;
		List<IIndividual> parents = new ArrayList<IIndividual>();
		int size = population.size();
		IRandGen randgen = getContext().createRandGen();

		for(int i=0; i<size; i++){
			// Select two random neighbor for each subproblem (individual in the population)
			index0 = randgen.choose(0, getNeighborhoodSize());
			problemID = this.problemNeighbors.get(i).get(index0).getId();
			parents.add(population.get(problemID));

			do{
				index1=randgen.choose(0, getNeighborhoodSize());
			}while(index0==index1);
			problemID = this.problemNeighbors.get(i).get(index1).getId();
			parents.add(population.get(problemID));
		}
		return parents;
	}

	/**
	 * {@inheritDoc}
	 * <p>For each new individual, MOEA/D apply the next replacement steps:
	 * <ol>
	 * <li>To update the reference point</li>
	 * <li>For each neighbor of the problem which individual was generated,
	 * evaluate if the new individual is better for the neighbor problem. If
	 * it is true, replace the neighbor with the new individual. A maximum
	 * number of replacement (<code>nr</code>) are allowed.</li>
	 * </ol>
	 * </p>
	 * */
	@Override
	public List<IIndividual> environmentalSelection(List<IIndividual> population, 
			List<IIndividual> offspring, List<IIndividual> archive) {
		List<MOEADNeighbor> neighbors;
		IIndividual offspringInd, individual;
		int nRepl;
		int maxRepl = getMaxNeighborsReplacement();
		double fitness;
		int problemIndex;
		int size = population.size();

		List<IIndividual> survivors = new ArrayList<IIndividual>();
		survivors.addAll(population);

		for(int i=0; i<size; i++){
			// New descendant
			offspringInd = offspring.get(i);

			// Update reference point
			updateReferencePoint(offspringInd);

			// Replacement using neighborhood information
			// Get neighbors of problem i (its parent's problem)
			neighbors = this.problemNeighbors.get(i);

			// Neighbors are permuted, so replacements are random
			Collections.shuffle(neighbors);
			nRepl = 0;

			// For each neighbor, if the new individual improve it
			// in the correspondent problem, replace the population member
			for(int j=0; nRepl<maxRepl && j<neighbors.size(); j++){

				// Get the individual position in the population
				problemIndex = neighbors.get(j).getId(); 

				// Evaluate the new individual in the neighbor problem
				fitness = fitnessFunction(offspringInd, getWeights().get(problemIndex));
				((MOFitness)offspringInd.getFitness()).setValue(fitness);

				// Get the actual solution of the neighbor problem
				individual = survivors.get(problemIndex);

				// The offspring is better than the current individual, replace it
				// and increment the number of replacements
				if(getFitnessFunctionComparator().compare(offspringInd, individual) == 1){
					//if(fitness <= ((MOFitness)individual.getFitness()).getValue()){
					survivors.set(problemIndex, offspringInd.copy());
					nRepl++;
				}
			}
		}
		return survivors;
	}

	/**
	 * {@inheritDoc}
	 * <p>In MOEA/D, each new offspring can be added to 
	 * the external population in two cases:
	 * <ol>
	 * <li>The offspring dominates some individuals in 
	 * the external population, the offspring is added and
	 * the dominated individuals are removed.</li>
	 * <li>The offspring is non dominated by any 
	 * individual in the external population.</li>
	 * </p>
	 * */
	@Override
	public List<IIndividual> updateArchive(List<IIndividual> population, 
			List<IIndividual> offspring, List<IIndividual> archive) {

		if(!useArchive())
			return null;

		else{
			// Copy current external population (archive)
			List<IIndividual> newSet = new ArrayList<IIndividual>();
			newSet.addAll(archive);

			int numOffspring = offspring.size();
			IIndividual offspringInd, individual;
			int j;
			int nonDominated;
			boolean dominates;

			for(int i=0; i<numOffspring; i++){
				offspringInd = offspring.get(i);
				j=0;
				dominates = false;
				nonDominated = 0;

				// Compare to each individual in external population
				while(j<newSet.size()){
					individual = newSet.get(j);

					if(!offspringInd.equals(individual)){

						// Pareto comparison
						switch(getSolutionComparator().compare(offspringInd, individual))
						{
						// Offspring dominates the individual in external population, offspring replaces him
						case 1:
							newSet.remove(j);
							dominates=true;
							break;
							// The individual dominates the offspring
						case -1:
							j++;
							break;

						case 0:
							j++;
							nonDominated++;
							break;
						}
					}
					else{
						j++;
					}
				}
				// If the offspring dominates someone or it is a non dominated solution
				// by any individual in the external population, add to the new external population
				// Note: if the external population is empty, the offspring
				// will be added since nonDominated=size=0
				if(dominates || nonDominated == newSet.size()){
					newSet.add(offspringInd.copy());
				}
			}

			return newSet;
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>This method invokes the fitness function that
	 * should be implemented by specific subclasses.</p>
	 * */
	@Override
	protected void fitnessAssignment(List<IIndividual> population, List<IIndividual> archive) {
		double fitness;
		IIndividual ind;
		int size = population.size();

		// Compute the distance between the individual and reference point on each objective
		for(int i=0; i<size; i++){
			ind = population.get(i);
			// Evaluate each individual with its correspondent problem
			fitness = fitnessFunction(ind,getWeights().get(i));
			// Set the fitness
			((MOFitness)ind.getFitness()).setValue(fitness);
		}
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Abstract methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Evaluate the individual for a given
	 * problem (weights vector).
	 * @param individual The individual to be evaluated.
	 * @param lambda The weight vector.
	 * @return The fitness value for the individual.
	 * */
	protected abstract double fitnessFunction(IIndividual individual, double [] lambda);

	/**
	 * Create an individual comparator to compare individuals in terms
	 * of the fitness value.
	 * @return An individual comparator properly configured for the selected
	 * fitness function.
	 * */
	protected abstract MOSolutionComparator createFitnessFunctionComparator();

	/////////////////////////////////////////////////////////////////
	// -------------------------------------------- Protected methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Create the reference point using the fitness metrics
	 * characteristics. As it is the initial point, it is
	 * set to worst values on each objective.
	 * @return Initial reference point.
	 * */
	protected double [] createReferencePoint(){
		double [] z = null;
		List<Objective> objectives = ((IMOEvaluator)getContext().getEvaluator()).getObjectives();
		int size = objectives.size();
		z = new double[size];
		for(int i=0; i<size; i++){
			if(objectives.get(i).isMaximized())
				z[i]=objectives.get(i).getMinimum()-1;
			else
				z[i]=objectives.get(i).getMaximum()+1;
		}
		return z;
	}

	/**
	 * Update reference point. Each coordinate (objective)
	 * is compared with the objective value in
	 * the individual, and the best value is saved.
	 * @param individual The individual to compare with-
	 * */
	protected void updateReferencePoint(IIndividual individual){

		List<Objective> objectives = ((IMOEvaluator)getContext().getEvaluator()).getObjectives();
		int numObj = objectives.size();
		MOFitness fitness = (MOFitness)individual.getFitness();
		double fitnessValue;
		for(int j=0; j<numObj; j++){
			try {
				fitnessValue = fitness.getObjectiveDoubleValue(j);
				if(objectives.get(j).isMaximized()){
					if(fitnessValue > getReferencePoint()[j]){
						setRefPointValue(fitnessValue,j);
					}
				}
				else{
					if(fitnessValue < getReferencePoint()[j]){
						setRefPointValue(fitnessValue,j);
					}
				}
			} catch (IllegalAccessException | IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Set neighbor problem for each lambda vector
	 * (individual problem weights).
	 * */
	protected void setProblemNeighbors(){
		int size = this.weights.size();
		double [][] distance = new double[size][size];
		this.problemNeighbors = new ArrayList<ArrayList<MOEADNeighbor>>(size);

		// Compute distance between problems (weight vectors)
		for(int i=0; i<size; i++){
			distance[i][i]=0.0;
			for(int j=i+1; j<size; j++){
				distance[i][j] = euclideanDistance(getWeights().get(i), getWeights().get(j));
				distance[j][i] = distance[i][j];
			}   
		}

		// Create the list of neighbors
		for(int i=0; i<size; i++){
			ArrayList<MOEADNeighbor> neighbors = new ArrayList<MOEADNeighbor>(size);
			for(int j=0; j<size; j++){
				neighbors.add(new MOEADNeighbor(j,distance[i][j]));
			}
			this.problemNeighbors.add(neighbors);
		}

		// Order neighbors by its distance
		MOEADNeighborsComparator comparator = new MOEADNeighborsComparator();
		for(int i=0; i<size; i++){
			Collections.sort(this.problemNeighbors.get(i), comparator);
		}

		// Save only the best "tau" neighbors
		for(int i=0; i<size; i++){
			while(this.problemNeighbors.get(i).size()>getNeighborhoodSize()){
				this.problemNeighbors.get(i).remove(this.problemNeighbors.get(i).size()-1);
			}
		}
	}

	/**
	 * Calculate the euclidean distance 
	 * between two arrays.
	 * @param v0 First weight vector.
	 * @param v1 Second weight vector.
	 * @return Euclidean distance between <code>v0</code> and <code>v1</code>.
	 * */
	protected double euclideanDistance(double [] v0, double [] v1){
		double distance = 0.0;
		double aux;
		for(int i=0; i<v0.length; i++){
			aux=v0[i]-v1[i];
			distance+=aux*aux;
		}
		distance=Math.sqrt(distance);
		return distance;
	}
	
	/**
	 * Create a command to extract non-dominated solutions
	 * */
	protected NonDominatedSolutionsExtractor createExtractor(){
		ParetoComparator fcomparator = (ParetoComparator)getSolutionComparator().getFitnessComparator();
		NonDominatedSolutionsExtractor command = new NonDominatedSolutionsExtractor();
		command.setComparator(fcomparator);
		return command;
	}

}
