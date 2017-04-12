package de.dailab.recommender.similarity;

/**
 * A measure of similarity between two vectors.
 * <p>
 * Given two vectors of length <i>k</i> and <i>k</i> eigenvectors, a similarity object computes a similarity value. To
 * be precise, an object of type Similarity computes a function of two vectors X and Y and a spectrum &Lambda;, all of
 * which are vectors of length <i>k</i>.
 * <p>
 * Implementations may be kernels. There is support for similarities with negative signature, i.e. similarities that are
 * not positive-semidefinite. Some similarity implementations don't support negative eigenvalues. Most similarities
 * support vectors of arbitrary length <i>k</i>. Some similarities however supported only fixed length vectors.
 * <p>
 * Interpreted as a link prediction function in a graph, similarities only depends on the neighbors of any two nodes.
 * <p>
 * Many similarities are mathematically undefined when the vectors have length zero. Implementations return 0 is this
 * case.
 * 
 * @author kunegis
 */
public interface Similarity
{
	/**
	 * @return a run object for computing the similarity for one value pair list.
	 */
	SimilarityRun run();

	/**
	 * Should be the class name without any "Similarity" part.
	 * 
	 * @return The name of this similarity measure
	 */
	@Override
	String toString();

	/**
	 * Whether the similarity is spectral, i.e. whether is can be expressed as x' F(&Lambda;) y, where F(&Lambda;) is
	 * deterministic.
	 * 
	 * @return Whether this similarity is spectral
	 */
	boolean isSpectral();

	/**
	 * Compute a transformed spectrum. Only for spectral similarities.
	 * 
	 * @param lambda Original spectrum
	 * @return The transformed spectrum; may be LAMBDA
	 * @throws UnsupportedOperationException When the similarity is not spectral
	 */
	public double[] transformSpectrum(double lambda[])
	    throws UnsupportedOperationException;
}
