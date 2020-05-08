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
 * The R3 indicator compares the quality of the Pareto front approximation and
 * the true Pareto front in terms of the proximity to a reference point. It requires 
 * a maximization problem, whereas objective values in the range [0,1] are recommended
 * to compute distances.
 * 
 * <p><i>Paper</i>: M.P. Hansen, A. Jaszkiewicz. "Evaluating the quality of
 * approximations to the non-dominated set". Tech. Report IMM-REP-1198-7.
 * University of Denmark. 1998.</p>
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
 * @see R2
 * */
public class R3 extends R2 {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 3093293727034313071L;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor
	 * */
	public R3(){
		super();
	}
	
	/**
	 * Parameterized constructor. 
	 * @param paretoSet The first Pareto set.
	 * @param paretoSet2 The second Pareto set.
	 * @param refPoint The reference point.
	 * @param h The controller of the number of scalarizing vectors.
	 * */
	public R3(List<IIndividual> paretoSet, List<IIndividual> paretoSet2, double [] refPoint, int h){
		super(paretoSet, paretoSet2, refPoint, h);
	}

	/**
	 * Parameterized constructor.
	 * @param paretoSet The first Pareto set.
	 * @param paretoFront2FileName Name of the file that contains the second Pareto front.
	 * @param refPoint The reference point.
	 * @param h The controller of the number of scalarizing vectors.
	 * */
	public R3(List<IIndividual> paretoSet, String paretoFront2FileName, double [] refPoint, int h){
		super(paretoSet, paretoFront2FileName, refPoint, h);
	}

	/**
	 * Parameterized constructor.
	 * @param paretoFrontFileName Name of the file that contains the first Pareto front.
	 * @param paretoSet2 The second Pareto set.
	 * @param refPoint The reference point.
	 * @param h The controller of the number of scalarizing vectors.
	 * */
	public R3(String paretoFrontFileName, List<IIndividual> paretoSet2, double [] refPoint, int h){
		super(paretoFrontFileName, paretoSet2, refPoint, h);
	}

	/**
	 * Parameterized constructor.
	 * @param paretoFrontFileName Name of the file that contains the first Pareto front.
	 * @param paretoFront2FileName Name of the file that contains the second Pareto front.
	 * @param refPoint The reference point.
	 * @param h The controller of the number of scalarizing vectors.
	 * */
	public R3(String paretoFrontFileName, String paretoFront2FileName, double [] refPoint, int h){
		super(paretoFrontFileName, paretoFront2FileName, refPoint, h);
	}

	//////////////////////////////////////////////////////////////////
	//----------------------------------------------- Override methods
	//////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void calculate() {
		int nVectors = this.lambda.length;
		double r3 = 0.0;
		
		// Compute the utility functions
		computeUtilityFunctions();
		
		// Compute the indicator
		for(int i=0; i<nVectors; i++){
			if(this.utilityB[i]!=0)
				r3+=(this.utilityB[i]-this.utilityA[i])/this.utilityB[i];
			else
				r3+=(this.utilityB[i]-this.utilityA[i]/10e-20);
		}
		r3 = r3/nVectors;
		this.setResult(r3);
	}
}
