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

package net.sf.jclec.realarray.mut;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.builder.EqualsBuilder;

import net.sf.jclec.IPopulation;
import net.sf.jclec.realarray.RealArrayIndividualSpecies;
import net.sf.jclec.realarray.UniformMutator;
import net.sf.jclec.util.range.Interval;

/**
 * Polynomial Mutator.
 * 
 * <p><i>Paper</i>: K. Deb, M. Goyal, "A combined genetic adaptive search (GeneAS) 
 * for engineering design."  Computer Science and Informatics, vol.26(4), pp. 30-45, 1996.
 * 
 * <p>HISTORY:
 * <ul>
 *	<li>(AR|JRR|SV, 1.0, March 2018)		First release.</li>
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
 * @see UniformMutator
 * */

public class PolynomialMutator extends UniformMutator {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -7883406359914980425L;

	/** The exponential term for computing the mutated value */
	private double exp;
	
	protected double distributionIndex;
	
	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public PolynomialMutator() {
		super();
	}

	/**
	 * Parameterized constructor.
	 * @param context Execution context.
	 * */
	public PolynomialMutator(IPopulation context){
		super();
		this.context = context;
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Get the distribution index.
	 * @return The distribution index.
	 */
	public double getDistributionIndex() {
		return this.distributionIndex;
	}
	
	/**
	 * Set the distribution index.
	 * @param distributionIndex The value that has to be set.
	 */
	public void setDistributionIndex(double distributionIndex) {
		this.distributionIndex = distributionIndex;
	}
	
	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void configure(Configuration settings) {
		super.configure(settings);
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public boolean equals(Object other) {
		if (other instanceof PolynomialMutator) {
			PolynomialMutator mutator = (PolynomialMutator) other;
			EqualsBuilder eb = new EqualsBuilder();
			eb.append(this.locusMutProb, mutator.locusMutProb);
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
	public void prepareMutation() {
		if(this.locusMutProb==-1)
			setLocusMutProb(defaultLocusMutProb());
		
		this.exp = 1/(this.distributionIndex+1);
		
		super.prepareMutation();
	}

    /**
	 * {@inheritDoc}
	 */
	@Override
	protected void doLocusMutation(double[] parentChromosome, double[] mutantChromosome, int locusIndex) {
		
		double delta, locusUpper, locusLower, newValue, rnd, xy, value, dif, delta1, delta2;
		
		// The bounds of the locus
		Interval interval = (Interval)((RealArrayIndividualSpecies)this.context.getSpecies()).getGenotypeSchema()[locusIndex];
		locusLower = interval.getLeft();
		locusUpper = interval.getRight();
		
		dif = locusUpper-locusLower;
		delta1 = (parentChromosome[locusIndex] - locusLower) / dif;
		delta2 = (locusUpper - parentChromosome[locusIndex]) / dif;
		
		rnd = this.randgen.raw();
		if(rnd < 0.5){
			xy = 1.0-delta1;
			value =2.0*rnd + (1.0-2.0*rnd) * Math.pow(xy, this.distributionIndex+1);
			delta = Math.pow(value,this.exp)-1.0;
		}
		else{
			xy = 1.0-delta2;
			value = 2.0*(1.0-rnd) + 2.0*(rnd-0.5)*Math.pow(xy, this.distributionIndex+1);
			delta = 1 - Math.pow(value,this.exp);
		}
		
		// Mutate the locus and check the locus bounds
		newValue = parentChromosome[locusIndex] + delta*(locusUpper-locusLower);
		mutantChromosome[locusIndex] = interval.nearestOf(newValue);	
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected double defaultLocusMutProb() {
		int nDecisionVariables;
		if(this.context!=null){
			nDecisionVariables = ((RealArrayIndividualSpecies)this.context.getSpecies()).getGenotypeLength();
			return (1.0/(double)nDecisionVariables);
		}
		else // not available yet
			return -1;
	}
}
