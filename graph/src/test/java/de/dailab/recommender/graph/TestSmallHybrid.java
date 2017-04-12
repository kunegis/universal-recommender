package de.dailab.recommender.graph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import de.dailab.recommender.constraint.Constraint;
import de.dailab.recommender.constraint.ConstraintRecommender;
import de.dailab.recommender.constraint.PresentConstraintRecommender;
import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.RelationshipType;
import de.dailab.recommender.graph.datasets.SmallHybridDataset;
import de.dailab.recommender.latent.LatentRecommender;
import de.dailab.recommender.path.CompoundPath;
import de.dailab.recommender.path.PathRecommender;
import de.dailab.recommender.path.RelationshipPath;
import de.dailab.recommender.recommend.PartialRecommender;
import de.dailab.recommender.recommend.Recommender;
import de.dailab.recommender.recommend.RecommenderModel;
import de.dailab.recommender.recommendation.Recommendation;
import de.dailab.recommender.recommendation.RecommendationUtils;
import de.dailab.recommender.text.TextSyntaxException;

/**
 * Test the dataset "small-hybrid" containing users, movies and genres, buddies and favorites. This dataset is small,
 * and recommendations can be explained on a simple ER-graph.
 * 
 * @author kunegis
 */
public class TestSmallHybrid
{
	private final Dataset dataset;

	private final Entity alice, bob, gus;
	private final Entity dirtyHarry, dieHard, killBill, theGodfather, dasBoot, citizenKane;

	/**
	 * Test the synthetic small-hybrid dataset.
	 * 
	 * @throws IOException IO error
	 * @throws TextSyntaxException Syntax error
	 */
	public TestSmallHybrid()
	    throws IOException, TextSyntaxException
	{
		dataset = new SmallHybridDataset();

		alice = new Entity(SmallHybridDataset.USER, 0);
		bob = new Entity(SmallHybridDataset.USER, 1);
		gus = new Entity(SmallHybridDataset.USER, 6);

		dirtyHarry = new Entity(SmallHybridDataset.MOVIE, 0);
		dieHard = new Entity(SmallHybridDataset.MOVIE, 1);
		killBill = new Entity(SmallHybridDataset.MOVIE, 2);
		theGodfather = new Entity(SmallHybridDataset.MOVIE, 4);
		dasBoot = new Entity(SmallHybridDataset.MOVIE, 5);
		citizenKane = new Entity(SmallHybridDataset.MOVIE, 6);
	}

	/**
	 * Test the default recommender.
	 */
	@Test
	public void testDefault()
	{
		final Recommender recommender = new LatentRecommender();

		final RecommenderModel recommenderModel = recommender.build(dataset);

		final Iterator <Recommendation> iterator = recommenderModel.recommend(alice, new EntityType[]
		{ SmallHybridDataset.MOVIE });

		final List <Recommendation> recommendations = RecommendationUtils.read(iterator, 2);

		System.out.println(RecommendationUtils.format(recommendations, dataset));
	}

	/**
	 * Test the path recommenders.
	 */
	@Test
	public void testPath()
	{
		final Recommender recommender = new PathRecommender(new CompoundPath(new RelationshipPath(
		    SmallHybridDataset.BUDDY), new RelationshipPath(SmallHybridDataset.FAVORITE)));

		final RecommenderModel recommenderModel = recommender.build(dataset);

		final Iterator <Recommendation> iterator = recommenderModel.recommend(bob, new EntityType[]
		{ SmallHybridDataset.MOVIE });

		final List <Recommendation> recommendations = RecommendationUtils.read(iterator, 2);

		System.out.println(RecommendationUtils.format(recommendations, dataset));

		final Entity e0 = recommendations.get(0).getEntity();
		final Entity e1 = recommendations.get(1).getEntity();

		assert (e0.equals(theGodfather) && e1.equals(dasBoot)) || (e0.equals(dasBoot) && e1.equals(theGodfather));

	}

	/**
	 * Test the neighborhood recommender.
	 */
	@Test
	public void testNeighborhood()
	{
		final Constraint constraint = new Constraint()
		{
			@Override
			public boolean accept(Recommendation recommendation)
			{
				return !recommendation.getEntity().equals(citizenKane);
			}
		};

		final Recommender recommender = new ConstraintRecommender(constraint, new PresentConstraintRecommender(
		    new RelationshipType[]
		    { SmallHybridDataset.FAVORITE }, new LatentRecommender()));

		final RecommenderModel recommenderModel = recommender.build(dataset);

		final Iterator <Recommendation> iterator = recommenderModel.recommend(alice, new EntityType[]
		{ SmallHybridDataset.MOVIE });

		final List <Recommendation> recommendations = RecommendationUtils.read(iterator, 3);

		assert recommendations.size() > 0;

		for (final Recommendation recommendation: recommendations)
		{
			assert !recommendation.getEntity().equals(dirtyHarry);
			assert !recommendation.getEntity().equals(dieHard);
			assert !recommendation.getEntity().equals(citizenKane);
		}

		System.out.println(RecommendationUtils.format(recommendations, dataset));
	}

	/**
	 * Test the continuous neighborhood recommender.
	 */
	@Test
	public void testNeighborhoodContinuous()
	{
		final Constraint notCitizenKane = new Constraint()
		{
			@Override
			public boolean accept(Recommendation recommendation)
			{
				return !recommendation.getEntity().equals(citizenKane);
			}

			@Override
			public String toString()
			{
				return "NotCitizenKane";
			}
		};

		final Recommender recommender = new ConstraintRecommender(notCitizenKane, new PresentConstraintRecommender(
		    new RelationshipType[]
		    { SmallHybridDataset.FAVORITE }, new LatentRecommender()));

		final RecommenderModel recommenderModel = recommender.build(dataset);

		final Iterator <Recommendation> i = recommenderModel.recommend(alice, new EntityType[]
		{ SmallHybridDataset.MOVIE });

		final List <Recommendation> recommendations = new ArrayList <Recommendation>();

		while (i.hasNext())
		{
			final Recommendation recommendation = i.next();
			recommendations.add(recommendation);
			assert !Double.isNaN(recommendation.getScore()) && !Double.isInfinite(recommendation.getScore());
			assert !recommendation.getEntity().equals(dirtyHarry);
			assert !recommendation.getEntity().equals(dieHard);
			assert !recommendation.getEntity().equals(citizenKane);
		}

		assert recommendations.size() >= 3;

		System.out.println(RecommendationUtils.format(recommendations));
	}

	/**
	 * Test the complex path recommender, i.e. following relationships backwards, etc.
	 */
	@Test
	public void testComplexPath()
	{
		final Recommender recommender = new PresentConstraintRecommender(new RelationshipType[]
		{ SmallHybridDataset.FAVORITE }, new PathRecommender(new CompoundPath(new RelationshipPath(
		    SmallHybridDataset.FAVORITE), new RelationshipPath(SmallHybridDataset.FAVORITE, true),
		    new RelationshipPath(SmallHybridDataset.FAVORITE))));

		final RecommenderModel recommenderModel = recommender.build(dataset);

		final Iterator <Recommendation> iterator = recommenderModel.recommend(alice, new EntityType[]
		{ SmallHybridDataset.MOVIE });

		final List <Recommendation> recommendations = RecommendationUtils.read(iterator, 2);

		System.out.println(RecommendationUtils.format(recommendations, dataset));

		assert recommendations.size() == 1;
		assert recommendations.get(0).getEntity().equals(killBill);
	}

	/**
	 * Test direct recommender followed by neighborhood recommender.
	 */
	@Test
	public void testPathNeighborhood()
	{
		final Recommender recommender = new PresentConstraintRecommender(new RelationshipType[]
		{ SmallHybridDataset.FAVORITE }, new PathRecommender(new RelationshipPath(SmallHybridDataset.FAVORITE),
		    new PartialRecommender(new RelationshipType[]
		    { SmallHybridDataset.MOVIEGENRE }, new LatentRecommender())));

		final RecommenderModel recommenderModel = recommender.build(dataset);

		final Iterator <Recommendation> i = recommenderModel.recommend(gus, new EntityType[]
		{ SmallHybridDataset.MOVIE });

		final List <Recommendation> recommendations = new LinkedList <Recommendation>();
		while (i.hasNext())
		{
			final Recommendation recommendation = i.next();
			recommendations.add(recommendation);
		}
		System.out.println(RecommendationUtils.format(recommendations, dataset));
	}
}
