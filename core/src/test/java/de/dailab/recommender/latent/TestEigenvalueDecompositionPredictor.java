package de.dailab.recommender.latent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import org.junit.Test;

import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntitySet;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.SimpleUnipartiteDataset;
import de.dailab.recommender.dataset.UnirelationalDataset;
import de.dailab.recommender.dataset.WeightRange;
import de.dailab.recommender.matrix.Matrix;
import de.dailab.recommender.predict.Predictor;
import de.dailab.recommender.predict.PredictorModel;
import de.dailab.recommender.recommend.Recommender;
import de.dailab.recommender.recommend.RecommenderModel;
import de.dailab.recommender.recommendation.Recommendation;

/**
 * Test the eigenvalue decomposition predictor.
 * 
 * @author kunegis
 */
public class TestEigenvalueDecompositionPredictor
{
	/**
	 * Compute eigenvalue decomposition predictions on a small example dataset.
	 */
	@Test
	public void test()
	{
		final UnirelationalDataset dataset = new SimpleUnipartiteDataset(9, WeightRange.UNWEIGHTED);

		final Matrix matrix = dataset.getMatrix();

		matrix.set(0, 1, 1);
		matrix.set(1, 2, 1);
		matrix.set(2, 3, 1);
		matrix.set(0, 4, 1);
		matrix.set(0, 5, 1);
		matrix.set(4, 6, 1);
		matrix.set(4, 7, 1);
		matrix.set(5, 6, 1);
		matrix.set(6, 8, 1);
		matrix.set(7, 8, 1);

		final Predictor predictor = new EigenvalueDecompositionPredictor(3);

		final PredictorModel predictorModel = predictor.build(dataset);

		final double predictions[] = new double[9];
		for (int i = 0; i < 9; ++i)
		{
			predictions[i] = predictorModel.predict(new Entity(SimpleUnipartiteDataset.ENTITY, 0), new Entity(
			    SimpleUnipartiteDataset.ENTITY, i));
			System.out.printf("prediction[%2d] = %g\n", i, predictions[i]);
		}
	}

	/**
	 * Test that no duplicates are returned.
	 */
	@Test
	public void testDuplicates()
	{
		final int n = 1000;
		final UnirelationalDataset dataset = new SimpleUnipartiteDataset(n, WeightRange.UNWEIGHTED);

		final Matrix matrix = dataset.getMatrix();

		final int r = (int) Math.pow(n, 1.5);

		final Random random = new Random();

		for (int i = 0; i < r; ++i)
			matrix.set(random.nextInt(n), random.nextInt(n), 1);

		final Recommender recommender = new LatentRecommender(new EigenvalueDecompositionPredictor());

		final RecommenderModel recommenderModel = recommender.build(dataset);

		for (int i = 0; i < 8; ++i)
		{
			final Entity entity = new Entity(SimpleUnipartiteDataset.ENTITY, i);

			final Iterator <Recommendation> iterator = recommenderModel.recommend(entity, new EntityType[]
			{ SimpleUnipartiteDataset.ENTITY });

			final Set <Entity> seen = new HashSet <Entity>();

			while (iterator.hasNext())
			{
				final Recommendation recommendation = iterator.next();

				assert !seen.contains(recommendation.getEntity());
				seen.add(recommendation.getEntity());
			}

			assert seen.size() > 10;
		}
	}

	/**
	 * Verify that a random matrix has positive and negative eigenvalues.
	 */
	@Test
	public void testSignedEigenvalues()
	{
		final int n = 1000;
		final UnirelationalDataset dataset = new SimpleUnipartiteDataset(n, WeightRange.SIGNED);
		final Matrix matrix = dataset.getMatrix();

		final int r = (int) Math.pow(n, 1.5);

		final Random random = new Random();

		for (int i = 0; i < r; ++i)
			matrix.set(random.nextInt(n), random.nextInt(n), random.nextGaussian());

		final LatentPredictor predictor = new EigenvalueDecompositionPredictor();

		final LatentPredictorModel predictorModel = predictor.build(dataset);

		int countPositive = 0;
		int countNegative = 0;

		for (final double lambda: predictorModel.getLambda())
		{
			assert !Double.isNaN(lambda);
			assert !Double.isInfinite(lambda);

			if (lambda > 0) ++countPositive;
			if (lambda < 0) ++countNegative;
		}

		assert countPositive > 0;
		assert countNegative > 0;
	}

	/**
	 * Verify that after LatentPredictorModel.orthogonalize(), the vectors are indeed orthonormal.
	 */
	@Test
	public void testOrthogonalize()
	{
		final double EPSILON = 1e-7;

		final int n = 1000;
		final UnirelationalDataset dataset = new SimpleUnipartiteDataset(n, WeightRange.SIGNED);

		final LatentPredictorModel model = new LatentPredictorModel(dataset, 9)
		{
			@Override
			public void update()
			{
				final Random random = new Random();

				u = new HashMap <EntityType, double[][]>();
				for (final EntitySet entitySet: dataset.getEntitySets())
				{
					final double ue[][] = new double[rank][entitySet.size()];

					u.put(entitySet.getType(), ue);
				}
				v = u;
				lambda = new double[rank];

				for (final double[][] ue: u.values())
				{
					for (int k = 0; k < rank; ++k)
						for (int i = 0; i < ue[0].length; ++i)
							ue[k][i] = random.nextGaussian();
				}

				iterate();
			}

			@Override
			public double iterate()
			{
				final double ret = orthogonalize();

				/* Ensure unit length of eigenvectors */
				for (int k = 0; k < rank; ++k)
				{
					double sumSquare = 0;
					for (final double[][] ue: u.values())
					{
						for (int i = 0; i < ue[0].length; ++i)
							sumSquare += ue[k][i] * ue[k][i];
					}
					assert Math.abs(1. - Math.sqrt(sumSquare)) < EPSILON;
				}

				/* Orthogonality */
				for (int k1 = 0; k1 < rank; ++k1)
					for (int k2 = k1 + 1; k2 < rank; ++k2)
					{
						double sumProduct = 0;
						for (final double[][] ue: u.values())
						{
							for (int i = 0; i < ue[0].length; ++i)
								sumProduct += ue[k1][i] * ue[k2][i];
						}
						assert Math.sqrt(Math.abs(sumProduct)) < EPSILON;
					}

				return ret;
			}
		};

		model.update();
	}
}
