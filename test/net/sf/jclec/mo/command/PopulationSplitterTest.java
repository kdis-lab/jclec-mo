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

import java.util.List;

import net.sf.jclec.IIndividual;

/**
 * Tests for the command that splits the populations according
 * to a given comparator.
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
 * @see PopulationSplitter
 * */
public class PopulationSplitterTest extends CommandTest {

	/* Properties */
	
	protected PopulationSplitter command = 
			new PopulationSplitter(createPopulationThreeNonDominated(), createIndividualsComparator());
	
	/* Tests */
	
	/**
	 * Check that the number of fronts and the number of solutions
	 * within each front are correct.
	 * */
	@Test
	public void testSplitIntoFrontSizeIsCorrect(){
		
		// Execute the command
		this.command.execute();
		List<List<IIndividual>> populationByFronts = this.command.getSplitPopulation();
		
		// The number of fronts should be 5
		assertTrue(populationByFronts.size() == 5);
		
		// Check the size of each front
		assertTrue(populationByFronts.get(0).size() == 3); 	// {5.0, 5.0}, {4.0, 6.0}, {6.0, 4.0}
		assertTrue(populationByFronts.get(1).size() == 2);	// {4.0, 3.0}, {3.0, 4.0}
		assertTrue(populationByFronts.get(2).size() == 2); 	// {2.0, 1.0}, {1.0, 2.0}
		assertTrue(populationByFronts.get(3).size() == 1);	// {1.0, 1.0}
		assertTrue(populationByFronts.get(4).size() == 1);	// {0.0, 0.0}
	}
}
