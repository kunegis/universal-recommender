package de.dailab.recommender.evaluation.recommenderperformance;

/**
 * A measure of a recommender's performance. Objects implementing this interface represent specific algorithms for
 * expressing the performance of a recommender.
 * 
 * @author kunegis
 */
public interface RecommenderPerformance
{
	/**
	 * Build a new instance for computing the performance of a single recommendation set.
	 * 
	 * @return The recommender performance run object
	 */
	RecommenderPerformanceRun run();

	/**
	 * @return The name of the algorithm. Corresponds to the class without any "RecommenderPerformance" part.
	 */
	@Override
	public String toString();
}
