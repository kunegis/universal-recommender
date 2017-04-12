package de.dailab.recommender.recommend;

import java.util.Iterator;
import java.util.List;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.predict.PredictorList;

/**
 * A list of recommenders. This interface has a build() method, so all recommenders can built at once for a given
 * dataset.
 * 
 * @see PredictorList
 * 
 * @author kunegis
 */
public interface RecommenderList
{
	/**
	 * The recommenders contained in this list.
	 * 
	 * @return The list of recommenders
	 */
	List <Recommender> getRecommenders();

	/**
	 * Build recommender models for the dataset. The returned iterator uses the same indexes as getRecommenders().
	 * <p>
	 * Implementations may build a shared model of the dataset.
	 * 
	 * @param dataset The dataset for which the recommender models are built
	 * @return An iterator oder recommender models.
	 */
	Iterator <RecommenderModel> build(Dataset dataset);
}
