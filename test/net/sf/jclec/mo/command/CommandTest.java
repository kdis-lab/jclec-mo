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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.sf.jclec.IFitness;
import net.sf.jclec.IIndividual;
import net.sf.jclec.binarray.BinArrayConstrainedIndividual;
import net.sf.jclec.binarray.BinArrayIndividual;
import net.sf.jclec.fitness.SimpleValueFitness;
import net.sf.jclec.fitness.ValueFitnessComparator;
import net.sf.jclec.mo.comparator.MOSolutionComparator;
import net.sf.jclec.mo.comparator.fcomparator.ParetoComparator;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;
import net.sf.jclec.mo.evaluation.fitness.NSGA2MOFitness;

/**
 * Auxiliary methods to test commands (utilities).
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
 * */

public class CommandTest {

	/* Auxiliary methods */
	
	/**
	 * Create a population of individuals with only one non dominated solution.
	 * @return List containing the individuals.
	 * */
	protected List<IIndividual> createPopulationOneNonDominated(){
		List<IIndividual> population = new ArrayList<IIndividual>();
		
		// Create some individuals, assuming the objective values
		SimpleValueFitness [] fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(5.0);
		fitness[1] = new SimpleValueFitness(5.0);
		BinArrayIndividual ind = new BinArrayIndividual(new byte[]{0,0,0}, new MOFitness(fitness));
		population.add(ind.copy());
		
		fitness[0] = new SimpleValueFitness(1.0);
		fitness[1] = new SimpleValueFitness(1.0);
		ind = new BinArrayIndividual(new byte[]{0,0,1}, new MOFitness(fitness));
		population.add(ind.copy());
		
		fitness[0] = new SimpleValueFitness(3.0);
		fitness[1] = new SimpleValueFitness(4.0);
		ind = new BinArrayIndividual(new byte[]{0,1,0}, new MOFitness(fitness));
		population.add(ind.copy());
		
		fitness[0] = new SimpleValueFitness(2.0);
		fitness[1] = new SimpleValueFitness(1.0);
		ind = new BinArrayIndividual(new byte[]{0,1,1}, new MOFitness(fitness));
		population.add(ind.copy());
		
		fitness[0] = new SimpleValueFitness(1.0);
		fitness[1] = new SimpleValueFitness(2.0);
		ind = new BinArrayIndividual(new byte[]{1,0,0}, new MOFitness(fitness));
		population.add(ind.copy());
		
		fitness[0] = new SimpleValueFitness(4.0);
		fitness[1] = new SimpleValueFitness(3.0);
		ind = new BinArrayIndividual(new byte[]{1,0,1}, new MOFitness(fitness));
		population.add(ind.copy());
		
		fitness[0] = new SimpleValueFitness(0.0);
		fitness[1] = new SimpleValueFitness(0.0);
		ind = new BinArrayIndividual(new byte[]{1,1,1}, new MOFitness(fitness));
		population.add(ind.copy());
		
		return population;
	}
	
	/**
	 * Create a population of individuals with three non dominated individuals.
	 * @return List containing the individuals.
	 * */
	protected List<IIndividual> createPopulationThreeNonDominated(){
		List<IIndividual> population = new ArrayList<IIndividual>();

		// Create some individuals assuming their objective values
		SimpleValueFitness [] fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(5.0);
		fitness[1] = new SimpleValueFitness(5.0);
		BinArrayIndividual ind = new BinArrayIndividual(new byte[]{0,0,0}, new MOFitness(fitness));
		population.add(ind.copy());

		fitness[0] = new SimpleValueFitness(4.0);
		fitness[1] = new SimpleValueFitness(6.0);
		ind = new BinArrayIndividual(new byte[]{0,0,0}, new MOFitness(fitness));
		population.add(ind.copy());

		fitness[0] = new SimpleValueFitness(6.0);
		fitness[1] = new SimpleValueFitness(4.0);
		ind = new BinArrayIndividual(new byte[]{0,0,0}, new MOFitness(fitness));
		population.add(ind.copy());

		fitness[0] = new SimpleValueFitness(1.0);
		fitness[1] = new SimpleValueFitness(1.0);
		ind = new BinArrayIndividual(new byte[]{0,0,1}, new MOFitness(fitness));
		population.add(ind.copy());

		fitness[0] = new SimpleValueFitness(3.0);
		fitness[1] = new SimpleValueFitness(4.0);
		ind = new BinArrayIndividual(new byte[]{0,1,0}, new MOFitness(fitness));
		population.add(ind.copy());

		fitness[0] = new SimpleValueFitness(2.0);
		fitness[1] = new SimpleValueFitness(1.0);
		ind = new BinArrayIndividual(new byte[]{0,1,1}, new MOFitness(fitness));
		population.add(ind.copy());

		fitness[0] = new SimpleValueFitness(1.0);
		fitness[1] = new SimpleValueFitness(2.0);
		ind = new BinArrayIndividual(new byte[]{1,0,0}, new MOFitness(fitness));
		population.add(ind.copy());

		fitness[0] = new SimpleValueFitness(4.0);
		fitness[1] = new SimpleValueFitness(3.0);
		ind = new BinArrayIndividual(new byte[]{1,0,1}, new MOFitness(fitness));
		population.add(ind.copy());

		fitness[0] = new SimpleValueFitness(0.0);
		fitness[1] = new SimpleValueFitness(0.0);
		ind = new BinArrayIndividual(new byte[]{1,1,1}, new MOFitness(fitness));
		population.add(ind.copy());

		return population;
	}
	
	/**
	 * Create a comparator to check the Pareto dominance.
	 * It assumes a bi-objective maximization problem.
	 * @return A Pareto comparator.
	 * */
	protected ParetoComparator createParetoComparator() {
		Comparator<IFitness> [] components = new ValueFitnessComparator[2];
		components[0] = new ValueFitnessComparator(false);//maximize objective 0
		components[1] = new ValueFitnessComparator(false);//maximize objective 1
		ParetoComparator comparator = new ParetoComparator(components);
		return comparator;
	}
	
	/**
	 * Create a comparator to compare individuals.
	 * It assumes that the fitness comparator is a Pareto comparator.
	 * @return A comparator of individuals.
	 * */
	protected MOSolutionComparator createIndividualsComparator() {
		MOSolutionComparator comparator = new MOSolutionComparator(createParetoComparator());
		return comparator;
	}
	
	/**
	 * Create a population with objectives values in the range [0,1].
	 * */
	protected List<IIndividual> createPopulationInRange01(){
		List<IIndividual> population = new ArrayList<IIndividual>();
		
		// Create some individuals, assuming the objective values
		SimpleValueFitness [] fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(0.0);
		fitness[1] = new SimpleValueFitness(1.0);
		BinArrayIndividual ind = new BinArrayIndividual(new byte[]{0,0}, new NSGA2MOFitness(new MOFitness(fitness)));
		population.add(ind.copy());
		
		fitness[0] = new SimpleValueFitness(0.5);
		fitness[1] = new SimpleValueFitness(0.5);
		ind = new BinArrayIndividual(new byte[]{0,1}, new NSGA2MOFitness(new MOFitness(fitness)));
		population.add(ind.copy());
		
		fitness[0] = new SimpleValueFitness(1.0);
		fitness[1] = new SimpleValueFitness(0.0);
		ind = new BinArrayIndividual(new byte[]{1,1}, new NSGA2MOFitness(new MOFitness(fitness)));
		population.add(ind.copy());
		
		fitness[0] = new SimpleValueFitness(0.5);
		fitness[1] = new SimpleValueFitness(0.5);
		ind = new BinArrayIndividual(new byte[]{1,0}, new NSGA2MOFitness(new MOFitness(fitness)));
		population.add(ind.copy());
		
		return population;
	}
	
	/**
	 * Create a population of individuals with only two infeasible solutions.
	 * @return List containing the individuals.
	 * */
	protected List<IIndividual> createPopulationTwoInfeasibles(){
		List<IIndividual> population = new ArrayList<IIndividual>();
		
		// Create some individuals, assuming the objective values
		SimpleValueFitness [] fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(5.0);
		fitness[1] = new SimpleValueFitness(5.0);
		BinArrayConstrainedIndividual ind = new BinArrayConstrainedIndividual(new byte[]{0,0,0}, new MOFitness(fitness));
		ind.setFeasible(true);
		ind.setDegreeOfInfeasibility(0.0);
		population.add(ind.copy());
		
		fitness[0] = new SimpleValueFitness(1.0);
		fitness[1] = new SimpleValueFitness(1.0);
		ind = new BinArrayConstrainedIndividual(new byte[]{0,0,1}, new MOFitness(fitness));
		ind.setFeasible(true);
		ind.setDegreeOfInfeasibility(0.0);
		population.add(ind.copy());
		
		fitness[0] = new SimpleValueFitness(3.0);
		fitness[1] = new SimpleValueFitness(4.0);
		ind = new BinArrayConstrainedIndividual(new byte[]{0,1,0}, new MOFitness(fitness));
		ind.setFeasible(false);
		ind.setDegreeOfInfeasibility(1.0);
		population.add(ind.copy());
		
		fitness[0] = new SimpleValueFitness(2.0);
		fitness[1] = new SimpleValueFitness(1.0);
		ind = new BinArrayConstrainedIndividual(new byte[]{0,1,1}, new MOFitness(fitness));
		ind.setFeasible(true);
		ind.setDegreeOfInfeasibility(0.0);
		population.add(ind.copy());
		
		fitness[0] = new SimpleValueFitness(1.0);
		fitness[1] = new SimpleValueFitness(2.0);
		ind = new BinArrayConstrainedIndividual(new byte[]{1,0,0}, new MOFitness(fitness));
		ind.setFeasible(false);
		ind.setDegreeOfInfeasibility(3.0);
		population.add(ind.copy());
		
		fitness[0] = new SimpleValueFitness(4.0);
		fitness[1] = new SimpleValueFitness(3.0);
		ind = new BinArrayConstrainedIndividual(new byte[]{1,0,1}, new MOFitness(fitness));
		ind.setFeasible(true);
		ind.setDegreeOfInfeasibility(0.0);
		population.add(ind.copy());
		
		fitness[0] = new SimpleValueFitness(0.0);
		fitness[1] = new SimpleValueFitness(0.0);
		ind = new BinArrayConstrainedIndividual(new byte[]{1,1,1}, new MOFitness(fitness));
		ind.setFeasible(true);
		ind.setDegreeOfInfeasibility(0.0);
		population.add(ind.copy());
		
		return population;
	}
	
	/**
	 * Create a population of individuals with only two infeasible solutions.
	 * @return List containing the individuals.
	 * */
	protected List<IIndividual> createPopulationAllFeasibles(){
		List<IIndividual> population = new ArrayList<IIndividual>();
		
		// Create some individuals, assuming the objective values
		SimpleValueFitness [] fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(5.0);
		fitness[1] = new SimpleValueFitness(5.0);
		BinArrayConstrainedIndividual ind = new BinArrayConstrainedIndividual(new byte[]{0,0,0}, new MOFitness(fitness));
		ind.setFeasible(true);
		ind.setDegreeOfInfeasibility(0.0);
		population.add(ind.copy());
		
		fitness[0] = new SimpleValueFitness(1.0);
		fitness[1] = new SimpleValueFitness(1.0);
		ind = new BinArrayConstrainedIndividual(new byte[]{0,0,1}, new MOFitness(fitness));
		ind.setFeasible(true);
		ind.setDegreeOfInfeasibility(0.0);
		population.add(ind.copy());
			
		fitness[0] = new SimpleValueFitness(4.0);
		fitness[1] = new SimpleValueFitness(3.0);
		ind = new BinArrayConstrainedIndividual(new byte[]{1,0,1}, new MOFitness(fitness));
		ind.setFeasible(true);
		ind.setDegreeOfInfeasibility(0.0);
		population.add(ind.copy());
		
		fitness[0] = new SimpleValueFitness(0.0);
		fitness[1] = new SimpleValueFitness(0.0);
		ind = new BinArrayConstrainedIndividual(new byte[]{1,1,1}, new MOFitness(fitness));
		ind.setFeasible(true);
		ind.setDegreeOfInfeasibility(0.0);
		population.add(ind.copy());
		
		return population;
	}
	
	/**
	 * Create a population of individuals with only two infeasible solutions.
	 * @return List containing the individuals.
	 * */
	protected List<IIndividual> createPopulationAllInfeasibles(){
		List<IIndividual> population = new ArrayList<IIndividual>();
		
		// Create some individuals, assuming the objective values
		SimpleValueFitness [] fitness = new SimpleValueFitness[2];
		fitness[0] = new SimpleValueFitness(5.0);
		fitness[1] = new SimpleValueFitness(5.0);
		BinArrayConstrainedIndividual ind = new BinArrayConstrainedIndividual(new byte[]{0,0,0}, new MOFitness(fitness));
		ind.setFeasible(false);
		ind.setDegreeOfInfeasibility(1.0);
		population.add(ind.copy());
		
		fitness[0] = new SimpleValueFitness(1.0);
		fitness[1] = new SimpleValueFitness(1.0);
		ind = new BinArrayConstrainedIndividual(new byte[]{0,0,1}, new MOFitness(fitness));
		ind.setFeasible(false);
		ind.setDegreeOfInfeasibility(3.0);
		population.add(ind.copy());
			
		fitness[0] = new SimpleValueFitness(4.0);
		fitness[1] = new SimpleValueFitness(3.0);
		ind = new BinArrayConstrainedIndividual(new byte[]{1,0,1}, new MOFitness(fitness));
		ind.setFeasible(false);
		ind.setDegreeOfInfeasibility(2.0);
		population.add(ind.copy());
		
		fitness[0] = new SimpleValueFitness(0.0);
		fitness[1] = new SimpleValueFitness(0.0);
		ind = new BinArrayConstrainedIndividual(new byte[]{1,1,1}, new MOFitness(fitness));
		ind.setFeasible(false);
		ind.setDegreeOfInfeasibility(7.0);
		population.add(ind.copy());
		
		return population;
	}
}
