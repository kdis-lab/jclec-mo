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

import org.apache.commons.configuration.Configuration;

import net.sf.jclec.IIndividual;

/**
 * The Two Set Coverage CS(A,B), defines the ratio of the number of solutions 
 * in front B that are dominated by solutions in front A to the number of 
 * solutions in B. This metric is also called Coverage, C(A,B).
 * 
 * <p><i>Paper</i>: E. Zitzler, K. Deb, L. Thiele. "Comparison of
 * Multiobjective Evolutionary Algorithms: Empirical Results".
 * Evolutionary Computation. vol. 8. no.2. pp. 173-195. 2000.</p> 
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
public class TwoSetCoverage extends BinaryIndicator {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -6176258990258205976L;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor. Maximization problem is assumed.
	 * */
	public TwoSetCoverage(){
		super();
		this.maximized=true;
		this.scaled=false;
	}

	/**
	 * Parameterized constructor.
	 * @param maximized Maximization problem flag.
	 * */
	public TwoSetCoverage(boolean maximized){
		super();
		this.maximized=maximized;
		this.scaled=false;
	}

	/**
	 * Parameterized constructor.
	 * @param paretoSet The first Pareto set (A)
	 * @param paretoSet2 The second Pareto set (B)
	 * @param maximized Maximization problem flag.
	 * */
	public TwoSetCoverage(List<IIndividual> paretoSet, List<IIndividual> paretoSet2, boolean maximized) {
		super(paretoSet, paretoSet2, maximized, false);
	}

	/**
	 * Parameterized constructor.
	 * @param paretoSet The first Pareto set (A)
	 * @param paretoFront2FileName Name of the file that contains the second Pareto front (B)
	 * @param maximized Maximization problem flag.
	 * */
	public TwoSetCoverage(List<IIndividual> paretoSet, String paretoFront2FileName, boolean maximized) {
		super(paretoSet, paretoFront2FileName, maximized, false);
	}

	/**
	 * Parameterized constructor.
	 * @param paretoFrontFileName Name of the file that contains the first Pareto front (A)
	 * @param paretoSet2 The second Pareto set (B)
	 * @param maximized Maximization problem flag.
	 * */
	public TwoSetCoverage(String paretoFrontFileName, List<IIndividual> paretoSet2, boolean maximized) {
		super(paretoFrontFileName, paretoSet2, maximized, false);
	}

	/**
	 * Parameterized constructor.
	 * @param paretoFrontFileNameName of the file that contains the first Pareto front (A).
	 * @param paretoFront2FileName Name of the file that contains the second Pareto front (B).
	 * @param maximized Maximization problem flag.
	 * */
	public TwoSetCoverage(String paretoSet1FileName, String paretoSet2FileName, boolean maximized) {
		super(paretoSet1FileName, paretoSet2FileName, maximized, false);
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Set the maximization flag. Null values will not set.
	 * @param maximized The value that has to be set.
	 * */
	public void setMaximize(Boolean maximized){
		if(maximized!=null)
			this.maximized = maximized;
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>This indicator requires the following parameters:
	 * <ul>
	 * 	<li>max (<code>Boolean</code>):
	 *  <p>Indicates whether the problem should be maximized. True by default.</p></li>
	 * </ul>
	 * </p>
	 * */
	@Override
	public void configure(Configuration settings){
		super.configure(settings);
		Boolean max = settings.getBoolean("max", true);
		this.maximized = max;
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void calculate() {
		int nDominated = 0;
		double [] solutionA;
		double [] solutionB;
		double tsc;
		int nSolutionsA = getNumberOfSolutions();
		int nSolutionsB = getNumberOfSolutionsSecondFront();
		boolean exit;

		// Empty fronts, return -1
		if(nSolutionsA == 0 || nSolutionsB == 0){
			tsc = -1.0;
		}

		// Check if both fronts contain the same points
		else if(equalFronts()){
			tsc = 1.0; // by definition
		}
		else{

			for(int i=0; i<nSolutionsB; i++){
				solutionB = getSolutionSecondFront(i);
				exit = false;

				// Check if the solution in A dominates the solution in B
				for(int j=0; !exit && j<nSolutionsA; j++){
					solutionA = getSolutionFront(j);
					if(dominates(solutionA,solutionB)){
						nDominated++;
						exit=true;
					}
				}
			}
			tsc = (double)nDominated / (double)nSolutionsB;
		}
		setResult(tsc);
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Private methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Check if the two fronts contains the same points.
	 * @return True if the two points are equal, false otherwise.
	 * */
	private boolean equalFronts(){
		boolean equals=true;
		int nSolutionsA = getNumberOfSolutions();
		int nSolutionsB = getNumberOfSolutionsSecondFront();
		boolean hasSolution;
		if(nSolutionsA==nSolutionsB){
			for(int i=0; equals && i<nSolutionsA; i++){
				hasSolution=false;
				for(int j=0; !hasSolution && j<nSolutionsB; j++){
					if(equals(getSolutionFront(i),getSolutionSecondFront(j)))
						hasSolution=true;
				}
				if(!hasSolution)
					equals=false;
			}
		}
		else
			equals = false;
		return equals;
	}

	/**
	 * Check if two solutions are equal.
	 * @param solution1 First solution.
	 * @param solution2 Second solution.
	 * @return True if both solutions are the same, false otherwise.
	 * */
	private boolean equals(double [] solution1, double [] solution2){
		boolean equals = true;
		int lenght = solution1.length;
		for(int i=0; equals && i<lenght; i++){
			if(solution1[i]!=solution2[i]){
				equals=false;
			}
		}
		return equals;
	}

	/** 
	 * Check if solution1 dominates solution2.
	 * @param solution1 First solution.
	 * @param solution2 Second solution.
	 * @return True if solution1 dominates solution2, false otherwise.
	 * */
	private boolean  dominates(double [] solution1, double [] solution2) {
		int betterInAnyObjective = 0, i;
		int nObjectives = getNumberOfObjectives();
		if(this.maximized){
			for (i=0; i<nObjectives && solution1[i]>=solution2[i]; i++)
				if (solution1[i] > solution2[i]) {
					betterInAnyObjective = 1;
				}
		}
		else{
			for (i=0; i<nObjectives && solution1[i]>=solution2[i]; i++)
				if (solution1[i] < solution2[i]) {
					betterInAnyObjective = 1;
				}
		}
		return ((i >= nObjectives) && (betterInAnyObjective>0));
	}
}