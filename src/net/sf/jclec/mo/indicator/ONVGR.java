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
 * The Overall Nondominated Vector Generation Ratio (ONVGR)
 * is defined as the ratio of the total number of non dominated
 * vectors in the front to the number of vectors in the second front.
 * 
 * <p><i>Paper</i>: D.A. Van Veldhuizen, G.B. Lamont.
 * "Multiobjective Evolutionary Algorithm Test Suites"
 * Proc. ACM Symposium on Applied Computing, pp.351-357. 1999.</p>
 * 
 * <p>HISTORY:
 * <ul>
 * <li>(AR|JRR|SV, 1.0, March 2018)		First release.</li>
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
 * @see BinaryIndicator
 * */
public class ONVGR extends BinaryIndicator {

	/////////////////////////////////////////////////////////////////
	// -------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -8474045240865372248L;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public ONVGR(){
		super();
		this.maximized=null;
		this.scaled=null;
	}
	
	/**
	 * Parameterized constructor.
	 * @param paretoSet The first Pareto set.
	 * @param paretoSet2 The second Pareto set.
	 * */
	public ONVGR(List<IIndividual> paretoSet, List<IIndividual> paretoSet2) {
		super(paretoSet, paretoSet2, null, null);
	}

	/**
	 * Parameterized constructor.
	 * @param paretoSet The first Pareto set.
	 * @param paretoFront2FileName Name of the file that contains the second Pareto front.
	 * */
	public ONVGR(List<IIndividual> paretoSet, String paretoFront2FileName) {
		super(paretoSet, paretoFront2FileName, null, null);
	}

	/**
	 * Parameterized constructor.
	 * @param paretoFrontFileName Name of the file that contains the first Pareto front.
	 * @param paretoSet2 The second Pareto set.
	 * */
	public ONVGR(String paretoFrontFileName, List<IIndividual> paretoSet2) {
		super(paretoFrontFileName, paretoSet2, null, null);
	}

	/**
	 * Parameterized constructor.
	 * @param paretoFrontFileName Name of the file that contains the first Pareto front.
	 * @param paretoFront2FileName Name of the file that contains the second Pareto front.
	 * */
	public ONVGR(String paretoFrontFileName, String paretoFront2FileName) {
		super(paretoFrontFileName, paretoFront2FileName, null, null);
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////
	
	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void calculate() {
		double nSolutions1 = (double)getNumberOfSolutions();
		double nSolutions2 = (double)getNumberOfSolutionsSecondFront();
		if(nSolutions2!=0)
			this.setResult(nSolutions1/nSolutions2);
	}
}
