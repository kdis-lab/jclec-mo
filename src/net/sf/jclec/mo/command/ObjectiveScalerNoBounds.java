/*
This file belongs to JCLEC-MO, a Java library for the
application and development of metaheuristic algorithms 
for the resolution of multi-objective and many-objective 
optimization problems.

Copyright (C) 2018. A. Ramirez, J.R. Romero, S. Ventura.
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

package net.sf.jclec.mo.command;

import java.util.List;

import net.sf.jclec.IIndividual;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;

/**
 * This command scales the objective values of the individuals considering the current 
 * values within the population, i.e. the bounds for the objective functions are extracted
 * from the population. This command uses other commands to obtain the required bounds
 * and the scale the values. The given population is modified during the execution
 * of the command.
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
 * @see Command
 * @see MaxObjectiveValue
 * @see MinObjectiveValue
 * @see ObjectiveScaler
 * */

public class ObjectiveScalerNoBounds extends ObjectiveScaler {

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 5018997170972854982L;

	/**
	 * Empty constructor.
	 * */
	public ObjectiveScalerNoBounds(){
		super();
	}

	/**
	 * Parameterized constructor.
	 * @param population The set of individuals to work with.
	 * */
	public ObjectiveScalerNoBounds(List<IIndividual> population) {
		super(population, null, null);
	}

	/**
	 * {@inheritDoc}
	 * <p>This command performs the scale of the objectives
	 * values. The current values will be replaced by the scaled values
	 * in the individual fitness object.</p>
	 * */
	@Override
	public void execute() {
		int size = this.population.size();
		if(size > 0){
			int nObjectives = ((MOFitness)this.population.get(0).getFitness()).getNumberOfObjectives();
			double [] min = new double[nObjectives];
			double [] max = new double[nObjectives];

			// Compute the minimum and maximum value of each objective
			MinObjectiveValue minCommand = new MinObjectiveValue();
			MaxObjectiveValue maxCommand = new MaxObjectiveValue();

			minCommand.setPopulation(this.population);
			maxCommand.setPopulation(this.population);

			for(int i=0; i<nObjectives; i++){

				// Minimum of objective i
				minCommand.setObjectiveIndex(i);
				minCommand.execute();
				min[i] = minCommand.getMinValue();

				// Maximum of objective i
				maxCommand.setObjectiveIndex(i);
				maxCommand.execute();
				max[i] = maxCommand.getMaxValue();
			}

			// Set the bounds and scale the objective values calling the super method
			super.setMinValues(min);
			super.setMaxValues(max);
			super.execute();
		}
	}
}
