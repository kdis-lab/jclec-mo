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

package net.sf.jclec.mo.strategy.util;

import java.util.Comparator;

/**
 * Auxiliary class for MOEA/D algorithm that implements a comparator of neighbors.
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
 * @see MOEAD
 * @see MOEADNeighbor
 * */
public class MOEADNeighborsComparator implements Comparator<MOEADNeighbor>{

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////
	
	/**
	 * Compare two problem neighbors. The one having the greater
	 * distance is the best.
	 * @param arg0 First problem
	 * @param arg1 Second problem
	 * @return Comparison results based on distances
	 * */
	@Override
	public int compare(MOEADNeighbor arg0, MOEADNeighbor arg1) {
		if(arg0.getDistance() > arg1.getDistance())
			return 1;
		else if(arg0.getDistance() < arg1.getDistance())
			return -1;
		else
			return 0;
	}
}
