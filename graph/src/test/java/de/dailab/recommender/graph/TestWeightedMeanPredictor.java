package de.dailab.recommender.graph;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.graph.datasets.Movielens100kDataset;
import de.dailab.recommender.latent.DefaultLatentPredictor;
import de.dailab.recommender.predict.WeightedMeanPredictor;
import de.dailab.recommender.recommend.FullRecommender;
import de.dailab.recommender.recommend.Recommender;
import de.dailab.recommender.recommend.RecommenderModel;
import de.dailab.recommender.recommendation.Recommendation;
import de.dailab.recommender.recommendation.RecommendationUtils;
import de.dailab.recommender.text.TextSyntaxException;

/**
 * Test the weighted mean predictor.
 * 
 * @author kunegis
 */
public class TestWeightedMeanPredictor
{
	/**
	 * Test the weighted mean predictor.
	 * 
	 * @throws TextSyntaxException error in the data files
	 * @throws IOException read error
	 */
	@Test
	public void testWeightedMeanPredictor()
	    throws TextSyntaxException, IOException
	{
		final Dataset dataset = new Movielens100kDataset();

		final Recommender recommender = new FullRecommender(new WeightedMeanPredictor(new DefaultLatentPredictor()));

		System.out.printf("Building %s...\n", recommender);

		final RecommenderModel recommenderModel = recommender.build(dataset);

		for (int id = 34; id < 49; ++id)
		{
			final Entity user = new Entity(Movielens100kDataset.USER, id);

			System.out.printf("Recommendations for %s (%s):\n", user, Arrays.toString(dataset.getAllMetadata(user)));

			final Iterator <Recommendation> iterator = recommenderModel.recommend(user, new EntityType[]
			{ Movielens100kDataset.MOVIE });

			final List <Recommendation> recommendations = RecommendationUtils.read(iterator, 19);

			System.out.println(RecommendationUtils.format(recommendations, dataset));
		}
	}
}
