package de.dailab.recommender.constraint;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.SimpleUnipartiteDataset;
import de.dailab.recommender.path.PathRecommender;
import de.dailab.recommender.recommend.RecommendationResult;
import de.dailab.recommender.recommend.Recommender;
import de.dailab.recommender.recommend.RecommenderModel;
import de.dailab.recommender.recommendation.Recommendation;

/**
 * Test the no-self recommender.
 * 
 * @author kunegis
 */
public class TestNoSelfRecommender
{
	/**
	 * Make sure the no-self recommender passes through the visited entity set.
	 */
	@Test
	public void testNoSelfRecommender()
	{
		final SimpleUnipartiteDataset dataset = new SimpleUnipartiteDataset(7);

		dataset.getMatrix().set(0, 1, 1);
		dataset.getMatrix().set(1, 2, 1);
		dataset.getMatrix().set(2, 3, 1);
		dataset.getMatrix().set(3, 4, 1);
		dataset.getMatrix().set(4, 5, 1);
		dataset.getMatrix().set(5, 6, 1);

		final Recommender recommender = new NoSelfRecommender(new PathRecommender());

		final RecommenderModel recommenderModel = recommender.build(dataset);

		final Map <Entity, Double> sources = new HashMap <Entity, Double>();
		sources.put(new Entity(SimpleUnipartiteDataset.ENTITY, 0), 1.);

		final RecommendationResult recommendationResult = recommenderModel.recommendExt(sources, new EntityType[]
		{ SimpleUnipartiteDataset.ENTITY });

		final Set <Entity> recommendedEntities = new HashSet <Entity>();

		final Iterator <Recommendation> iterator = recommendationResult.getIterator();
		while (iterator.hasNext())
		{
			final Recommendation recommendation = iterator.next();
			recommendedEntities.add(recommendation.getEntity());
		}

		final Set <Entity> visitedEntities = recommendationResult.getVisitedEntities();

		assert recommendedEntities.size() == 6;
		assert visitedEntities.size() == 7;

		assert visitedEntities.contains(new Entity(SimpleUnipartiteDataset.ENTITY, 0));

		for (int i = 1; i < 7; ++i)
		{
			assert visitedEntities.contains(new Entity(SimpleUnipartiteDataset.ENTITY, i));
			assert recommendedEntities.contains(new Entity(SimpleUnipartiteDataset.ENTITY, i));
		}
	}
}
