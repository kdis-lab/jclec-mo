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

import es.uco.kdis.datapro.dataset.Dataset;
import es.uco.kdis.datapro.dataset.column.NominalColumn;
import es.uco.kdis.datapro.dataset.column.NumericalColumn;
import es.uco.kdis.datapro.dataset.source.CsvDataset;
import es.uco.kdis.datapro.exception.IllegalFormatSpecificationException;
import es.uco.kdis.datapro.exception.NotAddedValueException;
import net.sf.jclec.mo.indicator.BinaryIndicator;
import net.sf.jclec.mo.indicator.Indicator;

/**
 * This handler computes quality indicators for a set of algorithms
 * whose results are located in a reporting directory.
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

public class ComputeIndicators extends MOExperimentHandler {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** The list of quality indicators */
	protected List<Indicator> indicators;

	/** The reference PF for binary indicators */
	protected String referencePFName;

	/** True if scaled fronts should be loaded, false otherwise */
	protected boolean scaled;

	/** The list of Pareto fronts (one per each algorithm) */
	protected List<Dataset> paretoFronts; // each list will contain all the executions of the same algorithm

	/** The reference Pareto front */
	protected Dataset referencePF;

	/** The number of objectives of the multi-objective problem */
	protected int numberOfObjectives;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Parameterized mutator.
	 * @param directoryName The path to the reporting directory.
	 * @param indicators The list of indicators to be computed.
	 * @param numberOfObjectives The number of objectives of the problem.
	 * @param referencePF The path to the reference Pareto front.
	 * @param scaled True if scaled fronts should be retrieved, false otherwise.
	 * */
	public ComputeIndicators(String directoryName, List<Indicator> indicators, int numberOfObjectives,
			String referencePF, boolean scaled) {
		super(directoryName);
		this.indicators = indicators;
		this.scaled = scaled;
		this.numberOfObjectives = numberOfObjectives;
		if(referencePF!=null){
			this.referencePFName = referencePF;
		}
		else{
			if(this.scaled){
				this.referencePFName = this.directory.getAbsolutePath() + "/" + this.directory.getName()+"-rpf-scaled.csv";
			}
			else{
				this.referencePFName = this.directory.getAbsolutePath() + "/" + this.directory.getName()+"-rpf.csv";
			}
		}
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void process() {
		readDatasets();
		readReferenceParetoFront();
		computeIndicators();
		if(nextHandler()!=null)
			nextHandler().process();
	}

	/**
	 * Load the datasets.
	 * */
	protected void readDatasets(){
		File [] algSubFolders;
		Dataset dataset = null;
		File subDirectory;
		String name;
		String format = new String();
		for(int i=0; i<this.numberOfObjectives; i++){
			format+="f";
		}

		if(this.directory.exists() && this.directory.isDirectory()){

			this.paretoFronts = new ArrayList<Dataset>();
			algSubFolders = this.directory.listFiles();

			// For each report, create a dataset to save all the PFs found by the same algorithm
			for(int i=0; i<algSubFolders.length; i++){
				subDirectory = new File(algSubFolders[i].getAbsolutePath() + "/pareto-fronts/");
				if(subDirectory.exists()){ // a directory that contains the results of an algorithm

					if(this.scaled){
						name = subDirectory.getAbsolutePath() + "/" + algSubFolders[i].getName()+"-summary-scaled.csv";
					}
					else{
						name = subDirectory.getAbsolutePath() + "/" + algSubFolders[i].getName()+"-summary.csv";
					}
					dataset = new CsvDataset(name);
					try {
						((CsvDataset)dataset).readDataset("nv", format);
						// Add the dataset to the list
						dataset.setName(algSubFolders[i].getName());
						this.paretoFronts.add(dataset);	
					} catch (IndexOutOfBoundsException | IOException
							| NotAddedValueException | IllegalFormatSpecificationException e) {
						e.printStackTrace();
					}				
				}
			}
		}
	}

	/**
	 * Load the reference Pareto front.
	 * */
	protected void readReferenceParetoFront(){
		// Add the RPF

		this.referencePF = new CsvDataset(this.referencePFName);
		String format = new String();
		for(int i=0; i<numberOfObjectives; i++){
			format+="f";
		}
		try {
			((CsvDataset)this.referencePF).readDataset("nv", format);
			this.referencePF.setName("ReferenceParetoFront");
		} catch (IndexOutOfBoundsException | IOException
				| NotAddedValueException | IllegalFormatSpecificationException e) {
			// a RPF/true front will not be used
		}
	}

	/**
	 * Compute the quality indicators.
	 * */
	protected void computeIndicators(){
		Indicator indicator;
		int size;
		double front[][], trueFront[][] = null;
		Dataset resultDataset;
		int nAlgorithms = this.paretoFronts.size();
		double result;
		NominalColumn column;
		String name;

		if(this.indicators!=null){

			// Create the datasets for saving results
			resultDataset = new CsvDataset();
			column =  new NominalColumn("Algorithm");
			for(int i=0; i<paretoFronts.size(); i++){
				column.addValue(paretoFronts.get(i).getName());
			}
			resultDataset.addColumn(column);
			for(int i=0;i<this.indicators.size();i++){
				resultDataset.addColumn(new NumericalColumn(this.indicators.get(i).getClass().getSimpleName()));
			}

			// If a second PF has to be used, extract its solutions.
			if(this.referencePF != null){
				trueFront = extractPF(this.referencePF);
			}

			// Execute each indicator
			size = indicators.size();
			for(int i=0; i<size; i++){
				indicator = this.indicators.get(i);

				if(indicator instanceof BinaryIndicator){
					if(trueFront != null)
						((BinaryIndicator) indicator).setSecondFront(trueFront);
					else
						System.err.println(indicator.getClass().getSimpleName() + " requires a second PF.");
				}

				for(int j=0; j<nAlgorithms; j++){
					front = extractPF(this.paretoFronts.get(j));
					indicator.setFront(front);
					indicator.calculate();
					result = indicator.getResult();
					resultDataset.getColumnByName(indicator.getClass().getSimpleName()).addValue(result);
				}	

			}

			// Save the dataset
			name = this.directory.getAbsolutePath() + "/" + directory.getName() + "-indicators.csv";
			try {
				resultDataset.setNumberOfDecimals(8);
				((CsvDataset)resultDataset).writeDataset(name);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Convert a dataset into a matrix.
	 * @param dataset The dataset representing a Pareto front.
	 * @return A numeric matrix containing the Pareto front.
	 * */
	private double [][] extractPF(Dataset dataset){
		int size = dataset.getColumn(0).getSize();
		double front [][] = new double[size][this.numberOfObjectives];
		NumericalColumn column;
		for(int j=0; j<this.numberOfObjectives; j++){
			column = (NumericalColumn)dataset.getColumn(j);
			for(int i=0; i<size; i++){
				front[i][j] = (double)column.getElement(i);
			}
		}
		return front;
	}
}
