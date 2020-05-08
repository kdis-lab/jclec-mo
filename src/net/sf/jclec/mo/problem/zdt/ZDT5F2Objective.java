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
import net.sf.jclec.binarray.BinArrayIndividual;
import net.sf.jclec.fitness.SimpleValueFitness;
import net.sf.jclec.mo.evaluation.Objective;

/**
 * F2 function of ZDT5 optimization problem. For a definition of the ZDT5 problem, see:
 *  
 * <p><i>Paper</i>: E. Ziztler, K. Deb, L. Thiele. "Comparison of Multiobjective Evolutionary 
 * Algorithms: Empirical Results". Evolutionary Computation, vol. 8, no. 2, pp. 173-195. 2000.</p>
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
public class ZDT5F2Objective extends Objective {

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
	public ZDT5F2Objective(){
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
		byte [] genotype;
		double f1, g = 0.0, h;
		int m;
		if(solution instanceof BinArrayIndividual){
			genotype = ((BinArrayIndividual) solution).getGenotype();
			m = genotype.length;
			
			f1 = 1 + calculateU(genotype, 0, 29);
			for(int i=30; i<m; i+=5){
				g+=calculateV(calculateU(genotype, i, i+4));
			}
			h = 1.0/f1;
			fitness = new SimpleValueFitness(g*h);
		}
		return fitness;
	}
	
	/**
	 * Calculate the 'u' function, i.e. the number of 1s in a 
	 * part of the genotype.
	 * @param genotype The genotype.
	 * @param init Initial index (inclusive).
	 * @param end End index (inclusive).
	 * @return Number of 1s between <code>genotype[init]</code> and
	 * <code>genotype[end]</code>.
	 * */
	private double calculateU(byte [] genotype, int init, int end){
		double ones = 0.0;
		for(int i=init; i<=end; i++){
			if(genotype[i]==1){
				ones++;
			}
		}
		return ones;
	}
	
	/**
	 * Calculate the 'v' function for a given 'u' value.
	 * @param u The u value.
	 * @return 2+u if u>5, 1 otherwise.
	 * */
	private double calculateV(double u){
		if (u<5.0)
			return 2.0+u;
		else
			return 1.0;
	}
}
