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


package net.sf.jclec.mo.comparator;

import static org.junit.Assert.assertTrue;

import java.util.Comparator;

import net.sf.jclec.IFitness;
import net.sf.jclec.fitness.SimpleValueFitness;
import net.sf.jclec.fitness.ValueFitnessComparator;
import net.sf.jclec.mo.comparator.fcomparator.ParetoComparator;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;

import org.junit.Test;

/**
 * Tests for the constrained comparator used in NSGA-II.
 * 
 * <p>HISTORY:
 * <ul>
 * <li>(AR|JRR|SV, 1.0, March 2018)		First release.</li>
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
 * @see NSGA2ConstrainedComparator
 * */

public class NSGA2ConstrainedComparatorTest {

	/* Tests */

	/**
	 * If the first individual is feasible and the second individual is infeasible,
	 * then the first individual is better than the second one regardless the objective
	 * values.
	 * */
	@Test
	public void testFeasibleIndividualBetterThanInfeasibleIndividual(){

		// Create the comparator
		Comparator<IFitness> [] components = new ValueFitnessComparator[2];
		components[0] = new ValueFitnessComparator(true); // minimize objective 1
		components[1] = new ValueFitnessComparator(true); // minimize objective 2
		ParetoComparator fcomparator = new ParetoComparator((Comparator<IFitness>[])components);
		NSGA2ConstrainedComparator comparator = new NSGA2ConstrainedComparator(fcomparator);

		// Create two individuals
		SimpleValueFitness [] fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(1.0);
		fitness[1] = new SimpleValueFitness(1.0);
		DummyConstrainedIndividual ind1 = new DummyConstrainedIndividual(new byte[]{0,0,0}, new MOFitness(fitness));
		ind1.setFeasible(true);

		fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(5.0);
		fitness[1] = new SimpleValueFitness(5.0);
		DummyConstrainedIndividual ind2 = new DummyConstrainedIndividual(new byte[]{0,0,0}, new MOFitness(fitness));
		ind2.setFeasible(false);

		// Check the comparison results
		double result = comparator.compare(ind1, ind2);
		assertTrue(result == 1.0);
		
		result = comparator.compare(ind2, ind1);
		assertTrue(result == -1.0);
	}
	
	/**
	 * If both individuals are infeasible but the first one violates a less number of constraints,
	 * then the first individual is better than the second one.
	 * */
	@Test
	public void testInfeasibleFirstIndividualBetterThanInfeasibleSecondIndividual(){

		// Create the comparator
		Comparator<IFitness> [] components = new ValueFitnessComparator[2];
		components[0] = new ValueFitnessComparator(true); // minimize objective 1
		components[1] = new ValueFitnessComparator(true); // minimize objective 2
		ParetoComparator fcomparator = new ParetoComparator((Comparator<IFitness>[])components);
		NSGA2ConstrainedComparator comparator = new NSGA2ConstrainedComparator(fcomparator);

		// Create two individuals
		SimpleValueFitness [] fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(1.0);
		fitness[1] = new SimpleValueFitness(1.0);
		DummyConstrainedIndividual ind1 = new DummyConstrainedIndividual(new byte[]{0,0,0}, new MOFitness(fitness));
		ind1.setFeasible(false);
		ind1.setDegreeOfInfeasibility(1.0);

		fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(5.0);
		fitness[1] = new SimpleValueFitness(5.0);
		DummyConstrainedIndividual ind2 = new DummyConstrainedIndividual(new byte[]{0,0,0}, new MOFitness(fitness));
		ind2.setFeasible(false);
		ind2.setDegreeOfInfeasibility(2.0);
		
		// Check the comparison results
		double result = comparator.compare(ind1, ind2);
		assertTrue(result == 1.0);
		
		result = comparator.compare(ind2, ind1);
		assertTrue(result == -1.0);
	}
	
	/**
	 * If both individuals are feasible but the first one dominates the second individual,
	 * then the first individual is preferred.
	 * */
	@Test
	public void testFeasibleIndividualsResultBasedOnDominance(){

		// Create the comparator
		Comparator<IFitness> [] components = new ValueFitnessComparator[2];
		components[0] = new ValueFitnessComparator(true); // minimize objective 1
		components[1] = new ValueFitnessComparator(true); // minimize objective 2
		ParetoComparator fcomparator = new ParetoComparator((Comparator<IFitness>[])components);
		NSGA2ConstrainedComparator comparator = new NSGA2ConstrainedComparator(fcomparator);

		// Create two individuals
		SimpleValueFitness [] fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(1.0);
		fitness[1] = new SimpleValueFitness(1.0);
		DummyConstrainedIndividual ind1 = new DummyConstrainedIndividual(new byte[]{0,0,0}, new MOFitness(fitness));
		ind1.setFeasible(true);
		
		fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(5.0);
		fitness[1] = new SimpleValueFitness(5.0);
		DummyConstrainedIndividual ind2 = new DummyConstrainedIndividual(new byte[]{0,0,0}, new MOFitness(fitness));
		ind2.setFeasible(true);

		// Check the comparison results
		double result = comparator.compare(ind1, ind2);
		assertTrue(result == 1.0);
		
		result = comparator.compare(ind2, ind1);
		assertTrue(result == -1.0);
	}
	
	/**
	 * If both individuals are infeasible and they have the same degree of constraint violations,
	 * they are equals regardless the objective values.
	 * */
	@Test
	public void testEqualDegreeOfInfeasibilityResultZero(){

		// Create the comparator
		Comparator<IFitness> [] components = new ValueFitnessComparator[2];
		components[0] = new ValueFitnessComparator(true); // minimize objective 1
		components[1] = new ValueFitnessComparator(true); // minimize objective 2
		ParetoComparator fcomparator = new ParetoComparator((Comparator<IFitness>[])components);
		NSGA2ConstrainedComparator comparator = new NSGA2ConstrainedComparator(fcomparator);

		// Create two individuals
		SimpleValueFitness [] fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(1.0);
		fitness[1] = new SimpleValueFitness(1.0);
		DummyConstrainedIndividual ind1 = new DummyConstrainedIndividual(new byte[]{0,0,0}, new MOFitness(fitness));
		ind1.setFeasible(false);
		ind1.setDegreeOfInfeasibility(1.0);

		fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(5.0);
		fitness[1] = new SimpleValueFitness(5.0);
		DummyConstrainedIndividual ind2 = new DummyConstrainedIndividual(new byte[]{0,0,0}, new MOFitness(fitness));
		ind2.setFeasible(false);
		ind2.setDegreeOfInfeasibility(1.0);

		// Check the comparison results
		double result = comparator.compare(ind1, ind2);
		assertTrue(result == 0.0);
		
		result = comparator.compare(ind2, ind1);
		assertTrue(result == 0.0);
	}
	
	/**
	 * If both individuals are feasible and they are non-dominated solutions,
	 * then the result should be <code>0.0</code>.
	 * */
	@Test
	public void testFeasibleNonDominatedIndividualsResultZero(){

		// Create the comparator
		Comparator<IFitness> [] components = new ValueFitnessComparator[2];
		components[0] = new ValueFitnessComparator(true); // minimize objective 1
		components[1] = new ValueFitnessComparator(true); // minimize objective 2
		ParetoComparator fcomparator = new ParetoComparator((Comparator<IFitness>[])components);
		NSGA2ConstrainedComparator comparator = new NSGA2ConstrainedComparator(fcomparator);

		// Create two individuals
		SimpleValueFitness [] fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(1.0);
		fitness[1] = new SimpleValueFitness(2.0);
		DummyConstrainedIndividual ind1 = new DummyConstrainedIndividual(new byte[]{0,0,0}, new MOFitness(fitness));
		ind1.setFeasible(true);
		
		fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(2.0);
		fitness[1] = new SimpleValueFitness(1.0);
		DummyConstrainedIndividual ind2 = new DummyConstrainedIndividual(new byte[]{0,0,0}, new MOFitness(fitness));
		ind2.setFeasible(true);

		// Check the comparison results
		double result = comparator.compare(ind1, ind2);
		assertTrue(result == 0.0);
		
		result = comparator.compare(ind2, ind1);
		assertTrue(result == 0.0);
	}	
}
