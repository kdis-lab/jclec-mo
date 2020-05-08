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

import net.sf.jclec.IConfigure;
import net.sf.jclec.IIndividual;

/**
 * A generic command to perform an operation over a set of individuals. Note that,
 * depending on the specific operation, the population could be modified after the 
 * execution of the command.
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
 * */
public abstract class Command implements IConfigure{

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 7648123259825917227L;

	/** The set of individuals to be transformed */
	protected List<IIndividual> population;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Parameterized constructor.
	 * @param population The set of individuals to work with.
	 * */
	public Command(List<IIndividual> population){
		this.population = population;
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/Set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Set the population.
	 * @param population The position index of the objective function.
	 * */
	public void setPopulation(List<IIndividual> population){
		this.population = population;
	}
	
	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Abstract methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Execute the command.
	 * */
	public abstract void execute();

	/**
	 * {@inheritDoc}
	 * */
	public void configure(Configuration settings){
		// do nothing
	}
}
