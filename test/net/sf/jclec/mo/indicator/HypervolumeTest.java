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
 * Tests for the Hypervolume indicator.
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
 * @see Hypervolume
 * 
 * */
public class HypervolumeTest {

	/* Properties */
	
	protected Hypervolume indicator = new Hypervolume();

	/* Tests */
	
	/**
	 * If both fronts are empty, then the result will be meaningless.
	 * */
	@Test
	public void testEmptyFrontsResultInvalid() {
		double [][] front = new double [][] {};
		this.indicator.setFront(front);
		this.indicator.calculate();
		double result = this.indicator.getResult();
		assertTrue(result == -1.0);
	}
	
	/**
	 * If the unique solution in the front is the origin,
	 * then the result will be equal to 0.0.
	 * */
	@Test
	public void testOriginFrontResultEqualsZero() {
		// The front contains the origin point
		double [][] front = new double [][] {{0.0, 0.0, 0.0}};
		this.indicator.setFront(front);
		this.indicator.calculate();
		double result = this.indicator.getResult();
		assertTrue(result == 0.0);
	}

	/**
	 * If the unique solution in the front is the ideal (utopian) point,
	 * then the result will be equal to <code>1.0</code>.
	 * */
	@Test
	public void testIdealFrontResultEqualsOne() {
		// The front contains the ideal point
		double [][] front = new double [][] {{1.0, 1.0, 1.0}};
		this.indicator.setFront(front);
		this.indicator.calculate();
		double result = this.indicator.getResult();
		assertTrue(result == 1.0);
	}
	
	/**
	 * If all the solutions in the front are points belonging
	 * to any axis, then the result will be equal to 0.0.
	 * */
	@Test
	public void testAxisFrontResultEqualsZero() {
		// The front contains solutions in each axis
		double [][] front = new double [][] {
				{1.0, 0.0, 0.0, 0.0},
				{0.0, 1.0, 0.0, 0.0},
				{0.0, 0.0, 1.0, 0.0},
				{0.0, 0.0, 0.0, 1.0}};
		this.indicator.setFront(front);
		this.indicator.calculate();
		double result = this.indicator.getResult();
		assertTrue(result == 0.0);
	}
	
	/**
	 * If the solutions in the front are defined in the
	 * range [0,1], the hypervolume also varies in [0,1].
	 * */
	@Test
	public void testResultInRange(){
		double [][] front = new double [][] {
				{1.0, 0.0},
				{0.9, 0.1},
				{0.8, 0.2},
				{0.7, 0.3},
				{0.6, 0.4},
				{0.5, 0.5},
				{0.4, 0.6},
				{0.3, 0.7},
				{0.2, 0.8},
				{0.1, 0.9},
				{0.0, 1.0}};
		this.indicator.setFront(front);
		this.indicator.calculate();
		double result = this.indicator.getResult();
		assertTrue((result >= 0.0) && (result <= 1.0));
	}
	
	/**
	 * Considering two fronts, if the first front
	 * contains non-dominated solutions and the second
	 * front contains dominated solutions, then the
	 * hypervolume for the first front should be greater
	 * than the hypervolume in the second front.
	 * */
	@Test
	public void testNonDominatedFrontResultGreater(){
		double [][] front1 = new double[][]{
				{0.4,0.6},
				{0.5,0.5},
				{0.6,0.4}
		};
		double [][] front2 = new double[][]{
				{0.3, 0.2},
				{0.2, 0.3}
		};
		
		this.indicator.setFront(front1);
		this.indicator.calculate();
		double result1 = this.indicator.getResult();
		
		this.indicator.setFront(front2);
		this.indicator.calculate();
		double result2 = this.indicator.getResult();
		
		assertTrue(result1 > result2);
	}
}