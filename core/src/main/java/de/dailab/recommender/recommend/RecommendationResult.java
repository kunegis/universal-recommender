package de.dailab.recommender.recommend;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.dailab.recommender.dataset.DatasetEntry;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.recommendation.Recommendation;

/**
 * The result of extended recommendations. This interface has getter methods for whatever a recommender may return.
 * <p>
 * Some features are optional; they are defined to throw UnsupportedOperationException when they are not supported.
 * <p>
 * The visited entities and the trail are optional. If they are implemented, they return only the data pertaining to the
 * entities that were previously recommended using this recommendation result object. In other words, first read out
 * recommendations from the iterator, then get the visited entity set and the trail.
 * 
 * @author kunegis
 */
public interface RecommendationResult
{
	/**
	 * Get the iterator over recommendations.
	 * 
	 * @return The iterator over recommendations; may safely be accessed multiple times
	 */
	public Iterator <Recommendation> getIterator();

	/**
	 * Get the entities that were visited during computation of the recommendations.
	 * 
	 * @return The set of visited entities, including the source; not NULL
	 * @throws UnsupportedOperationException When the operation is not supported
	 */
	public Set <Entity> getVisitedEntities()
	    throws UnsupportedOperationException;

	/**
	 * Return the trail, i.e. the path leading from recommendations back to source entities. This information is
	 * optional.
	 * 
	 * @return For each entity, the set of previous entities
	 * @throws UnsupportedOperationException When no trail is computed by the underlying recommender
	 */
	public Map <Entity, Set <DatasetEntry>> getTrail()
	    throws UnsupportedOperationException;
}
