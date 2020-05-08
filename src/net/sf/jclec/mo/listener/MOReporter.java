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
import java.util.Date;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.builder.EqualsBuilder;

import net.sf.jclec.AlgorithmEvent;
import net.sf.jclec.IAlgorithmListener;
import net.sf.jclec.IConfigure;
import net.sf.jclec.mo.IMOEvaluator;
import net.sf.jclec.mo.IMOAlgorithm;
import net.sf.jclec.mo.evaluation.Objective;

/**
 * Generic reporter for MO algorithms. It can generate
 * reports on console and on a file at the given frequency.
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
 * @see IAlgorithmListener
 * @see IConfigure
 * */

public abstract class MOReporter implements IAlgorithmListener, IConfigure {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -7695660462030543095L;

	/** Name of the report*/
	protected String reportTitle;

	/** Report frequency */
	protected int reportFrequency;

	/** Show report on console? */
	protected boolean reportOnConsole; 

	/** Write report on file? */
	protected boolean reportOnFile; 

	/** Report file */
	protected File reportFile;

	/** Report directory */
	protected File reportDirectory;

	/** Report file writer */
	protected FileWriter reportFileWriter;

	/** Number of executions with the same report title */
	protected int numberOfExecution;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////	

	/**
	 * Empty constructor.
	 * */
	public MOReporter() {
		super();
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////	

	/**
	 * Get the report title.
	 * @return Report title.
	 * */
	public String getReportTitle() {
		return this.reportTitle;
	}

	/**
	 * Set the report title.
	 * @param reportTitle New report title.
	 * */
	protected void setReportTitle(String reportTitle) {
		this.reportTitle = reportTitle;
	}

	/**
	 * Get the report frequency.
	 * @return Report frequency.
	 * */
	public int getReportFrequency() {
		return this.reportFrequency;
	}

	/**
	 * Set the report frequency.
	 * @param reportFrequency New report frequency
	 * */
	protected void setReportFrequency(int reportFrequency) {
		this.reportFrequency = reportFrequency;
	}

	/**
	 * Get the report on console flag.
	 * @return True if report is shown in console, false otherwise
	 * */
	public boolean isReportOnConsole() {
		return this.reportOnConsole;
	}

	/**
	 * Set the report on console flag.
	 * @param reportOnCconsole New flag value
	 * */
	protected void setReportOnConsole(boolean reportOnCconsole) {
		this.reportOnConsole = reportOnCconsole;
	}

	/**
	 * Get the report on file flag.
	 * @return True if report is saved in file, false otherwise
	 * */
	public boolean isReportOnFile() {
		return this.reportOnFile;
	}

	/**
	 * Set the report on file flag.
	 * @param reportOnFile New flag value
	 * */
	protected void setReportOnFile(boolean reportOnFile) {
		this.reportOnFile = reportOnFile;
	}

	/**
	 * Get the report file.
	 * @return Report file
	 * */
	public File getReportFile() {
		return this.reportFile;
	}

	/**
	 * Set the report file.
	 * @param reportFile New report file
	 * */
	protected void setReportFile(File reportFile) {
		this.reportFile = reportFile;
	}

	/**
	 * Get the report directory.
	 * @return Report directory
	 * */
	public File getReportDirectory() {
		return this.reportDirectory;
	}

	/**
	 * Set the report directory.
	 * @param reportDirectory New report directory
	 * */
	protected void setReportDirectory(File reportDirectory) {
		this.reportDirectory = reportDirectory;
	}

	/**
	 * Get the report writer.
	 * @return Report writer
	 * */
	public FileWriter getReportFileWriter() {
		return this.reportFileWriter;
	}

	/**
	 * Set the report writer.
	 * @param reportFileWriter New report file writer
	 * */
	protected void setReportFileWriter(FileWriter reportFileWriter) {
		this.reportFileWriter = reportFileWriter;
	}

	/**
	 * Get the number of executions
	 * @return Number of executions
	 * */
	public int getNumberOfExecution() {
		return this.numberOfExecution;
	}

	/**
	 * Set the number of executions
	 * @param execution The number of executions
	 * */
	protected void setNumberOfExecution(int execution) {
		this.numberOfExecution = execution;
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Configurable parameters are:
	 * <ul>
	 * 	<li>report-title (<code>String</code>): <p>Report title (file name). "untitled" by default.</p></li>
	 * 	<li>report-frequency (<code>int</code>): <p>Report frequency. 10 by default.</p></li>
	 * 	<li>report-on-console (<code>boolean</code>): <p>Show report on console. True by default.</p></li>
	 * 	<li>report-on-file (<code>boolean</code>): <p>Save report on file. False by default.</p></li>
	 * </ul>
	 * */
	@Override
	public void configure(Configuration settings) {
		// Set report title (default "untitled")
		String reportTitle = settings.getString("report-title", "untitled");
		setReportTitle(reportTitle);
		
		// Set report frequency (default 10 generations)
		int reportFrequency = settings.getInt("report-frequency", 10); 
		setReportFrequency(reportFrequency);

		// Set console report (default on)
		boolean reportOnConsole = settings.getBoolean("report-on-console", true);
		setReportOnConsole(reportOnConsole);

		// Set file report (default off)
		boolean reportOnFile = settings.getBoolean("report-on-file", false);
		setReportOnFile(reportOnFile);
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void algorithmStarted(AlgorithmEvent event) {

		File [] files;
		File file;
		StringBuffer sb;
		IMOAlgorithm alg = (IMOAlgorithm) event.getAlgorithm();
		String algName = alg.getStrategy().getClass().getSimpleName().toLowerCase();
		
		// If report is stored in a text file, create report file
		if (this.reportOnFile) {

			// Create the directory
			this.reportDirectory = new File("reports/" + this.reportTitle + "/" + algName);
			if(!this.reportDirectory.exists())
				this.reportDirectory.mkdirs();

			// Check if there exists files with the report title
			file = new File(this.reportDirectory.getAbsolutePath()+"/" + this.reportTitle + getName() +"-1.txt");
			if(file.exists()){

				// Count the number of executions with the same report title
				files = this.reportDirectory.listFiles();
				int n=0;
				for(File f: files){
					if(f.isFile() && f.getName().contains(this.reportTitle + getName())){
						n++;
					}
				}
				setNumberOfExecution(n+1);
			}
			else
				setNumberOfExecution(1);

			this.reportFile = new File(this.reportDirectory.getAbsolutePath()+"/"+this.reportTitle + getName() + "-" + numberOfExecution+".txt");

			// Create the report writer and add some initial information
			try {
				this.reportFileWriter = new FileWriter(this.reportFile);
				this.reportFileWriter.flush();

				// Write some basic information
				sb = new StringBuffer();
				String dateString = new Date(System.currentTimeMillis()).toString();
				sb.append("Date: " + dateString +"\n");
				sb.append("Algorithm: " + alg.getClass().getSimpleName() + "\n");
				sb.append("Multiobjective strategy: " + algName +"\n");
				List<Objective> objs = ((IMOEvaluator)alg.getContext().getEvaluator()).getObjectives();
				sb.append("Objective functions: ");
				int nObjs = objs.size();
				for(int i=0; i<nObjs-1; i++){
					sb.append(objs.get(i).getClass().getSimpleName() + ",");
				}
				sb.append(objs.get(nObjs-1).getClass().getSimpleName()+"\n----------\n");
				this.reportFileWriter.write(sb.toString());
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
	public void iterationCompleted(AlgorithmEvent event) {

		// Current generation
		int generation = ((IMOAlgorithm)event.getAlgorithm()).getContext().getGeneration();

		// Check if this is correct generation
		if (generation%getReportFrequency() == 0) {
			doIterationReport((IMOAlgorithm) event.getAlgorithm(),false);
		}
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void algorithmFinished(AlgorithmEvent event) {
		
		IMOAlgorithm algorithm = (IMOAlgorithm) event.getAlgorithm();
		// Do last generation report
		doIterationReport(algorithm,true);
		
		// Close report file if necessary
		if (this.reportOnFile  && this.reportFile != null) {
			try {
				this.reportFileWriter.append("Time (ms): " + algorithm.executionTime());
				this.reportFileWriter.close();
			} 
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		else if (this.reportOnConsole){
			System.out.println("Time (ms): " + algorithm.executionTime() + "\n-----------------------\n");
		}
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void algorithmTerminated(AlgorithmEvent e) {
		// Do nothing
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public boolean equals(Object other){
		if (other instanceof MOReporter) {
			MOReporter cother = (MOReporter) other;
			EqualsBuilder eb = new EqualsBuilder();
			// reportTitle
			eb.append(reportTitle, cother.reportTitle);
			// reportFrequency
			eb.append(reportFrequency, cother.reportFrequency);
			// reportOnConsole
			eb.append(reportOnConsole, cother.reportOnConsole);
			// reportOnFile
			eb.append(reportOnFile, cother.reportOnFile);		
			return eb.isEquals();
		}
		else {
			return false;
		}
	}

	/////////////////////////////////////////////////////////////////
	// -------------------------------------------- Protected methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Do an iteration report.
	 * @param algorithm The multi-objective algorithm.
	 * @param finalReport If true, the final report should be generated.
	 * */
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

		// Specific report
		doReport(algorithm, generation, finalReport);
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Abstract methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Report specific information.
	 * @param algorithm The multi-objective algorithm.
	 * @param generation Current generation.
	 * @param finalReport If true, the final report should be generated.
	 * */
	protected abstract void doReport(IMOAlgorithm algorithm, int generation, boolean finalReport);

	/**
	 * Report specific identifier.
	 * @return A <code>string</code> with an
	 * acronym of the reporter class name.
	 * */
	protected abstract String getName();
}