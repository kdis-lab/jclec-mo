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
import net.sf.jclec.mo.IConstrained;
import net.sf.jclec.mo.comparator.ConstrainedComparator;
import net.sf.jclec.mo.comparator.fcomparator.ParetoComparator;
import net.sf.jclec.mo.strategy.SMSEMOA;

/**
 * A SMS-EMOA strategy for problems with constraints. It overrides the reduction
 * method in order to consider that infeasible individuals should be removed
 * before considering the S contribution of feasible individuals. The parent
 * selector should be carefully chosen as this algorithm does not distinguish
 * infeasible from feasible individuals regarding their fitness values. Nevertheless,
 * the comparator of individuals is created to deal with constrains.
 * 
 * <p>HISTORY:
 * <ul>
 *  <li>(AR|JRR|SV, 1.0, March 2018)		First release.</li>
 * </ul>
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
 * @see SMSEMOA
 * @see IConstrained
 * @see ConstrainedComparator
 * */
public class ConstrainedSMSEMOA extends SMSEMOA {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 3369357722261595701L;

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>This version of SMS-EMOA uses a Pareto comparator
	 *  for constrained problems.</p>
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
	 * Select the individual that should be discarded from the given
	 * population. If there are any infeasible solution, it will be
	 * selected. If not, the S metric contribution is used to decide.
	 * @param population The population of individuals.
	 * @return The individual to be removed.
	 * */
	@Override
	protected int reduce(List<IIndividual> population){

		int indexToRemove = -1;
		int size = population.size();

		// Select the first infeasible individual
		for(int i=0; indexToRemove==-1 && i<size; i++){
			if(!((IConstrained)population.get(i)).isFeasible()){
				indexToRemove = i;
			}
		}
		if(indexToRemove!=-1){
			return indexToRemove;
		}

		// All the individuals are feasible, apply the original implementation
		else{
			return super.reduce(population);
		}
	}
}
