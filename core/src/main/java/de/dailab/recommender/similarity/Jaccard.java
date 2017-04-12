package de.dailab.recommender.similarity;

/**
 * The Jaccard coefficient between two vectors and signed generalizations.
 * 
 * @see <a href = "http://en.wikipedia.org/wiki/Jaccard_index">Wikipedia: Jaccard index</a>
 * 
 * @author kunegis
 */
public class Jaccard
    implements Similarity
{
	@Override
	public SimilarityRun run()
	{
		return new SimilarityRun()
		{
			private double productSum = 0;
			private double maxSum = 0;

			@Override
			public void add(double x, double y, double lambda)
			{
				productSum += x * y * lambda;

				maxSum += Math.max(Math.abs(x * lambda), Math.abs(y * lambda));
			}

			@Override
			public double getSimilarity()
			{
				if (maxSum == 0) return 0;
				return productSum / maxSum;
			}
		};
	}

	@Override
	public int hashCode()
	{
		return 23487238;
	}

	@Override
	public boolean equals(Object object)
	{
		return object instanceof Jaccard;
	}

	@Override
	public String toString()
	{
		return "Jaccard";
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
