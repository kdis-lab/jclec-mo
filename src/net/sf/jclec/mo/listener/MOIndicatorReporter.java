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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationRuntimeException;

import net.sf.jclec.AlgorithmEvent;
import net.sf.jclec.IConfigure;
import net.sf.jclec.IFitness;
import net.sf.jclec.IIndividual;
import net.sf.jclec.mo.IMOAlgorithm;
import net.sf.jclec.mo.IMOEvaluator;
import net.sf.jclec.mo.command.NonDominatedSolutionsExtractor;
import net.sf.jclec.mo.command.ObjectiveInverter;
import net.sf.jclec.mo.command.ObjectiveScaler;
import net.sf.jclec.mo.command.ObjectiveScalerNoBounds;
import net.sf.jclec.mo.comparator.fcomparator.MOFitnessComparator;
import net.sf.jclec.mo.comparator.fcomparator.ParetoComparator;
import net.sf.jclec.mo.evaluation.Objective;
import net.sf.jclec.mo.indicator.Indicator;

/**
 * Reporter for multi-objective algorithms that evaluates the quality
 * of the Pareto front using quality indicators. The list of indicators 
 * to be measured can be configured by the user.
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
 * @see Indicator
 * */

public class MOIndicatorReporter extends MOReporter {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -1714551237705404778L;

	/** List of quality indicators */
	protected List<Indicator> indicators;

	/** A command to extract the final solutions */
	protected NonDominatedSolutionsExtractor commandExtract;

	/** A command to invert the objective values */
	protected ObjectiveInverter commandInvert;

	/** A command to scale the objective values */
	protected ObjectiveScaler commandScale;

	/** Flag indicating that the archive should be processed before storing solutions */
	protected boolean filterFromArchive;

	/** The type of preprocessing required by each indicator */
	protected int [] type;

	/** Minimum objective values to be used when scaling without bounds is required*/
	protected double [] minValues;

	/** Maximum objective values to be used when scaling without bounds is required*/
	protected double [] maxValues;

	/** Minimum objective values to be used when inverting and scaling without bounds are required */
	protected double [] invertedMinValues;

	/** Maximum objective values to be used when inverting and scaling without bounds are required */
	protected double [] invertedMaxValues;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////	

	/**
	 * Empty constructor.
	 * */
	public MOIndicatorReporter() {
		super();
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////

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
	 * Get the list of indicators.
	 * @return List of indicators.
	 * */
	public List<Indicator> getIndicators() {
		return indicators;
	}

	/**
	 * Set list of indicators.
	 * @param indicators A list containing the indicators that will be calculated.
	 * */
	protected void setIndicators(List<Indicator> indicators) {
		this.indicators = indicators;
	}

	/**
	 * Get the command used to extract non dominated solutions.
	 * @return An extractor of solutions.
	 * */
	public NonDominatedSolutionsExtractor getExtractorCommand() {
		return commandExtract;
	}

	/**
	 * Set the command used to extract non dominated solutions.
	 * @param commandExtractor An extractor of solutions.
	 * */
	protected void setExtractorCommand(NonDominatedSolutionsExtractor commandExtractor) {
		this.commandExtract = commandExtractor;
	}

	/**
	 * Get the command used to invert the objective values.
	 * @return An inverter of objective values.
	 * */
	public ObjectiveInverter getInverterCommand() {
		return commandInvert;
	}

	/**
	 * Set the command used to invert the objective values.
	 * @param commandInvert An inverter of objective values.
	 * */
	protected void setInverterCommand(ObjectiveInverter commandInvert) {
		this.commandInvert = commandInvert;
	}

	/**
	 * Get the command used to scale the objective values.
	 * @return The command to scale objective values.
	 * */
	public ObjectiveScaler getScalerCommand() {
		return this.commandScale;
	}

	/**
	 * Set the command used to scale the objective values.
	 * @param commandScale A command to scale the objective values.
	 * */
	protected void setScalerCommand(ObjectiveScaler commandScale) {
		this.commandScale = commandScale;
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>Parameters for <code>MOIndicatorReporter</code> are:
	 * <ul>
	 * 
	 * 	<li>indicator (List of <code>Indicator</code>):
	 * 	<p>The list of indicators to be evaluated. Each indicator should be specified using the tag "indicator" and the following parameters:
	 * 		<ul>
	 * 			<li>[@type] (<code>String</code>): <p>Name of the class that implements the indicator.</p></li>
	 * 		</ul>	
	 * 	</p>
	 * 	</li>
	 * 	
	 * 	<li>command-invert (<code>ObjectiveInverter</code>):
	 * 	<p>The command to be used if inversion of objective values is required. <code>ObjectiveInverter</code> by default. If other class should
	 * 	be used, then the configuration file should contain the following parameter:
	 * 		<ul>
	 * 			<li>[@type] (<code>String</code>): <p>Name of the class that implements the command.</p></li>
	 * 		</ul>
	 *	</p>
	 *	</li>
	 *	
	 *	<li>command-scale (<code>ObjectiveScaler</code>):
	 * 	<p>The command to be used if objective values should be scaled. <code>ObjectiveScaleWithoutBounds</code> by default.
	 * 	If other class should be used, then the configuration file should contain the following parameter:
	 * 		<ul>
	 * 			<li>[@type] (<code>String</code>): <p>Name of the class that implements the command.</p></li>
	 * 		</ul>
	 *	</p>
	 *	</li>
	 *	
	 *	<li>command-extract (<code>NonDominatedSolutionsExtractor</code>):
	 * 	<p>The command to be used to extract the non dominated solutions. <code>NonDominatedSolutionsExtractor</code> with 
	 *  <code>ParetoComparator</code> by default. If other class should be used, then the configuration file should contain 
	 *  the following parameter:
	 * 		<ul>
	 * 			<li>[@type] (<code>String</code>): <p>Name of the class that implements the command.</p></li>
	 * 		</ul>
	 *	</p>
	 *	</li>
	 *	
	 *	<li>filter-archive (<code>boolean</code>): 
	 *  <p>Extract non-dominated solutions from the archive. False by default.</p>
	 * 	</li>
	 * </ul> 
	 * */
	@Override
	public void configure(Configuration settings) {

		// Get super implementation
		super.configure(settings);

		// Filter non-dominated solutions flag
		boolean filter = settings.getBoolean("filter-from-archive", false);
		this.setFilterFromArchive(filter);

		// Configure the list of indicators
		setIndicatorSettings(settings.subset("indicators"));

		// Configure the commands
		setInverterCommandSettings(settings.subset("command-invert"));
		setScalerCommandSettings(settings.subset("command-scale"));
		setExtractorCommandSettings(settings.subset("command-extract"));
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void algorithmStarted(AlgorithmEvent event) {

		// Call super implementation
		super.algorithmStarted(event);

		// Configure the comparators of the objective functions
		if(this.commandExtract.getComparator().getComponentComparators() == null){
			IMOAlgorithm algorithm = (IMOAlgorithm)event.getAlgorithm();
			Comparator<IFitness> fcomparators [] = ((MOFitnessComparator)algorithm.getContext().getEvaluator().getComparator()).getComponentComparators();
			this.commandExtract.getComparator().setComponentComparators(fcomparators);
		}

		// If the scaler was not set, check if it is required. If so, a default scaler is assigned
		List<Objective> objectives = ((IMOEvaluator)((IMOAlgorithm)event.getAlgorithm()).getContext().getEvaluator()).getObjectives();
		if(this.commandScale == null){
			boolean objectivesAreIn01 = true;
			int nObjs = objectives.size();
			for(int i=0; objectivesAreIn01 && i<nObjs; i++){
				if(objectives.get(i).getMinimum() != 0.0 || objectives.get(i).getMaximum() != 1.0){
					objectivesAreIn01 = false;
				}
			}
			if(!objectivesAreIn01){
				this.commandScale = new ObjectiveScalerNoBounds();
			}
		}

		// Assign the type of preprocessing that each indicator needs
		assignTypeOfPreprocessing((IMOAlgorithm) event.getAlgorithm());

		// Compute the inverted bounds
		computeInvertedBounds(objectives);

		// Do an iteration report
		doIterationReport((IMOAlgorithm) event.getAlgorithm(),false);
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	protected void doReport(IMOAlgorithm algorithm, int generation, boolean finalReport) {

		StringBuffer sb = new StringBuffer();
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

		int size = this.indicators.size();
		int popSize = paretoSet.size();
		Indicator indicator;
		List<IIndividual> invertedParetoSet = null;
		List<IIndividual> scaledParetoSet = null;
		List<IIndividual> invScaledParetoSet = null;

		for(int i=0; i<size; i++){

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

			// Append the result
			name = indicator.getClass().getSimpleName();
			sb.append("\t"+name + ": " + indicator.getResult() + "\n");
		}

		// Write report string to the standard output (if necessary) 
		if (isReportOnConsole()) {
			System.out.println(sb.toString());
		}

		// Write string to the report file (if necessary) 
		if (isReportOnFile()) {
			try {
				getReportFileWriter().write(sb.toString());
				getReportFileWriter().flush();
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
		return "-ind";
	}

	/////////////////////////////////////////////////////////////////
	// -------------------------------------------- Protected methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Check the indicator constraints and set the type
	 * of transformations that will required before computing
	 * the indicator.
	 * @param algorithm The algorithm being reported.
	 * */
	protected void assignTypeOfPreprocessing(IMOAlgorithm algorithm){

		int size = this.indicators.size();
		this.type = new int[size];	// set the type of population to be used. 1=current, 2=inverted, 3=scaled, 4=inverted&scaled
		Boolean maxProblem, maxIndicator, scaleIndicator;
		Indicator indicator;

		// Firstly, transform the population if required
		maxProblem = algorithm.getStrategy().isMaximized();

		for(int i=0; i<size; i++){

			// Get the indicator
			indicator = this.indicators.get(i);

			// Check the conditions and create copies of the population if required
			maxIndicator = indicator.requiresMaxProblem();
			scaleIndicator = indicator.requiresScaledObjectives();

			// The indicator has not constraints regarding the type of problem
			if(maxIndicator==null && (scaleIndicator==null || scaleIndicator==false)){
				this.type[i] = 1;
			}
			// Only scale is required
			else if(maxIndicator==null && scaleIndicator==true){
				this.type[i]=3;
			}

			// Scale is not required and inversion is required
			else if(!scaleIndicator && maxProblem!=null &&
					((maxIndicator==true && maxProblem==false) || (maxIndicator==false && maxProblem==true)) ){
				this.type[i] = 2;
			}

			// Scale is required but inversion is not required
			else if(scaleIndicator && maxProblem!=null &&
					((maxIndicator==true && maxProblem==true) || (maxIndicator==false && maxProblem==false))){
				this.type[i] = 3;
			}

			// Both inversion and scale is required
			else if(scaleIndicator && maxProblem!=null &&
					((maxIndicator==true && maxProblem==false) || (maxIndicator==false && maxProblem==true))){
				this.type[i] = 4;
			}

			// By default, use the original Pareto set
			else{ 
				this.type[i] = 1;
			}
		}
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Private methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Configure the list of indicators.
	 * @param settings The indicator configuration.
	 * */
	@SuppressWarnings("unchecked")
	private void setIndicatorSettings(Configuration settings) {
		// Get the class names
		String classnames [] = settings.getStringArray("indicator[@type]");
		int size = classnames.length;

		// Create the array of indicators
		List<Indicator> indicators = new ArrayList<Indicator>(size);
		Indicator indicator;

		// Create and configure each indicator
		Class<? extends Indicator> indicatorClass;

		try {
			for(int i=0; i<size; i++){
				// Class
				indicatorClass = (Class<? extends Indicator>) Class.forName(classnames[i]);
				indicator = indicatorClass.getDeclaredConstructor().newInstance();

				// Configure specific parameters
				if(indicator instanceof IConfigure){
					((IConfigure)indicator).configure(settings);
				}

				// Add the objective to the list
				indicators.add(indicator);
			}
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new ConfigurationRuntimeException("Problems creating an instance of indicator", e);
		}

		// Set the list of indicators
		this.setIndicators(indicators);
	}

	/**
	 * Configure the command that inverts the objective values.
	 * @param settings The command configuration.
	 * */
	@SuppressWarnings("unchecked")
	private void setInverterCommandSettings(Configuration settings) {

		ObjectiveInverter command;
		String classname = settings.getString("[@type]");

		if(classname == null){
			command = new ObjectiveInverter();
		}

		else{
			// Create and configure the command
			Class<? extends ObjectiveInverter> commandClass;
			try {
				// Class
				commandClass = (Class<? extends ObjectiveInverter>) Class.forName(classname);
				command = commandClass.getDeclaredConstructor().newInstance();

				// Configure specific parameters
				if(command instanceof IConfigure){
					((IConfigure)command).configure(settings);
				}
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				throw new ConfigurationRuntimeException("Problems creating an instance of command", e);
			}
		}

		// Set the command
		this.setInverterCommand(command);
	}

	/**
	 * Configure the command that scales the objective values.
	 * @param settings The command configuration.
	 * */
	@SuppressWarnings("unchecked")
	private void setScalerCommandSettings(Configuration settings){

		ObjectiveScaler command;
		String classname = settings.getString("[@type]");
		// The scaler is assigned only if it explicitly set in the configuration file 
		if(classname != null){
			// Create and configure the command
			Class<? extends ObjectiveScaler> commandClass;
			try {
				// Class
				commandClass = (Class<? extends ObjectiveScaler>) Class.forName(classname);
				command = commandClass.getDeclaredConstructor().newInstance();

				// Configure specific parameters
				if(command instanceof IConfigure){
					((IConfigure)command).configure(settings);
				}
			} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				throw new ConfigurationRuntimeException("Problems creating an instance of command", e);
			}
			// Set the command
			this.setScalerCommand(command);
		}
	}

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

	/**
	 * Copy and invert the lower and upper bounds of the scaled
	 * command, thus they will be available if transformations are required.
	 * @param objectives The list of objectives
	 * */
	private void computeInvertedBounds(List<Objective> objectives){

		double [] minValues;
		double [] maxValues;
		int nObjs = objectives.size();

		if(this.commandScale != null){
			minValues = this.commandScale.getMinValues();
			maxValues = this.commandScale.getMaxValues();
		}
		else{
			minValues = new double[nObjs];
			maxValues = new double[nObjs];
			for(int i=0; i<nObjs; i++){
				minValues[i] = objectives.get(i).getMinimum();
				maxValues[i] = objectives.get(i).getMaximum();
			}
		}

		if(minValues!=null && maxValues!=null){

			// Copy the original values
			this.maxValues = new double[nObjs];
			this.minValues = new double[nObjs];
			for(int i=0; i<nObjs; i++){
				this.minValues[i] = minValues[i];
				this.maxValues[i] = maxValues[i];
			}

			// Invert the values
			this.invertedMinValues = new double[nObjs];
			this.invertedMaxValues = new double[nObjs];
			for(int i=0; i<nObjs; i++){
				this.invertedMinValues[i] = -maxValues[i];
				this.invertedMaxValues[i] = -minValues[i];
			}
		}
	}
}