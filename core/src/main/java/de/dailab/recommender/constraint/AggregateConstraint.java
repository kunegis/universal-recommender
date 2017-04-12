package de.dailab.recommender.constraint;

import de.dailab.recommender.recommendation.Recommendation;

/**
 * A conjunction of multiple constraints.
 * <p>
 * The order of individual constraints is significant: If a constraint rejects a recommendation, the following
 * constraints are not queried, and their memory is thus not used.
 * 
 * @author kunegis
 */
public class AggregateConstraint
    implements RunningConstraint
{
	/**
	 * The conjunction of the given constraints, in that order.
	 * 
	 * @param generalConstraints The constraints to use
	 */
	public AggregateConstraint(GeneralConstraint... generalConstraints)
	{
		this.generalConstraints = generalConstraints;
	}

	private final GeneralConstraint generalConstraints[];

	@Override
	public Constraint run()
	{
		final int n = generalConstraints.length;
		final Constraint constraints[] = new Constraint[n];

		for (int i = 0; i < n; ++i)
		{
			if (generalConstraints[i] instanceof RunningConstraint)
				constraints[i] = ((RunningConstraint) generalConstraints[i]).run();
			else if (generalConstraints[i] instanceof Constraint)
				constraints[i] = (Constraint) generalConstraints[i];
			else
				throw new IllegalArgumentException("Invalid general constraint type "
				    + generalConstraints[i].getClass().getName());
		}

		return new Constraint()
		{
			@Override
			public boolean accept(Recommendation recommendation)
			{
				for (int i = 0; i < constraints.length; ++i)
				{
					if (!constraints[i].accept(recommendation)) return false;
				}

				return true;
			}
		};
	}
}
