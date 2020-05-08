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

package net.sf.jclec.mo.indicator;

import java.util.List;

import org.apache.commons.configuration.Configuration;

import net.sf.jclec.IIndividual;

/**
 * Abstract ternary indicator. This kind of measures evaluates the quality of 
 * a Pareto front comparing it with another Pareto front. In addition, it requires 
 * another front (e.g., the true Pareto front) to obtain the result.
 *  
 * <p>HISTORY:
 * <ul>
 * <li>(AR|JRR|SV, 1.0, March 2018)		First release.</li>
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

public abstract class TernaryIndicator extends Indicator {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 2621028386383879006L;

	/** The second Pareto front */
	protected double [][] secondFront;

	/** The true Pareto front */
	protected double [][] trueFront;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public TernaryIndicator(){
		super();
	}

	/**
	 * Parameterized constructor.
	 * @param paretoSet The first Pareto set approximation.
	 * @param paretoSet2 The second Pareto set approximation.
	 * @param trueParetoSet The true Pareto set.
	 * @param maximized Maximization problem is required?
	 * @param scaled Scaled objective values are required?
	 * */
	public TernaryIndicator(List<IIndividual> paretoSet, List<IIndividual> paretoSet2, List<IIndividual> trueParetoSet, Boolean maximized, Boolean scaled){
		super(paretoSet, maximized, scaled);
		this.secondFront = super.extractFromList(paretoSet2);
		this.trueFront = super.extractFromList(trueParetoSet);
	}

	/**
	 * Parameterized constructor.
	 * @param paretoSet The first Pareto set approximation.
	 * @param paretoSet2 The second Pareto set approximation.
	 * @param trueParetoFrontFileName Name of the file that contains the true Pareto front.
	 * @param maximized Maximization problem is required?
	 * @param scaled Scaled objective values are required?
	 * */
	public TernaryIndicator(List<IIndividual> paretoSet, List<IIndividual> paretoSet2, String trueParetoFrontFileName, Boolean maximized, Boolean scaled){
		super(paretoSet, maximized, scaled);
		this.secondFront = super.extractFromList(paretoSet2);
		this.trueFront = super.extractFromFile(trueParetoFrontFileName);
	}

	/**
	 * Parameterized constructor.
	 * @param paretoSet The Pareto set approximation.
	 * @param paretoFront2FileName Name of the file that contains the second Pareto front required.
	 * @param trueParetoSet The true Pareto set.
	 * @param maximized Maximization problem is required?
	 * @param scaled Scaled objective values are required?
	 * */
	public TernaryIndicator(List<IIndividual> paretoSet, String paretoFront2FileName, List<IIndividual> trueParetoSet, Boolean maximized, Boolean scaled){
		super(paretoSet, maximized, scaled);
		this.secondFront = super.extractFromFile(paretoFront2FileName);
		this.trueFront = super.extractFromList(trueParetoSet);
	}

	/**
	 * Parameterized constructor.
	 * @param paretoSet The Pareto set approximation.
	 * @param paretoFront2FileName Name of the file that contains the second Pareto front required.
	 * @param trueParetoFrontFileName Name of the file that contains the true Pareto front.
	 * @param maximized Maximization problem is required?
	 * @param scaled Scaled objective values are required?
	 * */
	public TernaryIndicator(List<IIndividual> paretoSet, String paretoFront2FileName, String trueParetoFrontFileName, Boolean maximized, Boolean scaled){
		super(paretoSet, maximized, scaled);
		this.secondFront = super.extractFromFile(paretoFront2FileName);
		this.trueFront = super.extractFromFile(trueParetoFrontFileName);
	}

	/**
	 * Parameterized constructor.
	 * @param paretoFrontFileName Name of the file that contains the first Pareto front approximation.
	 * @param paretoSet2 The second Pareto set approximation.
	 * @param trueParetoSet The true Pareto set.
	 * @param maximized Maximization problem is required?
	 * @param scaled Scaled objective values are required?
	 * */
	public TernaryIndicator(String paretoFrontFileName, List<IIndividual> paretoSet2, List<IIndividual> trueParetoSet, Boolean maximized, Boolean scaled){
		super(paretoFrontFileName, maximized, scaled);
		this.secondFront = super.extractFromList(paretoSet2);
		this.trueFront = super.extractFromList(trueParetoSet);
	}

	/**
	 * Parameterized constructor.
	 * @param paretoFrontFileName Name of the file that contains the first Pareto front approximation.
	 * @param paretoSet2 The second Pareto set approximation.
	 * @param trueParetoFrontFileName Name of the file that contains the true Pareto front.
	 * @param maximized Maximization problem is required?
	 * @param scaled Scaled objective values are required?
	 * */
	public TernaryIndicator(String paretoFrontFileName, List<IIndividual> paretoSet2, String trueParetoFrontFileName, Boolean maximized, Boolean scaled){
		super(paretoFrontFileName, maximized, scaled);
		this.secondFront = super.extractFromList(paretoSet2);
		this.trueFront = super.extractFromFile(trueParetoFrontFileName);
	}

	/**
	 * Parameterized constructor.
	 * @param paretoFrontFileName Name of the file that contains the first Pareto front approximation.
	 * @param paretoFront2FileName Name of the file that contains the second Pareto front required.
	 * @param trueParetoSet The true Pareto set.
	 * @param maximized Maximization problem is required?
	 * @param scaled Scaled objective values are required?
	 * */
	public TernaryIndicator(String paretoFrontFileName, String paretoFront2FileName, List<IIndividual> trueParetoSet, Boolean maximized, Boolean scaled){
		super(paretoFrontFileName, maximized, scaled);
		this.secondFront = super.extractFromFile(paretoFront2FileName);
		this.trueFront = super.extractFromList(trueParetoSet);
	}

	/**
	 * Parameterized constructor.
	 * @param paretoFrontFileName Name of the file that contains the first Pareto front approximation.
	 * @param paretoFront2FileName Name of the file that contains the second Pareto front required.
	 * @param trueParetoFrontFileName Name of the file that contains the true Pareto front.
	 * @param maximized Maximization problem is required?
	 * @param scaled Scaled objective values are required?
	 * */
	public TernaryIndicator(String paretoFrontFileName, String paretoFront2FileName, String trueParetoFrontFileName, Boolean maximized, Boolean scaled){
		super(paretoFrontFileName, maximized, scaled);
		this.secondFront = super.extractFromFile(paretoFront2FileName);
		this.trueFront = super.extractFromFile(trueParetoFrontFileName);
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

	/**
	 * Get the true Pareto front.
	 * @return The front
	 * */
	public double [][] getTrueFront(){
		return this.trueFront;
	}

	/**
	 * Set the true Pareto front.
	 * @param newFront The new front.
	 * */
	public void setTrueFront(double [][] newFront){
		this.trueFront = newFront;
	}
	
	/**
	 * Set the true front from the Pareto set.
	 * @param paretoSet The list of individuals belonging to the Pareto set.
	 * */
	public void setTrueFront(List<IIndividual> paretoSet){
		this.trueFront = extractFromList(paretoSet);
	}

	/**
	 * Get a solution in the true Pareto front.
	 * @param index The index of the solution.
	 * @return The solution in position <code>index</code>.
	 * */
	public double [] getSolutionTrueFront(int index){
		return this.trueFront[index];
	}

	/**
	 * Set a solution in the true Pareto front.
	 * @param index The index of the solution.
	 * @param solution The new solution.
	 * */
	public void setSolutionTrueFront(int index, double [] solution){
		this.trueFront[index] = solution;
	}

	/**
	 * Get the value of an objective of a solution in the true Pareto front.
	 * @param index The index of the solution.
	 * @param objective The objective position.
	 * @return The objective value of the solution.
	 * */
	public double getValueTrueFront(int index, int objective){
		return this.trueFront[index][objective];
	}

	/**
	 * Set the objective value of a solution in the true Pareto front.
	 * @param index The index of the solution.
	 * @param objective The index of the objective.
	 * @param newValue The new value.
	 * */
	public void setValueTrueFront(int index, int objective, double newValue){
		this.trueFront[index][objective] = newValue;
	}

	/**
	 * Get the number of solutions within the true Pareto front.
	 * @return Pareto front size.
	 * */
	public int getNumberOfSolutionsTrueFront(){
		return this.trueFront.length;
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void configure(Configuration settings){
		super.configure(settings);
	}
}
