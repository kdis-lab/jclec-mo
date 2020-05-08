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


package net.sf.jclec.mo.distance;

import static org.junit.Assert.assertTrue;
import net.sf.jclec.binarray.BinArrayIndividual;
import net.sf.jclec.fitness.SimpleValueFitness;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;

import org.junit.Test;

/**
 * Tests for the Manhattan distance.
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
 * @see ManhattanDistance
 * */

public class ManhattanDistanceTest {

	/* Properties */
	
	protected ManhattanDistance distance = new ManhattanDistance();
	
	/* Tests */
	
	/**
	 * If both individuals have the same objective values, 
	 * then the result should be <code>0.0</code>.
	 * */
	@Test
	public void testEqualObjectiveValuesResultZero(){
		
		// Create two individuals
		SimpleValueFitness [] fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(0.0);
		fitness[1] = new SimpleValueFitness(1.0);
		BinArrayIndividual ind1 = new BinArrayIndividual(new byte[]{0,0,0}, new MOFitness(fitness));

		fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(0.0);
		fitness[1] = new SimpleValueFitness(1.0);
		BinArrayIndividual ind2 = new BinArrayIndividual(new byte[]{0,0,1}, new MOFitness(fitness));

		// Check the result
		double result = this.distance.distance(ind1, ind2);
		assertTrue(result == 0.0);
	}
	
	/**
	 * Check the result considering a concrete example:
	 * d({0.0,0.0},{0.0,1.0})=1.0
	 * */
	@Test
	public void testResultOne(){
		
		// Create two individuals
		SimpleValueFitness [] fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(0.0);
		fitness[1] = new SimpleValueFitness(1.0);
		BinArrayIndividual ind1 = new BinArrayIndividual(new byte[]{0,0,0}, new MOFitness(fitness));

		fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(0.0);
		fitness[1] = new SimpleValueFitness(0.0);
		BinArrayIndividual ind2 = new BinArrayIndividual(new byte[]{0,0,1}, new MOFitness(fitness));

		// Check the results
		double result = this.distance.distance(ind1, ind2);
		assertTrue(result == 1.0);
		
		result = this.distance.distance(ind2, ind1);
		assertTrue(result == 1.0);
	}
	
	/**
	 * Check that distance between (0.0,0.0) and (5.0,5.0) is greater
	 * than distance between (0.0,0.0) and (1.0,1.0).
	 * */
	@Test
	public void testDistanceGreater(){
		
		// Create three individuals
		SimpleValueFitness [] fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(0.0);
		fitness[1] = new SimpleValueFitness(0.0);
		BinArrayIndividual ind1 = new BinArrayIndividual(new byte[]{0,0,0}, new MOFitness(fitness));

		fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(5.0);
		fitness[1] = new SimpleValueFitness(5.0);
		BinArrayIndividual ind2 = new BinArrayIndividual(new byte[]{0,0,1}, new MOFitness(fitness));

		fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(1.0);
		fitness[1] = new SimpleValueFitness(1.0);
		BinArrayIndividual ind3 = new BinArrayIndividual(new byte[]{0,1,0}, new MOFitness(fitness));

		// Check the result
		double result1 = this.distance.distance(ind1, ind2);
		double result2 = this.distance.distance(ind1, ind3);
		assertTrue(result1 > result2);
	}
}
