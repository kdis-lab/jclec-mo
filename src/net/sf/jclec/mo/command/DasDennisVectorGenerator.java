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

/**
 * A command to generate a set of weight vectors using Das and Dennis' method. It
 * generates points in a normalized hyperplane considering the configured number
 * of divisions <code>p1</code>. An inner layer can be also considered if a second
 * number of divisions, <code>p2</code>, is configured. This method is used by
 * reference-based many-objective algorithms like NSGA-III.
 * 
 * <p><i>Paper</i>:I. Das, J. Dennis,
 *  “Normal-boundary intersection: A new method for generating the Pareto surface 
 *  in nonlinear multicriteria optimization problems”, 
 *  SIAM J. Optimization, vol. 8, no. 3, pp. 631–657, 1998.</p>
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
 * */

public class DasDennisVectorGenerator extends Command {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 7438332004888876450L;

	/** Reference vectors */
	protected List<double []> referenceVectors;

	/** Number of objetives in the problem */
	protected int numberOfObjectives;

	/** Number of divisions in the outer layer */
	protected int p1;

	/** Number of divisions in the inner layer */
	protected int p2;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor
	 * */
	public DasDennisVectorGenerator() {
		super(null);
		this.p1 = -1;
		this.p2 = -1;
	}

	/**
	 * Parameterized constructor
	 * @param numObjs Number of objectives
	 * @param p Number of divisions
	 * */
	public DasDennisVectorGenerator(int numObjs, int p) {
		super(null);
		this.numberOfObjectives = numObjs;
		this.p1 = p;
		this.p2 = -1;
	}

	/**
	 * Parameterized constructor
	 * @param numObjs Number of objectives
	 * @param p1 Number of divisions in the outer layer
	 * @param p2 Number of divisions in the inner layer
	 * */
	public DasDennisVectorGenerator(int numObjs, int p1, int p2) {
		super(null);
		this.numberOfObjectives = numObjs;
		this.p1 = p1;
		this.p2 = p2;
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Set the number of objectives
	 * @param numObjs Number of objectives
	 * */
	public void setNumberOfObjectives(int numObjs) {
		this.numberOfObjectives = numObjs;
	}

	/**
	 * Set the number of divisions in the outer layer
	 * @param p Number of divisions
	 * */
	public void setP1(int p) {
		this.p1=p;
	}

	/**
	 * Set the number of divisions in the inner layer
	 * @param p Number of divisions
	 * */
	public void setP2(int p) {
		this.p2=p;
	}

	/**
	 * Get the generated vectors
	 * @param List of reference vectors
	 * */
	public List<double []> getUniformVectors(){
		return this.referenceVectors;
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void execute() {
		ArrayList<double []> pointsBoundary = new ArrayList<double []>();
		ArrayList<double []> pointsInside = null;
		double [] point = new double [this.numberOfObjectives];
		int nPoints, nPointsBoundary, nPointsInside = 0; 
		double center, coordinate;

		// Firstly, generate the reference points in the boundary layer
		for(int i=0; i<this.numberOfObjectives; i++)
			point[i] = -1.0;

		generateReferencePoints(pointsBoundary,point,this.numberOfObjectives,this.p1,this.p1,0);
		nPointsBoundary = pointsBoundary.size();

		// If a second layer is defined, generate the reference point in the inside layer
		if(this.p2!=-1){
			pointsInside = new ArrayList<double []>();
			generateReferencePoints(pointsInside,point,this.numberOfObjectives,this.p2,this.p2,0);
			center = 1.0/this.numberOfObjectives;
			nPointsInside = pointsInside.size();
			for(int i=0; i<nPointsInside; i++){
				for(int j=0; j<this.numberOfObjectives; j++){
					// update the center coordinate
					coordinate = (center + pointsInside.get(i)[j]/2);
					pointsInside.get(i)[j] = coordinate;
				}
			}
			nPointsInside = pointsInside.size();
		}

		// Set all the reference points
		nPoints = nPointsBoundary+nPointsInside;
		double [][] matrixPoints = new double[nPoints][this.numberOfObjectives];
		for(int i=0; i<nPointsBoundary; i++){
			for(int j=0; j<this.numberOfObjectives; j++){
				matrixPoints[i][j] = pointsBoundary.get(i)[j];
			}
		}
		if(this.p2!=-1){
			for(int i=nPointsBoundary, i2=0; i<nPoints; i++, i2++){
				for(int j=0; j<this.numberOfObjectives; j++){
					matrixPoints[i][j] = pointsInside.get(i2)[j];
				}
			}
		}

		double [] refVector;
		this.referenceVectors = new ArrayList<double []>();
		for(int i=0; i<matrixPoints.length; i++) {
			refVector = new double[matrixPoints[i].length];
			for(int j=0; j<matrixPoints[i].length; j++) {
				refVector[j] = matrixPoints[i][j];
			}
			this.referenceVectors.add(refVector);
		}
	}

	/////////////////////////////////////////////////////////////////
	// -------------------------------------------- Protected methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Recursively generate the reference points in the boundary layer.
	 * @param points The set of points.
	 * @param point Current point.
	 * @param nObjectives Number of objectives.
	 * @param left
	 * @param total
	 * @param element Current dimension.
	 * */
	protected void generateReferencePoints(ArrayList<double []> points, double[] point,
			int nObjectives, int left, int total, int element){

		double [] copy;
		if(element == nObjectives-1){
			point[element]=(double)left/(double)total;
			copy = new double[nObjectives];
			for(int i=0; i<nObjectives; i++)
				copy[i] = point[i];
			points.add(copy);
		}

		else{
			for(int i=0; i<=left; i++){
				point[element]=(double)i/(double)total;
				generateReferencePoints(points, point, nObjectives, left-i, total, element+1);
			}
		}
	}
}
