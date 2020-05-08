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

package net.sf.jclec.mo.strategy.util;

import org.apache.commons.lang.builder.ToStringBuilder;

import net.sf.jclec.IFitness;
import net.sf.jclec.JCLEC;
import net.sf.jclec.fitness.IValueFitness;

/**
 * Auxiliary class to store and manage hypercubes (grids).
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
 * @see SSeMOEA
 * @see GrEA
 * */

public class Hypercube implements JCLEC{

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 6510040503840061243L;

	/** Assigned hypercube to each objective value */
	protected IFitness [] values;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Parameterized constructor.
	 * @param values Hypercube values.
	 * */
	public Hypercube(IFitness [] values){
		setValues(values);
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Get hypercube values.
	 * @return Current values.
	 * */
	public IFitness [] getValues(){
		return this.values;
	}

	/**
	 * Set hypercube values.
	 * @param values New hypercube values.
	 * */
	public void setValues(IFitness [] values){
		int size = values.length;
		this.values = new IFitness [size];
		for(int i=0; i<size; i++){
			this.values[i] = values[i];
		}
	}

	/**
	 * Get the hypercube value at the specified position.
	 * @param index Index of the value to be retrieved.
	 * @return Hypercube value at position <code>index</code>.
	 * */
	public IFitness getValue(int index){
		return this.values[index];
	}

	/**
	 * Set an hypercube value.
	 * @param value The value that has to be set.
	 * @param index Index for the new value.
	 * */
	public void setValue(IFitness value, int index){
		if(index<this.values.length && index>0)
			this.values[index]=value;
	}

	/////////////////////////////////////////////////////////////////
	// ----------------------------------------------- Public methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Check if the hypercube is equal to other hypercube.
	 * @param other The other hypercube
	 * @return True if both objects represents the same hypercube,
	 * false otherwise.
	 * */
	public boolean equals(Hypercube other){
		boolean equals=true;
		IFitness values1 [] = other.getValues();
		double simpleValue0, simpleValue1;
		if(this.values.length!=values1.length)
			equals=false;
		else{
			for(int i=0; equals && i<this.values.length; i++){
				simpleValue0 = ((IValueFitness)this.values[i]).getValue();
				simpleValue1 =  ((IValueFitness)values1[i]).getValue();
				if(Math.abs(simpleValue0-simpleValue1)>0.000000001)
					equals=false;
			}
		}
		return equals;
	}

	/**
	 * Return a copy of the hypercube.
	 * @return A new hypercube with the same values 
	 * than the current object.
	 * */
	public Hypercube copy(){
		IFitness [] newValues = new IFitness [this.values.length];
		for(int i=0; i<this.values.length; i++)
			newValues[i] = this.values[i];
		return new Hypercube(newValues);
	}
	
	/**
	 * Return a string representation of the hypercube.
	 * @return String with the hypercube values.
	 * */
	@Override
	public String toString() {
		ToStringBuilder tsb = new ToStringBuilder(this);
		tsb.append("hypercubes", this.values);
		return tsb.toString();
	}
}