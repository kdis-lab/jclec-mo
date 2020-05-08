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

import java.util.Comparator;
import java.util.List;

import net.sf.jclec.IFitness;
import net.sf.jclec.IIndividual;
import net.sf.jclec.mo.IMOAlgorithm;

/**
 * Abstract strategy for a multi-objective PSO (MOPSO) algorithm. It specifies the required
 * structure of the methods that any MOPSO algorithm should implement, in addition to those
 * specified by {@link MOStrategy}: the turbulence mechanism, the update of positions and
 * the update of velocities. The execution control of the search process is performed by 
 * a <code>MOPSOAlgorithm</code>, so it will invoke the specific methods of the strategy. 
 * The algorithm is also in charge of updating the execution context that might be required 
 * to perform some operations in the strategy.
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
 * @see IMOAlgorithm
 * */

public abstract class MOPSOStrategy extends MOStrategy {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -598314457921606353L;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public MOPSOStrategy() {
		super();
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Abstract methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Initialize the needed variables after the initialization of the
	 * algorithm. The initial archive will be created (if it is required)
	 * using the initial swarm.
	 * @param swarm Particles in the current swarm.
	 * @return The initial archive of solutions.
	 * */
	@Override
	public abstract List<IIndividual> initialize(List<IIndividual> swarm);

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public abstract void update();

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public abstract void createSolutionComparator(Comparator<IFitness>[] components);

	/**
	 * Select particles that will act as leaders.
	 * @param swarm Particles within the current swarm.
	 * @param archive Individuals belonging to the external archive.
	 * @return List of selected individuals (leaders).
	 * */
	@Override
	public abstract List<IIndividual> matingSelection(List<IIndividual> swarm, List<IIndividual> archive);

	/**
	 * Select survivors for the next generation.
	 * @param swarm Particles within the current swarm.
	 * @param disturbedSwarm Particles created by variation operators.
	 * @param archive Particles belonging to the current archive.
	 * @return List of survivors.
	 * */
	@Override
	public abstract List<IIndividual> environmentalSelection(List<IIndividual> swarm, 
			List<IIndividual> disturbedSwarm, List<IIndividual> archive);

	/**
	 * Update the archive of solutions.
	 * @param swarm Particles within the current swarm.
	 * @param disturbedSwarm Particles created by variation operators.
	 * @param archive Particles belonging to the current archive.
	 * @return List containing the new member of the archive.
	 * */
	@Override
	public abstract List<IIndividual> updateArchive(List<IIndividual> swarm,
			List<IIndividual> disturbedSwarm, List<IIndividual> archive);

	/**
	 * Fitness assignment method. It sets the fitness value to the particles within
	 * the swarm, which could be used for mating selection and/or environmental selection.
	 * @param swarm Particles within the current swarm.
	 * @param archive Particles belonging to the current archive.
	 * */
	@Override
	protected abstract void fitnessAssignment(List<IIndividual> swarm, List<IIndividual> archive);

	/**
	 * Update the velocities of the particles within the swarm.
	 * @param swarm The swarm.
	 * @param leaders The leaders.
	 * */
	public abstract void updateVelocities(List<IIndividual> swarm, List<IIndividual> leaders);

	/**
	 * Update the positions of the particles within the swarm.
	 * @param swarm The swarm.
	 * */
	public abstract void updatePositions(List<IIndividual> swarm);

	/**
	 * Apply a turbulence mechanism within the swarm.
	 * @param swarm The swarm.
	 * */
	public abstract List<IIndividual> turbulence(List<IIndividual> swarm);
}
