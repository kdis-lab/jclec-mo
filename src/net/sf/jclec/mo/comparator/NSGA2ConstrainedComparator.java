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

package net.sf.jclec.mo.comparator;

import net.sf.jclec.IIndividual;
import net.sf.jclec.mo.IConstrained;
import net.sf.jclec.mo.comparator.fcomparator.MOFitnessComparator;

/**
 * Compare two individuals in a problem with constraints as proposed
 * in NSGA-II. This comparator establishes that two infeasible 
 * individuals should be compared using the degree of constraint
 * violation.
 * 
 * <p><i>Paper</i>: K. Deb, A. Pratap, S. Agarwal, and T. Meyarivan, 
 * “A fast and elitist multiobjective genetic algorithm: NSGA-II,” 
 * IEEE Transactions on Evolutionary Computation, vol. 6, no. 2, 
 * pp. 182–197, 2002.</p>
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
 * @see IConstrained
 * @see ConstrainedComparator
 * */

public class NSGA2ConstrainedComparator extends ConstrainedComparator {

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public NSGA2ConstrainedComparator(){
		super();
	}
	
	/**
	 * Parameterized constructor.
	 * @param fcomparator Fitness comparator
	 * */
	public NSGA2ConstrainedComparator(MOFitnessComparator fcomparator) {
		super(fcomparator);
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Compare two individuals. The following comparisons are made:
	 * <ul>
	 * 	<li>If one individual is feasible and the other is infeasible, 
	 * 	the first is preferred.</li>
	 * 	<li>If both individuals are infeasible, the one with the lowest 
	 * degree of constraint violation is preferred.</li>
	 * 	<li>If both individuals are feasible, compare them by fitness.</li>
	 * </ul>
	 * @param arg0 First individual
	 * @param arg1 Second individual
	 * @throws IllegalArgumentException
	 * */
	@Override
	public int compare(IIndividual arg0, IIndividual arg1) {

		// First, compare feasibility properties
		if(arg0 instanceof IConstrained && arg1 instanceof IConstrained){

			boolean feasible0 = ((IConstrained)arg0).isFeasible();
			boolean feasible1 = ((IConstrained)arg1).isFeasible();

			// Both individuals are feasible
			if(feasible0 && feasible1){
				return compareByFitness(arg0, arg1);
			}

			// Both individuals are infeasible, check the degree of infeasibility
			else if(!feasible0 && !feasible1){
				double degree0 = ((IConstrained)arg0).degreeOfInfeasibility();
				double degree1 = ((IConstrained)arg1).degreeOfInfeasibility();

				if(degree0 < degree1){
					return 1;
				}
				else if (degree0 > degree1){
					return -1;
				}
				else{
					return 0;
				}
			}

			// One individual is feasible and the other is infeasible 
			else{
				if(feasible0){
					return 1;
				}
				else{
					return -1;
				}
			}
		}
		else{
			throw new IllegalArgumentException("Individuals that implement IConstrained are expected");
		}
	}
}
