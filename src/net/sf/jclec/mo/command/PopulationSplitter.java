/*
This file belongs to JCLEC-MO, a Java library for the
application and development of metaheuristic algorithms 
for the resolution of multi-objective and many-objective 
optimization problems.

Copyright (C) 2018. A. Ramirez, J.R. Romero, S. Ventura.
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

package net.sf.jclec.mo.command;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import net.sf.jclec.IIndividual;
import net.sf.jclec.mo.comparator.MOSolutionComparator;

/**
 * This command splits the population in subsets according to a comparison criterion.
 * Here, any comparator extending from <code>MOIndividualsComparator</code> can be used. 
 * The given population is not modified during the execution of the command. The resulting 
 * subsets store copies of the individuals. These sets of solutions can be retrieved after 
 * the execution of the command using a getter method that will return a list of lists.
 *  
 * <p>The subsets are created in the following way:
 * <ol>
 * 	<li>The individuals that are better than any other individual are stored in the first set.</li>
 *  <li>The individuals that are better than any other individual, after discarding those 
 *  	belonging to the first set, are stored in the second set.</li>
 *  <li>The process continues until all the individuals are included in their corresponding subset.</li>
 * </ol>
 * </p>
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
 * @see Command
 * @see MOSolutionComparator
 * */
public class PopulationSplitter extends Command {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -1016444953886785494L;

	/** The split population */
	protected List<List<IIndividual>> splitPopulation;

	/** The individuals comparator */
	protected MOSolutionComparator comparator;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public PopulationSplitter() {
		super(null);
		this.splitPopulation = null;
		this.comparator = null;
	}

	/**
	 * Parameterized constructor.
	 * @param population The set of individuals to work with.
	 * @param comparator The comparator that will be used to compare individuals.
	 * */
	public PopulationSplitter(List<IIndividual> population, MOSolutionComparator comparator) {
		super(population);
		this.comparator = comparator;
		this.splitPopulation = null;
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Set the comparator.
	 * @param comparator The comparator that will be used to compare individuals.
	 * */
	public void setComparator(MOSolutionComparator comparator){
		this.comparator = comparator;
	}
	
	/**
	 * Get the population divided according to the comparison made.
	 * @return A double list containing the split population.
	 * */
	public List<List<IIndividual>> getSplitPopulation(){
		return this.splitPopulation;
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////
	
	/**
	 * {@inheritDoc}
	 * <p>This command uses a comparator to divide the population
	 * in different sets.
	 * It uses the comparator stored in <code>comparator</code>.
	 * The partitions will be stored in <code>splitPopulation</code>, so it can be then
	 * obtained using <code>getSplitPopulation()</code> method.</p>
	 * */
	@Override
	public void execute() {

		// Empty partition, the maximum number of subsets is equal to the population size
		int size = this.population.size();
		this.splitPopulation = new ArrayList<List<IIndividual>>(size);

		for(int i=0; i<size; i++){
			this.splitPopulation.add(new ArrayList<IIndividual>());
		}

		int actSubset = 0, added = 0;
		boolean addToCurrentSubset;
		BitSet checked = new BitSet(size);
		BitSet actchecked = new BitSet(size);
		int j;

		// Search best solutions
		while(added < size){

			for(int i=0; i<size; i++){

				if(checked.get(i))
					continue;

				actchecked.clear(i);
				addToCurrentSubset = true;

				j=0;
				while(addToCurrentSubset && j<size){
					// Individual i is worse than individual j
					if(i!=j && !checked.get(j) && 
							this.comparator.compare(this.population.get(j),this.population.get(i))==1){
						addToCurrentSubset = false;
					}
					j++;
				}

				// Individual i is better than any of the remaining solutions, store it in the current front
				if(addToCurrentSubset){
					actchecked.set(i);	// already visited
					this.splitPopulation.get(actSubset).add(this.population.get(i).copy());
					added++;
				}
			}

			// Update checked individuals
			for(j=0; j<size; j++){
				if(actchecked.get(j))
					checked.set(j);
			}

			// Increment the front counter
			actSubset++;
		}

		// Remove unused fronts
		for(int i=size-1; i>=actSubset; i--)
			this.splitPopulation.remove(i);
	}
}
