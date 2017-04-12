package de.dailab.recommender.recommend;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.path.PathRecommender;
import de.dailab.recommender.random.RandomGraph;
import de.dailab.recommender.recommendation.Recommendation;
import de.dailab.recommender.recommendation.RecommendationUtils;

/**
 * Test performance of the AllPath on large networks.
 * 
 * @author kunegis
 */
public class TestAllPathRecommenderLarge
{
	/**
	 * Run the all-path on a large random graph. This must finish in a timely fashion.
	 * <p>
	 * Bug observed in the UCPM actor recommender: the all-path recommender takes too long even when retrieving just a
	 * few recommendations.
	 */
	@Test
	public void test()
	{
		final int n = 50000;
		final double p = 10. / n;

		final Dataset dataset = new RandomGraph(n, p);

		final Recommender recommender = new PathRecommender(1000);

		final RecommenderModel recommenderModel = recommender.build(dataset);

		final Map <Entity, Double> sources = new HashMap <Entity, Double>();
		sources.put(new Entity(RandomGraph.ENTITY_TYPE, 0), 1.);
		sources.put(new Entity(RandomGraph.ENTITY_TYPE, 1), 1.);
		sources.put(new Entity(RandomGraph.ENTITY_TYPE, 2), 1.);

		final RecommendationResult recommendationResult = recommenderModel.recommendExt(sources, new EntityType[]
		{ RandomGraph.ENTITY_TYPE });

		final int k = 10;

		final List <Recommendation> recommendations = RecommendationUtils.read(recommendationResult.getIterator(), k);

		assert recommendations.size() == k;
	}
}
