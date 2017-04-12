package de.dailab.recommender.latent;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.predict.Predictor;

/**
 * A predictor that specifically returns a latent predictor model.
 * 
 * @author kunegis
 */
public interface LatentPredictor
    extends Predictor
{
	/**
	 * {@inheritDoc}
	 * 
	 * @return a latent predictor model
	 */
	@Override
	LatentPredictorModel build(Dataset dataset);

	/**
	 * {@inheritDoc}
	 * 
	 * @return a latent predictor model
	 */
	@Override
	LatentPredictorModel build(Dataset dataset, boolean update);
}
