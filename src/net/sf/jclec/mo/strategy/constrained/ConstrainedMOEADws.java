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
import net.sf.jclec.IIndividual;
import net.sf.jclec.mo.IConstrained;
import net.sf.jclec.mo.command.NonDominatedFeasibleSolutionsExtractor;
import net.sf.jclec.mo.command.NonDominatedSolutionsExtractor;
import net.sf.jclec.mo.comparator.ConstrainedComparator;
import net.sf.jclec.mo.comparator.MOSolutionComparator;
import net.sf.jclec.mo.comparator.fcomparator.MOValueFitnessComparator;
import net.sf.jclec.mo.comparator.fcomparator.ParetoComparator;
import net.sf.jclec.mo.strategy.MOEADws;

/**
 * A MOEA/D strategy for problems with constraints using the <i>Weight Sum Approach</i>. 
 * It assigns the worst value to infeasible individuals and configures 
 * the individuals comparator to deal with infeasible individuals.
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
 * @see MOEADws
 * @see IConstrained
 * @see ConstrainedComparator
 * */
public class ConstrainedMOEADws extends MOEADws {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 532378070275013790L;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public ConstrainedMOEADws(){
		super();
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>It creates a comparator that takes into account feasibility of the solutions. 
	 * This comparator guarantees that infeasible individuals will not be included in the archive.</p>
	 * */
	@Override
	public void createSolutionComparator(Comparator<IFitness>[] components) {
		// Fitness comparator
		ParetoComparator fcomparator = new ParetoComparator(components);
		// Individuals comparator
		ConstrainedComparator comparator = new ConstrainedComparator(fcomparator);
		setSolutionComparator(comparator);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>If the solution is infeasible, the fitness values is set
	 * to negative infinite (maximization problems) or positive
	 * infinite (minimization problems).</p>
	 * */
	@Override
	protected double fitnessFunction(IIndividual individual, double [] lambda){
		if(((IConstrained)individual).isFeasible()){
			return super.fitnessFunction(individual, lambda);
		}
		else{
			if(isMaximized())
				return Double.NEGATIVE_INFINITY;
			else
				return Double.POSITIVE_INFINITY;
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>It creates a fitness comparator that takes into account feasibility.</p>
	 * */
	@Override
	protected MOSolutionComparator createFitnessFunctionComparator() {
		// Set the inverse flag according to the type of problem
		MOValueFitnessComparator fcomparator = new MOValueFitnessComparator(!isMaximized());
		ConstrainedComparator comparator = new ConstrainedComparator(fcomparator);
		return comparator;
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