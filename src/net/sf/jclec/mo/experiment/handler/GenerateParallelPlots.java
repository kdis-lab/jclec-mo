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

import es.uco.kdis.datapro.algorithm.wrappers.r.plots.RParallelCoordPlotStrategy;
import es.uco.kdis.datapro.dataset.Dataset;
import es.uco.kdis.datapro.dataset.source.CsvDataset;
import es.uco.kdis.datapro.exception.IllegalFormatSpecificationException;
import es.uco.kdis.datapro.exception.NotAddedValueException;

/**
 * This handler generates a parallel coordinates plots that represent Pareto fronts.
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

public class GenerateParallelPlots extends MOExperimentHandler {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** True if scaled fronts should be loaded, false otherwise */
	protected boolean scaled;

	/** The set of Pareto fronts */
	protected List<Dataset> paretoFronts;

	/** Number of objectives */
	protected int numberOfObjectives;

	/** The type of file to be generated. */
	protected String fileExtension;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Parameterized constructor.
	 * @param directoryName The path to the reporting directory.
	 * @param scaled True if scaled fronts should be retrieved, false otherwise.
	 * @param numberOfObjectives The number of objectives of the problem.
	 * @param fileType The type of file in which the plot will be saved. The following
	 * values are allowed:
	 * <ul>
	 * <li>1: png</li>
	 * <li>2: pdf</li>
	 * <li>3: jpg</li>
	 * <li>4: jpeg</li>
	 * <li>5: eps</li>
	 * <li>6: ps</li>
	 * <li>7: svg</li>
	 * </ul>
	 * */
	public GenerateParallelPlots(String directoryName, boolean scaled, int numberOfObjectives, int fileType) {
		super(directoryName);
		this.scaled = scaled;
		this.numberOfObjectives = numberOfObjectives;
		switch(fileType){	
		case 1: this.fileExtension = ".png"; break;
		case 2: this.fileExtension = ".pdf";break;
		case 3: this.fileExtension = ".jpg";break;
		case 4: this.fileExtension = ".jpeg";break;
		case 5: this.fileExtension = ".eps";break;
		case 6: this.fileExtension = ".ps";break;
		case 7: this.fileExtension = ".svg";break;
		default:  this.fileExtension = ".png";break;
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
		generatePlots();
		if(nextHandler() != null)
			nextHandler().process();
	}

	/////////////////////////////////////////////////////////////////
	// -------------------------------------------- Protected methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Load the datasets
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

		// Add the RPF
		if(this.scaled){
			name = this.directory.getAbsolutePath() + "/" + this.directory.getName()+"-rpf-scaled.csv";
		}
		else{
			name = this.directory.getAbsolutePath() + "/" + this.directory.getName()+"-rpf.csv";
		}
		dataset = new CsvDataset(name);
		try {
			((CsvDataset)dataset).readDataset("nv", format);
			// Add the dataset to the list
			dataset.setName("ReferenceParetoFront");
			this.paretoFronts.add(dataset);	
		} catch (IndexOutOfBoundsException | IOException
				| NotAddedValueException | IllegalFormatSpecificationException e) {
		}
	}

	/**
	 * Generate the plots.
	 * */
	protected void generatePlots(){

		String plotDirectoryName=null;
		plotDirectoryName = getExperimentReportDirectory()+"/plots";
		File plotDirectory = new File(plotDirectoryName);
		if(!plotDirectory.exists())
			plotDirectory.mkdirs();

		int size = this.paretoFronts.size();
		RParallelCoordPlotStrategy algorithm;
		String plotFileName;
		for(int i=0; i<size-1; i++){
			if(this.paretoFronts.get(i).getColumn(0).getSize()>1){
				try {
					plotFileName = plotDirectory.getCanonicalPath() +"/" + this.paretoFronts.get(i).getName() + this.fileExtension;
					if(plotFileName.contains("\\"))
						plotFileName = plotFileName.replace("\\", "/");
					algorithm = new RParallelCoordPlotStrategy(this.paretoFronts.get(i), false, plotFileName);
					algorithm.setTitle(this.paretoFronts.get(i).getName());
					algorithm.setMinMaxLabels(true);
					algorithm.initialize();
					algorithm.execute();
					algorithm.postexec();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else{
				System.err.println("The PF of " + this.paretoFronts.get(i).getName() + " only contains one solution. The parallel plot cannot be generated.");
			}
		}

		// Close the connection after generating the last plot
		if(this.paretoFronts.get(size-1).getColumn(0).getSize() > 1){
			try {
				plotFileName = plotDirectory.getCanonicalPath() +"/" + this.paretoFronts.get(size-1).getName() + this.fileExtension;
				if(plotFileName.contains("\\"))
					plotFileName = plotFileName.replace("\\", "/");
				algorithm = new RParallelCoordPlotStrategy(this.paretoFronts.get(size-1), true, plotFileName);
				algorithm.setTitle(this.paretoFronts.get(size-1).getName());
				algorithm.setMinMaxLabels(true);
				algorithm.initialize();
				algorithm.execute();
				algorithm.postexec();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else{
			System.err.println("The PF of " + this.paretoFronts.get(size-1).getName() + " only contains one solution. The parallel plot cannot be generated.");
			algorithm = new RParallelCoordPlotStrategy(this.paretoFronts.get(size-1), true, "");
			algorithm.postexec(); // close the connection
		}
	}
}
