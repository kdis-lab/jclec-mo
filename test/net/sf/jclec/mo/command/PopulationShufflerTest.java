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

import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import net.sf.jclec.IIndividual;
import net.sf.jclec.util.random.RanecuFactory;

/**
 * Tests for the command that shuffles a set of solutions.
 * 
 * <p>HISTORY:
 * <ul>
 *  <li>(AR|JRR|SV, 1.0, March 2018)		First release.</li>
 * </ul>
 * </p>
 * 
 * @version 1.0
 * 
 * @author Aurora Ramirez (AR)
 * @author Jose Raul Romero (JRR)
 * @author Sebastian Ventura (SV)
 * 
 * <p>Knowledge Discovery and Intelligent Systems (KDIS) Research Group: 
 * {@link http://www.uco.es/grupos/kdis}</p>
 *  
 * @see PopulationShuffler
 * */

public class PopulationShufflerTest extends CommandTest {
	
	/* Properties */
	
	protected PopulationShuffler command = new PopulationShuffler();
	
	protected RanecuFactory randgen = new RanecuFactory();
	
	/* Tests */

	/**
	 * Check that the size of the population has not changed.
	 * */
	@Test
	public void testPopulationSizeIsCorrect(){
		List<IIndividual> population = createPopulationInRange01();
		int size = population.size();
		this.randgen.setSeed(123);
		this.command.setRandGen(this.randgen.createRandGen());
		this.command.setPopulation(population);
		this.command.execute();
		assertTrue(size == population.size());
	}
	
	/**
	 * Check that all the individuals of the population remains in
	 * the population after shuffle it.
	 * */
	@Test
	public void testSolutionsInPopulation(){
		List<IIndividual> population = createPopulationInRange01();
		List<IIndividual> copy = new ArrayList<IIndividual>();
		for(IIndividual ind: population){
			copy.add(ind.copy());
		}
		this.randgen.setSeed(123);
		this.command.setRandGen(this.randgen.createRandGen());
		this.command.setPopulation(population);
		this.command.execute();
		for(IIndividual ind: copy){
			assertTrue(population.contains(ind));
		}
	}
}