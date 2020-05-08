package net.sf.jclec.orderarray.rec;

import net.sf.jclec.base.AbstractIndividual;
import net.sf.jclec.orderarray.OrderArrayRecombinator;

/**
 * Partially Mapped Crossover (PMX).
 * This class belongs to the JCLEC-4 tutorial.
 * 
 * <p><i>Paper</i>: D. Goldberg, R. Lingle, "Alleles loci and the traveling salesman
 * problem". 1st Int. Conf. on Genetic Algorithms and their Applications, pp 154–159, 1985.
 * */
public class OrderPMXCrossover extends OrderArrayRecombinator {
	
	/////////////////////////////////////////////////////////////////
	// --------------------------------------- Serialization constant
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = 3835150645048325173L;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 */
	public OrderPMXCrossover() {
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
		if (other instanceof OrderPMXCrossover) {
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

		// Parents genotypes
		int [] p0_genome = ((AbstractIndividual<int[]>) parentsBuffer.get(parentsCounter)).getGenotype();
		int [] p1_genome = ((AbstractIndividual<int[]>) parentsBuffer.get(parentsCounter+1)).getGenotype();

		// Sons genotypes
		int [] s0_genome = new int[gl];
		int [] s1_genome = new int[gl];

		// Get crossover points
		int cp1, cp2;
		cp1 = randgen.choose(0, gl-1);
		cp2 = randgen.choose(cp1, gl);

        // Fill first son
        System.arraycopy(p0_genome, 0,   s0_genome, 0,   cp1);
        System.arraycopy(p1_genome, cp1, s0_genome, cp1, cp2-cp1);
        System.arraycopy(p0_genome, cp2, s0_genome, cp2, gl-cp2);

        for(int i=0;i<gl;i++)
        	if(i<cp1 || i>=cp2)
        		for(int j=cp1; j<cp2; j++)
        			if(s0_genome[i]==s0_genome[j])
        			{
        				System.arraycopy(p0_genome, j, s0_genome, i, 1);
        				j=cp1-1;
        			}

        // Fill second son
        System.arraycopy(p1_genome, 0,   s1_genome, 0,   cp1);
        System.arraycopy(p0_genome, cp1, s1_genome, cp1, cp2-cp1);
        System.arraycopy(p1_genome, cp2, s1_genome, cp2, gl-cp2);

        for(int i=0;i<gl;i++)
        	if(i<cp1 || i>=cp2)
        	for(int j=cp1; j<cp2; j++)
        		if(s1_genome[i]==s1_genome[j])
        		{
        			System.arraycopy(p1_genome, j, s1_genome, i, 1);
        			j=cp1-1;
        		}

		// Add sons to the buffer
		sonsBuffer.add(species.createIndividual(s0_genome));
		sonsBuffer.add(species.createIndividual(s1_genome));
	}
}