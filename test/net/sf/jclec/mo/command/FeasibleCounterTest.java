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

import net.sf.jclec.IIndividual;

import static org.junit.Assert.assertTrue;

import java.util.List;

/**
 * Tests for the command that counts the number of feasible solutions
 * within a given population.
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
 * @see FeasibleCounter
 * */

public class FeasibleCounterTest extends CommandTest{

	/* Properties */
	
	protected FeasibleCounter command = new FeasibleCounter();
	
	/* Tests */
	
	/**
	 * Check that the result is zero when population is null.
	 * */
	@Test
	public void testResultPopulationIsNull(){
		this.command.setPopulation(null);
		this.command.execute();
		int result = this.command.getNumberFeasibleSolutions();
		assertTrue(result == 0);
	}
	
	/**
	 * Check that the result is equal to the number of infeasible solutions.
	 * */
	@Test
	public void testResultPopulationTwoInfeasibles(){
		List<IIndividual> population = createPopulationTwoInfeasibles();
		this.command.setPopulation(population);
		this.command.execute();
		int result = this.command.getNumberFeasibleSolutions();
		assertTrue(result == (population.size()-2));
	}
	
	/**
	 * Check that the result is equal to the population size if
	 * all population members are feasible solutions.
	 * */
	@Test
	public void testResultPopulationAllFeasibles(){
		List<IIndividual> population = createPopulationAllFeasibles();
		this.command.setPopulation(population);
		this.command.execute();
		int result = this.command.getNumberFeasibleSolutions();
		assertTrue(result == population.size());
	}
	
	/**
	 * Check that the result is equal to zero if
	 * all population members are infeasible solutions.
	 * */
	@Test
	public void testResultPopulationAllInfeasibles(){
		List<IIndividual> population = createPopulationAllInfeasibles();
		this.command.setPopulation(population);
		this.command.execute();
		int result = this.command.getNumberFeasibleSolutions();
		assertTrue(result == 0);
	}
}
