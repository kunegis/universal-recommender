package de.dailab.recommender.graph;

import java.io.IOException;
import java.util.Iterator;

import org.junit.Test;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.UnirelationalDataset;
import de.dailab.recommender.evaluation.PredictorEvaluation;
import de.dailab.recommender.graph.unirelationaldatasets.Movielens100kRatingDataset;
import de.dailab.recommender.latent.CompoundLatentPredictor;
import de.dailab.recommender.latent.DefaultUnnormalizedLatentPredictor;
import de.dailab.recommender.latent.EigenvalueDecompositionPredictor;
import de.dailab.recommender.latent.LatentNormalizationPredictor;
import de.dailab.recommender.latent.LatentPredictor;
import de.dailab.recommender.latent.LatentRecommender;
import de.dailab.recommender.neighborhood.FullNeighborhoodFinder;
import de.dailab.recommender.predict.Predictor;
import de.dailab.recommender.predict.PredictorModel;
import de.dailab.recommender.recommend.Recommender;
import de.dailab.recommender.recommend.RecommenderModel;
import de.dailab.recommender.recommendation.Recommendation;
import de.dailab.recommender.recommendation.RecommendationUtils;
import de.dailab.recommender.text.TextSyntaxException;

/**
 * Test the compound latent predictor.
 * <p>
 * In this test, we implement a normalized eigenvalue decomposition predictor. When the chosen normalization method is
 * additive, it can be interpreted as subtraction of a low-rank predictor, which can be implemented as a latent
 * predictor. Since the QR predictor is also a latent predictor, both can be combined to a single latent predictor,
 * giving fast predictions and fast updates of the predictor model.
 * 
 * @author kunegis
 */
public class TestCompoundLatentPredictor
{
	private static final Entity USER_A = new Entity(Movielens100kRatingDataset.USER, 13);

	/**
	 * Test a compound latent predictor.
	 * 
	 * @throws IOException On IO errors
	 * @throws TextSyntaxException syntax error
	 */
	@Test
	public void testCompoundLatentPredictor()
	    throws IOException, TextSyntaxException
	{
		final Dataset dataset = new Movielens100kRatingDataset();

		final Predictor predictor = new CompoundLatentPredictor(new LatentNormalizationPredictor(),
		    new EigenvalueDecompositionPredictor());

		final PredictorModel predictorModel = predictor.build(dataset);

		for (int i = 0; i < 10; ++i)
		{
			final Entity movieA = new Entity(Movielens100kRatingDataset.MOVIE, 12 + i);

			final double prediction = predictorModel.predict(USER_A, movieA);

			System.out.printf("predict(%s, %s) = %s\n", USER_A, movieA, prediction);
		}
	}

	/**
	 * Test updating a dataset used in a compound latent predictor
	 * 
	 * @throws IOException on IO errors
	 * @throws TextSyntaxException on syntax errors
	 */
	@Test
	public void testUpdate()
	    throws IOException, TextSyntaxException
	{
		final int COUNT = 10;

		final UnirelationalDataset dataset = new Movielens100kRatingDataset();

		final LatentPredictor predictor = new CompoundLatentPredictor(new LatentNormalizationPredictor(),
		    new DefaultUnnormalizedLatentPredictor());

		final PredictorEvaluation predictorEvaluation = new PredictorEvaluation(dataset, predictor);

		System.out.println(predictorEvaluation);

		final Recommender recommender = new LatentRecommender(new FullNeighborhoodFinder(), predictor);

		final RecommenderModel recommenderModel = recommender.build(dataset);

		Iterator <Recommendation> iterator = recommenderModel.recommend(USER_A, new EntityType[]
		{ Movielens100kRatingDataset.MOVIE });

		System.out.println("Recommendations:");
		int count = COUNT;
		int negCount = COUNT / 2;
		while (iterator.hasNext() && count-- > 0)
		{
			final Recommendation recommendation = iterator.next();

			System.out.println(RecommendationUtils.format(recommendation, dataset));

			if (negCount-- > 0)
			{
				System.out.printf("...I don't like %s\n", recommendation.getEntity());
				dataset.getUniqueRelationshipSet().getMatrix().set(USER_A.getId(), recommendation.getEntity().getId(),
				    1);
			}
		}

		recommenderModel.update();

		iterator = recommenderModel.recommend(USER_A, new EntityType[]
		{ Movielens100kRatingDataset.MOVIE });

		System.out.println();
		System.out.println("Recommendations again:");
		count = COUNT;
		while (iterator.hasNext() && count-- > 0)
		{
			final Recommendation recommendation = iterator.next();

			System.out.println(RecommendationUtils.format(recommendation, dataset));

			dataset.getUniqueRelationshipSet().getMatrix().set(USER_A.getId(), recommendation.getEntity().getId(), 1);
		}

	}
}
