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

package net.sf.jclec.mo.indicator;

import java.util.List;

import org.apache.commons.configuration.Configuration;

import net.sf.jclec.IIndividual;

/**
 * The Maximum Pareto Front Error (ME) describes how far are the two sets and how well they 
 * conform in shape. It obtains the largest minimum distance between each solution in the
 * front and its closest solution in the second front. Objective values in the range [0,1]
 * are recommended to compute the distances between solutions.
 * 
 * <p><i>Paper</i>: D.A. Van Veldhuizen, G.B. Lamont. "Multiobjective Evolutionary 
 * Algorithm Test Suites". Proc. ACM Symposium on Applied Computing, pp.351-357. 1999.</p>
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
 * @see BinaryIndicator
 * */
public class MaximumError extends BinaryIndicator {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 3480580133235074995L;

	/** Power to compute the distance */
	protected int p;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor. 
	 * <code>p</code> is set to 2.
	 * */
	public MaximumError(){
		super();
		this.maximized=null;
		this.scaled=true;
		this.p=2;
	}
	
	/**
	 * Parameterized constructor. 
	 * <code>p</code> is set to 2.
	 * @param paretoSet The first Pareto set.
	 * @param paretoSet2 The second Pareto set.
	 * */
	public MaximumError(List<IIndividual> paretoSet, List<IIndividual> paretoSet2) {
		super(paretoSet, paretoSet2, null, true);
		this.p=2;
	}

	/**
	 * Parameterized constructor. 
	 * <code>p</code> is set to 2.
	 * @param paretoSet The first Pareto set.
	 * @param paretoFront2FileName Name of the file that contains the second Pareto front
	 * */
	public MaximumError(List<IIndividual> paretoSet, String paretoFront2FileName) {
		super(paretoSet, paretoFront2FileName, null, true);
		this.p=2;
	}

	/**
	 * Parameterized constructor.
	 * <code>p</code> is set to 2.
	 * @param paretoFrontFileName Name of the file that contains the first Pareto front.
	 * @param paretoSet2 The second Pareto set.
	 * */
	public MaximumError(String paretoFrontFileName, List<IIndividual> paretoSet2) {
		super(paretoFrontFileName, paretoSet2, null, true);
		this.p=2;
	}

	/**
	 * Parameterized constructor. 
	 * <code>p</code> is set to 2.
	 * @param paretoFrontFileName Name of the file that contains the first Pareto front.
	 * @param paretoFront2FileName Name of the file that contains the second Pareto front.
	 * */
	public MaximumError(String paretoFrontFileName, String paretoFront2FileName) {
		super(paretoFrontFileName, paretoFront2FileName, null, true);
		this.p=2;
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- get/set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Set the value of <code>p</code>.
	 * @param p The value that has to be set.
	 * */
	protected void setP(int p){
		this.p=p;
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////
	
	/**
	 * {@inheritDoc}
	 * <p>This indicator requires the following parameters:
	 * <ul>
	 * 	<li>p (<code>Integer</code>): 
	 * 	<p>The power to compute the distance. Default value is 2.</p></li>
	 * </ul>
	 * 
	 * */
	@Override
	public void configure(Configuration settings){
		super.configure(settings);
		int p = settings.getInt("p",2);
		this.setP(p);
	}
	
	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void calculate() {
		double me = -1.0;
		int nSolutions = getNumberOfSolutions();
		double distance;
		// Search the maximum distance
		for(int i=0; i<nSolutions; i++){
			distance = distanceToFront(getSolutionFront(i));
			if(distance>me)
				me=distance;
		}

		this.setResult(me);
	}

	/////////////////////////////////////////////////////////////////
	// -------------------------------------------- Protected methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Compute the distance of a solution to
	 * the closest solution of Pareto front.
	 * @param solution The solution if the true front.
	 * @return Minimum distance of the solution from the
	 * Pareto front.
	 * */
	protected double distanceToFront(double [] solution){
		double minDistance = Double.POSITIVE_INFINITY, distance;
		int nSolutions = this.getNumberOfSolutionsSecondFront();
		// Distance of the solution to each solution in the true Pareto front
		for(int i=0; i<nSolutions; i++){
			distance = distance(solution,getSolutionSecondFront(i));
			if(distance<minDistance)
				minDistance=distance;
		}
		return minDistance;
	}

	/**
	 * Distance between two solutions. The order
	 * of the power is <code>p</code>.
	 * @param solution1 First solution.
	 * @param solution2 Second solution.
	 * @return The distance between both solutions.
	 * */
	protected double distance(double [] solution1, double [] solution2){
		int nObjectives = solution1.length;
		double distance = 0.0;
		for(int i=0; i<nObjectives; i++){
			distance += Math.pow(Math.abs(solution1[i]-solution2[i]), this.p);
		}
		distance = Math.pow(distance, 1/(double)this.p);
		return distance;
	}
}
