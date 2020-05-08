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

import java.util.ArrayList;
import java.util.List;

import net.sf.jclec.IIndividual;
import net.sf.jclec.mo.evaluation.fitness.HypercubeMOFitness;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;

import org.junit.Test;

/**
 * Tests for the e-MOEA strategy.
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
 * @see SSeMOEA
 * */

public class SSeMOEATest extends MOStrategyTest {

	/* Properties */

	protected SSeMOEA emoeaStrategy = new SSeMOEA();

	protected MOFitness fitness = new HypercubeMOFitness();

	/* Tests */

	/**
	 * Test that the result returned by the initialization phase
	 * is not null and the archive contains the non-dominated solutions.
	 * */
	@Test
	public void testInitializeArchiveCreated(){
		try{
			// Create the initial population
			List<IIndividual> population = createPopulation(this.fitness);

			// Configure the strategy
			MOStrategyContext context = createContext();
			this.emoeaStrategy.setContext(context);
			this.emoeaStrategy.createSolutionComparator(createComparatorOfObjectives());
			double [] epsilon = new double[]{0.05,0.05};
			this.emoeaStrategy.setEpsilonValues(epsilon);

			// Execute the initialize method, the archive should contain the non-dominated solution
			List<IIndividual> result = this.emoeaStrategy.initialize(population);
			assertTrue(result.size() == 1);
		} catch (CloneNotSupportedException e) {
			fail();
		}
	}

	/**
	 * Test that the number of individuals selected
	 * as parents is equal to 2. In addition, check
	 * that one individual belongs to the current population
	 * and the other to the archive.
	 * */
	@Test
	public void testMatingSelectionResult(){
		try{
			// Create the initial population
			List<IIndividual> population = createPopulation(this.fitness);

			// Configure the strategy
			MOStrategyContext context = createContext();
			this.emoeaStrategy.setContext(context);
			this.emoeaStrategy.createSolutionComparator(createComparatorOfObjectives());
			double [] epsilon = new double[]{0.05,0.05};
			this.emoeaStrategy.setEpsilonValues(epsilon);

			// Execute the initialization method
			List<IIndividual> archive = this.emoeaStrategy.initialize(population);

			// Execute the mating selection, one parent should belong to the current population
			// and the other should belong to the archive
			List<IIndividual> parents = this.emoeaStrategy.matingSelection(population, archive);
			assertTrue(parents.size() == 2);
			assertTrue((population.contains(parents.get(0)) && archive.contains(parents.get(1))) ||
					(population.contains(parents.get(1)) && archive.contains(parents.get(0))));

			// If the archive is empty, both individuals will belong to the current population
			parents = this.emoeaStrategy.matingSelection(population, new ArrayList<IIndividual>());
			assertTrue(parents.size() == 2);
			assertTrue(population.contains(parents.get(0)));
			assertTrue(population.contains(parents.get(1)));
		} catch (CloneNotSupportedException e) {
			fail();
		}
	}

	/**
	 * Test that the number of individuals that will take
	 * part in the next generation is equal to the population size.
	 * */
	@Test
	public void testEnvironmentalSelectionResultSize(){
		try{
			// Create the populations
			List<IIndividual> population = createPopulation(this.fitness);
			List<IIndividual> offspring = createOffspring(this.fitness);

			// Configure the strategy
			MOStrategyContext context = createContext();
			this.emoeaStrategy.setContext(context);
			this.emoeaStrategy.createSolutionComparator(createComparatorOfObjectives());
			double [] epsilon = new double[]{0.05,0.05};
			this.emoeaStrategy.setEpsilonValues(epsilon);

			// Execute the initialization method
			List<IIndividual> archive = this.emoeaStrategy.initialize(population);

			// Execute the environmental selection
			int size = this.emoeaStrategy.environmentalSelection(population, offspring, archive).size();
			assertTrue(size == this.emoeaStrategy.getContext().getPopulationSize());
		} catch (CloneNotSupportedException e) {
			fail();
		}
	}

	/**
	 * Test that the result after updating the archive is not null.
	 * */
	@Test
	public void testArchiveUpdateResultIsNotNull(){
		try{
			// Create the populations
			List<IIndividual> population = createPopulation(this.fitness);
			List<IIndividual> offspring = createOffspring(this.fitness);

			// Configure the strategy
			MOStrategyContext context = createContext();
			this.emoeaStrategy.setContext(context);
			this.emoeaStrategy.createSolutionComparator(createComparatorOfObjectives());
			double [] epsilon = new double[]{0.05,0.05};
			this.emoeaStrategy.setEpsilonValues(epsilon);

			// Execute the initialization method
			List<IIndividual> archive = this.emoeaStrategy.initialize(population);
			
			// Check result
			List<IIndividual> result = this.emoeaStrategy.updateArchive(population, offspring, archive);
			assertTrue(result != null);
		} catch (CloneNotSupportedException e) {
			fail();
		}
	}
}
