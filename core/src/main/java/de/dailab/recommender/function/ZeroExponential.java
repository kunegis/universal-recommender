package de.dailab.recommender.function;

/**
 * The "zero exponential" function.
 * 
 * @author kunegis
 */
public class ZeroExponential
    implements Function
{
	/**
	 * Default value for sigma.
	 */
	public static final double DEFAULT_SIGMA = 1.e+3;

	/**
	 * The zero exponential with default sigma.
	 */
	public ZeroExponential()
	{
		this.sigmaSquared = DEFAULT_SIGMA * DEFAULT_SIGMA;
		this.namePartSigma = "";
	}

	/**
	 * Zero exponential with given value of sigma.
	 * 
	 * @param sigma Value of sigma
	 */
	public ZeroExponential(double sigma)
	{
		this.sigmaSquared = sigma * sigma;
		this.namePartSigma = String.format("/%.3e", sigma);
	}

	@Override
	public double apply(double x)
	{
		return Math.exp(sigmaSquared / x) - sigmaSquared / x - 1;
	}

	@Override
	public String toString()
	{
		return String.format("ZeroExponential(%s)", namePartSigma);
	}

	private final double sigmaSquared;
	private final String namePartSigma;
}
