package de.dailab.recommender.constraint;

import java.util.Iterator;
import java.util.NoSuchElementException;

import de.dailab.recommender.recommendation.Recommendation;

/**
 * Iterate over the recommendations given by a recommendation iterator and filter results using a constraint.
 * 
 * @author kunegis
 */
public class ConstraintRecommendationIterator
    implements Iterator <Recommendation>
{
	/**
	 * An iterator that filters out recommendations not fulfilling a given constraint.
	 * 
	 * @param iterator The underlying iterator
	 * @param constraint The constraint to honor
	 */
	public ConstraintRecommendationIterator(Constraint constraint, Iterator <Recommendation> iterator)
	{
		this.constraint = new SimpleRunningConstraint(constraint).run();
		// this.constraint = constraint;
		this.iterator = iterator;
	}

	@Override
	public boolean hasNext()
	{
		if (nextRecommendation != null) return true;

		while (iterator.hasNext())
		{
			nextRecommendation = iterator.next();
			if (constraint.accept(nextRecommendation)) return true;
		}
		nextRecommendation = null;
		return false;
	}

	@Override
	public Recommendation next()
	{
		if (!hasNext()) throw new NoSuchElementException();

		final Recommendation ret = nextRecommendation;
		nextRecommendation = null;
		return ret;
	}

	@Override
	public void remove()
	{
		throw new UnsupportedOperationException("Removing recommendations is unsupported");
	}

	private final Constraint constraint;
	private final Iterator <Recommendation> iterator;

	/**
	 * Non-NULL if prefetched. If set, passed the constraint.
	 */
	private Recommendation nextRecommendation;
}
