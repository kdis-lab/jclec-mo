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

package net.sf.jclec.mo.problem.tsp;

import net.sf.jclec.IFitness;
import net.sf.jclec.IIndividual;
import net.sf.jclec.fitness.SimpleValueFitness;
import net.sf.jclec.mo.evaluation.Objective;
import net.sf.jclec.orderarray.OrderArrayIndividual;

/**
 * The objective function for the multiobjective TSP. It computes a fitness value for the 
 * tour using a generic matrix of data. These data that can represent distances, cost, etc.
 * between each pair of cities (nodes in the graph).
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
 * @see MOTSPEvaluator
 * @see Objective
 * */
public class MOTSPObjective extends Objective {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -578350666233627961L;

	/** Matrix containing the data of a specific measure between
	 * two nodes in the TSP problem (distances, cost...) */
	private double [][] measure;

	/////////////////////////////////////////////////////////////////
	//-------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public MOTSPObjective(){
		super();
	}

	/////////////////////////////////////////////////////////////////
	//----------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Set the matrix of values (distances, costs...). It
	 * copies the values for the given matrix.
	 * @param matrix The matrix of values.
	 * */
	public void setMatrixofValues(double [][] matrix){
		int rows = matrix.length;
		int cols = matrix[0].length;
		this.measure = new double[rows][cols];
		for(int i=0; i<rows; i++){
			for(int j=0; j<cols; j++){
				this.measure[i][j] = matrix[i][j];	
			}
		}
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
		int [] genotype;
		int length;

		// Check the genotype encoding
		if(solution instanceof OrderArrayIndividual){

			// Get the genotype
			genotype = ((OrderArrayIndividual)solution).getGenotype();
			double totalValue = 0.0;

			// Calculate total value of the measure
			length = genotype.length-1;
			for (int i=0; i<length; i++) 
				totalValue += this.measure[genotype[i]][genotype[i+1]];
			totalValue += this.measure[genotype[length]][genotype[0]];

			fitness = new SimpleValueFitness(totalValue);
		}
		return fitness;
	}
}
