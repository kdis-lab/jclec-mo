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

import net.sf.jclec.JCLEC;

/**
 * Auxiliary class for MOEA/D algorithm. This class represents the neighborhood 
 * properties of an individual.
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
 * */
public class MOEADNeighbor implements JCLEC{

	//////////////////////////////////////////////////////////////////
	//----------------------------------------------------- Properties
	//////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -2728641742116503993L;

	/** Neighbor identifier */
	private int id;

	/** Distance to the neighbor */
	private double distance;

	//////////////////////////////////////////////////////////////////
	//--------------------------------------------------- Constructors
	//////////////////////////////////////////////////////////////////

	/**
	 * Parameterized constructor.
	 * @param id Neighbor identifier
	 * @param distance Distance to the neighbor.
	 * */
	public MOEADNeighbor(int id, double distance) {
		this.id = id;
		this.distance = distance;
	}

	//////////////////////////////////////////////////////////////////
	//------------------------------------------------ Get/set methods
	//////////////////////////////////////////////////////////////////

	/**
	 * Get the neighbor identifier.
	 * @return the identifier.
	 * */
	public int getId() {
		return this.id;
	}

	/**
	 * Get the neighbor distance.
	 * @return the distance to the neighbor.
	 * */
	public double getDistance() {
		return this.distance;
	}
}