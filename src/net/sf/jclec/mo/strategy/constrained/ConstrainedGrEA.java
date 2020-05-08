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

import java.util.Comparator;
import java.util.List;

import net.sf.jclec.IFitness;
import net.sf.jclec.IIndividual;
import net.sf.jclec.fitness.SimpleValueFitness;
import net.sf.jclec.mo.IConstrained;
import net.sf.jclec.mo.IMOEvaluator;
import net.sf.jclec.mo.comparator.ConstrainedComparator;
import net.sf.jclec.mo.comparator.fcomparator.ParetoComparator;
import net.sf.jclec.mo.evaluation.fitness.IGrEAMOFitness;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;
import net.sf.jclec.mo.strategy.GrEA;
import net.sf.jclec.mo.strategy.util.GridObjectiveProperties;
import net.sf.jclec.mo.strategy.util.Hypercube;

/**
 * A GrEA strategy for problems with constraints. It considers the worst
 * values of the grid properties for infeasible individuals, so feasible
 * individuals will have more opportunities to survive.
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
 * @see GrEA
 * @see IConstrained
 * */
public class ConstrainedGrEA extends GrEA{

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -1844008365032588181L;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public ConstrainedGrEA(){
		super();
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>This version of GrEA requires the configuration of
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
	 * <p>For infeasible individuals, the properties (grid ranking,
	 * grid crowding distance and grid point distance) are set
	 * to worser values than those computed to feasible individuals.</p>
	 * */
	@Override
	protected void fitnessAssignment(List<IIndividual> population, List<IIndividual> archive) {
		int size = population.size();
		double gridRanking, gridCrowdingDist, gridPointDist;
		IIndividual ind;
		IGrEAMOFitness fitness;
		Hypercube grid;
		Boolean isMaximize = isMaximized();
		for(int i=0; i<size; i++){
			ind = population.get(i);
			fitness = (IGrEAMOFitness)ind.getFitness();
			grid = fitness.getHypercube();

			// Feasible individuals, compute properties
			if(((IConstrained)ind).isFeasible()){
				gridRanking = computeGridRanking(grid);
			}

			// Infeasible individuals, set the worst grid ranking
			else{
				if(isMaximize)
					gridRanking = Double.MIN_VALUE;
				else 
					gridRanking = Double.MAX_VALUE;
			}

			// Rest of properties
			gridCrowdingDist = computeGridCrowdingDistance(grid, population);
			gridPointDist = computeGridPointDistance(ind, grid);

			// Save properties
			fitness.setRanking(gridRanking);
			fitness.setCrowdingDistance(gridCrowdingDist);
			fitness.setCoordinatePointDistance(gridPointDist);
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>Only feasible individuals are considered to
	 * compute the grid boundaries, since infeasible
	 * individuals will not be associated to any grid.</p>
	 * */
	@Override
	protected void setGridBoundaries(List<IIndividual> population){
		int nObj = ((IMOEvaluator)getContext().getEvaluator()).getObjectives().size();
		double min, max, lower, upper, width, aux, div = getNumberOfDivisions();
		int size = population.size();
		IIndividual ind;

		// For each objective
		for(int i=0; i<nObj; i++){
			min = Double.POSITIVE_INFINITY;
			max = Double.NEGATIVE_INFINITY;

			// Get minimum and maximum values for this objective
			for(int j=0; j<size; j++){
				ind = population.get(j);
				// Only consider feasible individuals
				if(((IConstrained)ind).isFeasible()){
					try {
						aux = ((MOFitness)ind.getFitness()).getObjectiveDoubleValue(i);
						if(aux<min)
							min=aux;
						if(aux>max)
							max=aux;
					} catch (IllegalAccessException | IllegalArgumentException e) {
						e.printStackTrace();
					}
				}
			}

			// Set boundaries and width
			aux = ((max-min)/(2*div));
			lower = min - aux;
			upper = max + aux;
			width = (upper-lower)/div;

			super.spaceDivProperties.add(new GridObjectiveProperties(lower, upper, width));
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>For infeasible individuals, grids are
	 * set to worser values than any feasible
	 * individual.</p>
	 * */
	@Override
	protected void gridAssignment(List<IIndividual> population){

		double value;
		int nObj = ((IMOEvaluator)getContext().getEvaluator()).getObjectives().size();
		int size = population.size();
		double bound, width;
		IIndividual ind;
		double fitnessValue;
		boolean isMaximize = isMaximized();

		// Compute the grid for each individual and objective
		for(int i=0; i<size; i++){
			IFitness gridValues [] = new IFitness[nObj];
			ind = population.get(i);

			// Feasible individuals
			if(((IConstrained)ind).isFeasible()){
				for(int j=0; j<nObj; j++){
					try {
						fitnessValue = ((MOFitness)ind.getFitness()).getObjectiveDoubleValue(j);
						bound = this.spaceDivProperties.get(j).getLowerBound();
						width = this.spaceDivProperties.get(j).getWidth();
						if(width!=0)
							value = (fitnessValue-bound)/width;
						else
							value = 0;	// only one grid
						if(isMaximize){
							value = Math.ceil(value);
						}
						else{
							value = Math.floor(value);
						}
						gridValues[j] = new SimpleValueFitness(value);
					} catch (IllegalAccessException
							| IllegalArgumentException e) {
						e.printStackTrace();
					}
				}
			}
			// Special treatment of infeasible individuals
			else{
				if(isMaximize)
					for(int j=0; j<nObj; j++)
						gridValues[j] = new SimpleValueFitness(Double.MIN_VALUE);

				else
					for(int j=0; j<nObj; j++)
						gridValues[j] = new SimpleValueFitness(Double.MAX_VALUE);
			}
			
			((IGrEAMOFitness)ind.getFitness()).setHypercube(new Hypercube(gridValues));
		}
	}
}