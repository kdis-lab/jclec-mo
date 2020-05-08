package net.sf.jclec.orderarray;

import net.sf.jclec.ISpecies;
import net.sf.jclec.base.AbstractMutator;
import net.sf.jclec.util.intset.IIntegerSet;

/**
 * OrderArrayIndividual (and subclasses) specific mutator.  
 * This class belongs to the JCLEC-4 tutorial.
 * */
public abstract class OrderArrayMutator extends AbstractMutator {
	
	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 1606583104193731031L;

	/** Individuals species */
	protected transient IOrderArraySpecies species; 

	/** Individuals schema */
	protected transient IIntegerSet [] schema;
	
	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////
	
	/**
	 * Empty constructor.
	 */
	public OrderArrayMutator() {
		super();
	}

	/////////////////////////////////////////////////////////////////
	// -------------------------------------------- Protected methods
	/////////////////////////////////////////////////////////////////
	
	// AbstractMutator methods
	
	/**
	 * {@inheritDoc}
	 */
	@Override	
	protected void prepareMutation(){
		ISpecies spc = context.getSpecies();
		if (spc instanceof IOrderArraySpecies) {
			// Sets individual species
			this.species = (IOrderArraySpecies) spc;
			// Sets genotype schema
			this.schema = this.species.getGenotypeSchema();
		}
		else {
			throw new IllegalStateException("Invalid species in context");
		}
	}

	/**
	 * Flip method.
	 * @param chrom Chromosome affected
	 * @param point1, point2, the point to change
	 */
	protected final void flip(int [] chrom, int point1, int point2){		
		// New locus value
		int aux;;
		// Choose mutated value
		aux = chrom[point1];
		chrom[point1] = chrom[point2];
		chrom[point2] = aux;
	}	
}