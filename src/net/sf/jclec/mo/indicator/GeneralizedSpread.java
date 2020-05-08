/*
This file belongs to JCLEC-MOEA, a Java library for the
application and development of metaheuristic algorithms 
for the resolution of multi-objective and many-objective 
optimization problems.

Copyright (C) 2018.  A. Ramírez, J.R. Romero, S. Ventura
Knowledge Discovery and Intelligent Systems Research Group

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package net.sf.jclec.mo.indicator;

import java.util.Comparator;
import java.util.List;

import net.sf.jclec.IFitness;
import net.sf.jclec.IIndividual;
import net.sf.jclec.fitness.ValueFitnessComparator;
import net.sf.jclec.mo.comparator.fcomparator.LexicographicComparator;

/**
 * The Spread indicator measures the extent of the Pareto front approximation
 * with respect to the second Pareto front. It is a generalization of the Spread
 * indicator for problems having more than 2 objectives. Objective values in the
 * range [0,1] are recommended, whereas minimization problems 
 * can be considered modifying the maximization flag.
 * 
 * <p><i>Paper</i>: A.Zhou, Y. Jin, Q. Zhang, B. Sendhoff, E. Tsang.
 * "Combining Model-based and Genetics-based Offspring Generation
 * for Multi-objective Optimization Using a Convergence Criterion".
 * IEEE Congress on Evolutionary Computation, pp. 892-899. 2006.</p>
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
 * @see Spread
 * */
public class GeneralizedSpread extends Spread {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 6982464970553291011L;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public GeneralizedSpread(){
		super();
	}

	/**
	 * Parameterized constructor.
	 * @param paretoSet The first Pareto set.
	 * @param paretoSet2 The second Pareto set.
	 * */
	public GeneralizedSpread(List<IIndividual> paretoSet, List<IIndividual> paretoSet2) {
		super(paretoSet, paretoSet2);
	}

	/**
	 * Parameterized constructor.
	 * @param paretoSet The first Pareto set.
	 * @param paretoFront2FileName Name of the file that contains the second Pareto front.
	 * */
	public GeneralizedSpread(List<IIndividual> paretoSet, String paretoFront2FileName) {
		super(paretoSet, paretoFront2FileName);
	}

	/**
	 * Parameterized constructor.
	 * @param paretoFrontFileName Name of the file that contains the first Pareto set.
	 * @param paretoSet2 The second Pareto set.
	 * */
	public GeneralizedSpread(String paretoFrontFileName, List<IIndividual> paretoSet2) {
		super(paretoFrontFileName, paretoSet2);
	}

	/**
	 * Parameterized constructor.
	 * @param paretoFrontFileName Name of the file that contains the first Pareto front.
	 * @param paretoFrontFileName Name of the file that contains the second Pareto front.
	 * */
	public GeneralizedSpread(String paretoFrontFileName, String paretoFront2FileName) {
		super(paretoFrontFileName, paretoFront2FileName);
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void calculate() {
		int nSolutions = getNumberOfSolutions();

		// If the size of the front is zero, the indicators value will be always equal to 1.0
		if(nSolutions<=1){
			this.setResult(1.0);
		}

		else if(nSolutions>1){

			int nSolutionsTrue = getNumberOfSolutionsSecondFront();
			int nObjectives = getNumberOfObjectives();

			// First, get the extreme points in the true pareto front
			double [][] extremeValues = new double[nObjectives][nObjectives];
			double maxValue, value;
			int index = 0;

			for(int i=0; i<nObjectives; i++){
				maxValue = Double.NEGATIVE_INFINITY;
				for(int j=0; j<nSolutionsTrue; j++){
					value = getValueSecondFront(j,i);
					if(value>maxValue){
						maxValue = value;
						index = j;
					}
				}
				extremeValues[i] = getSolutionSecondFront(index);
			}

			// Sort the front
			Comparator<IFitness> componentComparators [] = new ValueFitnessComparator[nObjectives];
			for(int i=0; i<nObjectives; i++)
				componentComparators[i] = new ValueFitnessComparator(!this.maximized);
			LexicographicComparator comparator = new LexicographicComparator(componentComparators);
			setFront(lexicographicalOrdering(this.front, comparator));

			// Distances to the extremes
			double extremeDistance = 0.0;
			for(int i=0; i<nObjectives; i++){
				extremeDistance += distanceNearestNeighbor(extremeValues[i],this.front);
			}

			// Distances in the front approximation
			double [] distance = new double[nSolutions];
			double meanDistance = 0.0;
			for(int i=0; i<nSolutions; i++){
				distance[i] = distanceNearestNeighbor(getSolutionFront(i),this.front);
				meanDistance += distance[i];
			}
			meanDistance = meanDistance/nSolutions;

			// Compute the indicator
			double spread, difDistances = 0.0;
			if(meanDistance != 0){
				for(int i=0; i<nSolutions; i++){
					difDistances += Math.abs(distance[i]-meanDistance);
				}
				spread = (extremeDistance + difDistances) / (extremeDistance + (nSolutions*meanDistance));
			}
			else
				spread = 1.0; // All the solutions are the same point in the objective space
			this.setResult(spread);
		}
	}

	/**
	 * Distance to the nearest neighbor.
	 * @param solution The solution.
	 * @param neighbors The neighbors.
	 * @param Euclidean distance to the nearest neighbor.
	 * */
	protected double distanceNearestNeighbor(double [] solution, double [][] neighbors){
		double minDistance = Double.POSITIVE_INFINITY, distance;
		int nSolutions = neighbors.length;

		for(int i=0; i<nSolutions; i++){
			distance = euclideanDistance(solution, neighbors[i]);
			if(distance<minDistance && distance!=0)// The solution itself can not be considered as a neighbor
				minDistance=distance;
		}
		// If all the solutions represent the same point in the objective space, then return 0
		if(minDistance == Double.POSITIVE_INFINITY){
			minDistance = 0.0;
		}
		return minDistance;
	}
}
