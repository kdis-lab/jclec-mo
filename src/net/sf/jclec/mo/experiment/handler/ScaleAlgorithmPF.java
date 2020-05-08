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
import es.uco.kdis.datapro.dataset.column.NumericalColumn;
import es.uco.kdis.datapro.dataset.source.CsvDataset;
import es.uco.kdis.datapro.exception.IllegalFormatSpecificationException;
import es.uco.kdis.datapro.exception.NotAddedValueException;

/**
 * This handler scales the values of the Pareto fronts storing all the non-dominated
 * solutions found by one algorithm.
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

public class ScaleAlgorithmPF extends MOExperimentHandler {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** The characteristics of the multi-objective problem */
	protected boolean [] mop;

	/** The lower bounds of the objective functions */
	protected double [] minValues;

	/** The upper bounds of the objective functions */
	protected double [] maxValues;

	/** The list of scaled PFs */
	protected List<Dataset> scaledParetoFronts;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Parameterized constructor.
	 * @param directoryName The path to the reporting directory.
	 * @param mop The type of objective functions (true=maximization, false=minimization).
	 * @param minValues The lower bound for all the objective functions.
	 * @param maxValues The upper bound for all the objective functions.
	 * */
	public ScaleAlgorithmPF(String directoryName, boolean [] mop, double [] minValues, double [] maxValues) {
		super(directoryName);
		this.mop = mop;
		this.minValues = minValues;
		this.maxValues = maxValues;
	}

	/**
	 * Parameterized constructor.
	 * @param directoryName The path to the reporting directory.
	 * @param mop The type of objective functions (true=maximization, false=minimization).
	 * */
	public ScaleAlgorithmPF(String directoryName, boolean [] mop) {
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
		if(this.minValues==null || this.maxValues==null)
			calculateBounds();
		scaleSolutions();
		saveDatasets();
		if(nextHandler()!=null)
			nextHandler().process();
	}

	/////////////////////////////////////////////////////////////////
	// -------------------------------------------- Protected methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Calculate the bounds considering the current solutions.
	 * */
	protected void calculateBounds(){

		File [] algSubFolders;
		Dataset dataset = null;
		String name;
		NumericalColumn col;
		double minCol, maxCol;

		int numberOfObjectives = this.mop.length;
		String sColumnFormat = new String();
		for(int i=0; i<numberOfObjectives; i++){
			sColumnFormat+="f";
		}

		// Initialize
		this.minValues = new double[numberOfObjectives];
		this.maxValues = new double[numberOfObjectives];
		for(int i=0; i<numberOfObjectives; i++){
			this.minValues[i] = Double.POSITIVE_INFINITY;
			this.maxValues[i] = Double.NEGATIVE_INFINITY;
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

					// Update min and max values
					for(int j=0; j<numberOfObjectives; j++){
						col = (NumericalColumn)dataset.getColumn(j);
						minCol = col.getMinValue();
						maxCol = col.getMaxValue();

						if(minCol < this.minValues[j])
							this.minValues[j] = minCol;
						if(maxCol > this.maxValues[j])
							this.maxValues[j] = maxCol;
					}
				}
			}
		}

		for(int i=0; i<numberOfObjectives; i++){
			if(this.maxValues[i] == Double.NEGATIVE_INFINITY)
				this.maxValues[i]=this.minValues[i];
			if(this.minValues[i] == Double.POSITIVE_INFINITY)
				this.minValues[i]=this.maxValues[i];
			if(this.maxValues[i]==-0.0)
				this.maxValues[i]=0.0;
			if(this.minValues[i]==-0.0)
				this.minValues[i]=0.0;
		}
	}

	/**
	 * Scale the solutions.
	 * */
	protected void scaleSolutions(){

		File [] algSubFolders;
		Dataset dataset = null;
		String name;
		NumericalColumn col;
		double value;
		int numberOfSolutions;
		int numberOfObjectives = this.mop.length;
		String sColumnFormat = new String();
		for(int i=0; i<numberOfObjectives; i++){
			sColumnFormat+="f";
		}

		this.scaledParetoFronts = new ArrayList<Dataset>();

		if(this.directory.exists() && this.directory.isDirectory()){

			// Scale the PF of each algorithm
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

					// Scale objectives
					numberOfSolutions = dataset.getColumn(0).getSize();

					for(int j=0; j<numberOfObjectives; j++){
						col = (NumericalColumn)dataset.getColumn(j);

						for(int k=0; k<numberOfSolutions; k++){
							value = (double)col.getElement(k);
							value = (value - this.minValues[j])/(this.maxValues[j]-this.minValues[j]);
							col.setValue(value, k);
						}
					}

					// Save the dataset
					dataset.setName(algSubFolders[i].getName());
					this.scaledParetoFronts.add(dataset);
				}
			}

			// Scale the RPF
			name = this.directory.getAbsolutePath()+"/"+this.directory.getName()+"-rpf.csv";
			dataset = new CsvDataset(name);
			try {
				((CsvDataset)dataset).readDataset("nv", sColumnFormat);
			} catch (IndexOutOfBoundsException | IOException
					| NotAddedValueException | IllegalFormatSpecificationException e) {
				e.printStackTrace();
			}

			numberOfSolutions = dataset.getColumn(0).getSize();
			for(int j=0; j<numberOfObjectives; j++){
				col = (NumericalColumn)dataset.getColumn(j);
				for(int k=0; k<numberOfSolutions; k++){
					value = (double)col.getElement(k);
					value = (value - this.minValues[j])/(this.maxValues[j]-this.minValues[j]);
					col.setValue(value, k);
				}
			}
			dataset.setName(this.directory.getName()+"-rpf");
			this.scaledParetoFronts.add(dataset);
		}
	}

	/**
	 * Save the datasets.
	 * */
	protected void saveDatasets(){

		// Save the scaled PF of each algorithm
		int size = this.scaledParetoFronts.size()-1;
		String name;
		for(int i=0; i<size; i++){
			name = this.directory.getAbsolutePath() + "/" + this.scaledParetoFronts.get(i).getName() + "/pareto-fronts/"
					+ this.scaledParetoFronts.get(i).getName() +"-summary-scaled.csv";
			try {
				this.scaledParetoFronts.get(i).setNumberOfDecimals(8);
				((CsvDataset)this.scaledParetoFronts.get(i)).writeDataset(name);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// Save the scaled RPF
		name = this.directory.getAbsolutePath()+"/"+this.scaledParetoFronts.get(size).getName() + "-scaled.csv";
		try {
			((CsvDataset)this.scaledParetoFronts.get(size)).writeDataset(name);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
