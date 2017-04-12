package de.dailab.recommender.graph;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.graph.datasets.Movielens100kDataset;
import de.dailab.recommender.latent.LatentNormalizationPredictor;
import de.dailab.recommender.latent.LatentRecommender;
import de.dailab.recommender.recommend.Recommender;
import de.dailab.recommender.recommend.RecommenderModel;
import de.dailab.recommender.recommendation.Recommendation;
import de.dailab.recommender.recommendation.RecommendationUtils;
import de.dailab.recommender.text.TextSyntaxException;

/**
 * Test the latent normalizer.
 * 
 * @author kunegis
 */
public class TestLatentNormalizer
{
	/**
	 * Test the latent normalizer.
	 * 
	 * @throws IOException IO error
	 * @throws TextSyntaxException Syntax error
	 */
	@Test
	public void testLatentNormalizer()
	    throws IOException, TextSyntaxException
	{
		final Dataset dataset = new Movielens100kDataset();

		final Recommender recommender = new LatentRecommender(new LatentNormalizationPredictor());

		final RecommenderModel recommenderModel = recommender.build(dataset);

		for (int id = 3; id < 12; ++id)
		{
			final Entity user = new Entity(Movielens100kDataset.USER, id);

			final Iterator <Recommendation> iterator = recommenderModel.recommend(user, new EntityType[]
			{ Movielens100kDataset.MOVIE });

			final List <Recommendation> recommendations = RecommendationUtils.read(iterator, 9);

			System.out.println(RecommendationUtils.format(recommendations, dataset));
		}
	}
}
