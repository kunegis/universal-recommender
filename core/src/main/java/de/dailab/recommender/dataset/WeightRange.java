package de.dailab.recommender.dataset;

/**
 * For a relationship type, the range of weights.
 * <p>
 * The weight range determines the range of allowed edge weights, or equivalently the range of nonzero entries in the
 * adjacency matrix.
 * 
 * @author kunegis
 */
public enum WeightRange
{
	/**
	 * An unweighted relationship type. All edges have weight one implicitly, and all edges have weight zero implicitly.
	 * The adjacency matrix is a 0/1 matrix, and link prediction / recommendation algorithms are formulated as
	 * information retrieval problems. The underlying graph is unweighted.
	 */
	UNWEIGHTED,

	/**
	 * A relationship type with positive edge weights. Non-edges have a weight of zero implicitly. The underlying graph
	 * can usually be modeled using multiple edges (for integral weights), and the adjacency matrix is nonnegative. Edge
	 * weights often represent a count.
	 */
	POSITIVE,

	/**
	 * A relationship with signed weights. Non-edges are not considered zero. The adjacency matrix is often a -1/0/+1
	 * matrix, and the underlying graph is signed.
	 */
	SIGNED,

	/**
	 * A relationship with variable weights on an unknown scale. Non-edges are not equivalent to zero-edges. It is
	 * usually necessary to normalize the adjacency matrix additively, e.g. by subtracted the global mean of edge
	 * weights.
	 */
	WEIGHTED;

	/**
	 * Whether in a dataset with this weight range, non-edges are equivalent to edges with zero weight. This returns
	 * true for UNWEIGHTED and POSITIVE.
	 * <p>
	 * If this is true, link prediction and matrix approximation methods will usually try to return zero for non-edges.
	 * If this is false, link prediction and matrix approximation methods will usually only apply to edges (as opposed
	 * to non-edges.)
	 * 
	 * @return Whether non-edges correspond to zero-edges
	 */
	public boolean isZeroSignificant()
	{
		return this == UNWEIGHTED || this == POSITIVE;
	}
}
