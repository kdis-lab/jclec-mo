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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.configuration.Configuration;

import net.sf.jclec.mo.evaluation.MOParallelEvaluator;
import net.sf.jclec.mo.evaluation.Objective;

/**
 * Parallel evaluator for the multiobjective Travelling Salesman Problem (TSP). It
 * is a generalized version of the single objective TSP, where the tour
 * is evaluated considering two or more measures (distances, cost...)
 * between the cities (nodes in the graph). For a detailed problem definition, see:
 *  
 * <ul>
 * <li>T. Lust, J. Techem. "The Multiobjective Travelling Salesman Problem: A Survey and a New Approach". 
 * Advances in Multi-Objective Nature Inspired Computing. 2010.</li>
 * <li>L. Paquete, M. Chiarandini, T. Stützle. "Pareto local optimum sets in the biobjective traveling 
 * salesman problem: An experimental study". Metaheuristics for Multiobjective Optimisation. 2004.</li>
 * </ul>
 * 
 * <p>This class loads TSP problem instances using the format defined in: {@link https://eden.dei.uc.pt/~paquete/tsp/}</p>
 * 
 * <p>Note: Many problem instances are available at the website, where each file contains the 2D coordinates
 * of a set of cities. Thus, each objective measures the total euclidean distance of the tour for a given set
 * of cities. However, this class can be used as a basis for other kind of data (e.g. matrix of cost), without
 * requiring different objective functions.</p>
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
 * @see MOParallelEvaluator
 * @see MOTSPObjective
 * */
public class MOTSPParallelEvaluator extends MOParallelEvaluator {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -2605700366482697363L;

	/////////////////////////////////////////////////////////////////
	//-------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public MOTSPParallelEvaluator(){
		super();
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>A specific parameter of this problem is:
	 * <ul>
	 * 	<li>problem-instances (Complex): List of problem instances,
	 * where each item should provide a path to the file name
	 * that contains the 2D coordinates of the set of cities. It
	 * uses the tag 'problem-instance' for each file.</li>
	 * </ul>
	 * </p>
	 * */
	@Override
	public void configure(Configuration settings) {
	
		// Call super method
		super.configure(settings);
		
		// Get the list of problem instances
		Configuration objSettings = settings.subset("problem-instances");
		String filenames [] = objSettings.getStringArray("problem-instance");
		int size = filenames.length;
		
		// Load each instance and copy it to the corresponding objective
		double [][] matrix;
		Objective obj;
		
		for(int i=0; i<size; i++){
			matrix = loadProblemInstance(filenames[i]);
			obj = this.getObjectives().get(i);
			if(obj instanceof MOTSPObjective){
				((MOTSPObjective)obj).setMatrixofValues(matrix);
			}
		}
	}
	
	/**
	 * Load the coordinates of the cities in the file
	 * and calculate the euclidean distances.
	 * @param filename Name of the file.
	 * @return Matrix of distances.
	 * */
	private double [][] loadProblemInstance(String filename){

		BufferedReader reader;
		String line;
		String [] aux;
		File file = new File(filename);
		int i = 0, j, nCities=0;
		double [] x;
		double [] y;
		double [][] distances = null;
		
		try {
			reader = new BufferedReader(new FileReader(file));

			// Read the number of cities
			while(!(line=reader.readLine()).contains("DIMENSION"));
			aux = line.split(" ");
			nCities = Integer.parseInt(aux[1]);

			distances = new double [nCities][nCities];
			x = new double[nCities];
			y = new double[nCities];

			// Read the coordinates
			while(!(line=reader.readLine()).contains("NODE_COORD_SECTION"));
			line=reader.readLine();
			while(!(line=reader.readLine()).contains("EOF")){
				aux = line.split(" ");
				x[i]=Double.parseDouble(aux[1]);
				y[i]=Double.parseDouble(aux[2]);
				i++;
			}
			
			reader.close();

			// Calculate euclidean distances
			for(i=0;i<nCities;i++){
				for(j=i+1;j<nCities;j++){
					distances[i][j] = Math.sqrt(Math.pow(x[i]-x[j],2)+Math.pow(y[i]-y[j],2));
					distances[j][i] = distances[i][j];
				}
			}

		}catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e){
			e.printStackTrace();
		}
		
		return distances;
	}
}
