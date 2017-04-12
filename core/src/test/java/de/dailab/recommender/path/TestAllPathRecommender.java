package de.dailab.recommender.path;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.Test;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntitySet;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.MetadataName;
import de.dailab.recommender.dataset.RelationshipFormat;
import de.dailab.recommender.dataset.RelationshipSet;
import de.dailab.recommender.dataset.RelationshipType;
import de.dailab.recommender.dataset.SimpleBipartiteDataset;
import de.dailab.recommender.dataset.SimpleUnipartiteDataset;
import de.dailab.recommender.dataset.UnirelationalDataset;
import de.dailab.recommender.dataset.WeightRange;
import de.dailab.recommender.matrix.Matrix;
import de.dailab.recommender.matrix.template.MatrixFactory;
import de.dailab.recommender.recommend.RecommendationResult;
import de.dailab.recommender.recommend.Recommender;
import de.dailab.recommender.recommend.RecommenderModel;
import de.dailab.recommender.recommendation.Recommendation;
import de.dailab.recommender.recommendation.RecommendationUtils;

/**
 * Test the all path recommender on a small synthetic dataset.
 * 
 * @author kunegis
 */
public class TestAllPathRecommender
{
	/**
	 * On a small synthetic dataset, apply the path recommender.
	 * <p>
	 * Check that edge weights and parallel paths work.
	 */
	@Test
	public void test()
	{
		final UnirelationalDataset dataset = new SyntheticDataset();

		final Recommender recommender = new PathRecommender(new AllPath2(.99), 100);

		final RecommenderModel recommenderModel = recommender.build(dataset);

		final Iterator <Recommendation> iterator = recommenderModel.recommend(new Entity(SyntheticDataset.ENTITY, 0),
		    new EntityType[]
		    { SyntheticDataset.ENTITY });

		final List <Recommendation> recommendations = RecommendationUtils.read(iterator, 10);

		for (final Recommendation recommendation: recommendations)
		{
			System.out.println(recommendation);
		}

		assert recommendations.size() == 5;
	}

	/**
	 * The graph is a chain of length N. Compute recommendations for the first entity in the chain.
	 */
	@Test
	public void testChain()
	{
		final EntityType ENTITY = new EntityType("entity");
		final RelationshipType RELATIONSHIP = new RelationshipType("relationship");

		final Dataset dataset = new Dataset();

		final EntitySet entitySet = new EntitySet(ENTITY);
		final int N = 1000;
		entitySet.setSize(N);
		entitySet.setMetadataNames(new ArrayList <MetadataName>(), new ArrayList <Object>());

		dataset.addEntitySet(entitySet);

		final RelationshipSet relationshipSet = new RelationshipSet(RELATIONSHIP, ENTITY, ENTITY,
		    RelationshipFormat.ASYM, WeightRange.POSITIVE);

		dataset.addRelationshipSet(relationshipSet);

		final Matrix matrix = MatrixFactory.newMemoryMatrix(N, N);

		relationshipSet.setMatrix(matrix);

		for (int i = 1; i < N; ++i)
			matrix.set(i - 1, i, 1);

		final Recommender recommender = new PathRecommender();

		final RecommenderModel recommenderModel = recommender.build(dataset);

		final Iterator <Recommendation> iterator = recommenderModel.recommend(new Entity(ENTITY, 0), new EntityType[]
		{ ENTITY });

		final int M = 5;
		final List <Recommendation> recommendations = RecommendationUtils.read(iterator, M);

		assert recommendations.size() == M;

		for (int i = 0; i < M; ++i)
			assert recommendations.get(i).getEntity().getId() == i;

		for (int i = 0; i + 1 < M; ++i)
			assert recommendations.get(i).getScore() > recommendations.get(i + 1).getScore();
	}

	/**
	 * Test PathRecommender.toString().
	 */
	@Test
	public void testToString()
	{
		final PathRecommender pathRecommender = new PathRecommender();

		System.out.println(pathRecommender.toString());
	}

	/**
	 * Test weighting of paths, e.g. that it is possible to give higher weights to longer paths.
	 */
	@Test
	public void testWeightedPath()
	{
		final UnirelationalDataset dataset = new SimpleUnipartiteDataset(4);
		final Matrix matrix = dataset.getMatrix();

		matrix.set(0, 1, 1);
		matrix.set(1, 2, 1);
		matrix.set(2, 3, 1);

		final Path rel = new RelationshipPath(SimpleUnipartiteDataset.RELATIONSHIP);

		final Recommender recommender = new PathRecommender(new ParallelPath(new WeightedPath(1, rel),
		    new WeightedPath(2, new CompoundPath(rel, rel)), new WeightedPath(3, new CompoundPath(rel, rel, rel))));

		final RecommenderModel recommenderModel = recommender.build(dataset);

		final Iterator <Recommendation> iterator = recommenderModel.recommend(new Entity(
		    SimpleUnipartiteDataset.ENTITY, 0), new EntityType[]
		{ SimpleUnipartiteDataset.ENTITY });

		assert iterator.next().getEntity().getId() == 3;
		assert iterator.next().getEntity().getId() == 2;
		assert iterator.next().getEntity().getId() == 1;
	}

	/**
	 * Apply the
	 */
	@Test
	public void testLargeWeights()
	{
		final int n = 1000;

		final UnirelationalDataset dataset = new SimpleUnipartiteDataset(n, WeightRange.POSITIVE);

		final Matrix matrix = dataset.getMatrix();

		for (int i = 0; i + 1 < n; ++i)
			matrix.set(i, i + 1, 1e3);

		final Recommender recommender = new PathRecommender();

		final RecommenderModel recommenderModel = recommender.build(dataset);

		final Iterator <Recommendation> iterator = recommenderModel.recommend(new Entity(
		    SimpleUnipartiteDataset.ENTITY, 0), new EntityType[]
		{ SimpleUnipartiteDataset.ENTITY });

		final int m = 10;

		final List <Recommendation> recommendations = RecommendationUtils.read(iterator, m);

		assert recommendations.size() == m;

		for (int i = 0; i < m; ++i)
			assert recommendations.get(i).getEntity().getId() == i;

		for (int i = 0; i + 1 < m; ++i)
			assert recommendations.get(i).getScore() > recommendations.get(i + 1).getScore();
	}

	/**
	 * Check that the path recommender iterator ends at some point, even when filters are used. The underlying all-path
	 * does not end.
	 */
	@Test
	public void testEnding()
	{
		final int m = 99;
		final int n = 77;

		final UnirelationalDataset dataset = new SimpleBipartiteDataset(m, n);

		final Matrix matrix = dataset.getMatrix();

		final Random random = new Random();

		for (int i = 0; i < m; ++i)
		{
			for (int r = 0; r < 10; ++r)
			{
				final int j = random.nextInt(n);

				matrix.set(i, j, 1);
			}
		}

		final Recommender recommender = new PathRecommender();

		final RecommenderModel recommenderModel = recommender.build(dataset);

		final Map <Entity, Double> sources = new HashMap <Entity, Double>();
		sources.put(new Entity(SimpleBipartiteDataset.SUBJECT, 0), 1.);

		final RecommendationResult recommendationResult = recommenderModel.recommendExt(sources, new EntityType[]
		{ SimpleBipartiteDataset.OBJECT });

		final List <Recommendation> recommendations = new ArrayList <Recommendation>();

		while (recommendationResult.getIterator().hasNext())
		{
			recommendations.add(recommendationResult.getIterator().next());
		}

		assert recommendations.size() >= n / 2;
	}
}
