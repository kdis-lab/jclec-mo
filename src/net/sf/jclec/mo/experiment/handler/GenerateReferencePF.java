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

import es.uco.kdis.datapro.algorithm.preprocessing.instance.RemoveDuplicates;
import es.uco.kdis.datapro.dataset.Dataset;
import es.uco.kdis.datapro.dataset.column.NumericalColumn;
import es.uco.kdis.datapro.dataset.source.CsvDataset;
import es.uco.kdis.datapro.exception.IllegalFormatSpecificationException;
import es.uco.kdis.datapro.exception.NotAddedValueException;

/**
 * This handler constructs a reference Pareto front from a set of fronts returned
 * by different algorithms.
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

public class GenerateReferencePF extends MOExperimentHandler {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** The reference Pareto front */
	protected Dataset referenceParetoFront;

	/** The information of the type of objective functions */
	protected boolean [] mop;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Parameterized constructor.
	 * @param directoryName The path to the reporting directory.
	 * @param mop The type of objective functions (true=maximize, false=minimize).
	 * */
	public GenerateReferencePF(String directoryName, boolean [] mop) {
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
		createInitialFront();
		removeDuplicatedSolutions();
		removeDominatedSolutions();
		saveDataset();
		if(nextHandler()!=null)
			nextHandler().process();
	}

	/////////////////////////////////////////////////////////////////
	// -------------------------------------------- Protected methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Create the Pareto front containing all the solutions found.
	 * */
	protected void createInitialFront(){

		File [] algSubFolders;
		Dataset dataset = null;
		String name;

		int numberOfObjectives = this.mop.length;
		String sColumnFormat = new String();
		for(int i=0; i<numberOfObjectives; i++){
			sColumnFormat+="f";
		}

		this.referenceParetoFront = new CsvDataset();
		for(int j=0; j<numberOfObjectives; j++){
			this.referenceParetoFront.addColumn(new NumericalColumn());
		}

		if(this.directory.exists() && this.directory.isDirectory()){
			algSubFolders = this.directory.listFiles();
			for(int i=0; i<algSubFolders.length; i++){

				if(algSubFolders[i].exists() && algSubFolders[i].isDirectory() 
						&& !algSubFolders[i].getName().equalsIgnoreCase("indicators")
						&& !algSubFolders[i].getName().equalsIgnoreCase("plots")){
					// Read the PF of each algorithm
					name = algSubFolders[i].getAbsolutePath() + "/pareto-fronts/" + algSubFolders[i].getName()+"-summary.csv";
					dataset = new CsvDataset(name);
					try {
						((CsvDataset)dataset).readDataset("nv", sColumnFormat);
					} catch (IndexOutOfBoundsException | IOException
							| NotAddedValueException | IllegalFormatSpecificationException e) {
						e.printStackTrace();
					}

					// Add all values
					for(int j=0; j<numberOfObjectives; j++){
						this.referenceParetoFront.getColumn(j).addAllValues(dataset.getColumn(j).getValues());
					}
				}
			}
			// Set the objective names
			for(int j=0; j<numberOfObjectives; j++){
				this.referenceParetoFront.getColumn(j).setName(dataset.getColumn(j).getName());
			}
		}
	}

	/**
	 * Remove dominated solutions.
	 * */
	protected void removeDominatedSolutions(){

		Dataset copy;
		boolean isDominated;
		int numberOfObjs = this.mop.length;
		double solution1[], solution2[];
		int nSol = this.referenceParetoFront.getColumn(0).getSize();

		copy = this.referenceParetoFront.clone();

		for(int i=0; i<nSol; i++){
			isDominated = false;

			// get the first solution
			solution1 = new double[numberOfObjs];
			for(int n=0; n<numberOfObjs; n++){
				solution1[n] = (double)this.referenceParetoFront.getColumn(n).getElement(i);
			}

			for(int j=0; !isDominated && j<nSol; j++){
				// get the second solution
				if(i!=j){
					solution2 = new double[numberOfObjs];
					for(int n=0; n<numberOfObjs; n++){
						solution2[n] = (double)this.referenceParetoFront.getColumn(n).getElement(j);
					}
					if(dominatedBy(solution1,solution2)){
						isDominated = true;
					}
				}
			}
			if(!isDominated){
				for(int j=0; j<numberOfObjs; j++){
					copy.getColumn(j).addValue(this.referenceParetoFront.getColumn(j).getElement(i));
				}
			}
		}
		this.referenceParetoFront = copy;	
	}

	/**
	 * Remove duplicated solutions.
	 * */
	protected void removeDuplicatedSolutions() {
		RemoveDuplicates algorithm = new RemoveDuplicates(this.referenceParetoFront);
		algorithm.initialize();
		algorithm.execute();
		algorithm.postexec();
		this.referenceParetoFront = (Dataset) algorithm.getResult();	
	}

	/**
	 * Save the dataset.
	 * */
	protected void saveDataset(){
		String name = this.directory.getAbsolutePath()+"/"+this.directory.getName()+"-rpf.csv";
		try {
			this.referenceParetoFront.setNumberOfDecimals(8);
			((CsvDataset)this.referenceParetoFront).writeDataset(name);
		} catch (IOException e) {
			e.printStackTrace();
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
