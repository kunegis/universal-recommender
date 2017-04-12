package de.dailab.recommender.genericmatrix;

/**
 * Corresponds to matrix.Entry, but generic.
 * 
 * @param <Index> Index type
 * @param <Value> Value type
 * @author kunegis
 */
public class Entry <Index, Value>
{
	/**
	 * The index value.
	 */
	public final Index index;

	/**
	 * The value.
	 */
	public final Value value;

	/**
	 * An entry with given entry and value.
	 * 
	 * @param index The index
	 * @param value The value
	 */
	public Entry(Index index, Value value)
	{
		this.index = index;
		this.value = value;
	}
}
