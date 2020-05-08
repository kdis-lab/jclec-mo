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

package net.sf.jclec.mo.evaluation;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationRuntimeException;

import net.sf.jclec.IConfigure;
import net.sf.jclec.IFitness;
import net.sf.jclec.IIndividual;
import net.sf.jclec.base.AbstractParallelEvaluator;
import net.sf.jclec.mo.IMOEvaluator;
import net.sf.jclec.mo.comparator.fcomparator.MOFitnessComparator;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;

/**
 * Parallel evaluator for a multi-objective problem. It evaluates each 
 * solution using a list of configured objectives.
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
 * @see AbstractParallelEvaluator
 * @see IMOEvaluator
 * @see Objective
 * */
public class MOParallelEvaluator extends AbstractParallelEvaluator implements IMOEvaluator{

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -6216868930986953748L;

	/** List of objectives to evaluate */
	protected List<Objective> objectives;

	/** Fitness comparator */
	protected MOFitnessComparator comparator;

	/** The fitness object that has to be assigned to the individuals */
	protected MOFitness fitnessPrototype;

	/////////////////////////////////////////////////////////////////
	//-------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public MOParallelEvaluator(){
		super();
	}

	/////////////////////////////////////////////////////////////////
	//---------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public List<Objective> getObjectives(){
		return this.objectives;
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public int numberOfObjectives(){
		return this.objectives.size();
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public final void setComparator(MOFitnessComparator comparator) {
		this.comparator = comparator;
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public final Comparator<IFitness> getComparator() {
		return this.comparator;
	}
	
	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void setFitnessPrototype(MOFitness fitness) {
		this.fitnessPrototype = fitness;	
	}

	/**
	 * {@inheritDoc}
	 * <p>Parameters for <code>MOParallelEvaluator</code>
	 * are:
	 * <ul>
	 *  <li>objective (List of <code>Objective</code>):
	 * 	The list of objectives to be evaluated. Parameters 
	 * 	for every objective are:
	 * 	<ul>
	 * 		<li><code>[@min] (double)</code>: 
	 * 		Minimum value, negative infinite by default.</li>
	 * 
	 * 		<li><code>[@max] (double)</code>: 
	 * 		Maximum value, positive infinite by default.</li> 
	 * 
	 * 		<li><code>[@maximize] (boolean)</code>: 
	 * 		Objective to be maximized (true) or minimized (false).</li>
	 * 	</ul>
	 * 	</li>
	 * </ul>
	 * </p>
	 * */
	@SuppressWarnings("unchecked")
	@Override
	public void configure(Configuration settings) {

		// Get the class names
		Configuration objSettings = settings.subset("objectives");
		String classnames [] = objSettings.getStringArray("objective[@type]");
		int size = classnames.length;

		// Create the array of objectives
		this.objectives = new ArrayList<Objective>(size);
		Objective obj;

		// Create and configure each objective
		Class<? extends Objective> objClass;
		double min, max;
		boolean maximize;
		Object property;
		try {
			for(int i=0; i<size; i++){

				// Class
				objClass = (Class<? extends Objective>) Class.forName(classnames[i]);
				obj = objClass.getDeclaredConstructor().newInstance();

				// Set the index
				obj.setIndex(i);

				// Configure minimum and maximum value
				// Minimum value
				property = objSettings.getProperty("objective("+i+")[@min]");
				if(property != null)
					min = Double.parseDouble(property.toString());
				else
					min = Double.NEGATIVE_INFINITY;
				obj.setMinimum(min);

				// Maximum value
				property = objSettings.getProperty("objective("+i+")[@max]");
				if(property != null)
					max = Double.parseDouble(property.toString());
				else
					max = Double.POSITIVE_INFINITY;
				obj.setMaximum(max);

				// Maximization flag
				property = objSettings.getProperty("objective("+i+")[@maximize]");
				if(property != null){
					maximize = Boolean.parseBoolean(property.toString());
					obj.setMaximized(maximize);
				}
				else
					throw new IllegalArgumentException("Maximization flag in objective is required for objective configuration");

				// Configure specific parameters
				if(obj instanceof IConfigure){
					((IConfigure)obj).configure(objSettings);
				}

				// Add the objective to the list
				this.objectives.add(obj);

			}
		} catch (InstantiationException e) {
			throw new ConfigurationRuntimeException("Illegal objective classname");
		} catch (IllegalAccessException|ClassNotFoundException|IllegalArgumentException|InvocationTargetException|NoSuchMethodException|SecurityException e) {
			throw new ConfigurationRuntimeException("Problems creating an instance of objective", e);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>This method iteratively evaluates the
	 * individual for each objective.</p>
	 * */
	@Override
	protected void evaluate(IIndividual solution) {

		// Create an empty fitness
		MOFitness fitness = null;
		try {
			fitness = (MOFitness)fitnessPrototype.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}

		// Evaluate the solution for each objective
		int nObj = numberOfObjectives();
		IFitness [] components = new IFitness[nObj];
		for(int i=0; i<nObj; i++){
			components[i] = this.objectives.get(i).evaluate(solution);
		}

		// Set components (objective values) in the fitness
		fitness.setObjectiveValues(components);

		// Set the fitness in the solution
		solution.setFitness(fitness);
	}
}
