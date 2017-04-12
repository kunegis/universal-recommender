package de.dailab.recommender.predict;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.Entity;

/**
 * A compiled model that can compute predictions for a dataset. Typically built using a predictor and a dataset.
 * 
 * @author kunegis
 */
public interface PredictorModel
{
	/**
	 * Compute a prediction for a given entity pair.
	 * <p>
	 * There is no restriction on the meaning of the result, except that larger values indicate a better match. If the
	 * underlying relationship type is weighted, the prediction may correspond to that weight range. If the underlying
	 * relationship type is unweighted, the prediction may be a probability for a link to exist. If there is no
	 * underlying relationship type, the value may denote similarity or proximity in the network.
	 * 
	 * @param source Source entity
	 * @param target Target entity
	 * 
	 * @return Predicted value
	 */
	double predict(Entity source, Entity target);

	/**
	 * Update the predictor model to changes in the dataset.
	 */
	void update();

	/**
	 * @return the dataset that was used to build this predictor model.
	 */
	Dataset getDataset();
}
