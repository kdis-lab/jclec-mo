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

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationRuntimeException;

import net.sf.jclec.IConfigure;
import net.sf.jclec.base.AbstractMutator;

/**
 * Multi-objective Evolutionary Programming (EP) Algorithm.
 * It implements a basic evolutionary programming
 * algorithm, where offspring are created
 * applying a mutation operator.
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
 * @see MOECAlgorithm
 * */
public class MOEPAlgorithm extends MOECAlgorithm {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -8751805004319678389L;

	/** Mutator */
	protected AbstractMutator mutator;

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Get the mutation operator.
	 * @return Mutation operator.
	 */
	public AbstractMutator getMutator() {
		return this.mutator;
	}

	/**
	 * Set the mutation operator.
	 * @param mutator Mutator that has to be set.
	 */
	public void setMutator(AbstractMutator mutator) {
		this.mutator = mutator;
		this.mutator.contextualize(getContext());
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>In EP, offspring are created by applying the mutation operator.</p>
	 * */
	@Override
	protected void doGeneration() {
		// Mutate all individuals
		this.cset = this.mutator.mutate(this.pset);

		// Evaluate all new individuals
		this.evaluator.evaluate(this.cset);
	}

	/**
	 * {@inheritDoc}
	 * <p>Specific parameters for <code>MOEPAlgorithm</code>:
	 * <ul>
	 * 	<li>mutator <code>AbstractMutator</code> (complex)<p>Mutation operator.</li>
	 * </ul>
	 * </p>
	 * */

	@Override
	public void configure(Configuration settings) {

		super.configure(settings);

		// Configure mutator 
		setMutationSettings(settings);
	}

	/////////////////////////////////////////////////////////////////
	// -------------------------------------------- Protected methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Configure mutator.
	 * @param settings Configuration object.
	 * */
	@SuppressWarnings("unchecked")
	protected void setMutationSettings(Configuration settings){
		try {
			String mutatorClassname = settings.getString("mutator[@type]");
			Class<? extends AbstractMutator> mutatorClass = (Class<? extends AbstractMutator>) Class.forName(mutatorClassname);
			AbstractMutator mutator = mutatorClass.getDeclaredConstructor().newInstance();
			// Configure specific parameters
			if (mutator instanceof IConfigure) {
				Configuration mutatorConfiguration = settings.subset("mutator");
				((IConfigure) mutator).configure(mutatorConfiguration);
			}
			setMutator(mutator);
		} 
		catch (ClassNotFoundException|InstantiationException|IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new ConfigurationRuntimeException("Problems creating an instance of mutator", e);
		}
	}
}