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
import net.sf.jclec.mo.comparator.fcomparator.ParetoComparator;
import net.sf.jclec.mo.strategy.SSeMOEA;

/**
 * An eMOEA strategy for problems with constraints. This version 
 * of SSeMOEA updates the archive only considering those feasible 
 * individuals that are non dominated.
 *  
 * <p>HISTORY:
 * <ul>
 *  <li>(AR|JRR|SV, 1.0, March 2018)		First release.</li>
 * </ul>
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
 * @see SSeMOEA
 * @see IConstrained
 * */
public class ConstrainedSSeMOEA extends SSeMOEA {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -9208720289284668326L;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public ConstrainedSSeMOEA(){
		super();
	}
	
	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>This version of eMOEA requires the configuration of
	 * a comparator for constrained problems.</p>
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
	 * <p>This version of eMOEA creates an extractor command
	 * that discard infeasible solutions before considering
	 * the e-dominance between solutions.</p>
	 * */
	@Override
	protected NonDominatedSolutionsExtractor createExtractor(){
		NonDominatedFeasibleSolutionsExtractor extractor = new NonDominatedFeasibleSolutionsExtractor();
		extractor.setComparator(this.hComparator);
		return extractor;
	}
}