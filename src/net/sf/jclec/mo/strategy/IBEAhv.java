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

package net.sf.jclec.mo.strategy;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.configuration.Configuration;

import net.sf.jclec.IIndividual;
import net.sf.jclec.mo.IMOEvaluator;
import net.sf.jclec.mo.command.ObjectiveScaler;
import net.sf.jclec.mo.command.ObjectiveScalerNoBounds;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;

/**
 * IBEA implementation using hypervolume indicator. Here, the
 * indicator considers the hypervolume of the objective space
 * dominated by one solution, as well as the volume of the space
 * that is dominated by one solution but not by the other.
 * 
 * <p>This version modifies the fitness values scaling with the objective
 * bounds in the current population. Therefore, the indicator always
 * lies in the interval [-1,1].</p>
 * 
 * <p>HISTORY:
 * <ul>
 *  <li>(AR|JRR|SV, 1.0, March 2018)		First release.</li>
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
 * @see IBEA
 * */
public class IBEAhv extends IBEA {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -4245800996552619626L;

	/** Reference point */
	protected double rho;
	
	/** Command to scale objective values */
	protected ObjectiveScaler scalerCommand;
	
	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public IBEAhv(){
		super();
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public List<IIndividual> initialize(List<IIndividual> population) {
		// Create the auxiliary command
		this.scalerCommand = new ObjectiveScalerNoBounds();
		return super.initialize(population);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>Parameters for IBEA are:
	 * <ul>	
	 * <li>rho (<code>double</code>): 
	 * <p>reference point (same value for all the coordinates). 
	 * It should not be smaller than 1. Default value is 1.</p></li>
	 * </ul>
	 * */
	@Override
	public void configure(Configuration settings){
		// Call super method
		super.configure(settings);

		// IBEAhv needs the reference point
		this.rho = settings.getDouble("rho",1);
		if(rho<1){
			throw new IllegalArgumentException("The value of rho cannot be smaller than 1.");
		}
	}
	
	/**
	 * {@inheritDoc}
	 * <p>IBEAhv overrides this method since objective values should be scaled.</p>
	 * */
	@Override
	protected void fitnessAssignment(List<IIndividual> population, List<IIndividual> archive) {

		// Invert objective values for maximization problem and scale the objective values
		// The commands are executed over a copy to avoid missing the objective values
		List<IIndividual> copy = new ArrayList<IIndividual>();
		int size = population.size();
		for(int i=0; i<size; i++){
			copy.add(population.get(i).copy());
		}
		
		if(isMaximized()){
			this.commandInvert.setPopulation(copy);
			this.commandInvert.execute();
		}	
		this.scalerCommand.setPopulation(copy);
		this.scalerCommand.execute();

		// Compute objective bounds and fitness components considering the scaled objective space
		computeBounds(copy);
		double [][] fitnessComponents = computeFitnessComponents(copy);

		// Set the overall fitness value of each individual
		double fvalue;
		for(int i=0; i<size; i++){
			fvalue = 0.0;
			for(int j=0; j<size; j++){
				if(i!=j){
					fvalue += fitnessComponents[i][j];
				}
			}
			((MOFitness)population.get(i).getFitness()).setValue(fvalue);
		}
	}
	
	/**
	 * {@inheritDoc}
	 * <p>IBEAhv computes the hypervolume dominated by the 
	 * second solution (<code>ind1</code>) but not by the 
	 * first solution (<code>ind0</code>).</p>
	 * */
	@Override
	protected double computeIndicator(IIndividual ind0, IIndividual ind1) {
		double result;
		int nObjs = ((IMOEvaluator)getContext().getEvaluator()).getObjectives().size();
		if(getSolutionComparator().compare(ind0, ind1) == 1){
			result = -calculateHvIndicator(ind0,ind1,nObjs);
		}
		else{
			result = calculateHvIndicator(ind0,ind1,nObjs);
		}

		return result;
	}

	/**
	 * This function recursively calculates the portion of the hypervolume
	 * for each dimension.
	 * @param ind0 First individual.
	 * @param ind1 Second individual.
	 * @param dim Current dimension.
	 * */
	private double calculateHvIndicator(IIndividual ind0, IIndividual ind1, int dim){

		double objA = -1, objB = -1, r, max;
		double volume = 0.0;
		r = rho*(this.maxBounds[dim-1] - this.minBounds[dim-1]);
		max = this.minBounds[dim-1]+r;

		try {
			objA = ((MOFitness)ind0.getFitness()).getObjectiveDoubleValue(dim-1);
		} catch (IllegalAccessException | IllegalArgumentException e) {
			e.printStackTrace();
		}
		if(ind1 == null){
			objB = max;
		}
		else{
			try {
				objB = ((MOFitness)ind1.getFitness()).getObjectiveDoubleValue(dim-1);
			} catch (IllegalAccessException | IllegalArgumentException e) {
				e.printStackTrace();
			}
		}

		if(dim==1){
			if (objA<objB)
				volume = (objB-objA)/r;
			else
				volume = 0;
		}
		else{
			if(objA<objB){
				volume = calculateHvIndicator(ind0, null, dim-1)*(objB-objA)/r;
				volume += calculateHvIndicator(ind0, ind1, dim-1)*(max-objB)/r;
			}
			else{
				volume = calculateHvIndicator(ind0, ind1, dim-1)*(max-objA)/r;
			}
		}

		return volume;
	}
}
