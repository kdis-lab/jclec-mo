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

package net.sf.jclec.binarray;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import net.sf.jclec.IFitness;
import net.sf.jclec.IIndividual;
import net.sf.jclec.binarray.BinArrayIndividual;
import net.sf.jclec.mo.IConstrained;

/**
 * A binary individual for problems with constraints. It extends the
 * behavior of <code>BinArrayIndividual</code>, including the
 * functionality provided by the <code>IConstrained</code> interface.
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
 * @see BinArrayIndividual
 * @see IConstrained
 * */

public class BinArrayConstrainedIndividual extends BinArrayIndividual implements IConstrained {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 4852649903824201170L;

	/** This property indicates whether the solution is feasible */
	private boolean isFeasible;

	/** The degree of infeasibility */
	private double degreeOfInfeasibility;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public BinArrayConstrainedIndividual() {
		super();
	}

	/**
	 * Parameterized constructor.
	 * @param genotype The genotype of the individual.
	 * */
	public BinArrayConstrainedIndividual(byte[] genotype) {
		super(genotype);
	}

	/**
	 * Parameterized constructor.
	 * @param genotype The genotype of the individual.
	 * @param fitness The fitness of the individual.
	 * */
	public BinArrayConstrainedIndividual(byte[] genotype, IFitness fitness) {
		super(genotype, fitness);
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

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
	public void setFeasible(boolean feasible) {
		this.isFeasible = feasible;
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public double degreeOfInfeasibility() {
		return this.degreeOfInfeasibility;
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void setDegreeOfInfeasibility(double degree) {
		this.degreeOfInfeasibility = degree;
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public IIndividual copy() {

		// Create a copy of the genotype
		int gl = genotype.length;
		byte [] gother = new byte[genotype.length];
		System.arraycopy(genotype, 0, gother, 0, gl);

		// Create a copy and set its properties
		BinArrayConstrainedIndividual other;
		if (fitness != null) {
			other = new BinArrayConstrainedIndividual(gother, fitness.copy());			
		}
		else {
			other = new BinArrayConstrainedIndividual(gother);			
		}
		other.setFeasible(this.isFeasible);
		other.setDegreeOfInfeasibility(this.degreeOfInfeasibility);

		return other;
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public boolean equals(Object other) {
		if (other instanceof BinArrayConstrainedIndividual) {
			BinArrayConstrainedIndividual baother = (BinArrayConstrainedIndividual) other;
			EqualsBuilder eb = new EqualsBuilder();
			eb.append(this.genotype, baother.genotype);
			eb.append(this.fitness, baother.fitness);
			eb.append(this.isFeasible, baother.isFeasible);
			eb.append(this.degreeOfInfeasibility, baother.degreeOfInfeasibility);
			return eb.isEquals();
		}
		else {
			return false;
		}
	}
	
	/**
	 * {@inheritDoc}
	 * */
	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder(this);
		tsb.append("genotype", this.genotype);
		tsb.append("fitness", this.fitness);
		tsb.append("isFeasible", this.isFeasible);
		tsb.append("degreeOfInfeasibility", this.degreeOfInfeasibility);
		return tsb.toString();
	}
}
