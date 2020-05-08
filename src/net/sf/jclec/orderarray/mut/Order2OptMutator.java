package net.sf.jclec.orderarray.mut;

import net.sf.jclec.IIndividual;
import net.sf.jclec.base.AbstractIndividual;
import net.sf.jclec.orderarray.OrderArrayMutator;

/**
 * 2-opt mutation in OrderIntArrayIndividuals.
 * This class belongs to the JCLEC-4 tutorial.
 * */
public class Order2OptMutator extends OrderArrayMutator
{
	/////////////////////////////////////////////////////////////////
	// ---------------------------------------------------- Propeties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 4051327838076744754L;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 */
	public Order2OptMutator(){
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
	protected void mutateNext()	{
		// Gets the genotype length
		int gl = species.getGenotypeLength();
		int [] mgenome = new int[gl];
		int aux;

		// Creates the individual to mutate
		IIndividual mutant = parentsBuffer.get(parentsCounter);

		int cp1 = randgen.choose(0, gl-1);
		int cp2 = randgen.choose(cp1, gl);

		System.arraycopy(((AbstractIndividual<int[]>) mutant).getGenotype(),0, mgenome,0, gl);

		// Mutation of the individual, swapping two genes
		aux = mgenome[cp1];
		mgenome[cp1] = mgenome[cp2];
		mgenome[cp2] = aux;

		// Returns the mutated individual
		sonsBuffer.add(species.createIndividual(mgenome));
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public boolean equals(Object other) {
		if (other instanceof Order2OptMutator) {
			return true;
		}
		else {
			return false;
		}
	}
}