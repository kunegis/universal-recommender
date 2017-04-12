package de.dailab.recommender.constraint;

import de.dailab.recommender.recommendation.Recommendation;

/**
 * A constraint that must be satisfied by recommendations.
 * <p>
 * An instance of this class may denote a set of constraints.
 * 
 * @author kunegis
 */
public interface Constraint
    extends GeneralConstraint
{
	/**
	 * Determine whether a given entity fulfills the constraint.
	 * 
	 * @param recommendation The recommendation that is check
	 * @return whether the recommendation fulfills the constraint
	 */
	boolean accept(Recommendation recommendation);

	/**
	 * @return the name of the constraint
	 */
	@Override
	public String toString();
}
