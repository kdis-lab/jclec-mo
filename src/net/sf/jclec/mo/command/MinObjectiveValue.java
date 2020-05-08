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
 * This command calculates the minimum value of an objective function within the population.
 * The given population is not modified during the execution of the command. The minimum
 * value can be retrieved after the execution of the command using a getter method. The index
 * of the objective function can be modified after the creation of the command, thus the <code>execute()</code>
 * method will calculate the bounds considering the index currently stored.
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

public class MinObjectiveValue extends Command {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -2949170614467059983L;

	/** The object to store the minimum value */
	protected double minValue;

	/** The index of the objective */
	protected int index;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public MinObjectiveValue(){
		super(null);
		this.minValue = Double.NaN;
		this.index = -1;
	}
	
	/**
	 * Parameterized constructor.
	 * @param population The set of individuals to work with.
	 * @param minValue The object where the minimum value will be stored.
	 * @param index The position index of the objective function.
	 * */
	public MinObjectiveValue(List<IIndividual> population, int index) {
		super(population);
		this.minValue = Double.NaN;
		this.index = index;
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////
	
	/**
	 * Set the objective index.
	 * @param index The position index of the objective function.
	 * */
	public void setObjectiveIndex(int index){
		this.index = index;
	}
	
	/**
	 * Get the value currently stored as the minimum.
	 * @return Last minimum value computed.
	 * */
	public double getMinValue(){
		return this.minValue;
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>This command calculates the minimum value for a given objective function.
	 * It considers the objective function whose position index is stored 
	 * in <code>index</code>.
	 * The result will be stored in <code>minValue</code>, so it can be then
	 * obtained using <code>getMinValue()</code> method. </p>
	 * */
	@Override
	public void execute() {
		this.minValue = Double.POSITIVE_INFINITY;
		int size = this.population.size();
		double value;
		for(int i=0; i<size; i++){
			try {
				value = ((MOFitness)this.population.get(i).getFitness()).getObjectiveDoubleValue(this.index);
				if(value<this.minValue)
					this.minValue=value;
			} catch (IllegalAccessException | IllegalArgumentException e) {
				// do nothing
			}
		}
	}
}
