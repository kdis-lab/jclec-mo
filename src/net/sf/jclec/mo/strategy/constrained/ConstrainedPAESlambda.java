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

package net.sf.jclec.mo.strategy.constrained;

import java.util.Comparator;

import net.sf.jclec.IFitness;
import net.sf.jclec.mo.IConstrained;
import net.sf.jclec.mo.command.NonDominatedFeasibleSolutionsExtractor;
import net.sf.jclec.mo.command.NonDominatedSolutionsExtractor;
import net.sf.jclec.mo.comparator.ConstrainedComparator;
import net.sf.jclec.mo.comparator.MOSolutionComparator;
import net.sf.jclec.mo.comparator.fcomparator.ParetoComparator;
import net.sf.jclec.mo.strategy.PAESlambda;

/**
 * A (1+lambda)-PAES/(mu+lambda)-PAES strategy for problems with constraints. 
 * This class configures a constrained individual comparator that establishes 
 * the dominance criterion between feasible and infeasible individuals.
 * 
 * <p>HISTORY:
 * <ul>
 *  <li>(AR|JRR|SV, 1.0, March 2018)		First release.</li>
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
 * @see PAESlambda
 * @see ConstrainedComparator
 * @see IConstrained
 * */
public class ConstrainedPAESlambda extends PAESlambda{

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -2704672290666269653L;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public ConstrainedPAESlambda(){
		super();
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>This version will use a Pareto comparator for constrained problems.</p>
	 * */
	@Override
	public void createSolutionComparator(Comparator<IFitness>[] components) {
		// Fitness comparator
		ParetoComparator fcomparator = new ParetoComparator(components);
		// Individuals comparator
		MOSolutionComparator comparator = new ConstrainedComparator(fcomparator);
		setSolutionComparator(comparator);
	}
	
	/**
	 * Auxiliary method to create a command.
	 * @return Command to extract non-dominated solutions.
	 * */
	protected NonDominatedSolutionsExtractor createExtractor(){
		ParetoComparator fcomparator = (ParetoComparator)getSolutionComparator().getFitnessComparator();
		NonDominatedFeasibleSolutionsExtractor command = new NonDominatedFeasibleSolutionsExtractor();
		command.setComparator(fcomparator);
		return command;
	}
}