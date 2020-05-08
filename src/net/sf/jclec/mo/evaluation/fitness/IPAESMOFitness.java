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

/**
 * This interface defines the methods that the fitness of a solution should 
 * provide in order to use some of the procedures defined by PAES algorithm.
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
 * @see PAES
 * */
public interface IPAESMOFitness {

	/**
	 * Get the location of each solution
	 * regarding the archive.
	 * @return The location.
	 * */
	public int getLocation();

	/**
	 * Set the location of the solution
	 * regarding the archive.
	 * @param location The location that has to be set.
	 * */
	public void setLocation(int location);
	
	/**
	 * Get the dominance score
	 * @return The dominance score.
	 * */
	public int getDominanceScore();

	/**
	 * Set the dominance score.
	 * @param dominance The dominance score that has to be set.
	 * */
	public void setDominanceScore(int dominance);
}
