package de.dailab.recommender.average;

import java.util.Locale;

/**
 * The generalized mean, defined as mean_p(x_i) = (1/n sum_i x_i^p)^(1/p), where p is the single parameter (the power).
 * <p>
 * Negative weights are supported in the following way: The corresponding value is negated and the absolute value is
 * applied to weights.
 * 
 * @author kunegis
 */
public class GeneralizedMean
    implements Average
{
	/**
	 * Generalized mean with given power.
	 * 
	 * @param p The power. Must be non-zero and non-infinite.
	 */
	public GeneralizedMean(double p)
	{
		assert p != 0.;
		assert !Double.isInfinite(p);

		this.p = p;
	}

	/**
	 * The generalized mean with a default power.
	 */
	public GeneralizedMean()
	{
		this(P_DEFAULT);
	}

	@Override
	public AverageRun run()
	{
		return new AverageRun()
		{
			@Override
			public void add(double weight, double value)
			{
				assert !Double.isNaN(value) && !Double.isInfinite(value);
				final double powValue = spow(value, p);
				assert !Double.isNaN(powValue);
				averageRun.add(weight, powValue);
			}

			@Override
			public double getAverage()
			{
				final double average = averageRun.getAverage();
				assert !Double.isNaN(average);
				final double ret = spow(average, 1. / p);
				assert !Double.isNaN(ret);
				return ret;
			}

			private final AverageRun averageRun = new Mean().run();

		};
	}

	@Override
	public String toString()
	{
		return String.format(Locale.ROOT, "GeneralizedMean(%.4f)", p);
	}

	private final double p;

	/**
	 * The default power.
	 */
	public static final double P_DEFAULT = .715;

	/**
	 * Signed power: return power of absolute value multiplied with sign of number.
	 * 
	 * @return abs(x) * (|x|^y)
	 */
	private static double spow(double x, double y)
	{
		if (x == 0.) return 0.; /* avoid taking a negative power of zero */
		return Math.signum(x) * Math.pow(Math.abs(x), y);
	}
}
