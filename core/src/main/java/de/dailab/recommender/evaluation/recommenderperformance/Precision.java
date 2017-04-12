package de.dailab.recommender.evaluation.recommenderperformance;

import java.util.Iterator;
import java.util.Set;

import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.recommendation.Recommendation;

/**
 * The mean average precision (MAP) of a recommender.
 * <p>
 * The cutoff parameters determines when to stop requesting recommendations from an iterator. Cutoff can be disabled, in
 * which case computation of the precision only terminated when the iterator terminates.
 * 
 * @author kunegis
 */
public class Precision
    implements RecommenderPerformance
{
	/**
	 * The mean average precision.
	 * 
	 * @param cutoff Stop reading recommendations after cutoffFactor times the number of entities in the test set.
	 * @param cutoffFactor The cutoff factor; ignored when cutoff is disabled
	 */
	public Precision(boolean cutoff, double cutoffFactor)
	{
		this.cutoff = cutoff;
		this.cutoffFactor = cutoffFactor;
	}

	/**
	 * The mean average precision with given cutoff flag.
	 * 
	 * @param cutoff Cutoff mode
	 */
	public Precision(boolean cutoff)
	{
		this(cutoff, CUTOFF_FACTOR_DEFAULT);
	}

	/**
	 * The mean average precision without cutoff.
	 */
	public Precision()
	{
		this(CUTOFF_DEFAULT, 0);
	}

	@Override
	public RecommenderPerformanceRun run()
	{
		return new RecommenderPerformanceRun()
		{
			private int entityCount = 0;

			/**
			 * Sum of all average precisions.
			 */
			private double averagePrecisionSum = 0;

			@Override
			public void add(Set <Entity> testSet, Iterator <Recommendation> recommendations)
			{
				++entityCount;

				double precisionSum = 0;
				int recommendationCount = 0;
				int goodCount = 0;

				while (recommendations.hasNext())
				{
					++recommendationCount;
					final Recommendation recommendation = recommendations.next();

					if (testSet.contains(recommendation.getEntity()))
					{
						++goodCount;
						precisionSum += goodCount / recommendationCount;

						/* Break when all test entities have been found */
						if (goodCount == testSet.size()) break;

						if (cutoff && recommendationCount > cutoffFactor * testSet.size()) break;
					}
				}

				/*
				 * If the recommender returned only entities outside the test set, the average precision is taken to be
				 * zero.
				 */
				final double averagePrecision = goodCount == 0 ? 0 : precisionSum / goodCount;

				averagePrecisionSum += averagePrecision;
			}

			@Override
			public double get()
			{
				assert entityCount > 0;
				return averagePrecisionSum / entityCount;
			}
		};
	}

	/**
	 * Whether to stop reading recommendations when it is clear the precision cannot anymore go up by much.
	 */
	private final boolean cutoff;

	/**
	 * In cutoff mode, stop reading recommendations after this times the number of entities in the test set.
	 */
	private final double cutoffFactor;

	private static final boolean CUTOFF_DEFAULT = false;

	private static final double CUTOFF_FACTOR_DEFAULT = 100;

	@Override
	public String toString()
	{
		return "Precision" + (cutoff ? String.format("(%g)", cutoffFactor) : "");
	}
}
