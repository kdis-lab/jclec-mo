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

package net.sf.jclec.pso;

import net.sf.jclec.ISpecies;
import net.sf.jclec.algorithm.PSOAlgorithm;
import net.sf.jclec.realarray.RealArrayCreator;

/**
 * A provider for PSO algorithms. It is an specialization of <code>RealArrayCreator</code> that 
 * also initializes the velocity of the particles.
 * 
 * <p>HISTORY:
 * <ul>
 *	<li>(AR|JRR|SV, 1.0, March 2018)		First release.</li>
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
 * @see RealArrayCreator
 * @see Particle
 * @see PSOAlgorithm
 * */

public class ParticleCreator extends RealArrayCreator {

	/////////////////////////////////////////////////////////////////
	//---------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -1849167749468258113L;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public ParticleCreator() {
		super();
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * */
	@Override
	protected void prepareCreation() {
		// Get context species
		ISpecies spc = this.context.getSpecies();
		if (spc instanceof ParticleSpecies) {
			this.species = (ParticleSpecies) spc;
		}
		else {
			throw new IllegalArgumentException("ParticleSpecies expected");
		}
		// Get individuals schema
		this.schema = this.species.getGenotypeSchema();
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	protected void createNext() {
		this.createdBuffer.add(((ParticleSpecies)this.species).createParticle(createPosition(),createVelocity()));
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public boolean equals(Object other){
		if (other instanceof ParticleCreator){
			return true;
		}
		else {
			return false;
		}
	}

	/////////////////////////////////////////////////////////////////
	// -------------------------------------------- Protected methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Create a random position in the objective space.
	 * @return An array of double values generated at random.
	 * */
	protected double [] createPosition(){
		int size = this.schema.length;
		double [] position = new double[size];
		for(int i=0; i<size; i++) {
			position[i] = this.schema[i].getRandom(this.randgen);
		}
		return position;
	}

	/**
	 * Create a random velocity for the particles.
	 * By default, the initial velocity is set to 0 for all the dimensions.
	 * @return An array of double values containing the initial velocities.
	 * */
	protected double [] createVelocity(){
		int size = this.schema.length;
		double [] velocity = new double[size];
		for(int i=0; i<size; i++){
			velocity[i] = 0.0;
		}
		return velocity;
	}
}
