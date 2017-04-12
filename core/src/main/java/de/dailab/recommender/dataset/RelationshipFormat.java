package de.dailab.recommender.dataset;

/**
 * For a relationship set, the symmetry and partition information.
 * <p>
 * The names of the constants correspond to the values used in the relationship header, except for the case.
 * 
 * @author kunegis
 */
public enum RelationshipFormat
{
	/**
	 * A symmetric relationship between two entities of the same type. The corresponding adjacency matrix is square and
	 * symmetric.
	 */
	SYM,

	/**
	 * An asymmetric relationship between two entities of the same type. The corresponding adjacency matrix is square
	 * and asymmetric.
	 */
	ASYM,

	/**
	 * A relationship between two entities of different types. The corresponding adjacency matrix is rectangular.
	 */
	BIP, ;

	/**
	 * Whether the relationship set of this format is square, i.e. the graph is unipartite.
	 * 
	 * @return Whether this relationship format represents a square relationship set
	 */
	public boolean isSquare()
	{
		return this != BIP;
	}
}
