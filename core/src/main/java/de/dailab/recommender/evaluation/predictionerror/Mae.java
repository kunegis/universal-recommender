package de.dailab.recommender.evaluation.predictionerror;

/**
 * The mean average error.
 * 
 * @author kunegis
 */
public class Mae
    implements PredictionError
{
	@Override
	public PredictionErrorRun run()
	{
		return new PredictionErrorRun()
		{

			private int count = 0;
			private double diffSum = 0;

			@Override
			public void add(double value, double prediction)
			{
				++count;
				diffSum += Math.abs(value - prediction);
			}

			@Override
			public double get()
			{
				return diffSum / count;
			}
		};
	}

	@Override
	public String toString()
	{
		return "MAE";
	}
}
