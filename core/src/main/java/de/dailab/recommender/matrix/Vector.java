package de.dailab.recommender.matrix;

/**
 * Sparse vector with fast index; may be held in memory or backed by a remote data source.
 * <p>
 * All indexes begin at zero.
 * <p>
 * Note that vectors don't have a fixed size.
 * 
 * @author kunegis
 */
public interface Vector
    extends MatrixIterable <Entry>
{
	/**
	 * @return Number of nonsparse entries
	 */
	int nnz();

	/**
	 * @return The internal index type, as the name of a primitive type, e.g. "short" or "boolean"
	 */
	String getIndexType();

	/**
	 * @return The internal weight type, as the name of a primitive type, e.g. "short" or "boolean"
	 */
	String getWeightType();

	/**
	 * Set a value.
	 * 
	 * @param i Index to set
	 * @param value New value
	 */
	void setGeneric(int i, double value);

	/**
	 * Read a value; return 0 for sparse values.
	 * 
	 * @param i Index to read
	 * @return Value in the vector; 0 for sparse entries
	 */
	double getGeneric(int i);

	/**
	 * Add a value to an entry, which may be sparse.
	 * 
	 * @param i Index to change
	 * @param value Value to add
	 */
	void addGeneric(int i, double value);

	MatrixIterator <Entry> iterator();
}
