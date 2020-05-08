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
import net.sf.jclec.binarray.BinArrayIndividual;
import net.sf.jclec.fitness.SimpleValueFitness;
import net.sf.jclec.fitness.ValueFitnessComparator;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;

import org.junit.Test;

/**
 * Tests for the Pareto comparator.
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
 * @see ParetoComparator
 * */
public class ParetoComparatorTest {

	/* Tests */

	/**
	 * If the first individual has greater objective values for all the objective
	 * functions than the second individual, then the first individual dominates
	 * the second one and the result should be <code>1.0</code>.
	 * */
	@Test
	public void testFirstIndividualDominatesSecondIndividual(){

		// Create the comparator
		Comparator<IFitness> [] components = new ValueFitnessComparator[2];
		components[0] = new ValueFitnessComparator(false); // maximize objective 1
		components[1] = new ValueFitnessComparator(false); // maximize objective 2
		ParetoComparator comparator = new ParetoComparator((Comparator<IFitness>[])components);

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
		double result = comparator.compare(ind1.getFitness(), ind2.getFitness());
		assertTrue(result == 1.0);
		
		result = comparator.compare(ind2.getFitness(), ind1.getFitness());
		assertTrue(result == -1.0);
	}

	/**
	 * If the first individual has greater or equal objective values for all the objective
	 * functions than the second individual, then the first individual weakly dominates
	 * the second one and the result should be <code>1.0</code>.
	 * */
	@Test
	public void testFirstIndividualWeaklyDominatesSecondIndividual(){

		// Create the comparator
		Comparator<IFitness> [] components = new ValueFitnessComparator[2];
		components[0] = new ValueFitnessComparator(false); // maximize objective 1
		components[1] = new ValueFitnessComparator(false); // maximize objective 2
		ParetoComparator comparator = new ParetoComparator((Comparator<IFitness>[])components);

		// Create two individuals
		SimpleValueFitness [] fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(2.0);
		fitness[1] = new SimpleValueFitness(1.0);
		BinArrayIndividual ind1 = new BinArrayIndividual(new byte[]{0,0,0}, new MOFitness(fitness));

		fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(1.0);
		fitness[1] = new SimpleValueFitness(1.0);
		BinArrayIndividual ind2 = new BinArrayIndividual(new byte[]{0,0,0}, new MOFitness(fitness));

		// Check the comparison result
		double result = comparator.compare(ind1.getFitness(), ind2.getFitness());
		assertTrue(result == 1.0);
		
		result = comparator.compare(ind2.getFitness(), ind1.getFitness());
		assertTrue(result == -1.0);
	}

	/**
	 * Assuming a minimization problem, if the first individual has greater objective
	 * values for all the objective functions than the second individual, then the 
	 * first individual is dominated the second one and the result should be <code>-1.0</code>.
	 * */
	@Test
	public void testSecondIndividualDominatesFirstIndividual(){
		// Create the comparator
		Comparator<IFitness> [] components = new ValueFitnessComparator[2];
		components[0] = new ValueFitnessComparator(true); // minimize objective 1
		components[1] = new ValueFitnessComparator(true); // minimize objective 2
		ParetoComparator comparator = new ParetoComparator((Comparator<IFitness>[])components);

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
		double result = comparator.compare(ind1.getFitness(), ind2.getFitness());
		assertTrue(result == -1.0);
		
		result = comparator.compare(ind2.getFitness(), ind1.getFitness());
		assertTrue(result == 1.0);
	}

	/**
	 * Assuming a minimization problem, if the first individual has greater or equal objective 
	 * values for all the objective functions than the second individual, then the first individual 
	 * is weakly dominated by the second one and the result should be <code>-1.0</code>.
	 * */
	@Test
	public void testSecondIndividualWeaklyDominatesFirstIndividual(){
		// Create the comparator
		Comparator<IFitness> [] components = new ValueFitnessComparator[2];
		components[0] = new ValueFitnessComparator(true); // minimize objective 1
		components[1] = new ValueFitnessComparator(true); // minimize objective 2
		ParetoComparator comparator = new ParetoComparator((Comparator<IFitness>[])components);

		// Create two individuals
		SimpleValueFitness [] fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(2.0);
		fitness[1] = new SimpleValueFitness(1.0);
		BinArrayIndividual ind1 = new BinArrayIndividual(new byte[]{0,0,0}, new MOFitness(fitness));

		fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(1.0);
		fitness[1] = new SimpleValueFitness(1.0);
		BinArrayIndividual ind2 = new BinArrayIndividual(new byte[]{0,0,0}, new MOFitness(fitness));

		// Check the comparison result
		double result = comparator.compare(ind1.getFitness(), ind2.getFitness());
		assertTrue(result == -1.0);
		
		result = comparator.compare(ind2.getFitness(), ind1.getFitness());
		assertTrue(result == 1.0);
	}

	/**
	 * If each individual has a better objective value for one objective function,
	 * then both individuals are not dominated and the result should be <code>0.0</code>.
	 * */
	@Test
	public void testNonDominatedIndividualsResultZero(){

		// Create the comparator
		Comparator<IFitness> [] components = new ValueFitnessComparator[2];
		components[0] = new ValueFitnessComparator(false); // maximize objective 1
		components[1] = new ValueFitnessComparator(true); // minimize objective 2
		ParetoComparator comparator = new ParetoComparator((Comparator<IFitness>[])components);

		// Create two individuals
		SimpleValueFitness [] fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(3.0);
		fitness[1] = new SimpleValueFitness(3.0);
		BinArrayIndividual ind1 = new BinArrayIndividual(new byte[]{0,0,0}, new MOFitness(fitness));
		
		fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(1.0);
		fitness[1] = new SimpleValueFitness(1.0);
		BinArrayIndividual ind2 = new BinArrayIndividual(new byte[]{0,0,0}, new MOFitness(fitness));

		// Check the comparison result
		double result = comparator.compare(ind1.getFitness(), ind2.getFitness());
		assertTrue(result == 0.0);
		
		result = comparator.compare(ind2.getFitness(), ind1.getFitness());
		assertTrue(result == 0.0);
	}
}
