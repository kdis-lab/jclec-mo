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
import net.sf.jclec.mo.IMOEvaluator;
import net.sf.jclec.mo.command.ObjectiveInverter;
import net.sf.jclec.mo.command.ObjectiveScalerNoBounds;
import net.sf.jclec.mo.command.PopulationSplitter;
import net.sf.jclec.mo.comparator.MOSolutionComparator;
import net.sf.jclec.mo.comparator.fcomparator.ParetoComparator;
import net.sf.jclec.mo.evaluation.Objective;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;
import net.sf.jclec.selector.RandomSelector;

/**
 * PAR strategy.
 *  
 * <p>The <b>P</b>reference-based <b>A</b>daptive <b>R</b>egion of interest</p> (PAR)
 * strategy is a technique that can be included in any MOEA to create preference-based
 * algorithms.
 * 
 * <p>This strategy defines preferences as a reference point that will be used to
 * adaptively delimit the region of interest (ROI) and bias the search towards it.
 * The strategy was originally proposed for minimization problems. However, this
 * implementation can also be applied to maximization problems, as inversion is
 * automatically done. Standardization is also recommended and performed using
 * the limits of the current solutions within the population.</p>
 * 
 * <p><i>Paper</i>: F. Goulart, F. Campelo. 
 * “Preference-guided evolutionary algorithms for many-objective optimization”, 
 * Information Sciences, vol. 329, pp. 236-255. 2015.</p>
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

public class PAR extends MOStrategy {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -9211181170008933381L;

	/** The reference point that expresses user's preference */
	protected double [] refPoint;

	/** Rho parameter */
	protected double rho;

	/** A parent selector */
	protected ISelector selector;

	/** A command to scale objective values */
	protected ObjectiveScalerNoBounds scaler;

	/** A command to split the population */
	protected PopulationSplitter splitter;

	/** A command to invert objective values */
	protected ObjectiveInverter inverter;

	/** A random selector */
	protected RandomSelector rSelector;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public PAR() {
		super();
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Get the value of Rho parameter
	 * @param Rho parameter value
	 * */
	public double getRho() {
		return rho;
	}

	/**
	 * Set the value of Rho parameter
	 * @param rho New value
	 * */
	protected void setRho(double rho) {
		this.rho = rho;
	}

	/**
	 * Get the reference point
	 * @return Reference point
	 * */
	public double [] getRefPoint() {
		return this.refPoint;
	}

	/**
	 * Set the reference point
	 * @param point Reference point
	 * */
	protected void setRefPoint(double [] point) {
		this.refPoint = point;
	}

	/**
	 * Get the selector
	 * @return Parent selector
	 * */
	public ISelector getSelector() {
		return this.getSelector();
	}

	/**
	 * Set the selector
	 * @param selector Parent selector
	 * */
	protected void setSelector(ISelector selector) {
		this.selector = selector;
		this.selector.contextualize(getContext());
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////


	/**
	 * {@inheritDoc}
	 * <p>RVEA has the following parameters:
	 * <ul>
	 * 	<li>rho (<code>double</code>): <p>A value greater than 0 used to compute the ASF function. Default value is 10E-6.</p></li>
	 * 	<li>refPoint (<code>Complex</code>): <p>The reference point. The value for each objective function should be enclosed 
	 * 	with "objx" where x is the position of the objective (starting from 1).</p></li>
	 * 	<li>parents-selector (<code>Complex</code>): <p>A selector operator to choose parents.</p></li>
	 * </ul>
	 * </p>
	 * */
	@Override
	public void configure(Configuration settings) {
		super.configure(settings);

		// Configure rho
		double rho = settings.getDouble("rho",0.0000001);
		if(rho <= 0) {
			rho = 0.0000001;
			throw new IllegalArgumentException("Rho should be greater than 0. Default value (10E-6) will be used.");
		}
		setRho(rho);

		// Configure reference point
		List<Objective> objectives = ((IMOEvaluator)this.getContext().getEvaluator()).getObjectives();
		int nObjs = objectives.size();
		double [] refPoint = new double[nObjs];
		double value;
		Objective obj;
		for(int i=0; i<nObjs; i++) {
			value = settings.getDouble("refPoint.obj"+(i+1));
			obj = objectives.get(i);
			if(value>= obj.getMinimum() && value <= obj.getMaximum()) {
				refPoint[i] = value;
			}
			else {
				if(obj.isMaximized())
					refPoint[i] = obj.getMaximum();
				else
					refPoint[i] = obj.getMinimum();
				throw new IllegalArgumentException("Value of the reference point for objective " + (i+1) + " is not valid. Optimal objective value will be used instead.");
			}
		}
		setRefPoint(refPoint);

		// Configure selector
		configureSelectorSettings(settings);
	}

	/**
	 * {@inheritDoc}
	 * <p>PAR does not define the use of an external archive, so null is returned</p>
	 * */
	@Override
	public List<IIndividual> initialize(List<IIndividual> population) {
		//Initialize auxiliary commands
		this.scaler = new ObjectiveScalerNoBounds();
		this.splitter = new PopulationSplitter();
		this.splitter.setComparator(getSolutionComparator());
		this.inverter = new ObjectiveInverter();
		this.rSelector = new RandomSelector(getContext());
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
	 * <p>By default, PAR will use Pareto dominance.</p>
	 * */
	@Override
	public void createSolutionComparator(Comparator<IFitness>[] components) {
		// Fitness comparator
		ParetoComparator fcomparator = new ParetoComparator(components);
		// Solution comparator
		MOSolutionComparator comparator = new MOSolutionComparator(fcomparator);
		setSolutionComparator(comparator);
	}

	/**
	 * {@inheritDoc}
	 * <p>As no specific method is described in the paper, this implementation will
	 * rely on a selector specified by the user in the configuration file.</p>
	 * */
	@Override
	public List<IIndividual> matingSelection(List<IIndividual> population, List<IIndividual> archive) {
		List<IIndividual> parents = this.selector.select(population, getContext().getPopulationSize());
		return parents;
	}

	/**
	 * {@inheritDoc}
	 * <p>In PAR, ASF values are computed to find the solution closer to the reference point and create
	 * perturbed points around it. Those solutions that are better that these points comprise the ROI.
	 * The ROI is augmented or reduced until the number of expected survivors (population size) is reached.</p>
	 * */
	@Override
	public List<IIndividual> environmentalSelection(List<IIndividual> population, List<IIndividual> offspring,
			List<IIndividual> archive) {

		// Joint both populations and scale objective values
		List<IIndividual> allSolutions = preparePopulation(population, offspring);

		// Assign ASF values to population and offspring
		fitnessAssignment(allSolutions, null);

		// Find the solution with minimum ASF with respect to the reference point
		int index = findIndexSolutionMinimumASF(allSolutions);
		IIndividual minSolution = allSolutions.get(index);

		// Generate perturbed reference points that will be the new ROI
		List<double []> auxPoints = generatePerturbedReferencePoints(minSolution);

		// Find the objective value of the solution closer to each perturbed reference point
		double [] closerObjValues = findCloserObjectiveValue(allSolutions, auxPoints);

		// Find the solutions belonging to the ROI
		List<IIndividual> survivors = findSolutionROI(population, offspring, allSolutions, closerObjValues);

		// Select survivors from the ROI and an additional criteria
		int popSize = getContext().getPopulationSize();
		int roiSize = survivors.size();
		if(roiSize > popSize) {
			reduceROI(survivors, popSize);
		}
		else if (roiSize < popSize) {
			augmentROI(survivors, population, offspring, allSolutions, (popSize-roiSize));
		}

		return survivors;
	}

	/**
	 * {@inheritDoc}
	 * <p>PAS does not use archive, so <code>null</code> is always returned.</p>
	 * */
	@Override
	public List<IIndividual> updateArchive(List<IIndividual> population, List<IIndividual> offspring,
			List<IIndividual> archive) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * <p>This method is used to compute the ASF value for each solution.</p>
	 * */
	@Override
	protected void fitnessAssignment(List<IIndividual> population, List<IIndividual> archive) {
		// Compute ASF for each solution in the population using the user's reference point
		int popSize = population.size();
		double asf;
		for(int i=0; i<popSize; i++) {
			asf = computeASF(population.get(i), this.refPoint);
			((MOFitness)population.get(i).getFitness()).setValue(asf);
		}
	}

	/////////////////////////////////////////////////////////////////
	// -------------------------------------------- Protected methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Configure a parent selector.
	 * @param settings New parent selector.
	 * */
	@SuppressWarnings("unchecked")
	protected void configureSelectorSettings(Configuration settings) {
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
	 * Prepare the intermediate population for environmental selection. It includes
	 * inversion of objective values if the problem should be maximized and standardization
	 * of objective values using current values in the the population. This method could
	 * be override to perform scalarization in a different way, as suggested in the paper.
	 * @param population Current population
	 * @param offspring Current offspring
	 * @return A joint set of solutions properly standardized.
	 * */
	protected List<IIndividual> preparePopulation(List<IIndividual> population, List<IIndividual> offspring){
		List<IIndividual> allSolutions = new ArrayList<IIndividual>();
		for(IIndividual s: population)
			allSolutions.add(s.copy());
		for(IIndividual s: offspring)
			allSolutions.add(s.copy());

		// Invert values if the problem should be maximized
		if(this.isMaximized()) {
			this.inverter.setPopulation(allSolutions);
			this.inverter.execute();
		}

		this.scaler.setPopulation(allSolutions);
		this.scaler.execute();

		return allSolutions;
	}

	/**
	 * Compute the Achievement Scalarization Function
	 * @param solution The solution for which ASF will be computed.
	 * @param refPoint The reference point to be used.
	 * @return ASF value
	 * */
	protected double computeASF(IIndividual solution, double[] refPoint) {
		int nObjs = ((IMOEvaluator)getContext().getEvaluator()).numberOfObjectives();
		double asf=0, dif, accDif=0, maxDif = Double.NEGATIVE_INFINITY;
		MOFitness fitness = (MOFitness)solution.getFitness();

		// Compute the differences between the objective value and the reference point
		// and find the maximum difference
		for(int i=0; i<nObjs; i++) {
			try {
				dif = fitness.getObjectiveDoubleValue(i) - refPoint[i];
				accDif += dif;
				if(dif>maxDif) {
					maxDif = dif;
				}
			} catch (IllegalAccessException | IllegalArgumentException e) {
				e.printStackTrace();
			}
		}

		// Compute the augmented Chebyshev norm
		asf = maxDif + this.rho*accDif;
		return asf;
	}

	/**
	 * Find the solution with the minimum ASF (stored as fitness value).
	 * @param solutions Set of solutions.
	 * @return Solution with minimum ASF.
	 * */
	protected int findIndexSolutionMinimumASF(List<IIndividual> solutions) {
		int index=-1, size = solutions.size();
		double asf, minAsf = Double.POSITIVE_INFINITY;
		for(int i=0; i<size; i++) {
			asf = ((MOFitness)solutions.get(i).getFitness()).getValue();
			if (asf < minAsf) {
				minAsf = asf;
				index = i;
			}
		}
		return index;
	}

	/**
	 * Generate one perturbed reference point for each objective direction.
	 * @param solution Solution closest to the original reference point (s_min).
	 * @return List of perturbed reference points. 
	 * */
	protected List<double []> generatePerturbedReferencePoints(IIndividual solution){
		int nObjs = ((IMOEvaluator)getContext().getEvaluator()).numberOfObjectives();
		List<double []> perturbedRefPoints = new ArrayList<double []>();
		double [] point;
		MOFitness fitness = (MOFitness)solution.getFitness();
		for(int i=0; i<nObjs; i++) {
			// Copy the original reference point
			point = this.refPoint.clone();
			try {
				// Perturb the current direction with the value of the min solution
				point[i] = point[i] + fitness.getObjectiveDoubleValue(i);
				perturbedRefPoints.add(point);
			} catch (IllegalAccessException | IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		return perturbedRefPoints;
	}

	/**
	 * Find the closer solution to each reference point (one per objective)
	 * and save its objective value for the corresponding objective.
	 * @param solutions Set of solutions
	 * @param refPoints Set of reference points
	 * @return A double array with the objective value of the closer solution to each point.
	 * */
	protected double [] findCloserObjectiveValue(List<IIndividual> solutions, List<double []> refPoints){
		int nObjs = refPoints.size();
		int size = solutions.size();
		double [] closerObjValues = new double[nObjs];
		double asf, minAsf;

		// For each reference point, compute ASF for each solution and find the minimum value
		try {
			for(int i=0; i<nObjs; i++) {
				minAsf = Double.POSITIVE_INFINITY;
				for(int j=0; j<size; j++) {
					asf = computeASF(solutions.get(j), refPoints.get(i));
					if(asf < minAsf) {
						minAsf = asf;
						// Save the objective value of the closer solution
						closerObjValues[i] = ((MOFitness)solutions.get(j).getFitness()).getObjectiveDoubleValue(i);
					}
				}
			}
		} catch (IllegalAccessException | IllegalArgumentException e) {
			e.printStackTrace();
		}
		return closerObjValues;
	}

	/**
	 * Find the solutions that belong to the ROI, i.e. solutions whose scaled
	 * objective values are always smaller or equals to the given best values.
	 * The solutions returned are the original solutions (without scaled values)
	 * from the population or the offspring. This method assumes that the scaled
	 * solution list first contains the modified population and then the modified offspring.
	 * The different lists will be modified after this method to remove already
	 * selected solutions.
	 * @param population Original population (without scaled values)
	 * @param offspring Original offspring (without scaled values)
	 * @param scaledSolutions Population+offspring with scaled values
	 * @param closerObjValues Best objective value for each objective
	 * @return List of original solutions (either population member or offspring) belonging to the ROI.
	 * */
	protected List<IIndividual> findSolutionROI(List<IIndividual> population, List<IIndividual> offspring, 
			List<IIndividual> scaledSolutions, double [] closerObjValues){
		List<IIndividual> roi = new ArrayList<IIndividual>();

		int popSize = population.size();
		int size = scaledSolutions.size();
		int numObjs = closerObjValues.length;
		boolean select;
		double value;
		MOFitness fitness;
		int i=0;
		while(i<size){

			// First, check if the solution satisfies the condition to be selected
			select = true;
			fitness = (MOFitness)scaledSolutions.get(i).getFitness();
			for(int j=0; select && j<numObjs; j++) {
				try {
					value = fitness.getObjectiveDoubleValue(j);
					// Every objective value should be smaller, otherwise, do not select
					if(value > closerObjValues[j]) {
						select = false;
					}
				} catch (IllegalAccessException | IllegalArgumentException e) {
					e.printStackTrace();
				}
			}

			// If the solution should be selected, find if it belongs to the population or offspring set
			// Also, remove from both lists so that is won't be selected anymore
			if(select) {
				scaledSolutions.remove(i);
				size--;
				if(i<popSize) { // The solution is a population member
					roi.add(population.get(i).copy());
					population.remove(i);
					popSize--; // update the barrier between population and offspring
				}
				else { // The solution is an offspring
					roi.add(offspring.get(i-popSize).copy());
					offspring.remove(i-popSize);
				}
			}
			else {
				i++;
			}
		}
		return roi;
	}

	/**
	 * This method used a non-dominated sorting procedure to choose
	 * the solutions to be kept. Solutions in the critical front
	 * are chosen at random. This method could be override to
	 * use another comparison criterion, e.g. indicator-based approach
	 * as also described in the paper.
	 * @param roi Solutions in the current ROI
	 * @param expectedSize The expected size of the ROI
	 * */
	protected void reduceROI(List<IIndividual> roi, int expectedSize) {
		this.splitter.setPopulation(roi);
		this.splitter.execute();
		List<List<IIndividual>> popByFronts = this.splitter.getSplitPopulation();
		int index = 0;
		roi.clear();
		while((roi.size() + popByFronts.get(index).size()) <= expectedSize) {
			roi.addAll(popByFronts.get(index));
			index++;
		}
		int numToSelect = expectedSize - roi.size();
		if(numToSelect>0) {
			roi.addAll(this.rSelector.select(popByFronts.get(index), numToSelect));
		}
	}

	/**
	 * Fill the ROI with the solutions having the minimum ASF from the
	 * remaining ones.
	 *
	 * The solutions added to the ROI are the original solutions (without scaled values)
	 * from the population or the offspring. This method assumes that the scaled
	 * solution list first contains the modified population and then the modified offspring.
	 * The different lists will be modified after this method to remove already
	 * selected solutions.
	 * 
	 * @param roi Solutions in the current ROI.
	 * @param population Original population (without scaled objectives).
	 * @param offspring Original offspring (without scaled objectives).
	 * @param scaledSolutions Population+offspring with scaled values.
	 * @param numToAdd Number of solutions to be added.
	 * */
	protected void augmentROI(List<IIndividual> roi, List<IIndividual> population, 
			List<IIndividual> offspring, List<IIndividual> scaledSolutions, int numToAdd) {
		int popSize = population.size();
		for(int i=0; i<numToAdd; i++) {
			int index = findIndexSolutionMinimumASF(scaledSolutions);
			scaledSolutions.remove(index);
			if(index<popSize) {
				roi.add(population.get(index).copy());
				population.remove(index);
				popSize--;
			}
			else {
				roi.add(offspring.get(index-popSize).copy());
				offspring.remove(index-popSize);
			}
		}
	}
}