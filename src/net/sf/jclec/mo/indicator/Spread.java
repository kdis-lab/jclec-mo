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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.sf.jclec.IFitness;
import net.sf.jclec.IIndividual;
import net.sf.jclec.fitness.SimpleValueFitness;
import net.sf.jclec.fitness.ValueFitnessComparator;
import net.sf.jclec.mo.comparator.fcomparator.LexicographicComparator;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;

/**
 * The Spread indicator measures the extent of the Pareto front approximation
 * with respect to the true Pareto front. Objective values in the range [0,1]
 * are recommended and a bi-objective problem is required. A maximization problem 
 * is assumed by default, but it can be inverted.
 * 
 * <p><i>Paper</i>: K. Deb, A. Pratap, S. Agarwal, T. Meyarivan. 
 * "A Fast and Elistist Multiobjective Genetic Algorithm: NSGA-II". 
 * IEEE Transactions on Evolutionary Computation. vol. 6. no. 2. 2002.</p>
 * 
 * <p>HISTORY:
 * <ul>
 * <li>(AR|JRR|SV, 1.0, March 2018)		First release.</li>
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
public class Spread extends BinaryIndicator {

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
	public Spread(){
		super();
		this.maximized=true;
		this.scaled=true;
	}

	/**
	 * Parameterized constructor.
	 * @param paretoSet The first Pareto set.
	 * @param paretoSet2 The second Pareto set.
	 * */
	public Spread(List<IIndividual> paretoSet, List<IIndividual> paretoSet2) {
		super(paretoSet, paretoSet2, true, true);
	}

	/**
	 * Parameterized constructor.
	 * @param paretoSet The first Pareto set.
	 * @param paretoFrontFileName Name of the file that contains the second Pareto front.
	 * */
	public Spread(List<IIndividual> paretoSet, String paretoFront2FileName) {
		super(paretoSet, paretoFront2FileName, true, true);
	}

	/**
	 * Parameterized constructor.
	 * @param paretoFrontFileName Name of the file that contains the first Pareto front.
	 * @param paretoSet2 The second Pareto set.
	 * */
	public Spread(String paretoFrontFileName, List<IIndividual> paretoSet2) {
		super(paretoFrontFileName, paretoSet2, true, true);
	}

	/**
	 * Parameterized constructor.
	 * @param paretoFrontFileName Name of the file that contains the first Pareto front.
	 * @param paretoFront2FileName Name of the file that contains the second Pareto front.
	 * */
	public Spread(String paretoFrontFileName, String paretoFront2FileName) {
		super(paretoFrontFileName, paretoFront2FileName, true, true);
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Set the maximization flag. Null values will not set.
	 * @param maximize New maximization flag
	 * */
	public void setMaximize(Boolean maximize){
		if(maximize!=null)
			this.maximized = maximize;
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
			int nSolutions2 = getNumberOfSolutionsSecondFront();
			int nObjectives = getNumberOfObjectives();

			// Create a lexicographical comparator. Since the 'inverse' flag
			// of is set to 'false' for maximization problems or 'true' for minimization problems.
			Comparator<IFitness> componentComparators [] = new ValueFitnessComparator[nObjectives];
			for(int i=0; i<nObjectives; i++)
				componentComparators[i] = new ValueFitnessComparator(!this.maximized);
			LexicographicComparator comparator = new LexicographicComparator(componentComparators);

			// Sort the solutions in lexicographical order.
			this.setFront(lexicographicalOrdering(this.front, comparator));
			this.setSecondFront(lexicographicalOrdering(this.secondFront, comparator));

			// Distance to the extremes
			double distanceFirst = euclideanDistance(getSolutionFront(0), getSolutionSecondFront(0));
			double distanceLast = euclideanDistance(getSolutionFront(nSolutions-1), getSolutionSecondFront(nSolutions2-1));

			// Distances in the front approximation
			double [] distance = new double[nSolutions-1];
			double meanDistance = 0.0;
			for(int i=0; i<nSolutions-1; i++){
				distance[i] = euclideanDistance(getSolutionFront(i), getSolutionFront(i+1));
				meanDistance += distance[i];
			}
			meanDistance = meanDistance / (nSolutions-1);

			// Compute the indicator
			double difDistances = 0.0, spread;
			for(int i=0; i<nSolutions-1; i++){
				difDistances += Math.abs(distance[i]-meanDistance);
			}

			spread = (distanceFirst+distanceLast+difDistances)/(distanceFirst+distanceLast+((nSolutions-1)*meanDistance));
			this.setResult(spread);
		}
	}

	/////////////////////////////////////////////////////////////////
	// -------------------------------------------- Protected methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Sort the solutions in the front in lexicographical order.
	 * It considers the maximization flag to properly configure the comparator.
	 * @param front The Pareto front.
	 * @param comparator The comparator.
	 * @param Front ordered using the comparator.
	 * */
	protected double [][] lexicographicalOrdering(double [][] front, Comparator<IFitness> comparator){
		int nObjectives = front[0].length;
		int nSolutions = front.length;

		// Copy the front in a list
		List<IFitness> aux = new ArrayList<IFitness>();
		MOFitness fitness;
		for(int i=0; i<nSolutions; i++){
			fitness = new MOFitness(new SimpleValueFitness[nObjectives]);
			for(int j=0; j<nObjectives; j++){
				fitness.setObjectiveValue(j, new SimpleValueFitness(front[i][j]));
			}
			aux.add(fitness);
		}

		// Sort the list
		Collections.sort(aux, comparator);

		// Copy in the front
		for(int i=0; i<nSolutions; i++){
			fitness = (MOFitness)aux.get(i);
			for(int j=0; j<nObjectives; j++){
				try {
					front[i][j]=fitness.getObjectiveDoubleValue(j);
				} catch (IllegalAccessException | IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
		}
		return front;
	}

	/**
	 * Compute the Euclidean distance between two solutions.
	 * @param solution1 First solution.
	 * @param solution2 Second solution.
	 * @return Distance between solutions.
	 * */
	protected double euclideanDistance(double [] solution1, double [] solution2){
		int nObjectives = solution1.length;
		double distance = 0.0;
		for(int i=0; i<nObjectives; i++){
			distance += (solution1[i]-solution2[i])*(solution1[i]-solution2[i]);
		}
		distance = Math.sqrt(distance);
		return distance;
	}
}
