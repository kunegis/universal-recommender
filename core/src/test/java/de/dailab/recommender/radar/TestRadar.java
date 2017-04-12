package de.dailab.recommender.radar;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import de.dailab.recommender.constraint.NoSelfRecommender;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.SimpleUnipartiteDataset;
import de.dailab.recommender.path.CompoundPath;
import de.dailab.recommender.path.PathRecommender;
import de.dailab.recommender.path.RelationshipPath;
import de.dailab.recommender.recommend.RecommendationResult;
import de.dailab.recommender.recommend.Recommender;
import de.dailab.recommender.recommend.RecommenderModel;
import de.dailab.recommender.recommendation.RecommendationUtils;

/**
 * Test the radar.
 * 
 * @author kunegis
 */
public class TestRadar
{
	/**
	 * Test using the radar with visited entities from extended recommendations.
	 */
	@Test
	public void testVisitedEntities()
	{
		final SimpleUnipartiteDataset simpleUnipartitedataset = new SimpleUnipartiteDataset(9);

		simpleUnipartitedataset.getMatrix().set(0, 1, 1);
		simpleUnipartitedataset.getMatrix().set(1, 2, 1);
		simpleUnipartitedataset.getMatrix().set(2, 3, 1);
		simpleUnipartitedataset.getMatrix().set(3, 4, 1);
		simpleUnipartitedataset.getMatrix().set(4, 5, 1);
		simpleUnipartitedataset.getMatrix().set(5, 6, 1);
		simpleUnipartitedataset.getMatrix().set(6, 7, 1);

		final RelationshipPath relationshipPath = new RelationshipPath(SimpleUnipartiteDataset.RELATIONSHIP);

		final Recommender recommender = new NoSelfRecommender(new PathRecommender(new CompoundPath(relationshipPath,
		    relationshipPath, relationshipPath, relationshipPath)));

		final RecommenderModel recommenderModel = recommender.build(simpleUnipartitedataset);

		final Map <Entity, Double> sources = new HashMap <Entity, Double>();
		sources.put(new Entity(SimpleUnipartiteDataset.ENTITY, 0), 1.);

		final RecommendationResult recommendationResult = recommenderModel.recommendExt(sources, new EntityType[]
		{ SimpleUnipartiteDataset.ENTITY });

		RecommendationUtils.read(recommendationResult.getIterator(), 100);

		final Set <Entity> visitedEntities = recommendationResult.getVisitedEntities();

		final Entity entities[] = new Entity[visitedEntities.size()];
		int i = 0;
		for (final Entity entity: visitedEntities)
			entities[i++] = entity;

		final Radar radar = new Radar(entities, simpleUnipartitedataset);

		assert radar.getPoints().size() == 5;

		int map = 0;
		for (final Point point: radar.getPoints())
		{
			map |= 1 << point.entity.getId();
		}
		assert map == 0x1F; /* points 0 to 4 */
	}

	/**
	 * The radar accepts a recommendation result as argument, using its visited entities as points.
	 */
	@Test
	public void testRecommendationResult()
	{
		final SimpleUnipartiteDataset simpleUnipartitedataset = new SimpleUnipartiteDataset(9);

		simpleUnipartitedataset.getMatrix().set(0, 1, 1);
		simpleUnipartitedataset.getMatrix().set(1, 2, 1);
		simpleUnipartitedataset.getMatrix().set(2, 3, 1);
		simpleUnipartitedataset.getMatrix().set(3, 4, 1);
		simpleUnipartitedataset.getMatrix().set(4, 5, 1);
		simpleUnipartitedataset.getMatrix().set(5, 6, 1);
		simpleUnipartitedataset.getMatrix().set(6, 7, 1);

		final RelationshipPath relationshipPath = new RelationshipPath(SimpleUnipartiteDataset.RELATIONSHIP);

		final Recommender recommender = new NoSelfRecommender(new PathRecommender(new CompoundPath(relationshipPath,
		    relationshipPath, relationshipPath, relationshipPath)));

		final RecommenderModel recommenderModel = recommender.build(simpleUnipartitedataset);

		final Map <Entity, Double> sources = new HashMap <Entity, Double>();
		sources.put(new Entity(SimpleUnipartiteDataset.ENTITY, 0), 1.);

		final RecommendationResult recommendationResult = recommenderModel.recommendExt(sources, new EntityType[]
		{ SimpleUnipartiteDataset.ENTITY });

		RecommendationUtils.read(recommendationResult.getIterator(), 100);

		final Radar radar = new Radar(recommendationResult, simpleUnipartitedataset);

		assert radar.getPoints().size() == 5;

		int map = 0;
		for (final Point point: radar.getPoints())
		{
			map |= 1 << point.entity.getId();
		}
		assert map == 0x1F; /* points 0 to 4 */
	}

}
