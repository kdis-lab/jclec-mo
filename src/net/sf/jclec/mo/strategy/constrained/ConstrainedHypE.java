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
import java.util.Vector;

import net.sf.jclec.IFitness;
import net.sf.jclec.IIndividual;
import net.sf.jclec.mo.IConstrained;
import net.sf.jclec.mo.IMOEvaluator;
import net.sf.jclec.mo.comparator.ConstrainedComparator;
import net.sf.jclec.mo.comparator.MOSolutionComparator;
import net.sf.jclec.mo.comparator.fcomparator.ParetoComparator;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;
import net.sf.jclec.mo.strategy.HypE;

/**
 * A HypE strategy for problems
 * with constraints. Infeasible individuals
 * will have low fitness values and also a
 * constrained dominance relation is established.
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
 * @see HypE
 * @see IConstrained
 * */

public class ConstrainedHypE extends HypE {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -7483624176127061562L;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public ConstrainedHypE(){
		super();
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>This version will use the Pareto
	 * dominance only for feasible individuals.</p>
	 * */
	@Override
	public void createSolutionComparator(Comparator<IFitness>[] components) {
		// Fitness comparator
		ParetoComparator fcomparator = new ParetoComparator(components);
		// Individuals comparator
		MOSolutionComparator comparator = new ConstrainedComparator(fcomparator);
		setSolutionComparator(comparator);
	}

	/**
	 * {@inheritDoc}
	 * <p>Fitness value equal to zero is assigned 
	 * for infeasible individuals.</p> 
	 * */
	@Override
	protected void fitnessAssignment(List<IIndividual> population, List<IIndividual> archive) {
		
		int size = population.size();
		Vector<Double> values;
		Vector<Double> points;

		// Extract and properly order the objective values for feasible individuals
		List<IIndividual> feasibles = new ArrayList<IIndividual>();
		for(IIndividual ind: population){
			if(((IConstrained)ind).isFeasible()){
				feasibles.add(ind);
			}
		}
		size = feasibles.size();
		int indexes [] = new int[size];
		for(int i=0; i<size; i++){
			indexes[i] = i;
		}
		
		// Compute the hypervolume
		if(feasibles.size()>0){
			points = getObjectiveValues(indexes, feasibles);
			values = hypeIndicator(points, size, 0, 1, getSamplingSize(), size);

			// Assign the fitness
			IIndividual ind;
			double fitness;
			int j=0;
			for(int i=0; i<size; i++){
				ind = population.get(i);
				if(((IConstrained)ind).isFeasible()){
					fitness = values.get(j);
					j++;
				}
				else
					fitness = Double.MIN_VALUE;
				((MOFitness)ind.getFitness()).setValue(fitness);
			}
		}
		
		// Infeasible individuals
		else{
			for(IIndividual ind: population){
				((MOFitness)ind.getFitness()).setValue(Double.MIN_VALUE);
			}
		}
	}

	/**
	 * Reduce the size of the critical front using
	 * the hypervolume indicator.
	 * @param individualsFront Individuals in the critical front.
	 * @param nSelect Number of individuals to be selected.
	 * */
	@Override
	protected List<IIndividual> frontReduction(List<IIndividual> individualsFront, int nSelect){

		List<IIndividual> result = new ArrayList<IIndividual>();
		int nObjectives = ((IMOEvaluator)getContext().getEvaluator()).numberOfObjectives();
		Vector<Double> values;
		Vector<Double> points, auxpoints;
		double value, minValue = Double.MAX_VALUE;
		int select = -1;
		int n = individualsFront.size();

		// Compute indicator and remove worst individuals
		// Extract and properly order the objective values for feasible individuals
		List<IIndividual> feasibles = new ArrayList<IIndividual>();
		for(IIndividual ind: individualsFront){
			if(((IConstrained)ind).isFeasible()){
				feasibles.add(ind);
			}
		}
		int size = feasibles.size();
		int indexes [] = new int[size];
		for(int i=0; i<size; i++){
			indexes[i] = i;
		}
		
		// The number of feasible individuals is greater than the required 
		// number of individuals to fill the population, so
		// progressively remove the worst individuals in the set
		// until it has the desired size
		if(size > nSelect){
			
			points = getObjectiveValues(indexes, feasibles);
			
			while(feasibles.size() > nSelect){

				values = hypeIndicator(points, feasibles.size(), 0, 1, getSamplingSize(), n);
				select = -1;
				minValue = Double.MAX_VALUE;

				// Search the worst indicator value
				for(int i=0; i<feasibles.size(); i++){
					value = values.get(i);
					if(value<minValue){
						minValue = value;
						select = i;
					}
				}

				// Remove the individual with lower indicator value
				feasibles.remove(select);
				values.remove(select);

				// Remove its points
				int init = select*nObjectives;
				int end = init + nObjectives;

				for(int i=init; i<end; i++){
					points.set(i,-1.0);
				}
				auxpoints = new Vector<Double>();
				for(int i=0; i<points.size(); i++){
					if(points.get(i)!=-1){
						auxpoints.add(points.get(i));
					}
				}
				points = auxpoints;

				// Decrement the size of the front
				n--; 
			}
			result = feasibles;
		}

		// The number of feasible individuals is less than the required 
		// number of individuals to fill the population, add all and
		// some randomly selected infeasible individuals
		else{
			result = feasibles;
			List<IIndividual> infeasibles = new ArrayList<IIndividual>();
			for(IIndividual ind: individualsFront){
				if(!((IConstrained)ind).isFeasible()){
					infeasibles.add(ind);
				}
			}
			
			int index;
			while(result.size()<nSelect){
				index = getContext().createRandGen().choose(0,infeasibles.size());
				result.add(infeasibles.get(index));
				infeasibles.remove(index);
			}
		}
		return result;
	}
}
