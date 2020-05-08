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

package net.sf.jclec.algorithm;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationRuntimeException;
import org.apache.commons.lang.builder.EqualsBuilder;

import net.sf.jclec.IConfigure;
import net.sf.jclec.IEvaluator;
import net.sf.jclec.IIndividual;
import net.sf.jclec.IPopulation;
import net.sf.jclec.IProvider;
import net.sf.jclec.ISpecies;
import net.sf.jclec.pso.Particle;
import net.sf.jclec.util.random.IRandGenFactory;

/**
 * An abstract algorithm implementing the PSO paradigm. It implements a general
 * PSO optimizer where a swarm is comprised of particles that are moved according 
 * to their current velocity. Characteristics like the number and influence of leaders,
 * the mechanism to update velocities in each generation and the possibility to add
 * variation operators should be defined by concrete classes. This class does not assume 
 * any contextualization mechanism.
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
 * @see AbstractAlgorithm
 * @see IPopulation
 * */

public abstract class PSOAlgorithm extends AbstractAlgorithm {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 6333040371448281039L;

	/** Random number generator factory */
	protected IRandGenFactory randGenFactory;

	/** Particle species */
	protected ISpecies species;

	/** Particle evaluator */
	protected IEvaluator evaluator;

	/** Particle provider */
	protected IProvider provider;

	/** Swarm size */
	protected int swarmSize;

	/** Maximum of generations (stopping criterion) */
	protected int maxOfGenerations;

	/** Maximum of evaluations (stopping criterion) */
	protected int maxOfEvaluations;

	/** Current generation */
	protected int generation;

	/** Current swarm */
	protected List<IIndividual> swarm;
	
	/** Set of leaders */
	protected List<IIndividual> leaders;
	
	/** The swarm after executing the turbulence mechanism */
	protected List<IIndividual> disturbedSwarm;
	
	/** The particles that will belong to the next swarm */
	protected List<IIndividual> survivors;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 */
	public PSOAlgorithm() {
		super();
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Get the provider.
	 * @return The provided used in the experiment.
	 * */
	public IProvider getProvider() {
		return this.provider;
	}
	
	/**
	 * Set the provider.
	 * @param provider The provided that has to be set.
	 * */
	public void setProvider(IProvider provider){
		this.provider = provider;
	}
	
	/**
	 * Get the species.
	 * @return The species used in the experiment.
	 * */
	public ISpecies getSpecies(){
		return this.species;
	}
	
	/**
	 * Set the species.
	 * @param species The species that has to be set.
	 * */
	public void setSpecies(ISpecies species) {
		this.species = species;
	}
	
	/**
	 * Get the evaluator.
	 * @return The evaluator used in the experiment.
	 * */
	public IEvaluator getEvaluator(){
		return this.evaluator;
	}
	
	/**
	 * Set the evaluator.
	 * @param evaluator The evaluator that has to be set.
	 * */
	public void setEvaluator(IEvaluator evaluator) {
		this.evaluator = evaluator;
	}

	/**
	 * Get the current generation.
	 * @return The current generation.
	 * */
	public int getGeneration(){
		return this.generation;
	}
	
	/**
	 * Set the current generation.
	 * @param generation The number of generation that has to be set.
	 * */
	public void setGeneration(int generation) {
		this.generation = generation;
	}
	
	/**
	 * Get the current swarm.
	 * @return The current swarm.
	 * */
	public List<IIndividual> getSwarm(){
		return this.swarm;
	}
	
	/**
	 * Set the current swarm.
	 * @param swarm The swarm that has to be set.
	 * */
	public void setSwarm(List<IIndividual> swarm){
		this.swarm = swarm;
	}

	/**
	 * Get the random number generator factory.
	 * @return The random number generator factory.
	 * */
	public IRandGenFactory getRandGenFactory() {
		return this.randGenFactory;
	}

	/**
	 * Set the random number generator factory.
	 * @param randGenFactory The factory that has to be set.
	 * */
	public void setRandGenFactory(IRandGenFactory randGenFactory) {
		this.randGenFactory = randGenFactory;
	}

	/**
	 * Get the swarm size.
	 * @return Number of particles within the swarm.
	 * */
	public final int getSwarmSize() {
		return this.swarmSize;
	}

	/**
	 * Set the swarm size.
	 * @param swarmSize The number of particles that will comprise the swarm.
	 * */
	public void setSwarmSize(int swarmSize) {
		this.swarmSize = swarmSize;
	}

	/***
	 * Get the maximum number of generations.
	 * @return Maximum number of generations. 
	 */
	public int getMaxOfGenerations() {
		return this.maxOfGenerations;
	}

	/**
	 * Set the maximum number of generations.
	 * @param maxOfGenerations Maximum number of generations.
	 * */
	public void setMaxOfGenerations(int maxOfGenerations) {
		this.maxOfGenerations = maxOfGenerations;
	}

	/**
	 * Get the maximum number of evaluations.
	 * @return Maximum number of evaluations.
	 * */
	public int getMaxOfEvaluations() {
		return this.maxOfEvaluations;
	}

	/**
	 * Set the maximum number of evaluations.
	 * @param maxOfEvaluations Maximum number of evaluations.
	 * */
	public void setMaxOfEvaluations(int maxOfEvaluations) {
		this.maxOfEvaluations = maxOfEvaluations;
	}

	/**
	 * Get the set of leaders within the swarm.
	 * @param Particles acting as leaders.
	 * */
	public List<IIndividual> getLeaders() {
		return this.leaders;
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////
	
	// IConfigure methods
	
	/**
	 * Configuration parameters for PSOAlgorithm are:
	 * <ul>
	 * <li>
	 * <code>species: ISpecies (complex)</code></p>
	 * Particle species
	 * </li><li>
	 * <code>evaluator IEvaluator (complex)</code></p>
	 * Particle evaluator
	 * </li><li>
	 * <code>swarm-size (integer)</code></p>
	 * Swarm size
	 * </li><li>
	 * <code>max-of-generations (integer)</code></p>
	 * Maximum number of generations
	 * </li>
	 * <li>
	 * <code>provider: IProvider (complex)</code></p>
	 * Particle provider
	 * </li>
	 * </ul>
	 */
	@Override
	public void configure(Configuration configuration){
		// Call super.configure() method
		super.configure(configuration);

		// Random generators factory
		setRandGenSettings(configuration);

		// Individual species
		setSpeciesSettings(configuration);

		// Individuals evaluator
		setEvaluatorSettings(configuration);

		// Swarm size
		int swarmSize = configuration.getInt("swarm-size");
		setSwarmSize(swarmSize);

		// Maximum of generations
		int maxOfGenerations = configuration.getInt("max-of-generations", Integer.MAX_VALUE); 
		setMaxOfGenerations(maxOfGenerations);

		// Maximum of generations
		int maxOfEvaluations = configuration.getInt("max-of-evaluations", Integer.MAX_VALUE); 
		setMaxOfEvaluations(maxOfEvaluations);

		// Individuals provider
		setProviderSettings(configuration);
	}

	// Object methods
	
	/**
	 * {@inheritDoc}
	 * */
	@Override
	public boolean equals(Object other){
		if (other instanceof PSOAlgorithm) {
			PSOAlgorithm cother = (PSOAlgorithm) other;
			EqualsBuilder eb = new EqualsBuilder();

			// Random number generation factory
			eb.append(this.randGenFactory, cother.randGenFactory);
			// Particle species
			eb.append(this.species, cother.species);
			// Particle evaluator
			eb.append(this.evaluator, cother.evaluator);
			// Swarm size
			eb.append(this.swarmSize, cother.swarmSize);
			// Max of generations
			eb.append(this.maxOfGenerations, cother.maxOfGenerations);
			// Particle provider
			eb.append(this.provider, cother.provider);
			// Return test result
			return eb.isEquals();
		}
		else {
			return false;
		}
	}

	// AbstractAlgorithm methods
	
	/**
	 * {@inheritDoc}
	 * */
	@Override
	protected void doInit() {
		// Create particles
		this.swarm = this.provider.provide(this.swarmSize);

		// Evaluate particles
		this.evaluator.evaluate(this.swarm);

		// Initialize the local memory of each particle
		for(int i=0; i<swarmSize; i++){
			((Particle)this.swarm.get(i)).setBestFitness(this.swarm.get(i).getFitness());
		}
		
		// Update (initialize) the leaders and global memories
		updateLeaders();
		updateMemories();

		// Do Control
		doControl();
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	protected void doIterate() {
		this.generation++;
		doMovement();
		doVariation();
		doReplacement();
		doUpdate();
		doControl();
	}

	/////////////////////////////////////////////////////////////////
	// -------------------------------------------- Protected methods
	/////////////////////////////////////////////////////////////////

	/**
	 * In each iteration, the velocities and the positions
	 * are updated. The evaluator is then invoked to compute
	 * the new fitness value.
	 * */
	protected void doMovement(){
		// Update the velocity of each particle
		updateVelocities();
		// Update the position of each particle
		updatePositions();
		// Evaluate particles
		this.evaluator.evaluate(this.swarm);
	}
		
	/**
	 * This method updates the set of leaders within the swarm
	 * and the memory of each particle.
	 * */
	protected void doUpdate(){
		updateLeaders();
		updateMemories();
	}
	
	/**
	 * This method should implement the mechanism
	 * used to update the velocity of each particle.
	 * */
	protected abstract void updateVelocities();

	/**
	 * This method updates the position of each
	 * particle considering its current position
	 * and the updated velocity.
	 * */
	protected abstract void updatePositions();
	
	/**
	 * If the PSO variant uses some operators to create
	 * new particles.
	 * */
	protected abstract void doVariation();

	/** 
	 * This method should implement the mechanism to 
	 * update the set of leaders in the swarm.
	 * */
	protected abstract void updateLeaders();
	
	/** 
	 * This method should implement the mechanism
	 * to update the memory of each particle 
	 * considering the set of leaders.
	 * */
	protected abstract void updateMemories();
	
	/**
	 * If the PSO variant uses a perturbation mechanism,
	 * this method should implement the replacement 
	 * strategy.
	 * */
	protected abstract void doReplacement();
	
	/**
	 * Check if evolution is finished. Default implementation of this
	 * method performs the operations:
	 * 
	 * <ul>
	 * <li>
	 * If number of generations exceeds the maximum allowed, set the
	 * finished flag to true. Else, the flag remains false
	 * </li>
	 * <li>
	 * If number of evaluations exceeds the maximum allowed, set the
	 * finished flag to true. Else, the flag remains false
	 * </li>
	 * <li>
	 * If one individual has an  acceptable fitness, set the finished
	 * flag to true. Else, the flag remains false. 
	 * </li>
	 * </ul>
	 */
	protected void doControl() {
		// The maximum number of generations is exceeded
		if (this.generation >= this.maxOfGenerations) {
			this.state = FINISHED;
			return;
		}
		// The maximum number of evaluations is exceeded
		if (this.evaluator.getNumberOfEvaluations() > this.maxOfEvaluations) {
			this.state = FINISHED;
			return;
		}
		// A particle has an acceptable fitness value
		for (IIndividual particle : this.swarm) {
			if (particle.getFitness().isAcceptable()) {
				this.state = FINISHED;
				return;
			}
		}
	}	

	/**
	 * Configure random number generator.
	 * @param settings Configuration object.
	 * */
	@SuppressWarnings("unchecked")
	protected void setRandGenSettings(Configuration settings) {
		// Random generators factory
		try {
			// Species classname
			String randGenFactoryClassname = settings.getString("rand-gen-factory[@type]");
			// Species class
			Class<? extends IRandGenFactory> randGenFactoryClass = 
					(Class<? extends IRandGenFactory>) Class.forName(randGenFactoryClassname);
			// Species instance
			IRandGenFactory randGenFactory = randGenFactoryClass.getDeclaredConstructor().newInstance();
			// Configure species
			if (randGenFactory instanceof IConfigure) {
				((IConfigure) randGenFactory).configure
				(settings.subset("rand-gen-factory"));
			}
			// Set species
			setRandGenFactory(randGenFactory);
		} 
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new ConfigurationRuntimeException
			("Problems creating an instance of random generators factory", e);
		}
	}

	/**
	 * Configure species.
	 * @param settings Configuration object.
	 * */
	@SuppressWarnings("unchecked")
	protected void setSpeciesSettings(Configuration settings) {
		// Individual species
		try {
			// Species classname
			String speciesClassname = settings.getString("species[@type]");
			// Species class
			Class<? extends ISpecies> speciesClass = (Class<? extends ISpecies>) Class.forName(speciesClassname);
			// Species instance
			ISpecies species = speciesClass.getDeclaredConstructor().newInstance();
			// Configure species
			if (species instanceof IConfigure) {
				((IConfigure) species).configure(settings.subset("species"));
			}
			// Set species
			setSpecies(species);
		} 
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new ConfigurationRuntimeException("Problems creating an instance of species", e);
		}
	}

	/**
	 * Configure evaluator.
	 * @param settings Configuration object.
	 * */
	@SuppressWarnings("unchecked")
	protected void setEvaluatorSettings(Configuration settings) {
		// Individuals evaluator
		try {
			// Evaluator classname
			String evaluatorClassname = settings.getString("evaluator[@type]");
			// Evaluator class
			Class<? extends IEvaluator> evaluatorClass = (Class<? extends IEvaluator>) Class.forName(evaluatorClassname);
			// Evaluator instance
			IEvaluator evaluator = evaluatorClass.getDeclaredConstructor().newInstance();
			// Configure species
			if (evaluator instanceof IConfigure) {
				((IConfigure) evaluator).configure(settings.subset("evaluator"));
			}
			// Set species
			setEvaluator(evaluator);
		} 
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new ConfigurationRuntimeException("Problems creating an instance of evaluator", e);
		}
	}

	/**
	 * Configure provider.
	 * @param settings Configuration object.
	 * */
	@SuppressWarnings("unchecked")
	protected void setProviderSettings(Configuration settings) {
		try {
			// Provider classname
			String providerClassname = settings.getString("provider[@type]");
			// Provider class
			Class<? extends IProvider> providerClass = (Class<? extends IProvider>) Class.forName(providerClassname);
			// Provider instance
			IProvider provider = providerClass.getDeclaredConstructor().newInstance();
			// Configure provider
			if (provider instanceof IConfigure) {
				((IConfigure) provider).configure(settings.subset("provider"));
			}
			// Set provider
			setProvider(provider);
		} 
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new ConfigurationRuntimeException("Problems creating an instance of provider", e);
		}
	}
}
