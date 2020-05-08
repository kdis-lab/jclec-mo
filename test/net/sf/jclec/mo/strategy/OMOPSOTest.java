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


package net.sf.jclec.mo.strategy;

import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.List;
import net.sf.jclec.IIndividual;
import net.sf.jclec.fitness.SimpleValueFitness;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;
import net.sf.jclec.mo.evaluation.fitness.OMOPSOMOFitness;
import net.sf.jclec.pso.Particle;
import org.junit.Test;

/**
 * Tests for the OMOPSO strategy.
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
 * @see OMOPSO
 * */

public class OMOPSOTest extends MOStrategyTest {

	/* Properties */

	protected OMOPSO omopsoStrategy = new OMOPSO();

	/* Tests */

	/**
	 * Test that the initial archive does not exceed
	 * the maximum size. 
	 * */
	@Test
	public void testInitializeResultSizeSmaller(){

		// Configure the strategy
		List<IIndividual> swarm = createSwarm();
		MOStrategyContext context = createContext();
		this.omopsoStrategy.setContext(context);
		this.omopsoStrategy.createSolutionComparator(createComparatorOfObjectives());
		int maxSize = 2;
		this.omopsoStrategy.setArchiveMaxSize(maxSize);
		double [] epsilon = new double[]{0.05,0.05};
		this.omopsoStrategy.setEpsilonValues(epsilon);

		// Execute the initialize method
		List<IIndividual> result = this.omopsoStrategy.initialize(swarm);
		assertTrue(result.size() <= maxSize);
	}

	/**
	 * Test that the initial archive does not exceed
	 * the maximum size. 
	 * */
	@Test
	public void testInitializeResultSize(){

		// Configure the strategy
		List<IIndividual> swarm = createSwarm();
		MOStrategyContext context = createContext();
		this.omopsoStrategy.setContext(context);
		this.omopsoStrategy.createSolutionComparator(createComparatorOfObjectives());
		int maxSize = 2;
		this.omopsoStrategy.setArchiveMaxSize(maxSize);
		double [] epsilon = new double[]{0.05,0.05};
		this.omopsoStrategy.setEpsilonValues(epsilon);

		// Execute the initialize method
		List<IIndividual> result = this.omopsoStrategy.initialize(swarm);
		assertTrue(result.size() == 1);
	}

	/**
	 * Test that the number of individuals selected
	 * as parents is smaller or equal to the population size.
	 * */
	@Test
	public void testMatingSelectionResultSize(){

		// Create the population and configure the strategy
		List<IIndividual> swarm = createSwarm();
		MOStrategyContext context = createContext();
		this.omopsoStrategy.setContext(context);
		this.omopsoStrategy.createSolutionComparator(createComparatorOfObjectives());
		int maxSize = 2;
		this.omopsoStrategy.setArchiveMaxSize(maxSize);
		double [] epsilon = new double[]{0.05,0.05};
		this.omopsoStrategy.setEpsilonValues(epsilon);
		List<IIndividual> archive = this.omopsoStrategy.initialize(swarm);

		// Execute the mating selection
		int size = this.omopsoStrategy.matingSelection(swarm, archive).size();
		assertTrue(size == context.getPopulationSize());
	}

	/**
	 * Test that the size of the set of surivors is correct.
	 * */
	@Test
	public void testEnvironmentalSelectionResultSize(){
		List<IIndividual> offspring = createOffspring();
		List<IIndividual> result = this.omopsoStrategy.environmentalSelection(null, offspring, null);
		assertTrue(result.size() == offspring.size());
	}
	
	/**
	 * Test that the result after updating the archive is null.
	 * */
	@Test
	public void testArchiveUpdateResultSize(){
		// Create the population and configure the strategy
		List<IIndividual> swarm = createSwarm();
		List<IIndividual> offspring = createOffspring();
		MOStrategyContext context = createContext();
		int maxSize = 2;
		this.omopsoStrategy.setArchiveMaxSize(maxSize);
		this.omopsoStrategy.setContext(context);
		this.omopsoStrategy.createSolutionComparator(createComparatorOfObjectives());
		double [] epsilon = new double[]{0.05,0.05};
		this.omopsoStrategy.setEpsilonValues(epsilon);
		
		// Initialize and update the archive
		List<IIndividual> archive = this.omopsoStrategy.initialize(swarm);
		List<IIndividual> result = this.omopsoStrategy.updateArchive(swarm, offspring, archive);
		assertTrue(result.size() > 0 && result.size() <= maxSize);
	}
	
	/**
	 * Create a population of individuals.
	 * @return List containing the individuals.
	 * */
	protected List<IIndividual> createSwarm(){
		List<IIndividual> swarm = new ArrayList<IIndividual>();

		// Create some individuals, assuming the objective values
		SimpleValueFitness [] fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(5.0);
		fitness[1] = new SimpleValueFitness(5.0);
		Particle particle = new Particle(new double[]{0,0,0}, new OMOPSOMOFitness(new MOFitness(fitness)));
		swarm.add(particle.copy());

		fitness[0] = new SimpleValueFitness(1.0);
		fitness[1] = new SimpleValueFitness(1.0);
		particle = new Particle(new double[]{0,0,1}, new OMOPSOMOFitness(new MOFitness(fitness)));
		swarm.add(particle.copy());

		fitness[0] = new SimpleValueFitness(3.0);
		fitness[1] = new SimpleValueFitness(4.0);
		particle = new Particle(new double[]{0,1,0}, new OMOPSOMOFitness(new MOFitness(fitness)));
		swarm.add(particle.copy());

		fitness[0] = new SimpleValueFitness(2.0);
		fitness[1] = new SimpleValueFitness(1.0);
		particle = new Particle(new double[]{0,1,1}, new OMOPSOMOFitness(new MOFitness(fitness)));
		swarm.add(particle.copy());

		fitness[0] = new SimpleValueFitness(1.0);
		fitness[1] = new SimpleValueFitness(2.0);
		particle = new Particle(new double[]{1,0,0}, new OMOPSOMOFitness(new MOFitness(fitness)));
		swarm.add(particle.copy());

		fitness[0] = new SimpleValueFitness(4.0);
		fitness[1] = new SimpleValueFitness(3.0);
		particle = new Particle(new double[]{1,0,1}, new OMOPSOMOFitness(new MOFitness(fitness)));
		swarm.add(particle.copy());

		return swarm;
	}
	
	/**
	 * Create a set of possible offspring.
	 * @return List containing the new swarm.
	 * */
	protected List<IIndividual> createOffspring(){

		// Create some individuals, assuming the objective values
		List<IIndividual> offspring = new ArrayList<IIndividual>();

		SimpleValueFitness [] fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(5.0);
		fitness[1] = new SimpleValueFitness(4.0);
		Particle particle = new Particle(new double[]{0,1,0}, new OMOPSOMOFitness(new MOFitness(fitness)));
		offspring.add(particle.copy());

		fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(4.0);
		fitness[1] = new SimpleValueFitness(5.0);
		particle = new Particle(new double[]{1,0,1}, new OMOPSOMOFitness(new MOFitness(fitness)));
		offspring.add(particle.copy());

		return offspring;
	}
}
