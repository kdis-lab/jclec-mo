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
import net.sf.jclec.fitness.SimpleValueFitness;
import net.sf.jclec.fitness.ValueFitnessComparator;
import net.sf.jclec.mo.IMOEvaluator;
import net.sf.jclec.mo.command.NonDominatedSolutionsExtractor;
import net.sf.jclec.mo.comparator.MOSolutionComparator;
import net.sf.jclec.mo.comparator.fcomparator.EpsilonDominanceComparator;
import net.sf.jclec.mo.comparator.fcomparator.ParetoComparator;
import net.sf.jclec.mo.distance.EuclideanHypercubeDistance;
import net.sf.jclec.mo.evaluation.Objective;
import net.sf.jclec.mo.evaluation.fitness.IHypercubeMOFitness;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;
import net.sf.jclec.mo.strategy.util.Hypercube;
import net.sf.jclec.util.random.IRandGen;

/**
 * eMOEA strategy
 * 
 * <p>The Steady State <b>e</b>psilon <b>M</b>ulti <b>O</b>bjective
 * <b>E</b>volutionary <b>A</b>lgorithm is a steady state algorithm that 
 * adapts the concept of Pareto dominance. It divides the objective space 
 * into hypercubes, so individuals are compared by means of the e-dominance relation,
 * over the resulting partitioned landscape. Formally, the solution <i>a</i> 
 * e-dominates the solution <i>b</i> if <i>a</i> belongs to better or equal 
 * hypercubes and, at least, to a better hypercube than <i>b</i>. 
 * As detailed in the original paper, the length of each hypercube has to be configured 
 * by the user. This implementation also provides an optional automatic configuration of the
 * lengths considering the desired number of hypercubes and the bounds of each
 * objective function.</p>
 * 
 * <p><i>Paper</i>: K. Deb, M. Mohan, and S. Mishra, “Towards a Quick
 * Computation of Well-Spread Pareto-Optimal Solutions”, 
 * in Evolutionary Multi-Criterion Optimization, vol. 2632 of LNCS, 
 * pp. 222–236, Springer, 2003.</p>
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
 * */
public class SSeMOEA extends MOStrategy {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -658742954819516472L;

	/** Epsilon values: the lengths for each objective */
	protected double [] epsilon;

	/** Number of hypercubes (adaptive version) */
	private int nHypercubes;
	
	/** Hypercube comparator */
	protected EpsilonDominanceComparator hComparator;
	
	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public SSeMOEA(){
		super();
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////

	/** 
	 * Get the array of epsilon values.
	 * @return epsilon values.
	 */
	public double[] getEpsilonValues(){
		return this.epsilon;
	}

	/**
	 * Set the array of epsilon values.
	 * @param epsilon The values that have to be set.
	 */
	protected void setEpsilonValues(double [] epsilon){
		this.epsilon = epsilon;
	}
	
	/**
	 * Get the number of hypercubes.
	 * @return Number of hypercubes.
	 * */
	protected int getNumberOfHypercubes(){
		return this.nHypercubes;
	}
	
	/**
	 * Set the number of hypercubes.
	 * @param nHypercubes The number of hypercubes.
	 */
	protected void setNumberOfHypercubes(int nHypercubes){
		this.nHypercubes = nHypercubes;
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>Parameters for SSeMOEA are:
	 * <ul>
	 * <li>epsilon-value (<code>List</code>): 
	 * <p>List of epsilon values to define the landscape partition
	 * (one for each objective function configure). If only
	 * one value is set, then it will be applied to all the objectives.</p></li>
	 * 
	 * <li>number-of-hypercubes (<code>integer</code>): 
	 * <p>Number of hypercubes.
	 * Default value is 10. This element is only used if the list of
	 * epsilon values is not provided.</p></li>
	 * </ul>
	 * </p>
	 * */
	@Override
	public void configure(Configuration settings){

		// Call super configuration
		super.configure(settings);
		
		// Set epsilon values
		List<Object> values = settings.subset("epsilon-values").getList("epsilon-value");
		int size = values.size();
		double [] epsilon = null;
		double value;
		
		// Check if the number of values is the same than the number of objectives
		int nObj = ((IMOEvaluator)getContext().getEvaluator()).numberOfObjectives();
		if(nObj==size){
			epsilon = new double[size];
			for(int i=0; i<size; i++){
				epsilon[i]= Double.valueOf(values.get(i).toString());
			}
			setEpsilonValues(epsilon);
		}
		
		// Only one value, set it for all the objectives
		else if(size==1){
			epsilon = new double[nObj];
			value = Double.valueOf(values.get(0).toString());
			for(int i=0; i<nObj; i++){
				epsilon[i]=value;
			}
			setEpsilonValues(epsilon);
		}
		
		// Try to get the number of hypercubes
		else{
			this.nHypercubes = settings.getInt("number-of-hypercubes",10);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p> eMOEA requires the next initialization steps:
	 * <ol>
	 * 	<li>Create the initial archive that will contain
	 * the non dominated solutions (e-dominance is checked) 
	 * within the initial population.</li>
	 * 	<li>Compute the hypercubes of the individuals
	 * in the initial archive.</li>
	 * </ol>
	 * </p>
	 * */
	@Override
	public List<IIndividual> initialize(List<IIndividual> population) {
		
		// Set the epsilon values if required
		if(this.epsilon==null & this.nHypercubes>0)
			setEpsilonValues();
		
		// Create the comparator of hypercubes
		createHypercubeComparator();
		
		// Create the first external population with non-dominated solution
		NonDominatedSolutionsExtractor command = new NonDominatedSolutionsExtractor();
		command.setComparator(new ParetoComparator(this.hComparator.getComponentComparators()));
		command.setPopulation(population);
		command.execute();
		List<IIndividual> archive = command.getNonDominatedSolutions();
		
		// Calculate the hypercubes for the archive members
		for(IIndividual ind: archive){
			((IHypercubeMOFitness)ind.getFitness()).setHypercube(computeHypercube(ind));
		}	
		
		// Return the first archive
		return archive;
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void update() {
		// Do nothing
	}
	
	/**
	 * {@inheritDoc}
	 * <p>SSeMOEA requires a Pareto comparator.</p>
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
	 * <p>In eMOEA, this method performs the selection of two parents:
	 * <ul>
	 * <li>Parent 1: One solution is chosen from the current population.
	 * Two individuals are randomly selected and the dominance between
	 * them is checked. The non dominated individual is selected. If
	 * both are non dominated solutions, one of them is randomly selected.</li>
	 * <li>Parent 2: One solution is randomly chosen from the archive.</li>
	 * </ul>
	 * </p>
	 * <p>If the archive is empty, then the two parents are
	 * chosen from the current population.</p>
	 * */
	@Override
	public List<IIndividual> matingSelection(List<IIndividual> population, List<IIndividual> archive) {
		
		List<IIndividual> parents = new ArrayList<IIndividual>(2);
		IIndividual ind1, ind2;
		int archiveSize = archive.size();
		MOSolutionComparator comparator = super.getSolutionComparator();
		IRandGen randgen = getContext().createRandGen();
		
		// Select two candidates from the current population
		List<IIndividual> candidates = new ArrayList<>(2);
		int index1 = randgen.choose(0, population.size());
		candidates.add(population.get(index1));
		int index2 = randgen.choose(0, population.size());
		candidates.add(population.get(index2));
		
		// The archive is not empty
		if(archiveSize>0){
			
			ind1 = candidates.get(0);
			ind2 = candidates.get(1);
			
			// Check pareto dominance to select the first parent
			switch(comparator.compare(ind1, ind2))
			{
			// Ind0 dominates Ind1
			case 1:
				parents.add(ind1);
				break;
				// Ind1 dominates Ind0
			case -1:
				parents.add(ind2);
				break;
				// Individuals are non dominated, choose one randomly
			case 0:
				if(randgen.coin(0.5))
					parents.add(ind1);
				else
					parents.add(ind2);
				break;
			}

			// Select second parent (different from the first one)
			// from the external population
			ind1 = archive.get(randgen.choose(0,archive.size()));
			parents.add(ind1);
		}
		
		// The archive is empty, add both parents from the current population
		else
			parents.addAll(candidates);
		return parents;
	}

	/**
	 * {@inheritDoc}
	 * <p>In eMOEA, each offspring replaces a dominated individual in the current
	 * population. If the offspring does not dominate any individual, one
	 * individual randomly chosen is removed.</p>
	 * */
	@Override
	public List<IIndividual> environmentalSelection(List<IIndividual> population, 
			List<IIndividual> offspring, List<IIndividual> archive) {
		
		// Copy current population
		List<IIndividual> survivors = new ArrayList<IIndividual>();
		survivors.addAll(population);

		MOSolutionComparator comparator = super.getSolutionComparator();
		int i;
		boolean finish;
		int nonDominated;
		IRandGen randgen = getContext().createRandGen();
		
		// For each offspring
		for(IIndividual offspringInd: offspring){
			i=0;
			finish=false;
			nonDominated=0;

			while(!finish && i<survivors.size()){
				IIndividual ind = survivors.get(i);

				// If the offspring dominates one individual in 
				// the current population, replace it
				switch(comparator.compare(offspringInd, ind)){
				case 1:
					survivors.remove(i);
					finish=true;
					break;
				case -1:
					i++;
					break;
				case 0:
					nonDominated++;
					i++;
					break;
				}
			}
			// The offspring dominates any individual
			if(finish){
				survivors.add(offspringInd.copy());
			}
			// The offspring is non dominated by any individual in the population
			// Replace one randomly chosen member
			else if(nonDominated==survivors.size()){
				int index = randgen.choose(0, survivors.size());
				survivors.remove(index);
				survivors.add(offspringInd.copy());
			}
		}
		return survivors;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * Before adding new solutions to the archive, the hypercubes of each 
	 * individual within the current archive and the set of offspring are computed. 
	 * Using these hypercubes, three situations can occur:
	 * <ul>
	 * <li>If the offspring is e-dominated by any individual within the archive, 
	 * the offspring is not added.</li>
	 * <li>If the offspring e-dominates some individuals within the archive, 
	 * the dominated individuals are removed and the offspring is added.</li>
	 * <li>If neither of the above two cases occur:</li>
	 * <ul>
	 * <li>If the offspring belongs to the same hypercube than 
	 * an archive member, Pareto dominance is checked. 
	 * If the offspring dominates the other, it is added to 
	 * the external set and the other is removed. If both are 
	 * non dominated, the offspring is added if it has the closer euclidean 
	 * distance to the ideal point in the hypercube, and the other is removed.</li>
	 * <li>It the offspring belongs to a different hypercube set, it is accepted.</li>
	 * </ul>
	 * </p>
	 * */
	@Override
	public List<IIndividual> updateArchive(List<IIndividual> population, 
			List<IIndividual> offspring, List<IIndividual> archive) {
		
		// Copy current archive
		List<IIndividual> newArchive = new ArrayList<IIndividual>();
		newArchive.addAll(archive);
		
		// Compute the hypercubes of the offspring
		for(IIndividual ind: offspring){
			((IHypercubeMOFitness)ind.getFitness()).setHypercube(computeHypercube(ind));
		}

		// Indexes of individuals which offspring dominates
		List<Integer> dominates = new ArrayList<Integer>();
		// Indicates if the offspring is dominated by someone
		boolean isDominated;
		// Indexes of non dominated individuals with same hypercubes
		List<Integer> equalHypercube = new ArrayList<Integer>();

		MOSolutionComparator comparator = super.getSolutionComparator();
		int nRemovals = 0;
		IIndividual offspringInd;
		Hypercube offspringIndHypercubes, indHypercubes;
		EuclideanHypercubeDistance hdistance = new EuclideanHypercubeDistance();
		
		for(int i=0; i<offspring.size(); i++){
			dominates.clear();
			equalHypercube.clear();
			isDominated = false;

			offspringInd = offspring.get(i);
			offspringIndHypercubes = ((IHypercubeMOFitness)offspring.get(i).getFitness()).getHypercube();

			if(!newArchive.contains(offspringInd)){

				// Check the dominance between each offspring and archive members
				for(int j=0; !isDominated && j<newArchive.size(); j++){
					indHypercubes = ((IHypercubeMOFitness)newArchive.get(j).getFitness()).getHypercube();
					
					switch(this.hComparator.compare(offspringInd.getFitness(), newArchive.get(j).getFitness()))
					{
					// Offspring dominates individual
					case 1:
						dominates.add(j);
						break;
					// Individual dominates offspring
					case -1:
						isDominated=true;
						break;
					// Individuals are non dominated, check if they belong to the same hypercube
					case 0:
						if(offspringIndHypercubes.equals(indHypercubes))
							equalHypercube.add(j);
					}
				}

				// Non dominated solution
				if(!isDominated){
					// Then, if it e-dominates some individuals in the
					// external population, those individuals are removed
					// and the offspring added
					if(dominates.size()>0){
						nRemovals = 0;
						for(Integer j: dominates){
							newArchive.remove(j-nRemovals);
							nRemovals++;
						}

						newArchive.add(offspringInd.copy());
					}
					// No dominance exists between offspring
					// and the members of external population
					else{
						// The offspring belongs to a new hypercube, he is added
						if(equalHypercube.size()==0){
							newArchive.add(offspringInd.copy());
						}

						// The offspring belongs to the same
						// hypercube than any individual in external population
						else{
							boolean winner = false;
							nRemovals = 0;
							for(Integer j: equalHypercube){
								// Check pareto dominance
								IIndividual ind = newArchive.get(j-nRemovals);
								switch(comparator.compare(offspringInd, ind))
								{
								// Offspring dominates j
								case 1: 
									winner = true;
									newArchive.remove(j-nRemovals);
									nRemovals++;
									break;
									// j dominates offspring
								case -1:
									winner=false;
									break;
									// Non-dominated solutions, check euclidean distance to the hyperbox limit
									// The fitness should be converted to an hypercube (set of double values)
								case 0:
									
									double d0 = hdistance.distance(offspringInd, convertFitness(offspringInd));
									double d1 = hdistance.distance(offspringInd, convertFitness(ind));
									if(d0<d1){
										winner=true;
										newArchive.remove(j-nRemovals);
										nRemovals++;
									}
									break;
								}
							}
							if(winner){
								newArchive.add(offspringInd.copy());
							}
						}
					}
				}
			}
		}
		return newArchive;
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	protected void fitnessAssignment(List<IIndividual> population, List<IIndividual> archive) {
		// Do nothing
	}

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------- Protected methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Compute the hypercubes for the given individual.
	 * Each component, j, of the hypercube (identification array) 
	 * is obtained from the correspondent objective value (f_j) as:
	 * <p>B_j = floor((f_j-f_j_min)/epsilon_j) if f_j must be minimized</p>
	 * <p>B_j = ceil((f_j-f_j_min)/epsilon_j) if f_j must be maximized</p>
	 * @param individual The individual
	 * @return Corresponding hypercubes
	 * */
	protected Hypercube computeHypercube(IIndividual individual){
		
		List<Objective> objectives = ((IMOEvaluator)getContext().getEvaluator()).getObjectives();
		int nObj = objectives.size();
		MOFitness fitness = (MOFitness)individual.getFitness();
		IFitness values [] = new IFitness[nObj];
		Objective obj;
		double value;
		double epsilon [] = getEpsilonValues();
		// Compute the hypercube for each objetive
		for(int i=0; i<nObj; i++){
			obj = objectives.get(i);
			try {
				value = (fitness.getObjectiveDoubleValue(i)-obj.getMinimum())/epsilon[i];
				if(obj.isMaximized()){
					values[i] = new SimpleValueFitness(Math.ceil(value));
				}
				else{
					values[i] = new SimpleValueFitness(Math.floor(value));
				}
			} catch (IllegalAccessException | IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		Hypercube h = new Hypercube(values);
		return h;
	}
	
	/**
	 * Create the comparator of hypercubes.
	 * using the information of objectives.
	 * */
	protected void createHypercubeComparator(){
		List<Objective> objectives = ((IMOEvaluator)getContext().getEvaluator()).getObjectives();
		int nObj = objectives.size();
		Comparator<IFitness> [] componentComparators = new ValueFitnessComparator[nObj];
		Objective obj;
		for(int i=0; i<nObj; i++){
			obj = objectives.get(i);
			componentComparators[i] = new ValueFitnessComparator(!obj.isMaximized());
		}
		this.hComparator = new EpsilonDominanceComparator(componentComparators);
	}
	
	/**
	 * Create the command that should be used to extract the
	 * non-dominated solutions that will be added to the archive.
	 * @return An extractor command.
	 * */
	protected NonDominatedSolutionsExtractor createExtractor(){
		NonDominatedSolutionsExtractor extractor = new NonDominatedSolutionsExtractor();
		extractor.setComparator(this.hComparator);
		return extractor;
	}
	
	/**
	 * Return a copy of the given individual in which
	 * the hypercube constraints the fitness values of that individual.
	 * @param individual The individual to be copied.
	 * @return A copy of the individual with the corresponding hypercube values.
	 * */
	private IIndividual convertFitness(IIndividual individual){
		IIndividual other = individual.copy();
		MOFitness fitness = (MOFitness)other.getFitness();
		int nObj = fitness.getNumberOfObjectives();
		IFitness values [] = new IFitness[nObj];
		for(int i=0; i<nObj; i++){
			try {
				values[i] = new SimpleValueFitness(fitness.getObjectiveDoubleValue(i));
			} catch (IllegalAccessException | IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		Hypercube cube = new Hypercube(values);
		((IHypercubeMOFitness)other.getFitness()).setHypercube(cube);
		return other;
	}
	
	/**
	 * Set the epsilon values (width of the hypercubes) using the bounds in the
	 * objective and the desired number of hypercubes.
	 * */
	private void setEpsilonValues() {
		List<Objective> objectives = ((IMOEvaluator)getContext().getEvaluator()).getObjectives();
		int size = objectives.size();
		this.epsilon = new double[size];
		Objective obj;
		for(int i=0; i<size; i++){
			obj=objectives.get(i);
			this.epsilon[i] = (obj.getMaximum()-obj.getMinimum())/this.nHypercubes;
		}
	}
}
