package de.dailab.recommender.latent;

import de.dailab.recommender.dataset.Dataset;

/**
 * A latent predictor that implements build(Dataset) using build(Dataset, true).
 * 
 * @author kunegis
 */
public abstract class AbstractLatentPredictor
    implements LatentPredictor
{
	@Override
	public LatentPredictorModel build(Dataset dataset)
	{
		return build(dataset, true);
	}
}
