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
import net.sf.jclec.mo.IMOEvaluator;
import net.sf.jclec.mo.command.CrowdingDistanceCalculator;
import net.sf.jclec.mo.command.NonDominatedFeasibleSolutionsExtractor;
import net.sf.jclec.mo.command.PopulationShuffler;
import net.sf.jclec.mo.command.PopulationSorter;
import net.sf.jclec.mo.comparator.ConstrainedComparator;
import net.sf.jclec.mo.comparator.CrowdingDistanceComparator;
import net.sf.jclec.mo.comparator.MOSolutionComparator;
import net.sf.jclec.mo.comparator.fcomparator.EpsilonDominanceComparator;
import net.sf.jclec.mo.comparator.fcomparator.MOFitnessComparator;
import net.sf.jclec.mo.comparator.fcomparator.ParetoComparator;
import net.sf.jclec.mo.evaluation.Objective;
import net.sf.jclec.mo.strategy.SMPSO;

/**
 * A variant of the SMPSO strategy for problems with constraints.
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
 * @see SMPSO
 * */

public class ConstrainedSMPSO extends SMPSO {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -4348573282897428418L;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public ConstrainedSMPSO(){
		super();
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>A constrained comparator is created at the solution level.</p>
	 * */
	@Override
	public void createSolutionComparator(Comparator<IFitness>[] components) {

		// Archive comparator
		MOFitnessComparator fcomparator = new EpsilonDominanceComparator(components);
		this.archiveComparator = new MOSolutionComparator(fcomparator);

		// Individuals comparator
		fcomparator = new ParetoComparator(components);
		MOSolutionComparator comparator = new ConstrainedComparator(fcomparator);
		setSolutionComparator(comparator);
	}

	/**
	 * {@inheritDoc}
	 * <p>Commands that discard infeasible solutions are created.</p>
	 * */
	@Override
	protected void createCommands(){
		// Create the extractor command that only extract feasible solutions
		this.extractor = new NonDominatedFeasibleSolutionsExtractor();
		this.extractor.setComparator((ParetoComparator)this.archiveComparator.getFitnessComparator());

		// Create the crowding distance calculator command
		this.dCalculator = new CrowdingDistanceCalculator();
		this.dCalculator.setComparator(getSolutionComparator().getFitnessComparator());
		List<Objective> objectives = ((IMOEvaluator)getContext().getEvaluator()).getObjectives();
		int size = objectives.size();
		double [] minValues = new double[size];
		double [] maxValues = new double[size];
		for(int i=0; i<size; i++){
			minValues[i] = ((IMOEvaluator)getContext().getEvaluator()).getObjectives().get(i).getMinimum();
			maxValues[i] = ((IMOEvaluator)getContext().getEvaluator()).getObjectives().get(i).getMaximum();
		}
		this.dCalculator.setMinValues(minValues);
		this.dCalculator.setMaxValues(maxValues);

		// Create the auxiliary commands
		this.sortCommand = new PopulationSorter();
		this.sortCommand.setComparator(new CrowdingDistanceComparator());
		this.sortCommand.setInverse(true); // order from max distance to min distance
		this.suffleCommand = new PopulationShuffler();
		this.suffleCommand.setRandGen(getContext().createRandGen());
	}
}
