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

package net.sf.jclec.mo.distance;

import net.sf.jclec.IDistance;
import net.sf.jclec.IIndividual;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;

/**
 * Manhattan distance between two individuals in the objective space. The distance is computed 
 * extracting the objective values from the fitness object of each individual. <code>MOFitness</code>
 * objects are expected.
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
 * @see IDistance
 * @see MOFitness
 * */
public class ManhattanDistance implements IDistance {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>Manhattan distance between two individuals. It requires
	 * that the individuals contain a <code>MOFitness</code> object.
	 * */
	@Override
	public double distance(IIndividual one, IIndividual other) {
		double distance = 0.0;

		try {
			MOFitness f0 = (MOFitness) one.getFitness();
			MOFitness f1 = (MOFitness) other.getFitness();
			int nObjs = f0.getNumberOfObjectives();

			//Calculate the distance of each one of the fitness components
			for(int i=0; i<nObjs; i++){
				distance += Math.abs(f0.getObjectiveDoubleValue(i) - f1.getObjectiveDoubleValue(i));		
			}
		} catch (IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
			distance = -1.0;
		}
		return distance;
	}
}
