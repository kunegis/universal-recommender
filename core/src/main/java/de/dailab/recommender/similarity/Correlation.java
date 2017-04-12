package de.dailab.recommender.similarity;

/**
 * The Pearson correlation between two vectors.
 * <p>
 * This implementation uses an unstable iterative algorithm. The memory and runtime are constant for each add().
 * <p>
 * The eigenvalues must be nonnegative.
 * 
 * @see <a
 *      href="http://en.wikipedia.org/wiki/Pearson_correlation">Wikipedia:&nbsp;Pearson&nbsp;product-moment&nbsp;correlation&nbsp;coefficient</a>
 * 
 * @author kunegis
 */
public class Correlation
    implements Similarity
{
	public SimilarityRun run()
	{
		return new SimilarityRun()
		{
			public void add(double x, double y, double lambda)
			{
				final double sqrtLambda = Math.sqrt(lambda);

				++n;
				nx += x * sqrtLambda;
				ny += y * sqrtLambda;
				nxx += x * x * lambda;
				nyy += y * y * lambda;
				nxy += x * y * lambda;
			}

			public double getSimilarity()
			{
				double ret = (nxy / n - nx * ny / n / n)
				    / Math.sqrt((nxx / n - nx * nx / n / n) * (nyy / n - ny * ny / n / n));
				if (Double.isNaN(ret) || Double.isInfinite(ret)) ret = 0.;

				return ret;
			}

			private int n = 0;
			private double nx = 0;
			private double ny = 0;
			private double nxx = 0;
			private double nyy = 0;
			private double nxy = 0;
		};
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof Correlation;
	}

	@Override
	public int hashCode()
	{
		return 23847;
	}

	@Override
	public String toString()
	{
		return "Correlation";
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
