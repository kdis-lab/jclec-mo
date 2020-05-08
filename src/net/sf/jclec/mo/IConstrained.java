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

package net.sf.jclec.mo;

/**
 * This interface provides the required methods that should implement those
 * classes that represent solutions for constrained problems. The use of
 * this interface is expected by the constrained versions of the strategies
 * and specific comparators.
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
 * */
public interface IConstrained {
	
	/**
	 * Get whether the solution is feasible.
	 * @return True if the solution is feasible,
	 * false otherwise.
	 * */
	public boolean isFeasible();
	
	/**
	 * Set whether the solution is feasible.
	 * @param feasible The boolean value that has to be set.
	 * */
	public void setFeasible(boolean feasible);
	
	/**
	 * Get the degree of constraint violation.
	 * @return A double value representing
	 * the degree of infeasibility.
	 * */
	public double degreeOfInfeasibility();
	
	/**
	 * Set the degree of constraint violation.
	 * @param degree The degree of infeasibility that has to be set.
	 * */
	public void setDegreeOfInfeasibility(double degree);
}
