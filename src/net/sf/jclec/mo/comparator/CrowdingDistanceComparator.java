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
import net.sf.jclec.mo.evaluation.fitness.ICrowdingDistanceMOFitness;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;

/**
 * Comparator for individuals regarding the crowding distance
 * value proposed in NSGA-II. It assumes that the fitness objects 
 * of the solutions to be compared implement the 
 * interface {@link ICrowdingDistanceMOFitness}.
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
 * @see MOSolutionComparator
 * @see ICrowdingDistanceMOFitness
 * */

public class CrowdingDistanceComparator extends MOSolutionComparator {

	/////////////////////////////////////////////////////////////////
	//-------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public CrowdingDistanceComparator() {
		super();
	}

	/////////////////////////////////////////////////////////////////
	//---------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Compare two individuals according to the crowding
	 * distance. Individuals should contain a fitness object
	 * that implements the <code>INSGA2MOFitness</code> interface.
	 * @param arg0 First individual
	 * @param arg1 Second individual
	 * @throws IllegalArgumentException
	 * */
	@Override
	public int compare(IIndividual arg0, IIndividual arg1) {
		
		ICrowdingDistanceMOFitness mofitness0, mofitness1;
		double crowding0, crowding1;

		// Extract fitness from the first individual
		if(arg0.getFitness() instanceof MOFitness)
			mofitness0 = (ICrowdingDistanceMOFitness) arg0.getFitness();
		else
			throw new IllegalArgumentException("The first individual should contain a fitness object implementing the INSGA2MOFitness interface");

		// Extract fitness from the second individual
		if(arg1.getFitness() instanceof MOFitness)
			mofitness1 = (ICrowdingDistanceMOFitness) arg1.getFitness();
		else
			throw new IllegalArgumentException("The second individual should contain a fitness object implementing the INSGA2MOFitness interface");

		// Compare the crowding distance values. Greater values are preferred.
		crowding0 = mofitness0.getCrowdingDistance();
		crowding1 = mofitness1.getCrowdingDistance();
		
		if(crowding0 > crowding1){
			return 1;
		}
		else if(crowding0 < crowding1){
			return -1;
		}
		else
			return 0;
	}
}
