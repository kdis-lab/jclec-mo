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

import java.util.List;

import net.sf.jclec.IIndividual;
import net.sf.jclec.mo.IConstrained;

/**
 * A command to count the number of infeasible solutions within a given population.
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
 * @see Command
 * @see IConstrained
 * */

public class InfeasibleCounter extends Command {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -6080076580717595388L;

	/** Number of infeasible solutions */
	protected int nInfeasibleSolutions;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public InfeasibleCounter() {
		super(null);
	}	

	/**
	 * Parameterized constructor.
	 * @param population The population of solutions.
	 * */
	public InfeasibleCounter(List<IIndividual> population) {
		super(population);
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Get the number of infeasible solutions.
	 * @return Number of infeasible solutions.
	 * */
	public int getNumberInfeasibleSolutions() {
		return this.nInfeasibleSolutions;
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void execute() {
		this.nInfeasibleSolutions = 0;
		IIndividual ind;
		if(this.population!=null) {
			int size = this.population.size();
			for(int i=0; i<size; i++){
				ind = this.population.get(i);
				if(!((IConstrained)ind).isFeasible()){
					this.nInfeasibleSolutions++;
				}
			}
		}
	}
}