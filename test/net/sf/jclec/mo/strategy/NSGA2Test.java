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

import java.util.ArrayList;
import java.util.List;

import net.sf.jclec.IIndividual;
import net.sf.jclec.binarray.BinArrayIndividual;
import net.sf.jclec.fitness.SimpleValueFitness;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;
import net.sf.jclec.mo.evaluation.fitness.NSGA2MOFitness;

import org.junit.Test;

/**
 * Tests for NSGA-II strategy.
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
 * @see NSGA2
 * */
public class NSGA2Test extends MOStrategyTest{

	/* Properties */
	
	protected NSGA2 nsga2Strategy = new NSGA2();
		
	/* Tests */
	
	/**
	 * Test that the result returned by the initialization phase
	 * is null (an archive is not created).
	 * */
	@Test
	public void testInitializeResultIsNull(){
		MOStrategyContext context = createContextPopulationInRange01();
		this.nsga2Strategy.setContext(context);
		this.nsga2Strategy.createSolutionComparator(createComparatorOfObjectives());
		List<IIndividual> result = this.nsga2Strategy.initialize(null);
		assertTrue(result == null);
	}
	
	/**
	 * Test that the number of individuals selected
	 * as parents is equal to the population size.
	 * */
	@Test
	public void testMatingSelectionResultSize(){
		List<IIndividual> population = createPopulationInRange01();
		MOStrategyContext context = createContextPopulationInRange01();
		this.nsga2Strategy.setContext(context);
		this.nsga2Strategy.createSolutionComparator(createComparatorOfObjectives());
		
		// Assuming the first generation
		this.nsga2Strategy.initialize(population);
		this.nsga2Strategy.getContext().setGeneration(1);
		int size = this.nsga2Strategy.matingSelection(population, null).size();
		assert(size == this.nsga2Strategy.getContext().getPopulationSize());
		
		// Assuming the i-th generation
		this.nsga2Strategy.getContext().setGeneration(50);
		// Assign NSGA-II properties
		this.nsga2Strategy.fastNonDominatedSorting(population);
		this.nsga2Strategy.crowdingDistanceAssignment(population);
		size = this.nsga2Strategy.matingSelection(population, null).size();
		assertTrue(size == this.nsga2Strategy.getContext().getPopulationSize());
	}
	
	/**
	 * Test that the number of individuals that will take
	 * part in the next generation is equal to the population size.
	 * */
	@Test
	public void testEnvironmentalSelectionResultSize(){
		List<IIndividual> population = createPopulationInRange01();
		MOStrategyContext context = createContextPopulationInRange01();
		this.nsga2Strategy.setContext(context);
		this.nsga2Strategy.createSolutionComparator(createComparatorOfObjectives());
		this.nsga2Strategy.initialize(population);
		int size = this.nsga2Strategy.environmentalSelection(population, population, null).size();
		assertTrue(size == this.nsga2Strategy.getContext().getPopulationSize());
	}
	
	/**
	 * Test that the result after updating is null (there is not an archive).
	 * */
	@Test
	public void testArchiveUpdateResultIsNull(){
		List<IIndividual> result = this.nsga2Strategy.updateArchive(null, null, null);
		assertTrue(result == null);
	}
	
	/**
	 * Test that the non-dominated sorting method correctly
	 * splits a population into fronts.
	 * */
	@Test
	public void testNonDominatedSorting() {
		List<IIndividual> population = createParent();
		this.nsga2Strategy.createSolutionComparator(createComparatorOfObjectives());
		List<List<IIndividual>> fronts = this.nsga2Strategy.fastNonDominatedSorting(population);
		
		assertTrue(fronts.size() == 4);
		assertTrue(fronts.get(0).size() == 1);
		assertTrue(fronts.get(1).size() == 2);
		assertTrue(fronts.get(2).size() == 2);
		assertTrue(fronts.get(3).size() == 1);
	}
	
	/**
	 * Test crowding distance computation.
	 * */
	@Test
	public void testCrowdingDistance() {
		List<IIndividual> population = createPopulationInRange01();
		MOStrategyContext context = createContextPopulationInRange01();
		this.nsga2Strategy.setContext(context);
		this.nsga2Strategy.createSolutionComparator(createComparatorOfObjectives());
		this.nsga2Strategy.initialize(population);
		this.nsga2Strategy.crowdingDistanceAssignment(population);
		// The two point in the middle have the same distance to the bounds of the Pareto set
		double result1 = ((NSGA2MOFitness)population.get(1).getFitness()).getCrowdingDistance(); 
		double result2 = ((NSGA2MOFitness)population.get(3).getFitness()).getCrowdingDistance();
		assertTrue(result1 == result2);
		assertTrue(result1 == 1.0);
	}
	
	/* Auxiliary methods */
	
	/**
	 * Create a population of individuals.
	 * @return List containing the individuals.
	 * */
	protected List<IIndividual> createParent(){
		List<IIndividual> population = new ArrayList<IIndividual>();
		
		// Create some individuals, assuming the objective values
		SimpleValueFitness [] fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(5.0);
		fitness[1] = new SimpleValueFitness(5.0);
		BinArrayIndividual ind = new BinArrayIndividual(new byte[]{0,0,0}, new NSGA2MOFitness(new MOFitness(fitness)));
		population.add(ind.copy());
		
		fitness[0] = new SimpleValueFitness(1.0);
		fitness[1] = new SimpleValueFitness(1.0);
		ind = new BinArrayIndividual(new byte[]{0,0,1}, new NSGA2MOFitness(new MOFitness(fitness)));
		population.add(ind.copy());
		
		fitness[0] = new SimpleValueFitness(3.0);
		fitness[1] = new SimpleValueFitness(4.0);
		ind = new BinArrayIndividual(new byte[]{0,1,0}, new NSGA2MOFitness(new MOFitness(fitness)));
		population.add(ind.copy());
		
		fitness[0] = new SimpleValueFitness(2.0);
		fitness[1] = new SimpleValueFitness(1.0);
		ind = new BinArrayIndividual(new byte[]{0,1,1}, new NSGA2MOFitness(new MOFitness(fitness)));
		population.add(ind.copy());
		
		fitness[0] = new SimpleValueFitness(1.0);
		fitness[1] = new SimpleValueFitness(2.0);
		ind = new BinArrayIndividual(new byte[]{1,0,0},new NSGA2MOFitness(new MOFitness(fitness)));
		population.add(ind.copy());
		
		fitness[0] = new SimpleValueFitness(4.0);
		fitness[1] = new SimpleValueFitness(3.0);
		ind = new BinArrayIndividual(new byte[]{1,0,1}, new NSGA2MOFitness(new MOFitness(fitness)));
		population.add(ind.copy());
		
		return population;
	}
	
	/**
	 * Create a population of individuals with
	 * objective values in the range [0,1].
	 * @return List containing the individuals.
	 * */
	protected List<IIndividual> createPopulationInRange01(){
		List<IIndividual> population = new ArrayList<IIndividual>();
		
		// Create some individuals, assuming the objective values
		SimpleValueFitness [] fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(0.0);
		fitness[1] = new SimpleValueFitness(1.0);
		BinArrayIndividual ind = new BinArrayIndividual(new byte[]{0,0}, new NSGA2MOFitness(new MOFitness(fitness)));
		population.add(ind.copy());
		
		fitness[0] = new SimpleValueFitness(0.5);
		fitness[1] = new SimpleValueFitness(0.5);
		ind = new BinArrayIndividual(new byte[]{0,1}, new NSGA2MOFitness(new MOFitness(fitness)));
		population.add(ind.copy());
		
		fitness[0] = new SimpleValueFitness(1.0);
		fitness[1] = new SimpleValueFitness(0.0);
		ind = new BinArrayIndividual(new byte[]{1,1}, new NSGA2MOFitness(new MOFitness(fitness)));
		population.add(ind.copy());
		
		fitness[0] = new SimpleValueFitness(0.5);
		fitness[1] = new SimpleValueFitness(0.5);
		ind = new BinArrayIndividual(new byte[]{1,0}, new NSGA2MOFitness(new MOFitness(fitness)));
		population.add(ind.copy());
		
		return population;
	}
}
