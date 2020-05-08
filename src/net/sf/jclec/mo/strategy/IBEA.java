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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.configuration.Configuration;

import net.sf.jclec.IFitness;
import net.sf.jclec.IIndividual;
import net.sf.jclec.mo.IMOEvaluator;
import net.sf.jclec.mo.command.MaxObjectiveValue;
import net.sf.jclec.mo.command.MinObjectiveValue;
import net.sf.jclec.mo.command.ObjectiveInverter;
import net.sf.jclec.mo.comparator.MOSolutionComparator;
import net.sf.jclec.mo.comparator.fcomparator.MOValueFitnessComparator;
import net.sf.jclec.mo.comparator.fcomparator.ParetoComparator;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;
import net.sf.jclec.util.random.IRandGen;

/**
 * IBEA strategy
 * 
 * <p>The <b>I</b>ndicator <b>B</b>ased <b>E</b>volutionary 
 * <b>A</b>lgorithm (IBEA) is a general multiobjective algorithm
 * that allows the user to set the desired quality indicator that
 * will guide the search. More specifically, the indicator is used
 * as a fitness value to perform the mating and environmental selection.
 * <p>Since many indicator could be considered, this class is defined
 * as an abstract class, so different specialization of this class
 * can be define with different quality indicators.</p>
 *  
 * <p>Originally, IBEA is defined for minimization problems. Nevertheless,
 * this implementation supports both minimization and maximization problem 
 * and also different scales in the objective functions. The unique precondition 
 * is that all objectives should be maximized or minimized, combining both types 
 * of objectives is not allowed. Inversion of objective values for maximization 
 * problems is already performed by the abstract class before invoking the fitness
 * assignment method. Objective values might be scaled depending on the specific
 * indicator, so the subclasses are in charge of scaling the objective values.</p>
 *  
 * <p><i>Paper</i>: E. Ziztler, S. Kunzli, 
 * “Indicator-Based Selection in Multiobjective Search” 
 * Parallel Problem Solving from Nature (PPSN VIII), 
 * pp. 832-842, 2004.</p>
 * 
 * <p><i>Adapted from the original implementation</i>: 
 * http://www.tik.ee.ethz.ch/pisa/selectors/ibea/?page=ibea.php
 * </p>
 * 
 * <p>HISTORY:
 * <ul>
 *  <li>(AR|JRR|SV, 1.0, March 2018)		First release.</li>
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
 */
public abstract class IBEA extends MOStrategy {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -7682811820718819200L;

	/** k parameter */
	protected double k;

	/** Minimum value for each objective in the current population */
	protected double [] minBounds;

	/** Maximum value for each objective in the current population */
	protected double [] maxBounds;

	/** Indicator value for each pair of individuals in the current population */
	protected double [][] indicatorValues;

	/** Maximum indicator value in the current population */
	protected double maxAbsValue;

	/** Command to calculate minimum objective values */
	protected MinObjectiveValue commandMinValues;

	/** Command to calculate maximum objective values */
	protected MaxObjectiveValue commandMaxValues;

	/** Command to invert objective values */
	protected ObjectiveInverter commandInvert;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public IBEA(){
		super();
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Get the k parameter
	 * @return k value
	 * */
	public double getKValue(){
		return k;
	}

	/**
	 * Set the k parameter
	 * @param k New k value
	 * */
	protected void setKValue(double k){
		this.k=k;
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>IBEA configures the tournament selector
	 * and compute the fitness for the initial generation
	 * (required for mating selection).
	 * */
	@Override
	public List<IIndividual> initialize(List<IIndividual> population) {

		// Initialize auxiliary commands
		this.commandMaxValues = new MaxObjectiveValue();
		this.commandMinValues = new MinObjectiveValue();
		this.commandInvert = new ObjectiveInverter();
		
		// Initialize objective bounds
		int nObjectives = ((IMOEvaluator)getContext().getEvaluator()).numberOfObjectives();
		this.minBounds = new double [nObjectives];
		this.maxBounds = new double [nObjectives];

		// Assign fitness for the initial population, since
		// it is required for mating selection in the first generation
		this.maxAbsValue = 0.0;
		fitnessAssignment(population, null);

		// IBEA does not use the archive
		return null;
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void update() {
		// clear variables
		this.maxAbsValue = 0.0;
		this.indicatorValues = null;
	}

	/**
	 * {@inheritDoc}
	 * <p>Parameters for IBEA are:
	 * <ul>	
	 * <li>k (<code>integer</code>):<p>fitness k parameter</p></li>
	 * </ul>
	 * </p>
	 * */
	@Override
	public void configure(Configuration settings){

		// Call super configuration
		super.configure(settings);

		double k = settings.getDouble("k");
		setKValue(k);

		// IBEA needs that all objectives to be maximized or minimized
		if(isMaximized()==null){
			System.err.println("All objectives should be minimized or maximized");
			System.exit(-1);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>IBEA uses a classical pareto comparator.</p>
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
	 * <p>IBEA performs a binary tournament
	 * based on the fitness value obtained
	 * from the corresponding indicator.</p>
	 * */
	@Override
	public List<IIndividual> matingSelection(List<IIndividual> population, List<IIndividual> archive) {

		List<IIndividual> parents = new ArrayList<IIndividual>();

		// Tournament selection based on the 
		// indicator-based fitness values
		int size = population.size();
		IIndividual candidate0, candidate1;

		IRandGen randgen = getContext().createRandGen();
		// Fitness should be maximized for both minimization and maximization problems
		MOValueFitnessComparator fcomparator = new MOValueFitnessComparator(false);

		for(int i=0; i<size; i++){
			int index0 = randgen.choose(0, size);
			int index1 = randgen.choose(0, size);
			candidate0 = population.get(index0);
			candidate1 = population.get(index1);

			// The opponent wins
			switch(fcomparator.compare(candidate0.getFitness(), candidate1.getFitness())){

			case 1:
				parents.add(candidate0.copy());
				break;
			case -1:
				parents.add(candidate1.copy());
				break;
			case 0:
				if(randgen.coin())
					parents.add(candidate0);
				else
					parents.add(candidate1);
			}
		}

		return parents;
	}

	/**
	 * {@inheritDoc}
	 * <p>IBEA iteratively remove individuals
	 * with worser fitness values. The fitness
	 * value of the remaining individuals will
	 * be updated to next generations.</p>
	 * */
	@Override
	public List<IIndividual> environmentalSelection(List<IIndividual> population, 
			List<IIndividual> offspring, List<IIndividual> archive) {

		// Join populations
		List<IIndividual> survivors = new ArrayList<IIndividual>();
		survivors.addAll(population);
		for(IIndividual ind: offspring){
			if(!survivors.contains(ind)){
				survivors.add(ind);
			}
		}

		// Set fitness values
		fitnessAssignment(survivors, null);

		// Select survivors 
		int popSize = getContext().getPopulationSize();
		int size = survivors.size();
		int nRemovals = size - popSize;
		int n = 0;
		double worstValue = 0.0, fvalue;
		int worstIndex = 0;
		IIndividual ind;

		if(maxAbsValue==0.0)
			this.maxAbsValue = 0.0000000000000000001;

		// Iteratively removes the worst individual
		// and update fitness values
		while(n<nRemovals){

			// Update worst value, get the first value
			worstValue = ((MOFitness)survivors.get(0).getFitness()).getValue();
			worstIndex = 0;

			// Search the current worst individual
			for(int i=1; i<size; i++){
				ind = survivors.get(i);
				fvalue = ((MOFitness)ind.getFitness()).getValue();
				if(fvalue<worstValue){
					worstValue = fvalue;
					worstIndex = i;
				}
			}

			// Calculate the fitness value of the individuals
			// if the worst individual is removed
			for(int i=0; i<size; i++){
				ind = survivors.get(i);
				fvalue = ((MOFitness)ind.getFitness()).getValue() 
						- Math.exp(((-1.0*indicatorValues[worstIndex][i])/maxAbsValue)/k);
				((MOFitness)ind.getFitness()).setValue(fvalue);
			}

			// The worst individual is removed
			survivors.remove(worstIndex);
			size--;
			n++;
		}

		// Return the survivors
		return survivors;
	}

	/**
	 * {@inheritDoc}
	 * <p>IBEA does not uses the archive.</p>
	 * */
	@Override
	public List<IIndividual> updateArchive(List<IIndividual> population, 
			List<IIndividual> offspring, List<IIndividual> archive) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * <p>IBEA assign to each individual the sum of the fitness components 
	 * (one per each individual in the population) based on the used indicator.</p>
	 * */
	@Override
	protected void fitnessAssignment(List<IIndividual> population, List<IIndividual> archive) {

		// Invert objective values for maximization problem
		if(isMaximized()){
			this.commandInvert.setPopulation(population);
			this.commandInvert.execute();
		}

		// Compute objective bounds and fitness components
		computeBounds(population);
		double [][] fitnessComponents = computeFitnessComponents(population);

		int size = population.size();

		// Set the overall fitness value of each individual
		double fvalue;
		for(int i=0; i<size; i++){
			fvalue = 0.0;
			for(int j=0; j<size; j++){
				if(i!=j){
					fvalue += fitnessComponents[i][j];
				}
			}
			((MOFitness)population.get(i).getFitness()).setValue(fvalue);
		}

		// Revert the inversion of objective values
		if(isMaximized()){
			this.commandInvert.setPopulation(population);
			this.commandInvert.execute();
		}
	}

	/**
	 * Compute the minimum and maximum
	 * value for each objective in the
	 * current population.
	 * @param population The current population.
	 * */
	protected void computeBounds(List<IIndividual> population){
		
		// Minimum and maximum value for each objective in the current population
		int size = this.maxBounds.length;
		this.commandMinValues.setPopulation(population);
		this.commandMaxValues.setPopulation(population);

		for(int i=0; i<size; i++){

			// Minimum value
			this.commandMinValues.setObjectiveIndex(i);
			this.commandMinValues.execute();
			this.minBounds[i] = this.commandMinValues.getMinValue();

			// Maximum value
			this.commandMaxValues.setObjectiveIndex(i);
			this.commandMaxValues.execute();
			this.maxBounds[i] = this.commandMaxValues.getMaxValue();
		}
	}

	/**
	 * Compute the fitness components for
	 * the given population.
	 * @param population The list of M individuals.
	 * @return A matrix MxM with the fitness component value
	 * for each pair of individuals.
	 * */
	protected double [][] computeFitnessComponents(List<IIndividual> population){
		int size = population.size();
		double [][] fitnessComponents = new double[size][size];
		this.indicatorValues = new double[size][size];
		this.maxAbsValue = -1.0;
		double absValue;

		// Compute the indicator for each pair of individuals
		for(int i=0; i<size; i++){
			for(int j=0; j<size; j++){
				this.indicatorValues[i][j] = computeIndicator(population.get(i), population.get(j));
				absValue = Math.abs(this.indicatorValues[i][j]);
				// Update the maximum value
				if(this.maxAbsValue < absValue){
					this.maxAbsValue = absValue;
				}
			}
		}

		// Compute the fitness components
		double denominator = this.maxAbsValue/ this.k;
		if(isMaximized())
			for(int i=0; i<size; i++){
				for(int j=0; j<size; j++){
					fitnessComponents[i][j] = Math.exp((-1.0*this.indicatorValues[i][j])/denominator);
				}
			}
		else	
			for(int i=0; i<size; i++){
				for(int j=0; j<size; j++){
					fitnessComponents[i][j] = Math.exp((-1.0*this.indicatorValues[i][j])/denominator);
				}
			}
		return fitnessComponents;
	}

	/**
	 * Compute the quality indicator that will be used to assign the fitness values.
	 * @param ind0 First individual.
	 * @param ind1 Second individual.
	 * @return The corresponding fitness component value between <code>ind0</code> 
	 * and <code>ind1</code> for the specific indicator.
	 * */
	protected abstract double computeIndicator(IIndividual ind0, IIndividual ind1);
}
