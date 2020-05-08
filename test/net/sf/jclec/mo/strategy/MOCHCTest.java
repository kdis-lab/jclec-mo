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

package net.sf.jclec.mo.strategy;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import net.sf.jclec.IDistance;
import net.sf.jclec.IIndividual;
import net.sf.jclec.binarray.HammingDistance;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;
import net.sf.jclec.mo.evaluation.fitness.NSGA2MOFitness;

import org.junit.Test;

/**
 * Tests for the MOCHC strategy.
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
 * @see MOCHC
 * */

public class MOCHCTest extends MOStrategyTest {

	/* Properties */

	protected MOCHC mochcStrategy = new MOCHC();
	
	protected MOFitness fitness = new NSGA2MOFitness();

	/* Tests */

	/**
	 * Test that the result returned by the initialization phase
	 * is null (the archive is not created).
	 * */
	@Test
	public void testInitializeResultIsNull(){
		try{
			// Configure the strategy
			List<IIndividual> population = createPopulation(fitness);
			MOStrategyContext context = createContext();
			mochcStrategy.setContext(context);
			mochcStrategy.createSolutionComparator(createComparatorOfObjectives());

			// Execute the initialize method
			List<IIndividual> result = mochcStrategy.initialize(population);
			assertTrue(result == null);
		} catch (CloneNotSupportedException e) {
			fail();
		}
	}

	/**
	 * Test that the number of individuals selected
	 * as parents is smaller or equal to the population size.
	 * */
	@Test
	public void testMatingSelectionResultSize(){
		try{
			// Create the population and configure the strategy
			List<IIndividual> population = createPopulation(fitness);
			MOStrategyContext context = createContext();
			IDistance distance = new HammingDistance();
			mochcStrategy.setContext(context);
			mochcStrategy.setDistance(distance);
			mochcStrategy.setCurrentD(1);
			mochcStrategy.createSolutionComparator(createComparatorOfObjectives());
			mochcStrategy.initialize(population);

			// Execute the mating selection
			int size = mochcStrategy.matingSelection(population, null).size();
			assertTrue(size <= context.getPopulationSize());
		} catch (CloneNotSupportedException e) {
			fail();
		}
	}

	/**
	 * Test that the result after updating the archive is null.
	 * */
	@Test
	public void testArchiveUpdateResultIsNull(){
		List<IIndividual> result = mochcStrategy.updateArchive(null, null, null);
		assertTrue(result == null);
	}
}
