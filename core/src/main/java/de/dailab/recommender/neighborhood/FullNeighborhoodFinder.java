package de.dailab.recommender.neighborhood;

import java.util.Iterator;
import java.util.LinkedList;

import de.dailab.recommender.similarity.Similarity;
import de.dailab.recommender.similarity.SimilarityRun;

/**
 * A trivial neighborhood finder that iterates over all vectors.
 * <p>
 * This class is inefficient. It has O(n) memory and runtime complexity. It does however return the correct results.
 * <p>
 * This class supports all similarity measures.
 * 
 * @author kunegis
 */
public class FullNeighborhoodFinder
    implements NeighborhoodFinder
{
	@Override
	public NeighborhoodFinderModel build(final Similarity similarity, final double lambda[], final double u[][])
	{
		assert lambda.length == u.length;

		return new NeighborhoodFinderModel()
		{
			@Override
			public Iterator <WeightedPoint> findContinuous(double[] vector)
			{
				final int k = u[0].length;

				final LinkedList <WeightedPoint> ret = new LinkedList <WeightedPoint>();

				for (int i = 0; i < u[0].length; ++i)
				{
					final SimilarityRun similarityRun = similarity.run();

					for (int j = 0; j < u.length; ++j)
					{
						similarityRun.add(vector[j], u[j][i], lambda[j]);
					}
					final double score = similarityRun.getSimilarity();
					assert !Double.isNaN(score) && !Double.isInfinite(score);

					if (ret.size() < k || ret.getLast().score < score)
					{
						WeightedPoint.merge(ret, new WeightedPoint(i, score), k);
					}
				}

				return ret.iterator();
			}

			@Override
			public void update()
			{
			/* Do nothing */
			}

			@Override
			public NeighborhoodFinder getNeighborhoodFinder()
			{
				return FullNeighborhoodFinder.this;
			}
		};
	}

	@Override
	public String toString()
	{
		return "Full";
	}
}
