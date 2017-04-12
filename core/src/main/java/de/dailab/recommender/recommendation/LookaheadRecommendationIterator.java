package de.dailab.recommender.recommendation;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import de.dailab.recommender.dataset.Entity;

/**
 * A recommendation iterator that looks ahead a certain number of recommendations and aggregates duplicate entities.
 * <p>
 * Recommendation algorithms return lists of recommendations. Some recommendation algorithms produce lists containing
 * duplicates. This iterator provides a wrapper that aggregates duplicates into one recommendation, adding their scores.
 * 
 * @author kunegis
 */
public class LookaheadRecommendationIterator
    implements Iterator <Recommendation>
{
	/**
	 * An iterator over recommendations that removes duplicates and aggregates scores additively.
	 * 
	 * @param lookahead The number of recommendations to look ahead
	 * @param iterator The underlying iterator, which may produce duplicates
	 */
	public LookaheadRecommendationIterator(int lookahead, Iterator <Recommendation> iterator)
	{
		if (iterator instanceof LookaheadRecommendationIterator) { throw new Error(
		    "Invalid nesting of lookahead iterators"); }

		assert lookahead > 0;
		this.lookahead = lookahead;
		this.walkLength = 5 * lookahead;
		this.iterator = iterator;
	}

	@Override
	public boolean hasNext()
	{
		int i = walkLength;
		while (queue.isEmpty() && iterator.hasNext() && i-- > 0)
		{
			insertNext();
		}

		return !queue.isEmpty();
	}

	@Override
	public Recommendation next()
	{
		int i = walkLength;
		while (queue.size() < lookahead && iterator.hasNext() && i-- > 0)
		{
			insertNext();
		}
		final Recommendation ret = queue.remove();
		queueMap.remove(ret.getEntity());
		return ret;
	}

	@Override
	public void remove()
	{
		throw new UnsupportedOperationException();
	}

	private final int lookahead;

	/**
	 * Maximal number of nodes to visit at each step. This is different from the lookahead, which only counts visited
	 * nodes that can be returned.
	 */
	private final int walkLength;

	private final Iterator <Recommendation> iterator;

	/**
	 * The next recommendations to return. Have not yet been seen. Sorted by score, the natural ordering of
	 * Recommendation. Entities are unique in this queue.
	 */
	private final Queue <Recommendation> queue = new PriorityQueue <Recommendation>();

	/**
	 * The recommendations in this.queue by their entity.
	 */
	private final Map <Entity, Recommendation> queueMap = new HashMap <Entity, Recommendation>();

	/**
	 * The entities already seen or in this.queue at the moment.
	 */
	private final Set <Entity> seen = new HashSet <Entity>();

	/**
	 * Insert or add the next recommendation from the iterator into the queue and queue map.
	 */
	private void insertNext()
	{
		final Recommendation next = iterator.next();

		final Entity entity = next.getEntity();

		if (!seen.contains(entity))
		{
			queue.add(next);
			queueMap.put(entity, next);
			seen.add(entity);
		}
		else
		{
			/* If the entity is not in the queue that means it was already returned. Forget about the additional weight. */
			final Recommendation previousRecommendation = queueMap.get(entity);
			if (previousRecommendation != null)
			{
				assert previousRecommendation.getEntity().equals(entity);
				queue.remove(previousRecommendation);
				queueMap.remove(entity);
				final Recommendation newRecommendation = new Recommendation(entity, previousRecommendation.getScore()
				    + next.getScore());
				queue.add(newRecommendation);
				queueMap.put(entity, newRecommendation);
			}
		}
	}
}
