package de.dailab.recommender.constraint;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.dailab.recommender.dataset.DatasetEntry;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.predict.PredictorModel;
import de.dailab.recommender.recommend.RecommendationResult;
import de.dailab.recommender.recommend.RecommenderModel;
import de.dailab.recommender.recommendation.Recommendation;

/**
 * A wrapper around a recommender model that filters out recommendations not fulfilling a given constraint.
 * 
 * @author kunegis
 */
public class ConstraintRecommenderModel
    implements RecommenderModel
{
	private final RunningConstraint runningConstraint;
	private final RecommenderModel recommenderModel;

	/**
	 * A constraint recommender model based on the given model that filters out recommendations not accepted by the
	 * given constraint.
	 * 
	 * @param generalConstraint The constraint that accepts those recommendations that should remain in the
	 *        recommendation list
	 * @param recommenderModel The recommender model out of which to filter recommendations
	 */
	public ConstraintRecommenderModel(GeneralConstraint generalConstraint, RecommenderModel recommenderModel)
	{
		if (generalConstraint instanceof Constraint)
			this.runningConstraint = new SimpleRunningConstraint((Constraint) generalConstraint);
		else if (generalConstraint instanceof RunningConstraint)
			this.runningConstraint = (RunningConstraint) generalConstraint;
		else
			throw new IllegalArgumentException("Invalid general constraint type "
			    + generalConstraint.getClass().getName());

		this.recommenderModel = recommenderModel;
	}

	/**
	 * A recommender model that honors multiple constraints. The order of the constraints is significant, as explained
	 * in {@code AggregateConstraint}.
	 * 
	 * @param recommenderModel The recommender model to apply the constraints to
	 * @param generalConstraints Ordered list of constraints to apply
	 */
	public ConstraintRecommenderModel(RecommenderModel recommenderModel, GeneralConstraint... generalConstraints)
	{
		this.runningConstraint = new AggregateConstraint(generalConstraints);

		this.recommenderModel = recommenderModel;
	}

	/**
	 * Get the underlying recommender model.
	 * 
	 * @return The underlying recommender model
	 */
	public RecommenderModel getRecommenderModel()
	{
		return recommenderModel;
	}

	@Override
	public Iterator <Recommendation> recommend(Entity source, EntityType[] targetEntityTypes)
	{
		final Iterator <Recommendation> iterator = recommenderModel.recommend(source, targetEntityTypes);
		return new ConstraintRecommendationIterator(runningConstraint.run(), iterator);
	}

	@Override
	public Iterator <Recommendation> recommend(Map <Entity, Double> sources, EntityType[] targetEntityTypes)
	{
		final Iterator <Recommendation> iterator = recommenderModel.recommend(sources, targetEntityTypes);
		return new ConstraintRecommendationIterator(runningConstraint.run(), iterator);
	}

	@Override
	public void update()
	{
		recommenderModel.update();
	}

	@Override
	public RecommendationResult recommendExt(Map <Entity, Double> sources, EntityType[] targetEntityTypes)
	{
		final RecommendationResult ret = recommenderModel.recommendExt(sources, targetEntityTypes);

		final Iterator <Recommendation> iterator = new ConstraintRecommendationIterator(runningConstraint.run(), ret
		    .getIterator());

		return new RecommendationResult()
		{
			@Override
			public Iterator <Recommendation> getIterator()
			{
				return iterator;
			}

			@Override
			public Map <Entity, Set <DatasetEntry>> getTrail()
			    throws UnsupportedOperationException
			{
				return ret.getTrail();
			}

			@Override
			public Set <Entity> getVisitedEntities()
			    throws UnsupportedOperationException
			{
				return ret.getVisitedEntities();
			}
		};
	}

	@Override
	public PredictorModel getPredictorModel()
	    throws UnsupportedOperationException
	{
		return recommenderModel.getPredictorModel();
	}
}
