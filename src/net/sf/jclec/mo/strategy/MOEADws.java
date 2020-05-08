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

package net.sf.jclec.mo.strategy;

import net.sf.jclec.IIndividual;
import net.sf.jclec.mo.IMOEvaluator;
import net.sf.jclec.mo.comparator.MOSolutionComparator;
import net.sf.jclec.mo.comparator.fcomparator.MOValueFitnessComparator;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;

/**
 * MOEA/D strategy with the <i>Weigthed Sum</i> approach.
 * This class implements a weighted sum of the objective values 
 * as fitness assignment technique. 
 * 
 * <p><i>Paper</i>: Q. Zhang and H. Li, “MOEA/D: A Multiobjective Evolutionary 
 * Algorithm Based on Decomposition”, IEEE Transactions on Evolutionary Computation,
 * vol. 11, no. 6, pp. 712–731, 2007.</p>
 * 
 * <p>HISTORY:
 * <ul>
 *  <li>(AR|JRR|SV, 1.0, March 2018)		First release.</li>
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
 * @see MOEAD
 * */
public class MOEADws extends MOEAD {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -3480584365606547357L;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public MOEADws(){
		super();
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>Here, the fitness function calculates the weighted
	 * sum of the objective values.</p>
	 * */
	@Override
	protected double fitnessFunction(IIndividual individual, double[] lambda) {
		MOFitness fitness = (MOFitness)individual.getFitness();
		double result = 0.0;
		int numObj = ((IMOEvaluator)getContext().getEvaluator()).numberOfObjectives();
		for(int j=0; j<numObj; j++){
			try {
				result += lambda[j]*fitness.getObjectiveDoubleValue(j);
			} catch (IllegalAccessException | IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * {@inheritDoc}
	 * <p>Here, the fitness function should be minimized for minimization problems,
	 * whilst it should be maximized for maximization problems.</p>
	 * */
	@Override
	protected MOSolutionComparator createFitnessFunctionComparator() {
		// Set the inverse flag according to the type of problem
		MOValueFitnessComparator fcomparator = new MOValueFitnessComparator(!isMaximized());
		MOSolutionComparator comparator = new MOSolutionComparator(fcomparator);
		return comparator;
	}
}
