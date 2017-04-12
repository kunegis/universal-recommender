package de.dailab.recommender.matrix;

import java.util.Iterator;

/**
 * An iterator over matrix entry that additionally allows to change values.
 * 
 * @param <T> The type over which to iterate
 * 
 * @author kunegis
 */
public interface MatrixIterator <T>
    extends Iterator <T>
{
	/**
	 * Change a value in the underlying matrix or vector.
	 * 
	 * @param newValue The new value
	 */
	public void set(double newValue);
}
