package de.dailab.recommender.graph;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.graph.datasets.Movielens100kDataset;
import de.dailab.recommender.path.PathRecommender;
import de.dailab.recommender.recommend.Recommender;
import de.dailab.recommender.recommend.RecommenderModel;
import de.dailab.recommender.recommendation.Recommendation;
import de.dailab.recommender.recommendation.RecommendationUtils;
import de.dailab.recommender.text.TextSyntaxException;

/**
 * Test the full direct recommender.
 * 
 * @author kunegis
 */
public class TestFullPathRecommender
{
	/**
	 * Test the full path recommender
	 * 
	 * @throws IOException IO error
	 * @throws TextSyntaxException syntax error
	 */
	@Test
	public void testFullPathRecommender()
	    throws IOException, TextSyntaxException
	{
		final Dataset dataset = new Movielens100kDataset();

		final Recommender recommender = new PathRecommender();

		final RecommenderModel recommenderModel = recommender.build(dataset);

		for (int id = 50; id < 56; ++id)
		{
			final Entity user = new Entity(Movielens100kDataset.USER, id);

			System.out.println(String.format("Recommendations for %s %s %s %s %s", user, dataset.getMetadata(user,
			    Movielens100kDataset.METADATA_GENDER), dataset.getMetadata(user,
			    Movielens100kDataset.METADATA_OCCUPATION), dataset.getMetadata(user,
			    Movielens100kDataset.METADATA_ZIP_CODE), dataset.getMetadata(user, Movielens100kDataset.METADATA_AGE)));

			final Iterator <Recommendation> iterator = recommenderModel.recommend(user, new EntityType[]
			{ Movielens100kDataset.MOVIE });

			final List <Recommendation> recommendations = RecommendationUtils.read(iterator, 10);

			System.out.println(RecommendationUtils.format(recommendations, dataset));
		}
	}
}
