package de.dailab.recommender.evaluation.predictionerror;

/**
 * The root mean squared error.
 * 
 * @author kunegis
 */
public class Rmse
    implements PredictionError
{
	@Override
	public PredictionErrorRun run()
	{
		return new PredictionErrorRun()
		{
			private int count = 0;
			private double squareSum = 0;

			@Override
			public void add(double value, double prediction)
			{
				assert !Double.isNaN(value) && !Double.isInfinite(value);
				assert !Double.isNaN(prediction) && !Double.isInfinite(prediction);

				final double diff = Math.abs(value - prediction);

				++count;
				squareSum += diff * diff;
			}

			@Override
			public double get()
			{
				return Math.sqrt(squareSum / count);
			}
		};
	}

	@Override
	public String toString()
	{
		return "RMSE";
	}
}
