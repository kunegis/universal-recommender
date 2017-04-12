package de.dailab.recommender.recommend;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

/**
 * An iterator that takes its recommendations from multiple distinct iterators. Removal is unsupported.
 * 
 * @param <T> Type returned by the iterator
 * 
 * @author kunegis
 */
public class AggregateIterator <T>
    implements Iterator <T>
{
	// XXX use org.apache.commons.collections.iterators.IteratorChain

	/**
	 * An aggregate iterator built from several given iterators.
	 * 
	 * @param iterators Collection of iterators to use
	 */
	public AggregateIterator(Collection <Iterator <T>> iterators)
	{
		this.iterators = new LinkedList <Iterator <T>>(iterators);
	}

	private final Queue <Iterator <T>> iterators;

	@Override
	public boolean hasNext()
	{
		while (!iterators.isEmpty() && !iterators.element().hasNext())
			iterators.remove();

		if (iterators.isEmpty()) return false;

		return true;
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
		throw new UnsupportedOperationException();
	}
}
