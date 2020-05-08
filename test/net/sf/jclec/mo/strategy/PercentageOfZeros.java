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

import net.sf.jclec.IFitness;
import net.sf.jclec.IIndividual;
import net.sf.jclec.binarray.BinArrayIndividual;
import net.sf.jclec.fitness.SimpleValueFitness;
import net.sf.jclec.mo.evaluation.Objective;

/**
 * This class represents a simple objective function
 * to be used in JCLEC-MOEA for testing purposes. 
 * It computes the percentage of zeros in the genotype.
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
 * */
public class PercentageOfZeros extends Objective {

	/** Serial ID */
	private static final long serialVersionUID = 7006469618059068675L;

	public PercentageOfZeros() {
		this.maximized = true;
		this.maximum = 1;
		this.minimum = 0;
	}
	
	/**
	 * Evaluation method.
	 * @param individual The individual to be evaluated
	 * @return A fitness object storing the objective value
	 * */
	@Override
	public IFitness evaluate(IIndividual individual) {
		double nZeros = 0;
		double value = -1;
		int length;
		if(individual instanceof BinArrayIndividual){
			byte [] genotype = ((BinArrayIndividual) individual).getGenotype();
			length = genotype.length;
			for(int i=0; i<length; i++){
				if(genotype[i]==0){
					nZeros++;
				}
			}
			value = nZeros/(double)length;
		}
		IFitness fitness = new SimpleValueFitness(value);
		return fitness;
	}
}