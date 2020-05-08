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
 * Tests for the ONVG indicator.
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
 * @see ONVG
 * */
public class ONVGTest {

	/* Properties */

	protected ONVG indicator = new ONVG();

	/* Tests */

	/**
	 * If the front is empty, then the result
	 * should be equal to <code>0</code>.
	 * */
	@Test
	public void testEmptyFrontResultEqualsZero() {
		// Create an empty Pareto set
		double [][] front = new double [][] {};
		this.indicator.setFront(front);
		this.indicator.calculate();
		double result = this.indicator.getResult();
		assertTrue(result == 0.0);
	}

	/**
	 * If the front contains <code>n</code> solutions,
	 * then the result should be <code>n</code>.
	 * */
	@Test
	public void testResultEqualSize() {
		// Create a Pareto set with 5 solutions
		double [][] front = new double [][] {
				{0.1,0.9},
				{0.2,0.8},
				{0.3,0.7},
				{0.4,0.6},
				{0.5,0.5}};
		this.indicator.setFront(front);
		this.indicator.calculate();
		double actual = this.indicator.getResult();
		double expected = 5.0;
		assertTrue(actual == expected);
	}
	
	/**
	 * If the front is not empty, then the result
	 * should be greater than <code>0.0</code>.
	 * */
	@Test
	public void testNotEmptyFrontResultGreaterThanOne() {
		double [][] front = new double [][] {{0.5,0.5}};
		this.indicator.setFront(front);
		this.indicator.calculate();
		double result = this.indicator.getResult();
		assertTrue(result > 0.0);
	}
}
