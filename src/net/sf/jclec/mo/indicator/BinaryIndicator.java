/*
This file belongs to JCLEC-MOEA, a Java library for the
application and development of metaheuristic algorithms 
for the resolution of multi-objective and many-objective 
optimization problems.

Copyright (C) 2018.  A. Ramírez, J.R. Romero, S. Ventura
Knowledge Discovery and Intelligent Systems Research Group

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package net.sf.jclec.mo.indicator;

import java.util.List;

import org.apache.commons.configuration.Configuration;

import net.sf.jclec.IIndividual;

/**
 * Abstract binary indicator. This kind of measures evaluates the quality of 
 * a Pareto front comparing it with another Pareto front (e.g., the true Pareto front).
 * 
 * <p>HISTORY:
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
 * @see Indicator
 * */

public abstract class BinaryIndicator extends Indicator {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -3468757331373854266L;

	/** The second Pareto front */
	protected double [][] secondFront;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public BinaryIndicator(){
		super();
	}

	/**
	 * Parameterized constructor.
	 * @param paretoSet The Pareto set approximation.
	 * @param paretoSet2 The true Pareto set or the second set required.
	 * @param maximized Maximization problem is required?
	 * @param scaled Scaled objective values are required?
	 * */
	public BinaryIndicator(List<IIndividual> paretoSet, List<IIndividual> paretoSet2, Boolean maximized, Boolean scaled){
		super(paretoSet, maximized, scaled);
		this.secondFront = super.extractFromList(paretoSet2);
	}

	/**
	 * Parameterized constructor.
	 * @param paretoSet The Pareto set approximation.
	 * @param paretoFront2FileName Name of the file that contains the true Pareto front or the second front required.
	 * @param maximized Maximization problem is required?
	 * @param scaled Scaled objective values are required?
	 * */
	public BinaryIndicator(List<IIndividual> paretoSet, String paretoFront2FileName, Boolean maximized, Boolean scaled){
		super(paretoSet, maximized, scaled);
		this.secondFront = super.extractFromFile(paretoFront2FileName);
	}

	/**
	 * Parameterized constructor.
	 * @param paretoFrontFileName Name of the file that contains the Pareto front approximation.
	 * @param paretoSet2 The true Pareto set or the second set required.
	 * @param maximized Maximization problem is required?
	 * @param scaled Scaled objective values are required?
	 * */
	public BinaryIndicator(String paretoFrontFileName, List<IIndividual> paretoSet2, Boolean maximized, Boolean scaled){
		super(paretoFrontFileName, maximized, scaled);
		this.secondFront = super.extractFromList(paretoSet2);
	}

	/**
	 * Parameterized constructor.
	 * @param paretoFrontFileName Name of the file that contains the Pareto front approximation.
	 * @param paretoFront2FileName Name of the file that contains the true Pareto front or the second front required.
	 * @param maximized Maximization problem is required?
	 * @param scaled Scaled objective values are required?
	 * */
	public BinaryIndicator(String paretoFrontFileName, String paretoFront2FileName, Boolean maximized, Boolean scaled){
		super(paretoFrontFileName, maximized, scaled);
		this.secondFront = super.extractFromFile(paretoFront2FileName);
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/Set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Get the second front.
	 * @return The front
	 * */
	public double [][] getSecondFront(){
		return this.secondFront;
	}

	/**
	 * Set the second front.
	 * @param newFront The new front.
	 * */
	public void setSecondFront(double [][] newFront){
		this.secondFront = newFront;
	}
	
	/**
	 * Set the second front from the Pareto set.
	 * @param paretoSet The list of individuals belonging to the Pareto set.
	 * */
	public void setSecondFront(List<IIndividual> paretoSet){
		this.secondFront = extractFromList(paretoSet);
	}

	/**
	 * Get a solution in the second front.
	 * @param index The index of the solution.
	 * @return The solution in position <code>index</code>.
	 * */
	public double [] getSolutionSecondFront(int index){
		return this.secondFront[index];
	}

	/**
	 * Set a solution in the second front.
	 * @param index The index of the solution.
	 * @param solution The new solution.
	 * */
	public void setSolutionSecondFront(int index, double [] solution){
		this.secondFront[index] = solution;
	}

	/**
	 * Get the value of an objective of a solution in the second Pareto front.
	 * @param index The index of the solution.
	 * @param objective The objective position.
	 * @return The objective value of the solution.
	 * */
	public double getValueSecondFront(int index, int objective){
		return this.secondFront[index][objective];
	}

	/**
	 * Set the objective value of a solution in the second front.
	 * @param index The index of the solution.
	 * @param objective The index of the objective.
	 * @param newValue The new value.
	 * */
	public void setValueSecondFront(int index, int objective, double newValue){
		this.secondFront[index][objective] = newValue;
	}

	/**
	 * Get the number of solutions within the second Pareto front.
	 * @return Pareto front size.
	 * */
	public int getNumberOfSolutionsSecondFront(){
		return this.secondFront.length;
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>Binary indicators require the following parameters:
	 * <ul>
	 * 	<li>second-pareto-front (<code>String</code>): 
	 * 	<p>The path of the file containing the second Pareto front.</p></li>
	 * </ul>
	 * </p>
	 * */
	@Override
	public void configure(Configuration settings){
		super.configure(settings);
		String filename = settings.getString("second-pareto-front");
		setSecondFront(extractFromFile(filename));
	}
}
