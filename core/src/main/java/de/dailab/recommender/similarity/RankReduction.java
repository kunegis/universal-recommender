package de.dailab.recommender.similarity;

/**
 * The rank reduction of a given similarity.
 * <p>
 * Only the <i>k</i> first latent dimensions are used, where <i>k</i> is the rank parameter.
 * 
 * @author kunegis
 */
public class RankReduction
    implements SimilarityTransformation
{
	/**
	 * The reduction of the given similarity to the given rank.
	 * 
	 * @param rank The reduced rank of this similarity
	 */
	public RankReduction(int rank)
	{
		this.rank = rank;
	}

	private final int rank;

	/**
	 * Get the reduced rank.
	 * 
	 * @return The reduced rank
	 */
	public int getRank()
	{
		return rank;
	}

	@Override
	public SimilarityRun run(Similarity similarity)
	{
		final SimilarityRun similarityRun = similarity.run();

		return new SimilarityRun()
		{
			private int remaining = rank;

			@Override
			public void add(double x, double y, double lambda)
			{
				if (remaining > 0)
				{
					--remaining;
					similarityRun.add(x, y, lambda);
				}
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
		return String.format("Rank(%d)", rank);
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
		for (int i = 0; i < lambda.length; ++i)
			if (i < rank)
				ret[i] = lambda[i];
			else
				ret[i] = 0;
		return ret;
	}
}
