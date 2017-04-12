package de.dailab.recommender.matrix;

/**
 * A sparse matrix with fast row and column index. May be held in memory or backed by a remote data source.
 * Implementations have values that are a subset of double and indexes that are a subset of int.
 * <p>
 * All indexes begin at zero.
 * <p>
 * As a general rule, out of range accesses result in undefined behavior. I.e., implementations are free to choose their
 * behavior, including failing or throwing a meaningful exception.
 * <p>
 * It is unspecified whether entries may be zero. For instance, after setting the value zero in a matrix cell, some
 * implementation may consider this cell empty and others may consider it having a value of zero. Example: An
 * implementation could be always full.
 * <p>
 * This interface provides no explicit way of clearing matrix entries. Elements can be set to zero, in which case an
 * implementation may choose to remove the corresponding entry.
 * 
 * @author kunegis
 */
public interface Matrix
{
	/**
	 * Number of rows. All row indexes are smaller than this value.
	 * 
	 * @return the number of rows
	 */
	int rows();

	/**
	 * Number of columns. All column indexes are smaller than this value.
	 * 
	 * @return the number of columns
	 */
	int cols();

	/**
	 * Compute the number of entries. This call may not run in constant time.
	 * 
	 * @return The number of entries
	 */
	int nnz();

	/**
	 * The most restrictive known weight type. Must be the name of a primitive type. "boolean" denotes unweighted sparse
	 * matrices.
	 * 
	 * @return The weight type
	 */
	String getWeightType();

	/**
	 * Return the number of entries in a row.
	 * 
	 * @param row The row index
	 * @return The number of entries in the row
	 */
	int getRowCount(int row);

	/**
	 * Return the number of entries in a column.
	 * 
	 * @param col The column index
	 * @return The number of entries in the column
	 */
	int getColCount(int col);

	/**
	 * Set the matrix cell to a value. Implementation may restrict the cells that can be set, or implement side-effects
	 * (e.g. a symmetric matrix would set (i,j) and (j,i) at the same time.)
	 * 
	 * @param i row index
	 * @param j column index
	 * @param value Value to set. May be rounded to the precision of the matrix.
	 */
	void set(int i, int j, double value);

	/**
	 * Get the cell value.
	 * 
	 * @param i row index
	 * @param j column index
	 * @return cell value, zero if an empty cell
	 */
	double get(int i, int j);

	/**
	 * Determine whether the matrix is known to be symmetric. If the result is TRUE, the matrix is symmetric. If the
	 * result is false, the symmetry of the matrix is unknown. Implementations typically do a best effort to return an
	 * accurate result in constant time.
	 * 
	 * @return Whether the matrix is known to be symmetric
	 */
	boolean isSymmetric();

	/**
	 * Multiplication with a vector.
	 * <p>
	 * Compute RET += WEIGHT * This * v.
	 * 
	 * @param v a vector of size M (row count of this)
	 * @param ret Previous values to which the product is added. If NULL, return the product. If set, the vector is
	 *        modified in-place and returned.
	 * @param weight factor
	 * @return The product; return RET if it was given
	 */
	double[] mult(double v[], double ret[], double weight);

	/**
	 * As mult(), but use This^t instead of This.
	 * <p>
	 * Compute RET += WEIGHT * This' * v.
	 * 
	 * @param v input vector
	 * @param ret output vector (optional)
	 * @param weight factor
	 * @return result
	 */
	double[] multT(double v[], double ret[], double weight);

	/**
	 * Iterate over all entries. The order is unspecified.
	 * 
	 * @return An iterable over all entries
	 */
	MatrixIterable <FullEntry> all();

	/**
	 * Iterate over the entries of a row.
	 * 
	 * @param i row index
	 * @return iterable over all row entries
	 */
	Iterable <Entry> row(int i);

	/**
	 * Iterate over the entries of a column.
	 * 
	 * @param j column index
	 * @return Iterable over all column entries
	 */
	Iterable <Entry> col(int j);

	/**
	 * Iterate over all nonempty rows.
	 * 
	 * @return An iterator over all nonempty rows, in an unspecified order
	 */
	Iterable <Integer> getRows();

	/**
	 * Iterate over all nonempty columns.
	 * 
	 * @return An iterator over all nonempty columns, in an unspecified order
	 */
	Iterable <Integer> getCols();
}
