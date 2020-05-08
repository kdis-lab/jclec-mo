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
 * Tests for the Epsilon indicator.
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
 * @see Epsilon
 * 
 * */
public class EpsilonTest {

	/* Properties */

	protected Epsilon indicator = new Epsilon();

	/* Tests */

	/**
	 * If both fronts are empty, then the result will be meaningless.
	 * */
	@Test
	public void testEmptyFrontsResultInvalid() {
		double [][] front = new double [][] {};
		double [][] trueFront = new double [][]{};
		this.indicator.setFront(front);
		this.indicator.setSecondFront(trueFront);
		this.indicator.calculate();
		double result = this.indicator.getResult();
		assertTrue(result == -1.0);
	}

	/**
	 * If the two fronts contain the same set of solutions,
	 * the result should be equal to <code>1</code>.
	 * */
	@Test 
	public void testEqualFrontsResultEqualsOne(){
		double [][] front = new double [][] {{0.5,0.5}};
		this.indicator.setFront(front);
		this.indicator.setSecondFront(front);
		this.indicator.calculate();
		double result = this.indicator.getResult();
		assertTrue(result == 1.0);
	}
	
	/**
	 * If all the solution in the second front are dominated by
	 * a solution in the first front, then the result will be 
	 * less than <code>1</code>.
	 * */
	@Test 
	public void testResultLessThanOne(){
		double [][] front1 = new double [][] {{1.0,1.0}};
		double [][] front2 = new double [][] {{0.3,0.7},{0.6,0.1},{0.5,0.5}};
		this.indicator.setFront(front1);
		this.indicator.setSecondFront(front2);
		this.indicator.calculate();
		double result = this.indicator.getResult();
		assertTrue(result < 1.0);
	}
	
	/**
	 * If the two fronts contain solutions not dominated by the other set,
	 * <code>I(A,B)>1</code> and <code>I(B,A)>1</code>.
	 * */
	@Test 
	public void testIncomparableFrontsResultGreaterThanOne(){
		double [][] front1 = new double [][] {{0.8,0.7}};
		double [][] front2 = new double [][] {{0.6,0.9}};
		this.indicator.setFront(front1);
		this.indicator.setSecondFront(front2);
		this.indicator.calculate();
		double result1 = this.indicator.getResult();
		// invert the order
		this.indicator.setFront(front2);
		this.indicator.setSecondFront(front1);
		this.indicator.calculate();
		double result2 = this.indicator.getResult();
		assertTrue((result1 > 1.0) && (result2 > 1.0));
	}
}
