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

package net.sf.jclec.mo.evaluation;

import org.apache.commons.configuration.Configuration;

import net.sf.jclec.IConfigure;
import net.sf.jclec.IFitness;
import net.sf.jclec.IIndividual;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;

/**
 * An abstract objective function for multi-objective optimization problems. 
 * It maintains the properties about the domain and requires the implementation 
 * of an evaluation method over a candidate solution.
 * 
 * <p>HISTORY:
 * <ul>
 * 		<li>(AR|JRR|SV, 1.0, March 2018)		First release.</li>
 * </ul>
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
 * @see MOFitness
 * @see IConfigure
 * */

public abstract class Objective implements IConfigure {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 3313182338501998644L;

	/** Type of objective (true=maximize, false=minimize) */
	protected boolean maximized;
	
	/** Maximum value */
	protected double maximum;
	
	/** Minimum value */
	protected double minimum;
	
	/** Index in the list of objectives */
	private int index;
		
	/////////////////////////////////////////////////////////////////
	//-------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * It initializes the properties to default values:
	 * <ul>
	 * <li> <code>maximized</code> is set to <code>true</code>.
	 * <li> <code>maximum</code> is set to <code>Double.POSITIVE_INIFINITY</code>.
	 * <li> <code>minimum</code> is set to <code>Double.NEGATIVE_INFINITY</code>.
	 * </ul>
	 * */
	public Objective(){
		this.maximized = true;
		this.maximum = Double.POSITIVE_INFINITY;
		this.minimum = Double.NEGATIVE_INFINITY;
	}

	/////////////////////////////////////////////////////////////////
	//----------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Get the maximization flag.
	 * @return True if objective should be maximized, false
	 * otherwise.
	 * */
	public boolean isMaximized(){
		return this.maximized;
	}

	/**
	 * Set the maximization flag.
	 * @param maximized The value that has to be set.
	 * */
	public void setMaximized(boolean maximized){
		this.maximized = maximized;
	}
	
	/**
	 * Get the minimum value of the objective.
	 * @return The minimum value of the objective.
	 * */
	public double getMinimum(){
		return this.minimum;
	}
	
	/**
	 * Set the minimum value of the objective.
	 * @param minimum The value that has to be set.
	 * */
	public void setMinimum(double minimum){
		this.minimum = minimum;
	}
	
	/**
	 * Get the maximum value of the objective.
	 * @return The maximum value of the objective.
	 * */
	public double getMaximum(){
		return this.maximum;
	}
	
	/**
	 * Set the maximum value of the objective.
	 * @param maximum The value that has to be set.
	 * */
	public void setMaximum(double maximum){
		this.maximum = maximum;
	}
	
	/**
	 * Set the index of the objective
	 * @param index The new index
	 * */
	public void setIndex(int index){
		this.index = index;
	}
	
	/**
	 * Get the index of the objective
	 * @return The index of the objective
	 * */
	public int getIndex(){
		return this.index;
	}
	
	/////////////////////////////////////////////////////////////////
	//---------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc} 
	 * */
	@Override
	public void configure(Configuration settings) {
		// Do nothing
	}

	/////////////////////////////////////////////////////////////////
	//---------------------------------------------- Abstract methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Evaluate a given solution.
	 * @param solution Solution to be evaluated.
	 * @return The value of the objective for the solution.
	 * */
	public abstract IFitness evaluate(IIndividual solution);
}
