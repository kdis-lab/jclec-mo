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

import net.sf.jclec.mo.evaluation.fitness.MOFitness;

/**
 * Compare two <code>MOFitness</code> objects based on the fitness value.
 * By default, this comparator considers a maximization problem, i.e. greater fitness
 * values are preferred, although minimization problems can be also considered setting the inversion flag.
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
 * @see MOFitnessComparator
 * */

public class MOValueFitnessComparator extends MOFitnessComparator {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** 
	 * Inversion flag. If this property is set to <code>true</code>, 
	 * smaller fitness values are preferred. */
	protected boolean inverse;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor. It assigns <code>false</code>
	 * to the inversion flag.
	 * */
	public MOValueFitnessComparator(){
		super();
		this.inverse = false;
	}
	
	/**
	 * Parameterized constructor.
	 * @param inverse The inverse flag value to be set.
	 */
	public MOValueFitnessComparator(boolean inverse){
		super();
		this.inverse = inverse;
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Get the value of the inversion flag.
	 * @return Inverse flag value.
	 */
	public boolean isInverse() {
		return inverse;
	}

	/**
	 * Set the value of the inversion flag.
	 * @param inverse Value that has to be set.
	 */
	public void setInverse(boolean inverse) {
		this.inverse = inverse;
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>Here, the comparison is made in terms
	 * of the fitness value.
	 * */
	@Override
	protected int compare(MOFitness mofitness0, MOFitness mofitness1) {

		double fvalue0 = mofitness0.getValue();
		double fvalue1 = mofitness1.getValue();
		
		if (fvalue0 > fvalue1) {
			if(this.inverse)
				return -1;
			else
				return 1;
		}
		
		else if(fvalue0 < fvalue1) {
			if(this.inverse)
				return 1;
			else
				return -1;
		}
		
		else {
			return 0;
		}
	}
}
