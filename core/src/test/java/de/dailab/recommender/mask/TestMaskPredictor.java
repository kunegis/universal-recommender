package de.dailab.recommender.mask;

import java.util.Random;

import org.junit.Test;

import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.SimpleBipartiteDataset;
import de.dailab.recommender.dataset.SimpleUnipartiteDataset;
import de.dailab.recommender.dataset.UnirelationalDataset;
import de.dailab.recommender.dataset.WeightRange;
import de.dailab.recommender.evaluation.PredictorEvaluation;
import de.dailab.recommender.matrix.Matrix;
import de.dailab.recommender.predict.Predictor;
import de.dailab.recommender.predict.PredictorModel;

/**
 * Test the mask predictor.
 * 
 * @author kunegis
 */
public class TestMaskPredictor
{
	/**
	 * Prediction ratings on a small synthetic unirelational networks with signed edges.
	 */
	@Test
	public void testMask()
	{
		final int n = 9;

		final UnirelationalDataset dataset = new SimpleUnipartiteDataset(n, WeightRange.SIGNED);

		/*
		 * The network is a user-user unipartite graph with signed edges. Users are in two groups (0-4 and 5-8). User
		 * from the same group mostly like each other and users from different groups mostly don't like each other.
		 * 
		 * Each edge is only present in one direction.
		 */

		final Matrix matrix = dataset.getMatrix();

		matrix.set(0, 1, +1);
		matrix.set(0, 2, +1);
		matrix.set(0, 4, -1);
		matrix.set(0, 5, -1);
		matrix.set(1, 2, +1);
		matrix.set(2, 3, +1);
		matrix.set(2, 4, +1);
		matrix.set(2, 6, -1);
		matrix.set(2, 7, -1);
		matrix.set(3, 4, +1);
		matrix.set(3, 7, -1);
		matrix.set(4, 8, +1);
		matrix.set(5, 6, +1);
		matrix.set(5, 7, +1);
		matrix.set(6, 7, +1);
		matrix.set(6, 8, +1);
		matrix.set(7, 8, +1);

		final Predictor predictor = new MaskDecompositionPredictor();

		final PredictorModel predictorModel = predictor.build(dataset);

		assert predictorModel.predict(new Entity(SimpleUnipartiteDataset.ENTITY, 4), new Entity(
		    SimpleUnipartiteDataset.ENTITY, 1)) < 0;

		for (int i = 0; i < n; ++i)
		{
			for (int j = 0; j < n; ++j)
			{
				System.out.print(" "
				    + predictorModel.predict(new Entity(SimpleUnipartiteDataset.ENTITY, i), new Entity(
				        SimpleUnipartiteDataset.ENTITY, j)));
			}
			System.out.println();
		}
	}

	/**
	 * Check that the mask decomposition converges on bipartite datasets.
	 */
	public void testBipartiteConvergence()
	{
		// XXX bipartite convergence

		final int m = 1000;
		final int n = 200;
		final int r = 10000;

		final UnirelationalDataset unirelationalDataset = new SimpleBipartiteDataset(m, n);

		final Matrix matrix = unirelationalDataset.getMatrix();

		final Random random = new Random();

		for (int i = 0; i < r; ++i)
			matrix.set(random.nextInt(m), random.nextInt(n), random.nextGaussian());

		System.out.println(new PredictorEvaluation(unirelationalDataset, new MaskDecompositionPredictor()));
	}
}
