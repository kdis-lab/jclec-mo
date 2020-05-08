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

import java.util.List;

import net.sf.jclec.IIndividual;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;

/**
 * Tests for the command that scales the objective values.
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
 * @see ObjectiveScaler
 * */

public class ObjectiveScalerTest extends CommandTest {
	
	/* Properties */
	
	protected ObjectiveScaler commandScaler = new ObjectiveScaler();
	
	protected MaxObjectiveValue commandMax = new MaxObjectiveValue();
	
	protected MinObjectiveValue commandMin = new MinObjectiveValue();
	
	/* Tests */
	
	/**
	 * Check that the scaling method returns the same number of individuals.
	 * */
	@Test
	public void testBoundsAfterScalingAreCorrect(){
		
		// Create the required variables
		List<IIndividual> population = createPopulationOneNonDominated();
		double [] min = new double[]{0.0,0.0};
		double [] max = new double[]{10.0, 10.0};
		
		// Configure and execute the command
		this.commandScaler.setPopulation(population);
		this.commandScaler.setMinValues(min);
		this.commandScaler.setMaxValues(max);
		this.commandScaler.execute();
		
		// Check the minimum objective values in the resulting population
		this.commandMin.setPopulation(population);
		this.commandMin.setObjectiveIndex(0);
		this.commandMin.execute();
		double result = this.commandMin.getMinValue();
		assertTrue(result >= 0.0);
		
		this.commandMin.setObjectiveIndex(1);
		this.commandMin.execute();
		result = this.commandMin.getMinValue();
		assertTrue(result >= 0.0);
		
		// Check the maximum objective values in the resulting population
		this.commandMax.setPopulation(population);
		this.commandMax.setObjectiveIndex(0);
		this.commandMax.execute();
		result = this.commandMax.getMaxValue();
		assertTrue(result <= 10.0);
		
		this.commandMax.setObjectiveIndex(1);
		this.commandMax.execute();
		result = this.commandMax.getMaxValue();
		assertTrue(result <= 10.0);
	}

	/**
	 * Check that the result after scaling the objective values is correct.
	 * */
	@Test
	public void testResultAfterScalingValues(){
		
		// Create the required variables
		List<IIndividual> population = createPopulationOneNonDominated();
		double [] min = new double[]{0.0,0.0};
		double [] max = new double[]{10.0, 10.0};
		
		// Configure and execute the command
		this.commandScaler.setPopulation(population);
		this.commandScaler.setMinValues(min);
		this.commandScaler.setMaxValues(max);
		this.commandScaler.execute();
		
		// Check that each individual has the expected objective values
		double [][] expectedValues = new double [][]{ {0.5,0.5}, {0.1,0.1}, {0.3, 0.4}, 
				{0.2, 0.1}, {0.1, 0.2}, {0.4, 0.3}, {0.0,0.0}};

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
}
