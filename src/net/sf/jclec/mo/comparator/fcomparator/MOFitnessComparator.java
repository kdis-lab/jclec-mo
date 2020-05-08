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

package net.sf.jclec.mo.comparator.fcomparator;

import java.util.Comparator;

import net.sf.jclec.IFitness;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;

/**
 * Abstract comparator for <code>MOFitness</code> objects.
 * 
 * <p>HISTORY:
 * <ul>
 * <li>(AR|JRR|SV, 1.0, March 2018)		First release.</li>
 * </ul>
 * </p>
 * 
 * @version 1.0
 * @author Aurora Ramirez (AR)
 * @author Jose Raul Romero (JRR)
 * @author Sebastian Ventura (SV)
 * 
 * <p>Knowledge Discovery and Intelligent Systems (KDIS) Research Group: 
 * {@link http://www.uco.es/grupos/kdis}</p>
 * 
 * @see MOFitness
 * @see Comparator
 * @see IFitness
 * */
public abstract class MOFitnessComparator implements Comparator<IFitness>{

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Component fitness comparators */
	protected Comparator<IFitness> [] componentComparators;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public MOFitnessComparator(){
		//do nothing
	}
	
	/**
	 * Parameterized constructor.
	 * @param componentComparators Component fitness comparators.
	 */
	public MOFitnessComparator(Comparator<IFitness> [] componentComparators) {
		super();
		setComponentComparators(componentComparators);
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Get component fitness comparators.
	 * @return Component fitness comparators
	 */
	public Comparator<IFitness>[] getComponentComparators() {
		return this.componentComparators;
	}

	/**
	 * Set component fitness comparators.
	 * @param componentComparators Component fitness comparators
	 */
	public void setComponentComparators(Comparator<IFitness>[] componentComparators) {
		this.componentComparators = componentComparators;
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Compare to fitness objects.
	 * @param arg0 First fitness
	 * @param arg1 Second fitness
	 * @return Comparison results between fitness objects
	 * @throws IllegalArgumentException
	 * */
	@Override
	public int compare(IFitness arg0, IFitness arg1) {
		MOFitness mofitness0, mofitness1;
		try {
			mofitness0 = (MOFitness) arg0;
		}
		catch(ClassCastException e) {
			throw new IllegalArgumentException("MOFitness expected as first argument");
		}
		try {
			mofitness1 = (MOFitness) arg1;
		}
		catch(ClassCastException e) {
			throw new IllegalArgumentException("MOFitness expected as second argument");
		}
		return compare(mofitness0, mofitness1);
	}


	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Abstract methods
	/////////////////////////////////////////////////////////////////
	
	/**
	 * Compare two fitness objects.
	 * @param mofitness0 First fitness.
	 * @param mofitness1 Second fitness.
	 * @return Comparison result for the given fitness objects: 1 if the first fitness is better than
	 * second fitness, 0 if both are equivalent, -1 otherwise.
	 * */
	protected abstract int compare(MOFitness mofitness0, MOFitness mofitness1);
}
