package de.dailab.recommender.function;

import java.util.Arrays;

/**
 * A polynomial.
 * 
 * @author kunegis
 */
public class Polynomial
    implements Function
{
	/**
	 * A polynomial with the given factors. The index in the array corresponds to the power of the variable.
	 * 
	 * @param factors The factors; may have any length
	 */
	public Polynomial(double... factors)
	{
		this.factors = factors;
	}

	/**
	 * Variable length, the index in the array is the power of the variable.
	 */
	private final double factors[];

	@Override
	public double apply(double x)
	{
		double ret = 0;
		double xi = 1;

		for (int i = 0; i < factors.length; ++i)
		{
			ret += factors[i] * xi;
			xi *= x;
		}

		return ret;
	}

	@Override
	public String toString()
	{
		return Arrays.toString(factors);
	}
}
