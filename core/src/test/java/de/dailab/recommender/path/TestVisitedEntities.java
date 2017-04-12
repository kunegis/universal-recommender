package de.dailab.recommender.path;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.junit.Test;

import de.dailab.recommender.dataset.DatasetEntry;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.SimpleUnipartiteDataset;
import de.dailab.recommender.recommend.RecommendationResult;
import de.dailab.recommender.recommend.Recommender;
import de.dailab.recommender.recommend.RecommenderModel;
import de.dailab.recommender.recommendation.Recommendation;

/**
 * Test that the path recommender returns the set of visited entities.
 * 
 * @author kunegis
 */
public class TestVisitedEntities
{
	/**
	 * Let AllPath find entities and check that it correctly returns the visited entities.
	 */
	@Test
	public void testVisitedEntities()
	{
		final SimpleUnipartiteDataset dataset = new SimpleUnipartiteDataset(5);

		dataset.getMatrix().set(0, 1, 1);
		dataset.getMatrix().set(1, 2, 1);
		dataset.getMatrix().set(2, 4, 1);

		final Map <Entity, Set <DatasetEntry>> trail = new HashMap <Entity, Set <DatasetEntry>>();

		final Path path = new AllPath2();
		final Iterator <Recommendation> iterator = path.recommend(dataset,
		    new Entity(SimpleUnipartiteDataset.ENTITY, 0), trail);

		final Set <Entity> recommendedEntities = new HashSet <Entity>();

		for (int i = 0; i < 10 && iterator.hasNext(); ++i)
		{
			final Recommendation recommendation = iterator.next();
			recommendedEntities.add(recommendation.getEntity());
		}

		final Set <Entity> visitedEntities = new HashSet <Entity>();
		for (final Entry <Entity, Set <DatasetEntry>> entry: trail.entrySet())
		{
			visitedEntities.add(entry.getKey());
			for (final DatasetEntry datasetEntry: entry.getValue())
				visitedEntities.add(datasetEntry.entity);
		}

		assert visitedEntities.size() == 4;
		assert recommendedEntities.size() == 4;
		for (final int i: new int[]
		{ 0, 1, 2, 4 })
		{
			final Entity entity = new Entity(SimpleUnipartiteDataset.ENTITY, i);
			assert visitedEntities.contains(entity);
			assert recommendedEntities.contains(entity);
		}
	}

	/**
	 * Let the path recommender recommend entities and check that the set of visited entities is correctly set.
	 */
	@Test
	public void testVisitedPathRecommender()
	{
		final SimpleUnipartiteDataset dataset = new SimpleUnipartiteDataset(5);

		dataset.getMatrix().set(0, 1, 1);
		dataset.getMatrix().set(1, 2, 1);
		dataset.getMatrix().set(2, 4, 1);

		final Recommender recommender = new PathRecommender();

		final RecommenderModel recommenderModel = recommender.build(dataset);

		final Map <Entity, Double> sources = new HashMap <Entity, Double>();
		sources.put(new Entity(SimpleUnipartiteDataset.ENTITY, 0), 1.);

		final RecommendationResult recommendationResult = recommenderModel.recommendExt(sources, new EntityType[]
		{ SimpleUnipartiteDataset.ENTITY });

		final Iterator <Recommendation> iterator = recommendationResult.getIterator();
		while (iterator.hasNext())
			iterator.next();

		final Set <Entity> visitedEntities = recommendationResult.getVisitedEntities();

		assert visitedEntities.size() == 4;
		for (final int i: new int[]
		{ 0, 1, 2, 4 })
		{
			final Entity entity = new Entity(SimpleUnipartiteDataset.ENTITY, i);
			assert visitedEntities.contains(entity);
		}
	}

	/**
	 * Test that in a compound path, the jumped-over entity are returned as visited.
	 */
	@Test
	public void testCompoundPath()
	{
		final SimpleUnipartiteDataset dataset = new SimpleUnipartiteDataset(4);

		dataset.getMatrix().set(0, 1, 1);
		dataset.getMatrix().set(1, 2, 1);

		final Path path = new CompoundRelationshipPath(SimpleUnipartiteDataset.RELATIONSHIP,
		    SimpleUnipartiteDataset.RELATIONSHIP);

		final Set <Entity> recommendedEntities = new HashSet <Entity>();

		final Map <Entity, Set <DatasetEntry>> trail = new HashMap <Entity, Set <DatasetEntry>>();

		final Iterator <Recommendation> iterator = path.recommend(dataset,
		    new Entity(SimpleUnipartiteDataset.ENTITY, 0), trail);

		final Set <Entity> visitedEntities = new HashSet <Entity>();

		for (final Entry <Entity, Set <DatasetEntry>> entry: trail.entrySet())
		{
			visitedEntities.add(entry.getKey());
			for (final DatasetEntry datasetEntry: entry.getValue())
				visitedEntities.add(datasetEntry.entity);
		}

		while (iterator.hasNext())
		{
			final Recommendation recommendation = iterator.next();
			recommendedEntities.add(recommendation.getEntity());
		}

		assert visitedEntities.size() == 3;
		assert recommendedEntities.size() == 1;

		assert visitedEntities.contains(new Entity(SimpleUnipartiteDataset.ENTITY, 0));
		assert visitedEntities.contains(new Entity(SimpleUnipartiteDataset.ENTITY, 1));
		assert visitedEntities.contains(new Entity(SimpleUnipartiteDataset.ENTITY, 2));
		assert recommendedEntities.contains(new Entity(SimpleUnipartiteDataset.ENTITY, 2));
	}

	/**
	 * Test parallel paths.
	 */
	@Test
	public void testParallelPath()
	{
		final SimpleUnipartiteDataset dataset = new SimpleUnipartiteDataset(7);

		dataset.getMatrix().set(0, 1, 1);
		dataset.getMatrix().set(1, 2, 1);
		dataset.getMatrix().set(2, 3, 1);
		dataset.getMatrix().set(3, 4, 1);
		dataset.getMatrix().set(4, 5, 1);
		dataset.getMatrix().set(5, 6, 1);

		final Path relationshipPath = new RelationshipPath(SimpleUnipartiteDataset.RELATIONSHIP);

		final Path path = new ParallelPath(relationshipPath, new CompoundPath(relationshipPath, relationshipPath),
		    new CompoundPath(relationshipPath, relationshipPath, relationshipPath, relationshipPath, relationshipPath));

		final Set <Entity> recommendedEntities = new HashSet <Entity>();

		final Map <Entity, Set <DatasetEntry>> trail = new HashMap <Entity, Set <DatasetEntry>>();

		final Iterator <Recommendation> iterator = path.recommend(dataset,
		    new Entity(SimpleUnipartiteDataset.ENTITY, 0), trail);

		final Set <Entity> visitedEntities = new HashSet <Entity>();
		for (final Entry <Entity, Set <DatasetEntry>> entry: trail.entrySet())
		{
			visitedEntities.add(entry.getKey());
			for (final DatasetEntry datasetEntry: entry.getValue())
				visitedEntities.add(datasetEntry.entity);
		}

		while (iterator.hasNext())
		{
			final Recommendation recommendation = iterator.next();
			recommendedEntities.add(recommendation.getEntity());
		}

		assert visitedEntities.size() == 6;
		assert recommendedEntities.size() == 3;

		assert visitedEntities.contains(new Entity(SimpleUnipartiteDataset.ENTITY, 0));
		assert visitedEntities.contains(new Entity(SimpleUnipartiteDataset.ENTITY, 1));
		assert visitedEntities.contains(new Entity(SimpleUnipartiteDataset.ENTITY, 2));
		assert visitedEntities.contains(new Entity(SimpleUnipartiteDataset.ENTITY, 3));
		assert visitedEntities.contains(new Entity(SimpleUnipartiteDataset.ENTITY, 4));
		assert visitedEntities.contains(new Entity(SimpleUnipartiteDataset.ENTITY, 5));
		assert recommendedEntities.contains(new Entity(SimpleUnipartiteDataset.ENTITY, 1));
		assert recommendedEntities.contains(new Entity(SimpleUnipartiteDataset.ENTITY, 2));
		assert recommendedEntities.contains(new Entity(SimpleUnipartiteDataset.ENTITY, 5));
	}
}
