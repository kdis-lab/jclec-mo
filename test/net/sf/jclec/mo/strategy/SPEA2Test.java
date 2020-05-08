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
import net.sf.jclec.base.AbstractSelector;
import net.sf.jclec.fitness.SimpleValueFitness;
import net.sf.jclec.mo.evaluation.MOEvaluator;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;
import net.sf.jclec.selector.TournamentSelector;

import org.junit.Test;

/**
 * Tests for the SPEA2 strategy.
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
 * @see SPEA2
 * */

public class SPEA2Test extends MOStrategyTest{

	/* Properties */

	protected SPEA2 spea2Strategy = new SPEA2();

	protected MOFitness fitness = new MOFitness();

	/* Tests */

	/**
	 * Test that the initial archive has the correct size
	 * considering that dominated solutions should be added.
	 * */
	@Test
	public void testInitializeArchiveSizeByIncrementing(){
		try{
			// Create the population. It has only one non-dominated solution (5,5)
			List<IIndividual> population = createPopulation(this.fitness);

			// Configure the strategy
			int archiveSize = 4;
			this.spea2Strategy.setArchiveSize(archiveSize);
			this.spea2Strategy.setKValue(2);
			MOStrategyContext context = createContext();
			this.spea2Strategy.setContext(context);
			this.spea2Strategy.createSolutionComparator(createComparatorOfObjectives());
			((MOEvaluator)this.spea2Strategy.getContext().getEvaluator()).setComparator(this.spea2Strategy.getSolutionComparator().getFitnessComparator());

			// Execute the initialize method
			List<IIndividual> result = this.spea2Strategy.initialize(population);
			assertTrue(result.size() == archiveSize);
		} catch (CloneNotSupportedException e) {
			fail();
		}
	}

	/**
	 * Test that the initial archive has the correct size
	 * considering that the truncation method should be used.
	 * */
	@Test
	public void testInitializeArchiveSizeByDecrementing(){
		try{
			// Create the population
			List<IIndividual> population = createPopulation(this.fitness);
			// Change the first solution (5,5), so now there are three non-dominated solution to choose among
			((MOFitness)population.get(0).getFitness()).setObjectiveValue(0, new SimpleValueFitness(2));
			((MOFitness)population.get(0).getFitness()).setObjectiveValue(0, new SimpleValueFitness(6));

			// Configure the strategy
			int archiveSize = 2;
			this.spea2Strategy.setArchiveSize(archiveSize);
			this.spea2Strategy.setKValue(1);
			MOStrategyContext context = createContext();
			this.spea2Strategy.setContext(context);
			this.spea2Strategy.createSolutionComparator(createComparatorOfObjectives());
			((MOEvaluator)this.spea2Strategy.getContext().getEvaluator()).setComparator(this.spea2Strategy.getSolutionComparator().getFitnessComparator());

			// Execute the initialize method
			List<IIndividual> result = this.spea2Strategy.initialize(population);
			assertTrue(result.size() == archiveSize);
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
			// Create the populations
			List<IIndividual> population = createPopulation(this.fitness);
			List<IIndividual> archive = new ArrayList<IIndividual>();
			archive.add(population.get(0).copy());

			// Configure the strategy
			MOStrategyContext context = createContext();
			this.spea2Strategy.setContext(context);
			this.spea2Strategy.createSolutionComparator(createComparatorOfObjectives());
			((MOEvaluator)this.spea2Strategy.getContext().getEvaluator()).setComparator(this.spea2Strategy.getSolutionComparator().getFitnessComparator());
			AbstractSelector selector = new TournamentSelector(this.spea2Strategy.getContext());
			this.spea2Strategy.setSelector(selector);

			// Execute the mating selection
			int size = this.spea2Strategy.matingSelection(population, archive).size();
			assert(size == this.spea2Strategy.getContext().getPopulationSize());
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
			// Create the population
			List<IIndividual> population = createPopulationInRange01(this.fitness);

			// Configure the strategy
			MOStrategyContext context = createContextPopulationInRange01();
			this.spea2Strategy.setContext(context);
			this.spea2Strategy.createSolutionComparator(createComparatorOfObjectives());
			((MOEvaluator)this.spea2Strategy.getContext().getEvaluator()).setComparator(this.spea2Strategy.getSolutionComparator().getFitnessComparator());

			// Execute the environmental selection
			int size = this.spea2Strategy.environmentalSelection(population, population, null).size();
			assertTrue(size == this.spea2Strategy.getContext().getPopulationSize());
		} catch (CloneNotSupportedException e) {
			fail();
		}
	}

	/**
	 * Test that the archive size remains the same after updating it.
	 * */
	@Test
	public void testUpdateArchiveResultSize(){
		try{
			// Create the populations
			List<IIndividual> population = createPopulation(this.fitness);
			List<IIndividual> archive = createArchive(this.fitness);
			List<IIndividual> offspring = createOffspring(this.fitness);

			// Configure the strategy
			int archiveSize = 4;
			this.spea2Strategy.setArchiveSize(archiveSize);
			this.spea2Strategy.setKValue(2);
			MOStrategyContext context = createContext();
			this.spea2Strategy.setContext(context);
			this.spea2Strategy.createSolutionComparator(createComparatorOfObjectives());
			((MOEvaluator)this.spea2Strategy.getContext().getEvaluator()).setComparator(this.spea2Strategy.getSolutionComparator().getFitnessComparator());

			// Execute the update method
			List<IIndividual> result = this.spea2Strategy.updateArchive(population, offspring, archive);
			assertTrue(result.size() == archiveSize);
		} catch (CloneNotSupportedException e) {
			fail();
		}
	}
}