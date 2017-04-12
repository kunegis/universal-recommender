package de.dailab.recommender.matrix;

/**
 * An iterator over matrix entries (Entry or FullEntry) with the possibility to update values.
 * 
 * @param <T> The type over which to iterate
 * 
 * @author kunegis
 */
public interface MatrixIterable <T>
    extends Iterable <T>
{
	@Override
	public MatrixIterator <T> iterator();
}
