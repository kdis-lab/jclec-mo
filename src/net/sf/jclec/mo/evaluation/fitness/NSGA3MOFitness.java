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

/**
 * Additional properties of a solution for the NSGA-III strategy.
 *  
 * <p>HISTORY:
 * <ul>
 *  	<li>(AR|JRR|SV, 1.0, March 2018)		First release.</li>
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
 * @see INSGA3MOFitness
 * @see NSGA2MOFitness
 * @see MOFitness
 * @see NSGA3
 */
public class NSGA3MOFitness extends NSGA2MOFitness implements INSGA3MOFitness {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -4656426059144121809L;

	/** Scaled objective values */
	protected double [] scaledObjectives;

	/** Associated reference point (its index) */
	protected int refPoint;

	/** Distance to the closest reference point */
	protected double distance;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public NSGA3MOFitness() {
		super();
		this.refPoint = -1;
		this.distance = Double.NaN;
	}
	
	/**
	 * Parameterized constructor
	 * @param fitness The fitness object.
	 * */
	public NSGA3MOFitness(MOFitness fitness) {
		super(fitness);
		this.refPoint = -1;
		this.distance = Double.NaN;
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>NSGA-III does not use the crowding distance, so this
	 * method does nothing
	 */
	@Override
	public void setCrowdingDistance(double crowdingDistance){
		// do nothing
	}

	/**
	 * {@inheritDoc}
	 * <p>NSGA-III does not use the crowding distance, so this
	 * method returns -1 by default.
	 */
	@Override
	public double getCrowdingDistance(){
		return -1;
	}

	/**
	 * {@inheritDoc}
	 * */
	public double [] getNormalisedObjectiveValues(){
		return this.scaledObjectives;
	}

	/**
	 * {@inheritDoc}
	 * */
	public void setNormalisedObjectiveValues(double [] values){
		int size = values.length;
		this.scaledObjectives = new double[size];
		for(int i=0; i<size; i++){
			this.scaledObjectives[i] = values[i];
		}
	}

	/**
	 * {@inheritDoc}
	 * */
	public double getNormalisedObjectiveValue(int index){
		if(this.scaledObjectives!=null)
			return this.scaledObjectives[index];
		else
			return -1.0;
	}

	/**
	 * {@inheritDoc}
	 * */
	public void setNormalisedObjectiveValue(double value, int index){
		if(this.scaledObjectives!=null && index>0 
				&& index<this.scaledObjectives.length){
			this.scaledObjectives[index] = value;
		}
	}

	/**
	 * {@inheritDoc}
	 * */
	public int getAssociatedRefPoint(){
		return this.refPoint;
	}

	/**
	 * {@inheritDoc}
	 * */
	public void setAssociatedRefPoint(int point){
		this.refPoint = point;
	}

	/**
	 * {@inheritDoc}
	 * */
	public double getDistance(){
		return this.distance;
	}

	/**
	 * {@inheritDoc}
	 * */
	public void setDistance(double distance){
		this.distance = distance;
	}
	
	/**
	 * {@inheritDoc}
	 * */
	@Override
	public IFitness copy() {

		// Create an empty fitness
		NSGA3MOFitness result = new NSGA3MOFitness();

		// Copy the components (objective values) and the global value
		result.setValue(this.value);
		if(this.components != null){
			int cl = this.components.length;
			result.components = new IFitness[cl];
			for (int i=0; i<cl; i++)
				result.components[i] = this.components[i].copy();
		}

		// Add the NSGA-III properties
		result.setFront(this.getFront());
		result.setDominatedBy(this.dominatedBy);
		result.setDominatedList(this.getDominatedList());
		if(this.scaledObjectives != null)
			result.setNormalisedObjectiveValues(this.getNormalisedObjectiveValues());
		result.setAssociatedRefPoint(this.getAssociatedRefPoint());
		result.setDistance(this.getDistance());

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
		hcb.append(scaledObjectives);
		hcb.append(refPoint);
		hcb.append(distance);

		// Return hash code
		return hcb.toHashCode();
	}
	
	/**
	 * {@inheritDoc}
	 * */
	@Override
	public boolean equals(Object oth) {
		
		// Check fitness type
		if (oth instanceof NSGA3MOFitness) {
			
			NSGA3MOFitness coth = (NSGA3MOFitness) oth;

			// Compare the specific properties
			EqualsBuilder eb = new EqualsBuilder();
			eb.append(this.getValue(), coth.getValue());
			eb.append(this.getFront(), coth.getFront());
			eb.append(this.getAssociatedRefPoint(), coth.getAssociatedRefPoint());
			eb.append(this.getDistance(), coth.getDistance());

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
		tsb.append("front", this.front);
		tsb.append("refPoint", this.refPoint);
		tsb.append("distance", this.distance);
		return tsb.toString();
	}
}
