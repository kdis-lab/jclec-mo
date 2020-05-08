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
import net.sf.jclec.IMutator;
import net.sf.jclec.IRecombinator;
import net.sf.jclec.base.FilteredMutator;
import net.sf.jclec.base.FilteredRecombinator;

/**
 * Multi-objective Genetic Algorithm (GA).
 * It implements the general behavior of a genetic algorithm, 
 * where offspring are created applying recombination and 
 * mutation operators with a specific probability. 
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
public class MOGeneticAlgorithm extends MOECAlgorithm {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -8751805004319678389L;

	/** Crossover */
	protected FilteredRecombinator recombinator;

	/** Mutator */
	protected FilteredMutator mutator;

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Get the crossover operator.
	 * @return Crossover operator.
	 */
	public IRecombinator getRecombinator() {
		return this.recombinator.getDecorated();
	}

	/**
	 * Set the crossover operator.	
	 * @param recombinator Crossover that has to be set.
	 */
	public void setRecombinator(IRecombinator recombinator) {
		if (this.recombinator == null) {
			this.recombinator = new FilteredRecombinator (getContext());
		}
		this.recombinator.setDecorated(recombinator);
	}

	/**
	 * Get the recombination probability.
	 * @return Recombination probability.
	 */
	public double getRecombinationProb(){
		return this.recombinator.getRecProb();
	}

	/**
	 * Set the recombination probability.
	 * @param recProb The probability that has to be set.
	 */
	public void setRecombinationProb(double recProb){
		if (this.recombinator != null) {
			this.recombinator.setRecProb(recProb);
		}
	}

	/**
	 * Get the mutation operator.
	 * @return Mutation operator.
	 */
	public IMutator getMutator() {
		return this.mutator.getDecorated();
	}

	/**
	 * Set the mutation operator.
	 * @param mutator Mutation operator.
	 */
	public void setMutator(IMutator mutator) {
		if (this.mutator == null) {
			this.mutator = new FilteredMutator (getContext());
		}
		this.mutator.setDecorated(mutator);
	}

	/**
	 * Get the mutation probability.
	 * @return Mutation probability.
	 */
	public double getMutationProb(){
		return this.mutator.getMutProb();
	}

	/**
	 * Set the mutation probability.
	 * @param mutProb The probability that has to be set.
	 */
	public void setMutationProb(double mutProb){
		if (this.mutator != null) {
			this.mutator.setMutProb(mutProb);
		}
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>In GAs, the genetic operators (crossover and mutation) 
	 * are executed.</p>
	 * */
	@Override
	protected void doGeneration() {
		// Recombine parents
		this.cset = this.recombinator.recombine(this.pset);

		// Copy non-recombined individuals
		int size = this.recombinator.getSterile().size();
		for (int i=0; i<size; i++) 
			this.cset.add(this.recombinator.getSterile().get(i).copy());

		// Mutate individuals
		this.cset = this.mutator.mutate(this.cset);

		// Add non-mutated individuals
		size = this.mutator.getSterile().size();
		for (int i=0; i<size; i++)
			this.cset.add(this.mutator.getSterile().get(i).copy());

		// Evaluate all new individuals
		this.evaluator.evaluate(this.cset);
	}

	/**
	 * {@inheritDoc}
	 * <p>Specific parameters for <code>MOGeneticAlgorithm</code>:
	 * <ul>
	 * 	<li>recombinator <code>IRecombinator</code> (complex)
	 * <p>Crossover operator.
	 * 	<ul>
	 * 	<li><code>[@rec-prob] (double)</code>: 
	 * 	Crossover probability.</li>
	 * 	</ul>
	 * </li>
	 * 	<li>mutator <code>IMutator</code> (complex)
	 * <p>Mutator operator.
	 * <ul>
	 * 	<li><code>[@mut-prob] (double)</code>: 
	 * 	Mutation probability.</li>
	 * 	</ul>
	 * </li>
	 * </ul>
	 * </p>
	 * */
	@Override
	public void configure(Configuration settings) {

		super.configure(settings);

		// Configure recombinator
		setRecombinatorSettings(settings);

		// Configure mutator
		setMutationSettings(settings);
	}

	/////////////////////////////////////////////////////////////////
	// -------------------------------------------- Protected methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Configure the recombinator.
	 * @param settings Configuration object.
	 * */
	@SuppressWarnings("unchecked")
	protected void setRecombinatorSettings(Configuration settings){
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
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new ConfigurationRuntimeException("Problems creating an instance of recombinator", e);
		}

		// Recombination probability 
		double recProb = settings.getDouble("recombinator[@rec-prob]");
		setRecombinationProb(recProb);
	}

	/**
	 * Configure the mutator.
	 * @param settings Configuration object.
	 * */
	@SuppressWarnings("unchecked")
	protected void setMutationSettings(Configuration settings){
		try {
			String mutatorClassname = settings.getString("mutator[@type]");
			Class<? extends IMutator> mutatorClass = (Class<? extends IMutator>) Class.forName(mutatorClassname);
			IMutator mutator = mutatorClass.getDeclaredConstructor().newInstance();
			if (mutator instanceof IConfigure) {
				Configuration mutatorConfiguration = settings.subset("mutator");
				((IConfigure) mutator).configure(mutatorConfiguration);
			}
			setMutator(mutator);
		} 
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new ConfigurationRuntimeException("Problems creating an instance of mutator", e);
		}

		// Mutation probability 
		double mutProb = settings.getDouble("mutator[@mut-prob]");
		setMutationProb(mutProb);
	}
}
