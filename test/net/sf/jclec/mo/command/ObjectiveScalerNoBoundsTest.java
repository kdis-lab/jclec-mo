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
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import net.sf.jclec.IIndividual;
import net.sf.jclec.binarray.BinArrayIndividual;
import net.sf.jclec.fitness.SimpleValueFitness;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;

/**
 * Tests for the command that scales the objective values
 * considering the current bounds in the population.
 * 
 * <p>HISTORY:
 * <ul>
 *   <li>(AR|JRR|SV, 1.0, March 2018)		First release.</li>
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
 * @see ObjectiveScalerNoBounds
 * */

public class ObjectiveScalerNoBoundsTest extends CommandTest {

	/* Properties */
	
	protected ObjectiveScalerNoBounds command = new ObjectiveScalerNoBounds();
	
	protected MaxObjectiveValue commandMax = new MaxObjectiveValue();
	
	protected MinObjectiveValue commandMin = new MinObjectiveValue();
	
	/* Tests */
	
	/**
	 * Check that the scaling method returns the same number of individuals.
	 * */
	@Test
	public void testBoundsAfterScalingAreCorrect(){
		
		// Execute the command
		List<IIndividual> population = createPopulationOneNonDominated();
		this.command.setPopulation(population);
		this.command.execute();
			
		// Check the minimum objective values in the resulting population
		this.commandMin.setPopulation(population);
		this.commandMin.setObjectiveIndex(0);
		this.commandMin.execute();
		double result = this.commandMin.getMinValue();
		assertTrue(result == 0.0);
		
		this.commandMin.setObjectiveIndex(1);
		this.commandMin.execute();
		result = this.commandMin.getMinValue();
		assertTrue(result == 0.0);
		
		// Check the maximum objective values in the resulting population
		this.commandMax.setPopulation(population);
		this.commandMax.setObjectiveIndex(0);
		this.commandMax.execute();
		result = this.commandMax.getMaxValue();
		assertTrue(result == 1.0);
		
		this.commandMax.setObjectiveIndex(1);
		this.commandMax.execute();
		result = this.commandMax.getMaxValue();
		assertTrue(result == 1.0);
	}

	/**
	 * Check that the result after scaling the objective values is correct.
	 * */
	@Test
	public void testResultAfterScalingValues(){
		
		// Execute the command
		List<IIndividual> population = createPopulationOneNonDominated();
		this.command.setPopulation(population);
		this.command.execute();
		
		// Check that each individual has the expected objective values
		double [][] expectedValues = new double [][]{ {1.0,1.0}, {0.2,0.2}, {0.6, 0.8}, 
				{0.4, 0.2}, {0.2, 0.4}, {0.8, 0.6}, {0.0,0.0}};

		int size = population.size();
		int nObjs = 2;
		double result;
		for(int i=0; i<size; i++){
			for(int j=0; j<nObjs; j++){
				try{
					result = ((MOFitness)population.get(i).getFitness()).getObjectiveDoubleValue(j);
					assertTrue(result == expectedValues[i][j]);
				} catch (Exception e){
					fail();
				}
			}
		}
	}

	/**
	 * If all the individual in the population have the same objective values,
	 * then all the scaled values should be equal to <code>1.0</code>.
	 * */
	@Test
	public void testResultAfterScalingValuesEqualIndividuals(){

		// Create the individual
		SimpleValueFitness [] fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(-5.0);
		fitness[1] = new SimpleValueFitness(-5.0);
		BinArrayIndividual ind = new BinArrayIndividual(new byte[]{0,0,0}, new MOFitness(fitness));
		List<IIndividual> population = new ArrayList<IIndividual>();
		population.add(ind.copy());
		population.add(ind.copy());

		// Execute the command
		this.command.setPopulation(population);
		this.command.execute();
		
		// Check the result
		double result;
		try{
			result = ((MOFitness)population.get(0).getFitness()).getObjectiveDoubleValue(0);
			assertTrue(result == 1.0);
		} catch (Exception e){
			fail();
		}
		
		try{
			result = ((MOFitness)population.get(0).getFitness()).getObjectiveDoubleValue(1);
			assertTrue(result == 1.0);
		} catch (Exception e){
			fail();
		}
	}
}
