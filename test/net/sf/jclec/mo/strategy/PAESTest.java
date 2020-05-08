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
import net.sf.jclec.mo.evaluation.MOEvaluator;
import net.sf.jclec.mo.evaluation.Objective;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;
import net.sf.jclec.mo.evaluation.fitness.PAESMOFitness;
import net.sf.jclec.util.random.IRandGen;
import net.sf.jclec.util.random.RanecuFactory;

import org.junit.Test;

/**
 * Tests for the PAES strategy.
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
 * @see PAES
 * */
public class PAESTest extends MOStrategyTest {

	/* Properties */

	protected PAES paesStrategy = new PAES();

	/* Tests */

	/**
	 * Test that the initial archive is correctly created.
	 * */
	@Test
	public void testInitializeArchiveCreated(){

		// Create the population. It only contains one individual
		List<IIndividual> population = createParent();

		// Configure the strategy
		MOStrategyContext context = createContext();
		this.paesStrategy.setContext(context);
		this.paesStrategy.createSolutionComparator(createComparatorOfObjectives());
		this.paesStrategy.setNumberOfBisections(10);
		
		// Execute the initialize method
		List<IIndividual> result = this.paesStrategy.initialize(population);
		// The archive should contain only one solution (the initial solution)
		assertTrue(result.size() == 1);
		// The archive member is the current solution
		assertTrue(result.get(0).equals(population.get(0))); 
	}

	/**
	 * Test that the parent is the current solution.
	 * */
	@Test
	public void testMatingSelectionParentCreated(){

		// Create the population, the archive should not be used
		List<IIndividual> population = createParent();

		// Configure the strategy
		MOStrategyContext context = createContext();
		this.paesStrategy.setContext(context);
		this.paesStrategy.createSolutionComparator(createComparatorOfObjectives());
		this.paesStrategy.setNumberOfBisections(10);
		
		// Execute the mating selection
		List<IIndividual> parents = this.paesStrategy.matingSelection(population, null);

		// Check that one individual is returned
		assert(parents.size() == 1);

		// Check that the parent is the current solution
		assertTrue(parents.get(0).equals(population.get(0)));
	}

	/**
	 * Test the environmental selection method. If the parent
	 * dominates the offspring, the parent will survive. If the
	 * offspring dominates the parent, the offspring will survive.
	 * */
	@Test
	public void testEnvironmentalSelectionDominantSolutionSurvives(){

		// Create the parent and the offspring
		List<IIndividual> solution1 = createParent();
		List<IIndividual> solution2 = createOffspring();

		// Configure the strategy
		MOStrategyContext context = createContextPopulationInRange01();
		this.paesStrategy.setContext(context);
		this.paesStrategy.createSolutionComparator(createComparatorOfObjectives());
		this.paesStrategy.setNumberOfBisections(10);
		
		// Execute the environmental selection: offspring dominates parent
		List<IIndividual> result = this.paesStrategy.environmentalSelection(solution1, solution2, null);
		assertTrue(result.size() == 1);
		assertTrue(result.get(0).equals(solution2.get(0)));

		// Change the role of parent and offspring: parent dominates offspring
		result = this.paesStrategy.environmentalSelection(solution2, solution1, null);
		assertTrue(result.size() == 1);
		assertTrue(result.get(0).equals(solution2.get(0)));
	}

	/**
	 * Test the environmental selection method considering the archive. 
	 * If the parent and the offspring are not dominated, the offspring
	 * survives according to the current state of the archive.
	 * */
	@Test
	public void testEnvironmentalSelectionUsingArchive(){

		// Create the parent, the offspring and the archive
		List<IIndividual> archive = new ArrayList<IIndividual>();
		List<IIndividual> parent = createParent();
		List<IIndividual> offspring = createOffspring(); 
		
		// Change the objective values to be non-dominated by the parent
		((MOFitness)offspring.get(0).getFitness()).setObjectiveDoubleValue(2.0, 0);
		((MOFitness)offspring.get(0).getFitness()).setObjectiveDoubleValue(3.0, 1);
		
		// Configure the strategy
		MOStrategyContext context = createContextPopulationInRange01();
		this.paesStrategy.setContext(context);
		this.paesStrategy.createSolutionComparator(createComparatorOfObjectives());
		this.paesStrategy.setNumberOfBisections(10);
		this.paesStrategy.initialize(parent);
		
		// Execute the environmental selection: parent and offspring are non-dominated
		// If the archive is empty, the parent and the offspring are compared according
		// to the crowding distance. If the offspring belong to a new location, it will survive
		List<IIndividual> result = this.paesStrategy.environmentalSelection(parent, offspring, archive);
		assertTrue(result.size() == 1);
		assertTrue(result.get(0).equals(offspring.get(0)));
	
		// If the mutant is dominated by an archive member, the parent will survive.
		archive = createArchive();
		result = this.paesStrategy.environmentalSelection(parent, offspring, archive);
		assertTrue(result.size() == 1);
		assertTrue(result.get(0).equals(parent.get(0)));
	}

	/**
	 * Test that the archive size is not exceeded.
	 * */
	@Test
	public void testUpdateMaxArchiveSize(){

		// Create the populations
		List<IIndividual> parent = createParent();
		List<IIndividual> archive = createArchive();
		List<IIndividual> offspring = createOffspring();

		// Configure the strategy
		int archiveSize = 1;
		this.paesStrategy.setArchiveSize(archiveSize);
		MOStrategyContext context = createContext();
		this.paesStrategy.setContext(context);
		this.paesStrategy.createSolutionComparator(createComparatorOfObjectives());
		this.paesStrategy.initialize(parent);
		
		// Execute the update method
		List<IIndividual> result = this.paesStrategy.updateArchive(parent, offspring, archive);
		assertTrue(result.size() == archiveSize);
	}

	/* Auxiliary methods */

	/**
	 * Create the initial solution.
	 * @return List containing the initial solution.
	 * */
	protected List<IIndividual> createParent(){
		List<IIndividual> population = new ArrayList<IIndividual>();

		// Create only one individual
		SimpleValueFitness [] fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(3.0);
		fitness[1] = new SimpleValueFitness(2.0);
		BinArrayIndividual ind = new BinArrayIndividual(new byte[]{0,0,0}, new PAESMOFitness(new MOFitness(fitness),0,1));
		population.add(ind.copy());	
		return population;
	}

	/**
	 * Create the offspring.
	 * @return List containing the offspring.
	 * */
	protected List<IIndividual> createOffspring(){
		List<IIndividual> offspring = new ArrayList<IIndividual>();

		// Create only one individual
		SimpleValueFitness [] fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(4.0);
		fitness[1] = new SimpleValueFitness(4.0);
		BinArrayIndividual ind = new BinArrayIndividual(new byte[]{0,1,0}, new PAESMOFitness(new MOFitness(fitness),0,1));
		offspring.add(ind.copy());	
		return offspring;
	}
	
	/**
	 * Create an archive with one member.
	 * @return List containing the archive.
	 * */
	protected List<IIndividual> createArchive(){
		List<IIndividual> archive = new ArrayList<IIndividual>();

		// Create only one individual
		SimpleValueFitness [] fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(5.0);
		fitness[1] = new SimpleValueFitness(5.0);
		BinArrayIndividual ind = new BinArrayIndividual(new byte[]{1,1,0}, new PAESMOFitness(new MOFitness(fitness),0,1));
		archive.add(ind.copy());	
		return archive;
	}

	/**
	 * Create the evolution context. As PAES is an
	 * Evolutionary Strategy, this method
	 * assumes that the population size is 1.
	 * @return Execution context.
	 * */
	protected MOStrategyContext createContext(){

		// Evaluator
		List<Objective> objectives = new ArrayList<Objective>(2); // dummy objective functions
		objectives.add(new PercentageOfOnes());
		objectives.get(0).setIndex(0);
		objectives.add(new PercentageOfZeros());
		objectives.get(1).setIndex(1);
		MOEvaluator evaluator = new MOEvaluator(objectives);

		// Random number generator
		RanecuFactory factory = new RanecuFactory();
		IRandGen randgen = factory.createRandGen();

		// Evolution context
		MOStrategyContext context = new MOStrategyContext(randgen, null, evaluator, null, 1); // only one individual
		return context;
	}
}
