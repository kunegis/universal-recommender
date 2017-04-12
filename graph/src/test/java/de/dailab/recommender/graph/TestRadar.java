package de.dailab.recommender.graph;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.graph.datasets.Movielens100kDataset;
import de.dailab.recommender.latent.DefaultUnnormalizedLatentPredictor;
import de.dailab.recommender.latent.LatentRecommender;
import de.dailab.recommender.latent.LatentRecommenderModel;
import de.dailab.recommender.radar.Radar;
import de.dailab.recommender.recommendation.Recommendation;
import de.dailab.recommender.recommendation.RecommendationUtils;
import de.dailab.recommender.text.TextSyntaxException;

/**
 * Test the radar on a Graph Store dataset.
 * 
 * @author kunegis
 */
public class TestRadar
{
	/**
	 * Test computing a radar from a small dataset.
	 * 
	 * @throws IOException on IO errors
	 * @throws TextSyntaxException on syntax errors
	 */
	@Test
	public void testRadar()
	    throws IOException, TextSyntaxException
	{
		final Dataset dataset = new Movielens100kDataset();

		final LatentRecommender latentRecommender = new LatentRecommender(new DefaultUnnormalizedLatentPredictor());

		final LatentRecommenderModel latentRecommenderModel = latentRecommender.build(dataset, false);

		final Entity entity = new Entity(Movielens100kDataset.USER, 2);

		final Iterator <Recommendation> iterator = latentRecommenderModel.recommend(entity, new EntityType[]
		{ Movielens100kDataset.MOVIE, Movielens100kDataset.USER });

		final List <Recommendation> recommendations = RecommendationUtils.read(iterator, 10);

		System.out.println(RecommendationUtils.format(recommendations, dataset));

		final Entity entities[] = new Entity[1 + recommendations.size()];
		entities[0] = entity;
		for (int i = 0; i < recommendations.size(); ++i)
			entities[1 + i] = recommendations.get(i).getEntity();

		final Radar radar = new Radar(entities, dataset, latentRecommenderModel.getLatentPredictorModel(),
		    latentRecommenderModel);

		System.out.println(radar);
	}
}
