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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import net.sf.jclec.IFitness;
import net.sf.jclec.IIndividual;

/**
 * Additional properties of a solution for the NSGA-II strategy.
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
 * @see INSGA2MOFitness
 * @see MOFitness
 * @see NSGA2
 */

public class NSGA2MOFitness extends MOFitness implements INSGA2MOFitness {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -3451250084041273321L;

	/** Front of individual */
	protected int front;

	/** Crowding distance */
	protected double crowdingDistance;

	/** Number of individuals that dominate it */
	protected int dominatedBy;

	/** The list of individuals that dominate it */
	protected List<IIndividual> dominatedList;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public NSGA2MOFitness(){
		super();
		this.front = -1;
		this.crowdingDistance = Double.NaN;
		this.dominatedBy = -1;
		this.dominatedList = null;
	}
	
	/**
	 * Parameterized constructor.
	 * @param fitness The fitness object.
	 * */
	public NSGA2MOFitness(MOFitness fitness){
		setValue(fitness.getValue());
		setObjectiveValues(fitness.getObjectiveValues());
		this.front = -1;
		this.crowdingDistance = 0.0;
		this.dominatedBy = 0;
		this.dominatedList = null;
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public int getFront() {
		return this.front;
	}
	
	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void setFront(int front) {
		this.front = front;
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public double getCrowdingDistance() {
		return this.crowdingDistance;
	}
	
	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void setCrowdingDistance(double crowdingDistance) {
		this.crowdingDistance = crowdingDistance;
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public int getDominatedBy() {
		return this.dominatedBy;
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void setDominatedBy(int dominatedBy) {
		this.dominatedBy = dominatedBy;
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void incrementDominatedBy() {
		this.dominatedBy++;
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void decrementDominatedBy() {
		this.dominatedBy--;
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public List<IIndividual> getDominatedList() {
		return this.dominatedList;
	}
	
	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void setDominatedList(List<IIndividual> dominatedList) {
		if(dominatedList!=null){
			this.dominatedList = new ArrayList<IIndividual>();
			for(int i=0; i<dominatedList.size(); i++){
				this.dominatedList.add(dominatedList.get(i));
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public IFitness copy() {

		// Create an empty fitness
		NSGA2MOFitness result = new NSGA2MOFitness();

		// Copy the components (objective values) and the global value
		result.setValue(this.value);
		if(this.components != null){
			int cl = this.components.length;
			result.components = new IFitness[cl];
			for (int i=0; i<cl; i++)
				result.components[i] = this.components[i].copy();
		}
		
		// Add the NSGA-II properties
		result.setFront(this.front);
		result.setDominatedBy(this.dominatedBy);
		result.setCrowdingDistance(this.crowdingDistance);
		result.setDominatedList(this.getDominatedList());

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
		hcb.append(front);
		hcb.append(crowdingDistance);
		hcb.append(dominatedBy);
		hcb.append(dominatedList);

		// Return hash code
		return hcb.toHashCode();
	}
	
	/**
	 * {@inheritDoc}
	 * */
	@Override
	public boolean equals(Object oth) {
		
		// Check fitness type
		if (oth instanceof NSGA2MOFitness) {
			
			NSGA2MOFitness coth = (NSGA2MOFitness) oth;
			
			// Compare the specific properties
			EqualsBuilder eb = new EqualsBuilder();
			eb.append(this.getValue(), coth.getValue());
			eb.append(this.getFront(), coth.getFront());
			eb.append(this.getCrowdingDistance(), coth.getCrowdingDistance());

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
		tsb.append("crowding distance", this.crowdingDistance);
		return tsb.toString();
	}
}