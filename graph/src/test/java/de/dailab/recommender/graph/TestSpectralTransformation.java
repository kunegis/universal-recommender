package de.dailab.recommender.graph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import de.dailab.recommender.dataset.UnirelationalDataset;
import de.dailab.recommender.evaluation.PredictorEvaluation;
import de.dailab.recommender.evaluation.predictionerror.CorrelationError;
import de.dailab.recommender.evaluation.predictionerror.PredictionError;
import de.dailab.recommender.function.Exponential;
import de.dailab.recommender.graph.unirelationaldatasets.Movielens100kRatingDataset;
import de.dailab.recommender.latent.EigenvalueDecompositionPredictor;
import de.dailab.recommender.latent.LatentPredictorList;
import de.dailab.recommender.latent.TransformedLatentPredictor;
import de.dailab.recommender.predict.Predictor;
import de.dailab.recommender.predict.PredictorList;
import de.dailab.recommender.similarity.RankReduction;
import de.dailab.recommender.similarity.SimilarityTransformation;
import de.dailab.recommender.similarity.SpectralTransformation;
import de.dailab.recommender.text.TextSyntaxException;

/**
 * Test similarity transformations.
 * 
 * @author kunegis
 */
public class TestSpectralTransformation
{
	/**
	 * Test using predictor lists.
	 * 
	 * @throws IOException on IO errors
	 * @throws TextSyntaxException on syntax errors
	 */
	@Test
	public void testPredictorList()
	    throws IOException, TextSyntaxException
	{
		final UnirelationalDataset dataset = new Movielens100kRatingDataset();

		final PredictorList predictorList = new LatentPredictorList(new EigenvalueDecompositionPredictor(),
		    new SpectralTransformation(new Exponential(1e-5)), new RankReduction(4));

		final PredictorEvaluation predictorEvaluation = new PredictorEvaluation(dataset, predictorList);

		System.out.println(predictorEvaluation);
	}

	/**
	 * Test rank reduction.
	 * 
	 * @throws IOException on IO errors
	 * @throws TextSyntaxException on syntax errors
	 */
	@Test
	public void testRankReduction()
	    throws IOException, TextSyntaxException
	{
		final int k = 30;

		final UnirelationalDataset dataset = new Movielens100kRatingDataset();

		final List <SimilarityTransformation> similarityTransformations = new ArrayList <SimilarityTransformation>();

		for (int i = 1; i <= k; ++i)
			similarityTransformations.add(new RankReduction(i));
		final PredictorList predictorList = new LatentPredictorList(new EigenvalueDecompositionPredictor(k),
		    similarityTransformations);

		final PredictorEvaluation predictorEvaluation = new PredictorEvaluation(dataset, predictorList);

		System.out.println(predictorEvaluation);

		final double values[] = new double[k + 1];
		for (final Entry <Predictor, Map <PredictionError, Double>> e: predictorEvaluation.errorValues.entrySet())
		{
			final Predictor predictor = e.getKey();
			final TransformedLatentPredictor transformedLatentPredictor = (TransformedLatentPredictor) predictor;
			final SimilarityTransformation similarityTransformation = transformedLatentPredictor
			    .getSimilarityTransformation();
			final RankReduction rankReduction = (RankReduction) similarityTransformation;
			final int rank = rankReduction.getRank();
			values[rank] = e.getValue().get(new CorrelationError());
		}

		for (int i = 0; i < k; ++i)
		{
			System.out.printf("%3d  %g\n", i, values[i]);
		}

		// XXX plot these numbers
	}
}
