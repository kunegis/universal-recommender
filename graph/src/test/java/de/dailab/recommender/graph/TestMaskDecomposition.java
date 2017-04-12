package de.dailab.recommender.graph;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.dailab.recommender.dataset.UnirelationalDataset;
import de.dailab.recommender.evaluation.PredictorEvaluation;
import de.dailab.recommender.graph.unirelationaldatasets.SlashdotZooDataset;
import de.dailab.recommender.latent.EigenvalueDecompositionPredictor;
import de.dailab.recommender.mask.MaskDecompositionPredictor;
import de.dailab.recommender.predict.Predictor;
import de.dailab.recommender.text.TextSyntaxException;

/**
 * Test the mask decomposition predictor.
 * 
 * @author kunegis
 */
public class TestMaskDecomposition
{
	/**
	 * Test the mask decomposition using the Slashdot Zoo dataset. The dataset used must be weighted and unipartite.
	 * 
	 * @throws IOException on IO errors
	 * @throws TextSyntaxException on syntax errors
	 */
	@Test
	public void testMaskDecomposition()
	    throws IOException, TextSyntaxException
	{
		final UnirelationalDataset dataset = new SlashdotZooDataset();

		final List <Predictor> predictors = new ArrayList <Predictor>();
		predictors.add(new EigenvalueDecompositionPredictor());
		predictors.add(new MaskDecompositionPredictor());

		final PredictorEvaluation predictorEvaluation = new PredictorEvaluation(dataset, predictors);

		System.out.println(predictorEvaluation);
	}
}
