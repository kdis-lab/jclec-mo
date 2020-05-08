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

import java.util.StringTokenizer;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang.builder.EqualsBuilder;

import net.sf.jclec.IConfigure;
import net.sf.jclec.binarray.BinArraySpecies;

/**
 * The species that can create instances of <code>BinArrayConstrainedIndividual</code>.
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
 * @see BinArrayConstrainedIndividual
 * */

public class BinArrayConstrainedIndividualSpecies extends BinArraySpecies implements IConfigure {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 1685831245197237615L;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 * */
	public BinArrayConstrainedIndividualSpecies() {
		super();
	}

	/**
	 * Parameterized constructor.
	 * @param genotypeSchema The genotype schema.
	 */

	public BinArrayConstrainedIndividualSpecies(byte [] genotypeSchema) {
		super();
		setGenotypeSchema(genotypeSchema);
	}

	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Get/set methods
	/////////////////////////////////////////////////////////////////

	/**
	 * Set the genotype schema.
	 * @param genotypeSchema The genotype schema that has to be set.
	 */
	public void setGenotypeSchema(byte [] genotypeSchema) {
		this.genotypeSchema = genotypeSchema;
	}

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------- Override methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 * <p>Specific parameters for this species are:
	 * <ul>
	 * <li>
	 * <code>[@genotype-length] (integer)</code></p>
	 *  Genotype length. The length of the genotype.
	 * </li>
	 * <li>
	 * <code>genotype-schema  (String)</code></p>
	 * Genotype schema. This parameter contains the characters that should
	 * be used to represent the schema elements.
	 * </li>
	 * </ul>
	 * </p> 
	 */

	public void configure(Configuration configuration) {
		// Genotype schema
		byte [] genotypeSchema;
		// Get 'length' parameter
		int genotypeLength = configuration.getInt("[@genotype-length]", 0);
		if (genotypeLength == 0) {
			// Genotype schema string
			String genotypeSchemaString = configuration.getString("genotype-schema");
			// Parses genotype-schema
			StringTokenizer st = new StringTokenizer(genotypeSchemaString);
			int gl = st.countTokens();
			genotypeSchema = new byte[gl];
			for (int i=0; i<gl; i++) {
				String nt = st.nextToken(); 
				if (nt.equals("*")) {
					genotypeSchema[i] = -1;
				}
				else {
					genotypeSchema[i] = Byte.parseByte(nt);
				}
			}			
		}
		else {
			// Allocate space for schema
			genotypeSchema = new byte[genotypeLength]; 
			// Set default values for schema
			for (int i=0; i<genotypeLength; i++) {
				genotypeSchema[i] = -1;
			}
		}
		// Set genotype schema
		setGenotypeSchema(genotypeSchema);
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public BinArrayIndividual createIndividual(byte[] genotype) {
		return new BinArrayConstrainedIndividual(genotype);
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public boolean equals(Object other){
		if (other instanceof BinArrayConstrainedIndividualSpecies) {
			EqualsBuilder eb = new EqualsBuilder();
			BinArrayConstrainedIndividualSpecies baoth = (BinArrayConstrainedIndividualSpecies) other;
			eb.append(this.genotypeSchema, baoth.genotypeSchema);
			return eb.isEquals();
		}
		else {
			return false;
		}
	}
}
