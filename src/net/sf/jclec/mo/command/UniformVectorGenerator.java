/*
This file belongs to JCLEC-MO, a Java library for the
application and development of metaheuristic algorithms 
for the resolution of multi-objective and many-objective 
optimization problems.

Copyright (C) 2018. A. Ramirez, J.R. Romero, S. Ventura.
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

package net.sf.jclec.mo.command;

import java.util.ArrayList;
import java.util.List;

/**
 * A command to generate a set of uniformly distributed reference vectors.
 * This method is used in decomposition algorithms like MOEA/D.
 * 
 * <p>The relation between the number of objectives and the control
 * parameter H is as follows: if k=#objectives-1 and n=H+#objectives-1,
 * then the number of vectors that will be generated is:
 * C_{k}_{n} = n!/(n-k)!*k! (subject to n>=k).</p>
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
 * @see Command
 * */

public class UniformVectorGenerator extends Command {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 1L;

	/** Number of objectives */
	protected int numberOfObjectives;

	/** Control parameter H */
	protected int h;

	/** List of reference vectors */
	protected List<double []> uniformVectors;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor
	 * */
	public UniformVectorGenerator() {
		super(null);
	}

	/**
	 * Parameterized constructor
	 * @param numObjs Number of objectives
	 * @param h Control parameter H
	 * */
	public UniformVectorGenerator(int numObjs, int h) {
		super(null);
		this.numberOfObjectives = numObjs;
		this.h = h;
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Set the value of H parameter
	 * @param h New value
	 * */
	public void setH(int h) {
		this.h = h;
	}

	/**
	 * Set the number of objectives
	 * @param numObjs Number of objectives
	 * */
	public void setNumberOfObjectives(int numObjs) {
		this.numberOfObjectives = numObjs;
	}

	/**
	 * Get the list of reference vector
	 * @return List of reference vector
	 * */
	public List<double []> getUniformVectors(){
		return this.uniformVectors;
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void execute() {
		ArrayList<Double> hIntervals = generateIntervals();
		int numberOfValues = hIntervals.size();
		int numberOfCombinations = (int)Math.pow(numberOfValues,this.numberOfObjectives);
		int consecutiveElements=numberOfCombinations;
		int accProduct = 1;
		int step;
		int [][] allCombinationsMatrix = new int[numberOfCombinations][this.numberOfObjectives];

		// Expand all possible combinations
		for(int i=0; i<this.numberOfObjectives; i++){
			consecutiveElements /= numberOfValues;
			for(int j = 0; j < numberOfValues; j++){
				for(int k = 0; k < consecutiveElements; k++){
					for(int r = 0; r < accProduct; r++){
						step = consecutiveElements*numberOfValues;
						allCombinationsMatrix[r*step + j*consecutiveElements + k][i] = j;
					}
				}
			}
			accProduct *= numberOfValues;
		}

		// Create the uniform vectors
		double sum = 0.0;
		double [] weight;
		this.uniformVectors = new ArrayList<double []>();
		for(int i=0; i<numberOfCombinations; i++){
			sum=0.0;
			// Get the sum of the weights in each combination
			weight = new double[this.numberOfObjectives];
			for(int j=0; j<this.numberOfObjectives; j++){
				sum += hIntervals.get(allCombinationsMatrix[i][j]);
			}

			// The combination is an uniform vector (sum==1)
			if(Math.abs(sum-1.0)<0.00000001){
				for(int j=0; j<this.numberOfObjectives; j++){
					weight[j]=hIntervals.get(allCombinationsMatrix[i][j]);
				}
				this.uniformVectors.add(weight);
			}			
		}
	}

	/////////////////////////////////////////////////////////////////
	// -------------------------------------------- Protected methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Generate the equally spaced intervals
	 * using the H parameter.
	 * @return Intervals for weight vectors.
	 * */
	protected ArrayList<Double> generateIntervals(){
		ArrayList<Double> values = new ArrayList<Double>();
		for(int i=0; i<=this.h; i++){
			values.add((double)i/this.h);
		}
		return values;
	}
}
