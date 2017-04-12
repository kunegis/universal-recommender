package de.dailab.recommender.path;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.DatasetEntry;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntitySet;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.RelationshipSet;
import de.dailab.recommender.dataset.RelationshipType;
import de.dailab.recommender.dataset.SimpleUnipartiteDataset;
import de.dailab.recommender.dataset.UnirelationalDataset;
import de.dailab.recommender.dataset.WeightRange;
import de.dailab.recommender.matrix.Matrix;
import de.dailab.recommender.matrix.template.MatrixFactory;
import de.dailab.recommender.predict.RelationshipTypePonderation;
import de.dailab.recommender.recommend.RecommendationResult;
import de.dailab.recommender.recommend.Recommender;
import de.dailab.recommender.recommend.RecommenderModel;
import de.dailab.recommender.recommendation.LookaheadRecommendationIterator;
import de.dailab.recommender.recommendation.Recommendation;
import de.dailab.recommender.recommendation.RecommendationUtils;

/**
 * Test the AllPath on the synthetic dataset.
 * 
 * @author kunegis
 */
public class TestAllPath
{
	/**
	 * Test the AllPath on the synthetic dataset.
	 */
	@Test
	public void test()
	{
		final UnirelationalDataset dataset = new SyntheticDataset();

		final Path path = new AllPath2(.5);

		final Map <Entity, Set <DatasetEntry>> trail = new HashMap <Entity, Set <DatasetEntry>>();

		final Iterator <Recommendation> iterator = path.recommend(dataset, new Entity(SyntheticDataset.ENTITY, 0),
		    trail);

		final Map <Entity, Double> weights = new HashMap <Entity, Double>();

		for (int i = 0; i < dataset.getEntitySet(SimpleUnipartiteDataset.ENTITY).size(); ++i)
		{
			weights.put(new Entity(SimpleUnipartiteDataset.ENTITY, i), 0.);
		}

		for (int i = 0; i < 10 && iterator.hasNext(); ++i)
		{
			final Recommendation recommendation = iterator.next();

			double weight = weights.get(recommendation.getEntity());
			weight += recommendation.getScore();
			weights.put(recommendation.getEntity(), weight);

			System.out.printf("Recommendation:  %s\n", recommendation);
			System.out.printf("Weights: %s\n\n", weights);
		}
	}

	/**
	 * Test the path recommender on a chain of negative edges. The recommender should return every other entity.
	 */
	@Test
	public void testNegative()
	{
		final int n = 100;

		final UnirelationalDataset dataset = new SimpleUnipartiteDataset(n);

		final Matrix matrix = dataset.getMatrix();

		for (int i = 0; i + 1 < n; ++i)
			matrix.set(i, i + 1, -1);

		final Recommender recommender = new PathRecommender();

		final RecommenderModel recommenderModel = recommender.build(dataset);

		final int k = 10;

		final List <Recommendation> recommendations = RecommendationUtils.read(recommenderModel.recommend(new Entity(
		    SimpleUnipartiteDataset.ENTITY, 0), new EntityType[]
		{ SimpleUnipartiteDataset.ENTITY }), k);

		System.out.println(RecommendationUtils.format(recommendations, dataset));

		assert recommendations.size() == k;

		for (int i = 0; i < k; ++i)
		{
			assert recommendations.get(i).getEntity().getId() == i * 2;
		}
	}

	/**
	 * Highly-weighted edges are followed first.
	 */
	@Test
	public void testEdgeWeight()
	{
		final UnirelationalDataset dataset = new SimpleUnipartiteDataset(5, WeightRange.POSITIVE);

		final Matrix matrix = dataset.getMatrix();

		matrix.set(0, 1, .5);
		matrix.set(0, 2, .3);
		matrix.set(0, 3, .7);
		matrix.set(0, 4, .4);

		final Path path = new AllPath2();

		final Iterator <Recommendation> iterator = path.recommend(dataset,
		    new Entity(SimpleUnipartiteDataset.ENTITY, 0), null);

		final List <Recommendation> recommendations = RecommendationUtils.read(iterator, 5);

		assert recommendations.get(1).getEntity().getId() == 3;
		assert recommendations.get(2).getEntity().getId() == 1;
		assert recommendations.get(3).getEntity().getId() == 4;
		assert recommendations.get(4).getEntity().getId() == 2;
	}

	/**
	 * Test the relationship type ponderation.
	 * <p>
	 * The graph is unipartite but with several relationship types.
	 */
	@Test
	public void testRelationshipTypePonderation()
	{
		final int n = 3;

		final EntityType entityType = new EntityType("entity");
		final RelationshipType relationshipTypeOne = new RelationshipType("one");
		final RelationshipType relationshipTypeTwo = new RelationshipType("two");

		/*
		 * Dataset
		 */
		final Dataset dataset = new Dataset();

		final EntitySet entitySet = new EntitySet(entityType);
		entitySet.setSize(n);
		dataset.addEntitySet(entitySet);

		final RelationshipSet relationshipSetOne = new RelationshipSet(relationshipTypeOne, entityType, entityType);
		final RelationshipSet relationshipSetTwo = new RelationshipSet(relationshipTypeTwo, entityType, entityType);

		final Matrix matrixOne = MatrixFactory.newMemoryMatrixUnweighted(n);
		final Matrix matrixTwo = MatrixFactory.newMemoryMatrixUnweighted(n);

		matrixOne.set(0, 1, 1);
		matrixTwo.set(0, 2, 1);

		relationshipSetOne.setMatrix(matrixOne);
		relationshipSetTwo.setMatrix(matrixTwo);
		dataset.addRelationshipSet(relationshipSetOne);
		dataset.addRelationshipSet(relationshipSetTwo);

		final Recommender recommenderOne = new PathRecommender(new AllPath2(new RelationshipTypePonderation(
		    relationshipTypeOne, 2)));
		final Recommender recommenderTwo = new PathRecommender(new AllPath2(new RelationshipTypePonderation(
		    relationshipTypeTwo, 2)));

		final RecommenderModel recommenderModelOne = recommenderOne.build(dataset);
		final RecommenderModel recommenderModelTwo = recommenderTwo.build(dataset);

		final Map <Entity, Double> sources = new HashMap <Entity, Double>();
		sources.put(new Entity(entityType, 0), 1.);

		final RecommendationResult recommendationResultOne = recommenderModelOne.recommendExt(sources, new EntityType[]
		{ entityType });
		final RecommendationResult recommendationResultTwo = recommenderModelTwo.recommendExt(sources, new EntityType[]
		{ entityType });

		Recommendation recommendationOne = recommendationResultOne.getIterator().next();
		Recommendation recommendationTwo = recommendationResultTwo.getIterator().next();

		if (recommendationOne.getEntity().getId() == 0)
		    recommendationOne = recommendationResultOne.getIterator().next();
		if (recommendationTwo.getEntity().getId() == 0)
		    recommendationTwo = recommendationResultTwo.getIterator().next();

		assert recommendationOne.getEntity().getId() == 1;
		assert recommendationTwo.getEntity().getId() == 2;
	}

	/**
	 * In a chain graph, the minimum recommendation path length is honored.
	 */
	@Test
	public void testMinLength()
	{
		final int n = 100;
		final int l = 5;

		final UnirelationalDataset dataset = new SimpleUnipartiteDataset(n);

		final Matrix matrix = dataset.getMatrix();

		for (int i = 0; i + 1 < n; ++i)
		{
			matrix.set(i, i + 1, 1);
		}

		final Path path = new AllPath2(null, .85, l);

		final Iterator <Recommendation> iterator = path.recommend(dataset,
		    new Entity(SimpleUnipartiteDataset.ENTITY, 0), null);

		final List <Recommendation> recommendations = new ArrayList <Recommendation>();

		int i = 0;
		while (iterator.hasNext() && ++i < 100)
		{
			final Recommendation recommendation = iterator.next();
			recommendations.add(recommendation);
			System.out.println(recommendation);
		}

		assert recommendations.get(0).getEntity().getId() == 5;
		assert recommendations.get(1).getEntity().getId() == 6;
		assert recommendations.get(2).getEntity().getId() == 7;
	}

	/**
	 * Test a large minimum length on a network with low diameter. Nothing should be returned.
	 */
	@Test
	public void testEmptyMinLength()
	{
		final int n = 100;
		final UnirelationalDataset dataset = new SimpleUnipartiteDataset(n);
		final Matrix matrix = dataset.getMatrix();
		for (int i = 0; i < n; ++i)
			for (int j = i + 1; j < n; ++j)
				matrix.set(i, j, 1);
		final Path path = new AllPath2(null, .85, 5);

		final Iterator <Recommendation> iterator = path.recommend(dataset,
		    new Entity(SimpleUnipartiteDataset.ENTITY, 0), null);

		assert !iterator.hasNext();
	}

	/**
	 * Weight path shorter than 5 very low. Don't use a minimum length.
	 */
	@Test
	public void testShortPathWeights()
	{
		final int n = 100;

		final UnirelationalDataset dataset = new SimpleUnipartiteDataset(n);

		final Matrix matrix = dataset.getMatrix();

		for (int i = 0; i + 1 < n; ++i)
		{
			matrix.set(i, i + 1, 1);
		}

		final Path path = new AllPath2(null, .85, new double[]
		{ .1, .1, .1, .1, .1, 100 });

		final Iterator <Recommendation> iterator = path.recommend(dataset,
		    new Entity(SimpleUnipartiteDataset.ENTITY, 0), null);

		int i = 0;
		while (iterator.hasNext() && ++i < 100)
		{
			final Recommendation recommendation = iterator.next();
			System.out.println(recommendation);
		}
	}

	/**
	 * Combination of shortest path weights and minimal length.
	 */
	@Test
	public void testShortPathWeightsAndMinLength()
	{
		final int n = 100;

		final UnirelationalDataset dataset = new SimpleUnipartiteDataset(n);

		final Matrix matrix = dataset.getMatrix();

		for (int i = 0; i + 1 < n; ++i)
		{
			matrix.set(i, i + 1, 1);
		}

		/*
		 * Lengths 0/1 are not returned. Lengths 2/3/4 are returned with increasing weight, then the decay is used.
		 */
		final Path path = new AllPath2(null, .85, new double[]
		{ 1, 1, 1, 2, 3, .5 }, 2);

		final Iterator <Recommendation> iterator = path.recommend(dataset,
		    new Entity(SimpleUnipartiteDataset.ENTITY, 0), null);

		final LookaheadRecommendationIterator lookaheadIterator = new LookaheadRecommendationIterator(7, iterator);

		final List <Recommendation> recommendations = new ArrayList <Recommendation>();

		int i = 0;
		while (lookaheadIterator.hasNext() && ++i < 100)
		{
			final Recommendation recommendation = lookaheadIterator.next();
			recommendations.add(recommendation);
			System.out.println(recommendation);
		}

		assert recommendations.get(0).getEntity().getId() == 4;
		assert recommendations.get(1).getEntity().getId() == 3;
		assert recommendations.get(2).getEntity().getId() == 2;
		assert recommendations.get(3).getEntity().getId() == 5;
		assert recommendations.get(4).getEntity().getId() == 6;
		assert recommendations.get(5).getEntity().getId() == 7;
	}

	/**
	 * The trail is complete when a minimal length is used.
	 */
	@Test
	public void testMinLengthTrail()
	{
		final int n = 100;
		final UnirelationalDataset dataset = new SimpleUnipartiteDataset(n);
		final Matrix matrix = dataset.getMatrix();
		for (int i = 0; i + 1 < n; ++i)
			matrix.set(i, i + 1, 1);
		final Path path = new AllPath2(null, .85, 6);

		final Map <Entity, Set <DatasetEntry>> trail = new HashMap <Entity, Set <DatasetEntry>>();

		final Iterator <Recommendation> iterator = path.recommend(dataset,
		    new Entity(SimpleUnipartiteDataset.ENTITY, 0), trail);

		final List <Recommendation> recommendations = RecommendationUtils.read(iterator, 10);

		assert recommendations.size() == 10;
		assert recommendations.get(0).getEntity().getId() == 6;

		final Set <DatasetEntry> previous = trail.get(new Entity(SimpleUnipartiteDataset.ENTITY, 1));

		assert previous.size() == 1;
		assert previous.iterator().next().entity.getId() == 0;

		System.out.println(recommendations);
		System.out.println(trail);
	}
}
