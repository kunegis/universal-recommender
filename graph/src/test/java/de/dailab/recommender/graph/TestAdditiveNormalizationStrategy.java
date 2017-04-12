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
import de.dailab.recommender.normalize.StaticAdditiveNormalizationStrategy;
import de.dailab.recommender.recommend.Recommender;
import de.dailab.recommender.recommend.RecommenderModel;
import de.dailab.recommender.recommendation.Recommendation;
import de.dailab.recommender.recommendation.RecommendationUtils;
import de.dailab.recommender.text.TextSyntaxException;

/**
 * Test the additive normalization strategy on a graph store dataset.
 * 
 * @author kunegis
 */
public class TestAdditiveNormalizationStrategy
{
	/**
	 * Test the static additive normalization strategy on a single dataset.
	 * 
	 * @throws IOException IO error
	 * @throws TextSyntaxException Syntax error
	 */
	@Test
	public void testStaticAdditiveNormalizationStrategy()
	    throws IOException, TextSyntaxException
	{
		final Dataset dataset = new Movielens100kDataset();

		final Recommender recommender = new LatentRecommender(new LatentNormalizationPredictor(
		    new StaticAdditiveNormalizationStrategy()));

		final RecommenderModel recommenderModel = recommender.build(dataset);

		for (int id = 34; id < 49; ++id)
		{
			final Entity user = new Entity(Movielens100kDataset.USER, id);

			System.out.printf("Recommendations for %s (%syo %s %s po%s):\n", user, dataset.getMetadata(user,
			    Movielens100kDataset.METADATA_AGE), dataset.getMetadata(user, Movielens100kDataset.METADATA_GENDER),
			    dataset.getMetadata(user, Movielens100kDataset.METADATA_OCCUPATION), dataset.getMetadata(user,
			        Movielens100kDataset.METADATA_ZIP_CODE));

			final Iterator <Recommendation> iterator = recommenderModel.recommend(user, new EntityType[]
			{ Movielens100kDataset.MOVIE });

			final List <Recommendation> recommendations = RecommendationUtils.read(iterator, 19);

			System.out.println(RecommendationUtils.format(recommendations, dataset));
		}
	}
}
