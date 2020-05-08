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

package net.sf.jclec.mo.command;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

import java.util.List;

import net.sf.jclec.IIndividual;
import net.sf.jclec.mo.comparator.fcomparator.ParetoComparator;

/**
 * Tests for the command that extracts non dominated solutions.
 * 
 * <p>HISTORY:
 * <ul>
 *  <li>(AR|JRR|SV, 1.0, March 2018)		First release.</li>
 * </ul>
 * </p>
 * 
 * @version 1.0
 * @author Aurora Ramirez (AR)
 * @author Jose Raul Romero (JRR)
 * @author Sebastian Ventura (SV)
 * 
 * <p>Knowledge Discovery and Intelligent Systems (KDIS) Research Group: 
 * {@link http://www.uco.es/grupos/kdis}</p>
 *  
 * @see NonDominatedSolutionsExtractor
 * */

public class NonDominatedSolutionsExtractorTest extends CommandTest {

	/* Properties */

	protected List<IIndividual> population = createPopulationThreeNonDominated();

	protected ParetoComparator comparator = createParetoComparator();

	protected NonDominatedSolutionsExtractor command = new NonDominatedSolutionsExtractor();
	
	/* Tests */

	/**
	 * Check that the size of the Pareto set is correct.
	 * */
	@Test
	public void testExtractParetoSetSizeIsCorrect(){
		
		// Execute the command
		this.command.setComparator(this.comparator);
		this.command.setPopulation(this.population);
		this.command.execute();
		
		// Get the result and check the size
		List<IIndividual> paretoSet = this.command.getNonDominatedSolutions();
		assertTrue(paretoSet.size() == 3);
	}

	/**
	 * Check that the Pareto set contain the non-dominated 
	 * solution of the population.
	 * */
	@Test
	public void testExtractParetoSetResultIsCorrect(){
		
		// Execute the command
		this.command.setComparator(this.comparator);
		this.command.setPopulation(this.population);
		this.command.execute();
		List<IIndividual> paretoSet = this.command.getNonDominatedSolutions();

		// Check that the non-dominated solutions are included in the resulting set
		assertTrue(paretoSet.contains(this.population.get(0)));
		assertTrue(paretoSet.contains(this.population.get(1)));
		assertTrue(paretoSet.contains(this.population.get(2)));
	}	
}
