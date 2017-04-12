package de.dailab.recommender.similarity;

/**
 * A compound similarity: the sum of two similarities. The first M vectors are passed to one similarity and the
 * following N vectors to another similarity. The resulting similarity is the sum of both values.
 * 
 * @author kunegis
 */
public class CompoundSimilarity
    implements Similarity
{
	/**
	 * Compound similarity of the two given similarities using the two given ranks. This similarity is only able to
	 * compute similarities between vectors whose length equals the sums of the given ranks.
	 * 
	 * @param rank_1 Rank to use for first similarity
	 * @param rank_2 Rank to use for second similarity
	 * @param similarity_1 First similarity
	 * @param similarity_2 Second similarity
	 */
	public CompoundSimilarity(int rank_1, int rank_2, Similarity similarity_1, Similarity similarity_2)
	{
		this.rank_1 = rank_1;
		this.rank_2 = rank_2;
		this.similarity_1 = similarity_1;
		this.similarity_2 = similarity_2;
	}

	private final int rank_1, rank_2;

	private final Similarity similarity_1, similarity_2;

	@Override
	public SimilarityRun run()
	{
		final SimilarityRun similarityRun_1 = similarity_1.run();
		final SimilarityRun similarityRun_2 = similarity_2.run();

		return new SimilarityRun()
		{
			private int i = 0;

			@Override
			public void add(double x, double y, double lambda)
			{
				assert i >= 0;
				if (!(i < rank_1 + rank_2)) throw new IllegalAccessError("Compound similarity called too often");
				if (i++ < rank_1)
					similarityRun_1.add(x, y, lambda);
				else
					similarityRun_2.add(x, y, lambda);
			}

			@Override
			public double getSimilarity()
			{
				if (i != rank_1 + rank_2) throw new IllegalAccessError("Compound similarity not called often enough");
				return similarityRun_1.getSimilarity() + similarityRun_2.getSimilarity();
			}
		};
	}

	@Override
	public boolean isSpectral()
	{
		return similarity_1.isSpectral() && similarity_2.isSpectral();
	}

	@Override
	public double[] transformSpectrum(double[] lambda)
	    throws UnsupportedOperationException
	{
		if (!(lambda.length == rank_1 + rank_2))
		    throw new IllegalArgumentException(
		        "Spectrum has different size than rank parameters of compound similarity");

		final double lambda_1[] = new double[rank_1];
		final double lambda_2[] = new double[rank_2];

		for (int i = 0; i < rank_1; ++i)
			lambda_1[i] = lambda[i];
		for (int i = 0; i < rank_2; ++i)
			lambda_2[i] = lambda[rank_1 + i];

		final double ret_1[] = similarity_1.transformSpectrum(lambda_1);
		final double ret_2[] = similarity_2.transformSpectrum(lambda_2);

		final double ret[] = new double[rank_1 + rank_2];

		for (int i = 0; i < rank_1; ++i)
			ret[i] = ret_1[i];
		for (int i = 0; i < rank_2; ++i)
			ret[rank_1 + i] = ret_2[i];

		return ret;
	}
}
