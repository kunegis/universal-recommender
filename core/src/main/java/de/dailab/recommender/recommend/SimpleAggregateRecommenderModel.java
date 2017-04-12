package de.dailab.recommender.recommend;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.recommendation.Recommendation;

/**
 * A recommender model that implements recommend(Entity) in terms of recommend(Map).
 * 
 * @author kunegis
 */
public abstract class SimpleAggregateRecommenderModel
    implements RecommenderModel
{
	@Override
	public Iterator <Recommendation> recommend(Entity source, EntityType[] targetEntityTypes)
	{
		final Map <Entity, Double> sources = new HashMap <Entity, Double>();
		sources.put(source, 1.);
		return recommend(sources, targetEntityTypes);
	}
}
