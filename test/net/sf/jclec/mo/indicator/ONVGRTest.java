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
 * <li>(AR|JRR|SV, 1.0, March 2018)		First release.</li>
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
 * @see ONVGR
 * 
 * */
public class ONVGRTest {

	/* Properties */

	protected ONVGR indicator = new ONVGR();

	/* Tests */

	/**
	 * If the first front is empty, then the
	 * result will be equal to <code>0.0</code>.
	 * */
	@Test
	public void testEmptyFrontResultEqualsZero(){
		// Empty fronts
		double [][] front = new double [][] {};
		double [][] trueFront = new double [][] {{0.5,0.5}};
		this.indicator.setFront(front);
		this.indicator.setSecondFront(trueFront);
		this.indicator.calculate();
		double result = this.indicator.getResult();
		assertTrue(result == 0.0);
	}
	
	/**
	 * If the first front contains the half part of solutions than
	 * the second front, the result should be <code>0.5</code>.
	 * */
	@Test
	public void testHalfSolutionsResultCorrect() {
		// Create a Pareto set with 5 solutions
		double [][] front = new double [][] {
				{0.1,0.9},
				{0.2,0.8},
				{0.3,0.7},
				{0.4,0.6},
				{0.5,0.5}};
		// True Pareto set with 10 solutions
		double [][] trueFront = new double [][] {
				{1.0, 0.0},
				{0.9, 0.1},
				{0.8, 0.2},
				{0.7, 0.3},
				{0.6, 0.4},
				{0.5, 0.5},
				{0.4, 0.6},
				{0.3, 0.7},
				{0.2, 0.8},
				{0.1, 0.9}};
		this.indicator.setFront(front);
		this.indicator.setSecondFront(trueFront);
		this.indicator.calculate();
		double actual = this.indicator.getResult();
		double expected = 0.5;
		assertTrue(actual == expected);
	}
	
	/**
	 * If the first front contains more solutions
	 * than the second front, then the result is
	 * greater than <code>1.0</code>.
	 * */
	@Test
	public void testResultGreaterZero() {
		// Create a Pareto set with 4 solutions
		double [][] front = new double [][] {
				{0.8,0.2},
				{0.1,0.9},
				{0.3,0.7},
				{0.4,0.8},
				{0.5,0.5}};
		// True Pareto set with 3 solutions
		double [][] trueFront = new double [][] {
				{1.0, 0.0},
				{0.5, 0.5},
				{1.0, 1.0}};
		this.indicator.setFront(front);
		this.indicator.setSecondFront(trueFront);
		this.indicator.calculate();
		double result = this.indicator.getResult();
		assertTrue((result >= 0.0));
	}
	
	/**
	 * If the two fronts contain the same
	 * number of solutions, the result should be <code>1.0</code>.
	 * */
	@Test
	public void testSameFrontSizeResultEqualsOne(){
		// Empty fronts
		double [][] front = new double [][] {{0.5,0.5}};
		double [][] trueFront = new double [][]{{1.0,1.0}};
		this.indicator.setFront(front);
		this.indicator.setSecondFront(trueFront);
		this.indicator.calculate();
		double result = this.indicator.getResult();
		assertTrue(result == 1.0);
	}
	
	/**
	 * If the two fronts are empty, then the
	 * indicator returns <code>-1.0</code>
	 * */
	@Test
	public void testResultIsInvalid(){
		// Empty fronts
		double [][] front = new double [][] {};
		double [][] trueFront = new double [][] {};
		this.indicator.setFront(front);
		this.indicator.setSecondFront(trueFront);
		this.indicator.calculate();
		double result = this.indicator.getResult();
		assertTrue(result == -1.0);
	}
}