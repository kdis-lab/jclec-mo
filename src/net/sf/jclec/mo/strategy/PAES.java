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
import net.sf.jclec.mo.comparator.MOSolutionComparator;
import net.sf.jclec.mo.comparator.fcomparator.ParetoComparator;
import net.sf.jclec.mo.evaluation.fitness.IPAESMOFitness;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;
import net.sf.jclec.util.random.IRandGen;

/**
 * PAES strategy.
 *  
 * <p>The <b>P</b>areto <b>A</b>rchived <b>E</b>volution <b>S</b>trategy 
 * (PAES) is an evolution strategy (ES) to solve multi-objective problems.</p> 
 * 
 * <p>PAES is a (1+1) ES that uses a reference archive to compare the mutated
 * individual with previously found non-dominated solutions in order to decide
 * if the mutant replaces the current individual.</p>
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
 * @see MOStrategy
 */

public class PAES extends MOStrategy {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 1631794252812592161L;

	/** The current individual will be replaced? */
	protected boolean replaceCurrent;

	/** The new individual will be added to the archive of solutions? */
	protected boolean addToArchive;

	/** The index of the solution in the archive that will be removed */
	protected int indexToRemove;

	/** The crowding value for each region of the search space */
	protected int [] crowdingValues;

	/** The number of bisections of the space */
	protected int numberOfBisections;

	/** Minimum values for each objective in the archive */
	protected double [] lowerBounds;

	/** Maximum values for each objective in the archive */
	protected double [] upperBounds;

	/** Size of the archive */
	protected int archiveSize;

	/** Number of grid locations */
	protected int numberOfLocations;

	/** Command to calculate minimum objective values */
	protected MinObjectiveValue commandMinValues;

	/** Command to calculate maximum objective values */
	protected MaxObjectiveValue commandMaxValues;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public PAES(){
		super();
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * The current individual will be replaced?
	 * @return Value of the <code>replaceCurrent</code> flag.
	 * */
	protected boolean replaceCurrent(){
		return this.replaceCurrent;
	}

	/**
	 * Set if the current individual will be replaced.
	 * @param replace The new value of the flag.
	 * */
	protected void setReplaceCurrent(boolean replace){
		this.replaceCurrent = replace;
	}

	/**
	 * The mutated individual will be added to the archive?
	 * @param Value of the <code>addToArchive</code> flag.
	 * */
	protected boolean addToArchive(){
		return this.addToArchive;
	}

	/**
	 * Set if the new individual will be added to the archive.
	 * @param add The new value of the flag.
	 * */
	protected void setAddToArchive(boolean add){
		this.addToArchive = add;
	}

	/**
	 * Get the position of the individual in the archive
	 * that will be removed.
	 * @return The index of the element in the archive to be removed.
	 * */
	protected int getIndexToRemove(){
		return this.indexToRemove;
	}

	/**
	 * Set the position of the individual in the archive that
	 * will be removed.
	 * @return The index of the element in the archive to be removed.
	 * */
	protected void setIndexToRemove(int index){
		this.indexToRemove = index;
	}

	/**
	 * Get the number of bisections.
	 * @return The number of bisections.
	 * */
	protected int getNumberOfBisections(){
		return this.numberOfBisections;
	}

	/**
	 * Set the number of bisections.
	 * @param bisections The number of bisections.
	 * */
	protected void setNumberOfBisections(int bisections){
		this.numberOfBisections=bisections;
	}

	/**
	 * Get the size of the archive.
	 * @return The size of the archive.
	 * */
	protected int getArchiveSize(){
		return this.archiveSize;
	}

	/**
	 * Set the archive size.
	 * @param size The new size.
	 * */
	protected void setArchiveSize(int size){
		this.archiveSize = size;
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>PAES has the following parameters:
	 * <ul>
	 * 	<li>number-of-bisections (<code>integer</code>): <p>Number of bisections.</p></li>
	 *  <li>archive-size (<code>integer</code>): <p>Archive size.</p></li>
	 * </ul>
	 * </p>
	 * */
	@Override
	public void configure(Configuration settings) {

		// Call super configuration
		super.configure(settings);

		// Get the number of bisections
		int bisections = settings.getInt("number-of-bisections");
		setNumberOfBisections(bisections);

		// Get archive size
		int archiveSize = settings.getInt("archive-size");
		setArchiveSize(archiveSize);
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public List<IIndividual> initialize(List<IIndividual> population) {

		// Initialize internal variables
		this.addToArchive = false;
		this.replaceCurrent = false;
		this.addToArchive = false;
		this.indexToRemove = -1;

		this.commandMaxValues = new MaxObjectiveValue();
		this.commandMinValues = new MinObjectiveValue();

		// Calculate the number of locations
		int nObjs = ((IMOEvaluator)getContext().getEvaluator()).numberOfObjectives();
		this.numberOfLocations = (int)Math.pow(2, nObjs*this.numberOfBisections);
		this.crowdingValues = new int[numberOfLocations];

		// Initialize the bounds for each objective
		this.upperBounds = new double[nObjs];
		this.lowerBounds = new double[nObjs];
		updateObjectiveBounds(population);

		// Create the initial archive: add the current solution
		List<IIndividual> newArchive = new ArrayList<IIndividual>();
		((IPAESMOFitness)population.get(0).getFitness()).setLocation(0);
		((IPAESMOFitness)population.get(0).getFitness()).setDominanceScore(1);
		newArchive.add(population.get(0));
		this.crowdingValues[0]=1;
		return newArchive;
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void update() {

		// Clean variables
		this.addToArchive = false;
		this.replaceCurrent = false;
		this.addToArchive = false;
		this.indexToRemove = -1;
	}

	/**
	 * {@inheritDoc}
	 * <p>PAES uses a Pareto comparator.</p>
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
	 * <p>A deterministic selection is performed.</p>
	 * */
	@Override
	public List<IIndividual> matingSelection(List<IIndividual> population, List<IIndividual> archive) {
		return population;
	}

	/**
	 * {@inheritDoc}
	 * <p>In (1+1) PAES, the replacement is based on the dominance principle
	 * and the crowding regions in the archive of solutions.</p>
	 * */
	@Override
	public List<IIndividual> environmentalSelection(List<IIndividual> population, 
			List<IIndividual> offspring, List<IIndividual> archive) {

		List<IIndividual> survivors = new ArrayList<IIndividual>(1);
		if(population.size()!=1 && offspring.size()!=1){
			throw new IllegalArgumentException("PAES requires only one parent and one mutant");
		}

		IIndividual parent = population.get(0);
		IIndividual mutant = offspring.get(0);

		// Compare by dominance
		switch(this.getSolutionComparator().compare(mutant, parent)){
		// the parent dominates the descendant, discard the descendant
		case -1:
			survivors.add(parent);
			setAddToArchive(false);
			break;
			// the descendant dominates the parent
		case 1:
			survivors.add(mutant);
			setAddToArchive(true);
			break;
			// solutions are non dominated	
		case 0:
			// check if the descendant is dominated by any member of the archive
			boolean isDominated = false;
			for(int i=0; !isDominated && i<archive.size(); i++){
				if(this.getSolutionComparator().compare(mutant,archive.get(i))==-1){
					isDominated=true;
				}
			}
			// the mutant is dominated, discard it
			if(isDominated){
				survivors.add(parent);
				setAddToArchive(false);
			}

			// otherwise, use the test method to decide
			else{

				test(parent, mutant, archive);
				if(replaceCurrent()){
					survivors.add(mutant);
				}
				else{
					survivors.add(parent);
				}
			}
			break;
		}
		return survivors;
	}

	/**
	 * {@inheritDoc}
	 * <p>In (1+1) PAES, the new individual can be add to the archive 
	 * if it the archive is not full or if it belongs to a less crowding
	 * region. It requires the previous execution of the <code>test()</code> 
	 * method, since that function determines both conditions.</p>
	 * */
	@Override
	public List<IIndividual> updateArchive(List<IIndividual> population, 
			List<IIndividual> offspring, List<IIndividual> archive) {

		int location, size;
		IPAESMOFitness fitness;
		IIndividual ind;
		int [] properties;
		
		// Get the mutant
		IIndividual mutant = offspring.get(0);

		// Copy the current archive
		List<IIndividual> newArchive = new ArrayList<IIndividual>(archive);

		// Check the result of the test method
		if(this.addToArchive){

			// Update location information
			properties = calculateLocation(mutant);
			this.crowdingValues[properties[0]]++;
			((IPAESMOFitness)mutant.getFitness()).setLocation(properties[0]);
			((IPAESMOFitness)mutant.getFitness()).setDominanceScore(properties[1]);

			// Add the solution
			newArchive.add(mutant);			
		}

		if(this.indexToRemove != -1){

			// Remove location information
			fitness = (IPAESMOFitness)newArchive.get(this.indexToRemove).getFitness();
			location = fitness.getLocation();
			this.crowdingValues[location]--;

			// Remove the solution from the archive
			newArchive.remove(this.indexToRemove);
		}

		// If the archive has been updated, recalculate the objective bounds and the grid locations
		// It is not properly defined in the paper
		if(this.addToArchive){
			updateObjectiveBounds(newArchive);
			size = newArchive.size();
			for(int i=0; i<size; i++){
				ind = newArchive.get(i);
				properties = calculateLocation(ind);
				((IPAESMOFitness)ind.getFitness()).setLocation(properties[0]);
				((IPAESMOFitness)ind.getFitness()).setDominanceScore(properties[1]);
			}
			// Update the crowding values
			calculateCrowdingValues(newArchive);
		}
		return newArchive;
	}

	/**
	 * {@inheritDoc}
	 * <p>(1+1)-PAES does not use a specific fitness value.</p>
	 * */
	@Override
	protected void fitnessAssignment(List<IIndividual> population, List<IIndividual> archive) {
		// do nothing 
	}

	/////////////////////////////////////////////////////////////////
	// -------------------------------------------- Protected methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Decide if the mutant will replace the current individual. Also
	 * check if the mutant should be added to the archive of solutions.
	 * @param current Current individual.
	 * @param mutant Mutated individual.
	 * @param archive The archive of solutions.
	 * */
	protected void test(IIndividual current, IIndividual mutant, List<IIndividual> archive){

		// Get the location of the parent (current solution) and the mutant
		int [] currentProperties = calculateLocation(current);
		int [] mutantProperties = calculateLocation(mutant);

		int crowdingCurrent  = this.crowdingValues[currentProperties[0]];
		int crowdingMutant = this.crowdingValues[mutantProperties[0]];
		
		int maxCrowdingRegion;
		List<Integer> membersMaxRegion;
		IRandGen randgen = getContext().createRandGen();
		int rndIndex;

		// Check crowding values to decide if the mutant replaces the current solution
		if(crowdingMutant < crowdingCurrent){
			setReplaceCurrent(true);
		}
		else{
			setReplaceCurrent(false);
		}

		// Next, check the crowding values in the archive to decide if the mutant
		// will be added to that archive

		// If the archive is not full, add the descendant to the archive
		if(archive.size() < getArchiveSize()){
			setAddToArchive(true);
		}

		// The archive is full
		else{

			// Get the most crowding region
			maxCrowdingRegion = getMaxCrowdingLocation();

			// The mutant belongs to a less crowded region
			if(crowdingMutant < maxCrowdingRegion){
				// The new solution will be added to the archive
				setAddToArchive(true);

				// Get solutions in that region
				membersMaxRegion = getSolutionsInLocation(archive, maxCrowdingRegion);

				// Choose one at random and store its position in the archive
				rndIndex = randgen.choose(0, membersMaxRegion.size());
				setIndexToRemove(membersMaxRegion.get(rndIndex));
			}

			// The mutant belongs to the most crowded region
			else{
				setAddToArchive(false);
			}
		}
	}

	/**
	 * Get the grid location for a given individual.
	 * @param individual The individual.
	 * @return A <code>GridLocationProperties</code> object with the location of the solution.
	 * */
	protected int [] calculateLocation(IIndividual individual){
		String sLocation = "";
		MOFitness fitness = (MOFitness)individual.getFitness();
		int numOfObjs = fitness.getNumberOfObjectives();
		double min, max;
		double fValue;
		int nBisections = getNumberOfBisections();
		double threshold;

		for(int i=0; i<numOfObjs; i++){

			// Fitness value for the objective
			try {
				fValue = fitness.getObjectiveDoubleValue(i);
			} catch (IllegalAccessException | IllegalArgumentException e) {
				break;
			} 

			// Global minimum and maximum values for the objective
			min = this.lowerBounds[i];
			max = this.upperBounds[i];

			// Now, iteratively cut the range in two parts
			for(int j=0; j<nBisections; j++){
				threshold = (max+min)/2.0;
				// Check if the solution lies on the larger half of the bisection
				if(fValue > threshold){
					sLocation += "1";
					min = threshold;
				}
				else{
					sLocation += "0";
					max = threshold;
				}	
			}
		}

		// If only one section exists, the location is always 0
		if(sLocation.length()==0){
			sLocation = "0";
		}

		// Convert the location into an integer value
		int location = convertBinaryLocation(sLocation);

		// Get the number of individuals belonging to the same location
		int crowding = this.crowdingValues[location];
		int [] result = new int[2];
		result[0]=location;
		result[1]=crowding;
		return result;
	}

	/**
	 * Update the bounds of each objective considering 
	 * a set of solutions.
	 * @param individuals A set of solutions.
	 * */
	protected void updateObjectiveBounds(List<IIndividual> population){
		int size = this.upperBounds.length;
		this.commandMinValues.setPopulation(population);
		this.commandMaxValues.setPopulation(population);

		// Update bounds 
		for(int i=0; i<size; i++){
			// Minimum value
			this.commandMinValues.setObjectiveIndex(i);
			this.commandMinValues.execute();
			this.lowerBounds[i] = this.commandMinValues.getMinValue();

			// Maximum value
			this.commandMaxValues.setObjectiveIndex(i);
			this.commandMaxValues.execute();
			this.upperBounds[i] = this.commandMaxValues.getMaxValue();
		}
	}

	/**
	 * Set the number of solutions in each location. It uses the grid location
	 * information stored in the fitness object of archive members.
	 * @param archive The current archive.
	 * */
	protected void calculateCrowdingValues(List<IIndividual> archive){
		for(int i=0; i<this.numberOfLocations; i++){
			this.crowdingValues[i] = 0;
		}
		int size = archive.size();
		int location;
		// Set the number of solution in each location
		for(int i=0; i<size; i++){
			location = ((IPAESMOFitness)archive.get(i).getFitness()).getLocation();
			this.crowdingValues[location]++;
		}
	}

	/**
	 * Convert the binary string location to an integer value.
	 * @param location String representing the location, e.g. "011".
	 * @return An integer with the location in decimal form, e.g. 3.
	 * */
	protected int convertBinaryLocation(String location){
		int iLocation;
		try{
			iLocation = Integer.parseInt(location, 2);
		}catch(NumberFormatException e){
			iLocation = -1;
		}
		return iLocation;
	}

	/**
	 * Get the index of the grid location with higher population.
	 * @return The index of the region having the maximum crowding value.
	 * */
	protected int getMaxCrowdingLocation(){
		int maxRegion = -1;
		int maxValue = -1;
		for(int i=0; i<this.numberOfLocations; i++){
			if(this.crowdingValues[i]>maxValue){
				maxValue = this.crowdingValues[i];
				maxRegion = i;
			}
		}
		return maxRegion;
	}

	/**
	 * Get the solutions belonging to a given grid location.
	 * @param archive The set of solutions in the archive.
	 * @param location The grid location.
	 * @return A list with the indexes of the solutions that belongs to the specified location.
	 * */
	protected List<Integer> getSolutionsInLocation(List<IIndividual> archive, int location){
		List<Integer> members = new ArrayList<Integer>();
		int size = archive.size();
		Integer value;
		for(int i=0; i<size; i++){
			if(((IPAESMOFitness)archive.get(i).getFitness()).getLocation() == location){
				value = Integer.valueOf(i);
				members.add(value);
			}
		}
		return members;
	}
}