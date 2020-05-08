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

import es.uco.kdis.datapro.algorithm.wrappers.r.plots.RBoxPlotStrategy;
import es.uco.kdis.datapro.dataset.Dataset;
import es.uco.kdis.datapro.dataset.source.CsvDataset;
import es.uco.kdis.datapro.exception.IllegalFormatSpecificationException;
import es.uco.kdis.datapro.exception.NotAddedValueException;

/**
 * This handler generates a boxplot with the distribution of quality indicators.
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
public class GenerateIndicatorBoxPlots extends MOExperimentHandler {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** The list of datasets containing the indicator results */
	protected List<Dataset> indicatorDatasets;

	/** The number of algorithms to be added as samples of the plot */
	protected int numberOfAlgorithms;

	/** The type of file to be generated. */
	protected String fileExtension;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Parameterized constructor.
	 * @param directoryName The path to the reporting directory.
	 * @param numberOfAlgorithms The number of algorithm to be included in the plot.
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
	public GenerateIndicatorBoxPlots(String directoryName, int numberOfAlgorithms, int fileType) {
		super(directoryName);
		this.numberOfAlgorithms = numberOfAlgorithms;
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
	 * Load the datasets.
	 * */
	protected void readDatasets() {
		File [] indicatorFiles;
		Dataset dataset = null;
		String indicatorName;
		File indicatorSubFolder;
		String format = new String();
		for(int i=0; i<this.numberOfAlgorithms; i++){
			format+="f";
		}

		if(this.directory.exists() && this.directory.isDirectory()){

			this.indicatorDatasets = new ArrayList<Dataset>();
			indicatorSubFolder = new File(this.directory.getAbsoluteFile()+"/indicators/");
			indicatorFiles = indicatorSubFolder.listFiles();

			// For each file, create a dataset to save all the values of the corresponding indicator
			for(int i=0; i<indicatorFiles.length; i++){
				if(indicatorFiles[i].getName().contains("-final")){
					dataset = new CsvDataset(indicatorFiles[i].getAbsolutePath());
					try {
						((CsvDataset)dataset).readDataset("nv",format);
					} catch (IndexOutOfBoundsException | IOException
							| NotAddedValueException | IllegalFormatSpecificationException e) {
						e.printStackTrace();
					}
					indicatorName = indicatorFiles[i].getName();
					indicatorName = indicatorName.substring(0,indicatorName.indexOf("-final"));
					dataset.setName(indicatorName);
					this.indicatorDatasets.add(dataset);
				}
			}
		}
	}

	/**
	 * Generate and save the plots.
	 * */
	protected void generatePlots() {

		String plotDirectoryName=null;
		plotDirectoryName = getExperimentReportDirectory()+"/plots";
		File plotDirectory = new File(plotDirectoryName);
		if(!plotDirectory.exists())
			plotDirectory.mkdirs();

		int size = this.indicatorDatasets.size();
		RBoxPlotStrategy algorithm;
		String plotFileName;
		for(int i=0; i<size-1; i++){
			try {
				plotFileName = plotDirectory.getCanonicalPath() +"/" + this.indicatorDatasets.get(i).getName() + this.fileExtension;
				if(plotFileName.contains("\\"))
					plotFileName = plotFileName.replace("\\", "/");
				algorithm = new RBoxPlotStrategy(this.indicatorDatasets.get(i), false, plotFileName);
				algorithm.setTitle(this.indicatorDatasets.get(i).getName());
				algorithm.initialize();
				algorithm.execute();
				algorithm.postexec();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// Close the connection after generating the last plot
		try {
			plotFileName = plotDirectory.getCanonicalPath() +"/" + this.indicatorDatasets.get(size-1).getName() + this.fileExtension;
			if(plotFileName.contains("\\"))
				plotFileName = plotFileName.replace("\\", "/");
			algorithm = new RBoxPlotStrategy(this.indicatorDatasets.get(size-1), true, plotFileName);
			algorithm.setTitle(this.indicatorDatasets.get(size-1).getName());
			algorithm.initialize();
			algorithm.execute();
			algorithm.postexec();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
