package de.dailab.recommender.predict;

import de.dailab.recommender.dataset.Dataset;

/**
 * An algorithm for computing a prediction given an entity pair.
 * <p>
 * Predictors represent certain algorithms with their parameters. Predictor models represent an instantiation of an
 * algorithm for a specific dataset. Implementations usually only store parameters in the Predictor and compute a model
 * in the constructor of their PredictorModel.
 * 
 * @author kunegis
 */
public interface Predictor
{
	/**
	 * Build a model based on a given dataset
	 * 
	 * @param dataset base the model on this dataset
	 * @return The model that can be queried.
	 */
	PredictorModel build(Dataset dataset);

	/**
	 * Build a predictor model out of a dataset and optionally update the model immediately.
	 * 
	 * @param dataset The dataset to build a model for
	 * @param update Update the model immediately
	 * @return The predictor model
	 */
	PredictorModel build(Dataset dataset, boolean update);

	/**
	 * @return The name of the algorithm. Names should correspond to class names, but omitting the "Predictor" part.
	 *         Parameters can be given in parentheses. Multiple algorithm part names are joined by a dash.
	 */
	public String toString();
}
