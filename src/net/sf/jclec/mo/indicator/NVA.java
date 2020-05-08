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
 * The NonDominated Vector Addition (NVA) indicator measures
 * the difference in the number of non dominated solutions
 * between two fronts.
 * 
 * <p><i>Book:</i> C.A. Coello Coello, D.A. Van Veldhuizen, G.B. Lamont.
 * "Evolutionary Algorithms for Solving Multi-Objective Problems". 1st edition.
 * Kluwer Academic Publ. 2002.</p>
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
 * @see BinaryIndicator
 * */
public class NVA extends BinaryIndicator {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -3172348357298675270L;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public NVA() {
		super();
		this.maximized = null;
		this.scaled = null;
	}

	/**
	 * Parameterized constructor.
	 * @param paretoSet The first Pareto set approximation.
	 * @param paretoSet2 The true Pareto set or the second set required.
	 * */
	public NVA(List<IIndividual> paretoSet, List<IIndividual> paretoSet2) {
		super(paretoSet, paretoSet2, null, null);
	}

	/**
	 * Parameterized constructor.
	 * @param paretoSet The Pareto set approximation.
	 * @param paretoFront2FileName Name of the file that contains the true Pareto front or the second front required.
	 * */
	public NVA(List<IIndividual> paretoSet, String paretoFront2FileName) {
		super(paretoSet, paretoFront2FileName, null, null);
	}

	/**
	 * Parameterized constructor.
	 * @param paretoFrontFileName Name of the file that contains the Pareto front approximation.
	 * @param paretoSet2 The true Pareto set or the second set required.
	 * */
	public NVA(String paretoFrontFileName, List<IIndividual> paretoSet2) {
		super(paretoFrontFileName, paretoSet2, null, null);
	}

	/**
	 * Parameterized constructor.
	 * @param paretoFrontFileName Name of the file that contains the Pareto front approximation.
	 * @param paretoFront2FileName Name of the file that contains the true Pareto front or the second front required.
	 * */
	public NVA(String paretoFrontFileName, String paretoFront2FileName) {
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
		if(getFront() != null && getSecondFront() !=null){
			double result = getNumberOfSolutions() - getNumberOfSolutionsSecondFront();
			setResult(result);
		}
	}
}
