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
 * The Spacing (S) describes the spread of solutions in a given Pareto set.
 * Objective values in the range [0,1] are recommended to compute distances.
 * 
 * <p><i>Book:</i> C.A. Coello Coello, D.A. Van Veldhuizen, G.B. Lamont.
 * "Evolutionary Algorithms for Solving Multi-Objective Problems". 1st edition.
 * Kluwer Academic Publ. 2002.</p>
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
 * @see UnaryIndicator
 * */
public class Spacing extends UnaryIndicator{

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -957412979548708483L;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public Spacing(){
		super();
		this.maximized=null;
		this.scaled=true;
	}
	
	/**
	 * Parameterized constructor.
	 * @param paretoSet The Pareto set.
	 * */
	public Spacing(List<IIndividual> paretoSet){
		super(paretoSet, null, true);
	}

	/**
	 * Parameterized constructor.
	 * @param paretoFrontFileName The name of the file that contains the Pareto front.
	 * */
	public Spacing(String paretoFrontFileName){
		super(paretoFrontFileName, null, true);
	}

	//////////////////////////////////////////////////////////////////
	//----------------------------------------------- Override methods
	//////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void calculate() {
		int nSolutions = getNumberOfSolutions();
		double [] d_i; // the array of minimum distances
		double value, minDistance, sumOfDistances=0.0, meanDistance=0.0;

		// If the Pareto set contains less than 2 solutions, the spacing is zero
		if (nSolutions < 2) {
			setResult(0.0);
		}

		else{

			// Compute the distances between solutions
			d_i = new double[nSolutions];
			for (int i=0; i<nSolutions; i++) {
				minDistance = Double.POSITIVE_INFINITY;
				for (int j=0; j<nSolutions; j++) {
					if (i != j) {
						minDistance = Math.min(minDistance, distance(getSolutionFront(i),getSolutionFront(j)));
					}
				}
				d_i[i] = minDistance;
			}

			// Compute the mean of the distances
			for(int i=0; i<nSolutions; i++)
				meanDistance += d_i[i];
			meanDistance = meanDistance / nSolutions;
			
			// Compute the Spacing value
			for (int i=0; i<nSolutions; i++)
				sumOfDistances += (meanDistance-d_i[i])*(meanDistance-d_i[i]);
			value = Math.sqrt(sumOfDistances / (nSolutions-1));
			setResult(value);
		}
	}

	//////////////////////////////////////////////////////////////////
	//------------------------------------------------ Private methods
	//////////////////////////////////////////////////////////////////

	/**
	 * Manhattan distance between two solutions.
	 * @param solution1 First solution.
	 * @param solution2 Second solution.
	 * @return Manhattan distance between the two solutions.
	 * */
	private double distance(double [] solution1, double [] solution2){
		double distance = 0.0;
		int nObjectives = solution1.length;
		for(int i=0; i<nObjectives; i++){
			distance += Math.abs(solution1[i]-solution2[i]);
		}
		return distance;
	}
}