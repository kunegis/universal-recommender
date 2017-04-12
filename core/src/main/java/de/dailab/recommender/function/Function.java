package de.dailab.recommender.function;

/**
 * A function from double to double.
 * 
 * @author kunegis
 */
public interface Function
{
	/**
	 * Compute the function.
	 * 
	 * @param x Number to apply the function to
	 * @return The function applied to X
	 */
	public double apply(double x);

	/**
	 * @return name of the function, including parameters in parentheses, if any
	 */
	public String toString();
}
