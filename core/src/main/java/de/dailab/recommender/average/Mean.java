package de.dailab.recommender.average;

/**
 * The arithmetic mean.
 * <p>
 * Negative weights are not supported.
 * <p>
 * Memory usage and runtime are both constant.
 * 
 * @author kunegis
 */
public class Mean
    implements Average
{
	@Override
	public AverageRun run()
	{

		return new AverageRun()
		{

			@Override
			public void add(double weight, double value)
			{
				assert weight >= 0;
				assert !Double.isInfinite(weight) && !Double.isNaN(weight);
				assert !Double.isInfinite(value) && !Double.isNaN(value);

				weightSum += weight;
				sum += weight * value;
			}

			@Override
			public double getAverage()
			{
				double ret = sum / weightSum;
				if (Double.isNaN(ret) || Double.isInfinite(ret)) ret = 0.;
				return ret;
			}

			private double weightSum = 0., sum = 0.;
		};
	}

	@Override
	public String toString()
	{
		return "Mean";
	}
}
