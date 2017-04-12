package de.dailab.recommender.matrix;

/**
 * Entry in iterations.
 *<p>
 * Entry objects are returned by iterators over matrix rows or columns.
 * 
 * @author kunegis
 */
public final class Entry
{
	/**
	 * The row or column index.
	 */
	public final int index;

	/**
	 * The matrix entry.
	 */
	public final double value;

	/**
	 * An entry with given index and value.
	 * 
	 * @param index The row or column index
	 * @param value The value
	 */
	public Entry(int index, double value)
	{
		this.index = index;
		this.value = value;
	}
}
