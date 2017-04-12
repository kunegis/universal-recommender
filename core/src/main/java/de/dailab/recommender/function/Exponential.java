package de.dailab.recommender.function;

/**
 * The exponential function.
 * 
 * @author kunegis
 */
public class Exponential
    implements Function
{
	/**
	 * The exponential function exp(alpha x).
	 * 
	 * @param alpha The parameter
	 */
	public Exponential(double alpha)
	{
		this.alpha = alpha;
	}

	/**
	 * The exponential exp(x).
	 */
	public Exponential()
	{
		this.alpha = 1;
	}

	@Override
	public double apply(double x)
	{
		return Math.exp(alpha * x);
	}

	@Override
	public String toString()
	{
		return String.format("Exp(%.3g)", alpha);
	}

	private final double alpha;
}
