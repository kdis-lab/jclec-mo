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
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationRuntimeException;

import net.sf.jclec.IConfigure;
import net.sf.jclec.IIndividual;
import net.sf.jclec.IMutator;
import net.sf.jclec.base.FilteredMutator;
import net.sf.jclec.pso.Particle;
import net.sf.jclec.pso.ParticleSpecies;
import net.sf.jclec.util.random.IRandGen;
import net.sf.jclec.util.range.IRange;
import net.sf.jclec.util.range.Interval;

/**
 * SMPSO strategy.
 * 
 * <p>The <b>S</b>peed-constrained <b>M</b>ulti-objective PSO is a variant of {@link OMOPSO} in which
 * the turbulence mechanism and the update of the velocities are modified. More specifically, 
 * the turbulence mechanism applies only a mutation operator with certain probability, and a 
 * velocity constriction procedure is included.</p>
 *  
 * <p><i>Paper</i>: A.J. Nebro, J.J. Durillo, J. Garcia-Nieto, C.A. Coello Coello, F. Luna, E. Alba, 
 * "SMPSO: A new PSO-based Metaheuristic for Multi-objective Optimization". 
 *  IEEE Symp. on Computational Intelligence in multi-criteria decision-making, pp. 66-73, 2009.</p>
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
 * @see OMOPSO
 * */
public class SMPSO extends OMOPSO {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 8292899189497392118L;

	/** The mutation to be used in the turbulence mechanism */
	protected FilteredMutator mutator;

	/** Minimum velocities */
	protected double [] minVelocities;

	/** Maximum velocities */
	protected double [] maxVelocities;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public SMPSO(){
		super();
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Get the mutator.
	 * @return The configured mutator.
	 * */
	public IMutator getMutator(){
		return this.mutator.getDecorated();
	}

	/**
	 * Set the mutator.
	 * @param mutator The mutator that has to be set.
	 * */
	protected void setMutator(IMutator mutator){
		if (this.mutator == null) {
			this.mutator = new FilteredMutator();
		}
		this.mutator.setDecorated(mutator);
		this.mutator.contextualize(getContext());
	}

	/**
	 * Get the mutation probability.
	 * @return The mutation probability.
	 */
	public double getMutationProb(){
		return this.mutator.getMutProb();
	}

	/**
	 * Set the mutation probability.
	 * @param mutProb The probability that has to be set.
	 */
	public void setMutationProb(double mutProb){
		if (this.mutator != null) {
			this.mutator.setMutProb(mutProb);
		}
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>In SMPSO, the minimum and maximum velocities are also initialized considering
	 * the problem characteristics.</p>
	 * */
	@Override
	public List<IIndividual> initialize(List<IIndividual> swarm) {
		List<IIndividual> archive = super.initialize(swarm);

		// Initialize minimum and maximum velocities using the decision variable bounds
		int length = ((ParticleSpecies)getContext().getSpecies()).getGenotypeLength();
		this.minVelocities = new double[length];
		this.maxVelocities = new double[length];
		IRange [] schema = ((ParticleSpecies)getContext().getSpecies()).getGenotypeSchema();
		for(int i=0; i<length; i++){
			this.maxVelocities[i] = (((Interval)schema[i]).getRight() - ((Interval)schema[i]).getLeft())/2.0;
			this.minVelocities[i] = -this.maxVelocities[i];
		}
		return archive;
	}

	/**
	 * {@inheritDoc}
	 * <p>In SMPSO, the uniform and non-uniform mutations are not necessary, since it only
	 * applies one mutation. Specific parameters for SMPSO are:
	 * <ul>
	 * 	<li>mutator: <code>IMutator</code> (complex)
	 * 	<p>The mutator to be applied. The mutation probability can
	 * 	be configured. Default value is 0.15.</p>
	 * 	</li>
	 * </ul>
	 * </p>
	 * */
	@Override
	public void configure(Configuration settings) {

		// Call super method
		super.configure(settings);
	}

	/**
	 * {@inheritDoc}
	 * <p>In SMPSO, the velocity is updated according to a constriction coefficient.</p>
	 * */
	@Override
	public void updateVelocities(List<IIndividual> swarm, List<IIndividual> leaders) {

		int swarmSize = swarm.size();

		// Generate random values for the weights
		IRandGen randgen = getContext().createRandGen();
		double w = randgen.uniform(0.1, 0.5);
		double c1 = randgen.uniform(1.5, 2.5);
		double c2 = randgen.uniform(1.5, 2.5);
		double r1 = randgen.uniform(0.0, 1.0);
		double r2 = randgen.uniform(0.0, 1.0);
		Particle particle;
		double [] velocity;
		double [] leaderBestPosition;
		double [] currentPosition;
		double [] bestPosition;
		double value, coefficient, sum;

		// Compute the coefficient
		sum = c1+c2;
		if(sum <= 4)
			coefficient = 1.0;
		else
			coefficient = 2.0 / (2.0 - sum - Math.sqrt((sum*sum)-(4*sum)));

		// Update velocities for every particle
		for(int i=0; i<swarmSize; i++){
			particle = (Particle)swarm.get(i);
			velocity = particle.getVelocity();
			currentPosition = particle.getPosition();
			bestPosition = particle.getBestPosition();
			leaderBestPosition = ((Particle)leaders.get(i)).getBestPosition();

			// Compute the velocity in each position
			for(int j=0; j<velocity.length; j++){

				value = coefficient * (w*velocity[j] + c1*r1*(bestPosition[j]-currentPosition[j]) 
						+ c2*r2*(leaderBestPosition[j]-currentPosition[j]));

				// Check if velocity has been exceeded
				if(value > this.maxVelocities[j]){
					value = this.maxVelocities[j];
				}
				if(value < this.minVelocities[j]){
					value = this.minVelocities[j];
				}
				velocity[j] = value;
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>In SMPSO, some of the particles of the swarm will be
	 * disturbed using a mutation operator.</p>
	 * */
	@Override
	public List<IIndividual> turbulence(List<IIndividual> swarm) {

		List<IIndividual> disturbedSwarm = new ArrayList<IIndividual>();

		// Apply mutation
		disturbedSwarm = this.mutator.mutate(swarm);

		// Fix positions
		fixPositions(disturbedSwarm);

		// Add solutions that have not been mutated
		int size = this.mutator.getSterile().size();
		for (int i=0; i<size; i++)
			disturbedSwarm.add(this.mutator.getSterile().get(i).copy());

		return disturbedSwarm;
	}

	/**
	 * In SMPSO, the positions that exceed the bounds are fixed
	 * assigning the minimum or maximum bound. The velocity is reduced
	 * by multiplying by 0.001.
	 * @param swarm The swarm whose positions have to be fixed.
	 * */
	@Override
	protected void fixPositions(List<IIndividual> swarm){
		int size = swarm.size();
		Particle particle;
		ParticleSpecies species = (ParticleSpecies)getContext().getSpecies();
		double lenght = species.getGenotypeLength();
		double min, max;

		for(int i=0; i<size; i++){
			particle = (Particle)swarm.get(i);
			for(int j=0; j<lenght; j++){
				min = ((Interval)species.getGenotypeSchema()[j]).getLeft();
				max = ((Interval)species.getGenotypeSchema()[j]).getRight();
				if(particle.getPosition()[j] < min){
					particle.getPosition()[j] = min;
					particle.getVelocity()[j] = 0.001*particle.getVelocity()[j];
				}
				if(particle.getPosition()[j] > max){
					particle.getPosition()[j] = max;
					particle.getVelocity()[j] = 0.001*particle.getVelocity()[j];
				}
			}
		}
	}

	/**
	 * Configure the mutator operator.
	 * @param settings Configuration object.
	 * */
	@SuppressWarnings("unchecked")
	@Override
	protected void setMutationSettings(Configuration settings){
		String mutatorClassname;
		Class<? extends IMutator> mutatorClass;
		IMutator mutator;

		try {
			mutatorClassname = settings.getString("mutator[@type]");
			mutatorClass = (Class<? extends IMutator>) Class.forName(mutatorClassname);
			mutator = mutatorClass.getDeclaredConstructor().newInstance();
			if (mutator instanceof IConfigure) {
				Configuration mutatorConfiguration = settings.subset("mutator");
				((IConfigure) mutator).configure(mutatorConfiguration);
			}
			setMutator(mutator);
		} 
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new ConfigurationRuntimeException("Problems creating an instance of mutator", e);
		}

		// Mutation probability, 15% of the population will be mutated by default
		double mutProb = settings.getDouble("mutator[@mut-prob]", 0.15);
		setMutationProb(mutProb);
	}
}
