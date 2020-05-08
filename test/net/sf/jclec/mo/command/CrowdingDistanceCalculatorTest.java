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

import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import net.sf.jclec.IIndividual;
import net.sf.jclec.mo.comparator.fcomparator.MOFitnessComparator;
import net.sf.jclec.mo.evaluation.fitness.NSGA2MOFitness;

/**
 * Tests for the command that calculates the crowding distance.
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
 * @see CrowdingDistanceCalculator
 * */

public class CrowdingDistanceCalculatorTest extends CommandTest {

	/* Properties */

	protected CrowdingDistanceCalculator command = new CrowdingDistanceCalculator();

	protected List<IIndividual> population = createPopulationInRange01();

	protected MOFitnessComparator comparator = createParetoComparator();
	
	/* Tests */

	/**
	 * Check that the crowding distance computation is correct.
	 * */
	@Test
	public void testCrowdingDistanceResult(){

		// Prepare the command
		this.command.setPopulation(this.population);
		this.command.setComparator(this.comparator);
		this.command.setMinValues(new double[]{0.0,0.0});
		this.command.setMaxValues(new double[]{1.0,1.0});
		
		// Execute the command
		this.command.execute();

		// The two points in the middle have the same distance to the bounds of the Pareto set
		double result1 = ((NSGA2MOFitness)this.population.get(1).getFitness()).getCrowdingDistance(); 
		double result2 = ((NSGA2MOFitness)this.population.get(3).getFitness()).getCrowdingDistance();
		assertTrue(result1 == result2);
		assertTrue(result1 == 1.0);
		
		// The two points in the bounds have the same crowding distance
		result1 = ((NSGA2MOFitness)this.population.get(0).getFitness()).getCrowdingDistance(); 
		result2 = ((NSGA2MOFitness)this.population.get(2).getFitness()).getCrowdingDistance();
		assertTrue(result1 == result2);
		assertTrue(result1 == Double.MAX_VALUE);
	}
}
