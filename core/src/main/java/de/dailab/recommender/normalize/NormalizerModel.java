package de.dailab.recommender.normalize;

/**
 * A model of a relationship set that can normalize and denormalize values.
 * <p>
 * normalize() and denormalize() are the inverse functions of each other.
 * 
 * @author kunegis
 */
public interface NormalizerModel
{
	/**
	 * Given a relationship weight in the initial relationship set, compute the normalized weight according to this
	 * algorithm.
	 * 
	 * @param value The initial weight
	 * @param rowIndex The row index
	 * @param colIndex The column index
	 * @return The normalized value
	 */
	double normalize(double value, int rowIndex, int colIndex);

	/**
	 * Given a normalized relationship weight, compute the initial weight. This is the inverse operation of normalize().
	 * The given weight/row/col triple does not necessarily correspond to an entry in the initial matrix. In general,
	 * this method is applied to predicted weights.
	 * 
	 * @param value The normalized weight
	 * @param rowIndex The row index
	 * @param colIndex The column index
	 * @return The initial value
	 */
	double denormalize(double value, int rowIndex, int colIndex);
}
