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
import net.sf.jclec.binarray.BinArrayIndividual;
import net.sf.jclec.fitness.SimpleValueFitness;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;
import net.sf.jclec.mo.evaluation.fitness.NSGA2MOFitness;

import org.junit.Test;

/**
 * Tests for the comparator based on the crowding
 * distance used in NSGA-II.
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
 * @see CrowdingDistanceComparator
 * */

public class CrowdingDistanceComparatorTest {

	/* Properties */
	
	protected CrowdingDistanceComparator comparator = new CrowdingDistanceComparator();
		
	/* Tests */

	/**
	 * If the first individual has a greater crowding distance than the second individual,
	 * then the first individual is preferred.
	 * */
	@Test
	public void testGreaterCrowdingDistanceIsBetter(){

		// Create two individuals
		SimpleValueFitness [] fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(1.0);
		fitness[1] = new SimpleValueFitness(1.0);
		BinArrayIndividual ind1 = new BinArrayIndividual(new byte[]{0,0,0}, new NSGA2MOFitness(new MOFitness(fitness)));
		((NSGA2MOFitness)ind1.getFitness()).setCrowdingDistance(2.0);
		
		fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(5.0);
		fitness[1] = new SimpleValueFitness(5.0);
		BinArrayIndividual ind2 = new BinArrayIndividual(new byte[]{0,0,0}, new NSGA2MOFitness(new MOFitness(fitness)));
		((NSGA2MOFitness)ind2.getFitness()).setCrowdingDistance(1.0);
		
		// Check the comparison results
		double result = this.comparator.compare(ind1, ind2);
		assertTrue(result == 1.0);
		
		result = this.comparator.compare(ind2, ind1);
		assertTrue(result == -1.0);
	}
	
	/**
	 * If both individuals have the same crowding distance,
	 * then they are equivalent regardless the objective values.
	 * */
	@Test
	public void testEqualCrowdingDistanceResultZero(){

		// Create two individuals
		SimpleValueFitness [] fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(1.0);
		fitness[1] = new SimpleValueFitness(1.0);
		BinArrayIndividual ind1 = new BinArrayIndividual(new byte[]{0,0,0}, new NSGA2MOFitness(new MOFitness(fitness)));
		((NSGA2MOFitness)ind1.getFitness()).setCrowdingDistance(1.0);
		
		fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(5.0);
		fitness[1] = new SimpleValueFitness(5.0);
		BinArrayIndividual ind2 = new BinArrayIndividual(new byte[]{0,0,0}, new NSGA2MOFitness(new MOFitness(fitness)));
		((NSGA2MOFitness)ind2.getFitness()).setCrowdingDistance(1.0);
		
		// Check the comparison results
		double result = this.comparator.compare(ind1, ind2);
		assertTrue(result == 0.0);
		
		result = this.comparator.compare(ind2, ind1);
		assertTrue(result == 0.0);
	}
}
