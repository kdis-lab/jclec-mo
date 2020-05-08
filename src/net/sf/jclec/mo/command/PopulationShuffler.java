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

import java.util.List;
import java.util.ListIterator;

import net.sf.jclec.IIndividual;
import net.sf.jclec.util.random.IRandGen;

/**
 * A command to shuffle a set of solutions. The input population
 * will be modified as a result.
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
 * */

public class PopulationShuffler extends Command {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -9036151963022408920L;

	/** The random number generator */
	protected IRandGen randgen;

	/////////////////////////////////////////////////////////////////
	// -------------------------------------------------- Constructor
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public PopulationShuffler() {
		super(null);
		this.randgen = null;
	}

	/**
	 * Parameterized constructor.
	 * @param population The set of solutions to be shuffled.
	 * @param randgen The random number generator.
	 * */
	public PopulationShuffler(List<IIndividual> population, IRandGen randgen) {
		super(population);
		this.randgen = randgen;
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/**
	 * Set the random number generator.
	 * @param randgen The random number generator.
	 * */
	public void setRandGen(IRandGen randgen){
		this.randgen = randgen;
	}

	/**
	 * Get the random number generator.
	 * @return The random number generator.
	 * */
	public IRandGen getRandGen(){
		return this.randgen;
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void execute() {
		int size = this.population.size();
		IIndividual [] solutions = this.population.toArray(new IIndividual[size]);
		
		// Shuffle array
		for (int i=size; i>1; i--) {
			swap(solutions, i-1, this.randgen.choose(i));
		}
		
		// Dump array back into list
		ListIterator<IIndividual> it = this.population.listIterator();
		for (int i=0; i<solutions.length; i++) {
			it.next();
			it.set(solutions[i]);
		}
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/**
	 * Swap the two specified elements in the specified array.
	 * @param solutions The array of solutions.
	 * @param i First index.
	 * @param j Second index.
	 */
	protected  void swap(IIndividual[] solutions, int i, int j) {
		IIndividual tmp = solutions[i];
		solutions[i] = solutions[j];
		solutions[j] = tmp;
	}
}