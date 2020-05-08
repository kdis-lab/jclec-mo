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
 * The Inverted Generational Distance (IGD) measures how
 * far the true Pareto front is from the Pareto front. 
 * Objective values in the range [0,1] are recommended to compute distances.
 * 
 * <p><i>Paper</i>: C.A. Coello Coello, N. Cruz Cortes.
 * "Solving Multiobjective Optimization Problems using
 * and Artificial Immune System". Genetic Programming and
 * Evolvable Machines. vol. 6. no. 2. pp. 163-190. 2005.</p>
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
 * @see GenerationalDistance
 * */

public class InvertedGenerationalDistance extends GenerationalDistance {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 510118341410263228L;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public InvertedGenerationalDistance(){
		super();
	}

	/**
	 * Parameterized constructor.
	 * @param paretoSet The first Pareto set.
	 * @param paretoSet2 The second Pareto set.
	 * */
	public InvertedGenerationalDistance(List<IIndividual> paretoSet, List<IIndividual> paretoSet2){
		super(paretoSet, paretoSet2);
	}

	/**
	 * Parameterized constructor.
	 * @param paretoSet The first Pareto set.
	 * @param paretoFront2FileName Name of the file that contains the second Pareto set
	 * */
	public InvertedGenerationalDistance(List<IIndividual> paretoSet, String paretoFront2FileName){
		super(paretoSet, paretoFront2FileName);
	}

	/**
	 * Parameterized constructor.
	 * @param paretoFrontFileName Name of the file that contains the first Pareto front.
	 * @param paretoSet2 The second Pareto set.
	 * */
	public InvertedGenerationalDistance(String paretoFrontFileName, List<IIndividual> paretoSet2){
		super(paretoFrontFileName, paretoSet2);
	}

	/**
	 * Parameterized constructor.
	 * @param paretoFrontFileName Name of the file that contains the first Pareto front.
	 * @param paretoFront2FileName Name of the file that contains the second Pareto front.
	 * */
	public InvertedGenerationalDistance(String paretoFrontFileName, String trueParetoFront2FileName){
		super(paretoFrontFileName, trueParetoFront2FileName);
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void calculate() {
		
		int nSolutions = getNumberOfSolutions();
		int nSolutionsSecond = getNumberOfSolutionsSecondFront();

		// At least one solution in both fronts
		if(nSolutions>0 && nSolutionsSecond>0){
			double igd = 0.0;
			for(int i=0; i<nSolutionsSecond; i++){
				igd += distanceToFront(getSolutionSecondFront(i));
			}
			igd = Math.pow(igd, 1/(double)this.p) / nSolutions;
			this.setResult(igd);
		}
	}

	/////////////////////////////////////////////////////////////////
	// -------------------------------------------- Protected methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Compute the distance of a solution to
	 * the closest solution of Pareto front.
	 * @param solution The solution if the true front.
	 * @return Minimum distance of the solution from the
	 * Pareto front.
	 * */
	@Override
	protected double distanceToFront(double [] solution){
		double minDistance = Double.POSITIVE_INFINITY, distance;
		int nSolutions = this.getNumberOfSolutions();
		// Distance of the solution to each solution in the Pareto set
		for(int i=0; i<nSolutions; i++){
			distance = distance(solution,getSolutionFront(i));
			if(distance<minDistance)
				minDistance=distance;
		}
		return minDistance;
	}
}
