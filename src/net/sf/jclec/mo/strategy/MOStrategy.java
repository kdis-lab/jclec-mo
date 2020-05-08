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

import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationRuntimeException;

import net.sf.jclec.IConfigure;
import net.sf.jclec.IFitness;
import net.sf.jclec.IIndividual;
import net.sf.jclec.mo.IMOAlgorithm;
import net.sf.jclec.mo.IMOEvaluator;
import net.sf.jclec.mo.comparator.MOSolutionComparator;
import net.sf.jclec.mo.evaluation.Objective;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;

/**
 * Abstract strategy for a multi-objective approach. It specifies the required
 * structure of the methods that any multi-objective algorithm should implement:
 * the mating selection, the environmental selection, a fitness assignment mechanism
 * and the management of the archive. The last two methods are optional. The execution
 * control of the search process is performed by an <code>IMOAlgorithm</code>, so it will
 * invoke the specific methods of the strategy. The algorithm is also in charge of updating
 * the execution context that might be required to perform some operations in the strategy.
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
 * @see IMOAlgorithm
 * @see MOStrategyContext
 * */

public abstract class MOStrategy implements IConfigure {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 7845914781913222028L;

	/** Individuals comparator */
	protected MOSolutionComparator comparator;

	/** Execution context (linkage with the algorithm) */
	private MOStrategyContext context;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public MOStrategy(){
		// do nothing
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Get the comparator of solutions.
	 * @return Configured comparator.
	 * */
	public MOSolutionComparator getSolutionComparator(){
		return this.comparator;
	}

	/**
	 * Set the comparator of solutions.
	 * @param comparator The comparator that has to be set.
	 * */
	public void setSolutionComparator(MOSolutionComparator comparator){
		this.comparator = comparator;
	}

	/**
	 * Set the execution context.
	 * @param context The context that has to be set.
	 * */
	public final void setContext(MOStrategyContext context){
		this.context = context;
	}

	/**
	 * Get the execution context.
	 * @return Execution context.
	 * */
	public final MOStrategyContext getContext(){
		return this.context;
	}

	/**
	 * Should the optimization problem be maximized?
	 * @return True for maximization problem, false for minimization problem,
	 * null otherwise (mixing objectives to be maximized and to be minimized).
	 * */
	public Boolean isMaximized(){
		Boolean result = null;
		int max=0, min=0;
		List<Objective> objectives = ((IMOEvaluator)getContext().getEvaluator()).getObjectives();
		int nObj = objectives.size();
		// Check each objective
		for(Objective obj: objectives)
			if(obj.isMaximized())
				max++;
			else
				min++;
		// Check overall type of optimization problem
		if (max==nObj)
			result = true;
		else if(min==nObj)
			result = false;
		return result;
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public void configure(Configuration settings) {
		// Fitness class name
		String fitnessClassName = settings.getString("fitness[@type]");
		MOFitness fitness = null;

		// A specific fitness object must be used
		if(fitnessClassName != null){
			Class<? extends MOFitness> objClass;
			try {
				objClass = (Class<? extends MOFitness>) Class.forName(fitnessClassName);
				fitness = objClass.getDeclaredConstructor().newInstance();
			}catch(ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e){
				throw new ConfigurationRuntimeException("Problems creating an instance of the fitness");
			}
		}

		// The default fitness object
		else{
			fitness = new MOFitness();
		}

		// Store the fitness object in the evolution context
		getContext().setFitnessPrototype(fitness);
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Abstract methods
	/////////////////////////////////////////////////////////////////
	
	/**
	 * Create the needed variables after the beginning of the
	 * algorithm. The initial archive will be created (if it is required)
	 * using the initial population.
	 * @param population Individuals in the current population.
	 * @return The initial archive of solutions.
	 * */
	public abstract List<IIndividual> initialize(List<IIndividual> population);

	/**
	 * Update or clean variables after performing 
	 * an iteration in the algorithm.
	 * */
	public abstract void update();

	/**
	 * Create the solution comparator.
	 * @param components The set of simple comparators for each objective.
	 * */
	public abstract void createSolutionComparator(Comparator<IFitness> [] components);

	/**
	 * Select individuals that will act as parents.
	 * @param population Individuals within the current population.
	 * @param archive Individuals belonging to the external population.
	 * @return List of selected individuals (parents).
	 * */
	public abstract List<IIndividual> matingSelection(List<IIndividual> population, List<IIndividual> archive);

	/**
	 * Select survivors for the next generation.
	 * @param population Individuals within the current population.
	 * @param offspring Individuals created by variation operators.
	 * @param archive Individuals belonging to the current archive.
	 * @return List of survivors.
	 * */
	public abstract List<IIndividual> environmentalSelection(List<IIndividual> population, List<IIndividual> offspring, List<IIndividual> archive);

	/**
	 * Update the archive of solutions.
	 * @param population Individuals within the current population.
	 * @param offspring Individuals created by variation operators.
	 * @param archive Individuals belonging to the current archive.
	 * @return List containing the new member of the archive.
	 * */
	public abstract List<IIndividual> updateArchive(List<IIndividual> population, List<IIndividual> offspring, List<IIndividual> archive);

	/**
	 * Fitness assignment method. It sets the fitness value to the individuals in
	 * the population, which could be used for mating selection and/or environmental selection.
	 * @param population Individuals within the current population.
	 * @param archive Individuals belonging to the current archive.
	 * */
	protected abstract void fitnessAssignment(List<IIndividual> population, List<IIndividual> archive);
}
