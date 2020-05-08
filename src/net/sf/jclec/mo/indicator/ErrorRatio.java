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
 * The Error Ratio (ER) indicates the number of vectors in the 
 * Pareto front that are not members of the second Pareto front.
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
 * @see BinaryIndicator
 * */
public class ErrorRatio extends BinaryIndicator {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -7668053261540764460L;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public ErrorRatio(){
		super();
		this.maximized=null;
		this.scaled=null;
	}

	/**
	 * Parameterized constructor.
	 * @param paretoSet The firest Pareto set.
	 * @param paretoSet2 The second Pareto set.
	 * */
	public ErrorRatio(List<IIndividual> paretoSet, List<IIndividual> paretoSet2){
		super(paretoSet, paretoSet2, null, null);
	}

	/**
	 * Parameterized constructor.
	 * @param paretoSetFileName The first Pareto set.
	 * @param paretoSet2FileName Name of the file that contains the second Pareto front.
	 * */
	public ErrorRatio(List<IIndividual> paretoSet, String paretoSet2FileName){
		super(paretoSet, paretoSet2FileName, null, null);
	}
	
	//////////////////////////////////////////////////////////////////
	//----------------------------------------------- Override methods
	//////////////////////////////////////////////////////////////////
	
	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void calculate() {
		double er = 0.0;
		int nSolutions = getNumberOfSolutions();
		int nSolutions2 = getNumberOfSolutionsSecondFront();
		
		// Check special conditions
		if(nSolutions==0 || nSolutions2==0){
			this.setResult(-1.0);
		}
		
		// For each solution, check if it belongs to the second Pareto front
		else{
			for(int i=0; i<nSolutions; i++){
				if(!isMember(getSolutionFront(i)))
					er++;
			}

			// Compute the ratio and save result
			er = er / nSolutions;
			this.setResult(er);	
		}
	}

	/**
	 * Check if a solution is member of the second Pareto front.
	 * @param solution The solution (its objective values).
	 * @return True if the solution belongs to the second Pareto front, false otherwise.
	 * */
	private boolean isMember(double [] solution){
		boolean isMember = false;
		int nSolutions = getNumberOfSolutionsSecondFront();
		int nObjectives = getNumberOfObjectives();
		int nEqualObjs;
		double [] solution2;

		// Compare the solution with each solution in the true front
		for(int i=0; !isMember && i<nSolutions; i++){
			solution2 = getSolutionSecondFront(i);
			nEqualObjs = 0;
			for(int j=0; nEqualObjs>=0 && j<nObjectives; j++){
				if(solution[j]==solution2[j])
					nEqualObjs++;
				else
					nEqualObjs = -1;
			}
			if(nEqualObjs==nObjectives)
				isMember=true;
		}
		return isMember;
	}
}
