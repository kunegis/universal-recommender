package de.dailab.recommender.recommendation;

import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.recommendation.Recommendation;
import de.dailab.recommender.recommendation.RecommendationUtils;

/**
 * Test the static methods in class Recommendations.
 * 
 * @author kunegis
 */
public class TestRecommendationUtils
{
	private static final Entity ENTITY = new Entity(new EntityType("movie"), 7);

	private static class Case
	{
		public Case(int k, double dst[], double src[], double ret[])
		{
			this.k = k;
			this.dst = toRecommendationList(dst);
			this.src = toRecommendationList(src);
			this.ret = toRecommendationList(ret);
		}

		public final int k;
		public final List <Recommendation> dst;
		public final List <Recommendation> src;
		public final List <Recommendation> ret;

	}

	private static final Case CASES[] = new Case[]
	{ new Case(99, new double[]
	{ 9, 7, 3 }, new double[]
	{ 8, 5 }, new double[]
	{ 9, 8, 7, 5, 3 }), new Case(99, new double[]
	{ 9, 8, 7 }, new double[]
	{ 3, 2, 1 }, new double[]
	{ 9, 8, 7, 3, 2, 1 }), new Case(99, new double[]
	{ 2, 1 }, new double[] {}, new double[]
	{ 2, 1 }), new Case(99, new double[] {}, new double[]
	{ 2, 1, 0 }, new double[]
	{ 2, 1, 0 }), new Case(99, new double[] {}, new double[] {}, new double[] {}), new Case(99, new double[]
	{ 8, 2 }, new double[]
	{ 7, 6, 3 }, new double[]
	{ 8, 7, 6, 3, 2 }), new Case(99, new double[]
	{ 9, 8, 5, 4 }, new double[]
	{ 7, 6, 3, 2 }, new double[]
	{ 9, 8, 7, 6, 5, 4, 3, 2 }), new Case(5, new double[]
	{ 8, 6, 4, 2 }, new double[]
	{ 9, 7, 5, 3, 1 }, new double[]
	{ 9, 8, 7, 6, 5 }), new Case(3, new double[]
	{ 3, 2 }, new double[]
	{ 1 }, new double[]
	{ 3, 2, 1 }), new Case(1, new double[]
	{ 2 }, new double[]
	{ 1 }, new double[]
	{ 2 }), };

	private static class SingleCase
	{
		public SingleCase(int k, double dst[], double recommendation, double ret[])
		{
			this.k = k;
			this.dst = toRecommendationList(dst);
			this.recommendation = new Recommendation(ENTITY, recommendation);
			this.ret = toRecommendationList(ret);
		}

		public final int k;
		public final List <Recommendation> dst;
		public final Recommendation recommendation;
		public final List <Recommendation> ret;
	}

	private static final SingleCase SINGLE_CASES[] = new SingleCase[]
	{ new SingleCase(99, new double[]
	{ 9, 6, 3 }, 5, new double[]
	{ 9, 6, 5, 3 }), new SingleCase(99, new double[]
	{ 4, 3, 2 }, 5, new double[]
	{ 5, 4, 3, 2 }), new SingleCase(99, new double[]
	{ 9, 8, 7 }, 5, new double[]
	{ 9, 8, 7, 5 }), new SingleCase(4, new double[]
	{ 8, 6, 4, 2 }, 5, new double[]
	{ 8, 6, 5, 4 }), new SingleCase(4, new double[]
	{ 4, 3, 2, 1 }, 5, new double[]
	{ 5, 4, 3, 2 }), new SingleCase(4, new double[]
	{ 9, 8, 7, 6 }, 5, new double[]
	{ 9, 8, 7, 6 }), };

	/**
	 * Test merging of recommendation lists.
	 */
	@Test
	public void testMerge()
	{
		for (final Case kase: CASES)
		{
			RecommendationUtils.merge(kase.dst, kase.src, kase.k);
			Assert.assertTrue(kase.dst.equals(kase.ret));
		}

		for (final SingleCase singleCase: SINGLE_CASES)
		{
			RecommendationUtils.merge(singleCase.dst, singleCase.recommendation, singleCase.k);
			Assert.assertTrue(singleCase.dst.equals(singleCase.ret));
		}
	}

	/**
	 * Create recommendation list from scores with unspecified, single entity.
	 * 
	 * @param scores Arrays of scores to use
	 * @return Recommendation list, in same order as given score array
	 */
	private static List <Recommendation> toRecommendationList(double scores[])
	{
		final List <Recommendation> ret = new LinkedList <Recommendation>();
		for (final double score: scores)
			ret.add(new Recommendation(ENTITY, score));
		return ret;
	}
}
