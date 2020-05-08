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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.sf.jclec.IFitness;
import net.sf.jclec.IIndividual;
import net.sf.jclec.binarray.BinArrayIndividual;
import net.sf.jclec.fitness.SimpleValueFitness;
import net.sf.jclec.fitness.ValueFitnessComparator;
import net.sf.jclec.mo.evaluation.MOEvaluator;
import net.sf.jclec.mo.evaluation.Objective;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;
import net.sf.jclec.mo.strategy.MOStrategyContext;
import net.sf.jclec.util.random.IRandGen;
import net.sf.jclec.util.random.RanecuFactory;

/**
 * Auxiliary class to test strategies.
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
 * */

public class MOStrategyTest {

	/* Auxiliary methods */

	/**
	 * Create a population of individuals.
	 * @return List containing the individuals.
	 * @throws CloneNotSupportedException 
	 * */
	protected List<IIndividual> createPopulation(MOFitness prototype) throws CloneNotSupportedException{
		List<IIndividual> population = new ArrayList<IIndividual>();

		// Create some individuals, assuming the objective values
		SimpleValueFitness [] objValues = new SimpleValueFitness[2];
		objValues[0] = new SimpleValueFitness(5.0);
		objValues[1] = new SimpleValueFitness(5.0);
		MOFitness fitness = (MOFitness) prototype.clone();
		fitness.setObjectiveValues(objValues);
		BinArrayIndividual individual = new BinArrayIndividual(new byte[]{0,0,0}, fitness);
		population.add(individual.copy());

		objValues[0] = new SimpleValueFitness(1.0);
		objValues[1] = new SimpleValueFitness(1.0);
		fitness = (MOFitness) prototype.clone();
		fitness.setObjectiveValues(objValues);
		individual = new BinArrayIndividual(new byte[]{0,0,1}, fitness);
		population.add(individual.copy());

		objValues[0] = new SimpleValueFitness(3.0);
		objValues[1] = new SimpleValueFitness(4.0);
		fitness = (MOFitness) prototype.clone();
		fitness.setObjectiveValues(objValues);
		individual = new BinArrayIndividual(new byte[]{0,1,0}, fitness);
		population.add(individual.copy());

		objValues[0] = new SimpleValueFitness(2.0);
		objValues[1] = new SimpleValueFitness(1.0);
		fitness = (MOFitness) prototype.clone();
		fitness.setObjectiveValues(objValues);
		individual = new BinArrayIndividual(new byte[]{0,1,1}, fitness);
		population.add(individual.copy());

		objValues[0] = new SimpleValueFitness(1.0);
		objValues[1] = new SimpleValueFitness(2.0);
		fitness = (MOFitness) prototype.clone();
		fitness.setObjectiveValues(objValues);
		individual = new BinArrayIndividual(new byte[]{1,0,0}, fitness);
		population.add(individual.copy());

		objValues[0] = new SimpleValueFitness(4.0);
		objValues[1] = new SimpleValueFitness(3.0);
		fitness = (MOFitness) prototype.clone();
		fitness.setObjectiveValues(objValues);
		individual = new BinArrayIndividual(new byte[]{1,0,1}, fitness);
		population.add(individual.copy());

		return population;
	}

	/**
	 * Create a set of possible offspring.
	 * @return List containing the individuals.
	 * @throws CloneNotSupportedException 
	 * */
	protected List<IIndividual> createOffspring(MOFitness prototype) throws CloneNotSupportedException{

		// Create some individuals, assuming the objective values
		List<IIndividual> offspring = new ArrayList<IIndividual>();

		SimpleValueFitness [] objValues = new SimpleValueFitness[2];
		objValues[0] = new SimpleValueFitness(5.0);
		objValues[1] = new SimpleValueFitness(4.0);
		MOFitness fitness = (MOFitness) prototype.clone();
		fitness.setObjectiveValues(objValues);
		BinArrayIndividual individual = new BinArrayIndividual(new byte[]{0,1,0}, fitness);
		offspring.add(individual.copy());

		objValues = new SimpleValueFitness[2];
		objValues[0] = new SimpleValueFitness(4.0);
		objValues[1] = new SimpleValueFitness(5.0);
		fitness = (MOFitness) prototype.clone();
		fitness.setObjectiveValues(objValues);
		individual = new BinArrayIndividual(new byte[]{1,0,1}, fitness);
		offspring.add(individual.copy());

		return offspring;
	}

	/**
	 * Create a possible archive of solutions.
	 * @return List containing the individuals.
	 * @throws CloneNotSupportedException 
	 * */
	protected List<IIndividual> createArchive(MOFitness prototype) throws CloneNotSupportedException{

		// Create an archive with one member
		List<IIndividual> archive = new ArrayList<IIndividual>();
		SimpleValueFitness [] objValues = new SimpleValueFitness[2];
		objValues[0] = new SimpleValueFitness(5.0);
		objValues[1] = new SimpleValueFitness(5.0);
		MOFitness fitness = (MOFitness) prototype.clone();
		fitness.setObjectiveValues(objValues);
		BinArrayIndividual individual = new BinArrayIndividual(new byte[]{0,0,0}, fitness);
		archive.add(individual.copy());
		return archive;
	}

	/**
	 * Create a population of individuals.
	 * @return List containing the individuals.
	 * @throws CloneNotSupportedException 
	 * */
	protected List<IIndividual> createPopulationInRange01(MOFitness prototype) throws CloneNotSupportedException{
		List<IIndividual> population = new ArrayList<IIndividual>();

		// Create some individuals, assuming the objective values
		SimpleValueFitness [] objValues = new SimpleValueFitness[2];
		objValues[0] = new SimpleValueFitness(0.0);
		objValues[1] = new SimpleValueFitness(1.0);
		MOFitness fitness = (MOFitness) prototype.clone();
		fitness.setObjectiveValues(objValues);
		BinArrayIndividual individual = new BinArrayIndividual(new byte[]{0,0}, fitness);
		population.add(individual.copy());

		objValues[0] = new SimpleValueFitness(0.5);
		objValues[1] = new SimpleValueFitness(0.5);
		fitness = (MOFitness) prototype.clone();
		fitness.setObjectiveValues(objValues);
		individual = new BinArrayIndividual(new byte[]{0,1}, fitness);
		population.add(individual.copy());

		objValues[0] = new SimpleValueFitness(1.0);
		objValues[1] = new SimpleValueFitness(0.0);
		fitness = (MOFitness) prototype.clone();
		fitness.setObjectiveValues(objValues);
		individual = new BinArrayIndividual(new byte[]{1,1}, fitness);
		population.add(individual.copy());

		objValues[0] = new SimpleValueFitness(0.5);
		objValues[1] = new SimpleValueFitness(0.5);
		fitness = (MOFitness) prototype.clone();
		fitness.setObjectiveValues(objValues);
		individual = new BinArrayIndividual(new byte[]{1,0}, fitness);
		population.add(individual.copy());

		return population;
	}

	/**
	 * Create a set of offspring.
	 * @return List containing the offspring.
	 * @throws CloneNotSupportedException 
	 * */
	protected List<IIndividual> createOffspringInRange01(MOFitness prototype) throws CloneNotSupportedException{
		List<IIndividual> offspring = new ArrayList<IIndividual>();
		// Create some individuals, assuming the objective values
		SimpleValueFitness [] objValues = new SimpleValueFitness[2];
		objValues[0] = new SimpleValueFitness(0.2);
		objValues[1] = new SimpleValueFitness(0.4);
		MOFitness fitness = (MOFitness) prototype.clone();
		fitness.setObjectiveValues(objValues);
		BinArrayIndividual individual = new BinArrayIndividual(new byte[]{0,0}, fitness);
		offspring.add(individual.copy());

		objValues = new SimpleValueFitness[2];
		objValues[0] = new SimpleValueFitness(0.6);
		objValues[1] = new SimpleValueFitness(0.3);
		fitness = (MOFitness) prototype.clone();
		fitness.setObjectiveValues(objValues);
		individual = new BinArrayIndividual(new byte[]{0,0}, fitness);
		offspring.add(individual.copy());
		
		objValues = new SimpleValueFitness[2];
		objValues[0] = new SimpleValueFitness(0.1);
		objValues[1] = new SimpleValueFitness(0.1);
		fitness = (MOFitness) prototype.clone();
		fitness.setObjectiveValues(objValues);
		individual = new BinArrayIndividual(new byte[]{0,0}, fitness);
		offspring.add(individual.copy());
		
		objValues = new SimpleValueFitness[2];
		objValues[0] = new SimpleValueFitness(0.3);
		objValues[1] = new SimpleValueFitness(0.2);
		fitness = (MOFitness) prototype.clone();
		fitness.setObjectiveValues(objValues);
		individual = new BinArrayIndividual(new byte[]{0,0}, fitness);
		offspring.add(individual.copy());

		return offspring;
	}
	
	/**
	 * Create an archive of solutions.
	 * @return List containing the individuals.
	 * @throws CloneNotSupportedException 
	 * */
	protected List<IIndividual> createArchiveInRange01(MOFitness prototype) throws CloneNotSupportedException{
		List<IIndividual> archive = new ArrayList<IIndividual>();
		// Create some individuals, assuming the objective values
		SimpleValueFitness [] objValues = new SimpleValueFitness[2];
		objValues[0] = new SimpleValueFitness(0.9);
		objValues[1] = new SimpleValueFitness(0.2);
		MOFitness fitness = (MOFitness) prototype.clone();
		fitness.setObjectiveValues(objValues);
		BinArrayIndividual individual = new BinArrayIndividual(new byte[]{0,0}, fitness);
		archive.add(individual.copy());
		return archive;
	}

	/**
	 * Create the comparator for the dummy optimization problem.
	 * @return Components of the fitness comparator.
	 * */
	protected Comparator<IFitness>[] createComparatorOfObjectives(){
		Comparator<IFitness> [] components = new ValueFitnessComparator[2];
		components[0] = new ValueFitnessComparator(false);//maximize objective 0
		components[1] = new ValueFitnessComparator(false);//maximize objective 1
		return components;
	}

	/**
	 * Create the evolution context. This method
	 * assumes that the population was created using
	 * the <code>createPopulation</code> method.
	 * @return Execution context
	 * */
	protected MOStrategyContext createContext(){

		// Evaluator
		List<Objective> objectives = new ArrayList<Objective>(2); // dummy objective functions
		objectives.add(new PercentageOfOnes());
		objectives.get(0).setIndex(0);
		objectives.add(new PercentageOfZeros());
		objectives.get(1).setIndex(1);
		MOEvaluator evaluator = new MOEvaluator(objectives);

		// Random number generator
		RanecuFactory factory = new RanecuFactory();
		IRandGen randgen = factory.createRandGen();

		// Evolution context
		MOStrategyContext context = new MOStrategyContext(randgen, null, evaluator, null, 6);
		return context;
	}

	/**
	 * Create the evolution context. This method
	 * assumes that the population was created using
	 * the <code>createPopulationInRange01</code> method.
	 * @return Execution context.
	 * */
	protected MOStrategyContext createContextPopulationInRange01(){

		// Evaluator
		List<Objective> objectives = new ArrayList<Objective>(2); // dummy objective functions
		objectives.add(new PercentageOfOnes());
		objectives.get(0).setIndex(0);
		objectives.add(new PercentageOfZeros());
		objectives.get(1).setIndex(1);
		MOEvaluator evaluator = new MOEvaluator(objectives);

		// Random number generator
		RanecuFactory factory = new RanecuFactory();
		IRandGen randgen = factory.createRandGen();

		// Evolution context
		MOStrategyContext context = new MOStrategyContext(randgen, null, evaluator, null, 4);
		return context;
	}
}
