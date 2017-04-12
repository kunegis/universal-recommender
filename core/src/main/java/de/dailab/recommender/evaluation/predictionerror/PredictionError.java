package de.dailab.recommender.evaluation.predictionerror;

/**
 * An error measure for predictors.
 * <p>
 * A prediction errors measures the difference between actual edge weights and predicted edge weights. Prediction errors
 * are usually nonnegative, and are zero only for perfect prediction.
 * <p>
 * An instance of this class represents a particular measure of prediction error. To compute the prediction error for a
 * specific edge weight/prediction pair, create a PredictionErrorRun object with run().
 * 
 * @author kunegis
 */
public interface PredictionError
{
	/**
	 * @return a new instance of the corresponding error run.
	 */
	PredictionErrorRun run();

	/**
	 * @return The name of the error measure. Corresponds to the class name with any "PredictionError" part removed.
	 */
	@Override
	public String toString();
}
