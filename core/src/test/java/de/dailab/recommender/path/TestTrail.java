package de.dailab.recommender.path;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import de.dailab.recommender.constraint.NoSelfRecommender;
import de.dailab.recommender.dataset.DatasetEntry;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityPonderation;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.SimpleUnipartiteDataset;
import de.dailab.recommender.recommend.RecommendationResult;
import de.dailab.recommender.recommend.Recommender;
import de.dailab.recommender.recommend.RecommenderModel;
import de.dailab.recommender.recommendation.Recommendation;
import de.dailab.recommender.recommendation.RecommendationUtils;

/**
 * Test the trail of path recommenders: return a directed graph explaining where recommendations come from.
 * 
 * @author kunegis
 */
public class TestTrail
{
	/**
	 * Test the trail on a chain graph.
	 */
	@Test
	public void testChainGraph()
	{
		/** Length of chain */
		final int n = 10;

		final SimpleUnipartiteDataset dataset = new SimpleUnipartiteDataset(n);

		for (int i = 0; i + 1 < n; ++i)
			dataset.getMatrix().set(i, i + 1, 1);

		final Recommender recommender = new PathRecommender();

		final RecommenderModel recommenderModel = recommender.build(dataset);

		final RecommendationResult recommendationResult = recommenderModel.recommendExt(new EntityPonderation(
		    new Entity(SimpleUnipartiteDataset.ENTITY, 0)), new EntityType[]
		{ SimpleUnipartiteDataset.ENTITY });

		final List <Recommendation> recommendations = RecommendationUtils.read(recommendationResult.getIterator(),
		    2 * n);

		assert recommendations.size() == n;

		final Map <Entity, Set <DatasetEntry>> trail = recommendationResult.getTrail();

		assert trail != null;

		for (int i = n - 2; i >= 0; --i)
		{
			final Entity entity = new Entity(SimpleUnipartiteDataset.ENTITY, i + 1);
			final Set <DatasetEntry> datasetEntries = trail.get(entity);
			assert datasetEntries.size() > 0;
			assert datasetEntries.contains(new DatasetEntry(new Entity(SimpleUnipartiteDataset.ENTITY, i),
			    SimpleUnipartiteDataset.RELATIONSHIP));
// assert datasetEntries.iterator().next().entity.equals(new Entity(SimpleUnipartiteDataset.ENTITY, i));
		}
	}

	/**
	 * In a star graph, recommendations for all outer vertices should return the central vertices, and there should be
	 * trails from the central vertex to the outer vertices.
	 */
	@Test
	public void testStarGraph()
	{
		/**
		 * Degree of central vertex in the star. The central node has ID , the others 1 to (n+1).
		 */
		final int n = 10;

		final SimpleUnipartiteDataset dataset = new SimpleUnipartiteDataset(n + 1);

		for (int i = 0; i < n; ++i)
			dataset.getMatrix().set(0, 1 + i, 1);

		final Recommender recommender = new NoSelfRecommender(new PathRecommender());

		final RecommenderModel recommenderModel = recommender.build(dataset);

		final Map <Entity, Double> sources = new HashMap <Entity, Double>();
		for (int i = 0; i < n; ++i)
			sources.put(new Entity(SimpleUnipartiteDataset.ENTITY, 1 + i), 1.);

		final RecommendationResult recommendationResult = recommenderModel.recommendExt(sources, new EntityType[]
		{ SimpleUnipartiteDataset.ENTITY });

		final List <Recommendation> recommendations = RecommendationUtils.read(recommendationResult.getIterator(), 2);

		assert recommendations.size() == 1;

		final Recommendation recommendation = recommendations.get(0);

		assert recommendation.getEntity().equals(new Entity(SimpleUnipartiteDataset.ENTITY, 0));
		assert recommendation.getScore() > 0;

		final Map <Entity, Set <DatasetEntry>> trail = recommendationResult.getTrail();

		/*
		 * Since the all path may follow edges multiple times, there will be a trail from the outer entities to the
		 * central entity.
		 */

		assert trail.size() >= 1;
		final Set <DatasetEntry> previous = trail.get(new Entity(SimpleUnipartiteDataset.ENTITY, 0));
		assert previous != null;
		assert previous.size() > 1;
	}
}
