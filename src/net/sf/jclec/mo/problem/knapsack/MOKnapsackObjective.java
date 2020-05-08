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

package net.sf.jclec.mo.problem.knapsack;

import net.sf.jclec.IFitness;
import net.sf.jclec.IIndividual;
import net.sf.jclec.binarray.BinArrayIndividual;
import net.sf.jclec.fitness.SimpleValueFitness;
import net.sf.jclec.mo.IConstrained;
import net.sf.jclec.mo.evaluation.Objective;

/**
 * The objective function for the multi-objective knapsack problem. 
 * It computes the total profit of a given knapsack, also differentiating
 * between feasible and infeasible solutions. Solutions exceeding the capacity
 * constraint will be set as infeasible and their constraint violation degree
 * will be equal to the difference between the current weight and the maximum
 * capacity of the knapsack.
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
 * @see Objective
 * @see MOKnapsackEvaluator
 * @see IConstrained
 * */

public class MOKnapsackObjective extends Objective {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 8354665764011699992L;

	/** Profits of the items in the knapsack */
	private double [] profits;

	/** Weights of the items in the knapsack */
	private double [] weights;

	/** Capacity of the knapsack */
	private double capacity;

	/////////////////////////////////////////////////////////////////
	//-------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public MOKnapsackObjective(){
		super();
	}

	/////////////////////////////////////////////////////////////////
	//----------------------------------------------- Set/get methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Set the array of profits.
	 * @param profits Array of profits.
	 * */
	protected void setProfits(double [] profits){
		this.profits = profits;
	}

	/**
	 * Set the array of weights.
	 * @param weigths Array of weights.
	 * */
	protected void setWeights(double [] weigths){
		this.weights = weigths;	
	}

	/**
	 * Set the capacity of the knapsack.
	 * @param capacity The capacity
	 * */
	protected void setCapacity(double capacity){
		this.capacity = capacity;
	}

	/////////////////////////////////////////////////////////////////
	//---------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public IFitness evaluate(IIndividual solution) {
		IFitness fitness = null;
		byte [] genotype;
		int length;
		double totalProfit = 0.0, totalWeight = 0.0;
		double currentExcess, previousExcess;

		// Check if the solution has a valid encoding
		if(solution instanceof BinArrayIndividual){

			// Get the binary genotype
			genotype = ((BinArrayIndividual) solution).getGenotype();
			length = genotype.length;

			// Calculate the profit for this knapsack
			for(int i=0; i<length; i++){
				totalProfit += genotype[i]*this.profits[i];
				totalWeight += genotype[i]*this.weights[i];
			}

			// Set the fitness value
			fitness = new SimpleValueFitness(totalProfit);

			// Check the weight constraint
			if(totalWeight <= this.capacity){
				((IConstrained)solution).setFeasible(true);
				((IConstrained)solution).setDegreeOfInfeasibility(0.0);
			}
			else{
				((IConstrained)solution).setFeasible(false);
				// The maximum excess (considering all the objectives)
				// will be set as the degree of infeasibility
				previousExcess = ((IConstrained)solution).degreeOfInfeasibility();
				currentExcess = totalWeight-this.capacity;
				if(currentExcess > previousExcess)
					((IConstrained)solution).setDegreeOfInfeasibility(currentExcess);
			}
		}
		return fitness;
	}
}
