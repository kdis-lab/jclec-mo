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
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationRuntimeException;

import net.sf.jclec.AlgorithmEvent;
import net.sf.jclec.IConfigure;
import net.sf.jclec.IFitness;
import net.sf.jclec.IIndividual;
import net.sf.jclec.mo.IMOAlgorithm;
import net.sf.jclec.mo.command.NonDominatedSolutionsExtractor;
import net.sf.jclec.mo.comparator.fcomparator.MOFitnessComparator;
import net.sf.jclec.mo.comparator.fcomparator.ParetoComparator;

/**
 * Pareto set reporter for MO algorithms. Non dominated
 * solutions from the archive (if exists) or the 
 * current population are stored in a file at the given
 * frequency. 
 *  
 * <p>HISTORY:
 * <ul>
 * <li>(AR|JRR|SV, 1.0, March 2018)		First release.</li>
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

public class MOParetoSetReporter extends MOReporter {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -7695660462030543095L;

	/** Directory where the pareto set will be stored */
	protected File reportDirParetoSet;

	/** Flag indicating that the archive should be processed before storing solutions */
	protected boolean filterFromArchive;

	/** A command to extract the final solutions */
	protected NonDominatedSolutionsExtractor command;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////	

	/**
	 * Empty constructor.
	 * */
	public MOParetoSetReporter() {
		super();
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////	

	/**
	 * Get report directory for populations.
	 * @return report directory
	 * */
	public File getParetoDirectory() {
		return this.reportDirParetoSet;
	}

	/**
	 * Set report directory for populations.
	 * @param reportDirectory New report directory.
	 * */
	protected void setParetoDirectory(File reportDirectory) {
		this.reportDirParetoSet = reportDirectory;
	}

	/**
	 * Filter non-dominated solutions from the archive?
	 * @return True if the archive should be processed before store it, false otherwise.
	 * */
	public boolean isFilterFromArchive() {
		return this.filterFromArchive;
	}

	/**
	 * Set whether the non-dominated solutions from
	 * the archive should be extracted.
	 * @param filterFromArchive New flag value.
	 * */
	protected void setFilterFromArchive(boolean filterFromArchive) {
		this.filterFromArchive = filterFromArchive;
	}

	/**
	 * Get the command used to extract non dominated solutions.
	 * @return An extractor of solutions.
	 * */
	public NonDominatedSolutionsExtractor getExtractorCommand() {
		return this.command;
	}

	/**
	 * Set the command used to extract non dominated solutions.
	 * @param command An extractor of solutions.
	 * */
	protected void setExtractorCommand(NonDominatedSolutionsExtractor command) {
		this.command = command;
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>Specific parameters are:
	 * <ul>
	 * 
	 * <li>filter-archive (<code>boolean</code>): <p>Extract non-dominated
	 * solutions from the archive. False by default.</p></li>
	 * 
	 * <li>command-extract (<code>NonDominatedSolutionsExtractor</code>):
	 * 	<p>The command that will be used to extract the non dominated solutions. <code>NonDominatedSolutionsExtractor</code> 
	 * 	with <code>ParetoComparator</code> by default.If other class should be used, then the configuration file should contain 
	 *  the following parameter:
	 * 		<ul>
	 * 			<li>[@type] (<code>String</code>): <p>Name of the class that implements the command.</p></li>
	 * 		</ul>
	 * </p>
	 * </li>
	 * </ul>
	 * */
	@Override
	public void configure(Configuration settings) {
		// Call super method
		super.configure(settings);

		// Filter non-dominated solutions flag
		boolean filter = settings.getBoolean("filter-from-archive", false);
		this.setFilterFromArchive(filter);

		// Configure the extractor of solutions
		this.setExtractorCommandSettings(settings.subset("command-extract"));
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void algorithmStarted(AlgorithmEvent event) {

		// Call super implementation
		super.algorithmStarted(event);

		// Configure the comparators of the objective functions
		if(this.command.getComparator().getComponentComparators() == null){
			IMOAlgorithm algorithm = (IMOAlgorithm)event.getAlgorithm();
			Comparator<IFitness> fcomparators [] = ((MOFitnessComparator)algorithm.getContext().getEvaluator().getComparator()).getComponentComparators();
			this.command.getComparator().setComponentComparators(fcomparators);
		}

		// Create a directory for storing pareto set files
		if(isReportOnFile()){
			File dir = new File(getReportDirectory().getAbsolutePath()+"/pareto-sets/");
			setParetoDirectory(dir);
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
			filename = algName +"-"+getNumberOfExecution()+"-final-ps.txt";
		else
			filename = algName +"-"+getNumberOfExecution()+"-gener"+generation+"-ps.txt";
		File file = new File(getParetoDirectory(), filename);
		FileWriter filewriter;
		StringBuffer sb = new StringBuffer();

		// Extract the pareto set according to the population sets used by the algorithm
		// and the specific configuration of this listener
		List<IIndividual> paretoSet = algorithm.getArchive();
		if(paretoSet == null){
			this.command.setPopulation(algorithm.getContext().getInhabitants());
			this.command.execute();
			paretoSet = this.command.getNonDominatedSolutions();
			sb.append("\tNon-dominated solutions (extracted from the population):\n");
		}
		else if(isFilterFromArchive()){
			this.command.setPopulation(algorithm.getArchive());
			this.command.execute();
			paretoSet = this.command.getNonDominatedSolutions();
			sb.append("\tNon-dominated solutions (extracted from the archive):\n");
		}
		else{
			sb.append("\tNon-dominated solutions (archive):\n");
		}

		if(paretoSet != null){
			for(IIndividual ind: paretoSet){
				sb.append(ind.toString() + "\n");
			}
		}

		// Write string to the report file (if necessary) 
		if(isReportOnConsole())
			System.out.println(sb.toString());
		
		if(isReportOnFile()){
			try {
				filewriter = new FileWriter(file);
				filewriter.write(sb.toString());
				filewriter.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	protected String getName(){
		return "-ParetoSet";
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Private methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Configure the command that extracts the non dominated solutions.
	 * @param settings The command configuration.
	 * */
	@SuppressWarnings("unchecked")
	private void setExtractorCommandSettings(Configuration settings){

		NonDominatedSolutionsExtractor command;
		String classname = settings.getString("[@type]");

		// A specific command has been configured
		if(classname != null){
			// Create and configure the command
			Class<? extends NonDominatedSolutionsExtractor> commandClass;
			try {
				// Class
				commandClass = (Class<? extends NonDominatedSolutionsExtractor>) Class.forName(classname);
				command = commandClass.getDeclaredConstructor().newInstance();

				// Configure specific parameters
				if(command instanceof IConfigure){
					((IConfigure)command).configure(settings);
				}
			} catch (InstantiationException|IllegalAccessException|ClassNotFoundException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				throw new ConfigurationRuntimeException("Problems creating an instance of command", e);
			}
		}

		// Default command configuration. At this point, the evaluator has not been created yet, 
		// so the components of the fitness comparator are unknown
		else{
			command = new NonDominatedSolutionsExtractor();
			command.setComparator(new ParetoComparator(null));
		}

		// Set the command
		this.setExtractorCommand(command);
	}
}