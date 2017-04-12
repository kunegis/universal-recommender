package de.dailab.recommender.constraint;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.iterators.FilterIterator;

import de.dailab.recommender.dataset.DatasetEntry;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.predict.PredictorModel;
import de.dailab.recommender.recommend.RecommendationResult;
import de.dailab.recommender.recommend.RecommenderModel;
import de.dailab.recommender.recommendation.Recommendation;

/**
 * A recommender model based on another recommender model that filters out source entities from recommendations.
 * <p>
 * This is primarily useful for recommendations from one entity type to the same entity type, e.g. user--user
 * recommendations.
 * 
 * @author kunegis
 */
public final class NoSelfRecommenderModel
    implements RecommenderModel
{
	private final RecommenderModel recommenderModel;

	/**
	 * The no-self recommender model based on the given recommender model
	 * 
	 * @param recommenderModel The underlying recommender model
	 */
	public NoSelfRecommenderModel(RecommenderModel recommenderModel)
	{
		assert recommenderModel != null;
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

	@SuppressWarnings("unchecked")
	@Override
	public Iterator <Recommendation> recommend(final Entity source, EntityType[] targetEntityTypes)
	{
		final Iterator <Recommendation> iterator = recommenderModel.recommend(source, targetEntityTypes);
		return new FilterIterator(iterator, new Predicate()
		{
			@Override
			public boolean evaluate(Object arg0)
			{
				final Recommendation recommendation = (Recommendation) arg0;
				return !recommendation.getEntity().equals(source);
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator <Recommendation> recommend(final Map <Entity, Double> sources, EntityType[] targetEntityTypes)
	{
		final Iterator <Recommendation> iterator = recommenderModel.recommend(sources, targetEntityTypes);
		return new FilterIterator(iterator, new Predicate()
		{
			@Override
			public boolean evaluate(Object arg0)
			{
				final Recommendation recommendation = (Recommendation) arg0;
				for (final Entity entity: sources.keySet())
					if (entity.equals(recommendation.getEntity())) return false;
				return true;
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public RecommendationResult recommendExt(final Map <Entity, Double> source, EntityType[] targetEntityTypes)
	{
		final RecommendationResult recommendationResult = recommenderModel.recommendExt(source, targetEntityTypes);
		final Iterator <Recommendation> filterIterator = new FilterIterator(recommendationResult.getIterator(),
		    new Predicate()
		    {
			    @Override
			    public boolean evaluate(Object arg0)
			    {
				    final Recommendation recommendation = (Recommendation) arg0;
				    return !source.containsKey(recommendation.getEntity());
			    }
		    });

		return new RecommendationResult()
		{
			@Override
			public Iterator <Recommendation> getIterator()
			{
				return filterIterator;
			}

			@Override
			public Set <Entity> getVisitedEntities()
			    throws UnsupportedOperationException
			{
				return recommendationResult.getVisitedEntities();
			}

			@Override
			public Map <Entity, Set <DatasetEntry>> getTrail()
			    throws UnsupportedOperationException
			{
				return recommendationResult.getTrail();
			}
		};
	}

	@Override
	public void update()
	{
		recommenderModel.update();
	}

	@Override
	public PredictorModel getPredictorModel()
	    throws UnsupportedOperationException
	{
		return recommenderModel.getPredictorModel();
	}
}
