package de.dailab.recommender.path;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.DatasetEntry;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.recommendation.Recommendation;

/**
 * A path that is also able to take several entities as sources.
 * 
 * @author kunegis
 */
public interface MultiPath
    extends Path
{
	/**
	 * Find entities by following relationships in a given dataset. This methods acts as recommend(Dataset, Entity, ...)
	 * but takes a weighted set of entities as input instead of a single entity.
	 * 
	 * @param dataset Dataset to use
	 * @param sources Starting point of chain; contains at least one entity
	 * @param trail How the path was found; may be NULL
	 * 
	 * @return iterator An iterator over found recommendations; need not finish
	 */
	Iterator <Recommendation> recommend(Dataset dataset, Map <Entity, Double> sources,
	    Map <Entity, Set <DatasetEntry>> trail);
}
