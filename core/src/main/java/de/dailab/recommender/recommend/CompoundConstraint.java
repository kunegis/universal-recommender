package de.dailab.recommender.recommend;

import de.dailab.recommender.constraint.Constraint;
import de.dailab.recommender.recommendation.Recommendation;

/**
 * A constraint composed of multiple constraint objects.
 * 
 * @author kunegis
 * 
 */
public class CompoundConstraint
    implements Constraint
{
	/**
	 * A constraint composed of several given constraints.
	 * 
	 * @param constraints List of constraints to honor
	 */
	public CompoundConstraint(Constraint... constraints)
	{
		assert constraints != null;
		this.constraints = constraints;
	}

	private final Constraint constraints[];

	@Override
	public boolean accept(Recommendation recommendation)
	{
		for (final Constraint constraint: constraints)
			if (!constraint.accept(recommendation)) return false;

		return true;
	}
}
