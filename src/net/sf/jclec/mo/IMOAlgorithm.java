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

import java.util.Comparator;
import java.util.List;

import net.sf.jclec.IFitness;
import net.sf.jclec.IIndividual;
import net.sf.jclec.mo.strategy.MOStrategy;
import net.sf.jclec.mo.strategy.MOStrategyContext;

/**
 * This interface specifies the required methods that any
 * multi-objective algorithm should implement.
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
public interface IMOAlgorithm {

	/**
	 * Get the execution time.
	 * @return The time that has elapsed between the beginning of the algorithm and the present.
	 * */
	public long executionTime();
	
	/**
	 * Get the current archive.
	 * @return Archive of solutions.
	 * */
	public List<IIndividual> getArchive();
	
	/**
	 * Get the non-dominated solutions in the current population.
	 * @return The set of non dominated solutions in the current population.
	 * */
	public List<IIndividual> getNonDominatedSolutions();
	
	/**
	 * Get the non-dominated solutions in the current archive.
	 * @return The set of non dominated solutions in the current archive.
	 * */
	public List<IIndividual> getNonDominatedSolutionsFromArchive();
	
	/**
	 * Get the multi-objective strategy.
	 * @return Current <code>MOStrategy</code>.
	 * */
	public MOStrategy getStrategy();
	
	/**
	 * Set the multi-objective strategy.
	 * @param strategy New evolutionary strategy.
	 * */
	public void setStrategy(MOStrategy strategy);
	
	/**
	 * Get the execution context.
	 * @return The execution context.
	 * */
	public MOStrategyContext getContext();
	
	/**
	 * Do the necessary steps to create the execution context 
	 * and set it in the strategy.
	 * */
	public void contextualizeStrategy();
	
	/**
	 * Create an array of <code>ValueFitnessComparator</code> for each objective in the problem.
	 * @return A set of component comparators correctly configured if 
	 * the evaluator implements <code>IMOEvaluator</code>, null otherwise
	 * */
	public Comparator<IFitness>[] createComponentsComparator();
}
