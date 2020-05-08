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
import net.sf.jclec.mo.strategy.util.Hypercube;

/**
 * Hypercube properties of a solution. This kind of information is used by 
 * algorithms based on landscape partition like eMOEA and GrEA.
 *  
 * <p>HISTORY:
 * <ul>
 *   	<li>(AR|JRR|SV, 1.0, March 2018)		First release.</li>
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
 * @see Hypercube
 * @see IHypercubeMOFitness
 */

public class HypercubeMOFitness extends MOFitness implements IHypercubeMOFitness {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -4422318214170529552L;

	/** Grid coordinates */
	private Hypercube hypercube;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public HypercubeMOFitness(){
		super();
		this.hypercube = null;
	}

	/**
	 * Parameterized constructor.
	 * @param fitness The fitness object.
	 * */
	public HypercubeMOFitness(MOFitness fitness){
		setValue(fitness.getValue());
		setObjectiveValues(fitness.getObjectiveValues());
		this.hypercube = null;
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public Hypercube getHypercube() {
		return this.hypercube;
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void setHypercube(Hypercube hypercube) {
		this.hypercube = hypercube;
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public IFitness copy() {

		// Create an empty fitness
		HypercubeMOFitness result = new HypercubeMOFitness();

		// Copy the components (objective values) and the global value
		result.setValue(this.getValue());
		if(this.components != null){
			int cl = this.components.length;
			result.components = new IFitness[cl];
			for (int i=0; i<cl; i++)
				result.components[i] = this.components[i].copy();
		}

		// Add the hypercube property
		if(this.hypercube != null)
			result.setHypercube(new Hypercube(this.hypercube.getValues()));

		// Return the copy
		return result;
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
		hcb.append(hypercube);

		// Return hash code
		return hcb.toHashCode();
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public boolean equals(Object oth) {

		// Check fitness type
		if (oth instanceof HypercubeMOFitness) {
			HypercubeMOFitness coth = (HypercubeMOFitness) oth;

			// Compare the specific properties
			EqualsBuilder eb = new EqualsBuilder();
			eb.append(this.getValue(), coth.getValue());
			eb.append(this.getHypercube(), coth.getHypercube());

			if(!eb.isEquals()){
				return false;
			}

			// Compare the objective values
			else if(components !=null && coth.components != null){
				eb = new EqualsBuilder();
				int cl  = components.length;
				int ocl = coth.components.length;
				if (cl == ocl) {
					for (int i=0; i<cl; i++) 
						eb.append(components[i], coth.components[i]);
					return eb.isEquals();
				}
				else{
					return false;
				}
			}
			else if(components ==null && coth.components == null){
				return true;
			}
			else{
				return false;
			}
		}
		else{
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder(this);
		tsb.append("components", components);
		tsb.append("value",this.value);
		tsb.append("hypercube", this.hypercube);
		return tsb.toString();
	}
}
