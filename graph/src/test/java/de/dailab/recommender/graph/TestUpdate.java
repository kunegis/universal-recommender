package de.dailab.recommender.graph;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.graph.datasets.Movielens100kDataset;
import de.dailab.recommender.latent.LatentRecommender;
import de.dailab.recommender.recommend.Recommender;
import de.dailab.recommender.recommend.RecommenderModel;
import de.dailab.recommender.recommendation.Recommendation;
import de.dailab.recommender.recommendation.RecommendationUtils;
import de.dailab.recommender.text.TextSyntaxException;

/**
 * Test online update of recommenders.
 * 
 * @author kunegis
 */
public class TestUpdate
{
	private final Entity USER_A = new Entity(Movielens100kDataset.USER, 24);

	/**
	 * Test updates of recommender models.
	 * 
	 * @throws IOException On IO errors
	 * @throws TextSyntaxException syntax error
	 */
	@Test
	public void testUpdate()
	    throws IOException, TextSyntaxException
	{
		/*
		 * Load dataset
		 */
		final Dataset dataset = new Movielens100kDataset();

		/*
		 * Build recommender model
		 */
		final Recommender recommender = new LatentRecommender();

		System.out.printf("Building %s...\n", recommender);

		final RecommenderModel recommenderModel = recommender.build(dataset);

		System.out.println();

		/*
		 * Compute recommendations for USER_A
		 */
		Iterator <Recommendation> iterator = recommenderModel.recommend(USER_A, new EntityType[]
		{ Movielens100kDataset.MOVIE });

		List <Recommendation> recommendations = RecommendationUtils.read(iterator, 5);

		System.out.println(RecommendationUtils.format(recommendations, dataset));

		assert recommendations.size() >= 2;

		/*
		 * Give a negative rating to the first two recommendations
		 */
		dataset.getRelationshipSet(Movielens100kDataset.RATING).getMatrix().set(USER_A.getId(),
		    recommendations.get(0).getEntity().getId(), 1);
		dataset.getRelationshipSet(Movielens100kDataset.RATING).getMatrix().set(USER_A.getId(),
		    recommendations.get(1).getEntity().getId(), 1);

		/*
		 * Update the model with the two new ratings
		 */
		recommenderModel.update();

		/*
		 * Re-compute recommendations for USER_A
		 */
		iterator = recommenderModel.recommend(USER_A, new EntityType[]
		{ Movielens100kDataset.MOVIE });

		recommendations = RecommendationUtils.read(iterator, 5);

		System.out.println(RecommendationUtils.format(recommendations, dataset));
	}
}
