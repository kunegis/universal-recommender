package de.dailab.recommender.latent;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.normalize.AdditiveNormalizationStrategy;
import de.dailab.recommender.normalize.DefaultAdditiveNormalizationStrategy;

/**
 * A latent predictor based only on an additive normalizer.
 * <p>
 * Additive normalization as the subtraction of a low-rank matrix. This predictor uses a latent model to implement a
 * given additive normalization strategy.
 * 
 * @author kunegis
 */
public class LatentNormalizationPredictor
    extends AbstractLatentPredictor
{
	/**
	 * A latent normalization predictor using a given additive normalization strategy.
	 * 
	 * @param additiveNormalizationStrategy The additive normalization strategy
	 */
	public LatentNormalizationPredictor(AdditiveNormalizationStrategy additiveNormalizationStrategy)
	{
		this.additiveNormalizationStrategy = additiveNormalizationStrategy;
	}

	/**
	 * A latent normalization predictor using the default additive normalization strategy.
	 */
	public LatentNormalizationPredictor()
	{
		this(new DefaultAdditiveNormalizationStrategy());
	}

	@Override
	public LatentPredictorModel build(Dataset dataset, boolean update)
	{
		return new LatentNormalizationPredictorModel(dataset, additiveNormalizationStrategy, update);
	}

	private final AdditiveNormalizationStrategy additiveNormalizationStrategy;

	@Override
	public String toString()
	{
		return String.format("LatentNormalization(%s)", additiveNormalizationStrategy);
	}
}
