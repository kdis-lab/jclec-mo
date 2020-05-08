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

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationRuntimeException;

import net.sf.jclec.IConfigure;
import net.sf.jclec.IIndividual;
import net.sf.jclec.mo.comparator.MOSolutionComparator;

/**
 * A command to sort a set of solutions according to a given comparison criterion.
 * The input population will be modified as a result.
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
 * @see MOSolutionComparator
 * */

public class PopulationSorter extends Command {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -6539934744032424732L;

	/** Compartor of solutions */
	protected MOSolutionComparator comparator;

	/** Inversion flag. If it set to true, greater values are preferred */
	protected boolean inverse;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public PopulationSorter(){
		super(null);
		this.comparator = null;
		this.inverse = false;
	}

	/**
	 * Parameterized constructor.
	 * @param population The set of individuals to work with.
	 * */
	public PopulationSorter(List<IIndividual> population) {
		super(population);
		this.comparator = null;
		this.inverse = false;
	}

	/**
	 * Parameterized constructor.
	 * @param population The set of individuals to work with.
	 * @param comparator The comparator that has to be used.
	 * @param inverse True if greater values are preferred.
	 * */
	public PopulationSorter(List<IIndividual> population, MOSolutionComparator comparator, boolean inverse) {
		super(population);
		this.comparator = comparator;
		this.inverse = inverse;
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Set the comparator.
	 * @param comparator The comparator that will be used to compare individuals.
	 * */
	public void setComparator(MOSolutionComparator comparator){
		this.comparator = comparator;
	}

	/**
	 * Get the comparator.
	 * @return The configured comparator.
	 * */
	public MOSolutionComparator getComparator(){
		return this.comparator;
	}

	/**
	 * Get the inverse flag.
	 * @return The inverse flag.
	 * */
	public boolean isInverse() {
		return inverse;
	}

	/**
	 * Set the inverse flag.
	 * @param True if the order should be inverted after ordering, false otherwise.
	 * */
	public void setInverse(boolean inverse) {
		this.inverse = inverse;
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>Parameters for this command are:
	 * <ul>
	 * 	<li>comparator (<code>MOParetoComparator</code>): 
	 * 	<p>Comparator implementing the dominance criterion to be used. <code>ParetoComparator</code> by default.</p></li>
	 * </ul>
	 * </p>
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public void configure(Configuration settings){

		String classname = settings.getString("comparator[@type]");
		MOSolutionComparator comparator;

		// Create and configure the comparator
		Class<? extends MOSolutionComparator> comparatorClass;
		try {
			// Class
			comparatorClass = (Class<? extends MOSolutionComparator>) Class.forName(classname);
			comparator = comparatorClass.getDeclaredConstructor().newInstance();

			// Configure specific parameters
			if(comparator instanceof IConfigure){
				((IConfigure)comparator).configure(settings.subset("comparator"));
			}
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new ConfigurationRuntimeException("Problems creating an instance of comparator", e);
		}
		setComparator(comparator);

		// Set the inverse flag
		boolean inverse = settings.getBoolean("inverse",false);
		setInverse(inverse);
	}

	@Override
	public void execute() {
		if(this.comparator != null && this.population != null){
			Collections.sort(this.population, this.comparator);
			if(this.inverse){
				Collections.reverse(this.population);
			}
		}
	}
}
