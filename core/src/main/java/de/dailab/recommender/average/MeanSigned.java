package de.dailab.recommender.average;

/**
 * Signed variant of the arithmetic mean.
 * 
 * @author kunegis
 */
public class MeanSigned
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
				weightSum += Math.abs(weight);
				sum += weight * value;
			}

			@Override
			public double getAverage()
			{
				if (weightSum == 0.) return sum;
				return sum / weightSum;
			}

			private double weightSum = 0., sum = 0.;
		};
	}

	@Override
	public String toString()
	{
		return "MeanSigned";
	}
}
