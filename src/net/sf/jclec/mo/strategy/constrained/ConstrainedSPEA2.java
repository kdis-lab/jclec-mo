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
import net.sf.jclec.mo.evaluation.fitness.MOFitness;
import net.sf.jclec.mo.strategy.SPEA2;

/**
 * A SPEA2 strategy for problems with constraints. This class overrides
 * the fitness assignment method. More specifically, it sets the worst fitness
 * value to infeasible individuals, so these individuals are not included in the 
 * archive although they represent non-dominated solutions.
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
 * @see SPEA2
 * @see IConstrained
 * */

public class ConstrainedSPEA2 extends SPEA2 {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 8062799503785813716L;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public ConstrainedSPEA2(){
		super();
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>SPEA2 uses a Pareto comparator.</p>
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
	 * {@inheritDoc}
	 * <p> This constrained version sets the worser fitness 
	 * value to infeasible individuals. It guarantees that 
	 * infeasible individuals will not be included in the 
	 * archive.</p>
	 * */
	@Override
	protected void fitnessAssignment(List<IIndividual> population, List<IIndividual> archive) {
		// Set fitness with the super class method
		super.fitnessAssignment(population, archive);

		// Set a worser fitness value to infeasible individuals
		int size = population.size();
		IIndividual individual;
		for(int i=0; i<size; i++){
			individual = population.get(i);
			if(!((IConstrained)individual).isFeasible()){
				((MOFitness)individual.getFitness()).setValue(Double.MAX_VALUE);
			}
		}
		if(archive!=null){
			size = archive.size();
			for(int i=0; i<size; i++){
				individual = archive.get(i);
				if(!((IConstrained)individual).isFeasible()){
					((MOFitness)individual.getFitness()).setValue(Double.MAX_VALUE);
				}
			}
		}
	}
}
