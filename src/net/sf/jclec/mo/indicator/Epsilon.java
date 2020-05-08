/*
This file belongs to JCLEC-MOEA, a Java library for the
application and development of metaheuristic algorithms 
for the resolution of multi-objective and many-objective 
optimization problems.

Copyright (C) 2018.  A. Ramírez, J.R. Romero, S. Ventura
Knowledge Discovery and Intelligent Systems Research Group

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

package net.sf.jclec.mo.indicator;

import java.util.List;

import net.sf.jclec.IIndividual;

/**
 * The Epsilon indicator (I_e) obtains the minimum 'epsilon' value, i.e. 
 * the factor by which a Pareto set is worse than other Pareto set. 
 * Objective values in the range [0,1] are recommended to compute distances.
 * 
 * <p><i>Paper</i>: E. Ziztler, L. Thiele, M. Laumanns, C.M. Fonseca,
 * V.G. da Fonseca. “Performance Assessment of Multiobjective Optimizers:
 * An Analysis and Review”. IEEE Transactions on Evolutionary Computation, 
 * vol. 7, no. 2, pp. 117–132, 2003.</p>
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
public class Epsilon extends BinaryIndicator {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 8309658841989321483L;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public Epsilon(){
		super();
		this.maximized=true;
		this.scaled=true;
	}

	/**
	 * Parameterized constructor.
	 * @param paretoSet1 The first Pareto set.
	 * @param paretoSet2 The second Pareto set.
	 * */
	public Epsilon(List<IIndividual> paretoSet1, List<IIndividual> paretoSet2) {
		super(paretoSet1, paretoSet2, true, true);
	}

	/**
	 * Parameterized constructor.
	 * @param paretoSet1 The first Pareto set.
	 * @param paretoFront2FileName Name of the file that contains the second Pareto front.
	 * */
	public Epsilon(List<IIndividual> paretoSet1, String paretoFront2FileName) {
		super(paretoSet1, paretoFront2FileName, true, true);
	}

	/**
	 * Parameterized constructor.
	 * @param paretoFront1FileName Name of the file that contains the first Pareto front.
	 * @param paretoSet2 The second Pareto set.
	 * */
	public Epsilon(String paretoSet1FileName, List<IIndividual> paretoSet2) {
		super(paretoSet1FileName, paretoSet2, true, true);
	}

	/**
	 * Parameterized constructor.
	 * @param paretoFront1FileName Name of the file that contains the first Pareto front.
	 * @param paretoFront2FileName Name of the file that contains the second Pareto front.
	 * */
	public Epsilon(String paretoFront1FileName, String paretoFront2FileName) {
		super(paretoFront1FileName, paretoFront2FileName, true, true);
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void calculate() {
		double epsilonValue;
		int nSolutions1 = getNumberOfSolutions();
		int nSolutions2 = getNumberOfSolutionsSecondFront();
		double [] solution1;
		double [] solution2;
		double [] epsilon = new double[nSolutions1];

		// At least one solution in each front
		if(nSolutions1>0 && nSolutions2>0){

			// Compute epsilon values
			for(int i=0; i<nSolutions1; i++){
				// Get a solution in the second front
				solution1 = getSolutionFront(i);
				epsilon[i] = Double.POSITIVE_INFINITY;

				// Compute the minimum epsilon value considering
				// all the solutions in the first front
				for(int j=0; j<nSolutions2; j++){
					solution2 = getSolutionSecondFront(j);
					epsilonValue = maxEpsilonValue(solution1, solution2);
					if(epsilonValue < epsilon[i])
						epsilon[i] = epsilonValue;
				}
			}

			// Compute the indicator: the maximum epsilon value
			epsilonValue = epsilon[0];
			for(int i=1; i<nSolutions1; i++){
				if(epsilon[i]>epsilonValue)
					epsilonValue = epsilon[i];
			}
			setResult(epsilonValue);
		}
	}

	/**
	 * Maximum epsilon value. It is the maximum factor
	 * by which solution1 is worse than solution2
	 * considering all the objectives.
	 * @param solution1 First solution.
	 * @param solution2 Second solution.
	 * @return Epsilon factor.
	 * */
	protected double maxEpsilonValue(double [] solution1, double [] solution2){
		double maxValue = Double.NEGATIVE_INFINITY;
		int nObjectives = solution1.length;
		double epsilon;

		for(int i=0; i<nObjectives; i++){
			if(solution1[i]!=0.0){
				epsilon = solution2[i]/solution1[i];
				if(epsilon>maxValue){
					maxValue = epsilon;
				}
			}
		}
		return maxValue;
	}
}