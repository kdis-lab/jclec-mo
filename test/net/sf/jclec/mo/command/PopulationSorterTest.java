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
import static org.junit.Assert.fail;
import java.util.List;
import org.junit.Test;
import net.sf.jclec.IIndividual;
import net.sf.jclec.mo.comparator.ComparatorByObjectives;
import net.sf.jclec.mo.comparator.fcomparator.MOFitnessComparator;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;

/**
 * Tests for the command that sorts a set of individuals according to a comparison criteria.
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
 * @see PopulationSorter
 * */

public class PopulationSorterTest extends CommandTest {
	
	/* Properties */

	protected PopulationSorter command = new PopulationSorter();
	
	/* Tests */
	
	/**
	 * Check that the size of the population has not changed.
	 * */
	@Test
	public void testPopulationSizeIsCorrect(){
		List<IIndividual> population = createPopulationOneNonDominated();
		int size = population.size();
		MOFitnessComparator fcomparator = createParetoComparator();
		ComparatorByObjectives comparator = new ComparatorByObjectives(fcomparator,0);
		this.command.setComparator(comparator);
		this.command.setPopulation(population);
		this.command.execute();
		assertTrue(size == population.size());
	}
	
	/**
	 * Check that solutions are sorted according to the value of the first objective.
	 * */
	@Test
	public void testPopulationIsSorted(){
		List<IIndividual> population = createPopulationOneNonDominated();
		MOFitnessComparator fcomparator = createParetoComparator();
		ComparatorByObjectives comparator = new ComparatorByObjectives(fcomparator,0); // compare only objective 0
		this.command.setComparator(comparator);
		this.command.setPopulation(population);
		this.command.execute();
		try {
			assertTrue(((MOFitness)population.get(0).getFitness()).getObjectiveDoubleValue(0)==0.0);
			assertTrue(((MOFitness)population.get(1).getFitness()).getObjectiveDoubleValue(0)==1.0);
			assertTrue(((MOFitness)population.get(2).getFitness()).getObjectiveDoubleValue(0)==1.0);
			assertTrue(((MOFitness)population.get(3).getFitness()).getObjectiveDoubleValue(0)==2.0);
			assertTrue(((MOFitness)population.get(4).getFitness()).getObjectiveDoubleValue(0)==3.0);
			assertTrue(((MOFitness)population.get(5).getFitness()).getObjectiveDoubleValue(0)==4.0);
			assertTrue(((MOFitness)population.get(6).getFitness()).getObjectiveDoubleValue(0)==5.0);
		} catch (IllegalAccessException | IllegalArgumentException e) {
			fail();
		}
	}
	
	/**
	 * Check that solutions are sorter according to the value of the second objective
	 * but in inverse order (smaller values are preferred)
	 * */
	@Test
	public void testPopulationReverseOrder(){
		List<IIndividual> population = createPopulationOneNonDominated();
		MOFitnessComparator fcomparator = createParetoComparator();
		ComparatorByObjectives comparator = new ComparatorByObjectives(fcomparator,1); // compare only objective 1
		this.command.setComparator(comparator);
		this.command.setInverse(true); // invert the order
		this.command.setPopulation(population);
		this.command.execute();
		try {
			assertTrue(((MOFitness)population.get(0).getFitness()).getObjectiveDoubleValue(1)==5.0);
			assertTrue(((MOFitness)population.get(1).getFitness()).getObjectiveDoubleValue(1)==4.0);
			assertTrue(((MOFitness)population.get(2).getFitness()).getObjectiveDoubleValue(1)==3.0);
			assertTrue(((MOFitness)population.get(3).getFitness()).getObjectiveDoubleValue(1)==2.0);
			assertTrue(((MOFitness)population.get(4).getFitness()).getObjectiveDoubleValue(1)==1.0);
			assertTrue(((MOFitness)population.get(5).getFitness()).getObjectiveDoubleValue(1)==1.0);
			assertTrue(((MOFitness)population.get(6).getFitness()).getObjectiveDoubleValue(1)==0.0);
		} catch (IllegalAccessException | IllegalArgumentException e) {
			fail();
		}
	}
}
