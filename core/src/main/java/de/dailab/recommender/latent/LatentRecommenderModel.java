package de.dailab.recommender.latent;

import de.dailab.recommender.recommend.RecommenderModel;

/**
 * A recommender model that is based on a latent predictor, and therefore contains a latent predictor model that can be
 * retrieved.
 * 
 * @author kunegis
 */
public interface LatentRecommenderModel
    extends RecommenderModel
{
	/**
	 * Get the underlying latent predictor.
	 * 
	 * @return The underlying latent predictor
	 */
	LatentPredictorModel getLatentPredictorModel();
}
