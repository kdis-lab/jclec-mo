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
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.configuration.Configuration;

import net.sf.jclec.AlgorithmEvent;
import net.sf.jclec.IIndividual;
import net.sf.jclec.mo.IMOAlgorithm;

/**
 * Population reporter for MO algorithms. The current population and the archive
 * are reported at the given frequency.
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
 * @see MOReporter
 * */

public class MOPopulationReporter extends MOReporter {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -7695660462030543095L;

	/** Directory to store complete populations */
	protected File reportDirPopulations;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////	

	/**
	 * Empty constructor.
	 * */
	public MOPopulationReporter() {
		super();
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Get report directory for populations.
	 * @return Report directory.
	 * */
	public File getPopulationsDirectory() {
		return this.reportDirPopulations;
	}

	/**
	 * Set report directory for populations.
	 * @param reportDirectory New report directory.
	 * */
	protected void setPopulationsDirectory(File reportDirectory) {
		this.reportDirPopulations = reportDirectory;
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void configure(Configuration settings) {
		// Call super method
		super.configure(settings);
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void algorithmStarted(AlgorithmEvent event) {

		// Call super implementation
		super.algorithmStarted(event);

		// Create a directory for storing the populations 
		if(isReportOnFile()){
			File dir = new File(super.getReportDirectory().getAbsolutePath()+"/populations/");
			setPopulationsDirectory(dir);
			if (!dir.exists()) {
				dir.mkdirs();
			}
		}
		
		// Do an iteration report
		doIterationReport((IMOAlgorithm) event.getAlgorithm(),false);
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * */
	@Override
	protected void doReport(IMOAlgorithm algorithm, int generation, boolean finalReport) {

		// Prepare a new report
		String algName = algorithm.getStrategy().getClass().getSimpleName().toLowerCase();
		String filename;
		if(finalReport)
			filename  = algName+"-"+getNumberOfExecution()+"-final.txt";
		else
			filename = algName+"-"+getNumberOfExecution()+"-gener"+generation+".txt";
		File file = new File(getPopulationsDirectory(), filename);
		FileWriter filewriter;
		StringBuffer sb = new StringBuffer();

		// Get populations
		List<IIndividual> inhabitants = algorithm.getContext().getInhabitants();
		List<IIndividual> archive = algorithm.getArchive();

		sb.append("\tCurrent Population\n\n");
		for (IIndividual ind : inhabitants) {
			sb.append("\t\t"+ind+"\n");
		}
		if(archive!=null){
			sb.append("\n\n\tArchive\n\n");
			for(IIndividual ind: archive){
				sb.append("\t\t"+ind+"\n");
			}
		}

		// Write report string to the standard output (if necessary) 
		if (isReportOnConsole()) {
			System.out.println(sb.toString());
		}

		// Write string to the report file (if necessary) 
		if (isReportOnFile()) {
			try {
				filewriter = new FileWriter(file);
				filewriter.write("\n"+sb.toString());
				filewriter.flush();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	protected String getName(){
		return "-Populations";
	}
}