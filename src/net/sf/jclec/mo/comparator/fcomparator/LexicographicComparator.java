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
 * Compare two <code>MOFitness</code> objects in lexicographic order.
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

public class LexicographicComparator extends MOFitnessComparator{

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor
	 * */
	public LexicographicComparator(){
		super();
	}
	
	/**
	 * Parameterized constructor.
	 * @param componentComparators Component fitness comparators
	 * */
	public LexicographicComparator(Comparator<IFitness>[] componentComparators) {
		super(componentComparators);
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>Here, objective values are compared in order.
	 */
	@Override
	public int compare(MOFitness mofitness0, MOFitness mofitness1) {
		
		// Fitness components
		IFitness [] fonec = mofitness0.getObjectiveValues();
		IFitness [] fothc = mofitness1.getObjectiveValues();
		int nc = fonec.length;
		
		// Compare lexicographically
		for (int i=0; i<nc; i++) {
			int cmp = componentComparators[i].compare(fonec[i] , fothc[i]);
			if (cmp != 0)
				return cmp;
		}
		
		// All fitness components are equivalent
		return 0;
	}
}
