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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.sf.jclec.IIndividual;
import net.sf.jclec.mo.comparator.ComparatorByObjectives;
import net.sf.jclec.mo.comparator.fcomparator.MOFitnessComparator;
import net.sf.jclec.mo.evaluation.fitness.ICrowdingDistanceMOFitness;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;

/**
 * A command to compute the crowding distance defined by NSGA-II. The fitness object 
 * of the solutions should implement the <code>ICrowdingDistanceMOFitness</code> 
 * interface, so that the crowding value will be directly stored in the solution.
 * 
 * <p>HISTORY:
 * <ul>
 * <li>(AR|JRR|SV, 1.0, March 2018)		First release.</li>
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
 * @see ICrowdingDistanceMOFitness
 * */
public class CrowdingDistanceCalculator extends Command {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 7348408055599660181L;

	/** Minimum value for each objective function */
	protected double [] minValues;

	/** Maximum value for each objective function */
	protected double [] maxValues;

	/** Fitness comparator to sort by objective */
	protected MOFitnessComparator comparator;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public CrowdingDistanceCalculator(){
		super(null);
		this.minValues = null;
		this.maxValues = null;
		this.comparator = null;
	}

	/**
	 * Parameterized constructor.
	 * @param population The set of individuals to work with.
	 * @param comparator The Pareto comparator that has to be used.
	 * */
	public CrowdingDistanceCalculator(List<IIndividual> population, MOFitnessComparator comparator) {
		super(population);
		this.minValues = null;
		this.maxValues = null;
		this.comparator = comparator;
	}

	/**
	 * Parameterized constructor.
	 * @param population The set of individuals to work with.
	 * @param comparator The Pareto comparator that has to be used.
	 * @param minValues The minimum value of each objective function.
	 * @param maxValues The maximum value of each objective function.
	 * */
	public CrowdingDistanceCalculator(List<IIndividual> population, MOFitnessComparator comparator, double [] minValues, double [] maxValues) {
		super(population);
		this.comparator = comparator;
		this.minValues = minValues;
		this.maxValues = maxValues;
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Set the lower bound for each objective.
	 * @param minValues Array with the minimum value of each objective.
	 * */
	public void setMinValues(double [] minValues){
		this.minValues = minValues;
	}

	/**
	 * Set the upper bound for each objective.
	 * @param minValues Array with the minimum value of each objective.
	 * */
	public void setMaxValues(double [] maxValues){
		this.maxValues = maxValues;
	}

	/**
	 * Set the comparator.
	 * @param comparator The comparator that will be used to compare individuals.
	 * */
	public void setComparator(MOFitnessComparator comparator){
		this.comparator = comparator;
	}

	/**
	 * Get the comparator.
	 * @return The configured comparator.
	 * */
	public MOFitnessComparator getComparator(){
		return this.comparator;
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void execute() {
		List<IIndividual> orderedIndividuals;
		double aux, fmax, fmin;
		int size = this.population.size();
		int nObj = ((MOFitness)this.population.get(0).getFitness()).getNumberOfObjectives();
		double objValuePrevious, objValueNext, crowdingDistance;
		double [] objectiveValues;

		for(int objective=0; objective<nObj; objective++){

			// Sort the individuals according to the current objective
			orderedIndividuals = sortByObjective(population, objective);

			// Obtain the objective values in an array form and scale it if required
			objectiveValues = scaleObjective(orderedIndividuals, objective);

			// The last and first individual have an infinite value
			((ICrowdingDistanceMOFitness)orderedIndividuals.get(0).getFitness()).setCrowdingDistance(Double.MAX_VALUE);
			((ICrowdingDistanceMOFitness)orderedIndividuals.get(size-1).getFitness()).setCrowdingDistance(Double.MAX_VALUE);

			// Compute the maximum and minimum value of this objective within the population
			fmin = objectiveValues[0];
			fmax = objectiveValues[size-1];

			if(fmin!=fmax){ // if the bounds are not equal, compute the distance between the rest of individuals
				for(int i=1; i<size-1; i++){
					objValuePrevious = objectiveValues[i-1]; // previous point
					objValueNext = objectiveValues[i+1]; // next point
					aux = (objValueNext - objValuePrevious) / (fmax - fmin);
					crowdingDistance = ((ICrowdingDistanceMOFitness)orderedIndividuals.get(i).getFitness()).getCrowdingDistance() + aux;
					((ICrowdingDistanceMOFitness)orderedIndividuals.get(i).getFitness()).setCrowdingDistance(crowdingDistance);
				}
			}
		}
	}

	/////////////////////////////////////////////////////////////////
	// -------------------------------------------- Protected methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Sort the list of individuals according to the 
	 * specified objective function.
	 * @param population List of individuals to be sorted.
	 * @param objective The position index of objective to order by.
	 * @return The list of individuals sorted according to the objective.
	 */
	protected List<IIndividual> sortByObjective(List<IIndividual> population, int objective){

		List<IIndividual> result = new ArrayList<IIndividual>(population);
		// Sort in increasing values of the objective
		ComparatorByObjectives comparatorObjectives = 
				new ComparatorByObjectives(this.comparator,objective);
		Collections.sort(result, comparatorObjectives);
		return result;
	}

	/**
	 * Scale the objective values if required. The objective values should be floating
	 * number. If the conversion is not possible, values will be equal to -1.
	 * @param population The list of individuals
	 * @param objective The objective index
	 * @return An array of double value containing the objective values. Values are scaled
	 * if the objective bounds are different from 0 (minimum) and 1 (maximum).
	 * */
	protected double [] scaleObjective(List<IIndividual> population, int objective){
		int size = population.size();
		double [] scaledValues = new double[size];
		double min = this.minValues[objective];
		double max = this.maxValues[objective];
		double value;

		// Only copy the values
		if(min==0 && max==1){
			for(int i=0; i<size; i++){
				try {
					scaledValues[i] = ((MOFitness)population.get(i).getFitness()).getObjectiveDoubleValue(objective);
				} catch (IllegalAccessException | IllegalArgumentException e) {
					scaledValues[i] = -1;
				}
			}
		}

		// If the bounds are not defined, objectives are scaled considering the values in the population
		if(min==Double.MIN_VALUE || min==Double.NEGATIVE_INFINITY || max==Double.MAX_VALUE || max==Double.POSITIVE_INFINITY){

			// Find minimum and maximum value
			MinObjectiveValue commandMin = new MinObjectiveValue(population, objective);
			commandMin.execute();
			min = commandMin.getMinValue();

			MaxObjectiveValue commandMax = new MaxObjectiveValue(population,objective);
			commandMax.execute();
			max = commandMax.getMaxValue();

			// Scale and copy the values
			if(max!=min){
				for(int i=0; i<size; i++){
					try {
						value = ((MOFitness)population.get(i).getFitness()).getObjectiveDoubleValue(objective);
						scaledValues[i] = (value-min)/(max-min);
					} catch (IllegalAccessException | IllegalArgumentException e) {
						scaledValues[i] = -1;
					}
				}
			}
			else{
				for(int i=0; i<size; i++){
					scaledValues[i] = 0;
				}
			}
		}

		// Scale and copy the values
		else{
			for(int i=0; i<size; i++){
				try {
					value = ((MOFitness)population.get(i).getFitness()).getObjectiveDoubleValue(objective);
					scaledValues[i] = (value-min)/(max-min);
				} catch (IllegalAccessException | IllegalArgumentException e) {
					scaledValues[i] = -1;
				}
			}
		}
		return scaledValues;
	}
}
