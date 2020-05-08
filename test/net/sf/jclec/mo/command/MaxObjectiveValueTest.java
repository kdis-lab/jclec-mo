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

import java.util.ArrayList;
import java.util.List;

import net.sf.jclec.IIndividual;
import net.sf.jclec.binarray.BinArrayIndividual;
import net.sf.jclec.fitness.SimpleValueFitness;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;

/**
 * Tests for the command that calculates the maximum objective values.
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
 * @see MaxObjectiveValue
 * */

public class MaxObjectiveValueTest extends CommandTest{

	/* Properties */
	
	protected MaxObjectiveValue command = new MaxObjectiveValue();
	
	protected List<IIndividual> population = createPopulationOneNonDominated();
	
	/* Tests */
	
	/**
	 * Check that the given maximum value for each objective is correct.
	 * */
	@Test
	public void testMaximumValueOfObjectives(){
		
		// Check objective 0
		this.command.setPopulation(this.population);
		this.command.setObjectiveIndex(0);
		this.command.execute();
		double result = this.command.getMaxValue();
		assertTrue(result == 5.0);
		
		// Check objective 1
		this.command.setObjectiveIndex(1);
		this.command.execute();
		result = this.command.getMaxValue();
		assertTrue(result == 5.0);
	}
	
	/**
	 * Check that the given maximum value for each objective is correct.
	 * */
	@Test
	public void testMaximumValueOfObjectivesOneIndividual(){
		
		// Create the individual
		SimpleValueFitness [] fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(-5.0);
		fitness[1] = new SimpleValueFitness(-5.0);
		BinArrayIndividual ind = new BinArrayIndividual(new byte[]{0,0,0}, new MOFitness(fitness));
		List<IIndividual> oneInd = new ArrayList<IIndividual>();
		oneInd.add(ind.copy());
		
		// Check objective 0
		this.command.setPopulation(oneInd);
		this.command.setObjectiveIndex(0);
		this.command.execute();
		double result = this.command.getMaxValue();
		assertTrue(result == -5.0);
		
		// Check objective 1
		this.command.setObjectiveIndex(1);
		this.command.execute();
		result = this.command.getMaxValue();
		assertTrue(result == -5.0);
	}
}
