package de.dailab.recommender.graph.datasets;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.latent.EigenvalueDecompositionPredictor;
import de.dailab.recommender.predict.RelationshipTypePonderation;
import de.dailab.recommender.recommend.FullRecommender;
import de.dailab.recommender.recommend.Recommender;
import de.dailab.recommender.recommend.RecommenderModel;
import de.dailab.recommender.recommendation.Recommendation;
import de.dailab.recommender.recommendation.RecommendationUtils;
import de.dailab.recommender.text.TextSyntaxException;

/**
 * Test the Gama dataset.
 * 
 * @author kunegis
 */
public class TestGama
{
	/**
	 * Test the Gama dataset.
	 * 
	 * @throws IOException on IO errors
	 * @throws TextSyntaxException on syntax errors
	 */
	@Test
	public void testGama()
	    throws IOException, TextSyntaxException
	{
		final Dataset dataset = new GamaDataset();

		final RelationshipTypePonderation ponderation = new RelationshipTypePonderation();
		ponderation.putWeight(GamaDataset.POS, +1);
		ponderation.putWeight(GamaDataset.NEG, -1);

		final Entity tribe = new Entity(GamaDataset.TRIBE, 7);

		for (final Recommender recommender: new Recommender[]
		{ new FullRecommender(new EigenvalueDecompositionPredictor(ponderation))
		/*
		 * , new FullRecommender(new LaplacianPredictor(ponderation))
		 */})
		{
			final RecommenderModel recommenderModel = recommender.build(dataset);

			final Iterator <Recommendation> iterator = recommenderModel.recommend(tribe, new EntityType[]
			{ GamaDataset.TRIBE });
			final List <Recommendation> recommendations = RecommendationUtils.read(iterator, 5);

			final String tribeName = (String) dataset.getEntitySet(GamaDataset.TRIBE).getMetadata(tribe.getId(),
			    GamaDataset.METADATA_NAME);

			System.out.printf("Friendly tribes of %s using %s:\n%s\n", tribeName, recommender, RecommendationUtils
			    .format(recommendations, dataset));
		}
	}
}
