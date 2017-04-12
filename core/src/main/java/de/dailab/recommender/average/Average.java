package de.dailab.recommender.average;

/**
 * A way to compute a weighted average (in the broad sense) of a list of numbers and weights.
 * <p>
 * In some cases, weights may be negative. Implementations document whether they support negative weights.
 * 
 * @author kunegis
 */
public interface Average
{
	/**
	 * Create an AverageRun object that can be used to compute the average for one set of numbers.
	 * 
	 * @return an average run
	 */
	public AverageRun run();

	/**
	 * @return the name of this algorithm
	 */
	public String toString();
}
