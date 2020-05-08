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


package net.sf.jclec.mo.comparator;

import net.sf.jclec.IFitness;
import net.sf.jclec.binarray.BinArrayIndividual;
import net.sf.jclec.mo.IConstrained;

/**
 * A dummy class to implement the <code>IConstrained</code> interface
 * that can be used to test comparators for problems with constraints.
 * 
 * <p>HISTORY:
 * <ul>
 *  <li>(AR|JRR|SV, 1.0, March 2018)		First release.</li>
 * </ul>
 * </p>
 * 
 * @version 1.0
 * @author Aurora Ramirez (AR)
 * @author Jose Raul Romero (JRR)
 * @author Sebastian Ventura (SV)
 * 
 * <p>Knowledge Discovery and Intelligent Systems (KDIS) Research Group: 
 * {@link http://www.uco.es/grupos/kdis}</p>
 *  
 * @see BinArrayIndividual
 * @see IConstrained
 * */

public class DummyConstrainedIndividual extends BinArrayIndividual implements IConstrained {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 8102845693260070961L;

	/** The individual is feasible or not */
	private boolean isFeasible;

	/** The degree of infeasibility of the individual */
	private double degreeOfInfeasibility;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public DummyConstrainedIndividual() {
		super();
	}

	/**
	 * Parameterized constructor.
	 * @param genotype The genotype of the individual.
	 * */
	public DummyConstrainedIndividual(byte[] genotype) {
		super(genotype);
	}

	/**
	 * Parameterized constructor.
	 * @param genotype The genotype of the individual.
	 * @param fitness The fitness of the individual.
	 * */
	public DummyConstrainedIndividual(byte[] genotype, IFitness fitness) {
		super(genotype, fitness);
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Set the feasibility of the individual.
	 * @param feasible Value that has to be set.
	 * */
	public void setFeasible(boolean feasible){
		this.isFeasible = feasible;
	}

	/**
	 * Set the degree of infeasibility of the individual.
	 * @param degreeOfInfeasibility Value that has to be set.
	 * */
	public void setDegreeOfInfeasibility(double degreeOfInfeasibility){
		this.degreeOfInfeasibility = degreeOfInfeasibility;
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public boolean isFeasible() {
		return this.isFeasible;
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public double degreeOfInfeasibility() {
		return this.degreeOfInfeasibility;
	}
}
