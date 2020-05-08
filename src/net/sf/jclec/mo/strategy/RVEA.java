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
import java.util.Comparator;
import java.util.List;

import org.apache.commons.configuration.Configuration;

import net.sf.jclec.IFitness;
import net.sf.jclec.IIndividual;
import net.sf.jclec.mo.IMOEvaluator;
import net.sf.jclec.mo.command.DasDennisVectorGenerator;
import net.sf.jclec.mo.command.MaxObjectiveValue;
import net.sf.jclec.mo.command.MinObjectiveValue;
import net.sf.jclec.mo.command.ObjectiveInverter;
import net.sf.jclec.mo.comparator.MOSolutionComparator;
import net.sf.jclec.mo.comparator.fcomparator.ParetoComparator;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;
import net.sf.jclec.selector.RandomSelector;

/**
 * RVEA strategy.
 *  
 * <p>The <b>R</b>eference <b>V</b>ector-gruided <b>E</b>volutionary <b>A</b>lgorithm
 * (RVEA) is a many-objective approach based on reference points.</p> 
 * 
 * <p>Similar to NSGA-III, RVEA maintains a set of reference points that are use
 * during environmental selection to choose the most promising solutions. A 
 * scalarization approach, named angle-penalized distance, is used to decide
 * the best solution for each reference vector. Instead of distances, RVEA is based
 * on the angle between the solution in the objective space and the reference vector.
 * This implementation generates uniform reference vectors using Das and Dennis' 
 * method as in NSGA-II. Nevertheless, it could be extended to implement other methods 
 * or accept user-defined points.</p>
 * 
 * <p>The algorithm includes an adaption mechanism to update the reference vectors 
 * based on the limits of the objective functions at a user-defined frequency. 
 * For this reason, this algorithm requires the configuration of the maximum number 
 * of generations. Although the paper describes the approach for minimization problem,
 *  a problem with all objectives to be maximized can be considered as well.</p>
 * 
 * <p><i>Paper</i>: R. Cheng, Y. Jin, M. Olhofer, B. Sendhoff. 
 * “A Reference Vector Guided Evolutionary Algorithm for Many-Objective Optimization”, 
 * IEEE Transactions on Evolutionary Computation, vol. 20, no. 5, pp. 773-791. 2016.</p>
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
 */

public class RVEA extends MOStrategy {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -8772430254262258971L;

	/** Random selector */
	protected RandomSelector selector;

	/** Initial reference vectors */
	protected List<double []> initialRefVectors;

	/** Current reference vectors */
	protected List<double []> refVectors;

	/** Frequency at which normalization is applied */
	protected double frequency;

	/** The number of updates of the reference vectors */
	protected int step;

	/** The ideal point in each generation */
	protected double [] idealPoint;

	/** The parameter to control the rate of change of the penalty function */
	protected double alpha;

	/** Command to invert objective values */
	protected ObjectiveInverter commandInvert;

	/** Command to compute the minimum value for an objective */
	protected MinObjectiveValue commanMinObj;

	/** Command to compute the maximum value for an objective */
	protected MaxObjectiveValue commanMaxObj;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public RVEA(){
		super();
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Get the set of initial reference vectors.
	 * @return Reference vectors.
	 * */
	public List<double []> getInitialReferenceVectors(){
		return this.initialRefVectors;
	}

	/**
	 * Set the initial reference vectors.
	 * @param refVectors The values that have to be set.
	 * */
	protected void setInitialReferenceVectors(List<double []> refVectors){
		this.initialRefVectors = refVectors;
	}

	/**
	 * Get the set of reference vectors.
	 * @return Reference vectors.
	 * */
	public List<double []> getReferenceVectors(){
		return this.refVectors;
	}

	/**
	 * Set the reference vectors.
	 * @param refVectors The values that have to be set.
	 * */
	protected void setReferenceVectors(List<double []> refVectors){
		this.refVectors = refVectors;
	}

	/**
	 * Get the update frequency for reference vectors.
	 * @return Update frequency
	 * */
	public double getFrequency() {
		return frequency;
	}

	/**
	 * Set the update frequency for reference vectors
	 * @param frequency Update frequency
	 * */
	protected void setFrequency(double frequency) {
		this.frequency = frequency;
	}

	/**
	 * Get the value of the alpha parameter.
	 * @return Alpha parameter value.
	 * */
	public double getAlpha() {
		return alpha;
	}

	/**
	 * Set the value of the alpha parameter.
	 * @param alpha New value.
	 * */
	protected void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>RVEA has the following parameters:
	 * <ul>
	 * 	<li>fr (<code>double</code>): <p>A value between 0 and 1 to configure the frequency
	 * 	of the normalization update. Default value is 0.1.</p></li>
	 * 	<li>alpha (<code>int</code>): <p>The exponent to compute the APD value. Default value is 2.</p></li>
	 * 	<li>p1 (<code>integer</code>): <p>number of divisions in the outer
	 * layer used in the automatic generation of reference points.</p></li>
	 * <li>p2 (<code>integer</code>): <p>number of divisions in the inner
	 * layer used in the automatic generation of reference points. Optional
	 * parameter.</p></li>
	 * </ul>
	 * </p>
	 * */
	@Override
	public void configure(Configuration settings) {
		super.configure(settings);

		// Update frequency
		double frequency = settings.getDouble("fr",0.1);
		if(frequency < 0.0 || frequency > 1.0) {
			frequency = 0.1;
			throw new IllegalArgumentException("Frequency parameter value should be a value between 0 and 1. Default value (0.1) will be used instead.");
		}
		setFrequency(frequency);

		// Penalty function control rate
		int alpha = settings.getInt("alpha", 2);
		if(alpha < 0) {
			alpha = 2;
			throw new IllegalArgumentException("Alpha parameter value should be greater than 0. Default value (2) will be used instead.");
		}
		setAlpha(alpha);

		// Initialize reference vectors
		int p1 = settings.getInt("p1");
		int p2 = settings.getInt("p2",-1);

		// Check the number of divisions
		int numObjs = ((IMOEvaluator)this.getContext().getEvaluator()).numberOfObjectives();
		if(p1<numObjs && p2==-1){
			throw new IllegalArgumentException("If p2 is not provided, p1 should be equal or greater than the number of objectives");
		}
		// Generate reference vectors
		DasDennisVectorGenerator command = new DasDennisVectorGenerator(numObjs,p1,p2);
		command.execute();
		setInitialReferenceVectors(command.getUniformVectors());

		// RVEA needs all objectives are to be minimized or maximized
		if(isMaximized()==null){
			System.err.println("All objectives should be maximized or minimized");
			System.exit(-1);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>RVEA does not use archive, <code>null</code> is always returned.</p>
	 * */
	@Override
	public List<IIndividual> initialize(List<IIndividual> population) {
		// Initialize auxiliary commands
		this.commandInvert = new ObjectiveInverter();
		this.commanMinObj = new MinObjectiveValue();
		this.commanMaxObj = new MaxObjectiveValue();

		// Make a copy of the initial reference vectors
		this.refVectors = new ArrayList<double []>(this.initialRefVectors.size());
		this.initialRefVectors.forEach(v -> this.refVectors.add(v.clone()));
		this.step = 0;

		// Create random selector
		this.selector = new RandomSelector(getContext());

		// This strategy does not use archive
		return null;
	}

	/**
	 * {@inheritDoc}
	 * <p>RVEA updates reference vectors at a given frequency</p>
	 * */
	@Override
	public void update() {
		int generation = getContext().getGeneration();
		int maxGener = getContext().getMaxGenerations();
		if(Math.abs((this.step*this.frequency*maxGener)-generation)<0.0000001) {
			normalizeReferenceVectors(getContext().getInhabitants());
			this.step++;
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>RVEA uses a Pareto comparator.</p>
	 * */
	@Override
	public void createSolutionComparator(Comparator<IFitness>[] components) {
		// Fitness comparator
		ParetoComparator fcomparator = new ParetoComparator(components);
		// Individuals comparator
		MOSolutionComparator comparator = new MOSolutionComparator(fcomparator);
		setSolutionComparator(comparator);
	}

	/**
	 * {@inheritDoc}
	 * <p>By default, RVEA randomly selects individuals to act as parents.</p>
	 * */
	@Override
	public List<IIndividual> matingSelection(List<IIndividual> population, List<IIndividual> archive) {
		// Random selection from the current population
		List<IIndividual> parents = this.selector.select(population, getContext().getPopulationSize());
		return parents;
	}

	/**
	 * {@inheritDoc}
	 * <p>In RVEA, this process consists in four steps:
	 * <ol>
	 * <li>Objective value translation</li>
	 * <li>Population partition</li>
	 * <li>APD calculation</li>
	 * <li>Elitism selection</li>
	 * </ol>
	 * </p>
	 * */
	@Override
	public List<IIndividual> environmentalSelection(List<IIndividual> population, List<IIndividual> offspring,
			List<IIndividual> archive) {

		// Join populations and scale values
		List<IIndividual> allSolutions = new ArrayList<IIndividual>(population);
		allSolutions.addAll(offspring);
		translateObjectives(allSolutions);

		// The population is partitions according to the distance to the reference vectors
		List<List<IIndividual>> solutionsByRefVector = splitPopulation(allSolutions);

		// For each partition, one solution will be selected based on convergence and diversity
		assignAPDToSolutions(solutionsByRefVector);
		List<IIndividual> survivors = elitistSelection(solutionsByRefVector);

		// If some reference vector did not have associated solutions, add some at random.
		int toSelect =  getContext().getPopulationSize() - survivors.size();
		if(toSelect > 0) {
			List<IIndividual> randomSolutions = this.selector.select(allSolutions, toSelect);
			for(IIndividual s: randomSolutions) {
				survivors.add(s.copy());
			}
		}

		revertObjectiveTranslation(survivors);
		
		return survivors;
	}

	/**
	 * {@inheritDoc}
	 * <p>RVEA does not use archive, this method always returns null.</p>
	 * */
	@Override
	public List<IIndividual> updateArchive(List<IIndividual> population, List<IIndividual> offspring,
			List<IIndividual> archive) {
		// Do nothing
		return null;
	}

	/**
	 * {@inheritDoc}
	 * <p>This method is not used in RVEA.</p>
	 * */
	@Override
	protected void fitnessAssignment(List<IIndividual> population, List<IIndividual> archive) {
		// Do nothing
	}

	/**
	 * Update the reference vector considering the objective limits in the current population.
	 * @param solutions Current population
	 * */
	protected void normalizeReferenceVectors(List<IIndividual> solutions) {
		int numObjs = ((IMOEvaluator)getContext().getEvaluator()).numberOfObjectives();
		this.commanMinObj.setPopulation(solutions);
		this.commanMaxObj.setPopulation(solutions);
		double [] min = new double[numObjs];
		double [] max = new double[numObjs];

		// Compute minimum and maximum value for each objective
		for(int i=0; i<numObjs; i++) {
			this.commanMinObj.setObjectiveIndex(i);
			this.commanMinObj.execute();
			min[i] = this.commanMinObj.getMinValue();

			this.commanMaxObj.setObjectiveIndex(i);
			this.commanMaxObj.execute();
			max[i] = this.commanMaxObj.getMaxValue();
		}

		// Update reference vectors
		this.refVectors.clear();
		int n = this.initialRefVectors.size();
		double [] refVector, initialVector;
		for(int i=0; i<n; i++) {
			refVector = new double[n];
			initialVector = this.initialRefVectors.get(i);
			for(int j=0; j<numObjs; j++) {
				if(max[i]-min[i]>0)
					refVector[j] = initialVector[j]*(max[i]-min[i]);
				else
					refVector[j] = 0.000001;
			}

			for(int j=0; j<numObjs; j++) {
				refVector[j] /= norm(refVector);
			}

			this.refVectors.add(refVector);
		}
	}

	/**
	 * Translate the objective values of the given solutions. Fitness
	 * objects will be modified as result.
	 * @param solutions A set of solutions.
	 * */
	protected void translateObjectives(List<IIndividual> solutions) {

		// First, invert objective values for maximization problems
		if(isMaximized()) {
			this.commandInvert.setPopulation(solutions);
			this.commandInvert.execute();
		}
		
		// Find ideal point (minimum value for each objective)
		this.idealPoint = computeIdealPoint(solutions);

		// Translate so that the ideal point is the origin
		int size = solutions.size();
		int numObjs =  ((IMOEvaluator)getContext().getEvaluator()).numberOfObjectives();
		double value;
		IIndividual sol;
		for(int i=0; i<size; i++) {
			sol = solutions.get(i);
			for(int j=0; j<numObjs; j++) {
				try {
					value = ((MOFitness)sol.getFitness()).getObjectiveDoubleValue(j);
					value = value - this.idealPoint[j];
					((MOFitness)sol.getFitness()).setObjectiveDoubleValue(value, j);
				} catch (IllegalAccessException|IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Compute the ideal point according to a given solutions. The ideal point
	 * is the one having the minimum value for each objective function.
	 * @param solutions A set of solutions.
	 * @return A double array with the ideal point.
	 * */
	protected double [] computeIdealPoint(List<IIndividual> solutions) {
		int numObjs = ((IMOEvaluator)getContext().getEvaluator()).numberOfObjectives();
		double [] idealPoint = new double[numObjs];
		this.commanMinObj.setPopulation(solutions);
		for(int i=0; i<numObjs; i++) {
			this.commanMinObj.setObjectiveIndex(i);
			this.commanMinObj.execute();
			idealPoint[i] = this.commanMinObj.getMinValue();
		}
		return idealPoint;
	}

	/**
	 * Split a set of solutions according to their distances to the reference vectors.
	 * @param solutions A set of solutions.
	 * @return A list for each reference point containing the solutions associated to that point.
	 * */
	protected List<List<IIndividual>> splitPopulation(List<IIndividual> solutions) {
		// One set of solutions for each reference vector
		List<List<IIndividual>> partitions = new ArrayList<List<IIndividual>>(this.refVectors.size());
		for(int i=0; i<this.refVectors.size(); i++) {
			partitions.add(new ArrayList<IIndividual>());
		}

		// Each solution is associated to one partition (closer reference vector)
		int popSize = solutions.size();
		int refSize = this.refVectors.size();
		int index = 0;
		double cosine, maxCosine;
		IIndividual solution;
		MOFitness fitness;
		for(int i=0; i<popSize; i++) {
			solution = solutions.get(i);
			fitness = (MOFitness)solution.getFitness();
			maxCosine = Double.NEGATIVE_INFINITY;
			// Find the reference vector with the maximum cosine angle
			for(int j=0; j<refSize; j++){
				cosine = cosine(fitnessAsVector(fitness), this.refVectors.get(j));
				if(cosine > maxCosine) {
					index = j;
					maxCosine = cosine;
				}
			}
			partitions.get(index).add(solution);
		}

		return partitions;
	}

	/**
	 * Transform the fitness into a simple double array.
	 * @param f Fitness object.
	 * @return A double array with the objective values.
	 * */
	protected double [] fitnessAsVector(MOFitness f) {
		int numObjs = f.getNumberOfObjectives();
		double [] values = new double[numObjs];
		for(int i=0; i<numObjs; i++) {
			try {
				values[i] = f.getObjectiveDoubleValue(i);
			} catch (IllegalAccessException | IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		return values;
	}

	/**
	 * Compute the angle between two vectors
	 * @param fitness Objective values
	 * @param vector Reference vector
	 * @retun The cosine between both vectors.
	 * */
	protected double cosine(double [] fitness, double [] vector) {
		double res = 0;
		for(int i=0; i<fitness.length; i++) {
			res += fitness[i]*vector[i];
		}
		res = res / (norm(fitness)*norm(vector));
		return res;
	}

	/**
	 * Compute the norm of a vector
	 * @param vector The vector
	 * @return The norm value
	 * */
	protected double norm(double [] vector) {
		double res = 0;
		for(int i=0; i<vector.length; i++) {
			res += vector[i]*vector[i];
		}
		res = Math.sqrt(res);
		return res;
	}

	/**
	 * Assign the angle-penalized distance of each solution to its associated reference vector. 
	 * The value is stored as the fitness value, so that no additional data structures are required.
	 * @param partitions The solutions into partitions
	 * */
	protected void assignAPDToSolutions(List<List<IIndividual>> partitions) {
		int numVectors = this.refVectors.size();
		double apd;
		double [] vector;
		int size;
		MOFitness fitness;
		double minAngle;

		for(int i=0; i<numVectors; i++) {
			vector = this.refVectors.get(i);
			minAngle = computeMinimumAngle(i);	
			size = partitions.get(i).size();
			for(int j=0; j<size; j++) {
				fitness = (MOFitness)partitions.get(i).get(j).getFitness();
				apd = computeAPD(fitnessAsVector(fitness), vector, minAngle);
				fitness.setValue(apd);
			}
		}
	}

	/**
	 * Compute the minimum angle for a reference vector with respect to the rest of vectors.
	 * @param index The index of the reference vector.
	 * @return Minimum angle.
	 * */
	protected double computeMinimumAngle(int index) {
		int numVectors = this.refVectors.size();
		double angle, minValue = Double.POSITIVE_INFINITY;
		double [] vector = this.refVectors.get(index);
		for(int i=0; i<numVectors; i++) {
			if(i!=index) {
				angle = Math.acos(cosine(vector,this.refVectors.get(i)));
				if(angle < minValue) {
					minValue = angle;
				}
			}
		}
		return minValue;
	}

	/**
	 * Compute the angle-penalized distance for a given set of objective values.
	 * @param fitness The objective values as a double array.
	 * @param vector The corresponding reference vector.
	 * @param minAngle Minimum angle for the reference vector.
	 * @return Angle-penalized value.
	 * */
	protected double computeAPD(double [] fitness, double [] vector, double minAngle) {
		int numObjs = fitness.length;
		double generation = (double)getContext().getGeneration();
		double maxGener = (double)getContext().getMaxGenerations();
		double diversity = numObjs * Math.pow((generation/maxGener),this.alpha) * Math.acos(cosine(fitness, vector)) / minAngle; 
		double distance = (1+diversity)*norm(fitness);
		return distance;
	}

	/**
	 * Choose the best solution for each partition. The best solution
	 * is the one having the minimum APD value.
	 * @param partitions Population split into sublist, one for each reference vector.
	 * @return A list with the best solution for each partition.
	 * */
	protected List<IIndividual> elitistSelection(List<List<IIndividual>> partitions) {
		List<IIndividual> selected = new ArrayList<IIndividual>();
		List<IIndividual> candidates;
		IIndividual solution;
		int n = partitions.size();
		for(int i=0; i<n; i++) {
			candidates = partitions.get(i);
			if(candidates.size()>0) {
				solution = findSolutionMinimumDistance(candidates);
				selected.add(solution.copy());
			}
		}	
		return selected;
	}

	/**
	 * Find the solution with the minimum APD value from a given list.
	 * @param solutions The set of solutions.
	 * @return The solution with minimum APD value.
	 * */
	protected IIndividual findSolutionMinimumDistance(List<IIndividual> solutions) {
		double dist, minDist = Double.POSITIVE_INFINITY;
		int index = 0, size = solutions.size();
		for(int i=0; i<size; i++) {
			dist = ((MOFitness)solutions.get(i).getFitness()).getValue();
			if(dist < minDist) {
				minDist = dist;
				index = i;
			}
		}
		return solutions.get(index);
	}

	/**
	 * Translate the objective values to the original values and invert if 
	 * the problem should be maximized. Values are directly changed in the
	 * fitness objects.
	 * @param solutions A list of solutions.
	 * */
	protected void revertObjectiveTranslation(List<IIndividual> solutions) {
		// Add ideal point
		// Translate so that the ideal point is the origin
		int size = solutions.size();
		int numObjs =  ((IMOEvaluator)getContext().getEvaluator()).numberOfObjectives();
		double value;
		IIndividual sol;
		for(int i=0; i<size; i++) {
			sol = solutions.get(i);
			for(int j=0; j<numObjs; j++) {
				try {
					value = ((MOFitness)sol.getFitness()).getObjectiveDoubleValue(j);
					value = value + this.idealPoint[j];
					((MOFitness)sol.getFitness()).setObjectiveDoubleValue(value, j);
				} catch (IllegalAccessException|IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
		}

		// Additionally, invert if the problem should be maximized
		if(isMaximized()) {
			this.commandInvert.setPopulation(solutions);
			this.commandInvert.execute();
		}
	}
}
