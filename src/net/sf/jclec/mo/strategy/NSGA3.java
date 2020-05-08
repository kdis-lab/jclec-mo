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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.configuration.Configuration;

import net.sf.jclec.IFitness;
import net.sf.jclec.IIndividual;
import net.sf.jclec.mo.IMOEvaluator;
import net.sf.jclec.mo.command.DasDennisVectorGenerator;
import net.sf.jclec.mo.command.ObjectiveInverter;
import net.sf.jclec.mo.comparator.MOSolutionComparator;
import net.sf.jclec.mo.comparator.fcomparator.ParetoComparator;
import net.sf.jclec.mo.evaluation.fitness.INSGA2MOFitness;
import net.sf.jclec.mo.evaluation.fitness.INSGA3MOFitness;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;
import net.sf.jclec.selector.RandomSelector;

/**
 * NSGA-III strategy.
 *  
 * <p>The <b>N</b>on <b>D</b>ominating <b>S</b>orting <b>G</b>enetic 
 * <b>A</b>lgorithm (NSGA-III) is a many-objective algorithm based on 
 * the non-dominated sorting approach of NSGA-II and a new diversity 
 * preservation technique based on reference points.</p>
 * 
 * <p>NSGA-III replaces the crowding distance used in NSGA-II by
 * a survival mechanism that considers the distance of equivalent solutions,
 * i.e. those belonging to the same front, to a set of reference points. 
 * The set of reference points can be provided by the used, simulating the 
 * most interesting search directions, or be automatically generated following 
 * the Das and Denni's approach.</p>
 * 
 * <p>Since the paper does not describe some aspects of the algorithm and 
 * an official code publicly available has not been released, the current 
 * implementation should be carefully used. This class is partially based on 
 * the first online implementation of the algorithm in C++:
 * {@link http://web.ntnu.edu.tw/~tcchiang/publications/nsga3cpp/nsga3cpp.htm}</p>
 *  
 * <p><i>Paper</i>: K. Deb, H. Jain, 
 * “An Evolutionary Many-Objective Optimization Algorithm Using
 * Reference-point Based Non-dominated Sorting Approach, Part I:
 * Solving Problems with Box Constraints”, IEEE Transactions on 
 * Evolutionary Computation, vol. 18, no. 4, pp.577-601, 2014.</p>
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
public class NSGA3 extends MOStrategy {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 6675581216711633597L;

	/** Reference points */
	private List<double []> refPoints;

	/** Using reference points defined by the user */
	private boolean userPoints;
	
	/** Command to invert objective values */
	protected ObjectiveInverter commandInvert;
	
	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public NSGA3(){
		super();
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Reference points are defined by the user?
	 * @return True if reference points are defined
	 * by the user, false otherwise.
	 * */
	public boolean areUserPoints(){
		return this.userPoints;
	}

	/**
	 * Set the user points flag.
	 * @param userPoints The value to be set.
	 * */
	protected void setUserPoints(boolean userPoints){
		this.userPoints = userPoints;
	}

	/**
	 * Get the set of reference points
	 * @return Reference points
	 * */
	public List<double []> getReferencePoints(){
		return this.refPoints;
	}

	/**
	 * Set the reference points
	 * @param refPoints The values that have to be set.
	 * */
	protected void setReferencePoints(List<double []> refPoints){
		this.refPoints = refPoints;
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>NSGA-III has the following parameters:
	 * <ul>
	 * 	<li>user-points (<code>boolean</code>): <p>the user
	 * will specify the reference points. Default value
	 * is false.</p></li>
	 * <p>If the user
	 * specify the reference points, (s)he should provide
	 * the path to the file where these points are stored.</p>
	 * 	<ul><li> path (String): <p>the file path. </p></li></ul>
	 * </li>
	 * <li>p1 (<code>integer</code>): <p>number of divisions in the boundary
	 * layer used in the automatic generation of reference points.</p></li>
	 * <li>p2 (<code>integer</code>): <p>number of divisions in the inner
	 * layer used in the automatic generation of reference points. Optional
	 * parameter.</p></li>
	 * </ul>
	 * </p>
	 * */
	@Override
	public void configure(Configuration settings) {
		// Call super configuration
		super.configure(settings);
		
		// Check if reference points are specified by the user
		boolean userPoints = settings.getBoolean("user-points", false);
		setUserPoints(userPoints);

		if(userPoints){
			String filename = settings.getString("path");
			readReferencePoints(filename);
		}
		else{
			int p1 = settings.getInt("p1");
			int p2 = settings.getInt("p2",-1);

			// Check the number of divisions
			int numObjs = ((IMOEvaluator)this.getContext().getEvaluator()).numberOfObjectives();
			if(p1<numObjs && p2==-1){
				throw new IllegalArgumentException("If p2 is not provided, p1 should be equal or greater than the number of objectives");
			}
			
			// Create reference points
			DasDennisVectorGenerator command = new DasDennisVectorGenerator(numObjs,p1,p2);
			command.execute();
			this.refPoints = command.getUniformVectors();
		}

		// NSGA-III needs that all objectives are to be minimized or maximized, but not mixing both types
		if(isMaximized()==null){
			System.err.println("All objectives should be maximized or minimized");
			System.exit(-1);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>NSGA-III does not use the archive.</p>
	 * */
	@Override
	public List<IIndividual> initialize(List<IIndividual> population) {
		
		// Initialize the auxiliary command
		this.commandInvert = new ObjectiveInverter();
		

		// nsga3 does not use the archive
		return null;
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
	 * <p>NSGA-III uses a Pareto comparator.</p>
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
	 * <p>NSGA-III randomly selects individuals to act as parents.</p>
	 * */
	@Override
	public List<IIndividual> matingSelection(List<IIndividual> population, List<IIndividual> archive) {
		// Random selection from the current population
		RandomSelector rselector = new RandomSelector(getContext());
		List<IIndividual> parents = rselector.select(population, getContext().getPopulationSize());
		return parents;
	}

	/**
	 * {@inheritDoc}
	 * <p>NSGA-III sorts the individuals by fronts like NSGA-II.
	 * The new population is conformed by the individuals belonging
	 * to the best fronts. Individuals from the critical front
	 * are selected based on the reference points.
	 * More specifically, the individuals closer to the reference
	 * points that have less associated individuals are selected.</p>
	 * */
	@Override
	public List<IIndividual> environmentalSelection(List<IIndividual> population, 
			List<IIndividual> offspring, List<IIndividual> archive) {

		// Join populations
		List<IIndividual> allInds = new ArrayList<IIndividual> ();
		allInds.addAll(population);

		for(IIndividual ind : offspring)
			if(!allInds.contains(ind))
				allInds.add(ind);

		// Invert the objective values, since NSGA3 was designed for minimization problems
		if(isMaximized()){
			this.commandInvert.setPopulation(allInds);
			this.commandInvert.execute();
		}

		// Initialize the specific properties of the fitness
		for(int i=0; i<allInds.size(); i++){
			((INSGA2MOFitness)allInds.get(i).getFitness()).setFront(-1);
			((INSGA2MOFitness)allInds.get(i).getFitness()).setDominatedBy(0);
			((INSGA3MOFitness)allInds.get(i).getFitness()).setAssociatedRefPoint(-1);
			((INSGA3MOFitness)allInds.get(i).getFitness()).setDistance(-1);
		}

		// Sort the individuals by fronts
		NSGA2 nsga2 = new NSGA2();
		nsga2.setSolutionComparator(this.getSolutionComparator());
		List<List<IIndividual>> populationByFronts = nsga2.fastNonDominatedSorting(allInds);

		// The new population is created with the individuals from the first fronts 
		List<IIndividual> survivors = new ArrayList<IIndividual>();
		List<IIndividual> accFronts = new ArrayList<IIndividual>();

		int size =  getContext().getPopulationSize();
		int index = 0;
		while((survivors.size() + populationByFronts.get(index).size()) < size ){
			survivors.addAll(populationByFronts.get(index));
			accFronts.addAll(populationByFronts.get(index));
			index++;
		}

		// Complete the population, if required, with some individuals
		// from the critical front using the reference points
		accFronts.addAll(populationByFronts.get(index));// add the critical front
		int fill = size-survivors.size();

		// Scale procedure and update the reference points (if required)
		List<double []> newRefPoints = normalize(accFronts);

		// Associate each individual with a reference point
		associateRefPoints(accFronts, newRefPoints);

		// Count individuals associated to each reference point,
		// only considering those individuals from the first fronts
		int nPoints = newRefPoints.size();
		int [] nicheCount = new int[nPoints];
		for(int i=0; i<nPoints; i++)
			nicheCount[i]=0;

		int point;
		size = survivors.size();
		for(int i=0; i<size; i++){
			point = ((INSGA3MOFitness)survivors.get(i).getFitness()).getAssociatedRefPoint();
			nicheCount[point]++;
		}

		// Select best individuals from the critical front
		List<IIndividual> newInds = niching(populationByFronts.get(index), nicheCount, fill);
		survivors.addAll(newInds);

		// Revert the objective transformation if required
		if(isMaximized()){
			this.commandInvert.setPopulation(survivors);
			this.commandInvert.execute();
		}
		return survivors;
	}

	/**
	 * {@inheritDoc}.
	 * <p>NSGA-III does not use the archive.</p>
	 * */
	@Override
	public List<IIndividual> updateArchive(List<IIndividual> population, 
			List<IIndividual> offspring, List<IIndividual> archive) {
		return null;
	}

	/**
	 * {@inheritDoc}.
	 * <p>NSGA-III does not assign fitness values.</p>
	 * */
	@Override
	protected void fitnessAssignment(List<IIndividual> population, List<IIndividual> archive) {
		// Do nothing
	}

	/////////////////////////////////////////////////////////////////
	// -------------------------------------------- Protected methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Scale the objective values and
	 * update the reference points.
	 * @param population Set of individuals.
	 * @return Updated reference points.
	 * */
	protected List<double []> normalize(List<IIndividual> population){

		int nObjectives = ((IMOEvaluator)getContext().getEvaluator()).numberOfObjectives();
		double value;
		double [] idealPoint = new double [nObjectives];
		int size = population.size();
		
		// For each objective
		for(int j=1; j<nObjectives; j++){

			// Compute ideal point (minimum value of each objective)
			idealPoint[j] = computeIdealPoint(population, j);

			// Translate objectives
			for(int i=0; i<size; i++){
				try {
					value = ((MOFitness)population.get(i).getFitness()).getObjectiveDoubleValue(j) - idealPoint[j];
					((INSGA3MOFitness)population.get(i).getFitness()).setNormalisedObjectiveValue(value, j);
				} catch (IllegalAccessException | IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
		}

		// Compute extreme points 
		double [][] extremePoints = computeExtremePoints(population, idealPoint);

		// Compute intercepts
		double intercepts [] = computeIntercepts(extremePoints);

		// Normalize objectives
		for(int i=0; i<size; i++){
			for(int j=0; j<nObjectives; j++){
				value = ((INSGA3MOFitness)population.get(i).getFitness()).getNormalisedObjectiveValue(j);
				if(Math.abs(intercepts[j]-idealPoint[j]) > 10e-20)
					value = value/(intercepts[j]-idealPoint[j]);
				else
					value = value/10e-20;

				((INSGA3MOFitness)population.get(i).getFitness()).setNormalisedObjectiveValue(value,j);
			}
		}

		// Update reference points if required
		if(areUserPoints())
			return mapUserPoints(idealPoint, intercepts);
		else
			return refPoints;
	}

	/**
	 * Associate the individuals with
	 * the closer reference point.
	 * @param population The set of individuals.
	 * @param points Reference points.
	 * */
	protected void associateRefPoints(List<IIndividual> population, List<double[]> points){
		// For each individual, compute the distance to each reference point
		// and save the smaller distance
		int index, nRefPoints = points.size();
		int nObjectives = points.get(0).length;
		double distance, minDistance;
		double [] fitness = new double [nObjectives];
		double norm;
		double [][] dirRefLines = new double[nRefPoints][nObjectives];

		// Compute the unit vector of each reference point
		// to obtain the reference line direction
		for(int i=0; i<nRefPoints; i++){
			norm = 0.0;

			// compute the norm
			for(int j=0; j<nObjectives; j++){
				norm += points.get(i)[j]*points.get(i)[j];
			}
			norm = Math.sqrt(norm);

			// Normalize the vector
			for(int j=0; j<nObjectives; j++){
				dirRefLines[i][j] = points.get(i)[j]/norm;
			}
		}

		// Compute the perpendicular distance of each individual from each reference line
		int size = population.size();
		for(int i=0; i<size; i++){
			minDistance = Double.POSITIVE_INFINITY;
			index = -1;

			for(int j=0; j<nObjectives; j++){
				fitness[j] = ((INSGA3MOFitness)population.get(i).getFitness()).getNormalisedObjectiveValue(j);
			}

			// Each individual is associated to the reference point 
			// whose reference line is closest to the individual
			for(int j=0; j<nRefPoints; j++){
				distance = perpendicularDistance(fitness, dirRefLines[j]);
				if(distance<minDistance){
					minDistance = distance;
					index = j;
				}
			}
			((INSGA3MOFitness)population.get(i).getFitness()).setDistance(minDistance);
			((INSGA3MOFitness)population.get(i).getFitness()).setAssociatedRefPoint(index);
		}
	}

	/**
	 * Select individuals that will survive
	 * from the critical front.
	 * <p>Note: the criticalFront and the niche
	 * count will be modified by this method.</p>
	 * @param criticalFront Individuals belonging to the critical front.
	 * @param nicheCount Number of associated individuals to each reference point.
	 * @param numToSelect Number of individuals to select.
	 * @return Selected individuals from the set of candidates.
	 * */
	protected List<IIndividual> niching(List<IIndividual> criticalFront, int [] nicheCount, int numToSelect){
		// The list of individuals chosen from the critical front
		List<IIndividual> selected = new ArrayList<IIndividual>();

		ArrayList<Integer> minRefPoints = new ArrayList<Integer>();
		ArrayList<Integer> candidates = new ArrayList<Integer>(); 
		int minNicheCount, associatedPoint;
		IIndividual ind;
		int k=0, index;
		double minDistance, distance;
		
		// A total of numToSelect new individuals will be added
		while(k<numToSelect) {
			
			// Firstly, identify the reference point with minimum nicheCount
			minRefPoints.clear();
			minNicheCount = Integer.MAX_VALUE;
			for(int i=0; i<nicheCount.length; i++){
				if(nicheCount[i]!=-1 && nicheCount[i]<=minNicheCount){
					if (nicheCount[i]<minNicheCount){
						minRefPoints.clear();
						minNicheCount = nicheCount[i];
					}
					minRefPoints.add(i);
				}
			}
			
			//System.out.println("MinRefPoints.size="+minRefPoints.size());

			// If more than one point has the minimum distance, choose the point at random
			int minPoint;
			if(minRefPoints.size()>1){
				index = getContext().createRandGen().choose(0, minRefPoints.size());
				minPoint = minRefPoints.get(index);
			}
			else
				minPoint = minRefPoints.get(0);

			// Get the individuals in the critical front that are associated to the selected point
			candidates.clear();
			for(int i=0; i<criticalFront.size(); i++){
				ind = criticalFront.get(i);
				if(ind != null){
					associatedPoint = ((INSGA3MOFitness)ind.getFitness()).getAssociatedRefPoint();
					if(associatedPoint == minPoint){
						candidates.add(i);
					}
				}
			}

			// If no individual from the critical point is associated with the
			// reference point, the point is excluded
			if(candidates.size() == 0){
				nicheCount[minPoint] = -1;
			}

			// Otherwise, check the niche count and the distances 
			else{
				// If it is the first individual to be associated, then add
				// the individual with the shortest distance to the point
				if(nicheCount[minPoint]==0){
					index = -1;
					minDistance = Double.POSITIVE_INFINITY;
					for(int i=0; i<candidates.size(); i++){
						ind = criticalFront.get(candidates.get(i));
						if(ind != null){
							distance = ((INSGA3MOFitness)ind.getFitness()).getDistance();
							if(distance<minDistance){
								index = candidates.get(i);
								minDistance = distance;
							}
						}
					}
				}

				// Otherwise, choose an individual at random
				else{
					index = getContext().createRandGen().choose(0, candidates.size());
					index = candidates.get(index);
				}

				// Add the individual
				selected.add(criticalFront.get(index).copy());

				// Update counters and delete the individual
				nicheCount[minPoint]++;
				k++;
				criticalFront.set(index, null);
			}
		}
		return selected;
	}

	/**
	 * Get the minimum objective value
	 * within a set of individuals.
	 * @param population The set of individuals.
	 * @param index Objective position.
	 * @return Minimum value for the specified objective.
	 * */
	protected double computeIdealPoint(List<IIndividual> population, int index){
		double min = Double.POSITIVE_INFINITY;
		double value;
		for(IIndividual ind: population){
			try {
				value = ((MOFitness)ind.getFitness()).getObjectiveDoubleValue(index);
				if(value<min){
					min = value;
				}
			} catch (IllegalAccessException | IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		return min;
	}

	/**
	 * Find the extreme points in each objective axis. This point is
	 * identified by finding the solution with the minimum ASF 
	 * (Achievement Scalarized Function).
	 * @param population The set of individuals.
	 * @param index Objective position.
	 * @return Extreme points.
	 * */
	protected double [][] computeExtremePoints(List<IIndividual> population, double [] idealPoint){
		int nObjectives = ((IMOEvaluator)getContext().getEvaluator()).numberOfObjectives();
		double asf, minAsf;
		double [] weights = new double[nObjectives];
		double [][] points = new double[nObjectives][nObjectives];
		int size = population.size();
		IIndividual ind;
		int index;

		// Compute ASF for each individual
		for(int i=0; i<nObjectives; i++){

			// Set the weights of the axis direction, e.g. (1,0,0), (0,1,0), (0,0,1),
			// where 0 is replaced by 10E-6
			for(int k=0; k<nObjectives; k++){
				weights[k]=0.000001;
			}
			weights[i] = 1.0;
			minAsf = Double.POSITIVE_INFINITY;
			index = 0;

			// Compute ASF for each individual
			for(int j=0; j<size; j++){
				ind = population.get(j);
				asf = computeASF(ind,idealPoint,weights);

				// If ASF value is lower than the current
				// ASF for this axis, update its associated
				// extreme point (the solution)
				if(asf < minAsf){
					minAsf = asf;
					index = j;
				}
			}

			// Save the coordinates of the extreme point for objective i
			for(int k=0; k<nObjectives; k++){
				try {
					points[i][k] = ((MOFitness)population.get(index).getFitness()).getObjectiveDoubleValue(k);
				} catch (IllegalAccessException | IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
		}
		return points;
	}

	/**
	 * Calculate the Achievement Scalarizing Function (ASF)
	 * for a given individual.
	 * @param individual The individual.
	 * @param idealPoint The ideal point (minimum value of each objective).
	 * @param weights The axis direction.
	 * @return ASF value.
	 * */
	protected double computeASF(IIndividual individual, double [] idealPoint, double [] weights){
		double asf, max = Double.NEGATIVE_INFINITY;
		MOFitness fitness = ((MOFitness)individual.getFitness());
		int nObjectives = fitness.getNumberOfObjectives();
		for(int i=0; i<nObjectives; i++){
			try {
				asf = (fitness.getObjectiveDoubleValue(i)-idealPoint[i])/weights[i];
				if(asf > max)
					max = asf;
			} catch (IllegalAccessException | IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		return max;
	}


	/**
	 * Compute intercepts of the hyperplane determined 
	 * by the given extreme points.
	 * <p>The intercept of a surface on a coordinate axis is the
	 * distance from the origin to the point where
	 * the surface cuts the axis:</p>
	 * <p>Example: 2x+3y=6, x-intercept=3, y-intercept=2</p>
	 * @param extremePoints Extreme points of the hyperplane
	 * @return Intercepts 
	 * */
	protected double[] computeIntercepts(double [][] extremePoints) {
		// Construct the hyperplane
		int nObjectives = extremePoints[0].length;
		double [] b = new double[nObjectives];

		for(int i=0; i<nObjectives; i++)
			b[i] = 1.0;

		double [] x = gaussianElimination(extremePoints, b);

		// Compute the intercepts of the hyperplane
		if(x[0]!=Double.NaN) // the hyperplane exist
			for(int i=0; i<nObjectives; i++)
				x[i] = 1.0/x[i];

		else{ // the hyperplane can not be defined
			// Use the max value of each objective (the paper does not specify how to handle it)
			for(int i=0; i<nObjectives; i++){
				x[i] = Double.NEGATIVE_INFINITY;
				for(int j=0; j<extremePoints.length; j++){
					if(extremePoints[j][i]>x[i]){
						x[i] = extremePoints[j][i];
					}
				}
			}	
		}
		return x;
	}

	/**
	 * Gaussian Elimination (row reduction algorithm)
	 * to solve systems of linear equations. This method
	 * can be used to find the equation of the hyperplane 
	 * for a given set of points.
	 * 
	 * <p>Reference:{@link http://introcs.cs.princeton.edu/java/95linear/GaussianElimination.java.html}</p>
	 * 
	 * <p>Example: Given three points (4,2,3), (1,2,3) and (-1,-2,-1), 
	 * the equation of the hyperplane is ax+by+cz=d,
	 * so we have to solve the following system of linear equations:</p>
	 * 
	 * <p>4a+2b+3c=3</p>
	 * <p>1a+2b+3c=3</p>
	 * <p>-a-2b-c=3</p>
	 * 
	 * <p>This method will return x=(0,-3,3), so the equation of the hyperplane
	 * is -3y+3z=3.</p>
	 * 
	 * @param matrix Matrix of points, e.g. {(4,2,3),(1,2,3),(-1,-2,-1)}
	 * @param b Vector, e.g. (3,3,3)
	 * @return The set of coefficients of the equation of the hyperplane, x.
	 * */
	protected double [] gaussianElimination(double [][] matrix, double [] b){

		int n = b.length;
		double eps = 1e-10, alpha;
		int max;
		boolean duplicate = false;

		// Gaussian elimination with partial pivoting
		for(int p=0; p<n && !duplicate; p++){

			// Find pivot row and swap
			max = p;
			for(int i=p+1; i<n; i++){
				if(Math.abs(matrix[i][p])>Math.abs(matrix[max][p])){
					max=i;
				}
			}

			double [] temp = matrix[p]; matrix[p] = matrix[max]; matrix[max]=temp;
			double t = b[p]; b[p] = b[max]; b[max]=t;

			// Check singular or nearly singular matrix (there exists duplicate points)
			if(Math.abs(matrix[p][p])<=eps){
				duplicate = true;
			}

			// Pivot within the matrix and b
			if(!duplicate){
				for(int i=p+1; i<n; i++){
					alpha = matrix[i][p]/matrix[p][p];
					b[i] -= alpha*b[p];
					for(int j=p; j<n; j++){
						matrix[i][j] -= alpha*matrix[p][j];
					}
				}
			}
		}

		double [] x = new double[n];

		// An unique hyperplane does not exit
		// The original paper does not define how to solve it!
		if(duplicate){
			for(int i=0; i<n; i++){
				x[i] = Double.NaN;
			}
		}

		// Gaussian elimination continues
		else{

			// Back substitution
			for(int i=n-1; i>=0; i--){
				double sum = 0.0;
				for(int j=i+1; j<n; j++){
					sum+=matrix[i][j]*x[j];
				}
				x[i]=(b[i]-sum)/matrix[i][i];
			}
		}
		return x;
	}

	/**
	 * Map the reference points to the created hyperplane.
	 * @param idealPoint The ideal point.
	 * @param intercepts Intercepts of the hyperplane.
	 * @return Set of reference points in the hyperplane.
	 * */
	protected List<double []> mapUserPoints(double [] idealPoint, double [] intercepts) {
		int nPoints = this.refPoints.size();
		int nObjectives = this.refPoints.get(0).length;
		List<double []> points = new ArrayList<double []>(nPoints);

		for(int i=0; i<nPoints; i++){
			double [] point = new double[nObjectives];
			for(int j=0; j<nObjectives; j++){
				point[j] = (this.refPoints.get(i)[j] - idealPoint[j])/intercepts[j];
			}
			points.add(point);
		}

		return points;
	}

	/**
	 * Perpendicular distance between a point
	 * and the line determined by a given point
	 * and the origin.
	 * 
	 * <p>The perpendicular distance is defined as:
	 * d = || (a-p) - ((a-p)·n)n ||, where:
	 * <ul>
	 * 	<li>a is a point in the line (here, the origin is considered).
	 * 	<li>p is the desired point.
	 * 	<li>n is the direction of the line expressed as an unit vector.
	 * </ul>
	 * </p>
	 * @param point The point (p).
	 * @param direction The unit vector that represents the line direction (n).
	 * @return Perpendicular distance between the point and the line.
	 * */
	protected double perpendicularDistance(double [] point, double [] direction){

		double distance = 0.0;
		int size = point.length;
		double [] term1 = new double[size];

		// If a=origin, then (a-p)=-p
		for(int i=0; i<size; i++)
			term1[i] = -1.0*point[i];

		// Compute the scalar product between (a-p) and n
		double k = 0.0;
		for(int i=0; i<size; i++){
			k+=term1[i]*direction[i];
		}

		// Compute (a-p)-((a-p)·n)n
		double [] term2 = new double[size];
		for(int i=0; i<size; i++)
			term2[i] = term1[i] - (k*direction[i]);

		// Distance of the resulting vector
		for(int i=0; i<size; i++){
			distance += term2[i]*term2[i];
		}
		distance = Math.sqrt(distance);

		return distance;
	}
	
	/**
	 * Set the reference points from the configuration.
	 * It read the coordinates of the points from a file.
	 * @param filename The name of the file.
	 * */
	protected void readReferencePoints(String filename) {
		File file = new File(filename);
		int nObjectives = ((IMOEvaluator)getContext().getEvaluator()).numberOfObjectives();
		StringTokenizer tokenizer; 
		int nPoints, dim;
		List<double []> points = null;

		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));

			// Read the number of points and the number of dimensions
			String line = reader.readLine();
			if(line!=null){
				tokenizer = new StringTokenizer(line, ",");
				nPoints = Integer.parseInt(tokenizer.nextToken());
				dim = Integer.parseInt(tokenizer.nextToken());

				// Check parameters
				if(nPoints<1){
					reader.close();
					throw new IllegalArgumentException("The number of points should be greater than 0");
				}
				if(dim!=nObjectives){
					reader.close();
					throw new IllegalArgumentException("The dimension does not match with the number of objectives");
				}
			}
			else{
				reader.close();
				throw new IOException("First line should contain the number of points and the dimension");
			}

			// Read the points
			int i = 0;
			points = new ArrayList<double[]>(nPoints);
			double [] point;
			while(i!=nPoints){
				line = reader.readLine();
				tokenizer = new StringTokenizer(line, ",");
				if(tokenizer.countTokens() == dim){
					point = new double[dim];
					for(int j=0; j<dim; j++){
						try{
							point[j] = Double.parseDouble(tokenizer.nextToken());
						}catch(NumberFormatException e){
							reader.close();
							System.err.println("Wrong format of the coordinates of reference points");
							e.printStackTrace();
						}
						points.add(point);
					}
					i++;
				}
				else{
					reader.close();
					throw new IllegalArgumentException("The list of reference points is not complete or its format is wrong.");
				}
			}

			// Close reader
			reader.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e){
			e.printStackTrace();
		}

		// Store the points
		setReferencePoints(points);
	}
}
