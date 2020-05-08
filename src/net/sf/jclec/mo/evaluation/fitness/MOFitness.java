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

package net.sf.jclec.mo.evaluation.fitness;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import net.sf.jclec.IFitness;
import net.sf.jclec.base.AbstractFitness;
import net.sf.jclec.fitness.SimpleValueFitness;

/**
 * Fitness for a multi-objective problem.
 * It stores both the objective values for each objective function and an independent fitness
 * value useful for the fitness assignment method required in some multi-objective strategies.
 * 
 * <p>HISTORY:
 * <ul>
 * 		<li>(AR|JRR|SV, 1.0, March 2018)		First release.</li>
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
 * @see AbstractFitness
 * */

public class MOFitness extends AbstractFitness implements Cloneable{

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -7822183130012532859L;

	/** Fitness components */
	protected IFitness [] components;

	/** Fitness value */
	protected double value;

	/** Fitness is acceptable */
	protected boolean isAcceptable;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 */
	public MOFitness() {
		super();
		this.value = Double.NaN;
		this.isAcceptable = false;
		this.components = null;
	}

	/**
	 * Parameterized constructor.
	 * @param components Fitness components
	 */
	public MOFitness(IFitness [] components) {
		super();
		setObjectiveValues(components);
		this.value = Double.NaN;
	}

	/**
	 * Parameterized constructor.
	 * @param components Fitness components.
	 * @param valued Fitness value.
	 */
	public MOFitness(IFitness [] components, double value) {
		super();
		setObjectiveValues(components);
		setValue(value);
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Get the fitness value.
	 * @return The fitness value.
	 * */
	public double getValue() {
		return this.value;
	}

	/**
	 * Set the fitness value.
	 * @param value The value that has to be set.
	 * */
	public void setValue(double value) {
		this.value = value;
	}

	/**
	 * Get the values for each objective.
	 * @return The set of objectives values.
	 * */
	public IFitness[] getObjectiveValues() {
		return this.components;
	}

	/**
	 * Set the objective values.
	 * @param components The values that have to be set.
	 * */
	public void setObjectiveValues(IFitness[] components) {
		this.components = components;
	}

	/**
	 * Get the objective value at a specific position.
	 * @param index Index of the objective.
	 * @return The objective value.
	 * @throws IndexOutOfBoundsException
	 * */
	public IFitness getObjectiveValue(int index) {
		try {
			return this.components[index];
		}
		catch(IndexOutOfBoundsException e) {
			throw new IllegalArgumentException(index + "isn't a valid component index");
		}
	}

	/**
	 * Set the value of a specific objective.
	 * @param index Index of the objective
	 * @param value New objective value
	 * @throws IndexOutOfBoundsException
	 * */
	public void setObjectiveValue(int index, IFitness value) {
		try {
			this.components[index] = value;
		}
		catch(IndexOutOfBoundsException e) {
			throw new IllegalArgumentException(index + "isn't a valid component index");
		}
	}

	/**
	 * Set whether the fitness is acceptable.
	 * @param isAcceptable True if the fitness is acceptable, false otherwise
	 * */
	public void setAcceptable(boolean isAcceptable){
		this.isAcceptable = isAcceptable;
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public boolean isAcceptable() {
		return this.isAcceptable;
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public IFitness copy() {
		// New fitness
		MOFitness result = new MOFitness();
		// Copy components
		if(this.components!=null){
			int cl = this.components.length;
			result.components = new IFitness[cl];
			for (int i=0; i<cl; i++)
				result.components[i] = this.components[i].copy();
		}
		// Copy value
		result.setValue(this.value);
		// Returns result
		return result;
	}

	/**
	 * {@inheritDoc}
	 *  */
	@Override
	public Object clone() throws CloneNotSupportedException{
		return this.copy();
	}

	/**
	 * Create a hash code of the fitness object.
	 * @return A hash code of the fitness.
	 * */
	@Override
	public int hashCode() {
		// Hash code builder
		HashCodeBuilder hcb = new HashCodeBuilder();

		// Append fitness components
		if(this.components != null)
			for (IFitness fitness : this.components)
				hcb.append(fitness);

		// Append value
		hcb.append(value);

		// Return hash code
		return hcb.toHashCode();
	}

	/**
	 * Compare the fitness with other object
	 * @param other Object to compare with.
	 * @return True if objects are equals, false otherwise.
	 * */
	@Override
	public boolean equals(Object other) {
		if (other instanceof MOFitness) {
			MOFitness coth = (MOFitness) other;
			// Comparing number of components
			int cl  = this.components.length;
			int ocl = coth.components.length;
			if (cl == ocl && this.getValue() == coth.getValue()) {
				EqualsBuilder eb = new EqualsBuilder();
				for (int i=0; i<cl; i++) 
					eb.append(this.components[i], coth.components[i]);
				return eb.isEquals();
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}

	/**
	 * A string representation of the fitness object.
	 * @return String representing the fitness
	 * */
	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder(this);
		tsb.append("components", components);
		tsb.append("value",this.value);
		return tsb.toString();
	}

	/////////////////////////////////////////////////////////////////
	// ----------------------------------------------- Public methods
	/////////////////////////////////////////////////////////////////

	/**
	 * If the fitness component is an instance of <code>SimpleValueFitness</code>, 
	 * this method sets the objective value at the specified position.
	 * @param value The objective value that that to be set.
	 * @param index The index of the objective.
	 * @see SimpleValueFitness
	 * */
	public void setObjectiveDoubleValue(double value, int index){
		if(index>=0 && index<this.components.length){
			if(this.components[index] instanceof SimpleValueFitness)
				((SimpleValueFitness)this.components[index]).setValue(value);
		}
	}

	/**
	 * If the fitness component is an instance of <code>SimpleValueFitness</code>, 
	 * this method returns the current value of the objective at the specified position.
	 * @param index The index of the objective.
	 * @return Value at the specified position.
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @see SimpleValueFitness
	 * */
	public double getObjectiveDoubleValue(int index) throws IllegalAccessException, IllegalArgumentException{
		if(index>=0 && index<this.components.length){
			if(this.components[index] instanceof SimpleValueFitness)
				return ((SimpleValueFitness)this.components[index]).getValue();
			else
				throw new IllegalAccessException("Fitness component at index " + index + " is not a SimpleValueFitness");
		}
		else{
			throw new IllegalArgumentException("Index " + index + " is not a valid index");
		}
	}

	/**
	 * Get the number of objectives.
	 * @return Number of objectives
	 * */
	public int getNumberOfObjectives(){
		return this.components.length;
	}
}
