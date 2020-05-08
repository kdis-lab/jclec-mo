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
import net.sf.jclec.mo.IMOEvaluator;
import net.sf.jclec.mo.command.FeasibleCounter;
import net.sf.jclec.mo.comparator.NSGA2ConstrainedComparator;
import net.sf.jclec.mo.comparator.fcomparator.ParetoComparator;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;
import net.sf.jclec.mo.strategy.NSGA3;
import net.sf.jclec.util.random.IRandGen;

/**
 * A NSGA-III strategy for problems with constraints. This class configures
 * a constrained individual comparator that establishes the dominance criterion
 * between feasible and infeasible individuals. It also considers that infeasible
 * individuals should not be used to compute the ideal and extreme points.
 *  
 * <p><i>Paper</i>: H. Jain, K. Deb
 * “An Evolutionary Many-Objective Optimization Algorithm Using
 * Reference-point Based Non-dominated Sorting Approach, Part II:
 * Handling Constraints and Extending to an Adaptive Approach”, 
 * IEEE Transactions on Evolutionary Computation, vol. 18, no. 4, 
 * pp.602-622, 2014.</p>
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
 * @see NSGA3
 * @see NSGA2ConstrainedComparator
 * @see IConstrained
 */
public class ConstrainedNSGA3 extends NSGA3 {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -5443927293828246586L;

	/** Counter command */
	private FeasibleCounter command;
	
	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public ConstrainedNSGA3() {
		super();
		command = new FeasibleCounter(null);
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>This version of NSGA-III requires the configuration of
	 * a comparator for constrained problems.</p>
	 * */
	@Override
	public void createSolutionComparator(
			Comparator<IFitness>[] components) {
		// Fitness comparator
		ParetoComparator fcomparator = new ParetoComparator(components);
		// Individuals comparator
		NSGA2ConstrainedComparator comparator = new NSGA2ConstrainedComparator(fcomparator);
		setSolutionComparator(comparator);
	}

	/**
	 * {@inheritDoc}
	 * <p>In the constrained version, a tournament selection
	 * is performed, considering that feasible individuals
	 * are preferred over infeasible individuals.</p>
	 * */
	@Override
	public List<IIndividual> matingSelection(List<IIndividual> population, List<IIndividual> archive) {
		// Random selection from the current population
		IRandGen randgen = this.getContext().createRandGen();
		int size = population.size();
		List<IIndividual> parents = new ArrayList<IIndividual>();
		IIndividual p1, p2;
		boolean feasible1, feasible2;
		double degree1, degree2;
		
		// Select parents
		
		for(int i=0; i<size; i++){
			// Choose two individuals at random
			p1 = population.get(randgen.choose(0, size));
			p2 = population.get(randgen.choose(1, size));
			
			// Compare the individuals
			feasible1 = ((IConstrained)p1).isFeasible();
			feasible2 = ((IConstrained)p2).isFeasible();
			
			// One individual is feasible
			if(feasible1 && !feasible2){
				parents.add(p1);
			}
			else if (!feasible1 && feasible2){
				parents.add(p2);
			}
			
			// Both individuals are infeasible
			// select according to the constraint violation
			else if (!feasible1 && !feasible2){
				degree1 = ((IConstrained)p1).degreeOfInfeasibility();
				degree2 = ((IConstrained)p2).degreeOfInfeasibility();
				
				if(degree1>degree2)
					parents.add(p2);
				else if (degree1<degree2)
					parents.add(p1);
				
				// The same degree of constraint violation, choose at random
				else if(randgen.coin())
					parents.add(p1);
				else
					parents.add(p2);
					
			}
			
			// Both individuals are feasible, choose at random
			// as in the unconstrained algorithm
			else{
				if(randgen.coin())
					parents.add(p1);
				else
					parents.add(p2);
			}
		}
		
		return parents;
	}
	
	/**
	 * {@inheritDoc}
	 * <p>The constrained version guarantees that only
	 * feasible individuals are considered to compute
	 * the ideal point.</p>
	 * */
	@Override
	protected double computeIdealPoint(List<IIndividual> population, int index){
		double min = Double.POSITIVE_INFINITY;
		double value;
		int size = population.size();

		// If the population contains both feasible and infeasible solutions,
		// this method should only consider feasible ones.
		this.command.setPopulation(population);
		this.command.execute();
		int nFeasibles = this.command.getNumberFeasibleSolutions();
		if(nFeasibles>0 && nFeasibles<size){

			for(IIndividual ind: population){
				if(((IConstrained)ind).isFeasible()){
					try {
						value = ((MOFitness)ind.getFitness()).getObjectiveDoubleValue(index);
						if(value<min){
							min = value;
						}
					} catch (IllegalAccessException | IllegalArgumentException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		// The original algorithm does not mention how
		// to deal that situation (all individuals are infeasible)
		// so call the unconstrained version. It is also invoked if
		// all individuals are feasible to avoid unnecessary conditional structures
		else{
			min = super.computeIdealPoint(population, index);
		}
		return min;
	}

	/**
	 * Find the extreme points in each
	 * objective axis. This point is
	 * identified by finding the solution
	 * with the minimum ASF (achievement scalarized
	 * function).
	 * @param population The set of individuals.
	 * @param index Objective position.
	 * @return Extreme points.
	 * */
	@Override
	protected double [][] computeExtremePoints(List<IIndividual> population, double [] idealPoint){

		int nObjectives = ((IMOEvaluator)getContext().getEvaluator()).numberOfObjectives();
		double asf, minAsf;
		double [] weights = new double[nObjectives];
		double [][] points = new double[nObjectives][nObjectives];
		int size = population.size();
		IIndividual ind;
		int index;

		// If the population contains both feasible and infeasible solutions,
		// this method should only consider feasible ones.
		this.command.setPopulation(population);
		this.command.execute();
		int nFeasibles = this.command.getNumberFeasibleSolutions();
		if(nFeasibles>0 && nFeasibles<size){

			// Compute ASF for each individual
			for(int i=0; i<nObjectives; i++){

				// Set the weights of the axis direction, e.g. (1,0,0), (0,1,0), (0,0,1),
				// where 0 is replaced by 10E-6
				for(int k=0; k<nObjectives; k++){
					weights[k]=0.000001;
				}
				weights[i] = 1.0;
				minAsf = Double.POSITIVE_INFINITY;
				index = -1;

				// Compute ASF for each individual
				for(int j=0; j<size; j++){
					ind = population.get(j);
					asf = computeASF(ind,idealPoint,weights);

					if(((IConstrained)ind).isFeasible()){
						// If ASF value is lower than the current
						// ASF for this axis, update its associated
						// extreme point (the solution)
						if(asf < minAsf){
							minAsf = asf;
							index = j;
						}
					}
				}

				// Save the coordinates of the extreme point for objective i
				for(int k=0; k<nObjectives; k++){
					try {
						points[i][k] = ((MOFitness)population.get(index).getFitness()).getObjectiveDoubleValue(k);
					} catch (IllegalAccessException | IllegalArgumentException e) {
						e.printStackTrace();
					}
				}
			}
		}

		// The original algorithm does not mention how
		// to deal that situation (all individuals are infeasible)
		// so call the unconstrained version. It is also invoked if
		// all individuals are feasible to avoid unnecessary conditional structures
		else{
			points = super.computeExtremePoints(population, idealPoint);
		}

		return points;
	}
}