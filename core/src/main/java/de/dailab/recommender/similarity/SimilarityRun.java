package de.dailab.recommender.similarity;

/**
 * Compute the similarity between two vectors. Objects of type {@code SimilarityRun} are usually returned by {@code
 * Similarity.run()}.
 * <p>
 * An object implementing this interface can be used to compute the similarity between two vectors. After creating an
 * object, add() will add vector components to the statistics. The similarity can be computed with getSimilarity(). It
 * is possible to call getSimilarity() multiple time, inserting values in between.
 * <p>
 * The x/y/lambda triples should be added by decreasing absolute lambda, but this is no strict rule.
 */
public interface SimilarityRun
{
	/**
	 * Add a vector component pair, along with an eigenvalue.
	 * <p>
	 * Lambda may be negative.
	 * 
	 * @param x A number
	 * @param y Another number
	 * @param lambda The eigenvalue
	 */
	public void add(double x, double y, double lambda);

	/**
	 * @return The similarity of the two vectors, as add()ed until now.
	 */
	public double getSimilarity();
}
