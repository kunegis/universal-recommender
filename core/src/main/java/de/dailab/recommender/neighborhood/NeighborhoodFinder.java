package de.dailab.recommender.neighborhood;

import de.dailab.recommender.similarity.Similarity;

/**
 * An algorithm to find the neighborhood of a vector, as measured by a similarity measure.
 * <p>
 * Objects of this type represent specific algorithms along with any parameters. The method build() is used to to build
 * a model output of a vector set that can be used to find the neighbor fast.
 * <p>
 * Implementing classes document which similarities they support.
 * 
 * @see Similarity
 * 
 * @author kunegis
 */
public interface NeighborhoodFinder
{
	/**
	 * Build a model out of a given latent model that is able to compute the neighborhood of a vector fast, according to
	 * the given similarity.
	 * 
	 * @param similarity The similarity according to which neighbors are searched
	 * @param lambda The singular values, of length (k)
	 * @param u The input matrix; the first index is the rank (k), the second index is the entity ID (n)
	 * @param entityType Recommendations returned by the model will contain entities of this type
	 * 
	 * @return a model that finds neighborhoods fast
	 * 
	 * @throws UnsupportedSimilarityException The given similarity is not supported by this neighborhood finder
	 */
	NeighborhoodFinderModel build(Similarity similarity, double lambda[], double u[][]/* , EntityType entityType */)
	    throws UnsupportedSimilarityException;

	/**
	 * @return the name of the algorithm. Should correspond to the class name, with any "NeighborhoodFinder" part
	 *         omitted. Parameters can be given on parentheses.
	 */
	public String toString();
}
