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

import org.apache.commons.configuration.Configuration;

import net.sf.jclec.IIndividual;
import net.sf.jclec.mo.evaluation.MOEvaluator;
import net.sf.jclec.mo.evaluation.Objective;
import net.sf.jclec.realarray.RealArrayIndividual;

/**
 * Evaluator for DTLZ6 optimization problem. For a definition of the DTLZ6 problem, see:
 *  
 * <p><i>Paper</i>: K. Deb, L. Thiele, M. Laumanns, E. Ziztler. "Scalable test problems for
 * evolutionary multi-objective optimization". Evolutionary Multiobjective Optimization, pp. 105-145. 2005.</p>
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
 * @see MOEvaluator
 * @see DTLZ6Objective
 * */
public class DTLZ6Evaluator extends MOEvaluator {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -637084792132588299L;

	/////////////////////////////////////////////////////////////////
	//-------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public DTLZ6Evaluator(){
		super();
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void configure(Configuration settings) {
		super.configure(settings);

		// Set the number of objectives
		int numberOfObjs = this.objectives.size();
		int i=0;
		for(Objective obj: this.objectives){
			if(obj instanceof DTLZ6Objective){
				((DTLZ6Objective)obj).setNumberOfObjectives(numberOfObjs);
				((DTLZ6Objective)obj).setIndex(i);
				i++;
			}
			else{
				throw new IllegalArgumentException("Objective class should be DTLZ6Objective");
			}
		}
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	protected void evaluate(IIndividual solution) {

		if(solution instanceof RealArrayIndividual){

			// Compute the g function
			double g = calculateG(((RealArrayIndividual)solution).getGenotype());
			
			// Set the g value for that solution in each objective
			for(Objective obj: this.objectives){
				((DTLZ6Objective)obj).setG(g);
			}
			
			// Call default implementation
			super.evaluate(solution);
		}
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Private methods
	/////////////////////////////////////////////////////////////////
	
	/**
	 * Calculate the constant 'g' used by all the objective functions.
	 * @param genotype The genotype of the solution.
	 * @return g value.
	 * */
	private double calculateG(double [] genotype){
		double g = 0.0;
		int length = genotype.length;
		int k = length - this.numberOfObjectives() + 1;
		for (int i=length-k; i<length; i++)
			g += Math.pow(genotype[i],0.1);
		return g;
	}
}
