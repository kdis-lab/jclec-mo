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

package net.sf.jclec.mo.problem.dtlz;

import net.sf.jclec.IFitness;
import net.sf.jclec.IIndividual;
import net.sf.jclec.fitness.SimpleValueFitness;
import net.sf.jclec.mo.evaluation.Objective;
import net.sf.jclec.realarray.RealArrayIndividual;

/**
 * Objective function of DTLZ3 optimization problem. For a definition of the DTLZ3 problem, see:
 *  
 * <p><i>Paper</i>: K. Deb, L. Thiele, M. Laumanns, E. Ziztler. "Scalable test problems for
 * evolutionary multi-objective optimization". Evolutionary Multiobjective Optimization, pp. 105-145. 2005.</p>
 * 
 * <p>URL: {@link http://people.ee.ethz.ch/~sop/download/supplementary/testproblems/dtlz3/}</p>
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
 * @see Objective
 * @see DTLZ3Evaluator
 * */
public class DTLZ3Objective extends Objective {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -2613717427843641289L;

	/** g value */
	protected double g;

	/** Number of objectives */
	protected int numberOfObjectives;

	/////////////////////////////////////////////////////////////////
	//-------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public DTLZ3Objective(){
		super();
	}

	/////////////////////////////////////////////////////////////////
	//----------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Set the result of the g function.
	 * @param g The g value.
	 * */
	protected void setG(double g){
		this.g = g;
	}

	/**
	 * Set the number of objectives.
	 * @param n Number of objectives.
	 * */
	protected void setNumberOfObjectives(int n){
		this.numberOfObjectives = n;
	}

	/////////////////////////////////////////////////////////////////
	//---------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public IFitness evaluate(IIndividual solution) {
		IFitness fitness = null;
		double [] genotype;

		double f;
		int myObjIndex = getIndex();
		int mIndex;
		double pi2 = Math.PI/2.0;
		
		if(solution instanceof RealArrayIndividual){
			genotype = ((RealArrayIndividual) solution).getGenotype();
					
			// The constant value
			f = 1.0+this.g; 

			// Accumulate from x_1 to x_M-1
			mIndex = this.numberOfObjectives-(myObjIndex + 1);
			for (int j=0; j<mIndex; j++){ 
				f *= Math.cos(genotype[j]*pi2);        
			}
			
			// Accumulate the last term
			if (myObjIndex != 0){
				f *= Math.sin(genotype[mIndex]*pi2);
			}
			
			// Set the fitness value
			fitness = new SimpleValueFitness(f);
		}
		return fitness;
	}
}
