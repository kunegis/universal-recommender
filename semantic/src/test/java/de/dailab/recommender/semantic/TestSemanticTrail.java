package de.dailab.recommender.semantic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.DatasetEntry;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.path.AllPath2;
import de.dailab.recommender.path.PathRecommender;
import de.dailab.recommender.recommend.RecommendationResult;
import de.dailab.recommender.recommend.Recommender;
import de.dailab.recommender.recommend.RecommenderModel;
import de.dailab.recommender.recommendation.Recommendation;
import de.dailab.recommender.recommendation.RecommendationUtils;

/**
 * Test the semantic trail.
 * 
 * @author kunegis
 */
public class TestSemanticTrail
{
	/**
	 * Test recommendations for all users.
	 */
	@Test
	public void testMany()
	{
		final Dataset dataset = new BipartiteSyntheticSemanticDataset();

		final Recommender recommender = new PathRecommender();

		final RecommenderModel recommenderModel = recommender.build(dataset);

		/*
		 * We count how many recommendations were non-empty. Recommendations may be empty when the user is non
		 * connected. We check that a certain proportion of all recommendations are non-empty.
		 */
		int count = 0;
		int countNonEmpty = 0;

		for (int i = 0; i < dataset.getEntitySet(BipartiteSyntheticSemanticDataset.USER).size(); ++i)
		{
			final Map <Entity, Double> sources = new HashMap <Entity, Double>();
			sources.put(new Entity(BipartiteSyntheticSemanticDataset.USER, i), 1.);

			final RecommendationResult recommendationResult = recommenderModel.recommendExt(sources, new EntityType[]
			{ BipartiteSyntheticSemanticDataset.USER });

			final List <Recommendation> recommendations = RecommendationUtils.read(recommendationResult.getIterator(),
			    100);

			assert recommendations.size() > 0;

			for (final Recommendation recommendation: recommendations)
			{
				final Entity entity = recommendation.getEntity();

				final SemanticTrail semanticTrail = new SemanticTrail(recommendationResult, entity);

				++count;

				assert semanticTrail.getTrail().size() <= SemanticTrail.ENTITY_COUNT_MAX_DEFAULT;

				if (semanticTrail.getTrail().size() > 0)
				{
					++countNonEmpty;
					final Set <Entity> entities = new HashSet <Entity>();

					/*
					 * Count paths: the number of paths equals one plus the number of bifurcations.
					 */
					final int pathCount[] = new int[1];
					pathCount[0] = 1;

					check(semanticTrail, entity, entities, pathCount, SemanticTrail.PATH_LENGTH_MAX_DEFAULT);

					/* All nodes are reachable from the recommended entity */
					final Set <Entity> rest = new HashSet <Entity>();
					rest.addAll(semanticTrail.getTrail().keySet());
					rest.removeAll(entities);
					assert rest.isEmpty();

					/* Number of paths */
					assert pathCount[0] <= SemanticTrail.PATH_COUNT_MAX_DEFAULT;
					assert pathCount[0] > 0;
				}
			}
		}

		assert ((double) countNonEmpty) / count > .9;
	}

	/**
	 * Check that the trail beginning at the given entity is valid.
	 * <p>
	 * This call is recursive and hangs if there are cycles in the trail. This is OK because it will make the unit test
	 * fail.
	 * <p>
	 * It is OK for the semantic trail to not contain the entity.
	 * <p>
	 * All reachable entities are written into the given entity set, including the given entity.
	 * <p>
	 * The number of paths found is added to the given path count.
	 * 
	 * @param semanticTrail The trail
	 * @param entity The recommended entity
	 * @param pathCount The path count
	 * @param pathLengthMax Check that paths are not longer than this value
	 */
	private static void check(SemanticTrail semanticTrail, Entity entity, Set <Entity> entities,
	    int pathCount[/* 1 */], int pathLengthMax)
	{
		assert pathLengthMax >= 0;

		if (entities.contains(entity)) return;

		entities.add(entity);
		final Set <DatasetEntry> datasetEntries = semanticTrail.getTrail().get(entity);

		if (datasetEntries == null) return;

		if (datasetEntries.size() > 0) pathCount[0] += datasetEntries.size() - 1;

		for (final DatasetEntry datasetEntry: datasetEntries)
		{
			check(semanticTrail, datasetEntry.entity, entities, pathCount, pathLengthMax - 1);
		}
	}

	/**
	 * A long trail always ends in a source entity.
	 */
	@Test
	public void testLongTrail()
	{
		/** Number of source users */
		final int n = 10;

		final Dataset dataset = new BipartiteSyntheticSemanticDataset();

		/**
		 * Recommended entities will be at distance 3 from the sources.
		 */
		final Recommender recommender = new PathRecommender(new AllPath2(null, .85, 3));

		final RecommenderModel recommenderModel = recommender.build(dataset);

		final Map <Entity, Double> sources = new HashMap <Entity, Double>();
		for (int i = 0; i < n; ++i)
			sources.put(new Entity(BipartiteSyntheticSemanticDataset.USER, i), 1.);

		final RecommendationResult recommendationResult = recommenderModel.recommendExt(sources, new EntityType[]
		{ BipartiteSyntheticSemanticDataset.MOVIE });

		final List <Recommendation> recommendations = RecommendationUtils.read(recommendationResult.getIterator(), 20);

		assert recommendations.size() > 0;

		System.out.println(recommendations);

		final Map <Entity, Set <DatasetEntry>> trail = recommendationResult.getTrail();

		/*
		 * The trail is a directed acyclic graph. All its final nodes must be source entities.
		 */
		int sourceCount = 0;
		for (final Set <DatasetEntry> datasetEntries: trail.values())
		{
			for (final DatasetEntry datasetEntry: datasetEntries)
			{
				final Entity origin = datasetEntry.entity;
				if (origin.getId() < n)
				{
					++sourceCount;
				}
				else
				{
					/* The trail does not end at a non-source entity */
					assert trail.containsKey(origin);
				}
			}
		}
		assert sourceCount > 0;

		/*
		 * The same test for each single trail leading to individual recommendations.
		 */
		for (final Recommendation recommendation: recommendations)
		{
			final SemanticTrail semanticTrail = new SemanticTrail(recommendationResult, recommendation.getEntity(),
			    10000, 10, 10000);

			final Map <Entity, Set <DatasetEntry>> singleTrail = semanticTrail.getTrail();

			System.out.printf("Single trail for %s:  \n%s\n", recommendation, singleTrail);

			int singleRecommendationCount = 0;
			for (final Set <DatasetEntry> datasetEntries: singleTrail.values())
			{
				for (final DatasetEntry datasetEntry: datasetEntries)
				{
					final Entity origin = datasetEntry.entity;
					if (origin.getId() < n)
					{
						++singleRecommendationCount;
					}
					else
					{
						/* The trail does not end at a non-source entity */
						assert singleTrail.containsKey(origin);
					}
				}
			}
			assert sourceCount > 0;
		}
	}
}
