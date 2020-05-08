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

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationRuntimeException;

import net.sf.jclec.IIndividual;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;

/**
 * This command scales the objective values of the individuals within the population.
 * To do this, it requires the maximum and minimum value of each objective function. If
 * the value to be scaled is out of these bounds, the nearest bound will be set as
 * the scaled value. In addition, if the lower and upper bound are equals, the resulting
 * value will be 1.0. The population will be modified after the execution of this command.
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

public class ObjectiveScaler extends Command {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -7902024332915247287L;

	/** Minimum value for each objective function */
	protected double [] minValues;

	/** Maximum value for each objective function */
	protected double [] maxValues;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public ObjectiveScaler(){
		super(null);
		this.minValues = null;
		this.maxValues = null;
	}

	/**
	 * Parameterized constructor.
	 * @param population The set of individuals to work with.
	 * @param minValues The minimum value of each objective function.
	 * @param maxValues The maximum value of each objective function.
	 * */
	public ObjectiveScaler(List<IIndividual> population, double [] minValues, double [] maxValues) {
		super(population);
		this.minValues = minValues;
		this.maxValues = maxValues;
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Get the lower bound of each objective.
	 * @param An array containing the lower bounds.
	 * */
	public double[] getMinValues(){
		return this.minValues;
	}
	
	/**
	 * Set the lower bound for each objective.
	 * @param minValues Array with the minimum value of each objective.
	 * */
	public void setMinValues(double [] minValues){
		this.minValues = minValues;
	}
	
	/**
	 * Get the upper bound of each objective.
	 * @param An array containing the upper bounds.
	 * */
	public double[] getMaxValues(){
		return this.maxValues;
	}

	/**
	 * Set the upper bound for each objective.
	 * @param minValues Array with the minimum value of each objective.
	 * */
	public void setMaxValues(double [] maxValues){
		this.maxValues = maxValues;
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>Parameters for <code>ObjectiveScaler</code>
	 * are:
	 * <ul>
	 * 	<li>bounds (List of two <code>double</code> values):
	 * 	The list of bounds (one for each objective). Each bound
	 * (using tag "bound") should contain the following parameters:
	 * 		<ul>
	 * 			<li>min (<code>double</code>): Minimum value.</li>
	 * 			<li>max (<code>double</code>): Maximum value.</li>
	 * 		</ul>
	 * 	</li>
	 * </ul> 
	 * </p>
	 * */
	@Override
	public void configure(Configuration settings) {

		// Get lower bounds
		Configuration boundsSettings = settings.subset("bounds");
		String values [] = boundsSettings.getStringArray("bound[@min]");
		int size = values.length;
		this.minValues = new double[size];
		try {
			for(int i=0; i<size; i++){
				this.minValues[i] = Double.valueOf(values[i]);
			}
		} catch (NumberFormatException e) {
			throw new ConfigurationRuntimeException("A double value is expected in bounds configuration");
		}


		// Get the upper bounds
		values = boundsSettings.getStringArray("bound[@max]");
		size = values.length;
		this.maxValues = new double[size];
		try {
			for(int i=0; i<size; i++){
				this.maxValues[i] = Double.valueOf(values[i]);
			}
		} catch (NumberFormatException e) {
			throw new ConfigurationRuntimeException("A double value is expected in bounds configuration");
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>This command performs the scale of the objectives
	 * values. The current values will be replaced by the scaled values
	 * in the individual fitness object.</p>
	 * */
	@Override
	public void execute() {

		double value;
		int size = this.population.size();
		IIndividual individual;

		// Scale
		if(size>0){
			int nObjectives = ((MOFitness)this.population.get(0).getFitness()).getNumberOfObjectives();
			try {
				for(int i=0; i<nObjectives; i++){
					if(this.maxValues[i]!=this.minValues[i]){
						for(int j=0; j<size; j++){
							individual = this.population.get(j);
							value = ((MOFitness)this.population.get(j).getFitness()).getObjectiveDoubleValue(i);
							value = (value-this.minValues[i])/(this.maxValues[i]-this.minValues[i]);
							if(value > 1.0)
								value = 1.0;
							if(value < 0.0)
								value = 0.0;
							((MOFitness)individual.getFitness()).setObjectiveDoubleValue(value, i);
						}
					}
					else{
						for(int j=0; j<size; j++){
							individual = this.population.get(j);
							((MOFitness)individual.getFitness()).setObjectiveDoubleValue(1.0, i);
						}
					}
				}
			} catch (IllegalAccessException|IllegalArgumentException e) {
				// do nothing
			}
		}
	}
}
