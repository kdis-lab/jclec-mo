package net.sf.jclec.orderarray;

import net.sf.jclec.IFitness;
import net.sf.jclec.IIndividual;
import net.sf.jclec.base.AbstractIndividual;

import org.apache.commons.lang.builder.EqualsBuilder;

/**
 * Individual with a integer order array as genotype.
 * This class belongs to the JCLEC-4 tutorial.
 * */
public class OrderArrayIndividual extends AbstractIndividual<int[]> {
	
 	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////

	/** Serial ID */
	private static final long serialVersionUID = -2908949095129168050L;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////

	/**
	 * Empty constructor.
	 */
	public OrderArrayIndividual() {
		super();
	}

	/**
	 * Constructor that sets individual genotype.
	 * @param genotype Individual genotype.
	 */
	public OrderArrayIndividual(int[] genotype) {
		super(genotype);
	}

	/**
	 * Constructor that sets individual genotype and fitness.
	 * @param genotype Individual genotype
	 * @param fitness  Individual fitness
	 */
	public OrderArrayIndividual(int[] genotype, IFitness fitness){
		super(genotype, fitness);
	}

	/////////////////////////////////////////////////////////////////
	// ----------------------------------------------- Public methods
	/////////////////////////////////////////////////////////////////

	// IIndividual interface

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IIndividual copy(){
		// Genotype length
		int gl = genotype.length;
		// Allocate a copy of genotype
		int [] gother = new int[genotype.length];
		// Copy genotype
		System.arraycopy(genotype, 0, gother, 0, gl);
		// Create new individuals, then return it
		if (fitness != null) {
			return new OrderArrayIndividual(gother, fitness.copy());
		}
		else {
			return new OrderArrayIndividual(gother);
		}
	}

	/**
	 * BinArrayIndividuals use 'Hamming distance' as distance.
	 * {@inheritDoc}
	 */
	public double distance(IIndividual other){
		// Other genotype
		int [] gother = ((OrderArrayIndividual) other).genotype;
		// Setting "Hamming" distance
		double distance = 0.0;
		int gl = genotype.length;
		for (int i=0; i<gl; i++) {
			double aux = genotype[i] - gother[i];
			distance += (aux>=0) ? aux : -aux;
		}
		// Returns hamming distance
		return distance;
	}

	// java.lang.Object methods

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object other){
		if (other instanceof OrderArrayIndividual) {
			OrderArrayIndividual iaother = (OrderArrayIndividual) other;
			EqualsBuilder eb = new EqualsBuilder();
			eb.append(genotype, iaother.genotype);
			eb.append(fitness, iaother.fitness);
			return eb.isEquals();
		}
		else {
			return false;
		}
	}

	/**
	 * {@inheritDoc}
	 * */
	@Override
	public String toString(){
		StringBuffer sb = new StringBuffer("OrderArrayIndividual {");
		// Genotype string
		sb.append("genotype = (");
		int glm1 = genotype.length -1;
		for (int i=0; i<glm1; i++) sb.append(genotype[i]+" ");
		sb.append(genotype[glm1]+"), ");
		// Fitness string
		sb.append("fitness = "+fitness.toString());
		sb.append("}");
		return sb.toString();
	}
}
