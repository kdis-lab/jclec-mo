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

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.builder.EqualsBuilder;

import net.sf.jclec.algorithm.PSOAlgorithm;
import net.sf.jclec.realarray.RealArrayIndividual;
import net.sf.jclec.realarray.RealArrayIndividualSpecies;
import net.sf.jclec.util.range.IRange;

/**
 * A species for PSO algorithms. It is an specialization of <code>RealArrayIndividualSpecies</code> that 
 * takes into account the specific properties of particles.
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
 * @see RealArrayIndividual
 * @see Particle
 * @see PSOAlgorithm
 * */

public class ParticleSpecies extends RealArrayIndividualSpecies {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 485895001204414467L;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 */
	public ParticleSpecies() {
		super();
	}

	/**
	 * Parameterized constructor.
	 * @param schema The solution schema.
	 */
	public ParticleSpecies(IRange [] schema) {
		super();
		setGenotypeSchema(schema);
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void configure(Configuration settings) {
		super.configure(settings);
	}

	/**
	 * To create a new particle, use {@link createParticle}
	 */
	@Override
	@Deprecated
	public RealArrayIndividual createIndividual() {
		return new Particle();
	}

	/**
	 *  To create a new particle with a specific position, use {@link createParticle}
	 */
	@Override
	@Deprecated
	public RealArrayIndividual createIndividual(double[] position) {
		return new Particle(position);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object other) {
		if (other instanceof ParticleSpecies) {
			EqualsBuilder eb = new EqualsBuilder();
			ParticleSpecies otherSpecies = (ParticleSpecies) other;
			eb.append(this.genotypeSchema, otherSpecies.genotypeSchema);
			return eb.isEquals();
		}
		else {
			return false;
		}
	}

	/////////////////////////////////////////////////////////////////
	// ----------------------------------------------- Public methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Create a new particle.
	 * @return An empty particle.
	 */
	public Particle createParticle() {
		return new Particle();
	}

	/**
	 * Create a new particle with the given position.
	 * @param position The position of the particle.
	 * @return A particle with a specific position.
	 */
	public Particle createParticle(double[] position) {
		return new Particle(position);
	}

	/**
	 * Create a new particle with the given position and velocity.
	 * @param position The position of the particle.
	 * @param velocity The velocity of the particle.
	 * @return A particle with a specific position and velocity.
	 */
	public Particle createParticle(double[] position, double [] velocity) {
		return new Particle(position);
	}
}
