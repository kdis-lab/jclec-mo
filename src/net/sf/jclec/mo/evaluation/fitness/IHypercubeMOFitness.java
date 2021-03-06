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

package net.sf.jclec.mo.evaluation.fitness;

import net.sf.jclec.mo.strategy.util.Hypercube;

/**
 * This interface defines the methods that fitness object 
 * should implement to store information about the hypercube 
 * an individual belongs to. This type of information is used
 * by landscape partition algorithms.
 *  
 * <p>HISTORY:
 * <ul>
 *  	<li>(AR|JRR|SV, 1.0, March 2018)		First release.</li>
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
 * @see Hypercube
 */
public interface IHypercubeMOFitness {

	/**
	 * Get the hypercube coordinates.
	 * @return Hypercube coordinates
	 * */
	public Hypercube getHypercube();

	/**
	 * Set the hypercube coordinates.
	 * @param hypercube The new hypercube coordinates
	 * */
	public void setHypercube(Hypercube hypercube);
}
