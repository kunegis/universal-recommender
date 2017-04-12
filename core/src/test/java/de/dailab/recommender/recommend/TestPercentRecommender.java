package de.dailab.recommender.recommend;

import java.util.Iterator;
import java.util.Random;

import org.junit.Test;

import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.SimpleUnipartiteDataset;
import de.dailab.recommender.dataset.UnirelationalDataset;
import de.dailab.recommender.latent.LatentRecommender;
import de.dailab.recommender.matrix.Matrix;
import de.dailab.recommender.recommendation.Recommendation;

/**
 * Test the percent recommender.
 * 
 * @author kunegis
 */
public class TestPercentRecommender
{
	/**
	 * Test the percent recommender.
	 */
	@Test
	public void test()
	{
		final int n = 100;
		final int r = 1000;
		final UnirelationalDataset dataset = new SimpleUnipartiteDataset(n);

		final Matrix matrix = dataset.getMatrix();

		final Random random = new Random();

		for (int i = 0; i < r; ++i)
		{
			matrix.set(random.nextInt(n), random.nextInt(n), random.nextGaussian());
		}

		final Recommender recommender = new PercentRecommender(new LatentRecommender());

		final RecommenderModel recommenderModel = recommender.build(dataset);

		final Iterator <Recommendation> iterator = recommenderModel.recommend(new Entity(
		    SimpleUnipartiteDataset.ENTITY, 1), new EntityType[]
		{ SimpleUnipartiteDataset.ENTITY });

		int count = 100;
		while (iterator.hasNext() && count-- > 0)
		{
			final Recommendation recommendation = iterator.next();

			System.out.printf("%d%% %s\n", (int) (100 * recommendation.getScore()), recommendation.getEntity());
		}
	}
}
