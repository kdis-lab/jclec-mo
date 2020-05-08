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

import org.junit.Test;

import net.sf.jclec.binarray.BinArrayIndividual;
import net.sf.jclec.fitness.SimpleValueFitness;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;

/**
 * Tests for the fitness value comparator.
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
 * @see MOValueFitnessComparator
 * */

public class MOValueFitnessComparatorTest {

	/* Properties */
	
	protected MOValueFitnessComparator comparator = new MOValueFitnessComparator();
	
	/* Tests */
	
	/**
	 * If the first individual has a greater fitness value than the second individual, 
	 * then the result should be equals to <code>1.0</code>.
	 * */
	@Test
	public void testFirstIndividualBetterThanSecondIndividual(){
		SimpleValueFitness [] fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(5.0);
		fitness[1] = new SimpleValueFitness(5.0);
		BinArrayIndividual ind1 = new BinArrayIndividual(new byte[]{0,0,0}, new MOFitness(fitness));
		((MOFitness)ind1.getFitness()).setValue(0.9);
		
		fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(1.0);
		fitness[1] = new SimpleValueFitness(1.0);
		BinArrayIndividual ind2 = new BinArrayIndividual(new byte[]{0,0,0}, new MOFitness(fitness));
		((MOFitness)ind2.getFitness()).setValue(0.1);
		
		// Check the comparison result
		double result = this.comparator.compare(ind1.getFitness(), ind2.getFitness());
		assertTrue(result == 1.0);
		
		result = this.comparator.compare(ind2.getFitness(), ind1.getFitness());
		assertTrue(result == -1.0);
	}
	
	/**
	 * If the first individual has a smaller fitness value than the second individual, 
	 * then the result should be equals to <code>-1.0</code>.
	 * */
	@Test
	public void testSecondIndividualBetterThanFirstIndividual(){
		SimpleValueFitness [] fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(5.0);
		fitness[1] = new SimpleValueFitness(5.0);
		BinArrayIndividual ind1 = new BinArrayIndividual(new byte[]{0,0,0}, new MOFitness(fitness));
		((MOFitness)ind1.getFitness()).setValue(0.3);
		
		fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(1.0);
		fitness[1] = new SimpleValueFitness(1.0);
		BinArrayIndividual ind2 = new BinArrayIndividual(new byte[]{0,0,0}, new MOFitness(fitness));
		((MOFitness)ind2.getFitness()).setValue(0.5);
		
		// Check the comparison result
		double result = this.comparator.compare(ind1.getFitness(), ind2.getFitness());
		assertTrue(result == -1.0);
		
		result = this.comparator.compare(ind2.getFitness(), ind1.getFitness());
		assertTrue(result == 1.0);
	}
	
	/**
	 * If both individuals have the same fitness value, 
	 * then the result should be equals to <code>0.0</code>.
	 * */
	@Test
	public void testEqualIndividualsResultZero(){
		SimpleValueFitness [] fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(5.0);
		fitness[1] = new SimpleValueFitness(5.0);
		BinArrayIndividual ind1 = new BinArrayIndividual(new byte[]{0,0,0}, new MOFitness(fitness));
		((MOFitness)ind1.getFitness()).setValue(0.2);
		
		fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(1.0);
		fitness[1] = new SimpleValueFitness(1.0);
		BinArrayIndividual ind2 = new BinArrayIndividual(new byte[]{0,0,0}, new MOFitness(fitness));
		((MOFitness)ind2.getFitness()).setValue(0.2);
		
		// Check the comparison result
		double result = this.comparator.compare(ind1.getFitness(), ind2.getFitness());
		assertTrue(result == 0.0);
		
		result = this.comparator.compare(ind2.getFitness(), ind1.getFitness());
		assertTrue(result == 0.0);
	}
	
	/**
	 * Assuming a minimization problem, if the first individual has a smaller fitness 
	 * value than the second individual, then the result should be equals to <code>1.0</code>.
	 * */
	@Test
	public void testFirstIndividualBetterThanSecondIndividualWithInverseFlag(){
		SimpleValueFitness [] fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(5.0);
		fitness[1] = new SimpleValueFitness(5.0);
		BinArrayIndividual ind1 = new BinArrayIndividual(new byte[]{0,0,0}, new MOFitness(fitness));
		((MOFitness)ind1.getFitness()).setValue(0.2);
		
		fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(1.0);
		fitness[1] = new SimpleValueFitness(1.0);
		BinArrayIndividual ind2 = new BinArrayIndividual(new byte[]{0,0,0}, new MOFitness(fitness));
		((MOFitness)ind2.getFitness()).setValue(0.4);
		
		// Check the comparison result
		this.comparator.setInverse(true); // minimization problem
		double result = this.comparator.compare(ind1.getFitness(), ind2.getFitness());
		assertTrue(result == 1.0);
		
		result = this.comparator.compare(ind2.getFitness(), ind1.getFitness());
		assertTrue(result == -1.0);
	}
}
