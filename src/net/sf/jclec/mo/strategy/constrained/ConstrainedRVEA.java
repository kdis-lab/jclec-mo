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
import net.sf.jclec.mo.command.InfeasibleCounter;
import net.sf.jclec.mo.comparator.ConstrainedComparator;
import net.sf.jclec.mo.comparator.fcomparator.ParetoComparator;
import net.sf.jclec.mo.evaluation.fitness.MOFitness;
import net.sf.jclec.mo.strategy.MOStrategy;
import net.sf.jclec.mo.strategy.RVEA;

/**
 * The RVEA strategy for problems with constraints.
 *
 * <p><i>Paper</i>: R. Cheng, Y. Jin, M. Olhofer, B. Sendhoff. 
 * “A Reference Vector Guided Evolutionary Algorithm for Many-Objective Optimization”, 
 * IEEE Transactions on Evolutionary Computation, vol. 20, no. 5, pp. 773-791. 2016.</p>
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
 * @see MOStrategy
 */

public class ConstrainedRVEA extends RVEA {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -6614462937097707770L;

	/** Command to count infeasible solutions */
	private InfeasibleCounter command;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public ConstrainedRVEA(){
		super();
		this.command = new InfeasibleCounter(null);
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>In case it is needed, this version of RVEA uses a Pareto comparator
	 *  for constrained problems.</p>
	 * */
	@Override
	public void createSolutionComparator(Comparator<IFitness>[] components) {
		// Fitness comparator
		ParetoComparator fcomparator = new ParetoComparator(components);
		// Solution comparator
		ConstrainedComparator comparator = new ConstrainedComparator(fcomparator);
		setSolutionComparator(comparator);
	}
	
	/**
	 * Choose the best solution for each partition. The best solution
	 * is the one having the minimum APD value.
	 * @param partitions Population split into sublist, one for each reference vector.
	 * @return A list with the best solution for each partition.
	 * */
	protected List<IIndividual> elitistSelection(List<List<IIndividual>> partitions) {
		List<IIndividual> selected = new ArrayList<IIndividual>();
		List<IIndividual> candidates;
		IIndividual solution;
		int n = partitions.size(), nInfeasibles;

		for(int i=0; i<n; i++) {
			candidates = partitions.get(i);
			if(candidates.size()>0) {

				// Count number of infeasible solutions
				this.command.setPopulation(candidates);
				this.command.execute();
				nInfeasibles = this.command.getNumberInfeasibleSolutions();

				// All solutions are infeasible, select the one with less constraint violation
				if(nInfeasibles == candidates.size()) {
					solution = findSolutionMinimumConstraintViolation(candidates);
				}

				// Proceed as non-constrained version, but considering only feasible solutions
				else {
					solution = findSolutionMinimumDistance(candidates);
				}
				selected.add(solution.copy());
			}
		}	
		return selected;
	}

	/**
	 * Find the solution with the minimum APD value from a given list. Infeasible
	 * solutions are not considered.
	 * @param solutions The set of solutions.
	 * @return The solution with minimum APD value.
	 * */
	protected IIndividual findSolutionMinimumDistance(List<IIndividual> solutions) {
		double dist, minDist = Double.POSITIVE_INFINITY;
		int index = 0, size = solutions.size();
		IIndividual solution;
		for(int i=0; i<size; i++) {
			solution = solutions.get(i);
			if(((IConstrained)solution).isFeasible()) {
				dist = ((MOFitness)solutions.get(i).getFitness()).getValue();
				if(dist < minDist) {
					minDist = dist;
					index = i;
				}
			}
		}
		return solutions.get(index);
	}
	
	/**
	 * Find the solution with the minimum constraint violation.
	 * @param solutions The set of solutions.
	 * @return The solution with minimum constraint violation
	 * */
	protected IIndividual findSolutionMinimumConstraintViolation(List<IIndividual> solutions) {
		double cv, minCV = Double.POSITIVE_INFINITY;
		int index = 0, size = solutions.size();
		IIndividual solution;
		for(int i=0; i<size; i++) {
			solution = solutions.get(i);
			if(!((IConstrained)solution).isFeasible()) {
				cv = ((IConstrained)solution).degreeOfInfeasibility();
				if(cv < minCV) {
					minCV = cv;
					index = i;
				}
			}
		}
		return solutions.get(index);
	}
}
