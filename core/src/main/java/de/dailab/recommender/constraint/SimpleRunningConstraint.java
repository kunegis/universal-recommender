package de.dailab.recommender.constraint;

/**
 * A running constraint based on a given constraint. The given constraint must have no state; it is reused for all
 * recommendation runs.
 * 
 * @author kunegis
 */
public class SimpleRunningConstraint
    implements RunningConstraint
{
	/**
	 * A running constraint that simply applies a given simple constraint.
	 * 
	 * @param constraint The simple constraint to apply
	 */
	public SimpleRunningConstraint(Constraint constraint)
	{
		this.constraint = constraint;
	}

	private final Constraint constraint;

	@Override
	public Constraint run()
	{
		return constraint;
	}

	@Override
	public String toString()
	{
		return constraint.toString();
	}
}
