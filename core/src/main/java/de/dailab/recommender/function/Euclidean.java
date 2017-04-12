package de.dailab.recommender.function;

/**
 * "Euclidean" function.
 * 
 * @author kunegis
 */
public class Euclidean
    implements Function
{
	/**
	 * Default value for sigma.
	 */
	public static final double SIGMA_DEFAULT = .1;

	/**
	 * Euclidean function with default parameter.
	 */
	public Euclidean()
	{
		this.sigmaSquared = SIGMA_DEFAULT * SIGMA_DEFAULT;
		this.namePartSigma = "";
	}

	/**
	 * Euclidean function with given sigma.
	 * 
	 * @param sigma Sigma value
	 */
	public Euclidean(double sigma)
	{
		this.sigmaSquared = sigma * sigma;
		this.namePartSigma = String.format("/%.3e", sigma);
	}

	@Override
	public double apply(double x)
	{
		return 1. / (x + sigmaSquared);
	}

	@Override
	public String toString()
	{
		return String.format("Euclidean(%s)", namePartSigma);
	}

	private final double sigmaSquared;
	private final String namePartSigma;

}
