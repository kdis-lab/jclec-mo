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

package net.sf.jclec.realarray.rec;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.builder.EqualsBuilder;

import net.sf.jclec.IPopulation;

/**
 * BLXAlpha crossover that generates two descendants from a pair of parent solutions.
 * 
 * <p><i>Paper</i>: L.J. Eshelman, J.D. Schaffer, "Real-coded genetic algorithms and
 * interval-schemata. Foundation of Genetic Algorithms 2, Morgan Kaufmann., San Mateo, pp 187–202, 1993.</p>
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
 * @see UniformCrossover2x2
 * */

public class BLXAlphaCrossover2x2 extends UniformCrossover2x2 {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 1328259755201368164L;

	/** Alpha parameter */
	protected double alpha;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 */

	public BLXAlphaCrossover2x2() {
		super();
	}

	/**
	 * Parameterized constructor.
	 * @param context Execution context.
	 */
	public BLXAlphaCrossover2x2(IPopulation context) {
		super(context);
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Get the alpha value.
	 * @return The value of the alpha parameter.
	 */
	public final double getAlpha() {
		return this.alpha;
	}

	/**
	 * Set the alpha value.
	 * @param alpha The value that has to be set.
	 */
	public final void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>Specific parameters for BLXAlphaCrossover are:
	 * <ul>
	 * 	<li>alpha (<code>integer</code>):<p>Alpha value. Default value is 0.5.</p></li>
	 * </ul>
	 * </p>
	 * */
	@Override
	public void configure(Configuration settings){
		super.configure(settings);
		double alpha = settings.getDouble("[@alpha]", 0.5);
		setAlpha(alpha);
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	protected void recombineLocus(double[] p0_genome, double[] p1_genome,
			double[] s0_genome, double[] s1_genome, int locusIndex) {

		// Calculate the new values
		double cmax = Math.max(p0_genome[locusIndex], p1_genome[locusIndex]);
		double cmin = Math.min(p1_genome[locusIndex], p0_genome[locusIndex]);
		double inf = cmin-(cmax-cmin)*this.alpha;
		double sup = cmax+(cmax-cmin)*this.alpha;
		
		// Set the values in the sons 
		s0_genome[locusIndex] = inf+(this.randgen.raw()*(sup-inf));
		s1_genome[locusIndex] = inf+(this.randgen.raw()*(sup-inf));
		
		// Check the interval and fix the value if required.			
		s0_genome[locusIndex] = this.genotypeSchema[locusIndex].nearestOf(s0_genome[locusIndex]);
		s1_genome[locusIndex] = this.genotypeSchema[locusIndex].nearestOf(s1_genome[locusIndex]);
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	protected double defaultLocusRecProb() {
		return 0.6;
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public boolean equals(Object other){
		if (other instanceof BLXAlphaCrossover2x2) {
			BLXAlphaCrossover2x2 crossover = (BLXAlphaCrossover2x2) other;
			EqualsBuilder eb = new EqualsBuilder();
			eb.append(this.locusRecProb, crossover.locusRecProb);
			eb.append(this.alpha, crossover.alpha);
			return eb.isEquals();
		}
		else {
			return false;
		}
	}
}
