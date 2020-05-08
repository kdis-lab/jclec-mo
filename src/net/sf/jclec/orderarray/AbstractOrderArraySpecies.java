package net.sf.jclec.orderarray;

import net.sf.jclec.util.intset.IIntegerSet;

/**
 * Abstract implementation for <code>IOrderArraySpecies</code>.
 * This class belongs to the JCLEC-4 tutorial.
 * */
public abstract class AbstractOrderArraySpecies implements IOrderArraySpecies {
	
	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -2860189329462902082L;
	
	/** Schema */
	protected IIntegerSet [] genotypeSchema;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor
	 */
	public AbstractOrderArraySpecies(){
		super();
	}

	/////////////////////////////////////////////////////////////////
	// ----------------------------------------------- Public methods
	/////////////////////////////////////////////////////////////////

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getGenotypeLength(){
		return this.genotypeSchema.length;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IIntegerSet [] getGenotypeSchema(){
		return this.genotypeSchema;
	}
}
