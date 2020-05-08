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

package net.sf.jclec.mo.problem.zdt;

import net.sf.jclec.IFitness;
import net.sf.jclec.IIndividual;
import net.sf.jclec.fitness.SimpleValueFitness;
import net.sf.jclec.mo.evaluation.Objective;
import net.sf.jclec.realarray.RealArrayIndividual;

/**
 * F2 function of ZDT1 optimization problem. For a definition of the ZDT1 problem, see:
 *  
 * <p><i>Paper</i>: E. Ziztler, K. Deb, L. Thiele. "Comparison of Multiobjective Evolutionary 
 * Algorithms: Empirical Results". Evolutionary Computation, vol. 8, no. 2, pp. 173-195. 2000.</p>
 * 
 * <p>URL: {@link http://people.ee.ethz.ch/~sop/download/supplementary/testproblems/zdt1/}</p>
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
 * @see Objective
 * */
public class ZDT1F2Objective extends Objective {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -2613717427843641289L;

	/////////////////////////////////////////////////////////////////
	//-------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public ZDT1F2Objective(){
		super();
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
		double f1, g = 0.0, h;
		int m;
		if(solution instanceof RealArrayIndividual){
			genotype = ((RealArrayIndividual) solution).getGenotype();
			m = genotype.length;
			f1 = genotype[0];
			for(int i=1; i<m; i++){
				g += genotype[i];
			}
			g = 1+9*g/(m-1);
			h = 1-Math.sqrt(f1/g);
			fitness = new SimpleValueFitness(g*h);
		}
		return fitness;
	}
}
