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
import net.sf.jclec.binarray.BinArrayIndividual;
import net.sf.jclec.fitness.SimpleValueFitness;
import net.sf.jclec.fitness.ValueFitnessComparator;
import net.sf.jclec.mo.comparator.fcomparator.ParetoComparator;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;

import org.junit.Test;

/**
 * Tests for the comparator by objective.
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
 * @see ComparatorByObjectives
 * */
public class ComparatorByObjectivesTest {

	/* Tests */

	/**
	 * If the first individual has greater objective values for all the objective
	 * functions than the second individual, then the result after comparing both 
	 * objectives should be <code>1.0</code>.
	 * */
	@Test
	public void testFirstIndividualBetterThanSecondIndividualAllObjectives(){

		// Create the comparator
		Comparator<IFitness> [] components = new ValueFitnessComparator[2];
		components[0] = new ValueFitnessComparator(false); // maximize objective 1
		components[1] = new ValueFitnessComparator(false); // maximize objective 2
		ParetoComparator fcomparator = new ParetoComparator((Comparator<IFitness>[])components);
		ComparatorByObjectives comparator = new ComparatorByObjectives(fcomparator, 0);

		// Create two individuals
		SimpleValueFitness [] fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(5.0);
		fitness[1] = new SimpleValueFitness(5.0);
		BinArrayIndividual ind1 = new BinArrayIndividual(new byte[]{0,0,0}, new MOFitness(fitness));

		fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(1.0);
		fitness[1] = new SimpleValueFitness(1.0);
		BinArrayIndividual ind2 = new BinArrayIndividual(new byte[]{0,0,0}, new MOFitness(fitness));

		// Check the comparison results
		double result1 = comparator.compare(ind1, ind2);
		comparator.setNumberObjective(1);
		double result2 = comparator.compare(ind1, ind2);
		assertTrue(result1 == result2);
		assertTrue(result1 == 1.0);
		
		comparator.setNumberObjective(0);
		result1 = comparator.compare(ind2, ind1);
		comparator.setNumberObjective(1);
		result2 = comparator.compare(ind2, ind1);
		assertTrue(result1 == result2);
		assertTrue(result1 == -1.0);
	}
	
	/**
	 * If each individual has one objective with a higher value, 
	 * then each individual wins in one competition.
	 * */
	@Test
	public void testFirstIndividualBetterThanSecondIndividualOneObjective(){

		// Create the comparator
		Comparator<IFitness> [] components = new ValueFitnessComparator[2];
		components[0] = new ValueFitnessComparator(false); // maximize objective 1
		components[1] = new ValueFitnessComparator(false); // maximize objective 2
		ParetoComparator fcomparator = new ParetoComparator((Comparator<IFitness>[])components);
		ComparatorByObjectives comparator = new ComparatorByObjectives(fcomparator, 0);

		// Create two individuals
		SimpleValueFitness [] fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(5.0);
		fitness[1] = new SimpleValueFitness(0.0);
		BinArrayIndividual ind1 = new BinArrayIndividual(new byte[]{0,0,0}, new MOFitness(fitness));

		fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(1.0);
		fitness[1] = new SimpleValueFitness(1.0);
		BinArrayIndividual ind2 = new BinArrayIndividual(new byte[]{0,0,0}, new MOFitness(fitness));

		// Check the comparison results
		double result1 = comparator.compare(ind1, ind2);
		comparator.setNumberObjective(1);
		double result2 = comparator.compare(ind1, ind2);
		assertTrue(result1 == 1.0);
		assertTrue(result2 == -1.0);
		
		comparator.setNumberObjective(0);
		result1 = comparator.compare(ind2, ind1);
		comparator.setNumberObjective(1);
		result2 = comparator.compare(ind2, ind1);
		
		assertTrue(result1 == -1.0);
		assertTrue(result2 == 1.0);
	}
	
	/**
	 * Assuming a minimization problem, if the first individual has greater objective values
	 * for all the objective functions than the second individual, then the result after comparing both 
	 * objectives should be <code>-1.0</code>.
	 * */
	@Test
	public void testSecondIndividualBetterThanFirstIndividualAllObjectives(){

		// Create the comparator
		Comparator<IFitness> [] components = new ValueFitnessComparator[2];
		components[0] = new ValueFitnessComparator(true); // minimize objective 1
		components[1] = new ValueFitnessComparator(true); // minimize objective 2
		ParetoComparator fcomparator = new ParetoComparator((Comparator<IFitness>[])components);
		ComparatorByObjectives comparator = new ComparatorByObjectives(fcomparator, 0);

		// Create two individuals
		SimpleValueFitness [] fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(5.0);
		fitness[1] = new SimpleValueFitness(5.0);
		BinArrayIndividual ind1 = new BinArrayIndividual(new byte[]{0,0,0}, new MOFitness(fitness));

		fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(1.0);
		fitness[1] = new SimpleValueFitness(1.0);
		BinArrayIndividual ind2 = new BinArrayIndividual(new byte[]{0,0,0}, new MOFitness(fitness));

		// Check the comparison results
		double result1 = comparator.compare(ind1, ind2);
		comparator.setNumberObjective(1);
		double result2 = comparator.compare(ind1, ind2);
		assertTrue(result1 == result2);
		assertTrue(result1 == -1.0);
		
		comparator.setNumberObjective(0);
		result1 = comparator.compare(ind2, ind1);
		comparator.setNumberObjective(1);
		result2 = comparator.compare(ind2, ind1);
		assertTrue(result1 == result2);
		assertTrue(result1 == 1.0);
	}
	
	/**
	 * If each individual has one objective with a higher value, 
	 * then each individual wins in one competition.
	 * */
	@Test
	public void testFirstIndividualBetterThanSecondIndividualOneObjectiveMinimization(){

		// Create the comparator
		Comparator<IFitness> [] components = new ValueFitnessComparator[2];
		components[0] = new ValueFitnessComparator(false); // maximize objective 1
		components[1] = new ValueFitnessComparator(true); // minimize objective 2
		ParetoComparator fcomparator = new ParetoComparator((Comparator<IFitness>[])components);
		ComparatorByObjectives comparator = new ComparatorByObjectives(fcomparator, 0);

		// Create two individuals
		SimpleValueFitness [] fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(5.0);
		fitness[1] = new SimpleValueFitness(5.0);
		BinArrayIndividual ind1 = new BinArrayIndividual(new byte[]{0,0,0}, new MOFitness(fitness));

		fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(1.0);
		fitness[1] = new SimpleValueFitness(1.0);
		BinArrayIndividual ind2 = new BinArrayIndividual(new byte[]{0,0,0}, new MOFitness(fitness));

		// Check the comparison result
		double result1 = comparator.compare(ind1, ind2);
		comparator.setNumberObjective(1);
		double result2 = comparator.compare(ind1, ind2);
		assertTrue(result1 == 1.0);
		assertTrue(result2 == -1.0);
		
		comparator.setNumberObjective(0);
		result1 = comparator.compare(ind2, ind1);
		comparator.setNumberObjective(1);
		result2 = comparator.compare(ind2, ind1);
		
		assertTrue(result1 == -1.0);
		assertTrue(result2 == 1.0);
	}
	
	/**
	 * If both individuals have the same objective values,
	 * the results should be <code>0.0</code>.
	 * */
	@Test
	public void testEqualIndividualsResultZero(){

		// Create the comparator
		Comparator<IFitness> [] components = new ValueFitnessComparator[2];
		components[0] = new ValueFitnessComparator(false); // maximize objective 1
		components[1] = new ValueFitnessComparator(true); // minimize objective 2
		ParetoComparator fcomparator = new ParetoComparator((Comparator<IFitness>[])components);
		ComparatorByObjectives comparator = new ComparatorByObjectives(fcomparator, 0);

		// Create two individuals
		SimpleValueFitness [] fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(3.0);
		fitness[1] = new SimpleValueFitness(1.0);
		BinArrayIndividual ind1 = new BinArrayIndividual(new byte[]{0,0,0}, new MOFitness(fitness));

		fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(3.0);
		fitness[1] = new SimpleValueFitness(1.0);
		BinArrayIndividual ind2 = new BinArrayIndividual(new byte[]{0,0,0}, new MOFitness(fitness));

		// Check the comparison result
		double result1 = comparator.compare(ind1, ind2);
		comparator.setNumberObjective(1);
		double result2 = comparator.compare(ind1, ind2);
		assertTrue(result1 == 0.0);
		assertTrue(result2 == 0.0);
		
		comparator.setNumberObjective(0);
		result1 = comparator.compare(ind2, ind1);
		comparator.setNumberObjective(1);
		result2 = comparator.compare(ind2, ind1);
		assertTrue(result1 == 0.0);
		assertTrue(result2 == 0.0);
	}
}
