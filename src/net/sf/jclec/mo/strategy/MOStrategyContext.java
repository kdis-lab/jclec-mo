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

import java.util.List;

import net.sf.jclec.IEvaluator;
import net.sf.jclec.IIndividual;
import net.sf.jclec.ISpecies;
import net.sf.jclec.mo.IMOAlgorithm;
import net.sf.jclec.mo.IMOPopulation;
import net.sf.jclec.mo.comparator.MOSolutionComparator;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;
import net.sf.jclec.util.random.IRandGen;

/**
 * Execution context required by the strategy. It stores general properties of the
 * optimization process that are needed by the strategy and specific tools (e.g., selectors).
 * The context should be conveniently created and updated by the multi-objective algorithm.
 * 
 * <p>History:
 * <ul>
 * 	<li>(AR|JRR|SV, 1.0, March 2018)		First release.</li>
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
 * @see MOStrategy
 * @see IMOPopulation
 * @see IMOAlgorithm
 * */

public final class MOStrategyContext implements IMOPopulation {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 7370784539349952026L;

	/** Generator of random numbers */
	private IRandGen randgen;

	/** Species */
	private ISpecies species;

	/** Evaluator */
	private IEvaluator evaluator;

	/** Population */
	private List<IIndividual> population;
	
	/** Archive */
	private List<IIndividual> archive;
	
	/** Maximum number of generations */
	private int maxGenerations;
	
	/** Maximum number of evaluations */
	private int maxEvaluations;
	
	/** Generation */
	private int generation;
	
	/** Population size */
	private int populationSize;
	
	/** Fitness class */
	private MOFitness fitnessPrototype;

	/** Solution comparator */
	private MOSolutionComparator comparator;
	
	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------  Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Parameterized constructor.
	 * @param randgen Generator of random numbers
	 * @param species The species that defines the type of individuals.
	 * @param evaluator The evaluator of the optimization problem.
	 * @param comparator The comparator of solutions.
	 * @param populationSize The configured population size.
	 * */
	public MOStrategyContext(IRandGen randgen, ISpecies species, IEvaluator evaluator, 
			MOSolutionComparator comparator, int populationSize){
		this.randgen = randgen;
		this.species = species;
		this.evaluator = evaluator;
		this.comparator = comparator;
		this.generation = 0;
		this.populationSize = populationSize;
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public IRandGen createRandGen() {
		return this.randgen;
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public ISpecies getSpecies() {
		return this.species;
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public IEvaluator getEvaluator() {
		return this.evaluator;
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public int getGeneration() {
		return this.generation;
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public List<IIndividual> getInhabitants() {
		return this.population;
	}
	
	/**
	 * {@inheritDoc}
	 * */
	@Override
	public List<IIndividual> getArchive() {
		return this.archive;
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Set the species.
	 * @param species The species object that has to be set.
	 * */
	public void setSpecies(ISpecies species){
		this.species = species;
	}
	
	/**
	 * Set the evaluator.
	 * @param evaluator The evaluator object that has to be set.
	 * */
	public void setEvaluator(IEvaluator evaluator){
		this.evaluator = evaluator;
	}
	
	/**
	 * Set the generator of random number.
	 * @param randgen The generator object that has to be set.
	 * */
	public void setRanGenFactory(IRandGen randgen){
		this.randgen = randgen;
	}
	
	/**
	 * Set the population.
	 * @param population List containing the individuals of the current population.
	 * */
	public void setInhabitants(List<IIndividual> population){
		this.population = population;
	}
	
	/**
	 * Set the archive.
	 * @param archive List containing the individuals of the current archive.
	 * */
	public void setArchive(List<IIndividual> archive){
		this.archive = archive;
	}
	
	/**
	 * Set the generation.
	 * @param generation Current generation.
	 * */
	public void setGeneration(int generation) {
		this.generation = generation;
	}
	
	/**
	 * Set the maximum number of generations.
	 * @param generations Maximum number of generations.
	 * */
	public void setMaxGenerations(int generations) {
		this.maxGenerations = generations;
	}
	
	/**
	 * Get the maximum number of generations.
	 * @return Maximum number of generations.
	 * */
	public int getMaxGenerations() {
		return this.maxGenerations;
	}
	
	/**
	 * Set the maximum number of evaluations.
	 * @param evaluations Maximum number of evaluations.
	 * */
	public void setMaxEvaluations(int evaluations) {
		this.maxEvaluations = evaluations;
	}
	
	/**
	 * Get the maximum number of evaluations.
	 * @return Maximum number of evaluations.
	 * */
	public int getMaxEvaluations() {
		return this.maxEvaluations;
	}
	
	/**
	 * Get the configured population size.
	 * @return The population size.
	 * */
	public int getPopulationSize(){
		return this.populationSize;
	}
	
	/**
	 * Set the population size.
	 * @param populationSize The new population size.
	 * */
	public void setPopulationSize(int populationSize){
		this.populationSize = populationSize;
	}
	
	/**
	 * Get the fitness type.
	 * @return The fitness object to be used in the experiment.
	 * */
	public MOFitness getFitnessPrototype(){
		return this.fitnessPrototype;
	}
	
	/**
	 * Set the fitness type.
	 * @param fitness The fitness object to be used in the experiment.
	 * */
	protected void setFitnessPrototype(MOFitness fitness){
		this.fitnessPrototype = fitness;
	}
	
	/**
	 * Get the solution comparator, which encapsulates the fitness comparator.
	 * @return The comparator of solutions.
	 * */
	public MOSolutionComparator getSolutionComparator(){
		return this.comparator;
	}
	
	/**
	 * Set the solution comparator.
	 * @param fitness The comparator of solutions.
	 * */
	protected void setSolutionComparator(MOSolutionComparator comparator){
		this.comparator = comparator;
	}
}