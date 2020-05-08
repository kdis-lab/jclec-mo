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
 * provide in order to use some of the procedures defined by NSGA-III algorithm.
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
 * @see NSGA3
 */

public interface INSGA3MOFitness {

	/**
	 * Get the normalized objective values.
	 * @return normalized objective values
	 * */
	public double [] getNormalisedObjectiveValues();

	/**
	 * Set the normalized objective values.
	 * @param values The values that have to be set.
	 * */
	public void setNormalisedObjectiveValues(double [] values);

	/**
	 * Get the normalized objective value at the 
	 * specified position.
	 * @param index The objective position index.
	 * @return normalized objective value at position <code>index</code>.
	 * */
	public double getNormalisedObjectiveValue(int index);

	/**
	 * Set the normalized objective value at the
	 * specified position.
	 * @param value The value that has to be set.
	 * @param index The objective position index.
	 * */
	public void setNormalisedObjectiveValue(double value, int index);

	/**
	 * Get the associated reference point.
	 * @return Associated reference point.
	 * */
	public int getAssociatedRefPoint();

	/**
	 * Set the associated reference point.
	 * @param point The value that has to be set.
	 * */
	public void setAssociatedRefPoint(int point);

	/**
	 * Get the distance to the closest reference point.
	 * @return Distance to the closest reference point.
	 * */
	public double getDistance();

	/**
	 * Set the distance to the closest reference point.
	 * @param distance The value that has to be set.
	 * */
	public void setDistance(double distance);
}
