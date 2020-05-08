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
 * Tests for the Maximum Error indicator.
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
 * @see MaximumError
 * 
 * */
public class MaximumErrorTest {

	/* Properties */
	
	protected MaximumError indicator = new MaximumError();

	/* Tests */
	
	/**
	 * If both fronts are empty, then the result will be meaningless.
	 * */
	@Test
	public void testEmptyFrontResultInvalid() {
		double [][] front = new double [][] {};
		double [][] trueFront = new double [][]{{0.5,0.5}};
		this.indicator.setFront(front);
		this.indicator.setSecondFront(trueFront);
		this.indicator.calculate();
		double result = this.indicator.getResult();
		assertTrue(result == -1.0);
	}
	
	/**
	 * If the front only contains solutions of the true front,
	 * then the result should be equal to <code>0.0</code>.
	 * */
	@Test
	public void testFrontInTrueFrontResultZero() {
		double [][] front = new double [][] {{0.4,0.6}};
		double [][] trueFront = new double [][]{{0.6,0.4},{0.5,0.5},{0.4,0.6}};
		this.indicator.setFront(front);
		this.indicator.setSecondFront(trueFront);
		this.indicator.calculate();
		double result = this.indicator.getResult();
		assertTrue(result == 0.0);
	}
	
	/**
	 * If the front does not contain any solution of the true front,
	 * then the result should be greater than <code>0.0</code>.
	 * */
	@Test
	public void testFrontNotInTrueFrontResultGreaterZero() {
		double [][] front = new double [][] {{0.3,0.3}};
		double [][] trueFront = new double [][]{{0.6,0.4},{0.5,0.5},{0.4,0.6}};
		this.indicator.setFront(front);
		this.indicator.setSecondFront(trueFront);
		this.indicator.calculate();
		double result = this.indicator.getResult();
		assertTrue(result > 0.0);
	}
	
	/**
	 * If the front contain some solutions of the true front and some
	 * solutions that are not in the true front,
	 * then the result should be greater than <code>0.0</code>.
	 * */
	@Test
	public void testFrontPartiallyInTrueFrontResultGreaterZero() {
		double [][] front = new double [][] {{0.3,0.3},{0.5,0.5}};
		double [][] trueFront = new double [][]{{0.6,0.4},{0.5,0.5},{0.4,0.6}};
		this.indicator.setFront(front);
		this.indicator.setSecondFront(trueFront);
		this.indicator.calculate();
		double result = this.indicator.getResult();
		assertTrue(result > 0.0);
	}
	
	/**
	 * Check the result with the example given in
	 * (Coello Coello et al., 2007).
	 * */
	@Test
	public void testOverlappedFrontResultCorrect() {
		double [][] trueFront = new double [][] {
				{1.5, 10.0},
				{2.0, 8.0},
				{3.0, 6.0},
				{4.0, 4.0}
		};
		double [][] front = new double [][] {
				{2.5, 9.0},
				{3.0, 6.0},
				{5.0, 4.0}
		};
		this.indicator.setFront(front);
		this.indicator.setSecondFront(trueFront);
		this.indicator.calculate();
		double result = this.indicator.getResult();
		assertTrue(Math.abs(result - 1.118) < 0.0001);
	}
}
