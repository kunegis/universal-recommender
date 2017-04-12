package de.dailab.recommender.latent;

import org.junit.Test;

import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.predict.PredictorModel;
import de.dailab.recommender.similarity.ExponentialKernel;

/**
 * Test spectral transformations of latent predictors.
 * 
 * @author kunegis
 */
public class TestSpectralTransformations
{
	/**
	 * Test the exponential kernel.
	 */
	@Test
	public void testExp()
	{
		final SyntheticDataset dataset = new SyntheticDataset();

		final LatentPredictor predictor = new EigenvalueDecompositionPredictor(13);

		final LatentPredictorModel predictorModel = predictor.build(dataset);

		final PredictorModel predictorModelExp = new TransformedLatentPredictorModel(new ExponentialKernel(1),
		    predictorModel);

		final Entity entity_0 = new Entity(SyntheticDataset.ENTITY, 0);

		for (int i = 0; i < dataset.getEntitySet(SyntheticDataset.ENTITY).size(); ++i)
			System.out.printf("prediction(%2d) = %g\n", i, predictorModelExp.predict(entity_0, new Entity(
			    SyntheticDataset.ENTITY, i)));
	}
}
