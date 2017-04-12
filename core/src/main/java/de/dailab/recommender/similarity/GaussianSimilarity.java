package de.dailab.recommender.similarity;

/**
 * Gaussian kernel, with additional support for signedness.
 * <p>
 * Expression: exp(&minus;1/2 d<sup>2</sup> / &sigma;<sup>2</sup>).
 * 
 * @author kunegis
 */
public class GaussianSimilarity
    implements Similarity
{
	// XXX implement as distance-based similarity

	/**
	 * Default value of sigma.
	 */
	public static final double DEFAULT_SIGMA = 2.e-4;

	/**
	 * Gaussian similarity with given value of sigma.
	 * 
	 * @param sigma The variance
	 */
	public GaussianSimilarity(double sigma)
	{
		this.sigma = sigma;
		this.signed = false;
		this.namePartSigma = getNamePartSigma(sigma);
	}

	/**
	 * Gaussian similarity with given signed flag.
	 * 
	 * @param signed If set, negative values are supported by taking the Gaussian of the absolute value and returning
	 *        its opposite.
	 */
	public GaussianSimilarity(boolean signed)
	{
		this.sigma = DEFAULT_SIGMA;
		this.signed = signed;
		this.namePartSigma = "";
	}

	/**
	 * Gaussian similarity with all default values.
	 */
	public GaussianSimilarity()
	{
		this.sigma = DEFAULT_SIGMA;
		this.signed = false;
		this.namePartSigma = "";
	}

	/**
	 * Gaussian similarity with given sigma and signed flag.
	 * 
	 * @param sigma sigma value
	 * @param signed Signed flag
	 */
	public GaussianSimilarity(double sigma, boolean signed)
	{
		this.sigma = sigma;
		this.signed = signed;
		this.namePartSigma = getNamePartSigma(sigma);
	}

	public SimilarityRun run()
	{
		return new SimilarityRun()
		{

			@Override
			public void add(double x, double y, double lambda)
			{
				squareDiffSum += (x - y) * (x - y) * lambda;
			}

			@Override
			public double getSimilarity()
			{

				if (!signed)
					return Math.exp(-.5 * squareDiffSum / (sigma * sigma));
				else
					return Math.signum(squareDiffSum) * Math.exp(-.5 * Math.abs(squareDiffSum) / (sigma * sigma));
			}

			private double squareDiffSum = 0.;
		};
	}

	@Override
	public String toString()
	{
		return String.format("Gaussian(%s%s)", signed ? "signed, " : "", namePartSigma);
	}

	private final double sigma;
	private final boolean signed;

	private final String namePartSigma;

	private static String getNamePartSigma(double sigma)
	{
		return String.format("%.1e", sigma);
	}

	@Override
	public boolean isSpectral()
	{
		return false;
	}

	@Override
	public double[] transformSpectrum(double[] lambda)
	{
		throw new UnsupportedOperationException();
	}
}
