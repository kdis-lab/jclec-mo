package net.sf.jclec.orderarray;

import net.sf.jclec.IIndividual;
import net.sf.jclec.ISpecies;
import net.sf.jclec.util.intset.IIntegerSet;

/**
 * Species for <code>OrderArrayIndividual</code>.
 * This class belongs to the JCLEC-4 tutorial.
 * */
public interface IOrderArraySpecies extends ISpecies {
	
	/**
	 * Factory method.
	 * @param genotype Individual genotype.
	 * @return A new instance of represented class.
	 */
	
	public IIndividual createIndividual(int [] genotype);
	
	/**
	 * Informs about individual genotype length.
	 * @return getGenotypeSchema().length.
	 */
	
	public int getGenotypeLength();
	
	/**
	 * Get the genotype schema.
	 * @return This genotype schema.
	 */
	public IIntegerSet [] getGenotypeSchema();
}