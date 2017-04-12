package de.dailab.recommender.function;

/**
 * The inverted sigmoid.
 * 
 * @author kunegis
 */
public class InvertedSigmoid
    implements Function
{
	/**
	 * Default value of sigma for the inverted sigmoid.
	 */
	public static final double DEFAULT_SIGMA = 7.e-5;

	/**
	 * The inverted sigmoid with default sigma.
	 */
	public InvertedSigmoid()
	{
		this.sigmaSquared = DEFAULT_SIGMA * DEFAULT_SIGMA;
		this.namePartSigma = "";
	}

	/**
	 * Inverted sigmoid with given value for sigma.
	 * 
	 * @param sigma Value of sigma
	 */
	public InvertedSigmoid(double sigma)
	{
		this.sigmaSquared = sigma * sigma;
		this.namePartSigma = String.format("/%.3e", sigma);
	}

	@Override
	public double apply(double x)
	{
		return Math.tanh(sigmaSquared / x);
	}

	@Override
	public String toString()
	{
		return String.format("InvertedSigmoid(%s)", namePartSigma);
	}

	private final double sigmaSquared;
	private final String namePartSigma;
}
