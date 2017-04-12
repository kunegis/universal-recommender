package de.dailab.recommender.similarity;

/**
 * The inverse Euclidean distance, with support for regularization and indefinite similarity signatures.
 * <p>
 * The similarity is computed by s<sup>&minus;2</sup> = &epsilon;<sup>2</sup> + &Sigma;<sub>i</sub> (x<sub>i</sub>
 * &minus; y<sub>i</sub>)<sup>2</sup>, where &epsilon; is a parameter denoting the error on x and y.
 * <p>
 * The signed flag can be used for indefinite signatures.
 * 
 * @author kunegis
 */
public class EuclideanSimilarity
    implements Similarity
{
	// XXX implement as distance based similarity

	/**
	 * Default value for epsilon.
	 */
	private static final double EPSILON_DEFAULT = .1;

	/**
	 * Default value of the signed flag
	 */
	private static final boolean SIGNED_DEFAULT = false;

	/**
	 * The inverted Euclidean similarity with given signed flag and value of epsilon.
	 * 
	 * @param epsilon Value of epsilon
	 * @param signed Signed flag
	 */
	public EuclideanSimilarity(double epsilon, boolean signed)
	{
		this.epsilon = epsilon;
		this.signed = signed;
	}

	/**
	 * The unsigned inverted Euclidean similarity with given value of epsilon.
	 * 
	 * @param epsilon The value of epsilon
	 */
	public EuclideanSimilarity(double epsilon)
	{
		this.epsilon = epsilon;
		this.signed = SIGNED_DEFAULT;
	}

	/**
	 * The inverted Euclidean similarity with given signed flag and the default value for epsilon.
	 * 
	 * @param signed whether the variant is signed
	 */
	public EuclideanSimilarity(boolean signed)
	{
		this.epsilon = EPSILON_DEFAULT;
		this.signed = signed;
	}

	/**
	 * The unsigned inverted Euclidean distance with default value of epsilon.
	 */
	public EuclideanSimilarity()
	{
		this.epsilon = EPSILON_DEFAULT;
		this.signed = SIGNED_DEFAULT;
	}

	public SimilarityRun run()
	{
		return new SimilarityRun()
		{
			@Override
			public void add(double x, double y, double lambda)
			{
				assert signed || lambda >= 0;
				squareDiffSum += (x - y) * (x - y) * lambda;
			}

			@Override
			public double getSimilarity()
			{
				if (signed)
					return Math.signum(squareDiffSum) * Math.pow(Math.abs(squareDiffSum) + epsilon * epsilon, -.5);
				else
					return Math.pow(squareDiffSum + epsilon * epsilon, -.5);
			}

			private double squareDiffSum = 0.;
		};
	}

	@Override
	public String toString()
	{
		return String.format("InvertedEuclidean(%s%s)", signed ? "signed " : "", epsilon <= 0 ? "" : String.format(
		    "%.1f", epsilon));
	}

	private final double epsilon;
	private final boolean signed;

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
