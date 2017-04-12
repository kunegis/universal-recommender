package de.dailab.recommender.neighborhood;

import de.dailab.recommender.similarity.EuclideanSimilarity;
import de.dailab.recommender.similarity.Similarity;

/**
 * A neighborhood finder that works with the (inverted) Euclidean similarity.
 * <p>
 * This is a basic clustering implementation which does not necessarily give results in the correct order.
 * 
 * @see EuclideanSimilarity
 * 
 * @author kunegis
 */
public class EuclideanNeighborhoodFinder
    implements NeighborhoodFinder
{
	@Override
	public NeighborhoodFinderModel build(Similarity similarity, double[] lambda, double[][] u)
	    throws UnsupportedSimilarityException
	{
		if (!(similarity instanceof EuclideanSimilarity))
		    throw new UnsupportedSimilarityException(
		        "The Euclidean neighborhood finder only works with the inverted Euclidean similarity");

		return null;
	}
}
