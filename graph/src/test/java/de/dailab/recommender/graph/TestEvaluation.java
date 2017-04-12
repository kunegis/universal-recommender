package de.dailab.recommender.graph;

import org.junit.Test;

import de.dailab.recommender.graph.datasets.Movielens100kDataset;
import de.dailab.recommender.graph.unirelationaldatasets.DblpCiteDataset;
import de.dailab.recommender.graph.unirelationaldatasets.EpinionsDataset;
import de.dailab.recommender.graph.unirelationaldatasets.InternetGrowthDataset;
import de.dailab.recommender.graph.unirelationaldatasets.Movielens100kRatingDataset;
import de.dailab.recommender.graph.unirelationaldatasets.WikiEditFrwikibooksDataset;

/**
 * Test evaluation on small Graph Store datasets.
 * 
 * @author kunegis
 */
public class TestEvaluation
{
	/**
	 * Test prediction on small unirelational Graph Store datasets.
	 * 
	 * @throws InstantiationException On loading a dataset
	 * @throws IllegalAccessException On loading a dataset
	 */
	@Test
	public void testUnirelationalPredictor()
	    throws InstantiationException, IllegalAccessException
	{
		Evaluate.evaluateUnirelationalPredictor(Movielens100kRatingDataset.class);
		Evaluate.evaluateUnirelationalPredictor(WikiEditFrwikibooksDataset.class);
		Evaluate.evaluateUnirelationalPredictor(InternetGrowthDataset.class);
		Evaluate.evaluateUnirelationalPredictor(EpinionsDataset.class);
	}

	/**
	 * Evaluate recommendation on unirelational Graph Store datasets.
	 * 
	 * @throws InstantiationException On loading a dataset
	 * @throws IllegalAccessException On loading a dataset
	 */
	@Test
	public void testUnirelationalRecommender()
	    throws InstantiationException, IllegalAccessException
	{
		Evaluate.evaluateUnirelationalRecommender(Movielens100kRatingDataset.class);
		Evaluate.evaluateUnirelationalRecommender(DblpCiteDataset.class);
	}

	/**
	 * Evaluate prediction on small Graph Store datasets.
	 * 
	 * @throws InstantiationException On loading a dataset
	 * @throws IllegalAccessException On loading a dataset
	 */
	@Test
	public void testPredictor()
	    throws InstantiationException, IllegalAccessException
	{
		Evaluate.evaluatePredictor(Movielens100kDataset.class);
	}

	/**
	 * Evaluate recommendation on small Graph Store datasets.
	 * 
	 * @throws InstantiationException On loading a dataset
	 * @throws IllegalAccessException On loading a dataset
	 */
	@Test
	public void testRecommender()
	    throws InstantiationException, IllegalAccessException
	{
		Evaluate.evaluateRecommender(Movielens100kDataset.class);
	}
}
