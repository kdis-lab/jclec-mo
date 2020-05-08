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

package net.sf.jclec.mo.experiment.handler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.uco.kdis.datapro.algorithm.preprocessing.instance.RemoveDuplicates;
import es.uco.kdis.datapro.dataset.Dataset;
import es.uco.kdis.datapro.dataset.column.NumericalColumn;
import es.uco.kdis.datapro.dataset.source.CsvDataset;
import es.uco.kdis.datapro.exception.IllegalFormatSpecificationException;
import es.uco.kdis.datapro.exception.NotAddedValueException;

/**
 * This handler constructs a unique Pareto front from a set of fronts returned
 * by different executions of the same algorithm.
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
 * @see MOExperimentHandler
 * */
public class GenerateAlgorithmPF extends MOExperimentHandler {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** The set of Pareto fronts */
	protected List<Dataset> paretoFronts;

	/** The characteristics of the multi-objective problem */
	protected boolean [] mop;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Parameterized constructor.
	 * @param directoryName The path to the reporting directory.
	 * @param mop The type of objective functions (true=maximisation, false=minimisation).
	 * */
	public GenerateAlgorithmPF(String directoryName, boolean [] mop) {
		super(directoryName);
		this.mop = mop;
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void process() {

		// Read the PFs for each algorithm
		readParetoFronts();

		// Remove duplicated solutions
		removeDuplicatedSolutions();

		// Remove dominated solutions
		removeDominatedSolutions();

		// Save the summary PF for each algorithm
		saveDatasets();

		// Call the successor
		if(nextHandler() != null){
			nextHandler().process();
		}
	}

	/////////////////////////////////////////////////////////////////
	// -------------------------------- Protected and private methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Load the Pareto fronts from the reporting directory
	 * */
	protected void readParetoFronts(){

		File [] algSubFolders;
		File [] algParetoFronts;
		File subDirectory;
		Dataset dataset = null;
		Dataset result;

		int numberOfObjectives = this.mop.length;
		String sColumnFormat = new String();
		for(int i=0; i<numberOfObjectives; i++){
			sColumnFormat+="f";
		}

		// Check if this handler can process the request
		if(this.directory.exists() && this.directory.isDirectory()){

			this.paretoFronts = new ArrayList<Dataset>();
			algSubFolders = this.directory.listFiles();

			// For each report, create a dataset to save all the PFs found by the same algorithm
			for(int i=0; i<algSubFolders.length; i++){
				subDirectory = new File(algSubFolders[i].getAbsolutePath()+"/pareto-fronts");
				if(subDirectory.exists() && subDirectory.isDirectory()){
					algParetoFronts = subDirectory.listFiles();

					// An empty dataset to store all the solutions found
					result = new CsvDataset();
					for(int j=0; j<numberOfObjectives; j++){
						result.addColumn(new NumericalColumn());
					}

					for(int j=0; j<algParetoFronts.length; j++){
						if(algParetoFronts[j].getName().contains("-final")){
							dataset = new CsvDataset(algParetoFronts[j].getAbsolutePath());
							try {
								((CsvDataset)dataset).readDataset("nv", sColumnFormat);
							} catch (IndexOutOfBoundsException | IOException
									| NotAddedValueException | IllegalFormatSpecificationException e) {
								e.printStackTrace();
							}

							// Add all values
							for(int k=0; k<numberOfObjectives; k++){
								result.getColumn(k).addAllValues(dataset.getColumn(k).getValues());
							}
						}
					}

					// Set the objective names
					for(int k=0; k<numberOfObjectives; k++){
						result.getColumn(k).setName(dataset.getColumn(k).getName());
					}

					// Set the dataset name
					result.setName(algSubFolders[i].getName());

					// Add the dataset to the list
					this.paretoFronts.add(result);
				}
			}
		}	
	}

	/**
	 * Remove the dominated solutions in each Pareto front. 
	 * */
	protected void removeDominatedSolutions() {
		int nAlgs = this.paretoFronts.size();
		int nSol;
		Dataset dataset, copy;
		boolean isDominated;
		int numberOfObjs = this.mop.length;
		double solution1[], solution2[];

		for(int i=0; i<nAlgs; i++){
			dataset = this.paretoFronts.get(i);
			nSol = dataset.getColumn(0).getSize();
			copy = dataset.clone();

			for(int j=0; j<nSol; j++){
				isDominated = false;

				// get the first solution
				solution1 = new double[numberOfObjs];
				for(int n=0; n<numberOfObjs; n++){
					solution1[n] = (double)dataset.getColumn(n).getElement(j);
				}

				for(int k=0; !isDominated && k<nSol; k++){
					// get the second solution
					if(j!=k){
						solution2 = new double[numberOfObjs];
						for(int n=0; n<numberOfObjs; n++){
							solution2[n] = (double)dataset.getColumn(n).getElement(k);
						}
						// check the dominance
						if(dominatedBy(solution1,solution2)){
							isDominated = true;
						}
					}
				}
				if(!isDominated){
					for(int k=0; k<numberOfObjs; k++){
						copy.getColumn(k).addValue(dataset.getColumn(k).getElement(j));
					}
				}
			}
			// Set the copy that only contains non-dominated solutions
			this.paretoFronts.set(i, copy);
		}
	}

	/**
	 * Remove duplicated solutions.
	 * */
	protected void removeDuplicatedSolutions() {
		RemoveDuplicates algorithm;
		int size = this.paretoFronts.size();
		for(int i=0; i<size; i++){
			algorithm = new RemoveDuplicates(this.paretoFronts.get(i));
			algorithm.initialize();
			algorithm.execute();
			algorithm.postexec();
			this.paretoFronts.set(i, (Dataset)algorithm.getResult());
		}
	}

	/**
	 * Save the datasets in the reporting directory.
	 * */
	protected void saveDatasets(){
		String name;
		Dataset dataset;

		for(int i=0; i<this.paretoFronts.size(); i++){
			try {
				dataset = this.paretoFronts.get(i);
				name = dataset.getName();
				dataset.setNumberOfDecimals(8);
				((CsvDataset)dataset).writeDataset(directory.getAbsolutePath()+"/"+name+"/pareto-fronts/"+name+"-summary.csv");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Check the Pareto dominance between two solutions.
	 * @param solution1 The first solution.
	 * @param solution2 The second solution.
	 * @return True if the first solution is dominated by the second solution.
	 * */
	private boolean dominatedBy(double [] solution1, double [] solution2){
		int result = 0;
		int size = solution1.length;
		// Compare objective values
		for (int i=0; i<size; i++) {
			int cmp = compare(solution1[i], solution2[i],i);
			if (result == 0) {
				result = cmp;
			}
			else {
				if (cmp != 0 & cmp != result) 
					return false; // non dominated
			}	
		}
		// Return result
		if(result == -1)
			return false;
		else
			return true;
	}

	/**
	 * Compare the objective value of two solutions.
	 * @param value1 The objective value for the first solution.
	 * @param value2 The objective value for the second solution.
	 * @param -1 if value1 is better than value2, 1 if value2 is 
	 * better than value2, false otherwise.
	 * */
	private int compare(double value1, double value2, int index){
		if(this.mop[index]) // maximize
			if(value1>value2)
				return -1;
			else if (value1==value2)
				return 0;
			else
				return 1;
		else
			if(value1>value2)
				return 1;
			else if (value1==value2)
				return 0;
			else
				return -1;
	}
}
