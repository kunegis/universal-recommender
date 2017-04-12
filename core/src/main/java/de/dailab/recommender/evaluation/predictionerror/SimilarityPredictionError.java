package de.dailab.recommender.evaluation.predictionerror;

import de.dailab.recommender.similarity.Similarity;
import de.dailab.recommender.similarity.SimilarityRun;

/**
 * A prediction error based on a similarity measure.
 * <p>
 * A certain number <code>max</code> from which a similarity measure is subtracted.
 * 
 * @author kunegis
 */
public class SimilarityPredictionError
    implements PredictionError
{
	/**
	 * A prediction error based on a given similarity measure and max value.
	 * 
	 * @param similarity The underlying similarity measure
	 * @param max The max value from which the similarity is subtracted
	 */
	public SimilarityPredictionError(Similarity similarity, double max)
	{
		this.similarity = similarity;
		this.max = max;
	}

	@Override
	public PredictionErrorRun run()
	{
		final SimilarityRun similarityRun = similarity.run();

		return new PredictionErrorRun()
		{
			@Override
			public double get()
			{
				return max - similarityRun.getSimilarity();
			}

			@Override
			public void add(double value, double prediction)
			{
				similarityRun.add(value, prediction, 1);
			}
		};
	}

	@Override
	public boolean equals(Object object)
	{
		final SimilarityPredictionError similarityPredictionError = (SimilarityPredictionError) object;
		return this.max == similarityPredictionError.max
		    && this.similarity.equals(similarityPredictionError.similarity);
	}

	@Override
	public int hashCode()
	{
		int ret = 12;
		ret += Double.valueOf(max).hashCode();
		ret *= 13;
		ret += similarity.hashCode();
		return ret;
	}

	@Override
	public String toString()
	{
		return String.format("%s\u2212%s", max, similarity);
	}

	private final Similarity similarity;
	private final double max;
}
