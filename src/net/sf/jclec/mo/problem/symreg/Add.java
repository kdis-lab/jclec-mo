package net.sf.jclec.mo.problem.symreg;

import net.sf.jclec.exprtree.fun.AbstractPrimitive;
import net.sf.jclec.exprtree.fun.ExprTreeFunction;

/**
 * Add operation for expression trees. This class belongs
 * to the JCLEC-4 tutorial.
 * */
public class Add extends AbstractPrimitive {

	private static final long serialVersionUID = 8279083725033255980L;

	/**
	 * This operator receives two double arrays as arguments and return
	 * a double array as result.
	 */
	
	public Add() 
	{
		super(new Class<?> [] {Double.class, Double.class}, Double.class);
	}

	@Override
	protected void evaluate(ExprTreeFunction context) 
	{
		// Get arguments (in context stack)
		Double arg1 = pop(context);
		Double arg2 = pop(context);
		
		// Push result in context stack
		push(context, arg1+arg2);
	}

	// java.lang.Object methods
	
	public boolean equals(Object other)
	{
		return other instanceof Add;
	}	
	
	public String toString()
	{
		return "+";
	}	
}
