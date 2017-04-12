package de.dailab.recommender.recommend;

import java.util.Iterator;

import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.neighborhood.WeightedPoint;
import de.dailab.recommender.recommendation.Recommendation;

/**
 * A recommendation iterator based on a weighted point iterator. All resulting entities are given the same entity type.
 * 
 * @author kunegis
 */
public class WeightedPointRecommendationIterator
    implements Iterator <Recommendation>
{
	/**
	 * A recommendation iterator based on a given weighted point iterator that returns recommendations with entities of
	 * the given type.
	 * 
	 * @param iterator The underlying weighted point iterator
	 * @param entityType The type of all recommended entities
	 */
	public WeightedPointRecommendationIterator(Iterator <WeightedPoint> iterator, EntityType entityType)
	{
		this.iterator = iterator;
		this.entityType = entityType;
	}

	private final Iterator <WeightedPoint> iterator;
	private final EntityType entityType;

	@Override
	public boolean hasNext()
	{
		return iterator.hasNext();
	}

	@Override
	public Recommendation next()
	{
		final WeightedPoint weightedPoint = iterator.next();
		return new Recommendation(new Entity(entityType, weightedPoint.point), weightedPoint.score);
	}

	@Override
	public void remove()
	{
		iterator.remove();
	}
}
