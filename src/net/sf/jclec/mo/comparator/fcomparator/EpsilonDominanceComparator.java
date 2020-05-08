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
import net.sf.jclec.mo.evaluation.fitness.IHypercubeMOFitness;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;

/**
 * Compare two <code>MOFitness<code> objects in terms of e-dominance. The fitness objects
 * should implement the <code>IHypercubeMOFitness</code> interface.
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
 * @see ParetoComparator
 * @see IHypercubeMOFitness
 * */
public class EpsilonDominanceComparator extends ParetoComparator {

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public EpsilonDominanceComparator() {
		super();
	}

	/**
	 * Parameterized constructor.
	 * @param componentComparators Component fitness comparators.
	 * */
	public EpsilonDominanceComparator(Comparator<IFitness>[] componentComparators) {
		super(componentComparators);
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>Here, the hypercube is extracted from the fitness object in
	 * order to check the e-dominance.</p>
	 */
	@Override
	public int compare(MOFitness mofitness0, MOFitness mofitness1) {
		// Extract the hypercubes
		IFitness [] cube0 = ((IHypercubeMOFitness)mofitness0).getHypercube().getValues();
		IFitness [] cube1 = ((IHypercubeMOFitness)mofitness1).getHypercube().getValues();
		int nc = cube0.length; // Number of components
		int result = 0;
		// Compare hypercube values
		for (int i=0; i<nc; i++) {
			int cmp = this.componentComparators[i].compare(cube0[i], cube1[i]);
			if (result == 0) {
				result = cmp;
			}
			else {
				if (cmp != 0 & cmp != result) 
					return 0;
			}	
		}
		// Return result
		return result;
	}
}
