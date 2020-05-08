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
import net.sf.jclec.mo.evaluation.fitness.MOFitness;

import org.junit.Test;

/**
 * Tests for the MOEA/D-ws strategy.
 * 
 * <p>HISTORY:
 * <ul>
 * 	<li>(AR|JRR|SV, 1.0, March 2018)		First release.</li>
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
 * @see MOEADws
 * */
public class MOEADwsTest extends MOStrategyTest {

	/* Properties */

	protected MOEADws moeadStrategy = new MOEADws();
	
	protected MOFitness fitness = new MOFitness();

	/* Tests */

	/**
	 * Test that the result returned by the initialization phase
	 * is not null. An archive with the non-dominated solutions
	 * should be created.
	 * */
	@Test
	public void testUseArchiveInitializeArchiveCreated(){
		try{
		// Create the initial population, it has four non-dominated solutions
		List<IIndividual> population = createPopulationInRange01(this.fitness);

		// Configure the strategy
		MOStrategyContext context = createContextPopulationInRange01();
		this.moeadStrategy.setContext(context);
		this.moeadStrategy.createSolutionComparator(createComparatorOfObjectives());
		this.moeadStrategy.setUseArchive(true);
		this.moeadStrategy.setWeights(createSetOfWeights());
		this.moeadStrategy.setNeighborhoodSize(2);
		this.moeadStrategy.setMaxNeighborsReplacement(1);

		// Execute the initialize method
		List<IIndividual> result = this.moeadStrategy.initialize(population);
		assertTrue(result.size() == 4);
		} catch (CloneNotSupportedException e) {
			fail();
		}
	}

	/**
	 * Test that the result returned by the initialization phase
	 * is null if the configuration states that the archive should
	 * not be used.
	 * */
	@Test
	public void testDontUseArchiveInitializeResultIsNull(){
		try{
		// Create the initial population, it has one non-dominated solution
		List<IIndividual> population = createPopulationInRange01(this.fitness);

		// Configure the strategy
		MOStrategyContext context = createContextPopulationInRange01();
		this.moeadStrategy.setContext(context);
		this.moeadStrategy.createSolutionComparator(createComparatorOfObjectives());
		this.moeadStrategy.setUseArchive(false);
		this.moeadStrategy.setWeights(createSetOfWeights());
		this.moeadStrategy.setNeighborhoodSize(2);
		this.moeadStrategy.setMaxNeighborsReplacement(1);

		// Execute the initialize method
		List<IIndividual> result = this.moeadStrategy.initialize(population);
		assertTrue(result == null);
		} catch (CloneNotSupportedException e) {
			fail();
		}
	}

	/**
	 * Test that the number of individuals selected
	 * as parents is equal to the population size.
	 * */
	@Test
	public void testMatingSelectionResultSize(){
		try{
		// Create the initial population, it has one non-dominated solution
		List<IIndividual> population = createPopulationInRange01(this.fitness);

		// Configure the strategy
		MOStrategyContext context = createContextPopulationInRange01();
		this.moeadStrategy.setContext(context);
		this.moeadStrategy.createSolutionComparator(createComparatorOfObjectives());
		this.moeadStrategy.setUseArchive(false);
		this.moeadStrategy.setWeights(createSetOfWeights());
		this.moeadStrategy.setNeighborhoodSize(2);
		this.moeadStrategy.setMaxNeighborsReplacement(1);

		// Initialize the strategy
		this.moeadStrategy.initialize(population);
		
		// Execute the mating selection
		int size = this.moeadStrategy.matingSelection(population, null).size();
		assert(size == (this.moeadStrategy.getContext().getPopulationSize()*2));
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
		// Create the initial population, it has one non-dominated solution
		List<IIndividual> population = createPopulationInRange01(this.fitness);

		// Configure the strategy
		MOStrategyContext context = createContextPopulationInRange01();
		this.moeadStrategy.setContext(context);
		this.moeadStrategy.createSolutionComparator(createComparatorOfObjectives());
		this.moeadStrategy.setFitnessFunctionComparator(this.moeadStrategy.createFitnessFunctionComparator());
		
		this.moeadStrategy.setUseArchive(false);
		this.moeadStrategy.setWeights(createSetOfWeights());
		this.moeadStrategy.setNeighborhoodSize(2);
		this.moeadStrategy.setMaxNeighborsReplacement(1);
		
		// Execute the initialization step to create the internal variables
		this.moeadStrategy.initialize(population);
				
		// Execute the environmental selection
		int size = this.moeadStrategy.environmentalSelection(population, population, null).size();
		assertTrue(size == this.moeadStrategy.getContext().getPopulationSize());
		} catch (CloneNotSupportedException e) {
			fail();
		}
	}

	/**
	 * Test that the result after updating is null when the
	 * configuration sets that the archive should not be used.
	 * */
	@Test
	public void testArchiveUpdateResultIsNull(){

		try{
		// Create the initial population, it has one non-dominated solution
		List<IIndividual> population = createPopulationInRange01(this.fitness);

		// Configure the strategy
		MOStrategyContext context = createContextPopulationInRange01();
		this.moeadStrategy.setContext(context);
		this.moeadStrategy.createSolutionComparator(createComparatorOfObjectives());
		this.moeadStrategy.setUseArchive(false);
		this.moeadStrategy.setWeights(createSetOfWeights());
		this.moeadStrategy.setNeighborhoodSize(2);
		this.moeadStrategy.setMaxNeighborsReplacement(1);

		// Execute the initialization step to create the internal variables
		this.moeadStrategy.initialize(population);

		List<IIndividual> result = this.moeadStrategy.updateArchive(population, population, null);
		assertTrue(result == null);
		} catch (CloneNotSupportedException e) {
			fail();
		}
	}

	/**
	 * Test that the result after updating is not null and
	 * the archive contains the non-dominated solutions.
	 * */
	@Test
	public void testArchiveUpdateResult(){
		try{
		// Create the initial population
		List<IIndividual> population = createPopulationInRange01(this.fitness);
		List<IIndividual> offspring = createOffspringInRange01(this.fitness);
		List<IIndividual> archive = createArchiveInRange01(this.fitness);
		
		// Configure the strategy
		MOStrategyContext context = createContextPopulationInRange01();
		this.moeadStrategy.setContext(context);
		this.moeadStrategy.createSolutionComparator(createComparatorOfObjectives());
		this.moeadStrategy.setUseArchive(true);
		this.moeadStrategy.setWeights(createSetOfWeights());
		this.moeadStrategy.setNeighborhoodSize(2);
		this.moeadStrategy.setMaxNeighborsReplacement(1);

		// Execute the initialization step to create the internal variables
		this.moeadStrategy.initialize(population);

		List<IIndividual> result = this.moeadStrategy.updateArchive(population, offspring, archive);
		assertTrue(result != null);
		assertTrue(result.size() == 3);	// non-dominated solutions within offspring (2) and current archive (1)
		} catch (CloneNotSupportedException e) {
			fail();
		}
	}

	/* Auxiliary methods */

	/**
	 * Create the set of weights that are
	 * used to evaluate the solutions.
	 * @return Set of weights vectors.
	 * */
	protected List<double []> createSetOfWeights(){
		List<double []> weights = new ArrayList<double []>();
		double [][] w = new double[][]{{0,0},{0,1},{1,0},{1,1}};
		weights.add(w[0]);
		weights.add(w[1]);
		weights.add(w[2]);
		weights.add(w[3]);
		return weights;
	}
}
