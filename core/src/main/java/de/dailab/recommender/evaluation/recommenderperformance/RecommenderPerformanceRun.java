package de.dailab.recommender.evaluation.recommenderperformance;

import java.util.Iterator;
import java.util.Set;

import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.recommendation.Recommendation;

/**
 * A single run for computing the performance of a recommender.
 * 
 * @author kunegis
 */
public interface RecommenderPerformanceRun
{
	// XXX in add(), pass not just a set of entities but a set of weighted entities.

	/**
	 * Add test set and recommendations pair for one entity.
	 * 
	 * @param testSet The test set retained for the next entity
	 * @param recommendations The recommendations returned by the recommender
	 */
	void add(Set <Entity> testSet, Iterator <Recommendation> recommendations);

	/**
	 * Compute the performance measure taking into account all entities added until now. May be called multiple times.
	 * 
	 * @return The performance measure
	 */
	double get();
}
