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
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationRuntimeException;

import es.uco.kdis.datapro.dataset.Dataset;
import es.uco.kdis.datapro.dataset.column.NumericalColumn;
import es.uco.kdis.datapro.dataset.source.CsvDataset;
import net.sf.jclec.AlgorithmEvent;
import net.sf.jclec.IConfigure;
import net.sf.jclec.IFitness;
import net.sf.jclec.IIndividual;
import net.sf.jclec.mo.IMOEvaluator;
import net.sf.jclec.mo.IMOAlgorithm;
import net.sf.jclec.mo.command.NonDominatedSolutionsExtractor;
import net.sf.jclec.mo.comparator.fcomparator.MOFitnessComparator;
import net.sf.jclec.mo.comparator.fcomparator.ParetoComparator;
import net.sf.jclec.mo.evaluation.Objective;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;

/**
 * Reporter for multi-objective algorithms that stores the 
 * pareto front in a CSV file. It only generates reports on files.
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
 * @version 1.0
 * @see MOReporter
 * */
public class MOParetoFrontReporter extends MOReporter {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -3038012844026164327L;

	/** Directory for saving pareto fronts */
	protected File reportDirParetoFront;

	/** Flag indicating that the archive should be processed before storing solutions */
	protected boolean filterFromArchive;

	/** A command to extract the final solutions */
	protected NonDominatedSolutionsExtractor command;

	/** A dataset to store the PF */
	protected Dataset dataset;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////	

	/**
	 * Empty constructor.
	 * */
	public MOParetoFrontReporter() {
		super();
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////	

	/**
	 * Get report directory for pareto fronts.
	 * @return report directory
	 * */
	public File getParetoDirectory() {
		return this.reportDirParetoFront;
	}

	/**
	 * Set report directory for pareto fronts.
	 * @param reportDirectory New report directory
	 * */
	protected void setParetoDirectory(File reportDirectory) {
		this.reportDirParetoFront = reportDirectory;
	}

	/**
	 * Filter non-dominated solutions from the archive?
	 * @return True if the archive should be processed before store it, false otherwise
	 * */
	public boolean isFilterFromArchive() {
		return this.filterFromArchive;
	}

	/**
	 * Set whether the non-dominated solutions from
	 * the archive should be extracted.
	 * @param filterFromArchive New flag value
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
	 * <li>filter-archive (<code>boolean</code>): <p>Extract non-dominated
	 * solutions from the archive. False by default.</p></li>
	 * <li>command-extract (<code>NonDominatedSolutionsExtractor</code>):
	 * 	<p>The command that will be used to extract the non dominated solutions. <code>NonDominatedSolutionsExtractor</code> 
	 * 	with <code>ParetoComparator</code> by default.If other class should be used, then the configuration file should contain 
	 *  the following parameter:
	 * 		<ul>
	 * 			<li>[@type] (<code>String</code>): <p>Name of the class that implements the command.</p></li>
	 * 		</ul>
	 * 	</p>
	 * </li>
	 * </ul>
	 * </p>
	 * <p><code>report-on-console</code> and <code>report-on-file</code> (inherited from <code>MOReporter</code>) are not considered.</p>
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

		// Set appropriate flags
		setReportOnConsole(false);
		setReportOnFile(true);
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void algorithmStarted(AlgorithmEvent event) {

		// Call super implementation
		super.algorithmStarted(event);

		// Configure the comparators of the objective functions
		IMOAlgorithm algorithm = (IMOAlgorithm)event.getAlgorithm();

		if(this.command.getComparator().getComponentComparators() == null){
			Comparator<IFitness> fcomparators [] = ((MOFitnessComparator)algorithm.getContext().getEvaluator().getComparator()).getComponentComparators();
			this.command.getComparator().setComponentComparators(fcomparators);
		}

		// Create a directory for storing pareto front files
		File dir = new File(getReportDirectory().getAbsolutePath()+"/pareto-fronts/");
		setParetoDirectory(dir);
		if (!dir.exists()) {
			dir.mkdirs();
		}

		// Do an iteration report
		doIterationReport((IMOAlgorithm) event.getAlgorithm(),false);
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	protected void doReport(IMOAlgorithm algorithm, int generation, boolean finalReport) {

		String algName = algorithm.getStrategy().getClass().getSimpleName().toLowerCase();
		String filename;
		if(finalReport)
			filename = getParetoDirectory() + "/" + algName +"-"+getNumberOfExecution()+"-final-pf.csv";
		else
			filename = getParetoDirectory() + "/" + algName +"-"+getNumberOfExecution()+"-gener"+generation+"-pf.csv";
		String name;

		// Create the dataset
		List<Objective> objectives = ((IMOEvaluator)algorithm.getContext().getEvaluator()).getObjectives();
		int nObjectives = objectives.size();
		this.dataset = new CsvDataset();
		for(int i=0; i<nObjectives; i++){
			name = objectives.get(i).getClass().getSimpleName();
			this.dataset.addColumn(new NumericalColumn(name));
		}

		// Extract the pareto set according to the population sets used by the algorithm
		// and the specific configuration of this listener
		List<IIndividual> paretoSet = algorithm.getArchive();
		if(paretoSet == null){
			this.command.setPopulation(algorithm.getContext().getInhabitants());
			this.command.execute();
			paretoSet = this.command.getNonDominatedSolutions();
		}
		else if(isFilterFromArchive()){
			this.command.setPopulation(algorithm.getArchive());
			this.command.execute();
			paretoSet = this.command.getNonDominatedSolutions();
		}

		// Store the objective values of non dominated individuals
		MOFitness fitness;
		try {
			if(paretoSet != null){
				for(IIndividual ind: paretoSet){
					fitness = ((MOFitness)ind.getFitness());
					for(int i=0; i<nObjectives; i++){
						this.dataset.getColumn(i).addValue(fitness.getObjectiveDoubleValue(i));
					}
				}
			}
			this.dataset.setNumberOfDecimals(8);
			((CsvDataset)this.dataset).writeDataset(filename);

		} catch (IllegalAccessException | IllegalArgumentException | IOException e) {
			e.printStackTrace();
		}
		
		// Clean the dataset
		this.dataset = null;
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	protected String getName() {
		return "-ParetoFront";
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
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
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
