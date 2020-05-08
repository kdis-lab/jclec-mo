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
 * Tests for the Error Ratio indicator.
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
 * @see ErrorRatio
 * */
public class ErrorRatioTest {

	/* Properties */

	protected ErrorRatio indicator = new ErrorRatio();

	/* Tests */
	
	/**
	 * If the two fronts are empty, the result is meaningless.
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
	 * If one of the two front is empty, the result is meaningless.
	 * */
	@Test
	public void testOneEmptyFrontResultInvalid() {
		double [][] front1 = new double [][] {};
		double [][] front2 = new double [][]{{0.5,0.4}};
		this.indicator.setFront(front1);
		this.indicator.setSecondFront(front2);
		this.indicator.calculate();
		double result = this.indicator.getResult();
		assertTrue(result == -1.0);
		// invert
		this.indicator.setFront(front2);
		this.indicator.setSecondFront(front1);
		this.indicator.calculate();
		result = this.indicator.getResult();
		assertTrue(result == -1.0);
	}
	
	/**
	 * If the front is the true front, then the result is <code>0</code>.
	 * */
	@Test
	public void testEqualFrontsResultEqualsZero(){
		double [][] front = new double[][]{{0.3,0.5},{0.7,0.4}};
		this.indicator.setFront(front);
		this.indicator.setSecondFront(front);
		this.indicator.calculate();
		double result = this.indicator.getResult();
		assertTrue(result == 0.0);
	}
	
	/**
	 * If the front contains some solutions of the true front, 
	 * then the result is less than <code>1.0</code>. This test uses
	 * the example given in (Coello Coello et al., 2007).
	 * */
	@Test
	public void testDifferentFrontsResultLessOne() {
		double [][] front = new double [][] {{2.5,9.0},{3.0,6.0},{5.0,4.0}};
		double [][] trueFront = new double [][]{{1.5,10.0},{2.0,8.0},{3.0,6.0},{4.0,4.0}};
		this.indicator.setFront(front);
		this.indicator.setSecondFront(trueFront);
		this.indicator.calculate();
		double result = this.indicator.getResult();
		double expected = 2.0/3.0;
		assertTrue(result < 1.0);
		assertTrue(result == expected);
	}
	
	/**
	 * If the front does not contain any solution of the true front, 
	 * then the result is <code>1.0</code>.
	 * */
	@Test
	public void testNonOverlappedFrontsResultEqualOne() {
		double [][] front = new double [][] {{2.5,9.0},{5.0,4.0}};
		double [][] trueFront = new double [][]{{1.5,10.0},{2.0,8.0},{3.0,6.0},{4.0,4.0}};
		this.indicator.setFront(front);
		this.indicator.setSecondFront(trueFront);
		this.indicator.calculate();
		double result = this.indicator.getResult();
		assertTrue(result == 1.0);
	}
}
