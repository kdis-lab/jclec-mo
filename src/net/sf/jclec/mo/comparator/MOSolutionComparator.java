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

package net.sf.jclec.mo.comparator;

import java.util.Comparator;

import net.sf.jclec.IIndividual;
import net.sf.jclec.mo.comparator.fcomparator.MOFitnessComparator;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;

/**
 * Comparator for solutions in a multi-objective problem.
 * This general class simply delegates the comparison
 * to the configured fitness comparator.
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
 * @see Comparator
 * @see IIndividual
 * */

public class MOSolutionComparator implements Comparator<IIndividual>{

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Fitness comparator */
	protected MOFitnessComparator fcomparator;
	
	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public MOSolutionComparator(){
	}
	
	/**
	 * Parameterized constructor.
	 * @param fcomparator Fitness comparator
	 * */
	public MOSolutionComparator(MOFitnessComparator fcomparator){
		setFitnessComparator(fcomparator);
	}
	
	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Get the internal fitness comparator.
	 * @return fitness comparator
	 * */
	public MOFitnessComparator getFitnessComparator(){
		return this.fcomparator;
	}
	
	/**
	 * Set the internal fitness comparator.
	 * @param fcomparator New fitness comparator
	 * */
	public void setFitnessComparator(MOFitnessComparator fcomparator){
		this.fcomparator = fcomparator;
	}
	
	/////////////////////////////////////////////////////////////////
	//---------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Compare two individuals. By default, individuals
	 * are compared using the internal fitness comparator.
	 * @param arg0 First individual
	 * @param arg1 Second individual
	 * @throws IllegalArgumentException
	 * */
	@Override
	public int compare(IIndividual arg0, IIndividual arg1) {
		MOFitness mofitness0, mofitness1;
		
		// Extract fitness from the first individual
		if(arg0.getFitness() instanceof MOFitness)
			mofitness0 = (MOFitness) arg0.getFitness();
		else
			throw new IllegalArgumentException("MOFitness expected as first argument");
		
		// Extract fitness from the second individual
		if(arg1.getFitness() instanceof MOFitness)
			mofitness1 = (MOFitness) arg1.getFitness();
		else
			throw new IllegalArgumentException("MOFitness expected as second argument");
		
		// Return fitness comparison
		return this.fcomparator.compare(mofitness0, mofitness1);
	}
}
