package de.dailab.recommender.recommend;

import java.util.Iterator;
import java.util.Map;

import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.predict.PredictorModel;
import de.dailab.recommender.recommendation.Recommendation;

/**
 * A compiled model of a dataset that can be used to compute recommendations quickly. Typically created by
 * Recommender.build(Dataset).
 * <p>
 * Recommender models return iterators over recommendations. The recommendations returned by an iterator should be
 * sorted by nonincreasing score. There is however no guarantee that score are actually nondecreasing. To sort
 * recommendations by score, use a {@code LookaheadRecommender}. Recommendation iterators may return the same entity
 * multiple times. It is also possible for recommendation iterators to never finish.
 * <p>
 * An extended interface recommendExt() returns a RecommendationResult object, which contains a recommendation iterator
 * and other information.
 * 
 * @author kunegis
 */
public interface RecommenderModel
{
	/**
	 * Compute recommendations.
	 * 
	 * @param source The source entity, for which recommendations are to be computed
	 * @param targetEntityTypes Types of entities to recommend
	 * @return An iterator over recommendations. Because there is no "underlying collection" the remove() method is
	 *         typically unsupported
	 */
	public Iterator <Recommendation> recommend(Entity source, EntityType targetEntityTypes[]);

	/**
	 * Compute recommendations based on a weighted set of given entities.
	 * 
	 * @param sources The source entities along with their weights
	 * @param targetEntityTypes The type of the entities to recommend
	 * @return An iterator over recommendations.
	 */
	public Iterator <Recommendation> recommend(Map <Entity, Double> sources, EntityType targetEntityTypes[]);

	/**
	 * Extended recommendations. Recommenders implementing this may return whatever additional information they want in
	 * the returned recommendation iterator, such as explanations, or not-recommended entities, etc.
	 * 
	 * @param sources The source entities for which recommendations are to be computed
	 * @param targetEntityTypes The request entity types
	 * @return A recommendation result, which contains at least a recommendation iterator, but maybe also other
	 *         information
	 */
	public RecommendationResult recommendExt(Map <Entity, Double> sources, EntityType targetEntityTypes[]);

	/**
	 * Update the model to changes in the dataset. If the underlying {@code Dataset} changes, this method updates this
	 * recommender model.
	 */
	public void update();

	/**
	 * Get the underlying predictor model. This operation is optional. It is only implemented when there is an
	 * underlying predictor model. As a general rule, latent recommenders have an underlying predictor, and path
	 * recommenders have no underlying predictor.
	 * 
	 * @return The underlying predictor
	 * @throws UnsupportedOperationException When there is no underlying predictor
	 */
	public PredictorModel getPredictorModel()
	    throws UnsupportedOperationException;
}
