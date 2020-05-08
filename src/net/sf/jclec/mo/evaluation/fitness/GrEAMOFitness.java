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
import net.sf.jclec.mo.strategy.GrEA;
import net.sf.jclec.mo.strategy.util.Hypercube;

/**
 * Additional fitness properties of a solution for the GrEA strategy.
 *  
 * <p>HISTORY:
 * <ul>
 *  <li>(AR|JRR|SV, 1.0, March 2018)		First release.</li>
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
 * @see IGrEAMOFitness
 * @see MOFitness
 * @see GrEA
 */

public class GrEAMOFitness extends MOFitness implements IGrEAMOFitness{

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -7810019567878061097L;

	/** Grid coordinates */
	protected Hypercube grid;

	/** Grid ranking */
	protected double ranking;

	/** Grid crowding distance */
	protected double crowdingDistance;

	/** Grid coordinate point distance */
	protected double coordinatePointDistance;

	/** Penalty degree in the grid ranking */
	protected double penaltyDegree;

	/** Front of individual */
	protected int front;

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
	public GrEAMOFitness(){
		super();
		this.front = -1;
		this.crowdingDistance = Double.NaN;
		this.dominatedBy = -1;
		this.dominatedList = null;
		this.penaltyDegree = Double.NaN;
		this.ranking = Double.NaN;
		this.grid = null;
	}

	/**
	 * Parameterized constructor.
	 * @param fitness The fitness object.
	 * */
	public GrEAMOFitness(MOFitness fitness){
		setValue(fitness.getValue());
		setObjectiveValues(fitness.getObjectiveValues());
		this.front = -1;
		this.crowdingDistance = 0.0;
		this.dominatedBy = 0;
		this.dominatedList = null;
		this.penaltyDegree = 0;
		this.ranking = -1;
		this.grid = null;
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public Hypercube getHypercube(){
		return this.grid;
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void setHypercube(Hypercube hypercube){
		this.grid = hypercube;
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public double getRanking() {
		return ranking;
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void setRanking(double ranking) {
		this.ranking = ranking;
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public double getCoordinatePointDistance() {
		return coordinatePointDistance;
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void setCoordinatePointDistance(double coordinatePointDistance) {
		this.coordinatePointDistance = coordinatePointDistance;
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public double getPenaltyDegree() {
		return penaltyDegree;
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void setPenaltyDegree(double penalty) {
		this.penaltyDegree = penalty;
	}

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
		GrEAMOFitness result = new GrEAMOFitness();

		// Copy the components (objective values) and the global value
		if(this.components != null){
			int cl = this.components.length;
			result.components = new IFitness[cl];
			for (int i=0; i<cl; i++)
				result.components[i] = this.components[i].copy();
		}
		result.setValue(this.value);

		// Add the GrEA properties
		if(this.grid!=null)
			result.setHypercube(new Hypercube(this.grid.getValues()));
		result.setRanking(this.ranking);
		result.setCrowdingDistance(this.crowdingDistance);
		result.setCoordinatePointDistance(this.coordinatePointDistance);
		result.setPenaltyDegree(this.penaltyDegree);
		result.setFront(this.front);
		result.setDominatedBy(this.dominatedBy);
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
		hcb.append(grid);
		hcb.append(ranking);
		hcb.append(crowdingDistance);
		hcb.append(coordinatePointDistance);
		hcb.append(penaltyDegree);

		// Return hash code
		return hcb.toHashCode();
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public boolean equals(Object oth) {

		// Check fitness type
		if (oth instanceof GrEAMOFitness) {
			GrEAMOFitness coth = (GrEAMOFitness) oth;

			// Compare the specific properties
			EqualsBuilder eb = new EqualsBuilder();
			eb.append(this.getValue(), coth.getValue());
			eb.append(this.getHypercube(), coth.getHypercube());
			eb.append(this.getRanking(), coth.getRanking());
			eb.append(this.getCoordinatePointDistance(), coth.getCoordinatePointDistance());
			eb.append(this.getPenaltyDegree(), coth.getPenaltyDegree());
			eb.append(this.getFront(), coth.getFront());
			eb.append(this.getCrowdingDistance(), coth.getCrowdingDistance());

			if(!eb.isEquals()){
				return false;
			}

			// Compare the objective values
			else if(this.components !=null && coth.components != null){
				eb = new EqualsBuilder();
				int cl  = this.components.length;
				int ocl = coth.components.length;
				if (cl == ocl) {
					for (int i=0; i<cl; i++) 
						eb.append(this.components[i], coth.components[i]);
					return eb.isEquals();
				}
				else{
					return false;
				}
			}
			else if(this.components == null && coth.components == null){
				return true;
			}
			else{
				return false;
			}
		}
		else {
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
		tsb.append("grid", this.grid);
		tsb.append("ranking", this.ranking);
		tsb.append("front", this.front);
		tsb.append("point distance", this.coordinatePointDistance);		
		tsb.append("crowding distance", this.crowdingDistance);
		tsb.append("penalty", this.penaltyDegree);
		return tsb.toString();
	}
}
