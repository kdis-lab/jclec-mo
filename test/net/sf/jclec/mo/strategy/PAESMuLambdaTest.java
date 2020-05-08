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
 * Tests for the (mu+lambda)-PAES strategy.
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
 * @see PAESlambda
 * */

public class PAESMuLambdaTest extends MOStrategyTest {

	/* Properties */

	protected PAESlambda paesStrategy = new PAESlambda();

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
		((MOEvaluator)this.paesStrategy.getContext().getEvaluator()).setComparator(this.paesStrategy.getSolutionComparator().getFitnessComparator());
		this.paesStrategy.setNumberOfBisections(10);
		int mu=2;
		this.paesStrategy.setMu(mu);
		int lambda=2;
		this.paesStrategy.setLambda(lambda);
		int maxArchiveSize = 1;
		this.paesStrategy.setArchiveSize(maxArchiveSize);
		
		// Execute the initialize method
		List<IIndividual> result = this.paesStrategy.initialize(population);
		// The archive should contain only one solution
		assertTrue(result.size() <= maxArchiveSize);
		// The archive member is the non-dominated solution within the current population
		assertTrue(result.get(0).equals(population.get(0))); 
	}

	/**
	 * Test that the set of parents is the correctly created.
	 * The size of the set should be equal to the configured
	 * <code>lambda</code> value.
	 * */
	@Test
	public void testMatingSelectionParentsCreated(){

		// Create the population
		List<IIndividual> population = createParent();

		// Configure the strategy
		MOStrategyContext context = createContext();
		this.paesStrategy.setContext(context);
		this.paesStrategy.createSolutionComparator(createComparatorOfObjectives());
		((MOEvaluator)this.paesStrategy.getContext().getEvaluator()).setComparator(this.paesStrategy.getSolutionComparator().getFitnessComparator());
		this.paesStrategy.setNumberOfBisections(10);
		int mu = 2;
		this.paesStrategy.setMu(mu);
		int lambda = 2;
		this.paesStrategy.setLambda(lambda);
		int maxArchiveSize = 1;
		this.paesStrategy.setArchiveSize(maxArchiveSize);
		
		// Initialize the strategy
		List<IIndividual> archive = this.paesStrategy.initialize(population);
		
		// Execute the mating selection
		List<IIndividual> parents = this.paesStrategy.matingSelection(population, archive);

		// Check that two individuals are returned
		assertTrue(parents.size() == lambda);
	}

	/**
	 * Test that the environmental selection method returns
	 * the expected number of solutions (<code>mu</code>). 
	 * */
	@Test
	public void testEnvironmentalSelectionResultSize(){

		// Create the parent and the offspring (two individuals)
		List<IIndividual> population = createParent();
		List<IIndividual> offspring = createOffspring();

		// Configure the strategy
		MOStrategyContext context = createContextPopulationInRange01();
		this.paesStrategy.setContext(context);
		this.paesStrategy.createSolutionComparator(createComparatorOfObjectives());
		this.paesStrategy.setNumberOfBisections(10);
		((MOEvaluator)this.paesStrategy.getContext().getEvaluator()).setComparator(this.paesStrategy.getSolutionComparator().getFitnessComparator());
		int mu = 2;
		this.paesStrategy.setMu(mu);
		int lambda = 2;
		this.paesStrategy.setLambda(lambda);
		int maxArchiveSize = 1;
		this.paesStrategy.setArchiveSize(maxArchiveSize);
		
		// Initialize the strategy
		List<IIndividual> archive = this.paesStrategy.initialize(population);
				
		// Execute the environmental selection: the first offspring dominates the second one
		List<IIndividual> result = this.paesStrategy.environmentalSelection(population, offspring, archive);
		assertTrue(result.size() == mu);
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
		int archiveSize = 2;
		this.paesStrategy.setArchiveSize(archiveSize);
		MOStrategyContext context = createContext();
		this.paesStrategy.setContext(context);
		this.paesStrategy.createSolutionComparator(createComparatorOfObjectives());
		((MOEvaluator)this.paesStrategy.getContext().getEvaluator()).setComparator(this.paesStrategy.getSolutionComparator().getFitnessComparator());
		int mu = 2;
		this.paesStrategy.setMu(mu);
		int lambda = 2;
		this.paesStrategy.setLambda(lambda);
		
		// Initialize the strategy
		this.paesStrategy.initialize(parent);
		
		// Execute the update method: the archive is empty
		List<IIndividual> result = this.paesStrategy.updateArchive(parent, offspring, new ArrayList<IIndividual>());
		assertTrue(result.size() <= archiveSize);
		
		// Execute the update method: the archive is full
		result = this.paesStrategy.updateArchive(parent, offspring, archive);
		assertTrue(result.size() <= archiveSize);
	}

	/* Auxiliary methods */

	/**
	 * Create the initial population with two individuals
	 * (<code>mu==2</code>).
	 * @return List containing the initial population.
	 * */
	protected List<IIndividual> createParent(){
		List<IIndividual> population = new ArrayList<IIndividual>();

		// Create two individuals
		SimpleValueFitness [] fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(3.0);
		fitness[1] = new SimpleValueFitness(2.0);
		BinArrayIndividual ind = new BinArrayIndividual(new byte[]{0,0,0}, new PAESMOFitness(new MOFitness(fitness),0,1));
		population.add(ind.copy());	
		
		fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(2.0);
		fitness[1] = new SimpleValueFitness(2.0);
		ind = new BinArrayIndividual(new byte[]{0,0,0}, new PAESMOFitness(new MOFitness(fitness),0,1));
		population.add(ind.copy());	
		
		return population;
	}

	/**
	 * Create two offspring (<code>lambda==2</code>).
	 * @return List containing the offspring.
	 * */
	protected List<IIndividual> createOffspring(){
		List<IIndividual> offspring = new ArrayList<IIndividual>();

		// Create a non-dominated individual
		SimpleValueFitness [] fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(4.0);
		fitness[1] = new SimpleValueFitness(4.0);
		BinArrayIndividual ind = new BinArrayIndividual(new byte[]{0,1,0}, new PAESMOFitness(new MOFitness(fitness),0,1));
		offspring.add(ind.copy());	

		// Create a dominated individual
		fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(1.0);
		fitness[1] = new SimpleValueFitness(1.0);
		ind = new BinArrayIndividual(new byte[]{1,0,1}, new PAESMOFitness(new MOFitness(fitness),0,1));
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
	 * Create the evolution context. In this variant,
	 * <code>mu==2</code> and <code>lambda==2</code>, so this method
	 * assumes that the population size is 2.
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
		MOStrategyContext context = new MOStrategyContext(randgen, null, evaluator, null, 2); // two individuals
		return context;
	}
}
