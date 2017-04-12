package de.dailab.recommender.recommend;

import java.util.Iterator;
import java.util.NoSuchElementException;

import de.dailab.recommender.recommendation.Recommendation;

/**
 * A filter that removes recommendations with nonpositive scores from a recommendation iterator.
 * 
 * @author kunegis
 */
public class PositiveIterator
    implements Iterator <Recommendation>
{
	/*
	 * Implementation note: this can easily be refactored to support a dynamic filter.
	 */

	/**
	 * An iterator that filters out nonpositive recommendations from a given iterator.
	 * 
	 * @param iterator The iterator to filter
	 */
	public PositiveIterator(Iterator <Recommendation> iterator)
	{
		this.iterator = iterator;
	}

	@Override
	public boolean hasNext()
	{
		if (next != null) return true;
		do
		{
			if (!iterator.hasNext()) return false;
			next = iterator.next();
		}
		while (!accept(next));
		return true;
	}

	@Override
	public Recommendation next()
	{
		if (!hasNext()) throw new NoSuchElementException();
		final Recommendation ret = next;
		next = null;
		return ret;
	}

	@Override
	public void remove()
	{
		throw new UnsupportedOperationException();
	}

	private Recommendation next = null;
	private final Iterator <Recommendation> iterator;

	/**
	 * Whether a recommendation is accepted by this filter.
	 * 
	 * @param recommendation A recommendation
	 * @return Whether the recommendation is strictly positive
	 */
	private static boolean accept(Recommendation recommendation)
	{
		return recommendation.getScore() > 0;
	}
}
