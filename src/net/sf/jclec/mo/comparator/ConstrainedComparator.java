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
import net.sf.jclec.mo.evaluation.fitness.MOFitness;

/**
 * Compare two solutions in a problem with constraints. By default, this
 * comparator establishes that two infeasible individuals are equals. If 
 * both individuals are feasible, then the fitness comparator is invoked.
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
 * @see MOSolutionComparator
 * @see IConstrained
 * */
public class ConstrainedComparator extends MOSolutionComparator {

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public ConstrainedComparator(){
		super();
	}
	
	/**
	 * Parameterized constructor.
	 * @param fcomparator Fitness comparator.
	 * */
	public ConstrainedComparator(MOFitnessComparator fcomparator) {
		super(fcomparator);
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Compare two individuals. By default, the following comparisons are made:
	 * <ul>
	 * 	<li>If one individual is feasible and the other is infeasible, the first is preferred.</li>
	 * 	<li>If both individuals are infeasible, they are equal.</li>
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

			// Both individuals are infeasible
			else if(!feasible0 && !feasible1){
				return 0;
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

	/**
	 * Compare two feasible individuals using the fitness comparator.
	 * @param arg0 First individual
	 * @param arg1 Second individual
	 * @return Comparison by fitness
	 * */
	protected int compareByFitness(IIndividual arg0, IIndividual arg1){
		MOFitness mofitness0, mofitness1;

		// Extract fitness from the first individual
		if(arg0.getFitness() instanceof MOFitness)
			mofitness0 = (MOFitness) arg0.getFitness();
		else
			throw new IllegalArgumentException("MOFitness expected as first argument");

		// Extract fitness from the second individual
		if(arg1.getFitness() instanceof MOFitness)
			mofitness1 = (MOFitness) arg1.getFitness();
		else
			throw new IllegalArgumentException("MOFitness expected as second argument");

		// Return fitness comparison
		return this.fcomparator.compare(mofitness0, mofitness1);
	}
}
