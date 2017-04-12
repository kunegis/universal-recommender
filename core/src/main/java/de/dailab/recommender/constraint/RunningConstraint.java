package de.dailab.recommender.constraint;

/**
 * A constraint that uses a Run object for each recommendation iterator. This makes it possible to implement constraint
 * that remember something about previous recommendations.
 * 
 * @author kunegis
 */
public interface RunningConstraint
    extends GeneralConstraint
{
	/**
	 * @return A constraint object that is used for one recommendation run
	 */
	Constraint run();

	/**
	 * @return the name of the constraint
	 */
	@Override
	public String toString();
}
