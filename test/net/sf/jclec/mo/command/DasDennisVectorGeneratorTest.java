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
 * Tests for the command that reference vectors for multi-objective
 * problems. The tests in this class check the combinations of number of
 * objectives and number of divisions described in NSGA-III paper.
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
 * @see DasDennisVectorGenerator
 * */

public class DasDennisVectorGeneratorTest extends CommandTest{

	/* Properties */
	protected DasDennisVectorGenerator command = new DasDennisVectorGenerator();
	
	/* Tests */
	
	/**
	 * Check that for a problem with 3 objectives and 1
	 * layer with 12 divisions, the number of reference
	 * points should be equal to 91.
	 * */
	@Test
	public void testNumberVectors3ObjectivesOneLayer(){
		this.command.setNumberOfObjectives(3);
		this.command.setP1(12);
		this.command.setP2(-1);
		this.command.execute();
		assertTrue(this.command.getUniformVectors().size()==91);
	}
	
	/**
	 * Check that for a problem with 5 objectives and 1
	 * layer with 6 divisions, the number of reference
	 * points should be equal to 210.
	 * */
	@Test
	public void testNumberVectors5ObjectivesOneLayer(){
		this.command.setNumberOfObjectives(5);
		this.command.setP1(6);
		this.command.setP2(-1);
		this.command.execute();
		assertTrue(this.command.getUniformVectors().size()==210);
	}
	
	/**
	 * Check that for a problem with 3 objectives and 2
	 * layers (p1=2, p2=1), the number of reference points
	 * should be equal to 9.
	 * */
	@Test
	public void testNumberVectors3ObjectivesTwoLayers(){
		this.command.setNumberOfObjectives(3);
		this.command.setP1(2);
		this.command.setP2(1);
		this.command.execute();
		assertTrue(this.command.getUniformVectors().size()==9);
	}
	
	/**
	 * Check that for a problem with 8 objectives and two
	 * layers (p1=3,p2=2), the number of reference points
	 * should be equal to 156.
	 * */
	@Test
	public void testNumberVectors8ObjectivesTwoLayers(){
		this.command.setNumberOfObjectives(8);
		this.command.setP1(3);
		this.command.setP2(2);
		this.command.execute();
		assertTrue(this.command.getUniformVectors().size()==156);
	}
}
