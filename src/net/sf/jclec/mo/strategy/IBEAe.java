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
import net.sf.jclec.mo.evaluation.fitness.MOFitness;

/**
 * IBEA implementation using epsilon indicator. This quality
 * indicator gives the minimum distance by which a solution 
 * needs to be translated in each dimension such that the 
 * another solution is weakly dominated.
 * 
 * <p>This version modifies the fitness values scaling with 
 * the objective bounds in the current population. Therefore, 
 * the indicator always lies in the interval [-1,1].</p>
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
 * @see IBEA
 * */
public class IBEAe extends IBEA {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -2748073967335861834L;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public IBEAe(){
		super();
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>IBEAe computes the epsilon-indicator
	 * between the given individuals. This
	 * indicator calculates the maximum distance
	 * by which individual <code>ind0</code> must
	 * be decreased (minimization problem) or
	 * in an objective such that the individual <code>ind1</code>
	 * is weakly dominated.</p>
	 * */
	@Override
	protected double computeIndicator(IIndividual ind0, IIndividual ind1) {

		double width;
		double epsilon = 0.0;
		double aux = 0.0;
		int nObjectives = ((IMOEvaluator)getContext().getEvaluator()).numberOfObjectives();
		
		// Initialize
		MOFitness fitness0 = (MOFitness)ind0.getFitness();
		MOFitness fitness1 = (MOFitness)ind1.getFitness();

		try {		
			for(int i=0; i<nObjectives; i++){
				width = this.maxBounds[i] - this.minBounds[i];
				if(width!=0)
					aux = ((fitness0.getObjectiveDoubleValue(i) - this.minBounds[i])/width) -
					((fitness1.getObjectiveDoubleValue(i) - this.minBounds[i])/width);
				if(aux>epsilon){
					epsilon = aux;
				}
			}

		} catch (IllegalAccessException | IllegalArgumentException e) {
			e.printStackTrace();
			epsilon=-2.0;//invalid value
		}
		return epsilon;
	}
}
