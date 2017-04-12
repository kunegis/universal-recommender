package de.dailab.recommender.recommend;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * An iterator that merge several given iterators.
 * <p>
 * This iterator returns elements from each of the underlying iterators in turns.
 * 
 * @param <T> The type of iterator
 * 
 * @see AggregateIterator
 * 
 * @author kunegis
 */
public class MergeIterator <T>
    implements Iterator <T>
{
	/**
	 * The merge of the given iterators.
	 * 
	 * @param iterators The iterators to merge
	 */
	public MergeIterator(List <Iterator <T>> iterators)
	{
		for (final Iterator <T> iterator: iterators)
			this.iterators.add(iterator);
	}

	/**
	 * The remaining iterators.
	 */
	private final Queue <Iterator <T>> iterators = new LinkedList <Iterator <T>>();

	@Override
	public boolean hasNext()
	{
		while (iterators.size() != 0)
		{
			if (!iterators.peek().hasNext())
				iterators.remove();
			else
				break;
		}
		return iterators.size() != 0;
	}

	@Override
	public T next()
	{
		if (!hasNext()) throw new NoSuchElementException();
		final Iterator <T> nextIterator = iterators.remove();
		final T ret = nextIterator.next();
		iterators.add(nextIterator);
		return ret;
	}

	@Override
	public void remove()
	{
		throw new UnsupportedOperationException("MergeIterator does not support removal");
	}
}
