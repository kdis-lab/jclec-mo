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

import java.util.List;

import net.sf.jclec.IIndividual;

/**
 * The Hypervolume (HV) obtains the hyper-area covered
 * by a given Pareto front. It requires a maximization 
 * problem, as well as objective values in the range [0,1].
 *
 * <p>The original implementation in C by Zitzler is available at:
 * {@link ftp://ftp.tik.ee.ethz.ch/pub/people/zitzler/hypervol.c}
 * </p>
 * 
 * <p><i>Book:</i> C.A. Coello Coello, D.A. Van Veldhuizen, G.B. Lamont.
 * "Evolutionary Algorithms for Solving Multi-Objective Problems". 1st edition.
 * Kluwer Academic Publ. 2002.</p>
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
 * @see UnaryIndicator
 * */

public class Hypervolume extends UnaryIndicator{

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -1326235296743142527L;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public Hypervolume(){
		super();
		this.maximized=true;
		this.scaled=true;
	}
	
	/**
	 * Parameterized constructor.
	 * @param paretoSet The Pareto set.
	 * */
	public Hypervolume(List<IIndividual> paretoSet){
		super(paretoSet, true, true);
	}

	/**
	 * Parameterized constructor.
	 * @param paretoFrontFileName The name of the file that contains the Pareto front.
	 * */
	public Hypervolume(String paretoFrontFileName){
		super(paretoFrontFileName, true, true);
	}
	
	//////////////////////////////////////////////////////////////////
	//----------------------------------------------- Override methods
	//////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void calculate(){
		if(getNumberOfSolutions()>0){
			double value = this.calculateHypervolume(getNumberOfSolutions(), getNumberOfObjectives());
			this.setResult(value);
		}
	}

	//////////////////////////////////////////////////////////////////
	//----------------------------------------------- Private methods
	//////////////////////////////////////////////////////////////////

	/**
	 * Calculate the hypervolume indicator.
	 * @param nSolutions Number of solutions.
	 * @param nObjectives Number of objectives.
	 * @return Hypervolume value for the pareto front.
	 * */
	private double calculateHypervolume(int nSolutions, int  nObjectives){

		int n = nSolutions;
		double volume = 0.0, distance = 0.0;
		int nNondominatedSolutions;
		double tempVolume, tempDistance;

		while (n > 0) {
			nNondominatedSolutions = filterNondominatedSet(n, nObjectives - 1);
			tempVolume = 0;
			if (nObjectives < 3) {
				if (nNondominatedSolutions < 1)  
					throw new IllegalArgumentException("The number of non-dominated solutions is less than 1");

				tempVolume = getValueFront(0,0);
			} else
				tempVolume = calculateHypervolume(nNondominatedSolutions, nObjectives - 1);

			tempDistance = surfaceUnchangedTo(n, nObjectives - 1);
			volume += tempVolume * (tempDistance - distance);
			distance = tempDistance;
			n = reduceNondominatedSet(n, nObjectives - 1, distance);
		}
		return volume;
	} 

	/**
	 * Resort some solutions in the front (from 0 to nSolutions-1)
	 * considering the non-dominated solutions regarding the first
	 * nObjectives. The number of non-dominated solutions is returned.
	 * @param nSolutions Number of solutions to be considered.
	 * @param nObjectives Number of objectives to be considered.
	 * @return Number of non-dominated solutions.
	 * */
	private int filterNondominatedSet(int nSolutions, int  nObjectives){
		int i = 0, j, n = nSolutions;
		while (i < n) {
			j = i+1;
			while (j < n) {
				// i dominates j, remove solution j
				if (dominates(getSolutionFront(i), getSolutionFront(j), nObjectives)) {
					n--;
					swap(j, n);
				} 
				// j dominates i, remove solution i and decrement i to evaluate the new solution at this position
				else if (dominates(getSolutionFront(j), getSolutionFront(i), nObjectives)) {
					n--;
					swap(i, n);
					i--;
					break;
				}
				// non dominated solutions
				else
					j++;
			}
			i++;
		}
		return n;
	}


	/** 
	 * Check if solution1 dominates solution2 in terms
	 * of the first nObjectives.
	 * @param solution1 First solution.
	 * @param solution2 Second solution.
	 * @param Number of objectives to be considered.
	 * @return True if solution1 dominates solution2, false otherwise.
	 * */
	private boolean  dominates(double [] solution1, double [] solution2, int  nObjectives) {
		int betterInAnyObjective = 0, i;
		for (i=0; i<nObjectives && solution1[i]>=solution2[i]; i++)
			if (solution1[i] > solution2[i]) 
				betterInAnyObjective = 1;
		return ((i >= nObjectives) && (betterInAnyObjective>0));
	}

	/**
	 * Swap two solutions in the front.
	 * @param index1 Index of the first solution.
	 * @param index2 Index of the second solution.
	 * */
	private void  swap(int index1, int index2){
		double [] temp = getSolutionFront(index1);
		setSolutionFront(index1, getSolutionFront(index2));
		setSolutionFront(index2, temp);
	}

	/**
	 * Calculate next value regarding a given dimension (objective),
	 * considering the points stored in front[0..noPoints-1].
	 * @param nSolutions Number of Points.
	 * @param objective The number of objective.
	 * */
	private double surfaceUnchangedTo(int nSolutions, int objective) {
		double  minValue, value;
		if (nSolutions < 1)  
			throw new IllegalArgumentException("The number of solutions must be greater than 1");

		minValue = getValueFront(0,objective);
		for (int i=1; i<nSolutions; i++) {
			value = getValueFront(i,objective);
			if (value<minValue)
				minValue = value;
		}
		return minValue;
	}

	/**
	 * Remove all points which have a value smaller than the given threshold 
	 * regarding the given dimension (objective). The front is sorted again 
	 * to set the remaining solutions at the beginning (positions 0..nSolutions-1).
	 * @param nSolutions Number of solutions.
	 * @param objective The index of the objective.
	 * @param threshold The threshold.
	 * */
	private int reduceNondominatedSet(int nSolutions, int objective, double threshold){
		int n = nSolutions;
		for(int i=0; i<n; i++)
			if (getValueFront(i,objective) <= threshold) {
				n--;
				swap(i, n);
			}
		return n;
	}	
}