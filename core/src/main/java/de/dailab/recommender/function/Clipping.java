package de.dailab.recommender.function;

/**
 * Utilities for the cutting postprocessing steps, which consists of restricting ratings to the interval [−1, +1].
 * 
 * @author kunegis
 */
public final class Clipping
{
	/**
	 * Restrict a rating value to the interval [−1,+1].
	 * 
	 * @param x The rating
	 * @return The rating restricted to the interval
	 */
	public static double clip(double x)
	{
		if (Double.isNaN(x)) return 0.;
		if (x > 1.0) x = 1.0;
		if (x < -1.0) x = -1.0;
		return x;
	}

	/**
	 * Clip a number to the given bounds.
	 * 
	 * @param x The number to clip
	 * @param bounds The bounds to use
	 * @return the clipped number
	 */
	public static double clip(double x, Bounds bounds)
	{
		if (Double.isNaN(x)) return x;
		if (!Double.isNaN(bounds.min) && x < bounds.min) x = bounds.min;
		if (!Double.isNaN(bounds.max) && x > bounds.max) x = bounds.max;
		return x;
	}

	/**
	 * Make sure D is in the right range
	 * 
	 * @param d Must be in [-1, +1]
	 * @return d
	 */
	public static double dontClip(double d)
	{
		assert -1.0 <= d && d <= +1.0;
		return d;
	}

	/**
	 * Round to either +a or -a, whatever is nearer.
	 * 
	 * @param x The number to round
	 * @param a A number >0
	 * @return -a or +a
	 */
	public static double upclip(double x, double a)
	{
		assert a > 0.0;
		return x < 0.0 ? -a : +a;
	}
}
