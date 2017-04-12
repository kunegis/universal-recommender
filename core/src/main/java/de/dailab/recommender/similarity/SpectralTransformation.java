package de.dailab.recommender.similarity;

import de.dailab.recommender.function.Function;

/**
 * A spectral transformation applied to a given similarity. The spectral transformation is specified by a function, and
 * is applied to all eigenvalues.
 * 
 * @see <a href="http://portal.acm.org/citation.cfm?doid=1553374.1553447">J. Kunegis &amp; A. Lommatzsch, <i>Learning
 *      spectral graph transformations for link prediction</i>.</a>
 * 
 * @author kunegis
 */
public class SpectralTransformation
    implements SimilarityTransformation
{
	/**
	 * A spectral transformation consisting of applying a given function to the eigenvalues.
	 * 
	 * @param transformation The function to apply to the eigenvalues
	 * @param normalized If set, all eigenvalues are first divided by the absolute first eigenvalue, then the spectral
	 *        transformation is applied, then the result is multiplied by the absolute first eigenvalue; the first
	 *        eigenvalue is used, which should be the largest in absolute value
	 */
	public SpectralTransformation(Function transformation, boolean normalized)
	{
		this.transformation = transformation;
		this.normalized = normalized;
	}

	/**
	 * A normalized spectral transformation using the given transformation function.
	 * 
	 * @param transformation The spectral transformation function to use
	 */
	public SpectralTransformation(Function transformation)
	{
		this(transformation, NORMALIZED_DEFAULT);
	}

	private final Function transformation;

	private final boolean normalized;

	/**
	 * The default value of the NORMALIZED parameter, true.
	 */
	public static final boolean NORMALIZED_DEFAULT = true;

	@Override
	public SimilarityRun run(Similarity similarity)
	{
		final SimilarityRun similarityRun = similarity.run();

		final double lambda_0[] = new double[]
		{ Double.NaN };

		return new SimilarityRun()
		{
			@Override
			public void add(double x, double y, double lambda)
			{
				if (lambda_0[0] != lambda_0[0]) lambda_0[0] = lambda;

				if (normalized) lambda /= lambda_0[0];

				double transformedLambda = transformation.apply(lambda);

				if (normalized) transformedLambda *= lambda_0[0];

				similarityRun.add(x, y, transformedLambda);
			}

			@Override
			public double getSimilarity()
			{
				return similarityRun.getSimilarity();
			}
		};
	}

	@Override
	public String toString()
	{
		return String.format("SpectralTransformation(%s %s)", normalized ? "normalized" : "unnormalized",
		    transformation);
	}

	@Override
	public boolean isSpectral()
	{
		return true;
	}

	@Override
	public double[] transformSpectrum(double[] lambda)
	{
		final double ret[] = new double[lambda.length];

		if (lambda.length != 0)
		{
			final double norm = normalized && lambda[0] != 0. ? Math.abs(lambda[0]) : 1.;

			for (int i = 0; i < lambda.length; ++i)
				ret[i] = transformation.apply(lambda[i] / norm) * norm;
		}

		return ret;
	}
}
