package de.dailab.recommender.latent;

import de.dailab.recommender.recommend.SimpleRecommenderModel;

/**
 * A latent recommender model where recommendExt() is implemented in terms of recommender().
 * 
 * @author kunegis
 */
public abstract class SimpleLatentRecommenderModel
    extends SimpleRecommenderModel
    implements LatentRecommenderModel
{

}
