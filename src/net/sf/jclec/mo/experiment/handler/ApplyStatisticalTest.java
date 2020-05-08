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
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import es.uco.kdis.datapro.algorithm.wrappers.r.RCustomStrategy;
import es.uco.kdis.datapro.algorithm.wrappers.r.tests.RFriedman;
import es.uco.kdis.datapro.algorithm.wrappers.r.tests.RWilcoxon;
import es.uco.kdis.datapro.dataset.Dataset;
import es.uco.kdis.datapro.dataset.column.NumericalColumn;
import es.uco.kdis.datapro.dataset.source.CsvDataset;

/**
 * This handler applies the Wilcoxon test or the Friedman test for each quality indicator
 * located in corresponding directory. It requires a set of experiments, each one having
 * its own results. If the number of algorithms is equal to 2, the Wilcoxon test is executed,
 * otherwise, the Friedman test is carried out.
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

public class ApplyStatisticalTest extends MOExperimentHandler {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** The path to the all the experiment to compare */
	protected String [] experimentNames;

	/** The list of indicator results to be used */
	protected List<Dataset> indicatorResults;

	/** The number of indicators */
	protected int numberOfIndicators;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Parameterized constructor.
	 * @param directoryName The path to the general reporting directory.
	 * @param experimentNames The names of the experiments (subfolders in the reporting directory)
	 * @param numberOfIndicators Number of indicators to be analyzed.
	 * */
	public ApplyStatisticalTest(String directoryName, String [] experimentNames, int numberOfIndicators) {
		super(directoryName);
		this.experimentNames = experimentNames;
		this.numberOfIndicators = numberOfIndicators;
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
		computeTests();
		if(nextHandler()!=null)
			nextHandler().process();
	}

	/////////////////////////////////////////////////////////////////
	// -------------------------------------------- Protected methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Load the indicator results as datasets.
	 * */
	protected void readDatasets(){

		String format = new String("s");//the first column is nominal (algorithm name)
		for(int i=0; i<this.numberOfIndicators; i++){
			format+="f";
		}
		File algSummary;
		Dataset algData, indicatorData = null;
		boolean found;
		String indicatorName, algName;
		int nAlgorithms;
		int algIndex;
		double value;

		// check if the request can be processed
		if(this.directory.exists() && this.directory.isDirectory()){
			this.indicatorResults = new ArrayList<Dataset>();

			for(int i=0; i<this.experimentNames.length; i++){
				algSummary = new File(this.directory.getAbsolutePath() + "/" + this.experimentNames[i] 
						+ "/" + this.experimentNames[i] + "-indicators.csv");
				if(algSummary.exists()){

					algData = new CsvDataset(algSummary.getAbsolutePath());
					try{
						((CsvDataset)algData).readDataset("nv", format);
					}catch(Exception e){
						e.printStackTrace();
					}

					nAlgorithms = algData.getColumn(0).getSize();
					for(int j=1; j<this.numberOfIndicators+1; j++){ // omit the first column (algorithm name)

						// Search the dataset for that indicator
						indicatorName = algData.getColumn(j).getName();
						found = false;
						for(int k=0; !found && k<this.indicatorResults.size(); k++){
							if(this.indicatorResults.get(k).getName().equalsIgnoreCase(indicatorName)){
								found = true;
								indicatorData = this.indicatorResults.get(k);
							}
						}
						if(!found){
							indicatorData = new CsvDataset();
							indicatorData.setName(indicatorName);
							this.indicatorResults.add(indicatorData);
						}

						// Search the column of each algorithm
						algIndex = -1;
						for(int k=0; k<nAlgorithms; k++){
							algName = (String)algData.getColumn(0).getElement(k);
							algIndex = indicatorData.getIndexOfColumn(indicatorData.getColumnByName(algName));
							if(algIndex == -1){
								indicatorData.addColumn(new NumericalColumn(algName));
								algIndex = indicatorData.getIndexOfColumn(indicatorData.getColumnByName(algName));
							}
							value = (double)algData.getColumnByName(indicatorName).getElement(k);
							indicatorData.getColumn(algIndex).addValue(value);
						}
					}
				}
			}

			// save datasets
			String name;
			String dirName;
			try{
				dirName = this.directory.getAbsolutePath()+"/tests";
				File aux = new File(dirName);
				if(!aux.exists())
					aux.mkdirs();
				for(Dataset d: this.indicatorResults){
					name = dirName + "/" + d.getName() + ".csv";
					((CsvDataset)d).writeDataset(name);
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}

	/**
	 * Compute the statistical tests and store the results into files.
	 * */
	@SuppressWarnings("unchecked")
	protected void computeTests(){
		int nAlgorithms;
		int size = this.indicatorResults.size();
		RCustomStrategy algorithm = null;
		boolean close = false;
		Map<String,Object> result;
		Iterator<String> iterator;
		StringBuffer sb;
		String key, testName="", fileName;
		FileWriter writer;

		// For each quality indicator
		for(int i=0; i<size; i++){
			if(i==size-1)
				close = true;
			nAlgorithms = this.indicatorResults.get(i).getColumns().size();
			if(nAlgorithms == 2){ // Wilcoxon
				algorithm = new RWilcoxon(this.indicatorResults.get(i), null, close, 0, 1);
				((RWilcoxon)algorithm).setConfidenceInterval(true);
				testName = "Wilcoxon";
			}
			else if(nAlgorithms > 2){ // Friedman
				algorithm = new RFriedman(this.indicatorResults.get(i), null, close);
				testName = "Friedman";
			}
			else{ // a test cannot be applied
				System.err.println("The " + this.indicatorResults.get(i).getName() + " file contains less than two samples");
			}

			if(algorithm!=null && algorithm.isExecutable()){

				// Execute the algorithm
				algorithm.initialize();
				algorithm.execute();
				algorithm.postexec();
				result = (Map<String,Object>)algorithm.getResult();

				// Read the result
				sb = new StringBuffer();
				iterator = result.keySet().iterator();
				while(iterator.hasNext()){
					key = iterator.next();
					sb.append(key + ": " + result.get(key) + "\n");
				}

				// Save the result
				try {
					fileName = this.directory.getAbsolutePath() + "/tests/" 
							+ this.indicatorResults.get(i).getName() + "-" + testName + ".txt";
					writer = new FileWriter(fileName);
					writer.write(sb.toString());
					writer.flush();
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
