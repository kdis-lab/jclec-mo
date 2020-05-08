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

package net.sf.jclec.mo.indicator;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests for the R3 indicator.
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
 * @see R3
 * */

public class R3Test {
	
	/* Properties */

	protected R3 indicator = new R3();

	/* Tests */

	/**
	 * Check that R3(A,B) > R3(B,A)
	 * */
	@Test
	public void testEquivalentResult() {
		double [][] frontA = new double[][]{{1,10},{10,1.8}};
		double [][] frontB = new double[][]{{2.2,10},{7,-1}};
		double [] refPoint = new double[]{10,10};
		int h = 2;
		
		this.indicator.setFront(frontA);
		this.indicator.setSecondFront(frontB);
		this.indicator.setRefPoint(refPoint);
		this.indicator.setH(h);
		this.indicator.initializeWeightVectors();
		this.indicator.calculate();
		double result1 = this.indicator.getResult();

		this.indicator.setFront(frontB);
		this.indicator.setSecondFront(frontA);
		this.indicator.calculate();
		double result2 = this.indicator.getResult();
		assertTrue(result1 > result2);
	}
}
