package de.dailab.recommender.graph;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.RelationshipSet;
import de.dailab.recommender.dataset.UnirelationalDataset;
import de.dailab.recommender.dataset.WeightRange;
import de.dailab.recommender.evaluation.PredictorEvaluation;
import de.dailab.recommender.evaluation.PredictorSplitType;
import de.dailab.recommender.evaluation.RecommenderEvaluation;
import de.dailab.recommender.evaluation.RecommenderSplitType;
import de.dailab.recommender.evaluation.Split;
import de.dailab.recommender.graph.datasets.Movielens100kDataset;
import de.dailab.recommender.graph.unirelationaldatasets.AdvogatoDataset;
import de.dailab.recommender.graph.unirelationaldatasets.DblpCiteDataset;
import de.dailab.recommender.graph.unirelationaldatasets.Movielens100kRatingDataset;

/**
 * Test all recommenders and predictors with all small enough semantic and unirelational Graph Store datasets.
 * <p>
 * This class logs to stderr.
 * 
 * @author kunegis
 */
public class Evaluate
{
	/**
	 * Test recommendation on all semantic datasets.
	 * 
	 * @throws InstantiationException During loading
	 * @throws IllegalAccessException During loading
	 */
	public static void evaluateRecommenders()
	    throws InstantiationException, IllegalAccessException
	{
		for (final Class <? extends GraphStoreDataset> datasetClass: DATASETS)
		{
			evaluateRecommender(datasetClass);
		}
	}

	/**
	 * Evaluate one dataset for recommendation.
	 * 
	 * @param datasetClass The dataset to evaluate
	 * @throws InstantiationException When the dataset cannot be loaded
	 * @throws IllegalAccessException When the dataset cannot be loaded
	 */
	public static void evaluateRecommender(final Class <? extends GraphStoreDataset> datasetClass)
	    throws InstantiationException, IllegalAccessException
	{
		System.out.printf("Loading %s...\n", datasetClass.getSimpleName());

		final Dataset dataset = datasetClass.newInstance();

		System.out.println("  loaded");

		RelationshipSet relationshipSet = null;
		int size = 0;
		for (final RelationshipSet i: dataset.getRelationshipSets())
		{
			final int newSize = i.getMatrix().nnz();
			if (newSize > size)
			{
				size = newSize;
				relationshipSet = i;
			}
		}
		if (relationshipSet == null)
		{
			/* Dataset has no biggest relationship set */
			assert false;
		}

		System.out.printf("\tRecommending relationship type %s\n", relationshipSet.getType());

		final Split split = new Split(dataset, relationshipSet.getType(), new RecommenderSplitType());

		final RecommenderEvaluation evaluation = new RecommenderEvaluation(split, logger);

		System.out.println(evaluation);
	}

	/**
	 * Test prediction on all semantic datasets that have weighted edges.
	 * 
	 * @throws InstantiationException During loading
	 * @throws IllegalAccessException During loading
	 */
	public static void evaluatePredictors()
	    throws InstantiationException, IllegalAccessException
	{
		for (final Class <? extends GraphStoreDataset> datasetClass: DATASETS)
		{
			evaluatePredictor(datasetClass);
		}
	}

	/**
	 * Evaluate prediction on a dataset.
	 * 
	 * @param datasetClass The dataset on which to evaluate
	 * @throws InstantiationException When the dataset cannot be loaded
	 * @throws IllegalAccessException When the dataset cannot be loaded
	 */
	public static void evaluatePredictor(Class <? extends GraphStoreDataset> datasetClass)
	    throws InstantiationException, IllegalAccessException
	{
		System.out.printf("Loading %s...\n", datasetClass.getSimpleName());

		final Dataset dataset = datasetClass.newInstance();

		System.out.println("  loaded");

		RelationshipSet relationshipSet = null;
		int size = 0;
		for (final RelationshipSet i: dataset.getRelationshipSets())
		{
			if (i.getWeightRange() != WeightRange.UNWEIGHTED)
			{
				final int newSize = i.getMatrix().nnz();
				if (newSize > size)
				{
					size = newSize;
					relationshipSet = i;
				}
			}
		}
		if (relationshipSet == null)
		{
			System.out.println("\tSkipping unweighted dataset");
			return;
		}

		System.out.printf("\tpredicting relationship type %s\n", relationshipSet.getType());

		final Split split = new Split(dataset, relationshipSet.getType(), new PredictorSplitType());

		final PredictorEvaluation evaluation = new PredictorEvaluation(split, logger);

		System.out.println(evaluation);
	}

	/**
	 * Test recommendation in all unirelational Graph Store datasets.
	 * 
	 * @throws InstantiationException during loading
	 * @throws IllegalAccessException during loading
	 */
	public static void evaluateUnirelationalRecommenders()
	    throws InstantiationException, IllegalAccessException
	{
		for (final Class <? extends UnirelationalGraphStoreDataset> datasetClass: DATASETS_UNIRELATIONAL)
		{
			evaluateUnirelationalRecommender(datasetClass);
		}
	}

	/**
	 * Evaluate recommendation on a unirelational Graph Store dataset.
	 * 
	 * @param datasetClass The dataset to evaluate
	 * @throws InstantiationException When the dataset cannot be loaded
	 * @throws IllegalAccessException When the dataset cannot be loaded
	 */
	public static void evaluateUnirelationalRecommender(
	    final Class <? extends UnirelationalGraphStoreDataset> datasetClass)
	    throws InstantiationException, IllegalAccessException
	{
		System.out.printf("Loading %s...\n", datasetClass.getSimpleName());

		final UnirelationalDataset dataset = datasetClass.newInstance();

		System.out.println("  loaded");

		final Split split = new Split(dataset, dataset.getUniqueRelationshipType(), new RecommenderSplitType());

		final RecommenderEvaluation evaluation = new RecommenderEvaluation(split, logger);

		System.out.println(evaluation);
	}

	/**
	 * Evaluate predictors on unirelational Graph Store datasets.
	 * 
	 * @throws InstantiationException During loading
	 * @throws IllegalAccessException During loading
	 */
	public static void evaluateUnirelationalPredictors()
	    throws InstantiationException, IllegalAccessException
	{
		for (final Class <? extends UnirelationalGraphStoreDataset> datasetClass: DATASETS_UNIRELATIONAL)
		{
			evaluateUnirelationalPredictor(datasetClass);
		}
	}

	/**
	 * Evaluate prediction on a unirelational dataset.
	 * 
	 * @param datasetClass The dataset which is evaluated
	 * @throws InstantiationException When the dataset cannot be loaded
	 * @throws IllegalAccessException When the dataset cannot be loaded
	 */
	public static void evaluateUnirelationalPredictor(Class <? extends UnirelationalGraphStoreDataset> datasetClass)
	    throws InstantiationException, IllegalAccessException
	{
		System.out.printf("Loading %s...\n", datasetClass.getSimpleName());

		final UnirelationalDataset dataset = datasetClass.newInstance();

		System.out.println("  loaded");

// /* Cannot predict on unweighted datasets */
// if (dataset.getRelationshipSet(dataset.getUniqueRelationshipType()).getWeightRange() == WeightRange.UNWEIGHTED)
// {
// System.out.println("\tSkipping unweighted dataset");
// return;
// }

		final Split split = new Split(dataset, dataset.getUniqueRelationshipType(), new PredictorSplitType());

		final PredictorEvaluation evaluation = new PredictorEvaluation(split, logger);

		System.out.println(evaluation);
	}

	/**
	 * Perform all evaluations.
	 * 
	 * @param args Ignored
	 * @throws InstantiationException when a dataset was not found
	 * @throws IllegalAccessException when a dataset was not found
	 */
	public static void main(String[] args)
	    throws InstantiationException, IllegalAccessException
	{
		evaluateUnirelationalPredictors();
		evaluateUnirelationalRecommenders();
		evaluatePredictors();
		evaluateRecommenders();
	}

	/**
	 * All evaluated datasets.
	 */
	private final static List <Class <? extends GraphStoreDataset>> DATASETS = new ArrayList <Class <? extends GraphStoreDataset>>();
	static
	{
		DATASETS.add(Movielens100kDataset.class);
	}

	/**
	 * All unirelational datasets that are tested. They must be small enough to be loadable on all machines were tests
	 * are executed.
	 */
	private final static List <Class <? extends UnirelationalGraphStoreDataset>> DATASETS_UNIRELATIONAL = new ArrayList <Class <? extends UnirelationalGraphStoreDataset>>();
	static
	{
		DATASETS_UNIRELATIONAL.add(DblpCiteDataset.class);
		DATASETS_UNIRELATIONAL.add(Movielens100kRatingDataset.class);
		DATASETS_UNIRELATIONAL.add(AdvogatoDataset.class);
	}

	private static final Logger logger = Logger.getLogger(Evaluate.class);
}
