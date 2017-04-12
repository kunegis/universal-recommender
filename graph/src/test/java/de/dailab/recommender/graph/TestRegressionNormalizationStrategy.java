package de.dailab.recommender.graph;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import de.dailab.recommender.constraint.NoSelfRecommender;
import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.graph.datasets.Movielens100kDataset;
import de.dailab.recommender.latent.EigenvalueDecompositionPredictor;
import de.dailab.recommender.predict.NormalizedPredictor;
import de.dailab.recommender.recommend.FullRecommender;
import de.dailab.recommender.recommend.Recommender;
import de.dailab.recommender.recommend.RecommenderModel;
import de.dailab.recommender.recommendation.Recommendation;
import de.dailab.recommender.recommendation.RecommendationUtils;
import de.dailab.recommender.regressionnormalization.RegressionNormalizationStrategy;
import de.dailab.recommender.text.TextSyntaxException;

/**
 * Test the regression normalization strategy.
 * 
 * @author kunegis
 */
public class TestRegressionNormalizationStrategy
{
	/**
	 * Test the regression normalization strategy.
	 * 
	 * @throws TextSyntaxException syntax error in the graph file
	 * @throws IOException IO error
	 */
	@Test
	public void test()
	    throws TextSyntaxException, IOException
	{
		final Dataset dataset = new Movielens100kDataset();

		final Recommender recommender = new NoSelfRecommender(new FullRecommender(new NormalizedPredictor(
		    new RegressionNormalizationStrategy(), new EigenvalueDecompositionPredictor())));

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
