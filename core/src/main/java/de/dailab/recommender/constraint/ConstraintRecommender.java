package de.dailab.recommender.constraint;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.recommend.AbstractRecommender;
import de.dailab.recommender.recommend.Recommender;
import de.dailab.recommender.recommend.RecommenderModel;

/**
 * A recommender with additional constraints on the recommended entities. This is a wrapper around another recommender
 * that filters results.
 * 
 * @author kunegis
 */
public class ConstraintRecommender
    extends AbstractRecommender
{
	/**
	 * A recommender that filters out recommendations not fulfilling a given constraint.
	 * 
	 * @param generalConstraint The constraint to honor
	 * @param recommender The underlying recommender
	 */
	public ConstraintRecommender(GeneralConstraint generalConstraint, Recommender recommender)
	{
		if (generalConstraint instanceof RunningConstraint)
			this.runningConstraint = (RunningConstraint) generalConstraint;
		else if (generalConstraint instanceof Constraint)
			this.runningConstraint = new SimpleRunningConstraint((Constraint) generalConstraint);
		else
			throw new IllegalArgumentException("Invalid general constraint type "
			    + generalConstraint.getClass().getName());
		this.recommender = recommender;
	}

	private final RunningConstraint runningConstraint;
	private final Recommender recommender;

	@Override
	public RecommenderModel build(Dataset dataset, boolean update)
	{
		final RecommenderModel recommenderModel = recommender.build(dataset, update);
		return new ConstraintRecommenderModel(runningConstraint, recommenderModel);
	}

	@Override
	public String toString()
	{
		return String.format("Constraint(%s)-%s", runningConstraint, recommender);
	}
}
