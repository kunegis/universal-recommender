package de.dailab.recommender.function;

/**
 * The rational sigmoid.
 * 
 * @author kunegis
 */
public class SigmoidRat
    implements Sigmoid
{
	@Override
	public double apply(double x)
	{
		return Math.signum(x) * (1. - 1. / (1. + Math.abs(x)));
	}

	@Override
	public String toString()
	{
		return "SigmoidRat";
	}
}
