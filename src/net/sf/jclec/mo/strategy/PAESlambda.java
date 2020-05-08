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
import java.util.List;

import org.apache.commons.configuration.Configuration;

import net.sf.jclec.IIndividual;
import net.sf.jclec.mo.IMOEvaluator;
import net.sf.jclec.mo.command.MaxObjectiveValue;
import net.sf.jclec.mo.command.MinObjectiveValue;
import net.sf.jclec.mo.command.NonDominatedSolutionsExtractor;
import net.sf.jclec.mo.comparator.fcomparator.ParetoComparator;
import net.sf.jclec.mo.evaluation.fitness.IPAESMOFitness;
import net.sf.jclec.util.random.IRandGen;

/**
 * PAES strategy.
 *  
 * <p>The <b>P</b>areto <b>A</b>rchived <b>E</b>volution <b>S</b>trategy 
 * (PAES) is an evolution strategy (ES) to solve multiobjective problems.</p> 
 * 
 * <p>This class implements (1+lambda)-PAES and (mu+lambda)-PAES. In these
 * variants, a fitness value is assigned to each individual, which is then
 * used to decided if they will be included in the archive or not.</p>
 * 
 * <p><i>Paper</i>: J.D. Knowles, D.W. Corne, 
 * “Approximating the Nondominated Front Using the Pareto Archived Evolution Strategy”, 
 * Evolutionary Computation, vol. 8, no. 2, pp. 149-172. 2000.</p>
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
 * @see PAES
 */

public class PAESlambda extends PAES {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 4261814388719575612L;

	/** Number of parents (mu) */
	protected int mu;

	/** Number of mutants (lambda) */
	protected int lambda;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public PAESlambda(){
		super();
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Get the value of <code>mu</code>.
	 * @return Mu value
	 * */
	public int getMu(){
		return this.mu;
	}
	
	/**
	 * Set the value of <code>mu</code>.
	 * @param mu The value that has to be set.
	 * */
	protected void setMu(int mu){
		this.mu = mu;
	}
	
	/**
	 * Get the value of <code>lambda</code>.
	 * @return Lambda value.
	 * */
	public int getLambda(){
		return this.lambda;
	}
	
	/**
	 * Set the value of <code>lambda</code>.
	 * @param lambda The value that has to be set.
	 * */
	protected void setLambda(int lambda){
		this.lambda = lambda;
	}
	
	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public List<IIndividual> initialize(List<IIndividual> population) {

		int size;

		// Calculate the number of locations
		int nObjs = ((IMOEvaluator)getContext().getEvaluator()).numberOfObjectives();
		this.numberOfLocations = (int)Math.pow(2, nObjs*this.numberOfBisections);
		this.crowdingValues = new int[numberOfLocations];

		// Initialize the bounds for each objective
		this.commandMaxValues = new MaxObjectiveValue();
		this.commandMinValues = new MinObjectiveValue();
		this.upperBounds = new double[nObjs];
		this.lowerBounds = new double[nObjs];
		updateObjectiveBounds(population);

		// Create the initial archive: add non-dominated solutions in the current population
		NonDominatedSolutionsExtractor command = createExtractor();
		command.setPopulation(population);
		command.execute();
		List<IIndividual> newArchive = command.getNonDominatedSolutions();
		
		// If mu==1, only one solution has been included in the archive, so it is not
		// necessary to calculate the locations and its crowding values
		if(this.mu==1){
			newArchive = new ArrayList<IIndividual>(1);
			newArchive.add(population.get(0));
			((IPAESMOFitness)newArchive.get(0).getFitness()).setLocation(0);
			((IPAESMOFitness)newArchive.get(0).getFitness()).setDominanceScore(1);
			this.crowdingValues[0]=1;
		}

		else{
			// Calculate the grid location of each solution
			size = newArchive.size();
			int [] properties;
			for(int i=0; i<size; i++){
				properties = calculateLocation(newArchive.get(i));
				((IPAESMOFitness)newArchive.get(i).getFitness()).setLocation(properties[0]);
				((IPAESMOFitness)newArchive.get(i).getFitness()).setDominanceScore(properties[1]);
			}

			// Initialize crowding values
			calculateCrowdingValues(newArchive);

			// If the archive has more individuals that the maximum size
			// remove the solutions belonging to the most crowding location
			int maxCrowdingLocation;
			List<Integer> membersMaxLocation;
			int rndMember, indexInArchive;
			IRandGen randgen = getContext().createRandGen();

			while(newArchive.size() > getArchiveSize()){

				// Get the location with the maximum crowding value
				maxCrowdingLocation = getMaxCrowdingLocation();

				// Get solutions in that region
				membersMaxLocation = getSolutionsInLocation(newArchive, maxCrowdingLocation);

				// Choose one at individual at random to be removed
				rndMember = randgen.choose(0, membersMaxLocation.size());
				indexInArchive = membersMaxLocation.get(rndMember);
				newArchive.remove(indexInArchive);

				// Update current properties of the archive
				this.crowdingValues[maxCrowdingLocation]--;
			}
		}

		return newArchive;
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void update() {
		super.update();
	}

	/**
	 * {@inheritDoc}
	 * <p>Parameters for (1+lambda)-PAES and (mu+lambda)-PAES are:
	 * <ul>
	 * 	<li>mu (<code>integer</code>): <p>Number of parents.</p></li>
	 *  <li>lambda (<code>integer</code>): <p>Number of mutants.</p></li>
	 * </ul>
	 * </p>
	 * */
	@Override
	public void configure(Configuration settings) {
		// Call super configuration
		super.configure(settings);
		
		int mu = settings.getInt("mu");
		setMu(mu);
		
		int lambda = settings.getInt("lambda");
		setLambda(lambda);		
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public List<IIndividual> matingSelection(List<IIndividual> population, List<IIndividual> archive){
		List<IIndividual> parents = new ArrayList<IIndividual>();
		int index1, index2;
		IRandGen randGen = getContext().createRandGen();
		int size;
		IIndividual ind, ind2;
		int [] properties;
		
		// Calculate the properties for the current population
		size = population.size();

		for(int i=0; i<size; i++){
			ind = population.get(i);
			properties = calculateLocation(ind);
			((IPAESMOFitness)population.get(i).getFitness()).setLocation(properties[0]);
			((IPAESMOFitness)population.get(i).getFitness()).setDominanceScore(calculateDominanceScore(ind,archive));
		}

		// Use binary tournament to select 'lambda' parents
		for(int i=0; i<this.lambda; i++){
			index1 = randGen.choose(0, this.mu);
			index2 = randGen.choose(0, this.mu);

			ind = population.get(index1);
			ind2 = population.get(index2);

			switch(compareFitness((IPAESMOFitness)ind.getFitness(), (IPAESMOFitness)ind2.getFitness())){
			case 1:
				parents.add(ind);
				break;
			case -1:
				parents.add(ind2);
				break;
			case 0:
				if(randGen.coin()){
					parents.add(ind);
				}
				else{
					parents.add(ind2);
				}
				break;
			}
		}
		return parents;
	}

	/**
	 * {@inheritDoc}
	 * <p>In (1+lambda)-PAES and (mu+lambda)-PAES, the replacement is based on 
	 * the dominance score and the crowding values.</p>
	 * */
	@Override
	public List<IIndividual> environmentalSelection(List<IIndividual> population, 
			List<IIndividual> offspring, List<IIndividual> archive) {

		List<IIndividual> survivors = new ArrayList<IIndividual>();
		int index1, index2;
		IRandGen randGen = getContext().createRandGen();
		int [] properties;

		// Calculate the properties of the new individuals
		int size = offspring.size();
		IIndividual ind, ind2;
		for(int i=0; i<size; i++){
			ind = offspring.get(i);
			properties = calculateLocation(ind);
			((IPAESMOFitness)ind.getFitness()).setLocation(properties[0]);
			((IPAESMOFitness)ind.getFitness()).setDominanceScore(calculateDominanceScore(ind, archive));
		}

		// Select 'mu' individuals from the set of 'lambda' mutants
		for(int i=0; i<this.mu; i++){
			index1 = randGen.choose(0, this.lambda);
			index2 = randGen.choose(0, this.lambda);
			ind = offspring.get(index1);
			ind2 = offspring.get(index2);

			switch(compareFitness((IPAESMOFitness)ind.getFitness(), (IPAESMOFitness)ind2.getFitness())){
			case 1:
				survivors.add(ind);
				break;
			case -1:
				survivors.add(ind2);
				break;
			case 0:
				if(randGen.coin()){
					survivors.add(ind);
				}
				else{
					survivors.add(ind2);
				}
				break;
			}
		}

		return survivors;
	}

	/**
	 * {@inheritDoc}
	 * <p>In (1+lambda)-PAES and (mu+lambda)-PAES, offspring are
	 * evaluated considering the grid locations and the dominance principle.</p>
	 **/
	@Override
	public List<IIndividual> updateArchive(List<IIndividual> population, 
			List<IIndividual> offspring, List<IIndividual> archive) {

		int pSize = offspring.size();
		int aSize = archive.size();

		int i=0,j=0;
		IIndividual mutant, archiveInd;
		boolean isDominated, isCopy;
		List<IIndividual> newArchive = new ArrayList<IIndividual>();
		IPAESMOFitness fitness;
		int maxCrowdingLocation, crowdingMutant, rndMember, indexInArchive;
		List<Integer> membersMaxRegion;
		IRandGen randgen = getContext().createRandGen();

		// Update bounds considering both the current archive and the offspring
		// It should be done only when they have change more than a threshold (not properly explained in the paper)
		List<IIndividual> allInds = new ArrayList<IIndividual>(archive);
		allInds.addAll(offspring);
		super.updateObjectiveBounds(allInds);

		// Set the grid location properties for each solution belonging to the archive
		int [] properties;
		for(i=0; i<aSize; i++){
			archiveInd = archive.get(i);
			properties = calculateLocation(archiveInd);
			((IPAESMOFitness)archiveInd.getFitness()).setLocation(properties[0]);
			((IPAESMOFitness)archiveInd.getFitness()).setDominanceScore(0); // only non dominated solutions
		}
		calculateCrowdingValues(archive);

		// Set the grid location properties for each mutant
		for(i=0; i<pSize; i++){
			mutant = offspring.get(i);
			properties = calculateLocation(mutant);
			((IPAESMOFitness)mutant.getFitness()).setLocation(properties[0]);
			((IPAESMOFitness)mutant.getFitness()).setDominanceScore(calculateDominanceScore(mutant, archive));
		}

		// Copy the current archive
		newArchive.addAll(archive);

		// For each potential member (mutant), check if it should be added or not
		for(i=0; i<pSize; i++){
			mutant = offspring.get(i);
			isDominated=false;
			isCopy=false;
			aSize = newArchive.size();

			for(j=0; !isDominated&&!isCopy&&j<aSize; j++){
				archiveInd = newArchive.get(j);
				if(i!=j){
					if(mutant.equals(archiveInd)){
						isCopy=true;
					}
					else if(getSolutionComparator().compare(mutant, archiveInd)==1){
						isDominated=true;
					}
				}
			}

			// The mutant is a potential member
			if(!isCopy && !isDominated){

				// Remove individuals that are dominated by the mutant
				j=0;
				isDominated = false;
				while(!isDominated && j<aSize){

					archiveInd = newArchive.get(j);
					switch(getSolutionComparator().compare(mutant, archiveInd)){
					// the mutant dominates the archive member
					case 1:
						fitness = (IPAESMOFitness)newArchive.get(j).getFitness();
						this.crowdingValues[fitness.getLocation()]--;
						newArchive.remove(j);
						aSize--;
						break;
						// the mutant is dominated	
					case -1:
						isDominated = true;
						break;
						// non dominated, check the following member
					case 0:
						j++;
						break;
					}
				}

				// The mutant is not dominated by any member in the archive
				if(!isDominated){

					// The archive is not full, then add the mutant
					if(aSize < getArchiveSize()){
						newArchive.add(mutant);
						fitness = (IPAESMOFitness)mutant.getFitness();
						this.crowdingValues[fitness.getLocation()]++;
					}

					// The archive is full, check crowding values
					else{
						crowdingMutant = this.crowdingValues[((IPAESMOFitness)mutant.getFitness()).getLocation()];

						// Get the most crowding region
						maxCrowdingLocation = getMaxCrowdingLocation();

						// The mutant belongs to a less crowded region
						if(crowdingMutant < this.crowdingValues[maxCrowdingLocation]){
							// Get solutions in that region
							membersMaxRegion = super.getSolutionsInLocation(newArchive, maxCrowdingLocation);

							// Choose one at random and remove it
							rndMember = randgen.choose(0, membersMaxRegion.size());
							indexInArchive = membersMaxRegion.get(rndMember);

							// Update properties
							if(indexInArchive!=-1){
								this.crowdingValues[maxCrowdingLocation]--;
								newArchive.remove(indexInArchive);

								// Add the mutant
								newArchive.add(mutant);
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
		// do nothing
	}

	/**
	 * This function calculates the dominance score for a given individual.
	 * @param individual The individual.
	 * @param archive The archive of solutions.
	 * */
	protected int calculateDominanceScore(IIndividual individual, List<IIndividual> archive){
		int score = 0;
		int size = archive.size();
		IIndividual archiveMember;
		for(int j=0; score==0 && j<size; j++){
			archiveMember = archive.get(j);
			switch (getSolutionComparator().compare(individual, archiveMember)) {
			// the individual dominates the archive member
			case 1:
				score = 1;
				break;
				// the individual is dominated by the archive member
			case -1:
				score = -1;
				break;
				// non dominated solutions	
			default:
				break;
			}
		}
		return score;
	}

	/**
	 * This function compares the dominance score and the crowding value
	 * for a given pair of individuals.
	 * @param fitness1 Fitness of the first individual.
	 * @param fitness2 Fitness of the second individual.
	 * */
	protected int compareFitness(IPAESMOFitness fitness1, IPAESMOFitness fitness2){
		int result;
		double score1, score2;
		double crowding1, crowding2;

		// Get the dominance scores
		score1 = fitness1.getDominanceScore();
		score2 = fitness2.getDominanceScore();

		// Different dominance score, don't use the crowding values
		if(score1 != score2){

			if(score1 > score2){
				result = 1;
			}
			else {
				result = -1;
			}
		}

		// Equal dominance score, check the crowding values
		else{
			crowding1 = this.crowdingValues[fitness1.getLocation()];
			crowding2 = this.crowdingValues[fitness2.getLocation()];

			if(crowding1 < crowding2){
				result = 1;
			}
			else if(crowding1 > crowding2){
				result = -1;
			}
			else{
				result = 0;
			}
		}
		return result;
	}
	
	/**
	 * Auxiliary method to create a command extractor.
	 * @return A command to extract non-dominated solutions.
	 * */
	protected NonDominatedSolutionsExtractor createExtractor(){
		ParetoComparator fcomparator = (ParetoComparator)getSolutionComparator().getFitnessComparator();
		NonDominatedSolutionsExtractor command = new NonDominatedSolutionsExtractor();
		command.setComparator(fcomparator);
		return command;
	}
}
