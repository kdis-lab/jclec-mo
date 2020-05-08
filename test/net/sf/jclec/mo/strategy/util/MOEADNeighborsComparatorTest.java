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


package net.sf.jclec.mo.strategy.util;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests for the MOEA/D neighbor comparator.
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
 * @see MOEADNeighborsComparator
 * */
public class MOEADNeighborsComparatorTest {

	/* Properties */

	protected MOEADNeighborsComparator comparator = new MOEADNeighborsComparator();

	/* Tests */

	/**
	 * If the first neighbor has a greater distance than the second neighbor, 
	 * then the first neighbor is preferred.
	 * */
	@Test
	public void testFirstNeighborBetterThanSecondNeighbor(){
		MOEADNeighbor neighbor1 = new MOEADNeighbor(0, 2.0);
		MOEADNeighbor neighbor2 = new MOEADNeighbor(0, 1.0);
		
		// Check the comparison result
		double result = this.comparator.compare(neighbor1, neighbor2);
		assertTrue(result == 1.0);
		result = this.comparator.compare(neighbor2, neighbor1);
		assertTrue(result == -1.0);
	}

	/**
	 * If the two neighbors have the same distance,
	 * then the result should be <code>0.0</code>.
	 * */
	@Test
	public void testEqualDistancesResultZero(){
		MOEADNeighbor neighbor1 = new MOEADNeighbor(0, 2.0);
		MOEADNeighbor neighbor2 = new MOEADNeighbor(0, 2.0);
		
		// Check the comparison result
		double result = this.comparator.compare(neighbor1, neighbor2);
		assertTrue(result == 0.0);
		result = this.comparator.compare(neighbor2, neighbor1);
		assertTrue(result == 0.0);
	}
}
