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

package net.sf.jclec.mo.command;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Tests for the command that generates uniform vectors for multi-objective
 * problems. The tests in this class check the combinations of number of
 * objectives and H parameter used in MOEA/D paper.
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
 * @see UniformVectorGenerator
 * */

public class UniformVectorGeneratorTest extends CommandTest{

	/* Properties */
	protected UniformVectorGenerator command = new UniformVectorGenerator();
	
	/* Tests */
	/**
	 * Check that for a problem with 2 objectives and H=149,
	 * the number of vectors is 150.
	 * */
	@Test
	public void testNumberVectors2Objectives(){
		this.command.setNumberOfObjectives(2);
		this.command.setH(149);
		this.command.execute();
		assertTrue(this.command.getUniformVectors().size()==150);
	}
	
	/**
	 * Check that for a problem with 3 objectives and H=25,
	 * the number of vectors is 150.
	 * */
	@Test
	public void testNumberVectors3Objectives(){
		this.command.setNumberOfObjectives(3);
		this.command.setH(25);
		this.command.execute();
		assertTrue(this.command.getUniformVectors().size()==351);
	}
	
	/**
	 * Check that for a problem with 4 objectives and H=12,
	 * the number of vectors is 455.
	 * */
	@Test
	public void testNumberVectors4Objectives(){
		this.command.setNumberOfObjectives(4);
		this.command.setH(12);
		this.command.execute();
		assertTrue(this.command.getUniformVectors().size()==455);
	}
}
