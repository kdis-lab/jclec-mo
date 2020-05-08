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

import org.junit.Test;

import net.sf.jclec.IFitness;
import net.sf.jclec.binarray.BinArrayIndividual;
import net.sf.jclec.fitness.SimpleValueFitness;
import net.sf.jclec.fitness.ValueFitnessComparator;
import net.sf.jclec.mo.comparator.fcomparator.ParetoComparator;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;

/**
 * Tests for the comparator of solutions.
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
 * @see MOSolutionComparator
 * */

public class MOSolutionComparatorTest {

	/* Tests */

	/**
	 * If the first individual dominates the second individual, then the first 
	 * individual is better than the second one.
	 * */
	@Test
	public void testFirstIndividualBetterThanSecondIndividual(){

		// Create the comparator
		Comparator<IFitness> [] components = new ValueFitnessComparator[2];
		components[0] = new ValueFitnessComparator(false); // maximize objective 1
		components[1] = new ValueFitnessComparator(false); // maximize objective 2
		ParetoComparator fcomparator = new ParetoComparator((Comparator<IFitness>[])components);
		MOSolutionComparator comparator = new MOSolutionComparator(fcomparator);

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
		double result = comparator.compare(ind1, ind2);
		assertTrue(result == 1.0);
		
		result = comparator.compare(ind2, ind1);
		assertTrue(result == -1.0);
	}
	
	/**
	 * If both individuals are non dominated, then the result should
	 * be <code>0.0</code>.
	 * */
	@Test
	public void testNonDominatedIndividuals(){

		// Create the comparator
		Comparator<IFitness> [] components = new ValueFitnessComparator[2];
		components[0] = new ValueFitnessComparator(false); // maximize objective 1
		components[1] = new ValueFitnessComparator(false); // maximize objective 2
		ParetoComparator fcomparator = new ParetoComparator((Comparator<IFitness>[])components);
		MOSolutionComparator comparator = new MOSolutionComparator(fcomparator);

		// Create two individuals
		SimpleValueFitness [] fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(4.0);
		fitness[1] = new SimpleValueFitness(6.0);
		BinArrayIndividual ind1 = new BinArrayIndividual(new byte[]{0,0,0}, new MOFitness(fitness));
		
		fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(6.0);
		fitness[1] = new SimpleValueFitness(4.0);
		BinArrayIndividual ind2 = new BinArrayIndividual(new byte[]{0,0,0}, new MOFitness(fitness));

		// Check the comparison results
		double result = comparator.compare(ind1, ind2);
		assertTrue(result == 0.0);
		
		result = comparator.compare(ind2, ind1);
		assertTrue(result == 0.0);
	}
}
