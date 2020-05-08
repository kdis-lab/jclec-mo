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
import net.sf.jclec.mo.strategy.PAR;

/**
 * PAR strategy for constrained problems.
 * 
 * <p><i>Paper</i>: F. Goulart, F. Campelo. 
 * “Preference-guided evolutionary algorithms for many-objective optimization”, 
 * Information Sciences, vol. 329, pp. 236-255. 2015.</p>
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
 * @see PAR
 */

public class ConstrainedPAR extends PAR {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -6589044448263111288L;

	/////////////////////////////////////////////////////////////////
	// -------------------------------------------------- Constructor
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor
	 * */
	public ConstrainedPAR() {
		super();
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>For constrained problems, the Pareto comparator
	 * considering constrains is configured.</p>
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
	 *{@inheritDoc}
	 *<p>For constrained problems, infeasible solutions will have a greater
	 *ASF value (<code>Double.MAX_VALUE</code>) than any feasible solution.</p>
	 * */
	protected double computeASF(IIndividual solution, double[] refPoint) {
		// Feasible solution, call super method
		if(((IConstrained)solution).isFeasible()) {
			return super.computeASF(solution, refPoint);
		}
		// Infeasible solution, return the worst value
		else {
			return Double.MAX_VALUE;
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>This method has been override to exclude infeasible solutions from the preliminary ROI</p>
	 * */
	protected List<IIndividual> findSolutionROI(List<IIndividual> population, List<IIndividual> offspring, 
			List<IIndividual> scaledSolutions, double [] closerObjValues){
		List<IIndividual> roi = new ArrayList<IIndividual>();

		int popSize = population.size();
		int size = scaledSolutions.size();
		int numObjs = closerObjValues.length;
		boolean select;
		double value;
		MOFitness fitness;
		int i=0;
		while(i<size){

			// First, check if the solution is feasible
			if(((IConstrained)(MOFitness)scaledSolutions.get(i)).isFeasible()) {

				// Second, check if the solution satisfies the condition to be selected
				select = true;
				fitness = (MOFitness)scaledSolutions.get(i).getFitness();
				for(int j=0; select && j<numObjs; j++) {
					try {
						value = fitness.getObjectiveDoubleValue(j);
						// Every objective value should be smaller, otherwise, do not select
						if(value > closerObjValues[j]) {
							select = false;
						}
					} catch (IllegalAccessException | IllegalArgumentException e) {
						e.printStackTrace();
					}
				}

				// If the solution should be selected, find if it belongs to the population or offspring set
				// Also, remove from both lists so that is won't be selected anymore
				if(select) {
					scaledSolutions.remove(i);
					size--;
					if(i<popSize) { // The solution is a population member
						roi.add(population.get(i).copy());
						population.remove(i);
						popSize--; // update the barrier between population and offspring
					}
					else { // The solution is an offspring
						roi.add(offspring.get(i-popSize).copy());
						offspring.remove(i-popSize);
					}
				}
				else {
					i++;
				}
			}
			else {
				i++;
			}
		}
		return roi;
	}
}
