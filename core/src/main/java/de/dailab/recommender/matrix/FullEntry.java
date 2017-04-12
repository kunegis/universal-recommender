package de.dailab.recommender.matrix;

/**
 * Row and column IDs along with a weight.
 */
public final class FullEntry
{
	/**
	 * A full entry with given row and column indexes and value.
	 * 
	 * @param rowIndex The row index
	 * @param colIndex The column index
	 * @param value The value
	 */
	public FullEntry(int rowIndex, int colIndex, double value)
	{
		this.rowIndex = rowIndex;
		this.colIndex = colIndex;
		this.value = value;
	}

	/**
	 * The row index.
	 */
	public final int rowIndex;

	/**
	 * The column index.
	 */
	public final int colIndex;

	/**
	 * The matrix entry.
	 */
	public final double value;
}
