package de.dailab.recommender.latent;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.SimpleUnipartiteDataset;
import de.dailab.recommender.dataset.WeightRange;
import de.dailab.recommender.matrix.Matrix;
import de.dailab.recommender.recommendation.Recommendation;
import de.dailab.recommender.recommendation.RecommendationUtils;

/**
 * The default latent recommender has a minimum of accuracy.
 * 
 * @author kunegis
 */
public class TestDefault
{
	/**
	 * Test default predictor on a chain.
	 * <p>
	 * We have to add a triangle to the chain to make the exponential kernel work (i.e., we make the graph
	 * non-bipartite).
	 */
	@Test
	public void testDefault()
	{
		final int n = 50, k = 10;
		final SimpleUnipartiteDataset dataset = new SimpleUnipartiteDataset(n + 2, WeightRange.UNWEIGHTED);

		final Matrix matrix = dataset.getMatrix();

		for (int i = 0; i + 1 < n; ++i)
			matrix.set(i, i + 1, 1);

		/* Extra triangle */
		matrix.set(n - 1, n, 1);
		matrix.set(n - 1, n + 1, 1);
		matrix.set(n, n + 1, 1);

		/* Only works for full rank */
		final int rank = n + 2;
// final int rank = n * 2 / 3;

		final LatentRecommender recommender = new LatentRecommender(rank);

		final LatentRecommenderModel recommenderModel = recommender.build(dataset);

		final Iterator <Recommendation> iterator = recommenderModel.recommend(new Entity(
		    SimpleUnipartiteDataset.ENTITY, 0), new EntityType[]
		{ SimpleUnipartiteDataset.ENTITY });

		final List <Recommendation> recommendations = RecommendationUtils.read(iterator, k);

		assert recommendations.size() == k;

		System.out.printf("Recommender:  %s\n", recommender);

		final LatentPredictorModel latentPredictorModel = recommenderModel.getLatentPredictorModel();

		System.out.println("Lambda:");
		for (int i = 0; i < rank; ++i)
			System.out.printf("%.3g ", latentPredictorModel.lambda[i]);
		System.out.println("\n");

		for (int i = 0; i < n; ++i)
		{
			final double score = latentPredictorModel.predict(new Entity(SimpleUnipartiteDataset.ENTITY, 0),
			    new Entity(SimpleUnipartiteDataset.ENTITY, i));

			System.out.printf("prediction(0, %2d) = %g\n", i, score);
		}

		System.out.println();

		for (int i = 0; i < k; ++i)
		{
			final Recommendation recommendation = recommendations.get(i);
			System.out.printf("recommendations[%2d] = %2d %g\n", i, recommendation.getEntity().getId(), recommendation
			    .getScore());
		}

		assert recommendations.get(0).getEntity().getId() == 0;
		assert recommendations.get(1).getEntity().getId() == 1;
		assert recommendations.get(2).getEntity().getId() == 2;
// assert recommendations.get(3).getEntity().getId() == 3;
	}
}
