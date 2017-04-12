package de.dailab.recommender.recommend;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.dailab.recommender.dataset.DatasetEntry;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.recommendation.Recommendation;

/**
 * Implement extended recommendations based on basic recommendations. Optional features of RecommendationResults are not
 * supported.
 * 
 * @author kunegis
 */
public abstract class SimpleRecommenderModel
    implements RecommenderModel
{
	@Override
	public RecommendationResult recommendExt(Map <Entity, Double> sources, EntityType[] targetEntityTypes)
	    throws UnsupportedOperationException
	{
		final Iterator <Recommendation> iterator = recommend(sources, targetEntityTypes);

		return new RecommendationResult()
		{
			@Override
			public Iterator <Recommendation> getIterator()
			{
				return iterator;
			}

			@Override
			public Set <Entity> getVisitedEntities()
			    throws UnsupportedOperationException
			{
				throw new UnsupportedOperationException();
			}

			@Override
			public Map <Entity, Set <DatasetEntry>> getTrail()
			    throws UnsupportedOperationException
			{
				throw new UnsupportedOperationException();
			}
		};
	}
}
