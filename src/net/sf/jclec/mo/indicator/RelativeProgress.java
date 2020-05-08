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
 * The Progress indicator measures the relative convergence improvement in terms
 * of generational distance. It requires two pareto fronts, one representing the
 * set of solutions at the first generation and the other containing the set
 * of solution at a generic generation, t. The convergence is measured in terms
 * of the achieved generational distance (GD).
 * 
 * <p><i>Book:</i> C.A. Coello Coello, D.A. Van Veldhuizen, G.B. Lamont.
 * "Evolutionary Algorithms for Solving Multi-Objective Problems". 1st edition.
 * Kluwer Academic Publ. 2002.</p>
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
 * @see TernaryIndicator
 * @see GenerationalDistance
 * */

public class RelativeProgress extends TernaryIndicator {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -2394543538633990189L;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public RelativeProgress(){
		super();
		this.maximized=null;
		this.scaled=true;
	}

	/**
	 * Parameterized constructor.
	 * @param paretoSet The first Pareto set approximation.
	 * @param paretoSet2 The second Pareto set approximation.
	 * @param trueParetoSet The true Pareto set.
	 * */
	public RelativeProgress(List<IIndividual> paretoSet, List<IIndividual> paretoSet2, List<IIndividual> trueParetoSet){
		super(paretoSet, paretoSet2, trueParetoSet, null, true);
	}

	/**
	 * Parameterized constructor.
	 * @param paretoSet The first Pareto set approximation.
	 * @param paretoSet2 The second Pareto set approximation.
	 * @param trueParetoFrontFileName Name of the file that contains the true Pareto front.
	 * */
	public RelativeProgress(List<IIndividual> paretoSet, List<IIndividual> paretoSet2, String trueParetoFrontFileName){
		super(paretoSet, paretoSet2, trueParetoFrontFileName, null, true);
	}

	/**
	 * Parameterized constructor.
	 * @param paretoSet The Pareto set approximation.
	 * @param paretoFront2FileName Name of the file that contains the second Pareto front required.
	 * @param trueParetoSet The true Pareto set.
	 * */
	public RelativeProgress(List<IIndividual> paretoSet, String paretoFront2FileName, List<IIndividual> trueParetoSet){
		super(paretoSet, paretoFront2FileName, trueParetoSet, null, true);
	}

	/**
	 * Parameterized constructor.
	 * @param paretoSet The Pareto set approximation.
	 * @param paretoFront2FileName Name of the file that contains the second Pareto front required.
	 * @param trueParetoFrontFileName Name of the file that contains the true Pareto front.
	 * */
	public RelativeProgress(List<IIndividual> paretoSet, String paretoFront2FileName, String trueParetoFrontFileName){
		super(paretoSet, paretoFront2FileName, trueParetoFrontFileName, null, true);
	}

	/**
	 * Parameterized constructor.
	 * @param paretoFrontFileName Name of the file that contains the first Pareto front approximation.
	 * @param paretoSet2 The second Pareto set approximation.
	 * @param trueParetoSet The true Pareto set.
	 * */
	public RelativeProgress(String paretoFrontFileName, List<IIndividual> paretoSet2, List<IIndividual> trueParetoSet){
		super(paretoFrontFileName, paretoSet2, trueParetoSet, null, true);
	}

	/**
	 * Parameterized constructor.
	 * @param paretoFrontFileName Name of the file that contains the first Pareto front approximation.
	 * @param paretoSet2 The second Pareto set approximation.
	 * @param trueParetoFrontFileName Name of the file that contains the true Pareto front.
	 * */
	public RelativeProgress(String paretoFrontFileName, List<IIndividual> paretoSet2, String trueParetoFrontFileName){
		super(paretoFrontFileName, paretoSet2, trueParetoFrontFileName, null, true);
	}

	/**
	 * Parameterized constructor.
	 * @param paretoFrontFileName Name of the file that contains the first Pareto front approximation.
	 * @param paretoFront2FileName Name of the file that contains the second Pareto front required.
	 * @param trueParetoSet The true Pareto set.
	 * */
	public RelativeProgress(String paretoFrontFileName, String paretoFront2FileName, List<IIndividual> trueParetoSet){
		super(paretoFrontFileName, paretoFront2FileName, trueParetoSet, null, true);
	}

	/**
	 * Parameterized constructor.
	 * @param paretoFrontFileName Name of the file that contains the first Pareto front approximation.
	 * @param paretoFront2FileName Name of the file that contains the second Pareto front required.
	 * @param trueParetoFrontFileName Name of the file that contains the true Pareto front.
	 * */
	public RelativeProgress(String paretoFrontFileName, String paretoFront2FileName, String trueParetoFrontFileName){
		super(paretoFrontFileName, paretoFront2FileName, trueParetoFrontFileName, null, true);
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void calculate() {

		if(getFront()!=null && getSecondFront()!= null && getTrueFront() != null){
			GenerationalDistance gd = new GenerationalDistance();
			// Compute GD for the first front
			gd.setFront(this.getFront());
			gd.setSecondFront(this.getTrueFront());
			gd.calculate();
			double g_1 = gd.getResult();
			// Compute GD for the second front
			gd.setFront(this.secondFront);
			gd.calculate();
			double g_t = gd.getResult();
			if(g_t != 0.0 && g_t != -1.0 && g_1 != -1.0)
				this.result = Math.log(g_1/g_t);
			else if (g_t==0.0 && g_1==0.0){
				this.result = 0.0;
			}
		}
	}
}
