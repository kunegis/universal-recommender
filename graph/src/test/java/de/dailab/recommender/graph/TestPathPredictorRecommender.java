package de.dailab.recommender.graph;

import java.io.IOException;

import org.junit.Test;

import de.dailab.recommender.dataset.UnirelationalDataset;
import de.dailab.recommender.evaluation.RecommenderEvaluation;
import de.dailab.recommender.graph.unirelationaldatasets.Movielens100kRatingDataset;
import de.dailab.recommender.latent.DefaultLatentPredictor;
import de.dailab.recommender.latent.LatentRecommender;
import de.dailab.recommender.neighborhood.ClusteringNeighborhoodFinder;
import de.dailab.recommender.neighborhood.FullNeighborhoodFinder;
import de.dailab.recommender.recommend.PathPredictorRecommender;
import de.dailab.recommender.recommend.Recommender;
import de.dailab.recommender.text.TextSyntaxException;

/**
 * Test the path neighborhood finder.
 * 
 * @author kunegis
 */
public class TestPathPredictorRecommender
{
	/**
	 * Test the path predictor recommender.
	 * 
	 * @throws IOException on IO errors
	 * @throws TextSyntaxException on syntax errors
	 */
	@Test
	public void testPathPredictorRecommender()
	    throws IOException, TextSyntaxException
	{
		final UnirelationalDataset dataset = new Movielens100kRatingDataset();

		final Recommender pathPredictorRecommender = new PathPredictorRecommender(new DefaultLatentPredictor());
		final Recommender fullRecommender = new LatentRecommender(new FullNeighborhoodFinder(),
		    new DefaultLatentPredictor());
		final Recommender clusteringRecommender = new LatentRecommender(new ClusteringNeighborhoodFinder(),
		    new DefaultLatentPredictor());

		final RecommenderEvaluation recommenderEvaluation = new RecommenderEvaluation(dataset,
		    pathPredictorRecommender, fullRecommender, clusteringRecommender);

		System.out.println(recommenderEvaluation);
	}
}
