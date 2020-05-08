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
 * This command inverts the objective values of the individuals within the population.
 * The population will be modified after the execution of this command.
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
 * */

public class ObjectiveInverter extends Command {

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -7935306583744437353L;

	/**
	 * Empty constructor.
	 * */
	public ObjectiveInverter() {
		super(null);
	}

	/**
	 * Parameterized constructor.
	 * @param population The set of individuals to work with.
	 * */
	public ObjectiveInverter(List<IIndividual> population) {
		super(population);
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>This command performs an inversion of the objectives values. 
	 * The current values will be replaced by the inverted values in the individual fitness object. 
	 * The bounds of the objectives will be extracted from the current population.</p>
	 * */
	@Override
	public void execute() {
		MOFitness fitness;
		double value;
		int size = this.population.size();
		if(size>0){
			int nObjectives = ((MOFitness)this.population.get(0).getFitness()).getNumberOfObjectives();
			try {
				for(int i=0; i<size; i++){
					fitness = (MOFitness)this.population.get(i).getFitness();
					for(int j=0; j<nObjectives; j++){
						value = fitness.getObjectiveDoubleValue(j);
						if(value!=0.0)
							fitness.setObjectiveDoubleValue(-1.0*value, j);
					}
				}
			} catch (IllegalAccessException|IllegalArgumentException e) {
				// do nothing
			}
		}
	}
}