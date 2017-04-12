package de.dailab.recommender.curve;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.graph.unirelationaldatasets.SlashdotZooDataset;
import de.dailab.recommender.latent.EigenvalueDecompositionPredictor;
import de.dailab.recommender.latent.LatentRecommender;
import de.dailab.recommender.neighborhood.FullNeighborhoodFinder;
import de.dailab.recommender.recommend.Recommender;
import de.dailab.recommender.recommend.RecommenderModel;
import de.dailab.recommender.recommendation.Recommendation;
import de.dailab.recommender.recommendation.RecommendationUtils;
import de.dailab.recommender.similarity.Similarity;
import de.dailab.recommender.text.TextSyntaxException;

/**
 * Test learning a spectral transformation.
 * 
 * @author kunegis
 */
public class TestCurve
{
	/**
	 * Learn a spectral transformation in the Slashdot Zoo.
	 * 
	 * @throws IOException while loading the dataset
	 * @throws TextSyntaxException in the dataset
	 */
	@Test
	public void testCurve()
	    throws IOException, TextSyntaxException
	{
		final SlashdotZooDataset dataset = new SlashdotZooDataset();

		final Entity user = new Entity(dataset.getUniqueRelationshipSet().getSubject(), 12);

		final Similarity similarity = Curve.learnEigenvalueSimilarity(dataset, dataset.getUniqueRelationshipType(), 9);

		System.out.println(similarity);

		final Recommender recommender = new LatentRecommender(new FullNeighborhoodFinder(),
		    new EigenvalueDecompositionPredictor(similarity));

		final RecommenderModel recommenderModel = recommender.build(dataset);

		final Iterator <Recommendation> iterator = recommenderModel.recommend(user, new EntityType[]
		{ dataset.getUniqueRelationshipSet().getObject() });

		final List <Recommendation> recommendations = RecommendationUtils.read(iterator, 20);

		System.out.println(RecommendationUtils.format(recommendations));
	}
}
