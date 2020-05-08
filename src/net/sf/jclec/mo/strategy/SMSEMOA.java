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

package net.sf.jclec.mo.strategy;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationRuntimeException;

import net.sf.jclec.IConfigure;
import net.sf.jclec.IFitness;
import net.sf.jclec.IIndividual;
import net.sf.jclec.ISelector;
import net.sf.jclec.ITool;
import net.sf.jclec.mo.command.ObjectiveInverter;
import net.sf.jclec.mo.command.ObjectiveScaler;
import net.sf.jclec.mo.command.ObjectiveScalerNoBounds;
import net.sf.jclec.mo.command.PopulationSplitter;
import net.sf.jclec.mo.comparator.MOSolutionComparator;
import net.sf.jclec.mo.comparator.fcomparator.ParetoComparator;
import net.sf.jclec.mo.indicator.Hypervolume;

/**
 * SMS-EMOA strategy
 * 
 * <p>The <b>S</b> <b>M</b>etric <b>S</b>election <b>E</b>volutionary
 * <b>M</b>ulti <b>O</b>bjective <b>A</b>lgorithm is a steady-state
 * algorithm that uses the S metric (hypervolume) to select the individuals 
 * that contribute the most to the metric in both the mating selection 
 * and environmental selection. Non-dominated sorting is used to save non-dominated
 * solutions in the environmental selection before using the S contribution to decide
 * among solutions belonging to the critical front.</p>
 *
 * <p>This implementation supports both minimization and
 * maximization problems and also different scales in the
 * objective functions. Objective values are scaled before
 * computing the hypervolume contribution. The unique precondition
 * is that all objectives should be maximized or minimized, combining
 * both types of objectives is not allowed.</p>
 * 
 * <p><i>Paper</i>: N. Beume, B. Naujoks, M. Emmerich, 
 * “SMS-EMOA: A Multiobjective selection based on dominated hypervolume",
 * European Journal of Operational Research, vol. 181, pp. 1653-1669, 2007.</p>
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
 * @see MOStrategy
 * */

public class SMSEMOA extends MOStrategy {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -4136244177261958932L;

	/** Parent selector */
	protected ISelector selector;

	/** Command to split population into fronts */
	protected PopulationSplitter commandSplit;
	
	/** Command to scale the objective values */
	protected ObjectiveScaler commandScale;
	
	/** Command to invert objective values */
	protected ObjectiveInverter commandInvert;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public SMSEMOA() {
		super();
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Get the parent selector.
	 * @return Parent selector
	 */
	public ISelector getSelector() {
		return this.selector;
	}

	/**
	 * Set the parent selector.	
	 * @param selector The selector that has to be set.
	 */
	protected void setSelector(ISelector selector) {
		this.selector = selector;
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>Parameters for SMS-EMOA are:
	 * <ul>
	 * <li>parent-selector <code>ISelector</code> (complex)
	 * <p>Parent selector.</li> 
	 * </p>
	 * */
	@Override
	@SuppressWarnings("unchecked")
	public void configure(Configuration settings){

		// Call super configuration
		super.configure(settings);

		// Selector
		try {
			String classname = settings.getString("parents-selector[@type]");
			Class<? extends ISelector> selectorClass = (Class<? extends ISelector>) Class.forName(classname);
			ISelector selector = selectorClass.getDeclaredConstructor().newInstance();
			if (selector instanceof IConfigure) {
				((IConfigure) selector).configure(settings.subset("parents-selector"));
			}
			setSelector(selector);
		} 
		catch (ClassNotFoundException e) {
			throw new ConfigurationRuntimeException("Illegal selector classname");
		} 
		catch (InstantiationException|IllegalAccessException|IllegalArgumentException|InvocationTargetException|NoSuchMethodException|SecurityException e) {
			throw new ConfigurationRuntimeException("Problems creating an instance of selector", e);
		} 
	}

	/**
	 * {@inheritDoc}
	 * <p>SMS-EMOA does not use an archive, so <code>null</code>
	 * is returned.</p>
	 * */
	@Override
	public List<IIndividual> initialize(List<IIndividual> population) {

		// Configure the selector with the context
		if(this.selector instanceof ITool)
			this.selector.contextualize(getContext());

		// Initialize the command
		this.commandSplit = new PopulationSplitter();
		this.commandSplit.setComparator(this.comparator);
		this.commandScale = new ObjectiveScalerNoBounds();
		this.commandInvert = new ObjectiveInverter();

		return null;
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void update() {
		// do nothing
	}

	/**
	 * {@inheritDoc}
	 * <p>SMS-EMOA requires a Pareto comparator.</p>
	 * */
	@Override
	public void createSolutionComparator(Comparator<IFitness>[] components) {
		// Fitness comparator
		ParetoComparator fcomparator = new ParetoComparator(components);
		// Individuals comparator
		MOSolutionComparator comparator = new MOSolutionComparator(fcomparator);
		setSolutionComparator(comparator);
	}

	/**
	 * {@inheritDoc}
	 * <p>In SMS-EMOA, two parents are selected from the current population.</p>
	 * */
	@Override
	public List<IIndividual> matingSelection(List<IIndividual> population, List<IIndividual> archive) {
		List<IIndividual> parents = new ArrayList<IIndividual>(2);
		parents = getSelector().select(population, 2);	// select two parents using the configured operator
		return parents;
	}

	/**
	 * {@inheritDoc}
	 * <p>In SMS-EMOA, the offspring replaces an individual in the
	 * current population if the new individual leads to a higher
	 * value of the S metric.</p>
	 * */
	@Override
	public List<IIndividual> environmentalSelection(List<IIndividual> population, 
			List<IIndividual> offspring, List<IIndividual> archive) {

		// Join the populations
		ArrayList<IIndividual> survivors = new ArrayList<IIndividual>();
		survivors.addAll(population);
		survivors.add(offspring.get(0));	//only one offspring was generated
		// Split the population into fronts
		this.commandSplit.setPopulation(survivors);
		this.commandSplit.execute();
		List<List<IIndividual>> populationByFronts = this.commandSplit.getSplitPopulation();
		
		// One individual should be removed from the worst ranked front
		survivors.clear();
		int indexToRemove = reduce(populationByFronts.get(populationByFronts.size()-1));
		populationByFronts.get(populationByFronts.size()-1).remove(indexToRemove);
		for(int i=0; i<populationByFronts.size(); i++){
			survivors.addAll(populationByFronts.get(i));
		}
		
		return survivors;
	}

	/**
	 * {@inheritDoc}
	 * <p>SMS-EMOA does not use an archive, so <code>null</code> is returned.</p>
	 * */
	@Override
	public List<IIndividual> updateArchive(List<IIndividual> population,
			List<IIndividual> offspring, List<IIndividual> archive) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * <p>SMS-EMOA does not require a fitness assignment method.</p>
	 * */
	@Override
	protected void fitnessAssignment(List<IIndividual> population, List<IIndividual> archive) {
		// do nothing
	}

	/**
	 * Select the individual that should be discarded from the given
	 * population computing the S metric contribution.
	 * @param population The population of individuals.
	 * @return The position index of the individual that less contributes to S metric.
	 * */
	protected int reduce(List<IIndividual> population){

		int indexToRemove = -1;
		double minHvContribution = Double.MAX_VALUE;
		double totalHv, currentHv;
		Hypervolume hv;
		IIndividual ind;
		int size = population.size();

		if(size > 1){

			// Firstly, copy the population as the objective values will be modified
			List<IIndividual> copy = new ArrayList<IIndividual>();
			for(int i=0; i<size; i++){
				copy.add(population.get(i).copy());
			}

			// Invert the objective values if required, since the hypervolume indicator requires a maximization problem
			if(!isMaximized()){
				this.commandInvert.setPopulation(copy);
				this.commandInvert.execute();
			}

			// Scale the objective values
			this.commandScale.setPopulation(copy);
			this.commandScale.execute();

			// Get the total hypervolume
			hv = new Hypervolume(copy);
			hv.calculate();
			totalHv = hv.getResult();

			// For each solution in the set, compute the hypervolume without considering that solution
			for(int i=0; i<size; i++){

				// Save a copy of the current individual
				ind = copy.get(i).copy(); 

				// Remove the individual and compute the new hypervolume
				copy.remove(i);
				hv.setFront(copy);
				hv.calculate();
				currentHv = totalHv - hv.getResult();

				// Update the minimum HV contribution
				if(currentHv < minHvContribution){
					minHvContribution = currentHv;
					indexToRemove = i;
				}

				// Add the individual in its previous position
				copy.add(i,ind);
			}
		}
		else{
			indexToRemove = 0;
		}

		return indexToRemove;
	}
}