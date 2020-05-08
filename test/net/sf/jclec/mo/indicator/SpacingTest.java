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
 * Tests for the Spacing indicator.
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
 * @see Spacing
 * 
 * */
public class SpacingTest {

	/* Properties */
	
	protected Spacing indicator = new Spacing();

	/* Tests */
	
	/**
	 * If both fronts are empty, then the result will be meaningless.
	 * */
	@Test
	public void testEmptyFrontsResultZero() {
		double [][] front = new double [][] {};
		this.indicator.setFront(front);
		this.indicator.calculate();
		double result = this.indicator.getResult();
		assertTrue(result == 0.0);
	}
	
	/**
	 * If the solutions are equally spaced in the front,
	 * then the result should be <code>0.0</code>.
	 * */
	@Test
	public void testEquallySpacedSolutionsResultZero() {
		double [][] front = new double [][] {
				{0.4,0.6},
				{0.5,0.5},
				{0.6,0.4},
		};
		this.indicator.setFront(front);
		this.indicator.calculate();
		double result = this.indicator.getResult();
		assertTrue(result == 0.0);
	}
	
	/**
	 * If the front contains only one solution, then the
	 * result should be <code>0.0</code>.
	 * */
	@Test
	public void testOneSolutionInFrontResultZero() {
		double [][] front = new double [][] {{0.2,0.3}};
		this.indicator.setFront(front);
		this.indicator.calculate();
		double result = this.indicator.getResult();
		assertTrue(result == 0.0);
	}
}
