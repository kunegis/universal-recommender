package de.dailab.recommender.recommend;

import de.dailab.recommender.dataset.Dataset;

/**
 * A Recommender object denotes a particular recommendation algorithm. Given a dataset, a recommender can build a
 * recommender model which can then be used to compute recommendations.
 * <p>
 * Constructors of implementations typically take parameters of the algorithms as arguments (if any).
 * 
 * @author kunegis
 */
public interface Recommender
{
	/**
	 * Given a dataset, build a recommender model. The returned recommender model can then be used to compute
	 * recommendations quickly. Invocation of this function may be slow.
	 * 
	 * @param dataset A dataset
	 * @return A recommender model that can be used to compute recommendations for the dataset
	 */
	RecommenderModel build(Dataset dataset);

	/**
	 * Build a recommender model, but don't initialize it. The returned recommender model has to be updated using
	 * update() before it can be used for recommendation. Invocation of this method should be faster than
	 * build(Dataset).
	 * 
	 * @param dataset The dataset for which to build a recommender model
	 * @param update Update the model immediately
	 * @return A possibly not updated recommender model
	 */
	RecommenderModel build(Dataset dataset, boolean update);

	/**
	 * @return the name of the recommendation algorithm. Names are usually similar to the class name, but omit the
	 *         "Recommender" part. Arguments are given in parentheses. Multiple algorithm part names are joined by a
	 *         dash.
	 */
	public String toString();
}
