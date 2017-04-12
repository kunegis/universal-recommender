package de.dailab.recommender.evaluation.predictionerror;

/**
 * An object that computes an error. Usually built using Error.run().
 * <p>
 * Usage: add value pairs to this object and call get().
 * 
 * @author kunegis
 */
public interface PredictionErrorRun
{
	/**
	 * Add a value/prediction pair to the run.
	 * <p>
	 * Note that while some error measures might be symmetric in value-prediction switching, this is not always the
	 * case. Therefore, the order of arguments to this function is significant.
	 * 
	 * @param value The actual value
	 * @param prediction The prediction
	 */
	void add(double value, double prediction);

	/**
	 * Compute the error.
	 * 
	 * @return The error. Nonnegative. Lower values denote better prediction accuracy.
	 */
	double get();
}
