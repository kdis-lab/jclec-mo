package net.sf.jclec.orderarray;

import net.sf.jclec.IConfigure;
import net.sf.jclec.util.intset.IIntegerSet;
import net.sf.jclec.util.intset.Interval;


import org.apache.commons.configuration.Configuration;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import net.sf.jclec.util.intset.Closure;

/**
 * OrderArrayIndividual species.
 * This class belongs to the JCLEC-4 tutorial.
 * */
public class OrderArrayIndividualSpecies extends AbstractOrderArraySpecies implements IConfigure {
	
	/////////////////////////////////////////////////////////////////
	// --------------------------------------------------- Properties
	/////////////////////////////////////////////////////////////////
	
	/** Serial ID */
	private static final long serialVersionUID = 577795667519852596L;

	/////////////////////////////////////////////////////////////////
	// ------------------------------------------------- Constructors
	/////////////////////////////////////////////////////////////////
	
	/**
	 * Empty constructor.
	 */	
	public OrderArrayIndividualSpecies() {
		super();
	}

	/**
	 * Constructor that sets genotype schema.
	 * @param genotypeSchema Genotype schema.
	 */
	public OrderArrayIndividualSpecies(IIntegerSet [] genotypeSchema) {
		super();
		setGenotypeSchema(genotypeSchema);
	}
	
	/////////////////////////////////////////////////////////////////
	// ----------------------------------------------- Public methods
	/////////////////////////////////////////////////////////////////
	
	// Setting properties
	
	/**
	 * Set genotype schema.
	 * @param genotypeSchema New genotype schema.
	 */
	public void setGenotypeSchema(IIntegerSet [] genotypeSchema){
		this.genotypeSchema = genotypeSchema;
	}
	
	// IBinArrayIndividualSpecies interface
	
	/**
	 * {@inheritDoc}
	 */
	public OrderArrayIndividual createIndividual() {
		return new OrderArrayIndividual();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public OrderArrayIndividual createIndividual(int[] genotype) {
		return new OrderArrayIndividual(genotype);
	}
	
	// IConfigure interface
	/**
	 * {@inheritDoc}
	 * */
	@Override
	public void configure(Configuration settings) {
		// Genotype lenght
		int genotypeLength = settings.getInt("[@genotype-length]", 0);
		
		if (genotypeLength != 0) {
			
			// Genotype schema
			IIntegerSet [] genotypeSchema = new IIntegerSet[genotypeLength];
			// Set genotype schema components
			
			for (int i=0; i<genotypeLength; i++) {
				// Set schema component
				Interval integerSet = new Interval();
				integerSet.setLeft(0);
				integerSet.setRight(genotypeLength-1);
				integerSet.setClosure(Closure.ClosedClosed);
				genotypeSchema[i] = integerSet;
				
			}
			
			// Set genotype schema
			setGenotypeSchema(genotypeSchema);
		}
	}

	// java.lang.Object methods
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString(){
		// Performs Schema rendering
		ToStringBuilder tsb = new ToStringBuilder(this);
		// Append schema
		tsb.append("schema", genotypeSchema);
		// Returns rendered schema
		return tsb.toString();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object other){
		if (other instanceof OrderArrayIndividualSpecies) {
			EqualsBuilder eb = new EqualsBuilder();
			OrderArrayIndividualSpecies iaoth = (OrderArrayIndividualSpecies) other;
			eb.append(this.genotypeSchema, iaoth.genotypeSchema);
			return eb.isEquals();
		}
		else {
			return false;
		}
	}
}
