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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationRuntimeException;

import net.sf.jclec.IConfigure;
import net.sf.jclec.IIndividual;
import net.sf.jclec.mo.comparator.fcomparator.ParetoComparator;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;

/**
 * This command extracts the non-dominated solutions of the given population.
 * It requires the definition of the dominance principle to be used. Here, 
 * any comparator inheriting from <code>ParetoComparator</code> can be used. 
 * The given population is not modified during the execution of the command, 
 * since the set of non-dominated solutions is stored in a specific list that 
 * can be retrieved using the <code>getNonDominatedSolutions</code> method.
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
 * @see ParetoComparator
 * */

public class NonDominatedSolutionsExtractor extends Command {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -2371702095008162954L;

	/** Pareto comparator */
	protected ParetoComparator comparator;

	/** The set of non-dominated individuals */
	protected List<IIndividual> nonDominated;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public NonDominatedSolutionsExtractor(){
		super(null);
		this.comparator = null;
		this.nonDominated = null;
	}

	/**
	 * Parameterized constructor.
	 * @param population The set of individuals to work with.
	 * @param comparator The Pareto comparator that has to be used.
	 * */
	public NonDominatedSolutionsExtractor(List<IIndividual> population, ParetoComparator comparator) {
		super(population);
		this.comparator = comparator;
		this.nonDominated = null;
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Set the comparator.
	 * @param comparator The comparator that will be used to compare individuals.
	 * */
	public void setComparator(ParetoComparator comparator){
		this.comparator = comparator;
	}

	/**
	 * Get the comparator.
	 * @return The configured comparator.
	 * */
	public ParetoComparator getComparator(){
		return this.comparator;
	}

	/**
	 * Get the set of non dominated solutions.
	 * @return List containing the non dominated individuals 
	 * regarding the Pareto dominance. 
	 * */
	public List<IIndividual> getNonDominatedSolutions(){
		return this.nonDominated;
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
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public void configure(Configuration settings){

		String classname = settings.getString("comparator[@type]");
		ParetoComparator comparator;

		// A specific comparator has been configured
		if(classname != null){
			// Create and configure the comparator
			Class<? extends ParetoComparator> comparatorClass;
			try {
				// Class
				comparatorClass = (Class<? extends ParetoComparator>) Class.forName(classname);
				comparator = comparatorClass.getDeclaredConstructor().newInstance();

				// Configure specific parameters
				if(comparator instanceof IConfigure){
					((IConfigure)comparator).configure(settings.subset("comparator"));
				}
			} catch (InstantiationException e) {
				throw new ConfigurationRuntimeException("Illegal command classname");
			} catch (IllegalAccessException|ClassNotFoundException|IllegalArgumentException|InvocationTargetException|NoSuchMethodException|SecurityException e) {
				throw new ConfigurationRuntimeException("Problems creating an instance of comparator", e);
			}
		}

		// Default comparator configuration. At this point, the evaluator has not been created yet, 
		// so the components of the fitness comparator are unknown
		else{
			comparator = new ParetoComparator();
		}
		
		setComparator(comparator);
	}

	/**
	 * {@inheritDoc}
	 * <p>This command uses a <code>ParetoComparator</code> to extract
	 * the non-dominated solutions. The resulting set will be stored
	 * in <code>nonDominated</code>, so it can be retrieved using the
	 * <code>getNonDominatedSolutions()</code> method.
	 * </p>
	 * */
	@Override
	public void execute() {

		int size = this.population.size();
		boolean dominated;
		IIndividual ind;
		this.nonDominated = new ArrayList<IIndividual>();

		for(int i=0; i<size; i++) {
			dominated = false;
			ind = this.population.get(i);
			for(int j=0; !dominated && j<size; j++)	{

				// Check pareto dominance
				switch(this.comparator.compare((MOFitness)ind.getFitness(), (MOFitness)this.population.get(j).getFitness())){
				case 1: // i dominates j
					break;

				case -1: // j dominates i
					dominated = true;
					break;

				case 0: // non dominated solutions
					break;
				}
			}
			if(!dominated){
				this.nonDominated.add(ind);
			}
		}
	}
}
