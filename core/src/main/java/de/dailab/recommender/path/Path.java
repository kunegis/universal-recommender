package de.dailab.recommender.path;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.DatasetEntry;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.recommendation.Recommendation;

/**
 * A path across relationship types.
 * <p>
 * Given a dataset and a starting entity, a path returned an iterator over entities.
 * <p>
 * A path may return entity multiple times. The returned iterator may not stop. Use a lookahead iterator to aggregate
 * weights for individual entities and filter out duplicates.
 * 
 * @author kunegis
 */
public interface Path
{
	/**
	 * Find entities by following relationships in a given dataset.
	 * <p>
	 * The trail is saved in the given map, which is not read and may already contain other entities. If trail is NULL,
	 * no trail is saved.
	 * <p>
	 * The trail contains, for each entity that was found, the set of previous entities in the graph, which is usually
	 * just one entity. In other words, the trail is a directed graph that connects all recommended entities to the
	 * source, in that direction.
	 * 
	 * @param dataset Dataset to use
	 * @param source Starting point of chain
	 * @param trail How the path was found; may be NULL
	 * 
	 * @return iterator An iterator over found recommendations; need not finish
	 */
	Iterator <Recommendation> recommend(Dataset dataset, Entity source, Map <Entity, Set <DatasetEntry>> trail);

	/**
	 * Compute the inverse path.
	 * 
	 * @return the inverse of this path.
	 */
	Path invert();

	/**
	 * The name of the path. Corresponds to the class name without the "Path" part followed by parameters in
	 * parentheses.
	 * 
	 * @return The name of the path
	 */
	@Override
	public String toString();
}
