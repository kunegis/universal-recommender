package de.dailab.recommender.pia;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.db.DbDataset;
import de.dailab.recommender.db.ModelException;
import de.dailab.recommender.latent.LatentRecommender;
import de.dailab.recommender.recommend.Recommender;
import de.dailab.recommender.recommend.RecommenderModel;
import de.dailab.recommender.recommendation.Recommendation;
import de.dailab.recommender.recommendation.RecommendationUtils;

/**
 * Test the PIA recommender.
 * 
 * @author kunegis
 */
public class TestPia
{

	private final static String DB_URL = "jdbc:mysql://tyr:3306/sepia_development";
	private final static String DB_USERNAME = "PIA";
	private final static String DB_PASSWORD = "dPP88is";

	private final static long USER_JEROME = 842531;

	/**
	 * Test the PIA recommender.
	 * 
	 * @throws SQLException database error
	 * @throws ModelException model error
	 */
	@Test
	public void testPiaRecommender()
	    throws SQLException, ModelException
	{
		final Connection connection = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
		connection.setReadOnly(true);
		final DbDataset dataset = new PiaDataset(connection);
		connection.close();

		final Recommender recommender = new LatentRecommender();

		final RecommenderModel recommenderModel = recommender.build(dataset);

		final Entity jerome = dataset.getEntityByObject(PiaDataset.ENTITY_TYPE_USER, USER_JEROME);

		final Iterator <Recommendation> iterator = recommenderModel.recommend(jerome, new EntityType[]
		{ PiaDataset.ENTITY_TYPE_ENTITY });

		final List <Recommendation> recommendations = RecommendationUtils.read(iterator, 9);

		System.out.println(RecommendationUtils.format(recommendations, dataset));
	}
}
