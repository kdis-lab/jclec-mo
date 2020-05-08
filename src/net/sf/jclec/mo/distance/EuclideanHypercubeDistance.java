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
import net.sf.jclec.IFitness;
import net.sf.jclec.IIndividual;
import net.sf.jclec.fitness.IValueFitness;
import net.sf.jclec.mo.evaluation.fitness.IHypercubeMOFitness;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;

/**
 * Euclidean distance between two individuals in a partitioned objective space. The distance is computed 
 * extracting the hypercube values from the fitness object of each individual. The fitness objects should
 * implement the  <code>IHypercubeMOFitness</code> interface.
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
 * @see IHypercubeMOFitness
 * */
public class EuclideanHypercubeDistance implements IDistance {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>Euclidean distance between two hypercubes.</p>
	 * */
	@Override
	public double distance(IIndividual one, IIndividual other) {

		double distance = -1.0, dif = 0.0, simpleValue0, simpleValue1;
		try{
			IFitness [] values0 = ((IHypercubeMOFitness)one.getFitness()).getHypercube().getValues();
			IFitness [] values1 = ((IHypercubeMOFitness)other.getFitness()).getHypercube().getValues();
			if(values0.length == values1.length){
				distance = 0.0;
				for(int i=0; i<values0.length; i++){
					simpleValue0 = ((IValueFitness)values0[i]).getValue();
					simpleValue1 = ((IValueFitness)values1[i]).getValue();
					dif = simpleValue0 - simpleValue1;
					distance += dif*dif;
				}
				distance = Math.sqrt(distance);
			}
		}catch (IllegalArgumentException e){
			e.printStackTrace();
		}
		return distance;
	}
}
