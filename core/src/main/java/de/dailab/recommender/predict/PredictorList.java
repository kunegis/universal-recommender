package de.dailab.recommender.predict;

import java.util.Iterator;
import java.util.List;

import de.dailab.recommender.dataset.Dataset;

/**
 * A list of predictors. A list of predictors has the ability to build predictor models shared by several models, so
 * using a predictor list is more efficient that building several predictor models separately. This is useful for
 * ensemble predictors and evaluation.
 * <p>
 * The basic implementation is PredictorList, which just builds each predictor model separately.
 * 
 * @author kunegis
 */
public interface PredictorList
{
	/**
	 * @return The predictors contained in this set, in the same order as build() would build them
	 */
	List <Predictor> getPredictors();

	/**
	 * Build all predictor models.
	 * 
	 * @param dataset The dataset to build the predictor models for
	 * 
	 * @return An iterator over the predictor models; they may share an underlying model
	 */
	Iterator <PredictorModel> build(Dataset dataset);
}
