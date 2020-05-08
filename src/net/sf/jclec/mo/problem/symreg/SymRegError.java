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

package net.sf.jclec.mo.problem.symreg;

import net.sf.jclec.IFitness;
import net.sf.jclec.IIndividual;
import net.sf.jclec.exprtree.ExprTree;
import net.sf.jclec.exprtree.ExprTreeIndividual;
import net.sf.jclec.exprtree.fun.ExprTreeFunction;
import net.sf.jclec.fitness.SimpleValueFitness;
import net.sf.jclec.mo.evaluation.Objective;

/**
 * An objective function for the symbolic regression problem. 
 * It calculates the error in the function approximation. The
 * x and y values of this example fits with the function 
 * f(x)=x^4+x^3+x^2+x. As it does not requires additional
 * data, this objective can be configured with the generic evaluators
 * <code>MOEvaluator</code> and <code>MOParallelEvaluator</code>.
 * 
 * <p>HISTORY:
 * <ul>
 * 	<li>(AR|JRR|SV, 1.0, February 2018)		First release.</li>
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

public class SymRegError extends Objective{

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -4895547639749517462L;

	/** X values to test the function */
	private double [] xvalues = {-2., -1., 0., 1., 2.};

	/** Expected Y values */
	private double [] yvalues = {10., 0., 0., 4., 30.};

	/////////////////////////////////////////////////////////////////
	//-------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public SymRegError(){
		super();
	}

	/////////////////////////////////////////////////////////////////
	//---------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public IFitness evaluate(IIndividual ind) {

		IFitness fitness = null;
		ExprTree genotype;
		ExprTreeFunction function;

		if(ind instanceof ExprTreeIndividual){
			// Get the genotype and create the function
			genotype = ((ExprTreeIndividual) ind).getGenotype();
			function = new ExprTreeFunction(genotype);

			// Calculate estimated value of the function using the 'x' values
			double [] y = new double[this.xvalues.length];
			for(int i = 0; i<this.xvalues.length; i++)
				y[i] = function.<Double>execute(this.xvalues[i]);

			// Calculate the difference with the expected values (y)
			double rms = 0.0;
			for (int i=0; i<this.yvalues.length; i++) {
				double diff = y[i] - this.yvalues[i];
				rms += diff * diff;
			}
			rms = Math.sqrt(rms);
			fitness = new SimpleValueFitness(rms);
		}
		return fitness;
	}
}
