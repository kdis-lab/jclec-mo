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

package net.sf.jclec.mo.indicator;

import java.util.List;
import net.sf.jclec.IIndividual;

/**
 * Abstract unary indicator. This kind of measure evaluates the quality of 
 * a Pareto front without requiring the true Pareto front.
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
 * @see Indicator
 * */

public abstract class UnaryIndicator extends Indicator {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 1504107898424605273L;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public UnaryIndicator(){
		super();
	}
	
	/**
	 * Parameterized constructor.
	 * @param paretoSet The Pareto set approximation.
	 * @param maximized Maximization problem is required?
	 * @param scaled Scaled objective values are required?
	 * */
	public UnaryIndicator(List<IIndividual> paretoSet, Boolean maximized, Boolean scaled){
		super(paretoSet, maximized, scaled);
	}

	/**
	 * Parameterized constructor.
	 * @param paretoFrontFileName Name of the file that contains the Pareto front approximation.
	 * @param maximized Maximization problem is required?
	 * @param scaled Scaled objective values are required?
	 * */
	public UnaryIndicator(String paretoFrontFileName, Boolean maximized, Boolean scaled){
		super(paretoFrontFileName,  maximized, scaled);
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Abstract methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public abstract void calculate();
}
