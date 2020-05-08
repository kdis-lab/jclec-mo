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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.configuration.Configuration;

import net.sf.jclec.IConfigure;
import net.sf.jclec.IIndividual;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;

/**
 * Abstract indicator for measure the quality of the Pareto front. 
 * It receives the Pareto set in a list or the file name containing
 * the set. Specific indicators, either unary or binary, are implemented
 * as subclasses.
 * 
 * <p><i>Reference book</i>: C.A. Coello, Coello, G.B. Lamont, D.A. Van Veldhuizen, 
 * “Evolutionary Algorithms for Solving Multi-Objective Problems”. 2nd Edition.
 * Genetic and Evolutionary Computation Series. Springer. 2007.</p>
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
 * @see IConfigure
 * */

public abstract class Indicator implements IConfigure{

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -488234718666635626L;

	/** The Pareto front is stored in matrix form, where rows
	 * are the solutions and columns, the objectives. */
	protected double [][] front;

	/** The result of the indicator */
	protected double result;

	/** Indicates whether the indicator requires a maximization
	 * formulation of the optimization problem. */
	protected Boolean maximized;

	/** Indicates whether the indicator requires an objective space in the range [0,1]. */
	protected Boolean scaled;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public Indicator(){
		this.result = -1;
		this.maximized = null;
		this.scaled = null;
	}

	/**
	 * Parameterized constructor.
	 * @param paretoSet The Pareto set approximation.
	 * @param maximized Maximization problem is required?
	 * @param scaled Scaled objective values are required?
	 * */
	public Indicator(List<IIndividual> paretoSet, Boolean maximized, Boolean scaled){
		this.front = extractFromList(paretoSet);
		this.maximized = maximized;
		this.scaled = scaled;
		this.result = -1;
	}

	/**
	 * Parameterized constructor.
	 * @param paretoFrontFileName Name of the file that contains the Pareto front.
	 * @param maximized Maximization problem is required?
	 * @param scaled  Scaled objective values are required?
	 * */
	public Indicator(String paretoFrontFileName, Boolean maximized, Boolean scaled){
		this.front = extractFromFile(paretoFrontFileName);
		this.maximized = maximized;
		this.scaled = scaled;
		this.result = -1;
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/Set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Get the result of the indicator.
	 * @return The result of the indicator, -1 if the
	 * indicator has not been calculated yet. 
	 * */
	public double getResult(){
		return this.result;
	}

	/**
	 * Set the result of the indicator.
	 * @param result The value that has to be set.
	 * */
	protected void setResult(double result){
		this.result = result;
	}

	/**
	 * Get the maximization problem flag.
	 * @return True if the indicator requires a maximization
	 * problem definition, false otherwise.
	 * */
	public Boolean requiresMaxProblem(){
		return this.maximized;
	}

	/**
	 * Get the scale problem flag.
	 * @return True if the indicator requires scaled
	 * objective values, false otherwise.
	 * */
	public Boolean requiresScaledObjectives(){
		return this.scaled;
	}

	/**
	 * Get the front.
	 * @return The front.
	 * */
	public double [][] getFront(){
		return this.front;
	}

	/**
	 * Set the front.
	 * @param newFront The new front.
	 * */
	public void setFront(double [][] newFront){
		this.front = newFront;
	}

	/**
	 * Set the front from the Pareto set.
	 * @param paretoSet The list of individuals belonging to the Pareto set.
	 * */
	public void setFront(List<IIndividual> paretoSet){
		this.front = extractFromList(paretoSet);
	}

	/**
	 * Get a solution in the front.
	 * @param index The index of the solution.
	 * @return The solution in position <code>index</code>.
	 * */
	public double [] getSolutionFront(int index){
		return this.front[index];
	}

	/**
	 * Set a solution in the front.
	 * @param index The index of the solution.
	 * @param values The objective values that have to be set.
	 * */
	public void setSolutionFront(int index, double [] values){
		this.front[index] = values;
	}

	/**
	 * Get the value of an objective of a solution.
	 * @param index The index of the solution.
	 * @param objective The objective position.
	 * @return The objective value of the solution.
	 * */
	public double getValueFront(int index, int objective){
		return this.front[index][objective];
	}

	/**
	 * Set the objective value of a solution in the front.
	 * @param index The index of the solution.
	 * @param objective The index of the objective.
	 * @param value The value that has to be set.
	 * */
	public void setValueFront(int index, int objective, double value){
		this.front[index][objective] = value;
	}

	/**
	 * Get the number of solutions within the Pareto set.
	 * @return Pareto set size.
	 * */
	public int getNumberOfSolutions(){
		return this.front.length;
	}

	/**
	 * Get the number of objectives in the
	 * optimization problem.
	 * @return The number of objectives.
	 * */
	public int getNumberOfObjectives(){
		return this.front[0].length;
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Abstract methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Compute the indicator value.
	 * */
	public abstract void calculate();

	/////////////////////////////////////////////////////////////////
	// -------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void configure(Configuration settings) {
		// Do nothing 
	}

	/////////////////////////////////////////////////////////////////
	// -------------------------------------------- Protected methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Extract the Pareto front from the Pareto set.
	 * @param paretoSet The individuals within the Pareto set.
	 * @return A matrix of <code>double<code> values that contains the objective
	 * values of all the solutions.
	 * */
	public double [][] extractFromList(List<IIndividual> paretoSet) {
		int nSolutions = paretoSet.size();
		double [][] front;
		if(nSolutions > 0){
			int nObjectives = ((MOFitness)paretoSet.get(0).getFitness()).getNumberOfObjectives();
			front = new double [nSolutions][nObjectives];
			MOFitness fitness;
			for(int i=0; i<nSolutions; i++){
				fitness = (MOFitness)paretoSet.get(i).getFitness();
				for(int j=0; j<nObjectives; j++){
					try {
						front[i][j] = fitness.getObjectiveDoubleValue(j);
					} catch (IllegalAccessException | IllegalArgumentException e) {
						e.printStackTrace();
					}
				}
			}
		}
		else
			front = new double[0][0];
		return front;
	}

	/**
	 * Extract the Pareto front from a file (.txt, .csv). The file
	 * should contain a solution in each line, where the
	 * objective values must be separated by commas. The name of
	 * the objectives can be included as well when using the CSV format.
	 *   
	 * @param fileName The name of the file where the Pareto set is stored.
	 * @return A matrix of <code>double<code> values that contains the objective
	 * values of all the solutions.
	 * */
	public double [][] extractFromFile(String fileName){

		StringTokenizer tokenizer; 
		List<ArrayList<Double>> front  = null;
		File file = new File(fileName);
		String line;
		int nObjectives = -1;
		int i = 0;

		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));

			// Get the number of objectives from the first solution
			front = new ArrayList<ArrayList<Double>>();
			line = reader.readLine();
			if(line!=null){
				tokenizer = new StringTokenizer(line, ",");
				nObjectives = tokenizer.countTokens();

				// Check if the first line contains the objectives names
				@SuppressWarnings("unused")
				double value;
				try{
					value = Double.parseDouble(tokenizer.nextToken());
				}catch(NumberFormatException e){
					// Skip this line
					line = reader.readLine();
				}
			}
			else{
				reader.close();
				System.err.println("Wrong format of the coordinates of solutions");
				System.exit(-1);
			}

			// Read the all the solutions
			i=0;
			while(line!=null){
				tokenizer = new StringTokenizer(line, ",");
				if(tokenizer.countTokens() == nObjectives){
					front.add(new ArrayList<Double>());
					for(int j=0; j<nObjectives; j++){
						try{
							front.get(i).add(Double.parseDouble(tokenizer.nextToken()));
						}catch(NumberFormatException e){
							reader.close();
							System.err.println("Wrong format of the coordinates of non-dominated solutions");
							e.printStackTrace();
						}
					}
					i++;
				}
				else{
					reader.close();
					throw new IllegalArgumentException("Missing solution or wrong format.");
				}
				// next line
				line = reader.readLine();
			}

			// Close reader
			reader.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e){
			e.printStackTrace();
		}

		// To array
		int nSolutions = front.size();
		double [][] points = new double[nSolutions][nObjectives];
		for(i=0; i<nSolutions; i++){
			for(int j=0; j<nObjectives; j++){
				points[i][j] = front.get(i).get(j);
			}
		}
		return points;
	}
}
