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
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.configuration.Configuration;

import net.sf.jclec.IFitness;
import net.sf.jclec.IIndividual;
import net.sf.jclec.base.AbstractSelector;
import net.sf.jclec.mo.comparator.MOSolutionComparator;
import net.sf.jclec.mo.comparator.fcomparator.ParetoComparator;
import net.sf.jclec.mo.distance.EuclideanDistance;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;
import net.sf.jclec.selector.TournamentSelector;
import net.sf.jclec.selector.WorsesSelector;

/**
 * SPEA2 strategy
 * 
 * <p>The <b>S</b>trength <b>P</b>areto <b>E</b>volutionary <b>A</b>lgorithm 2 (SPEA2) 
 * is an algorithm which incorporates a fine-grained fitness assignment strategy, 
 * a density estimation technique, and an enhanced archive truncation method.</p>
 * 
 * <p><i>Paper</i>: E. Zitzler, M. Laumanns, and L. Thiele, “SPEA2:
 * Improving the Strength Pareto Evolutionary Algorithm,” 
 * in Proc. Conf. on Evolutionary Methods for Design, Optimisation 
 * and Control with Applications to Industrial Problems, pp. 95–100, 2001.</p>
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
public class SPEA2 extends MOStrategy{

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -7777801597787675347L;

	/** Parent selector */
	protected AbstractSelector selector;

	/** Archive size */
	protected int archiveSize;

	/** The value for the nearest k-th neighbor technique */	
	protected int kValue;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public SPEA2(){
		super();
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Get the parent selector.
	 * @return Parents selector
	 */
	public AbstractSelector getSelector() {
		return this.selector;
	}

	/**
	 * Set the parent selector.
	 * @param selector The selector that has to be set.
	 */
	protected void setSelector(AbstractSelector selector) {
		this.selector = selector;
	}

	/**
	 * Get the size of the archive.
	 * @return Size of the archive.
	 */
	public int getArchiveSize() {
		return archiveSize;
	}

	/**
	 * Set the size of the archive.
	 * @param size The value that has to be set.
	 */
	public void setArchiveSize(int size) {
		this.archiveSize = size;
	}

	/**
	 * Get the k value of the kNN approach.
	 * @return k value
	 */
	public int getKValue(){
		return this.kValue;
	}

	/**
	 * Set the k value of the kNN approach.
	 * @param k The value that has to be set.
	 */
	public void setKValue(int k){
		if(k > getArchiveSize())
			throw new IllegalArgumentException("The k-value should be smaller than the archive size");
		else
			this.kValue = k;
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>Parameters for SPEA2 are:
	 * <ul>
	 * <li>archive-size (<code>integer</code>): 
	 * <p>Size of the archive. By default,
	 * the size of the population.</p></li> 
	 * 
	 * <li>k-value (<code>integer</code>): 
	 * <p>k value for k-nearest neighbors technique.
	 * By default sqrt(population size + archive size)</p></li>
	 * </p>
	 * */
	@Override
	public void configure(Configuration settings){

		// Call super configuration
		super.configure(settings);
		
		// Archive size
		try{
			int archiveSize = settings.getInt("archive-size", getContext().getPopulationSize());
			setArchiveSize(archiveSize);
		}catch(Exception e){
			System.err.println("The archive size should be specified");
			e.printStackTrace();
		}

		// k-value
		try{
			int kValue = settings.getInt("k-value", (int)Math.sqrt(getContext().getPopulationSize()+getArchiveSize()));
			setKValue(kValue);
		}catch(IllegalArgumentException e){
			e.printStackTrace();
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>In SPEA2, a tournament selector is configured and
	 * the fitness assignment is called to create the first
	 * archive considering the individuals within the initial population.</p>
	 * */
	@Override
	public List<IIndividual> initialize(List<IIndividual> population) {

		// Create the tournament selector
		this.selector = new TournamentSelector();
		((TournamentSelector)this.selector).setTournamentSize(2);
		this.selector.contextualize(super.getContext());

		// Create the initial archive
		fitnessAssignment(population, null);
		return createArchive(population, null);
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
	 * <p>SPEA2 uses a Pareto comparator.</p>
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
	 * <p>In SPEA2, parents are selected from the archive performing 
	 * a binary tournament with replacement.</p>
	 */
	@Override
	public List<IIndividual> matingSelection(List<IIndividual> population, List<IIndividual> archive) {
		// Perform a selection with replacement
		// considering the archive members
		List<IIndividual> parents = getSelector().select(archive,population.size(),true);
		return parents;
	}

	/**
	 * {@inheritDoc}
	 * <p>In SPEA2, the population will be replaced by the set of offspring.</p>
	 * */
	@Override
	public List<IIndividual> environmentalSelection(List<IIndividual> population, 
			List<IIndividual> offspring, List<IIndividual> archive) {
		// Return offspring
		return offspring;
	}

	/**
	 * {@inheritDoc}
	 * <p>A new archive is created considering the
	 * new population (offspring) and the current archive.</p>
	 * */
	@Override
	public List<IIndividual> updateArchive(List<IIndividual> population, 
			List<IIndividual> offspring, List<IIndividual> archive) {
		
		// Join the current population and the set of offspring
		List<IIndividual> allinds = new ArrayList<IIndividual> ();
		allinds.addAll(population);
		allinds.addAll(offspring);
		
		// Firstly, compute the new fitness values
		fitnessAssignment(allinds,archive);
		
		// Create a new archive
		List<IIndividual> newArchive = createArchive(allinds, archive);
		return newArchive;
	}

	/**
	 * {@inheritDoc}
	 * <p>SPEA2 assigns a fitness value to each
	 * individual in each population considering
	 * a strength value and a density value.</p>
	 * */
	@Override
	protected void fitnessAssignment(List<IIndividual> population, List<IIndividual> archive) {
		// Unite the populations
		List<IIndividual> allinds = new ArrayList<IIndividual> ();
		allinds.addAll(population);

		// If the archive already exists, add its individuals
		if(archive!=null){
			for(IIndividual ind : archive)
				if(!allinds.contains(ind))
					allinds.add(ind);
		}

		// Initialize variables
		MOSolutionComparator comparator = super.getSolutionComparator();
		int size = allinds.size();
		long [] S = new long [size];
		long [] R = new long [size];
		double [] D = new double [size];
		double [][] distance = new double [size][size]; 

		for(int i=0; i<size; i++){
			S[i] = 0;
			R[i] = 0;
			D[i] = 0.0;
		}

		/*
		 * First component of the fitness: Raw Fitness, R
		 */

		// For each individual, count the the number of individuals 
		// it dominates (its strength value)
		for(int i=0; i<size; i++)
			for(int j=0; j<size; j++)
				if(i!=j && comparator.compare(allinds.get(i), allinds.get(j)) == 1)
					S[i] ++;

		// Obtain the raw fitness as the sum of strength values of 
		// the individuals that dominate it
		for(int i=0; i<size; i++)
			for(int j=0; j<size; j++)
				if(i!=j && comparator.compare(allinds.get(j), allinds.get(i)) == 1)
					R[i] += S[j];

		/*
		 * Second component of the fitness: density information, D
		 */

		// Calculate the distance between the individuals
		EuclideanDistance euclDistance = new EuclideanDistance();
		for(int i=0; i<size; i++){
			for(int j=i+1; j<size; j++){
				distance[i][j] = euclDistance.distance(allinds.get(i), allinds.get(j));
				distance[j][i] = distance[i][j];
			}

			distance[i][i] = 0;
		}

		// Sort the individuals by rows
		for(int i= 0; i<size; i++)
			Arrays.sort(distance[i]);

		// The k_th nearest neighbor is selected
		for(int i=0; i<size; i++)
			D[i] = (1 / ( distance[i][this.kValue] + 2.0 ));

		// Finally, the fitness value is assigned
		for(int i=0; i<size; i++){
			((MOFitness)allinds.get(i).getFitness()).setValue(D[i] + R[i]);
		}

		S = R = null;
		D = null;
	}

	/////////////////////////////////////////////////////////////////
	// -------------------------------------------- Protected methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Create the archive of solutions.
	 * @param population Set of individuals in the current population.
	 * @param archive Set of individuals in the current archive.
	 * @return New archive of solutions.
	 * */
	protected List<IIndividual> createArchive(List<IIndividual> population, 
			List<IIndividual> archive){

		// Unite populations
		List<IIndividual> allinds = new ArrayList<IIndividual> ();
		allinds.addAll(population);

		if(archive!=null){
			for(IIndividual ind : archive)
				if(!allinds.contains(ind))
					allinds.add(ind);
		}

		List<IIndividual> newArchive = new ArrayList<IIndividual>();

		// The non dominated individuals are added to the archive
		for(IIndividual ind : allinds)
			if(!newArchive.contains(ind) && ((MOFitness) ind.getFitness()).getValue() < 1)
				newArchive.add(ind);

		// The archive has a smaller value than required
		if (this.archiveSize > newArchive.size())
			newArchive = incrementPopulation(allinds, newArchive, newArchive.size());

		// The archive size has been exceeded
		else if(this.archiveSize < newArchive.size())
			newArchive = decrementPopulation(newArchive, newArchive.size());

		return newArchive;
	}

	/**
	 * Add dominated individuals to the archive. This method
	 * modifies the given archive.
	 * @param population The set of individuals in the current population.
	 * @param archive The set of individuals in the current archive.
	 * @param currentSize Current size of the archive.
	 * @return New archive.
	 * */
	protected List<IIndividual> incrementPopulation(List<IIndividual> population, 
			List<IIndividual> archive, int currentSize){
		// The individuals are sorted of decreasing form
		List<IIndividual> sortedSet = sortPopulation(population);
		boolean exit = false;
		for(int i=0; i<population.size() && exit == false; i++)
			if( ((MOFitness) (sortedSet.get(i)).getFitness()).getValue() >= 1){
				archive.add(sortedSet.get(i));
				currentSize ++;
				if(currentSize == this.archiveSize)
					exit = true;
			}
		return archive;
	}

	/**
	 * Remove some non dominated individuals in the archive.
	 * It uses the k-nearest method to keep diversity in the population, 
	 * also avoiding the possible loss of outer solutions. This method
	 * modifies the given archive.
	 * @param archive The set of individuals
	 * @param currentSize Current size of the archive
	 * @return New archive
	 */
	protected List<IIndividual> decrementPopulation(List<IIndividual> archive, int currentSize){

		double [] distance = new double[currentSize];
		double [] k_distance = new double[currentSize];
		double auxDistance;
		int [] k_nearest = new int[currentSize];
		int [] sortedIndividuals = new int[currentSize];
		int auxSortedIndividual;

		for(int i=0; i<currentSize; i++){
			k_distance[i] = Double.MAX_VALUE;
			k_nearest[i] = i;
		}

		EuclideanDistance euclDistance = new EuclideanDistance();
		while(currentSize > this.archiveSize){

			// Compute the euclidean distance between solutions
			for(int i=0; i<currentSize; i++){
				for(int j=0; j<currentSize; j++){	
					distance[j] = euclDistance.distance(archive.get(i),archive.get(j));
					sortedIndividuals[j] = j;
				}

				// Sort the individuals with respect to its distance to individual 'i'
				for(int j=0; j<currentSize-1; j++)
					for(int l=j+1; l<currentSize; l++)
						if(distance[j] > distance[l]){

							auxDistance = distance[j];
							distance[j] = distance[l];
							distance[l] = auxDistance;

							auxSortedIndividual = sortedIndividuals[j];
							sortedIndividuals[j] = sortedIndividuals[l];
							sortedIndividuals[l] = auxSortedIndividual;
						}

				k_nearest[i] = sortedIndividuals[this.kValue];
				k_distance[i] = distance[this.kValue];
			}

			// Sort the individuals considering the k_distance
			// to the k_nearest neighbor calculated in the previous step
			for(int j=0; j<currentSize; j++)
				sortedIndividuals[j] = j;

			for(int j=0; j<currentSize-1; j++)
				for(int l=j+1; l<currentSize; l++)
					if(k_distance[j] > k_distance[l]){
						auxDistance = k_distance[j];
						k_distance[j] = k_distance[l];
						k_distance[l] = auxDistance;

						auxSortedIndividual = sortedIndividuals[j];
						sortedIndividuals[j] = sortedIndividuals[l];
						sortedIndividuals[l] = auxSortedIndividual;
					}
			// Remove the closer individual to the k-th neighbor
			archive.remove(k_nearest[sortedIndividuals[0]]);
			currentSize --;
		}
		return archive;
	}

	/**
	 * Sort population by fitness. The population is sorted
	 * following a decreasing order.
	 * @param population Individuals to be ordered.
	 * @return Population sorted by fitness.
	 * */
	protected List<IIndividual> sortPopulation(List<IIndividual> population){
		// Sort in decreasing form
		WorsesSelector selector = new WorsesSelector(super.getContext());
		return selector.select(population, population.size());
	}
}