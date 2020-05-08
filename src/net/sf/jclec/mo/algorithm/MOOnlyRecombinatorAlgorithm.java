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

package net.sf.jclec.mo.algorithm;

import net.sf.jclec.IConfigure;
import net.sf.jclec.IRecombinator;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationRuntimeException;

/**
 * This class implements a genetic algorithm in which only recombination is performed. 
 * By default, the genetic operator is applied to all the individuals within the population.
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
 * @see MOGeneticAlgorithm
 * */
public class MOOnlyRecombinatorAlgorithm extends MOGeneticAlgorithm {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -7418772115273052788L;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public MOOnlyRecombinatorAlgorithm(){
		super();
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>Since this type of algorithm does not use the mutation operator, 
	 * only the recombinator configuration will be retrieved from the configuration file.</p>
	 * */
	@Override
	public void configure(Configuration settings) {
		super.configure(settings);
	}

	/**
	 * {@inheritDoc}
	 * <p>Here, only recombination is used to generate offspring.</p>
	 * */
	@Override
	protected void doGeneration() {
		// Recombine parents
		this.cset = this.recombinator.recombine(this.pset);

		// Copy non-recombined individuals
		int size = this.recombinator.getSterile().size();
		for (int i=0; i<size; i++) 
			this.cset.add(this.recombinator.getSterile().get(i).copy());

		// Evaluate offspring
		this.evaluator.evaluate(this.cset);
	}

	/**
	 * {@inheritDoc}
	 * <p>In this type of algorithm, the recombination 
	 * probability is 1.0 by default.</p>
	 * */
	@SuppressWarnings("unchecked")
	@Override
	protected void setRecombinatorSettings(Configuration settings){
		// Recombinator object
		try {
			String recombinatorClassname = settings.getString("recombinator[@type]");
			Class<? extends IRecombinator> recombinatorClass = (Class<? extends IRecombinator>) Class.forName(recombinatorClassname);
			IRecombinator recombinator = recombinatorClass.getDeclaredConstructor().newInstance();
			if (recombinator instanceof IConfigure) {
				Configuration recombinatorConfiguration = settings.subset("recombinator");
				((IConfigure) recombinator).configure(recombinatorConfiguration);
			}
			setRecombinator(recombinator);
		} 
		catch (ClassNotFoundException|InstantiationException|IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new ConfigurationRuntimeException("Problems creating an instance of recombinator", e);
		}

		// Recombination probability 
		double recProb = settings.getDouble("recombinator[@rec-prob]",1.0);
		setRecombinationProb(recProb);
	}

	/**
	 * {@inheritDoc}
	 * <p>This type of algorithm does not use mutator operator, 
	 * so this method does nothing.</p>
	 * */
	@Override
	protected void setMutationSettings(Configuration settings){
		// do nothing
	}
}
