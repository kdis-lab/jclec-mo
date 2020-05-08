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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationRuntimeException;

import net.sf.jclec.IConfigure;
import net.sf.jclec.IIndividual;
import net.sf.jclec.IRecombinator;
import net.sf.jclec.base.AbstractMutator;
import net.sf.jclec.base.AbstractRecombinator;
import net.sf.jclec.selector.RandomSelector;

/**
 * Multi-objective Evolution Strategy (ES) Algorithm.
 * It implements the general behavior of an Evolution Strategy, 
 * where offspring are created applying an optional crossover 
 * operator and a mutation operator.
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
public class MOESAlgorithm extends MOECAlgorithm {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -8751805004319678389L;

	/** Crossover operator (optional) */
	protected AbstractRecombinator recombinator;

	/** Mutator operator */
	protected AbstractMutator mutator;

	/** Number of parents (population size) */
	protected int mu;

	/** Number of offspring */
	protected int lambda;

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Get the crossover operator.
	 * @return Crossover operator.
	 */
	public IRecombinator getRecombinator() {
		return this.recombinator;
	}

	/**
	 * Set the crossover operator.	
	 * @param recombinator The crossover operator that has to be set.
	 */
	public void setRecombinator(AbstractRecombinator recombinator) {
		this.recombinator = recombinator;
		if(this.recombinator != null)
			this.recombinator.contextualize(getContext());
	}

	/**
	 * Get the mutation operator.
	 * @return Mutation operator.
	 */
	public AbstractMutator getMutator() {
		return this.mutator;
	}

	/**
	 * Set the mutation operator.
	 * @param mutator Mutation operator that has to be set.
	 */
	public void setMutator(AbstractMutator mutator) {
		this.mutator = mutator;
		this.mutator.contextualize(getContext());
	}

	/**
	 * Get the value of <code>mu</code> property.
	 * @return Number of parents.
	 * */
	public int getMu(){
		return this.mu;
	}
	
	/**
	 * Set the value of the <code>mu</code> property.
	 * @param mu The value that has to be set.
	 * */
	protected void setMu(int mu){
		this.mu = mu;
	}
	
	/**
	 * Get the value of <code>lambda</code> property.
	 * @return Number of offspring.
	 * */
	public int getLambda(){
		return this.lambda;
	}
	
	/**
	 * Set the value of the <code>lambda</code> property.
	 * @param lambda The value that has to be set.
	 * */
	protected void setLambda(int lambda){
		this.lambda = lambda;
	}
	
	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>Specific parameters for <code>MOESAlgorithm</code>:
	 * <ul>
	 * <li>recombinator: <code>AbstractRecombinator</code>
	 * <p>Crossover operator (optional parameter).</p>
	 * </li>
	 * <li>mutator: <code>AbstractMutator</code>
	 * <p>Mutation operator</p>
	 * </li>
	 * <li>lambda (<code>int</code>): 
	 * <p>Number of offspring. Default value is 1.</p>
	 * </li>
	 * </ul>
	 * */
	@Override
	public void configure(Configuration settings) {

		super.configure(settings);

		// Configure recombinator (optional parameter)
		setRecombinationSettings(settings);

		// Configure mutator 
		setMutationSettings(settings);
		
		// Set mu
		setMu(this.populationSize);
		
		// Configure lambda
		int lambda = settings.getInt("lambda",1);
		setLambda(lambda);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>In ES, the generation of offspring depends on the
	 * <code>mu</code> and <code>lambda</code> parameters.</p>
	 * */
	@Override
	protected void doGeneration() {

		RandomSelector selector;
		List<IIndividual> aux;
		
		// Recombine parents
		if(this.mu > 1 && this.recombinator != null){
			
			// Recombine parents, by default two descendants are generated for each pair of parents
			this.cset = this.recombinator.recombine(this.pset);
			
			// Filter 'lambda' descendants
			if(this.cset.size() > this.lambda){
				
				// If lambda==2*mu, two parents generates one descendant
				if(this.lambda == 2*this.mu){
					aux = new ArrayList<IIndividual>();
					for(int i=0; i<this.cset.size(); i+=2){
						aux.add(this.cset.get(i));
					}
				}
				
				// Otherwise, choose 'lambda' individuals at random from the pool of descendants
				else{
					selector = new RandomSelector();
					selector.contextualize(getStrategy().getContext());
					this.cset = selector.select(this.cset, this.lambda);
				}
			}
		}
		
		// Recombination is not used
		else
			this.cset = this.pset;
		
		// Mutate all individuals
		this.cset = this.mutator.mutate(this.cset);

		// Evaluate all new individuals
		this.evaluator.evaluate(this.cset);
	}
	
	/**
	 * Configure recombinator.
	 * @param settings Configuration object.
	 * */
	@SuppressWarnings("unchecked")
	protected void setRecombinationSettings(Configuration settings){
		try {
			String recombinatorClassname = settings.getString("recombinator[@type]");
			if(recombinatorClassname != null){
				Class<? extends AbstractRecombinator> recombinatorClass = 
						(Class<? extends AbstractRecombinator>) Class.forName(recombinatorClassname);
				AbstractRecombinator recombinator = recombinatorClass.getDeclaredConstructor().newInstance();
				if (recombinator instanceof IConfigure) {
					Configuration recombinatorConfiguration = settings.subset("recombinator");
					((IConfigure) recombinator).configure(recombinatorConfiguration);
				}
				setRecombinator(recombinator);
			}
		}
		catch (ClassNotFoundException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			throw new ConfigurationRuntimeException("Problems creating an instance of recombinator", e);
		}
	}
	
	/**
	 * Configure mutator.
	 * @param settings Configuration object.
	 * */
	@SuppressWarnings("unchecked")
	protected void setMutationSettings(Configuration settings){
		try {
			String mutatorClassname = settings.getString("mutator[@type]");
			Class<? extends AbstractMutator> mutatorClass = 
					(Class<? extends AbstractMutator>) Class.forName(mutatorClassname);

			AbstractMutator mutator = mutatorClass.getDeclaredConstructor().newInstance();
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