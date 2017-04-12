package de.dailab.recommender.latent;

import de.dailab.recommender.similarity.ExponentialKernel;

/**
 * A good parameter-free latent predictor for unweighted datasets.
 * 
 * @deprecated Use DefaultLatentPredictor, which now also supports spectral transformations
 * 
 * @author kunegis
 */
@Deprecated
public class DefaultUnweightedLatentPredictor
    extends TransformedLatentPredictor
{
	/**
	 * The default unweighted latent predictor. This is the default unnormalized latent predictor with the exponential
	 * kernel.
	 */
	public DefaultUnweightedLatentPredictor()
	{
		super(new ExponentialKernel(), new DefaultUnnormalizedLatentPredictor());
	}
}
