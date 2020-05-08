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
 * Auxiliary class for GrEA algorithm. This class represents the grid properties
 * of an objective (lower and upper boundaries and width) divided in grids.
 * 
 * <p>HISTORY:
 * <ul>
 *	<li>(AR|JRR|SV, 1.0, March 2018)		First release.</li>
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
 * @see GrEA
 * */

public class GridObjectiveProperties implements JCLEC{

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -48199289586172294L;

	/** Lower bound */
	private double lowerBound;

	/** Upper bound */
	private double upperBound;

	/** Width */
	private double width;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////
	
	/**
	 * Parameterized constructor.
	 * @param lowerBound Lower bound.
	 * @param upperBound Upper bound.
	 * @param width Width of the grids.
	 * */
	public GridObjectiveProperties(double lowerBound, double upperBound, double width) {
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.width = width;
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/Set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Get the lower bound.
	 * @return Lower bound of the objective.
	 * */
	public double getLowerBound() {
		return this.lowerBound;
	}

	/**
	 * Set the lower bound.
	 * @param lowerBound New lower bound.
	 * */
	public void setLowerBound(double lowerBound) {
		this.lowerBound = lowerBound;
	}

	/**
	 * Get the upper bound.
	 * @return Upper bound of the objective.
	 * */
	public double getUpperBound() {
		return this.upperBound;
	}

	/**
	 * Set the upper bound.
	 * @param upperBound New upper bound.
	 * */
	public void setUpperBound(double upperBound) {
		this.upperBound = upperBound;
	}

	/**
	 * Get the grid width.
	 * @return Grid width.
	 * */
	public double getWidth() {
		return this.width;
	}

	/**
	 * Set the grid width.
	 * @param width New grid width.
	 * */
	public void setWidth(double width) {
		this.width = width;
	}
}
