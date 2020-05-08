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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.configuration.Configuration;

import net.sf.jclec.mo.evaluation.MOEvaluator;
import net.sf.jclec.mo.evaluation.Objective;

/**
 * Evaluator for the multiobjective knapsack problem. It
 * is a generalized version of the single objective knapsack
 * problem, where more than one knapsack can be considered.
 * For a detailed problem definition, see: 
 * 
 * <p><i>Paper</i>: E. Ziztler, L. Thiele. "Multiobjective Evolutionary Algorithm:
 *  A comparative Case Study and the Strength Pareto Approach". IEEE Transactions
 *  on Evolutionary Computation, vol. 3, no. 4, pp. 257-271. 1999.</p>
 * 
 * <p>This class requires a problem instance stored in a file with the format
 * defined by E. Zitzler and M. Laumanns in their test problem suite:
 * {@link http://www.tik.ee.ethz.ch/sop/download/supplementary/testProblemSuite}</p>
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
 * @see MOEvaluator
 * @see MOKnapsackObjective
 * */
public class MOKnapsackEvaluator extends MOEvaluator {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -6807191626260228772L;

	/** Matrix of profits, where each row stores the profits 
	 * of the items of a different knapsack */
	protected double [][] profits;

	/** Matrix of weights, where each row stores the weights 
	 * of the items of a different knapsack */
	protected double [][] weights;

	/** Capacities of the n knapsacks */
	protected double [] capacities;

	/////////////////////////////////////////////////////////////////
	//-------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public MOKnapsackEvaluator(){
		super();
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>A specific parameter of this problem is:
	 * <ul>
	 * 	<li>problem-instance (<code>String</code>): The path of the file containing the problem instance.</li>
	 * </ul>
	 * */
	@Override
	public void configure(Configuration settings) {
		super.configure(settings);

		// Load the problem instance
		try{
			String filename = settings.getString("problem-instance");
			loadProblemInstance(filename);
		}catch(IllegalArgumentException e){
			System.err.println("A problem instance is required");
			e.printStackTrace();
		}

		// Check the number of objectives
		if(numberOfObjectives()!=capacities.length){
			throw new IllegalArgumentException("The problem instance does not match with the number of objectives");
		}
		
		// Configure additional information in the objectives
		try{
			setProblemInformation();
		}catch(IllegalArgumentException e){
			e.printStackTrace();
		}
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Private methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Read the file that contains the problem instance.
	 * @param filename The file name.
	 * */
	private void loadProblemInstance(String filename){
		
		BufferedReader reader;
		int nKnapsacks, nItems;
		String line;
		String [] aux;
		File file = new File(filename);
		int i, j;
		
		try {
			reader = new BufferedReader(new FileReader(file));
		
			// From the first line, get the number of knapsacks and items
			line = reader.readLine();
			aux = line.split(" ");
			nKnapsacks = Integer.parseInt(aux[3].substring(1));
			nItems = Integer.parseInt(aux[5]);
			
			// Create the matrices
			this.profits = new double[nKnapsacks][nItems];
			this.weights = new double[nKnapsacks][nItems];
			this.capacities = new double[nKnapsacks];
			
			// Read the rest of the problem instance
			i=-1; j=-1;
			line = reader.readLine();
			while(line!=null){
				
				// A new knapsack
				if(line.contains("knapsack")){
					i++;
					j=-1;
				}
				
				// Read the capacity of knapsack i
				else if(line.contains("capacity")){
					aux = line.split(" ");
					this.capacities[i] = Double.parseDouble(aux[2]);
				}
				
				// Read the beginning of a new item for knapsack i
				else if(line.contains("item")){
					j++;
				}
				
				// Read the weight of item j
				else if(line.contains("weight")){
					aux = line.split(" ");
					this.weights[i][j] = Double.parseDouble(aux[3]);
				}
				
				// Read the profit of item j
				else if(line.contains("profit")){
					aux = line.split(" ");
					this.profits[i][j] = Double.parseDouble(aux[3]);
				}	
				
				line = reader.readLine();
			}
			reader.close();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e){
			e.printStackTrace();
		}
	}

	/**
	 * Configure the problem instance data in the objective functions.
	 * @throws IllegalArgumentException When an objective function is not a <code>MOKnapsackObjective</code> object.
	 * */
	private void setProblemInformation() throws IllegalArgumentException {

		// Configure the profits and weights in the objective functions
		int size = this.objectives.size();
		Objective obj;
		for(int i=0; i<size; i++){
			obj = this.objectives.get(i);
			if(obj instanceof MOKnapsackObjective){
				((MOKnapsackObjective)obj).setCapacity(this.capacities[i]);
				((MOKnapsackObjective)obj).setProfits(this.profits[i]);
				((MOKnapsackObjective)obj).setWeights(this.weights[i]);
			}
			else
				throw new IllegalArgumentException("Objective " + i + " is not a MOKnapsackObjective object");
		}
	}
}
