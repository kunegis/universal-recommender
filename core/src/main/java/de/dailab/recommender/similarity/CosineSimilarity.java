package de.dailab.recommender.similarity;

/**
 * The cosine similarity computed in one pass.
 * <p>
 * The eigenvalues must be nonnegative.
 * <p>
 * As a similarity measure of nodes in an unweighted graph, this is the standard cosine similarity. If the vectors are
 * first normalized to zero mean and unit standard deviation, this gives the Pearson correlation.
 * 
 * @author kunegis
 */
public class CosineSimilarity
    implements Similarity
{
	@Override
	public SimilarityRun run()
	{
		return new SimilarityRun()
		{
			public void add(double x, double y, double lambda)
			{
				assert lambda >= 0;

				nxy += x * y * lambda;
				nxx += x * x * lambda;
				nyy += y * y * lambda;
			}

			public double getSimilarity()
			{
				double ret = nxy / Math.sqrt(nxx * nyy);
				if (Double.isNaN(ret)) ret = 0;
				return ret;
			}

			private double nxy = 0;
			private double nxx = 0;
			private double nyy = 0;
		};
	}

	@Override
	public int hashCode()
	{
		return 289378247;
	}

	@Override
	public boolean equals(Object object)
	{
		return object instanceof CosineSimilarity;
	}

	@Override
	public String toString()
	{
		return "Cosine";
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
