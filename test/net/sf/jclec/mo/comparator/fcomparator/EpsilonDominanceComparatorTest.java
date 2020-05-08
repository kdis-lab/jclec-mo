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


package net.sf.jclec.mo.comparator.fcomparator;

import static org.junit.Assert.assertTrue;

import java.util.Comparator;

import net.sf.jclec.IFitness;
import net.sf.jclec.fitness.SimpleValueFitness;
import net.sf.jclec.fitness.ValueFitnessComparator;
import net.sf.jclec.mo.evaluation.fitness.HypercubeMOFitness;
import net.sf.jclec.mo.strategy.util.Hypercube;

import org.junit.Test;

/**
 * Tests for the Hypercube comparator (e-dominance).
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
 * @see HypercubeComparator
 * */
public class EpsilonDominanceComparatorTest {

	/* Tests */

	/**
	 * If the first hypercube has greater values for all the positions
	 * than the second hypercube, then an individual located in the first 
	 * hypercube e-dominates the individual located in the second hypercube.
	 * */
	@Test
	public void testFirstHypercubeDominatesSecondHypercube(){

		// Create the comparator
		Comparator<IFitness> [] components = new ValueFitnessComparator[3];
		components[0] = new ValueFitnessComparator(false); // maximize objective 1
		components[1] = new ValueFitnessComparator(false); // maximize objective 2
		components[2] = new ValueFitnessComparator(false); // maximize objective 3
		EpsilonDominanceComparator comparator = new EpsilonDominanceComparator(components);

		// Create two hypercubes
		IFitness [] values1 = new IFitness[3];
		values1[0] = new SimpleValueFitness(2.0);
		values1[1] = new SimpleValueFitness(3.0);
		values1[2] = new SimpleValueFitness(1.0);
		Hypercube h1 = new Hypercube(values1);
		HypercubeMOFitness fitness1 = new HypercubeMOFitness();
		fitness1.setHypercube(h1);

		IFitness [] values2 = new IFitness[3];
		values2[0] = new SimpleValueFitness(1.0);
		values2[1] = new SimpleValueFitness(2.0);
		values2[2] = new SimpleValueFitness(0.0);
		Hypercube h2 = new Hypercube(values2);
		HypercubeMOFitness fitness2 = new HypercubeMOFitness();
		fitness2.setHypercube(h2);

		// Check the comparison result
		double result = comparator.compare(fitness1, fitness2);
		assertTrue(result == 1.0);
		result = comparator.compare(fitness2, fitness1);
		assertTrue(result == -1.0);
	}

	/**
	 * If the first hypercube has greater or equal values for all the positions
	 * than the second hypercube, then an individual located in the first 
	 * hypercube e-dominates the individual located in the second hypercube.
	 * */
	@Test
	public void testFirstHypercubeWeaklyDominatesSecondHypercube(){

		// Create the comparator
		Comparator<IFitness> [] components = new ValueFitnessComparator[3];
		components[0] = new ValueFitnessComparator(false); // maximize objective 1
		components[1] = new ValueFitnessComparator(false); // maximize objective 2
		components[2] = new ValueFitnessComparator(false); // maximize objective 3
		EpsilonDominanceComparator comparator = new EpsilonDominanceComparator(components);

		// Create two hypercubes
		IFitness [] values1 = new IFitness[3];
		values1[0] = new SimpleValueFitness(2.0);
		values1[1] = new SimpleValueFitness(3.0);
		values1[2] = new SimpleValueFitness(1.0);
		Hypercube h1 = new Hypercube(values1);
		HypercubeMOFitness fitness1 = new HypercubeMOFitness();
		fitness1.setHypercube(h1);

		IFitness [] values2 = new IFitness[3];
		values2[0] = new SimpleValueFitness(2.0);
		values2[1] = new SimpleValueFitness(3.0);
		values2[2] = new SimpleValueFitness(0.0);
		Hypercube h2 = new Hypercube(values2);
		HypercubeMOFitness fitness2 = new HypercubeMOFitness();
		fitness2.setHypercube(h2);

		// Check the comparison result
		double result = comparator.compare(fitness1, fitness2);
		assertTrue(result == 1.0);
		result = comparator.compare(fitness2, fitness1);
		assertTrue(result == -1.0);
	}

	/**
	 * Assuming a minimization problem, if the first hypercube has greater values for 
	 * all the positions than the second hypercube, then an individual located in the first 
	 * hypercube is e-dominated by the individual located in the second hypercube.
	 * */
	@Test
	public void testSecondIndividualDominatesFirstIndividual(){

		// Create the comparator
		Comparator<IFitness> [] components = new ValueFitnessComparator[3];
		components[0] = new ValueFitnessComparator(true); // minimize objective 1
		components[1] = new ValueFitnessComparator(true); // minimize objective 2
		components[2] = new ValueFitnessComparator(true); // minimize objective 3
		EpsilonDominanceComparator comparator = new EpsilonDominanceComparator(components);

		// Create two hypercubes
		IFitness [] values1 = new IFitness[3];
		values1[0] = new SimpleValueFitness(2.0);
		values1[1] = new SimpleValueFitness(3.0);
		values1[2] = new SimpleValueFitness(1.0);
		Hypercube h1 = new Hypercube(values1);
		HypercubeMOFitness fitness1 = new HypercubeMOFitness();
		fitness1.setHypercube(h1);

		IFitness [] values2 = new IFitness[3];
		values2[0] = new SimpleValueFitness(1.0);
		values2[1] = new SimpleValueFitness(2.0);
		values2[2] = new SimpleValueFitness(0.0);
		Hypercube h2 = new Hypercube(values2);
		HypercubeMOFitness fitness2 = new HypercubeMOFitness();
		fitness2.setHypercube(h2);

		// Check the comparison result
		double result = comparator.compare(fitness1, fitness2);
		assertTrue(result == -1.0);
		result = comparator.compare(fitness2, fitness1);
		assertTrue(result == 1.0);
	}

	/**
	 * Assuming a minimization problem, if the first hypercube has greater or equal 
	 * values for all the positions than the second hypercube, then an individual 
	 * located in the first hypercube is weakly e-dominated by the individual located in 
	 * the second hypercube.
	 * */
	@Test
	public void testSecondIndividualWeaklyDominatesFirstIndividual(){

		// Create the comparator
		Comparator<IFitness> [] components = new ValueFitnessComparator[3];
		components[0] = new ValueFitnessComparator(true); // minimize objective 1
		components[1] = new ValueFitnessComparator(true); // minimize objective 2
		components[2] = new ValueFitnessComparator(true); // minimize objective 3
		EpsilonDominanceComparator comparator = new EpsilonDominanceComparator(components);

		// Create two hypercubes
		IFitness [] values1 = new IFitness[3];
		values1[0] = new SimpleValueFitness(2.0);
		values1[1] = new SimpleValueFitness(3.0);
		values1[2] = new SimpleValueFitness(1.0);
		Hypercube h1 = new Hypercube(values1);
		HypercubeMOFitness fitness1 = new HypercubeMOFitness();
		fitness1.setHypercube(h1);

		IFitness [] values2 = new IFitness[3];
		values2[0] = new SimpleValueFitness(2.0);
		values2[1] = new SimpleValueFitness(3.0);
		values2[2] = new SimpleValueFitness(0.0);
		Hypercube h2 = new Hypercube(values2);
		HypercubeMOFitness fitness2 = new HypercubeMOFitness();
		fitness2.setHypercube(h2);

		// Check the comparison result
		double result = comparator.compare(fitness1, fitness2);
		assertTrue(result == -1.0);
		result = comparator.compare(fitness2, fitness1);
		assertTrue(result == 1.0);
	}

	/**
	 * If each hypercube has a better value for one position, then the corresponding 
	 * individuals are not dominated and the result should be <code>0.0</code>.
	 * */
	@Test
	public void testNonDominatedIndividualsResultZero(){

		// Create the comparator
		Comparator<IFitness> [] components = new ValueFitnessComparator[2];
		components[0] = new ValueFitnessComparator(true); // minimize objective 1
		components[1] = new ValueFitnessComparator(false); // maximize objective 2
		EpsilonDominanceComparator comparator = new EpsilonDominanceComparator(components);
		
		// Create two hypercubes
		IFitness [] values1 = new IFitness[2];
		values1[0] = new SimpleValueFitness(4.0);
		values1[1] = new SimpleValueFitness(5.0);
		Hypercube h1 = new Hypercube(values1);
		HypercubeMOFitness fitness1 = new HypercubeMOFitness();
		fitness1.setHypercube(h1);
		
		IFitness [] values2 = new IFitness[2];
		values2[0] = new SimpleValueFitness(2.0);
		values2[1] = new SimpleValueFitness(3.0);
		Hypercube h2 = new Hypercube(values2);
		HypercubeMOFitness fitness2 = new HypercubeMOFitness();
		fitness2.setHypercube(h2);
		
		// Check the comparison result
		double result = comparator.compare(fitness1, fitness2);
		assertTrue(result == 0.0);
		result = comparator.compare(fitness2, fitness1);
		assertTrue(result == 0.0);
	}
}
