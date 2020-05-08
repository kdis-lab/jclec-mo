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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationRuntimeException;

import net.sf.jclec.IConfigure;
import net.sf.jclec.IDistance;
import net.sf.jclec.IFitness;
import net.sf.jclec.IIndividual;
import net.sf.jclec.IMutator;
import net.sf.jclec.mo.command.PopulationShuffler;
import net.sf.jclec.mo.command.PopulationSorter;
import net.sf.jclec.mo.comparator.CrowdingDistanceComparator;
import net.sf.jclec.mo.comparator.MOSolutionComparator;
import net.sf.jclec.mo.comparator.fcomparator.ParetoComparator;

/**
 * MOCHC strategy
 * 
 * <p>This strategy implements the adaptation of CHC algorithm to multi-objective optimization.
 * MOCHC applies the same restart mechanism that CHC, but it includes a new environmental 
 * selection based on the mechanism defined in NSGA-II.</p>
 *
 * <p><i>Paper</i>: A.J. Nebro, E. Alba, G. Molina, F. Chicano, F. Luna, J.J. Durillo, 
 * “Optimal Antenna Placement Using a New Multi-Objective CHC Algorithm". Proc. 9th
 * annual conference on Genetic and Evolutionary Computation (GECCO'09), pp. 876-883, 2003.</p>
 * 
 * <p>HISTORY:
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
 * */

public class MOCHC extends MOStrategy {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -1657286089249674102L;

	/** Mutator for restarting the population */
	protected IMutator mutator;

	/** Distance between two individuals */
	protected IDistance distance;

	/** Initial value of d parameter (minimum distance) */
	protected int initialD;

	/** Current value of d parameter (minimum distance) */
	protected int currentD;

	/** Value of d parameter (minimum distance) after restarting the population */
	protected int restartD;
	
	/** Distance threshold */
	protected int convergenceValue;

	/** Number of individuals that will survive */
	protected int numberOfSurvivors;

	/** A command to shuffle the population */
	protected PopulationShuffler command;

	/** A comparator based on crowding distance */
	protected CrowdingDistanceComparator comparator;
	
	/** A command to sort the population */
	protected PopulationSorter sortCommand;
	
	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public MOCHC() {
		super();
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Get the mutation operator.
	 * @return Mutation operator.
	 */
	public IMutator getMutator() {
		return this.mutator;
	}

	/**
	 * Set the mutation operator.
	 * @param mutator Mutation operator.
	 */
	public void setMutator(IMutator mutator) {
		this.mutator = mutator;
		this.mutator.contextualize(getContext());
	}

	/**
	 * Get the distance to prevent incest.
	 * @return The distance object.
	 * */
	public IDistance getDistance() {
		return this.distance;
	}

	/**
	 * Set the distance to prevent incest.
	 * @param distance The distance that has to be set.
	 * */
	protected void setDistance(IDistance distance) {
		this.distance = distance;
	}

	/**
	 * Get the initial distance threshold.
	 * @return Value of the property <code>initialD</code>.
	 * */
	public int getInitialD() {
		return this.initialD;
	}

	/**
	 * Set the initial distance threshold.
	 * @param initialD The value that has to be set.
	 * */
	protected void setInitialD(int initialD) {
		this.initialD = initialD;
	}

	/**
	 * Get the current distance threshold.
	 * @return Value of the property <code>currentD</code>.
	 * */
	public int getCurrentD() {
		return this.currentD;
	}

	/**
	 * Set the current distance threshold.
	 * @param currentD The value that has to be set.
	 * */
	protected void setCurrentD(int currentD) {
		this.currentD = currentD;
	}

	/**
	 * Get the distance threshold that should be used in restart.
	 * @return Value of the property <code>restartD</code>.
	 * */
	public int getRestartD() {
		return this.restartD;
	}

	/**
	 * Set the distance threshold to be used in restart.
	 * @param restardD The value that has to be set.
	 * */
	protected void setRestartD(int restardD) {
		this.restartD = restardD;
	}

	/**
	 * Get the number of survivors.
	 * @return The number of survivors.
	 * */
	public int getNumberOfSurvivors() {
		return this.numberOfSurvivors;
	}

	/**
	 * Set the number of survivors.
	 * @param numberOfSurvivors The value that has to be set.
	 * */
	protected void setNumberOfSurvivors(int numberOfSurvivors) {
		this.numberOfSurvivors = numberOfSurvivors;
	}
	
	/**
	 * Get the convergence value.
	 * @return The convergence value.
	 * */
	public int getConvergenceValue() {
		return this.convergenceValue;
	}

	/**
	 * Set the convergence value.
	 * @param convergenceValue The value that has to be set.
	 * */
	public void setConvergenceValue(int convergenceValue) {
		this.convergenceValue = convergenceValue;
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public List<IIndividual> initialize(List<IIndividual> population) {

		// Initialize the d parameter
		this.currentD = this.initialD;
		
		// Initialize the command and the comparator
		this.command = new PopulationShuffler(population, getContext().createRandGen());
		this.comparator = new CrowdingDistanceComparator();
		this.sortCommand = new PopulationSorter();
		this.sortCommand.setComparator(this.comparator);
		this.sortCommand.setInverse(true); // order from max distance to min distance
		
		// Archive is not used
		return null;
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void update() {
		// do nothing
	}

	/**
	 * {@inheritDoc}
	 * <p>Specific parameters for MOCHC are:
	 * <ul>
	 * 	<li>mutator: <code>IMutator</code> (complex)
	 * 	<p>The mutator to be applied.</p>
	 * 	</li>
	 * 	<li>distance: <code>IDistance</code> (complex)
	 * 	<p>The distance to be used.</p>
	 * 	</li>
	 * 	<li>initial-d (<code>integer</code>).
	 * 	<p>Initial distance threshold.</p>
	 * 	</li>
	 * 	<li>restart-d (<code>integer</code>).
	 * 	<p>Distance threshold to be restarted.</p>
	 * 	</li>
	 * 	<li>number-of-survivors (<code>integer</code>).
	 * 	<p>Number of survivors. By default, the 5% of the population size.</p>
	 * 	</li>
	 * 	<li>convergence-value (<code>integer</code>).
	 * 	<p>Convergence value. By default, 1.</p>
	 * 	</li>
	 * </ul>
	 * </p>
	 * */
	@Override
	public void configure(Configuration settings){
		super.configure(settings);

		// Configure mutator 
		configureMutator(settings);

		// Configure distance
		configureDistance(settings);

		// Configure the rest of parameters
		// Initial d
		int initialD = settings.getInt("initial-d");
		setInitialD(initialD);

		// Restart d
		int restartD = settings.getInt("restart-d");
		setRestartD(restartD);

		// Number of survivors, by default, 5% of the population
		int numberOfSurvivors = settings.getInt("number-of-survivors", (int) 0.05*getContext().getPopulationSize());
		setNumberOfSurvivors(numberOfSurvivors);
		
		// Convergence value (K), 1 by default
		int convergenceValue = settings.getInt("convergence-value",1);
		setConvergenceValue(convergenceValue);
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void createSolutionComparator(Comparator<IFitness>[] components) {
		// Fitness comparator
		ParetoComparator fcomparator = new ParetoComparator(components);
		// Individuals comparator
		MOSolutionComparator comparator = new MOSolutionComparator(fcomparator);
		setSolutionComparator(comparator);
	}

	/**
	 * {@inheritDoc}
	 * <p>In MOCHC, parents are selected using a incest prevention technique.</p>
	 * */
	@Override
	public List<IIndividual> matingSelection(List<IIndividual> population, List<IIndividual> archive) {

		List<IIndividual> parents = new ArrayList<IIndividual>();

		// Shuffle the population
		this.command.setPopulation(population);
		this.command.execute();

		// Incest prevention
		IIndividual parent0, parent1;
		int size = population.size()-1;
		for(int i=0; i<size; i+=2){
			// Select two individuals
			parent0 = population.get(i);
			parent1 = population.get(i+1);

			// Check the distance between them
			if(this.distance.distance(parent0, parent1) > 2*this.currentD){
				parents.add(parent0);
				parents.add(parent1);
			}
		}
		return parents;
	}

	/**
	 * {@inheritDoc}
	 * <p>It performs the same procedure than NSGA-II. After that, 
	 * the convergence is checked and the restart mechanism is applied, if required.</p>
	 * */
	@Override
	public List<IIndividual> environmentalSelection(List<IIndividual> population, 
			List<IIndividual> offspring, List<IIndividual> archive) {

		// Firstly, select according the method proposed by NSGA-II
		NSGA2 nsga2 = new NSGA2();
		nsga2.setSolutionComparator(getSolutionComparator());
		nsga2.setContext(getContext());
		nsga2.initialize(population);
		List<IIndividual> survivors = nsga2.environmentalSelection(population, offspring, archive);
		List<IIndividual> mutants;
		
		// If the current population and the survivors are equals, restarting is required
		boolean restart = true;
		int size = survivors.size();
		for(int i=0; restart && i<size; i++){
			if(!population.contains(survivors.get(i))){
				restart = false;
			}
		}
		
		// Decrement d parameter
		if(restart){	
			this.currentD--;
		}
		
		// Check current distance
		if(this.currentD <= -this.convergenceValue){
			// Restart the d parameter
			this.currentD = restartD;
					
			// Sort using crowding distance and select the percentage of survivors
			this.sortCommand.setPopulation(population);
			this.sortCommand.execute();
			
			// Only the first n solutions survive, so generate mutated solutions from n+1 to population size
			mutants = this.mutator.mutate(survivors.subList(numberOfSurvivors, size));
			getContext().getEvaluator().evaluate(mutants); // mutants should be evaluated
			int numberOfMutants=mutants.size();
			for(int i=0; i<numberOfMutants; i++){
				survivors.set(i+numberOfSurvivors, mutants.get(i)); // replace solutions
			}
		}
	
		return survivors;
	}

	/**
	 * {@inheritDoc}
	 * <p>MOCHC does not uses the archive.</p>
	 * */
	@Override
	public List<IIndividual> updateArchive(List<IIndividual> population, List<IIndividual> offspring, 
			List<IIndividual> archive) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * <p>MOCHC does not uses a fitness function.</p>
	 * */
	@Override
	protected void fitnessAssignment(List<IIndividual> population, List<IIndividual> archive) {
		// do nothing
	}

	/////////////////////////////////////////////////////////////////
	// -------------------------------------------- Protected methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Configure the mutator.
	 * @param settings The configuration object.
	 * */
	@SuppressWarnings("unchecked")
	protected void configureMutator(Configuration settings){
		try {
			String mutatorClassname = settings.getString("mutator[@type]");

			Class<? extends IMutator> mutatorClass = 
					(Class<? extends IMutator>) Class.forName(mutatorClassname);

			IMutator mutator = mutatorClass.getDeclaredConstructor().newInstance();
			if (mutator instanceof IConfigure) {
				Configuration mutatorConfiguration = settings.subset("mutator");
				((IConfigure) mutator).configure(mutatorConfiguration);
			}
			setMutator(mutator);
		} 
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new ConfigurationRuntimeException("Problems creating an instance of mutator", e);
		}
	}

	/**
	 * Configure the distance.
	 * @param settings The configuration object.
	 * */
	@SuppressWarnings("unchecked")
	protected void configureDistance(Configuration settings){
		try {
			// Distance classname
			String distanceClassname = 
					settings.getString("distance[@type]");
			// Distance class
			Class<? extends IDistance> distanceClass = 
					(Class<? extends IDistance>) Class.forName(distanceClassname);
			// Instance of IDistance object
			IDistance distance = distanceClass.getDeclaredConstructor().newInstance();
			// Configure distance if necessary
			if (distance instanceof IConfigure) {
				// Extract distance configuration
				((IConfigure) distance).configure(settings.subset("distance"));
			}
			// Set distance
			setDistance(distance);
		}
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new ConfigurationRuntimeException("Problems creating an instance of distance", e);
		}
	}
}
