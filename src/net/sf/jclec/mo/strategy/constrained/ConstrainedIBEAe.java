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

package net.sf.jclec.mo.strategy.constrained;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import net.sf.jclec.IFitness;
import net.sf.jclec.IIndividual;
import net.sf.jclec.mo.IConstrained;
import net.sf.jclec.mo.comparator.ConstrainedComparator;
import net.sf.jclec.mo.comparator.fcomparator.ParetoComparator;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;
import net.sf.jclec.mo.strategy.IBEAe;

/**
 * An IBEAe strategy for problems with constraints. It sets the worst fitness values
 * to infeasible individuals, so any feasible individuals will have more opportunities
 * to survive.
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
 * @see IBEAe
 * @see IConstrained
 * */
public class ConstrainedIBEAe extends IBEAe {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -6679288045275644563L;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public ConstrainedIBEAe(){
		super();
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>This version of IBEAe requires the configuration of
	 * a comparator for constrained problems.</p>
	 * */
	@Override
	public void createSolutionComparator(Comparator<IFitness>[] components) {
		// Fitness comparator
		ParetoComparator fcomparator = new ParetoComparator(components);
		// Individuals comparator
		ConstrainedComparator comparator = new ConstrainedComparator(fcomparator);
		setSolutionComparator(comparator);
	}
	
	/**
	 * {@inheritDoc}
	 * <p>The constrained version of IBEAe sets the worst fitness value
	 * to infeasible individuals.</p>
	 * */
	@Override
	protected void fitnessAssignment(List<IIndividual> population, List<IIndividual> archive) {

		// Compute objective bounds and fitness components
		computeBounds(population);
		double [][] fitnessComponents = computeFitnessComponents(population);

		// Set the overall fitness value of each individual
		int size = population.size();
		double fvalue = 0.0;

		for(int i=0; i<size; i++){

			// Sum fitness components for feasible individuals
			if(((IConstrained)population.get(i)).isFeasible()){
				fvalue = 0.0;
				for(int j=0; j<size; j++){
					if(i!=j){
						fvalue += fitnessComponents[i][j];
					}
				}
			}

			// Set an invalid fitness for infeasible individuals
			else{
				if(isMaximized())
					fvalue = Double.MIN_VALUE;
				else
					fvalue = Double.MAX_VALUE;
			}

			((MOFitness)population.get(i).getFitness()).setValue(fvalue);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>The constrained version of IBEAe updates the fitness value of
	 * infeasible individuals, so feasible individuals will be always preferred.</p>
	 * */
	@Override
	public List<IIndividual> environmentalSelection(List<IIndividual> population, 
			List<IIndividual> offspring, List<IIndividual> archive) {

		// Join populations
		List<IIndividual> survivors = new ArrayList<IIndividual>();
		survivors.addAll(population);
		for(IIndividual ind: offspring){
			if(!survivors.contains(ind)){
				survivors.add(ind);
			}
		}

		// Set fitness values
		fitnessAssignment(survivors, null);

		// Select survivors 
		int popSize = getContext().getPopulationSize();
		int size = survivors.size();
		int nRemovals = size - popSize;
		int n = 0;
		double worstValue = 0.0, fvalue;
		int worstIndex = 0;
		IIndividual ind;

		if(this.maxAbsValue==0.0)
			this.maxAbsValue = 0.0000000000000000001;

		// Iteratively remove the worst individual
		// and update fitness values
		while(n<nRemovals){

			// Update worst value, get the first value
			worstValue = ((MOFitness)survivors.get(0).getFitness()).getValue();
			worstIndex = 0;

			// Search the current worst individual
			for(int i=1; i<size; i++){
				ind = survivors.get(i);
				fvalue = ((MOFitness)ind.getFitness()).getValue();
				if(fvalue<worstValue){
					worstValue = fvalue;
					worstIndex = i;
				}
			}
			// Calculate the fitness value of the individuals
			// if the worst individual is removed
			for(int i=0; i<size; i++){
				ind = survivors.get(i);
				if(((IConstrained)ind).isFeasible())
					fvalue = ((MOFitness)ind.getFitness()).getValue() 
					- Math.exp(((-1.0*this.indicatorValues[worstIndex][i])/this.maxAbsValue)/this.k);
				else{ 
					if(isMaximized())
						fvalue = Double.MIN_VALUE;
					else
						fvalue = Double.MAX_VALUE;
				}
				((MOFitness)ind.getFitness()).setValue(fvalue);
			}

			// The worst individual is removed
			survivors.remove(worstIndex);
			size--;
			n++;
		}
		return survivors;
	}
}
