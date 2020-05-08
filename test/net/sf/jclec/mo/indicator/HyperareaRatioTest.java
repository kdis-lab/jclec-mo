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
 * Tests for the Hyperarea Ratio indicator.
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
 * @see HyperareaRatio
 * */
public class HyperareaRatioTest {

	/* Properties */

	protected HyperareaRatio indicator = new HyperareaRatio();

	/* Tests */

	/**
	 * If the front is the true front, then the result 
	 * is equal to <code>1.0</code>.
	 * */
	@Test
	public void testEqualFrontsResultEqualsOne() {
		double [][] front = new double [][] {{0.4,0.5,0.7},{0.8,0.3,0.7},{0.6,0.7,0.1}};
		this.indicator.setFront(front);
		this.indicator.setSecondFront(front);
		this.indicator.calculate();
		double result = this.indicator.getResult();
		assertTrue(result == 1.0);
	}
	
	/**
	 * Check that the result is correct using the example given in Coello's book.
	 * */
	@Test
	public void testResultIsCorrect() {
		double [][] front = new double [][] {{0.25,0.9},{0.3,0.6},{0.5,0.4}};
		double [][] trueFront = new double [][]{{0.15,1.0},{0.2,0.8},{0.3,0.6},{0.4,0.4}};
		this.indicator.setFront(front);
		this.indicator.setSecondFront(trueFront);
		this.indicator.calculate();
		double result = this.indicator.getResult();
		assertTrue(Math.abs(result-1.155)<0.001);
	}
	
	/**
	 * If the front is empty, then the result 
	 * is equal to <code>0.0</code>.
	 * */
	@Test
	public void testEmptyFrontResultInvalid() {
		double [][] front = new double [][] {};
		double [][] trueFront = new double [][]{{0.15,1.0},{0.2,0.8},{0.3,0.6},{0.4,0.4}};
		this.indicator.setFront(front);
		this.indicator.setSecondFront(trueFront);
		this.indicator.calculate();
		double result = this.indicator.getResult();
		assertTrue(result == -1.0);
	}
	
	/**
	 * If both fronts are empty, then the result 
	 * is equal to <code>Double.NaN</code>.
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
}