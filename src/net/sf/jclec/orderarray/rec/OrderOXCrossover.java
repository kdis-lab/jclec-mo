package net.sf.jclec.orderarray.rec;

import net.sf.jclec.base.AbstractIndividual;
import net.sf.jclec.orderarray.OrderArrayRecombinator;

/**
 * Order Crossover (OX).
 * This class belongs to the JCLEC-4 tutorial.
 * 
 * <p><i>Paper</i>: L. Davis, "Applying adaptive algorithms to epistatic domains". 
 * Proceedings of the 9th international joint conference on Artificial
 * intelligence (IJCAI’85), pp 162–164, 1985.</p>
 * */
public class OrderOXCrossover extends OrderArrayRecombinator {
	
	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 3835150645048325173L;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 */
	public OrderOXCrossover() {
		super();
	}

	/////////////////////////////////////////////////////////////////
	// ----------------------------------------------- Public methods
	/////////////////////////////////////////////////////////////////

	// java.lang.Object methods

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object other) {
		if (other instanceof OrderOXCrossover) {
			return true;
		}
		else {
			return false;
		}
	}

	/////////////////////////////////////////////////////////////////
	// -------------------------------------------- Protected methods
	/////////////////////////////////////////////////////////////////

	// AbstractRecombinator methods

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void recombineNext() {
		int gl = species.getGenotypeLength();

		// Parents' genotypes
		int [] p0_genome = ((AbstractIndividual<int[]>) parentsBuffer.get(parentsCounter)).getGenotype();
		int [] p1_genome = ((AbstractIndividual<int[]>) parentsBuffer.get(parentsCounter+1)).getGenotype();

		// Sons' genotypes
		int [] s0_genome = new int[gl];
		int [] s1_genome = new int[gl];

		// Gets random crossover points
		int cp1, cp2;
		cp1 = randgen.choose(0, gl-1);
		cp2 = randgen.choose(cp1, gl);

		// First son
		System.arraycopy(p1_genome, cp1, s0_genome, cp1, cp2-cp1);
		int equal=0,z=cp2;

		for(int i=0; i<gl; i++)
		{
			for(int j=cp1; j<cp2; j++)
				if(p0_genome[((cp2+i)%gl)]==s0_genome[j])
					equal=1;

			if(equal==0)
			{
					s0_genome[z]=p0_genome[((cp2+i)%gl)];
					z=(z+1)%gl;
			}
			equal=0;
		}

		// Second son
		System.arraycopy(p0_genome, cp1, s1_genome, cp1, cp2-cp1);
		equal=0;
		z=cp2;

		for(int i=0; i<gl; i++)
		{
			for(int j=cp1; j<cp2; j++)
				if(p1_genome[((cp2+i)%gl)]==s1_genome[j])
					equal=1;

			if(equal==0)
			{
					s1_genome[z]=p1_genome[((cp2+i)%gl)];
					z=(z+1)%gl;
			}
			equal=0;
		}

		// Adds sons to the sons' buffer
		sonsBuffer.add(species.createIndividual(s0_genome));
		sonsBuffer.add(species.createIndividual(s1_genome));
	}
}
