package de.dailab.recommender.function;

/**
 * The "Gaussian" function, i.e. exp(&minus;1/2 x / &sigma;<sup>2</sup>).
 * 
 * @author kunegis
 */
public class Gaussian
    implements Function
{
	/**
	 * The default sigma value.
	 */
	public static final double DEFAULT_SIGMA = 2.e-4;

	/**
	 * The Gaussian function with default parameters.
	 */
	public Gaussian()
	{
		sigmaSquared = DEFAULT_SIGMA * DEFAULT_SIGMA;
		namePartSigma = "";
	}

	/**
	 * The Gaussian function with given sigma.
	 * 
	 * @param sigma The sigma value
	 */
	public Gaussian(double sigma)
	{
		sigmaSquared = sigma * sigma;
		namePartSigma = String.format("/%.3e", sigma);
	}

	@Override
	public double apply(double x)
	{
		return Math.exp(-.5 * x / sigmaSquared);
	}

	@Override
	public String toString()
	{
		return String.format("Gaussian(%s)", namePartSigma);
	}

	private final double sigmaSquared;
	private final String namePartSigma;

}
