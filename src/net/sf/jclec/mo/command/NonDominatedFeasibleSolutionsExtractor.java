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
import java.util.List;

import net.sf.jclec.IIndividual;
import net.sf.jclec.mo.IConstrained;
import net.sf.jclec.mo.comparator.fcomparator.ParetoComparator;

/**
 * This command extracts the non-dominated solutions of the given population considering
 * that the population can contain both feasible and infeasible solutions.
 * It requires the definition of the dominance principle to be used. Here, any comparator 
 * extending from <code>ParetoComparator</code> can be used. Infeasible solutions will be 
 * discarded before comparing the dominance criterion, so individuals should implement 
 * the <code>IConstrained</code> interface. The given population is not modified during 
 * the execution of the command, since the set of non-dominated solutions is stored in
 * a specific list that can be retrieved using the <code>getNonDominatedSolutions</code> method.
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
 * @see NonDominatedSolutionsExtractor
 * @see ParetoComparator
 * @see IConstrained
 * */

public class NonDominatedFeasibleSolutionsExtractor extends NonDominatedSolutionsExtractor {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -8502858253450576566L;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public NonDominatedFeasibleSolutionsExtractor(){
		super();
	}

	/**
	 * Parameterized constructor.
	 * @param population The set of individuals to work with.
	 * @param comparator The Pareto comparator that has to be used.
	 * */
	public NonDominatedFeasibleSolutionsExtractor(List<IIndividual> population, ParetoComparator comparator) {
		super(population,comparator);
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * This command filters the infeasible solutions before
	 * calling the super method. The resulting set will be stored
	 * in <code>nonDominated</code>, so it can be retrieved using the
	 * <code>getNonDominatedSolutions()</code> method.
	 * */
	@Override
	public void execute() {

		int size = this.population.size();
		IIndividual ind;
		List<IIndividual> feasibleSolutions = new ArrayList<IIndividual>();

		// Select feasible solutions
		for(int i=0; i<size; i++) {
			ind = this.population.get(i);
			if(((IConstrained)ind).isFeasible()){
				feasibleSolutions.add(ind);
			}
		}

		// Call the super method
		super.setPopulation(feasibleSolutions);
		super.execute();
	}
}
