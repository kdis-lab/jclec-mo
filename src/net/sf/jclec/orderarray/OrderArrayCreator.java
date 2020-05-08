package net.sf.jclec.orderarray;

import net.sf.jclec.ISpecies;
import net.sf.jclec.base.AbstractCreator;
import net.sf.jclec.util.intset.IIntegerSet;

/**
 * Creation of <code>OrderArrayIndividual</code> (and subclasses).
 * This class belongs to the JCLEC-4 tutorial.
 * */
public class OrderArrayCreator extends AbstractCreator {

	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////
	
	/** Serial ID */
	private static final long serialVersionUID = -2638928425169895614L;
	
	/** Associated species */
	protected transient IOrderArraySpecies species;
	
	/** Genotype schema */
	protected transient IIntegerSet [] schema;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////
	
	/**
	 * Empty constructor.
	 */
	public OrderArrayCreator() {
		super();
	}

	/////////////////////////////////////////////////////////////////
	// ----------------------------------------------- Public methods
	/////////////////////////////////////////////////////////////////
	
	// java.lang.Object methods
	/**
	 * {@inheritDoc}
	 * */
	@Override
	public boolean equals(Object other){
		if (other instanceof OrderArrayCreator){
			return true;
		}
		else {
			return false;
		}
	}

	/////////////////////////////////////////////////////////////////
	// -------------------------------------------- Protected methods
	/////////////////////////////////////////////////////////////////

	// AbstractCreator methods

	/**
	 * {@inheritDoc}
	 * */
	@Override
	protected void prepareCreation() {
		ISpecies spc = context.getSpecies();
		if (spc instanceof IOrderArraySpecies) {
			// Sets individual species
			this.species = (IOrderArraySpecies) spc;
			// Sets genotype schema
			this.schema = this.species.getGenotypeSchema();
		}
		else {
			throw new IllegalStateException("Illegal species in context");
		}
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	protected void createNext() {
		createdBuffer.add(species.createIndividual(createGenotype()));
	}
	
	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------- Private methods
	/////////////////////////////////////////////////////////////////
	
	/**
	 * Create a int [] genotype, filling it randomly
	 */
	private final int[] createGenotype(){
		// Genotype length
		int gl = schema.length;
		// New genotype
		int [] genotype = new int[gl];
		
        int [] route = new int[gl];
        
		for(int i=0; i<gl; i++)
        	  route[i] = i;

		int position = randgen.choose(0, gl-1);
	
		genotype[0] = route[position];

		removeNode(route, position,0);

        for(int i=1;  i<gl;  i++)
        {
        	position = randgen.choose(i, gl-1);
        	genotype[i] = route[position];
        	removeNode(route, position, i);
       	}
		
		return genotype;
	}	
	
	/**
	 * Remove a node in the route.
	 * @param route The route.
	 * @param position The position index to be removed.
	 * @param index The position index to replace.
	 * */
	private void removeNode(int [] route, int position, int index){
		int aux;
		aux = route[position];
	    route[position] = route[index];
	    route[index] = aux;
	}
}