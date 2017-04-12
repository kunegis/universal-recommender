package de.dailab.recommender.function;

/**
 * The hyperbolic tangent tangent.
 * 
 * @author kunegis
 */
public class SigmoidTanh
    implements Sigmoid
{
	@Override
	public double apply(double x)
	{
		return Math.tanh(x);
	}

	@Override
	public String toString()
	{
		return "SigmoidTanh";
	}
}
