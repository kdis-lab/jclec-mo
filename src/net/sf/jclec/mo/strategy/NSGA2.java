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
import net.sf.jclec.mo.command.CrowdingDistanceCalculator;
import net.sf.jclec.mo.command.PopulationSorter;
import net.sf.jclec.mo.comparator.CrowdingDistanceComparator;
import net.sf.jclec.mo.comparator.MOSolutionComparator;
import net.sf.jclec.mo.comparator.fcomparator.ParetoComparator;
import net.sf.jclec.mo.evaluation.Objective;
import net.sf.jclec.mo.evaluation.fitness.INSGA2MOFitness;
import net.sf.jclec.util.random.IRandGen;

/**
 * NSGA-II strategy.
 *  
 * <p>The <b>N</b>on <b>D</b>ominating <b>S</b>orting <b>G</b>enetic 
 * <b>A</b>lgorithm II (NSGA-II) is a multi-objective algorithm based 
 * on the Pareto dominance with low computational requirements.</p>
 * 
 * <p>NSGA-II performs an elitism-preservation optimization process. It uses a dominance 
 * ranking to classify the population into fronts and a truncation operator based 
 * on a crowding distance to guide the search.</p>
 *  
 * <p><i>Paper</i>: K. Deb, A. Pratap, S. Agarwal, and T. Meyarivan, 
 * “A fast and elitist multiobjective genetic algorithm: NSGA-II,” 
 * IEEE Transactions on Evolutionary Computation, vol. 6, no. 2, 
 * pp. 182–197, 2002.</p>
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
public class NSGA2 extends MOStrategy {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 690695573577645909L;
	
	/** Crowding distance calculator */
	protected CrowdingDistanceCalculator dCommand;
	
	/** Sort command */
	protected PopulationSorter sortCommand;
	
	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public NSGA2(){
		super();
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void configure(Configuration settings) {
		// Call super configuration
		super.configure(settings);
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public List<IIndividual> initialize(List<IIndividual> population) {
		// Create and configure the command
		this.dCommand = new CrowdingDistanceCalculator();
		this.dCommand.setComparator(getSolutionComparator().getFitnessComparator());
		List<Objective> objectives = ((IMOEvaluator)getContext().getEvaluator()).getObjectives();
		int size = objectives.size();
		double [] minValues = new double[size];
		double [] maxValues = new double[size];
		for(int i=0; i<size; i++){
			minValues[i] = ((IMOEvaluator)getContext().getEvaluator()).getObjectives().get(i).getMinimum();
			maxValues[i] = ((IMOEvaluator)getContext().getEvaluator()).getObjectives().get(i).getMaximum();
		}
		this.dCommand.setMinValues(minValues);
		this.dCommand.setMaxValues(maxValues);
		
		// Sort command
		this.sortCommand = new PopulationSorter();
		this.sortCommand.setComparator(new CrowdingDistanceComparator());
		this.sortCommand.setInverse(true); // order from max distance to min distance
		
		return null; // NSGA-II does not use the archive
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void update() {
	}

	/**
	 * {@inheritDoc}
	 * <p>NSGA-II uses a Pareto comparator.</p>
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
	 * <p> NSGA-II returns the current population in the
	 * first generation, and performs a special tournament
	 * selection in the rest of generations.</p>
	 * */
	@Override
	public List<IIndividual> matingSelection(List<IIndividual> population, List<IIndividual> archive) {
		if(getContext().getGeneration() == 1)
			return population;
		else{
			return tournamentSelection(population);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>In NSGA-II, the environmental selection performs the following steps:
	 * <ol>
	 * <li> Order the population by fronts regarding the Pareto dominance principle 
	 * (<code>fastNodominatedSorting()</code>).</li>
	 * 
	 * <li> Compute the crowding distance between all the individuals 
	 * (<code>crowdingDistanceAssignment</code>).</li>
	 * 
	 * <li> Create the new population progressively keeping individuals by fronts.
	 * The crowding distance is used to decide which individuals of the last front
	 * should be added to the population.</li>
	 * </ol>
	 * </p>
	 */
	@Override
	public List<IIndividual> environmentalSelection(List<IIndividual> population, 
			List<IIndividual> offspring, List<IIndividual> archive) {

		// Join the populations
		List<IIndividual> allinds = new ArrayList<IIndividual> ();
		allinds.addAll(population);

		for(IIndividual ind : offspring)
			if(!allinds.contains(ind))
				allinds.add(ind);

		// Initialize the specific properties of the fitness
		for(int i=0; i<allinds.size(); i++){
			((INSGA2MOFitness)allinds.get(i).getFitness()).setFront(-1);
			((INSGA2MOFitness)allinds.get(i).getFitness()).setDominatedBy(0);
			((INSGA2MOFitness)allinds.get(i).getFitness()).setCrowdingDistance(0);
		}

		// The individuals are sorted by fronts
		List<List<IIndividual>> populationByFronts = fastNonDominatedSorting(allinds);

		// The crowding distance is computed
		for(List<IIndividual> front : populationByFronts) 
			crowdingDistanceAssignment(front);

		// Create the set of survivors
		List<IIndividual> survivors = new ArrayList<IIndividual>();
		int size =  population.size();
		int index = 0;

		// Add individuals by fronts
		while((survivors.size() + populationByFronts.get(index).size()) < size ){
			survivors.addAll(populationByFronts.get(index));
			index++;
		}

		// Complete the population, if required, with some individuals
		// on the critical front using the crowding distance
		int fill = size-survivors.size();
		if(fill>0){
			this.sortCommand.setPopulation(populationByFronts.get(index));
			this.sortCommand.execute();
			for(int j=0; j<fill; j++)
				survivors.add(populationByFronts.get(index).get(j));
		}
		return survivors;
	}

	/**
	 * {@inheritDoc}
	 * <p>NSGA-II does not use an external population.</p>
	 * */
	@Override
	public List<IIndividual> updateArchive(List<IIndividual> population, 
			List<IIndividual> offspring, List<IIndividual> archive) {
		return null;
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
	 * Binary tournament selection based on the front and crowding distance.
	 * When comparing two solutions with different fronts, the one
	 * having the lower front value is preferred. If they belong
	 * to the same front, the solution with less crowding distance
	 * is preferred.
	 * @param population The individuals set to select from.
	 * @return Selected individuals.
	 * */
	protected List<IIndividual> tournamentSelection(List<IIndividual> population) {
		List<IIndividual> parents = new ArrayList<IIndividual>();
		int size = population.size();
		INSGA2MOFitness fitness0, fitness1;
		IRandGen randgen = getContext().createRandGen();
		IIndividual ind0, ind1;

		for(int i=0; i<size; i++){
			// Select two individuals at random
			ind0 = population.get(randgen.choose(0, size));
			ind1 = population.get(randgen.choose(0, size));

			fitness0 = (INSGA2MOFitness)ind0.getFitness();
			fitness1 = (INSGA2MOFitness)ind1.getFitness();

			// Compare the fronts, the individual with the lesser front is preferred
			if(fitness0.getFront() < fitness1.getFront())
				parents.add(ind0);
			else if(fitness0.getFront() > fitness1.getFront())
				parents.add(ind1);

			// If they belong to the same front, the individual 
			// with the greater crowding distance should be selected
			else if(fitness0.getCrowdingDistance() > fitness1.getCrowdingDistance()){
				parents.add(ind0);
			}
			else if(fitness0.getCrowdingDistance() < fitness1.getCrowdingDistance()){
				parents.add(ind1);
			}

			// If they have the same crowding value, choose at random
			else if(randgen.coin()){
				parents.add(ind0);
			}
			else{
				parents.add(ind1);
			}	
		}

		return parents;
	}

	/**
	 * Sort the individuals by fronts. The first front (rank=1) 
	 * will include all the non dominated individuals. The second 
	 * front will include all the individuals that are non dominated
	 * after discarding individuals belonging to the first front, 
	 * and so on.
	 * <p>It requires that the fitness of the individuals implement the 
	 * <code>INSGA2MOFitness</code> interface.</p>
	 * @param population The population which is going to be ordered by fronts.
	 * @return A double list containing the population ordered by fronts.
	 */
	public List<List<IIndividual>> fastNonDominatedSorting(List<IIndividual> population) {

		// The set of non-dominated fronts
		List<List<IIndividual>> populationByFronts  = new ArrayList<List<IIndividual>>();
		populationByFronts.add(new ArrayList<IIndividual>());	// add the first front

		// It stores the set of dominated individuals of an individual
		List<IIndividual> auxDominated = new ArrayList<IIndividual>(); 

		int size = population.size();

		// For each individual in the population, compute the number of individuals
		// that dominate it and the list of individuals it dominates
		MOSolutionComparator comparator = super.getSolutionComparator();
		for(int i=0; i<size; i++) {
			for(int j=0; j<size; j++) {
				// Check Pareto dominance
				switch(comparator.compare(population.get(i), population.get(j))){
				// If the result is equal to 1, i dominates j
				case 1:
					auxDominated.add(population.get(j));
					break;

					// If the result is equal to -1, j dominates i
				case -1:
					((INSGA2MOFitness)population.get(i).getFitness()).incrementDominatedBy();
					break;

					// If the result is equal to 0, they are non dominated individuals 
				case 0:
					break;
				}
			}

			//The list of dominated individuals is added
			((INSGA2MOFitness)population.get(i).getFitness()).setDominatedList(auxDominated);

			// If the individual is not dominated by any individual, add to the first front
			if (((INSGA2MOFitness)population.get(i).getFitness()).getDominatedBy() == 0) {
				((INSGA2MOFitness)population.get(i).getFitness()).setFront(1);
				populationByFronts.get(0).add(population.get(i));
			}

			// Clear the list for the next individual
			auxDominated.clear();
		}

		// Initialize the necessary variables
		int numFront = 1;
		List<IIndividual> previousFront;
		List<IIndividual> nextFront = new ArrayList<IIndividual>();

		INSGA2MOFitness fitness;
		IIndividual dominatedInd;
		int remainingIndividuals = size - populationByFronts.get(0).size();

		// Establish the front to which each individual belongs
		while (remainingIndividuals > 0) {

			// Clean the current front and extract the previous front
			nextFront.clear();
			previousFront = populationByFronts.get(numFront-1); 

			// Add to current front those individuals that are non-dominated after
			// discarding the individuals in the previous front
			for(int i=0; i < previousFront.size(); i++){

				fitness = ((INSGA2MOFitness)previousFront.get(i).getFitness());

				for(int j=0; j < fitness.getDominatedList().size(); j++){

					// Decrement the number of individuals that dominates this individual
					dominatedInd = fitness.getDominatedList().get(j);
					((INSGA2MOFitness)dominatedInd.getFitness()).decrementDominatedBy();

					// If the individual is non dominated by anyone, it will belong to the next front
					if(((INSGA2MOFitness)dominatedInd.getFitness()).getDominatedBy() == 0){
						((INSGA2MOFitness)dominatedInd.getFitness()).setFront(numFront + 1);
						nextFront.add(dominatedInd);
					}
				}
			}

			// Add the current front to the list of fronts and increment the number of fronts
			populationByFronts.add(new ArrayList<IIndividual>());
			populationByFronts.get(numFront).addAll(nextFront);

			// Update the counter
			remainingIndividuals -= nextFront.size();

			// Increment the number of fronts
			numFront++;
		}

		return populationByFronts;
	}

	/**
	 * Compute an estimation of the density of each solution of the population. 
	 * The crowding distance considers the average distance of two points 
	 * (solutions) on either side of this point along each of the objectives.
	 * <p>It requires that the fitness of the individuals implement the 
	 * <code>INSGA2MOFitness</code> interface. </p>
	 * @param population The list of individuals.
	 */
	protected void crowdingDistanceAssignment(List<IIndividual> population){
		// Call the command that compute the crowding distance
		this.dCommand.setPopulation(population);
		this.dCommand.execute();
	}
}