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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import net.sf.jclec.IFitness;
import net.sf.jclec.IIndividual;
import net.sf.jclec.algorithm.PSOAlgorithm;
import net.sf.jclec.realarray.RealArrayIndividual;

/**
 * This class represents solutions as particles to be optimized by a PSO algorithm. It is
 * an specialization of <code>RealArrayIndividual</code> that includes the velocity and memories
 * of the solution.
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
 * @see PSOAlgorithm
 * */

public class Particle extends RealArrayIndividual {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 7890227592448466427L;

	/** Particle velocity */
	protected double [] velocity;

	/** Particle best position */
	protected double [] bestPosition;
	
	/** Particle best fitness */
	protected IFitness bestFitness;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 */
	public Particle() {
		super();
	}

	/**
	 * Parameterized constructor. 
	 * @param position Particle position.
	 */
	public Particle(double[] position) {
		super(position);
		this.velocity = new double[position.length];
		this.bestPosition = position;
	}

	/**
	 * Parameterized constructor.
	 * @param position Particle position.
	 * @param fitness  Particle fitness.
	 */
	public Particle(double[] position, IFitness fitness) {
		super(position, fitness);
		this.velocity = new double[position.length];
		this.bestPosition = position;
		this.bestFitness = fitness;
	}

	/**
	 * Parameterized constructor.
	 * @param position Particle position.
	 * @param fitness  Particle fitness.
	 * @param velocity Particle velocity.
	 */
	public Particle(double[] position, IFitness fitness, double [] velocity) {
		super(position, fitness);
		this.velocity = velocity;
		this.bestPosition = position;
		this.bestFitness = fitness;
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Get the particle position.
	 * @return Particle position.
	 * */
	public double [] getPosition(){
		return this.genotype;
	}

	/**
	 * Set the particle position.
	 * @param position New particle position.
	 * */
	public void setPosition(double [] position){
		this.genotype = position;
	}

	/**
	 * Set the particle velocity.
	 * @return Particle velocity.
	 * */
	public double [] getVelocity(){
		return this.velocity;
	}

	/**
	 * Set the particle velocity.
	 * @param velocity New particle velocity.
	 * */
	public void setVelocity(double [] velocity){
		this.velocity = velocity;
	}

	/**
	 * Set the particle best position.
	 * @return Particle best position.
	 * */
	public double [] getBestPosition(){
		return this.bestPosition;
	}

	/**
	 * Set the particle best position.
	 * @param position New Particle best position.
	 * */
	public void setBestPosition(double [] position){
		this.bestPosition = position;
	}
	
	/**
	 * Get the particle best fitness.
	 * @return Particle best fitness.
	 * */
	public IFitness getBestFitness(){
		return this.bestFitness;
	}
	
	/**
	 * Set the particle best fitness.
	 * @param fitness New particle best fitness.
	 * */
	public void setBestFitness(IFitness fitness){
		this.bestFitness = fitness;
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public IIndividual copy() {
		Particle other = new Particle();
		int size = this.genotype.length;

		// Create a copy of the position array
		double [] otherPosition = new double[size];
		System.arraycopy(this.genotype, 0, otherPosition, 0, size);
		other.setPosition(otherPosition);

		// Create a copy of the velocity array
		double [] otherVelocity = new double[size];
		System.arraycopy(this.velocity, 0, otherVelocity, 0, size);
		other.setVelocity(otherVelocity);

		// Create a copy of the best position
		double [] otherBest = new double[size];
		System.arraycopy(this.bestPosition, 0, otherBest, 0, size);
		other.setBestPosition(otherBest);

		// Copy the fitness
		if (this.fitness != null) {
			other.setFitness(this.fitness.copy());
		}
		
		// Copy the best fitness
		if(this.bestFitness != null){
			other.setBestFitness(this.bestFitness.copy());
		}
		return other;
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public boolean equals(Object other) {
		if (other instanceof Particle) {
			Particle otherParticle = (Particle) other;
			EqualsBuilder eb = new EqualsBuilder();
			eb.append(genotype, otherParticle.genotype);
			eb.append(velocity, otherParticle.velocity);
			eb.append(bestPosition, otherParticle.bestPosition);
			eb.append(bestFitness, otherParticle.bestFitness);
			return eb.isEquals();
		}
		else {
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public int hashCode() {
		// Hash code builder
		HashCodeBuilder hcb = new HashCodeBuilder();

		// Append super-hashCode
		hcb.appendSuper(super.hashCode());

		// Append specific properties
		hcb.append(this.velocity);
		hcb.append(this.bestPosition);
		hcb.append(this.bestFitness);

		// Return hash code
		return hcb.toHashCode();
	}
	
	/**
	 * {@inheritDoc}
	 * */
	@Override
	public String toString(){
		ToStringBuilder tsb = new ToStringBuilder(this);
		tsb.append("position", genotype);
		tsb.append("velocity", velocity);
		tsb.append("fitness", fitness);
		return tsb.toString();
	}
}
