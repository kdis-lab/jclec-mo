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

package net.sf.jclec.mo.indicator;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.Configuration;

import net.sf.jclec.IIndividual;

/**
 * The R2 indicator compares the quality of the Pareto front approximation and
 * the second (true) Pareto front in terms of the proximity to a reference point.
 * It requires a maximization problem, whereas objective values in the range [0,1]
 * are recommended to compute distances.
 * 
 * <p><i>Paper</i>: M.P. Hansen, A. Jaszkiewicz. "Evaluating the quality of
 * approximations to the non-dominated set". Tech. Report IMM-REP-1198-7.
 * University of Denmark. 1998.</p>
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
 * @see BinaryIndicator
 * */
public class R2 extends BinaryIndicator {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 7215048475841684655L;

	/** Scalarizing vectors (vectors of weights) */
	protected double [][] lambda;

	/** The parameter that controls the number of scalarizing vectors */
	protected int h;

	/** The coordinates of the reference point */
	protected double [] refPoint;

	/** The utility function values for the first front */
	protected double [] utilityA;
	
	/** The utility function values for the second front */
	protected double [] utilityB;
	
	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public R2(){
		super();
		this.maximized=true;
		this.scaled=true;
	}

	/**
	 * Parameterized constructor. 
	 * @param paretoSet The first Pareto set.
	 * @param paretoSet2 The second Pareto set.
	 * @param refPoint The reference point.
	 * @param h The controller of the number of scalarizing vectors.
	 * */
	public R2(List<IIndividual> paretoSet, List<IIndividual> paretoSet2, double [] refPoint, int h){
		super(paretoSet, paretoSet, true, true);
		this.refPoint = refPoint;
		this.h=h;
		this.initializeWeightVectors();
	}

	/**
	 * Parameterized constructor.
	 * @param paretoSet The first Pareto set.
	 * @param paretoFront2FileName Name of the file that contains the second Pareto front.
	 * @param refPoint The reference point.
	 * @param h The controller of the number of scalarizing vectors.
	 * */
	public R2(List<IIndividual> paretoSet, String paretoFront2FileName, double [] refPoint, int h){
		super(paretoSet, paretoFront2FileName, true, true);
		this.refPoint = refPoint;
		this.h=h;
		this.initializeWeightVectors();
	}

	/**
	 * Parameterized constructor.
	 * @param paretoFrontFileName Name of the file that contains the first Pareto front.
	 * @param paretoSet2 The second Pareto set.
	 * @param refPoint The reference point.
	 * @param h The controller of the number of scalarizing vectors.
	 * */
	public R2(String paretoFrontFileName, List<IIndividual> paretoSet2, double [] refPoint, int h){
		super(paretoFrontFileName, paretoSet2, true, true);
		this.refPoint = refPoint;
		this.h=h;
		this.initializeWeightVectors();
	}

	/**
	 * Parameterized constructor.
	 * @param paretoFrontFileName Name of the file that contains the first Pareto front.
	 * @param paretoFront2FileName Name of the file that contains the second Pareto front.
	 * @param refPoint The reference point.
	 * @param h The controller of the number of scalarizing vectors.
	 * */
	public R2(String paretoFrontFileName, String paretoFront2FileName, double [] refPoint, int h){
		super(paretoFrontFileName, paretoFront2FileName, true, true);
		this.refPoint = refPoint;
		this.h=h;
		this.initializeWeightVectors();
	}

	//////////////////////////////////////////////////////////////////
	//------------------------------------------------ Get/set methods
	//////////////////////////////////////////////////////////////////

	/**
	 * Get the H parameter.
	 * @return H value.
	 * */
	protected int getH(){
		return this.h;
	}

	/**
	 * Set the H parameter.
	 * @param h The value that has to be set.
	 * */
	protected void setH(int h){
		this.h=h;
	}

	/**
	 * Get the reference point.
	 * @return The reference point.
	 * */
	protected double [] getRefPoint(){
		return this.refPoint;
	}

	/**
	 * Set the reference point.
	 * @param newRefPoint New reference point.
	 * */
	protected void setRefPoint(double [] newRefPoint){
		this.refPoint = newRefPoint;
	}

	//////////////////////////////////////////////////////////////////
	//----------------------------------------------- Override methods
	//////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>This indicator requires the following parameters:
	 * <ul>
	 * 	<li>refPoint (<code>String</code>):
	 *  <p>The reference point coordinates (separated by commas).</p></li>
	 *  <li>H (<code>Integer</code>):
	 *  <p>The control parameter to space partition.</p></li>
	 * 	</ul>
	 * </p>
	 * */
	@Override
	public void configure(Configuration settings) {
		
		super.configure(settings);
		String [] aux = settings.getStringArray("refPoint");
		int nObjs = aux.length;
		double [] point = new double[nObjs];
		try{
			for(int i=0; i<nObjs; i++){
				point[i] = Double.parseDouble(aux[i]);
			}
			setRefPoint(point);
		}catch(NumberFormatException e){
			System.err.println("Error parsing Reference Point coordinates");
			System.exit(-1);
		}
		
		int h = settings.getInt("H");
		if(h<=0 || (nObjs-1)>(h+nObjs-1))
			throw new IllegalArgumentException("Invalid H value. The space partition is not valid for the number of objectives");
		setH(h);
		initializeWeightVectors();
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void calculate() {

		int nVectors = this.lambda.length;
		double r2 = 0.0;
		
		// Compute the utility functions
		computeUtilityFunctions();

		// Compute the indicator
		for(int i=0; i<nVectors; i++){
			r2+=this.utilityB[i]-this.utilityA[i];
		}
		r2 = r2/nVectors;
		this.setResult(r2);
	}

	//////////////////////////////////////////////////////////////////
	//---------------------------------------------- Protected methods
	//////////////////////////////////////////////////////////////////

	/**
	 * Compute the utility function using
	 * the Tchebycheff distance.
	 * */
	protected void computeUtilityFunctions(){
		int nVectors = this.lambda.length;
		int nSolutionsA = getNumberOfSolutions();
		int nSolutionsB = getNumberOfSolutionsSecondFront();

		this.utilityA = new double[nVectors];
		this.utilityB = new double[nVectors];
		double distance;

		// Compute utility function of the first front
		for(int i=0; i<nVectors; i++){
			this.utilityA[i] = Double.POSITIVE_INFINITY;
			for(int j=0; j<nSolutionsA; j++){
				distance = tchebycheffDistance(getSolutionFront(j), i);
				if(distance<this.utilityA[i])
					this.utilityA[i]=distance;
			}
		}

		// Compute utility function of the second front
		for(int i=0; i<nVectors; i++){
			this.utilityB[i] = Double.POSITIVE_INFINITY;
			for(int j=0; j<nSolutionsB; j++){
				distance = tchebycheffDistance(getSolutionSecondFront(j), i);
				if(distance<this.utilityB[i])
					this.utilityB[i]=distance;
			}
		}
	}
	
	/**
	 * Tchebycheff distance between a solution and 
	 * the reference point considering the weight vector at the
	 * given index.
	 * @param solution The solution.
	 * @param index The lambda vector index.
	 * @return Tchebycheff distance.
	 * */
	protected double tchebycheffDistance(double [] solution, int index){
		double maxDistance = Double.NEGATIVE_INFINITY, distance;
		int nObjectives = solution.length;
		
		for(int i=0; i<nObjectives; i++){
			distance = this.lambda[index][i]*Math.abs(solution[i]-this.refPoint[i]);
			if(distance>maxDistance)
				maxDistance=distance;
		}
		return maxDistance;
	}

	//////////////////////////////////////////////////////////////////
	//------------------------------------------------ Private methods
	//////////////////////////////////////////////////////////////////

	/**
	 * Generate the uniform weight vectors
	 * using the H parameter for an
	 * arbitrary number of objectives.
	 * */
	protected void initializeWeightVectors(){
		ArrayList<Double> hIntervals = generateIntervals();
		int numberOfObjectives = getRefPoint().length;
		int numberOfValues = hIntervals.size();
		int numberOfCombinations = (int)Math.pow(numberOfValues,numberOfObjectives);
		int consecutiveElements=numberOfCombinations;
		int accProduct = 1;
		int step;
		int [][] allCombinationsMatrix = new int[numberOfCombinations][numberOfObjectives];

		// Expand all possible combinations
		for(int i=0; i<numberOfObjectives; i++){
			consecutiveElements /= numberOfValues;
			for(int j = 0; j < numberOfValues; j++){
				for(int k = 0; k < consecutiveElements; k++){
					for(int r = 0; r < accProduct; r++){
						step = consecutiveElements*numberOfValues;
						allCombinationsMatrix[r*step + j*consecutiveElements + k][i] = j;
					}
				}
			}
			accProduct *= numberOfValues;
		}

		// Create the uniform vectors
		double sum = 0.0;
		double [] weight;
		ArrayList<double []> uniformWeights = new ArrayList<double []>();
		for(int i=0; i<numberOfCombinations; i++){
			sum=0.0;
			// Get the sum of the weights in each combination
			weight = new double[numberOfObjectives];
			for(int j=0; j<numberOfObjectives; j++){
				sum += hIntervals.get(allCombinationsMatrix[i][j]);
			}

			// The combination is an uniform vector (sum==1)
			if(Math.abs(sum-1.0)<0.00000001){
				for(int j=0; j<numberOfObjectives; j++){
					weight[j]=hIntervals.get(allCombinationsMatrix[i][j]);
				}
				uniformWeights.add(weight);
			}			
		}

		// Copy in lamdba matrix
		int nVectors = uniformWeights.size();
		this.lambda = new double[nVectors][numberOfObjectives];
		for(int i=0; i<nVectors; i++){
			this.lambda[i] = uniformWeights.get(i);
		}
	}

	/**
	 * Generate the equally spaced intervals
	 * using the H parameter.
	 * @return Intervals for weight vectors.
	 * */
	private ArrayList<Double> generateIntervals(){
		ArrayList<Double> values = new ArrayList<Double>();
		for(int i=0; i<=this.h; i++){
			values.add((double)i/this.h);
		}
		return values;
	}
}