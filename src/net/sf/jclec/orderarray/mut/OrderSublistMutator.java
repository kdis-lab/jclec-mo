package net.sf.jclec.orderarray.mut;

import net.sf.jclec.IIndividual;
import net.sf.jclec.base.AbstractIndividual;
import net.sf.jclec.orderarray.OrderArrayMutator;

/**
 * Random sublist mutation in OrderArrayIndividuals. It swaps two positions of the genotype.
 * This class belongs to the JCLEC-4 tutorial.
 * */

public class OrderSublistMutator extends OrderArrayMutator {
	
	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 4051327838076744754L;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 */
	public OrderSublistMutator() {
		super();
	}

	/////////////////////////////////////////////////////////////////
	// ----------------------------------------------- Public methods
	/////////////////////////////////////////////////////////////////

	// AbstractMutator methods

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void mutateNext() {
		// Gets the genotype length
		int gl = species.getGenotypeLength();
		int [] mgenome = new int[gl];
		int posicion, aux;
		int cp1 = randgen.choose(0, gl-2);
		int cp2 = randgen.choose(cp1+1, gl);

		// Creates the individual to mutate
		IIndividual mutant = parentsBuffer.get(parentsCounter);

		System.arraycopy(((AbstractIndividual<int[]>) mutant).getGenotype(), 0, mgenome, 0, gl);

		// Shuffles the sublist
		posicion = randgen.choose(cp1+1,cp2);
		aux=mgenome[cp1];
		mgenome[cp1]=mgenome[posicion];
		mgenome[posicion]=aux;

		for(int i=cp1+1; i<cp2; i++)
		{
			posicion=randgen.choose(i,cp2-1);
			aux=mgenome[i];
			mgenome[i]=mgenome[posicion];
			mgenome[posicion]=aux;
		}

		// Returns the mutated individual
		sonsBuffer.add(species.createIndividual(mgenome));
	}

	// java.lang.Object methods

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public boolean equals(Object other)	{
		if (other instanceof OrderSublistMutator) {
			return true;
		}
		else {
			return false;
		}
	}
}