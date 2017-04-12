package de.dailab.recommender.graph;

import java.io.IOException;
import java.util.Iterator;

import org.junit.Test;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.RelationshipSet;
import de.dailab.recommender.graph.unirelationaldatasets.AdvogatoDataset;
import de.dailab.recommender.graph.unirelationaldatasets.Bibsonomy2uiDataset;
import de.dailab.recommender.graph.unirelationaldatasets.DblpCiteDataset;
import de.dailab.recommender.graph.unirelationaldatasets.HepThCitationsDataset;
import de.dailab.recommender.graph.unirelationaldatasets.JesterDataset;
import de.dailab.recommender.graph.unirelationaldatasets.Movielens100kRatingDataset;
import de.dailab.recommender.graph.unirelationaldatasets.SlashdotZooDataset;
import de.dailab.recommender.graph.unirelationaldatasets.WikiEditElwikiDataset;
import de.dailab.recommender.latent.LatentRecommender;
import de.dailab.recommender.matrix.Entry;
import de.dailab.recommender.matrix.FullEntry;
import de.dailab.recommender.matrix.Matrix;
import de.dailab.recommender.recommend.Recommender;
import de.dailab.recommender.recommend.RecommenderModel;
import de.dailab.recommender.recommendation.Recommendation;
import de.dailab.recommender.recommendation.RecommendationUtils;
import de.dailab.recommender.text.TextSyntaxException;

/**
 * Test loading of unirelational datasets.
 * 
 * @author kunegis
 */
public class TestUnirelational
{
	/**
	 * Datasets for which we compute recommendations.
	 */
	private static final String DATASET_NAMES_FULL[] = new String[]
	{ DblpCiteDataset.NAME, AdvogatoDataset.NAME, Movielens100kRatingDataset.NAME };

	/**
	 * Datasets that we only load.
	 */
	private static final String DATASET_NAMES_LOAD[] = new String[]
	{ HepThCitationsDataset.NAME, SlashdotZooDataset.NAME, JesterDataset.NAME, Bibsonomy2uiDataset.NAME,
	    WikiEditElwikiDataset.NAME };

	/**
	 * Test all unirelational graph store datasets.
	 * 
	 * @throws TextSyntaxException syntax error
	 * @throws IOException IO error
	 */
	@Test
	public void testUnirelational()
	    throws TextSyntaxException, IOException
	{
		for (final String name: DATASET_NAMES_FULL)
		{
			final Dataset dataset = loadDataset(name);

			computeRecommendations(dataset, name);
		}

		for (final String name: DATASET_NAMES_LOAD)
		{
			@SuppressWarnings("unused")
			final Dataset dataset = loadDataset(name);
		}
	}

	private Dataset loadDataset(String name)
	    throws IOException, TextSyntaxException
	{
		System.out.printf("Load %s...\n", name);

		final Dataset dataset = new UnirelationalGraphStoreDataset(name);

		final Matrix matrix = dataset.getRelationshipSets().iterator().next().getMatrix();

		System.out.printf("typeof(matrix) == %s\n", matrix.getClass().getSimpleName());

		assert matrix.rows() >= 90;
		assert matrix.cols() >= 90;

		System.out.println("\tTest by row...");

		int rowCount = 0;
		double rowSum = 0.;
		double rowSquareSum = 0.;

		for (int i = 0; i < matrix.rows(); ++i)
		{
			for (final Entry entry: matrix.row(i))
			{
				++rowCount;
				rowSum += entry.value;
				rowSquareSum += entry.value * entry.value;
			}
		}

		System.out.println("\tTest by column...");

		int colCount = 0;
		double colSum = 0.;
		double colSquareSum = 0.;
		for (int j = 0; j < matrix.cols(); ++j)
		{
			for (final Entry entry: matrix.col(j))
			{
				++colCount;
				colSum += entry.value;
				colSquareSum += entry.value * entry.value;
			}
		}

		System.out.println("\tTest all...");

		int allCount = 0;
		double allSum = 0.;
		double allSquareSum = 0.;
		for (final FullEntry fullEntry: matrix.all())
		{
			++allCount;
			allSum += fullEntry.value;
			allSquareSum += fullEntry.value * fullEntry.value;
		}

		/*
		 * Note: Due to rounding, the exact sum and square sum depends on the summation order.
		 */

		assert rowCount == allCount;
		assert colCount == allCount;
		assert rowSum == allSum;
		assert colSum == allSum;
		assert Math.abs(rowSquareSum - allSquareSum) < 1e-6;
		assert Math.abs(colSquareSum - allSquareSum) < 1e-6;

		return dataset;
	}

	/**
	 * Compute recommendations for the dataset, and output them.
	 * 
	 * @param dataset the dataset, containing a single relationship set.
	 * @param name of dataset
	 */
	private void computeRecommendations(Dataset dataset, String name)
	{
		final RelationshipSet relationshipSet = dataset.getRelationshipSets().iterator().next();

		final EntityType sourceType = relationshipSet.getSubject();
		final EntityType targetType = relationshipSet.getObject();

		final Recommender recommender = new LatentRecommender();

		System.out.printf("Build recommender %s for %s...", recommender, name);

		final RecommenderModel recommenderModel = recommender.build(dataset);

		System.out.println();

		for (int i = 15; i < 18; ++i)
		{
			final Entity source = new Entity(sourceType, i);

			System.out.printf("recommendations for %s:\n", source);

			final Iterator <Recommendation> iterator = recommenderModel.recommend(source, new EntityType[]
			{ targetType });

			for (int j = 0; j < 5; ++j)
			{
				if (!iterator.hasNext()) break;
				final Recommendation recommendation = iterator.next();
				System.out.println("\t" + RecommendationUtils.format(recommendation, dataset));
			}
		}
	}
}
