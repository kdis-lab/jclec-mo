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

import java.util.List;

import net.sf.jclec.IIndividual;

/**
 * This interface defines the methods that the fitness of a solution should
 * provide in order to use some of the procedures defined by NSGA-II algorithm.
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
 * @see NSGA2
 */

public interface INSGA2MOFitness extends ICrowdingDistanceMOFitness{

	/**
	 * Get the value of the front.
	 * @return Front of the individual
	 */
	public int getFront();
	
	/**
	 * Set the value of the front.
	 * @param front The value that has to be set.
	 */
	public void setFront(int front);

	/**
	 * Get the number of individuals that dominate this individual.
	 * @return Number of individuals that dominate it.
	 */
	public int getDominatedBy();

	/**
	 * Set the number of individual that dominate this individual.
	 * @param dominatedBy The value that has to be set.
	 */
	public void setDominatedBy(int dominatedBy);

	/**
	 * Increment the number of individuals that dominate the individual.
	 * */
	public void incrementDominatedBy();

	/**
	 * Decrement the number of individuals that dominate the individual.
	 * */
	public void decrementDominatedBy();

	/**
	 * Get the list of dominated individuals.
	 * @return List containing the individuals that are dominated 
	 * by this individual.
	 */
	public List<IIndividual> getDominatedList();

	/**
	 * Set the list of individuals that are dominated by the individual.
	 * @param dominatedList The list that has to be set.
	 */
	public void setDominatedList(List<IIndividual> dominatedList);	
}
