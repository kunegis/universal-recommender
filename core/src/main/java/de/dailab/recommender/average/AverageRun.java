package de.dailab.recommender.average;

/**
 * Calculate some sort of average given value/weight pairs.
 * <p>
 * AverageRun objects are returned by Average.get(). Once created, add() adds a value and a weight. getAverage() can
 * then be used to compute the average. addAverage() can be called multiple times.
 * <p>
 * The memory usage and runtime is unspecified. Some implementations may need O(1), others O(n) memory in the number of
 * values added. The runtime is unspecified but typically constant for both add() and getAverage().
 * 
 * @author kunegis
 */
public interface AverageRun
{
	/**
	 * Add a number to the set of number of with the average is computed.
	 * 
	 * @param weight The weight. May be negative.
	 * @param value The number itself
	 */
	public void add(double weight, double value);

	/**
	 * Compute the average of all numbers previously added. This method can be called multiple times.
	 * 
	 * @return The average of all numbers previously added.
	 */
	public double getAverage();
}
