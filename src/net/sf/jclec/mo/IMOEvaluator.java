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

package net.sf.jclec.mo;

import java.util.List;

import net.sf.jclec.IConfigure;
import net.sf.jclec.mo.comparator.fcomparator.MOFitnessComparator;
import net.sf.jclec.mo.evaluation.Objective;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;

/**
 * This interface provides the required methods that any class 
 * that evaluates a multi-objective problem should implement.
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
 * */
public interface IMOEvaluator extends IConfigure{

	/**
	 * Get the list of objective functions.
	 * @return A list containing the objective functions.
	 * */
	public List<Objective> getObjectives();
	
	/**
	 * Get the number of objectives.
	 * @return The number of objectives.
	 * */
	public int numberOfObjectives();
	
	/**
	 * Set the fitness comparator.
	 * @param comparator Comparator that has to be set.
	 * */
	public void setComparator(MOFitnessComparator comparator);
	
	/**
	 * Set the fitness object to be used in the experiment.
	 * @param fitnessClass The fitness object.
	 * */
	public void setFitnessPrototype(MOFitness fitness);
}
