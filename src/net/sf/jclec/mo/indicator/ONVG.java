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
 * The Overall Nondominated Vector Generation (ONVG) is defined as the total number 
 * of non dominated vector found during the search, i.e. the size of the Pareto front.
 * 
 * <p><i>Paper</i>: D.A. Van Veldhuizen, G.B. Lamont.
 * "Multiobjective Evolutionary Algorithm Test Suites"
 * Proc. ACM Symposium on Applied Computing, pp.351-357. 1999.</p>
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
 * @see UnaryIndicator
 * */
public class ONVG extends UnaryIndicator {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 7657850987316235055L;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public ONVG(){
		super();
		this.maximized = null;
		this.scaled = null;
	}
	
	/**
	 * Parameterized constructor.
	 * @param paretoSet The Pareto set approximation
	 * */
	public ONVG(List<IIndividual> paretoSet) {
		super(paretoSet, null, null);
	}

	/**
	 * Parameterized constructor.
	 * @param paretoFrontFileName The name of the file that contains the Pareto front approximation
	 * */
	public ONVG(String paretoFrontFileName) {
		super(paretoFrontFileName, false, false);
	}

	/////////////////////////////////////////////////////////////////
	//---------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void calculate() {
		double onvg = getNumberOfSolutions();
		this.setResult(onvg);
	}
}
