package de.dailab.recommender.graph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.evaluation.PredictorEvaluation;
import de.dailab.recommender.evaluation.RecommenderEvaluation;
import de.dailab.recommender.evaluation.RecommenderSplitType;
import de.dailab.recommender.evaluation.Split;
import de.dailab.recommender.graph.datasets.Movielens100kDataset;
import de.dailab.recommender.laplacian.LaplacianPredictor;
import de.dailab.recommender.latent.LatentRecommender;
import de.dailab.recommender.neighborhood.EuclideanNeighborhoodFinder;
import de.dailab.recommender.predict.Predictor;
import de.dailab.recommender.recommend.Recommender;
import de.dailab.recommender.text.TextSyntaxException;

/**
 * Test the Laplacian predictor.
 * 
 * @author kunegis
 */
public class TestLaplacian
{
	/**
	 * Test prediction.
	 * 
	 * @throws IOException IO errors
	 * @throws TextSyntaxException Syntax errors
	 */
	@Test
	public void testPredictor()
	    throws IOException, TextSyntaxException
	{
		final Dataset dataset = new Movielens100kDataset();

		final Split split = new Split(dataset, Movielens100kDataset.RATING);

		final Predictor predictor = new LaplacianPredictor();

		final List <Predictor> predictors = new ArrayList <Predictor>();
		predictors.add(predictor);

		final PredictorEvaluation evaluation = new PredictorEvaluation(split, predictors);

		System.out.println(evaluation);
	}

	/**
	 * Test recommendation.
	 * 
	 * @throws TextSyntaxException on syntax errors
	 * @throws IOException on IO exceptions
	 */
	public void testRecommender()
	    throws IOException, TextSyntaxException
	{
		// XXX doesn't work at the moment because EuclideanNeighborhoodFinder isn't implemented.

		final Dataset dataset = new Movielens100kDataset();

		final Split split = new Split(dataset, Movielens100kDataset.RATING, new RecommenderSplitType());

		final List <Recommender> recommenders = new ArrayList <Recommender>(RecommenderEvaluation.RECOMMENDERS_DEFAULT
		    .getRecommenders());
// recommenders.add(new FullRecommender(new LaplacianPredictor()));
		recommenders.add(new LatentRecommender(new EuclideanNeighborhoodFinder(), new LaplacianPredictor()));

		final RecommenderEvaluation evaluation = new RecommenderEvaluation(split, recommenders);

		System.out.println(evaluation);
	}
}
