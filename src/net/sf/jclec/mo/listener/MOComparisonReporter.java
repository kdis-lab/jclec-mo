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

package net.sf.jclec.mo.listener;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.Configuration;

import es.uco.kdis.datapro.dataset.Dataset;
import es.uco.kdis.datapro.dataset.column.ColumnAbstraction;
import es.uco.kdis.datapro.dataset.column.NumericalColumn;
import es.uco.kdis.datapro.dataset.source.CsvDataset;
import es.uco.kdis.datapro.datatypes.MissingValue;
import es.uco.kdis.datapro.exception.IllegalFormatSpecificationException;
import es.uco.kdis.datapro.exception.NotAddedValueException;
import net.sf.jclec.AlgorithmEvent;
import net.sf.jclec.IIndividual;
import net.sf.jclec.mo.IMOAlgorithm;
import net.sf.jclec.mo.command.ObjectiveScaler;
import net.sf.jclec.mo.indicator.Indicator;

/**
 * Reporter for comparison purposes. This reporter stores the results of one or
 * more algorithms in the an Excel file, one per each indicator configured.
 * The list of indicators to be measured can be configured by the user.
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
 * @see MOIndicatorReporter
 * @see Indicator
 * */

public class MOComparisonReporter extends MOIndicatorReporter{

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 3348528917458369296L;

	/** Number of algorithms to be compared */
	protected int numberOfAlgorithms;

	/** Number of executions to be performed*/
	protected int numberOfExecutions;

	/** The list of dataset, each one storing the results for one indicator */
	protected List<Dataset> datasets;

	/** Directory for saving the files */
	protected File reportDirIndicators;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public MOComparisonReporter() {
		super();
	}

	/////////////////////////////////////////////////////////////////
	//----------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Get the number of algorithms.
	 * @return The number of algorithms being compared.
	 * */
	public int getNumberOfAlgorithms(){
		return this.numberOfAlgorithms;
	}

	/**
	 * Set the number of algorithms.
	 * @param numberOfAlgorithms The value that has to be set.
	 * */
	public void setNumberOfAlgorithms(int numberOfAlgorithms){
		this.numberOfAlgorithms = numberOfAlgorithms;
	}

	/**
	 * Get the number of executions.
	 * @return The number of executions to be performed.
	 * */
	public int getNumberOfExecutions(){
		return this.numberOfExecution;
	}

	/**
	 * Set the number of executions.
	 * @param numberOfExecutions The value that has to be set.
	 * */
	public void setNumberOfExecutions(int numberOfExecutions){
		this.numberOfExecutions = numberOfExecutions;
	}

	/**
	 * Get report directory for storing the files.
	 * @return report directory
	 * */
	public File getComparisonDirectory() {
		return this.reportDirIndicators;
	}

	/**
	 * Set report directory for storing the files.
	 * @param reportDirectory New report directory.
	 * */
	protected void setComparisonDirectory(File reportDirectory) {
		this.reportDirIndicators = reportDirectory;
	}

	/////////////////////////////////////////////////////////////////
	//---------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * */
	@Override
	protected String getName(){
		return "-comp";
	}

	/**
	 * {@inheritDoc}
	 * <p>Specific parameters for this reporter are:
	 * <ul>
	 * 	<li>number-of-algorithms (<code>int</code>): <p>Number of algorithms to be compared.</p></li>
	 *  <li>number-of-executions (<code>int</code>): <p>Number of executions to be performed for each algorithm.</p></li>
	 * </ul>
	 * </p>
	 * <p><code>report-on-console</code> and <code>report-on-file</code> (inherited from <code>MOReporter</code>) are not considered.</p>
	 * */
	@Override
	public void configure(Configuration settings) {
		// Call super method
		super.configure(settings);

		// Add the number of algorithms
		int n = settings.getInt("number-of-algorithms");
		setNumberOfAlgorithms(n);

		// Add the number of executions
		n = settings.getInt("number-of-executions");
		setNumberOfExecutions(n);

		// Set appropriate flags
		setReportOnConsole(false);
		setReportOnFile(true);
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void algorithmStarted(AlgorithmEvent event) {

		// Call super method
		super.algorithmStarted(event);

		// Create a directory for storing pareto front files
		File dir = new File("reports/" + this.reportTitle +"/indicators/");
		setComparisonDirectory(dir);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		// Do report
		loadDatasets(0,false);
		doReport((IMOAlgorithm)event.getAlgorithm(), 0, false);
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	protected void doIterationReport(IMOAlgorithm algorithm, boolean finalReport){
		// Current generation
		int generation = algorithm.getContext().getGeneration();

		// Generic report information
		String reportInfo = "Generation " + generation + " Report\n";
		String name;

		// Write report string to the standard output (if necessary) 
		if (isReportOnConsole()) {
			name = this.getClass().getSimpleName();
			System.out.println(name + " -- " + reportInfo);
		}

		// Write string to the report file (if necessary) 
		if (isReportOnFile()) {
			try {
				getReportFileWriter().write("\n"+reportInfo);
				getReportFileWriter().flush();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}

		// The specific report should be performed after creating the datasets (performed by algorithStarted)
		if(this.datasets != null && generation>0){
			loadDatasets(generation,finalReport);
			doReport(algorithm, generation,finalReport);
		}
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	protected void doReport(IMOAlgorithm algorithm, int generation, boolean finalReport) {

		int nIndicators = this.indicators.size();
		int popSize;
		Indicator indicator;
		List<IIndividual> invertedParetoSet = null;
		List<IIndividual> scaledParetoSet = null;
		List<IIndividual> invScaledParetoSet = null;
		Dataset dataset = null;
		boolean found;
		ColumnAbstraction column = null;
		String name;

		// Extract the pareto set according to the population sets used by the algorithm
		// and the specific configuration of this listener
		List<IIndividual> paretoSet = algorithm.getArchive();
		if(paretoSet == null){
			this.commandExtract.setPopulation(algorithm.getContext().getInhabitants());
			this.commandExtract.execute();
			paretoSet = this.commandExtract.getNonDominatedSolutions();
		}
		else if(isFilterFromArchive()){
			this.commandExtract.setPopulation(algorithm.getArchive());
			this.commandExtract.execute();
			paretoSet = this.commandExtract.getNonDominatedSolutions();
		}
		popSize = paretoSet.size();

		for(int i=0; i<nIndicators; i++){

			// Get the indicator
			indicator = this.indicators.get(i);
			switch(type[i]){
			/* No transformation is required */
			case 1: 
				// Set the original Pareto set
				indicator.setFront(indicator.extractFromList(paretoSet));
				break;

				/* Inverted objective values */
			case 2: 
				if(invertedParetoSet == null){
					// Create a copy of the Pareto set
					invertedParetoSet = new ArrayList<IIndividual>();
					for(int j=0; j<popSize; j++){
						invertedParetoSet.add(paretoSet.get(j).copy());
					}

					// Invert the values 
					this.commandInvert.setPopulation(invertedParetoSet);
					this.commandInvert.execute();
				}
				// Set the inverted Pareto set
				indicator.setFront(indicator.extractFromList(invertedParetoSet));
				break;

				/* Scale objective values */
			case 3: 
				// Check if the operation was performed before
				if(scaledParetoSet == null){

					// Create a copy of the Pareto set
					scaledParetoSet = new ArrayList<IIndividual>();
					for(int j=0; j<popSize; j++){
						scaledParetoSet.add(paretoSet.get(j).copy());
					}

					// If the scaler was set, execute the command
					if(this.commandScale != null){
						// Set the appropriate bounds, if required
						if(this.minValues!=null && this.maxValues!=null){
							this.commandScale.setMinValues(this.minValues);
							this.commandScale.setMaxValues(this.maxValues);
						}

						// Scale the values
						this.commandScale.setPopulation(scaledParetoSet);
						this.commandScale.execute();
					}
				}
				// Set the scaled Pareto set
				indicator.setFront(indicator.extractFromList(scaledParetoSet));
				break;

				/* Invert and scale objective values */
			case 4: 
				// Check if the operation was performed before
				if(invScaledParetoSet == null){

					// Create a copy of the Pareto set
					invScaledParetoSet = new ArrayList<IIndividual>();
					for(int j=0; j<popSize; j++){
						invScaledParetoSet.add(paretoSet.get(j).copy());
					}

					// Invert and scale the values
					this.commandInvert.setPopulation(invScaledParetoSet);
					this.commandInvert.execute();

					if(this.commandScale == null){
						this.commandScale = new ObjectiveScaler();
					}
					
					// Set the appropriate bounds, if required
					if(this.invertedMinValues!=null && this.invertedMaxValues!=null){
						this.commandScale.setMinValues(this.invertedMinValues);
						this.commandScale.setMaxValues(this.invertedMaxValues);
					}
					
					this.commandScale.setPopulation(invScaledParetoSet);
					this.commandScale.execute();
				}
				// Set the inverted and scaled Pareto set
				indicator.setFront(indicator.extractFromList(invScaledParetoSet));
				break;
			}

			// Compute the indicator
			indicator.calculate();

			// Search the dataset that stores the values of this indicator
			name = indicator.getClass().getSimpleName();
			found = false;
			for(int j=0; !found && j<nIndicators; j++){
				dataset = this.datasets.get(j);
				if(dataset.getName().equalsIgnoreCase(name)){
					found = true;
				}
			}

			// If the dataset exists, search the column that stores the results of the current algorithm
			if(found){
				found = false;
				name = algorithm.getStrategy().getClass().getSimpleName();
				column = dataset.getColumnByName(name);

				if(column != null){

					// Get the position element to be stored (first non-null value)
					int index=0;
					while(!found && index<this.numberOfExecutions){
						if(column.getElement(index) instanceof MissingValue)
							found=true;
						else
							index++;
					}
					column.setValue(indicator.getResult(), index);
				}

				// The algorithm has not been included yet, set the result in the first column without name
				else{
					for(int j=0; !found && j<this.numberOfAlgorithms; j++){
						column = dataset.getColumn(j);
						if(column.getName().equalsIgnoreCase("alg")){ //a column without an assigned algorithm
							column.setName(name);
							column.setValue(indicator.getResult(),0);
							found=true;
						}
					}
				}
			}
		}

		// Write all the datasets
		for(int i=0; i<nIndicators; i++){
			dataset = this.datasets.get(i);
			dataset.setNumberOfDecimals(8);
			if(finalReport)
				name = this.reportDirIndicators.getAbsolutePath() + "/" + dataset.getName() + "-final.csv";
			else
				name = this.reportDirIndicators.getAbsolutePath() + "/" + dataset.getName() + "-gener" + generation + ".csv";
			try {
				dataset.setNumberOfDecimals(8);
				((CsvDataset)dataset).writeDataset(name);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/////////////////////////////////////////////////////////////////
	// -------------------------------------------- Protected methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Load the datasets from previous executions. If the files
	 * do no exist, empty datasets will be created.
	 * @param generation Current generation.
	 * @param finalReport To indicate if the current execution is the last one.
	 * */
	protected void loadDatasets(int generation, boolean finalReport){
		// The directory contains some files, load each file into a dataset
		File [] files = reportDirIndicators.listFiles();
		int nIndicators = this.indicators.size();
		String name;
		Dataset dataset;
		String format = "";
		ColumnAbstraction column;
		String currentFile;
		if(finalReport)
			currentFile = "-final";
		else
			currentFile = "-gener"+generation;

		boolean found = false;

		// Check if the directory already contains files for the current generation
		for(int i=0; !found && i<files.length; i++){
			if(files[i].getName().contains(currentFile))
				found=true;
		}
		if(found){

			// Each column will be assigned to one algorithm, and values are numerical
			for(int i=0; i<this.numberOfAlgorithms; i++)
				format += "f";

			// Read the datasets that correspond to the current generation	
			this.datasets = new ArrayList<Dataset>();
			for(int i=0; i<files.length; i++){
				name = files[i].getName();
				if(name.contains(currentFile)){
					name = name.substring(0, name.indexOf(currentFile)); // remove extension
					dataset = new CsvDataset(files[i].getAbsolutePath());
					dataset.setNullValue("?");
					dataset.setNumberOfDecimals(8);
					try {
						((CsvDataset)dataset).readDataset("nv", format);
					} catch (IndexOutOfBoundsException | IOException
							| NotAddedValueException | IllegalFormatSpecificationException e) {
						e.printStackTrace();
					}
					dataset.setName(name);
					this.datasets.add(dataset);
				}
			}
		}

		// The files do not exist, create empty datasets
		else if(this.numberOfAlgorithms >0 && nIndicators >0){
			this.datasets = new ArrayList<Dataset>(nIndicators);
			for(int i=0; i<nIndicators; i++){
				dataset = new CsvDataset();
				dataset.setName(this.indicators.get(i).getClass().getSimpleName());
				for(int j=0; j<numberOfAlgorithms; j++){
					column = new NumericalColumn("alg"); // not valid name yet
					for(int k=0; k<numberOfExecutions; k++){
						column.addValue(MissingValue.getMissingValue());
					}

					dataset.addColumn(column);
				}
				this.datasets.add(dataset);
			}
		}
	}
}
