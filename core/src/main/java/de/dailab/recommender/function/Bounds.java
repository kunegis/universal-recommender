package de.dailab.recommender.function;

/**
 * The bounds of a rating source.
 * 
 * Any of the fields may be +-Inf to indicate unboundedness.
 * 
 * @author kunegis
 */
public final class Bounds
{
	/**
	 * Unbounded on both ends.
	 */
	private Bounds()
	{
		min = Double.NEGATIVE_INFINITY;
		max = Double.POSITIVE_INFINITY;
	}

	/**
	 * Bounds defined by given minimum and maximum.
	 * 
	 * @param min minimum
	 * @param max maximum
	 */
	public Bounds(double min, double max)
	{
		this.min = min;
		this.max = max;
	}

	/**
	 * Minimum value. May be -Inf.
	 */
	public final double min;

	/**
	 * Maximum value. May be +Inf.
	 */
	public final double max;

	/**
	 * Unbounded, i.e. [-Inf, +Inf].
	 */
	public static final Bounds UNBOUNDED = new Bounds();
}
