package de.dailab.recommender.similarity;

import de.dailab.recommender.function.Function;

/**
 * Similarity measures based on the distance.
 * <p>
 * As a similarity measure, compute a decreasing function of the squared Euclidean distance between two vectors.
 * <p>
 * Additionally, indefinite similarities ("negative squared distances") are supported in various ways, depending on the
 * mode.
 * 
 * @author kunegis
 */
public class DistanceBasedSimilarity
    implements Similarity
{
	/**
	 * How indefinite similarities (negative lambdas) are handled.
	 * 
	 * @author kunegis
	 */
	public enum Mode
	{
		/**
		 * Unsigned similarity. Apply the function to negative values.
		 */
		UNSIGNED,

		/**
		 * Signed similarity. Apply the function to the absolute value of the squared Euclidean distance and multiply
		 * the result with it's sign.
		 */
		SIGNED,

		/**
		 * Symmetric similarity. Apply the function to the absolute value of the squared Euclidean distance.
		 */
		SYMMETRIC;
	}

	/**
	 * Similarity is a function applied to the squared Euclidean distance, which can be signed.
	 * 
	 * @param function The function applied to the squared distance, giving the similarity
	 * @param mode The mode
	 */
	public DistanceBasedSimilarity(Function function, Mode mode)
	{
		this.function = function;
		this.mode = mode;

		name = buildName();
	}

	public SimilarityRun run()
	{
		return new SimilarityRun()
		{

			@Override
			public void add(double x, double y, double lambda)
			{
				squareDiffSum += (x - y) * (x - y) * lambda;
			}

			@Override
			public double getSimilarity()
			{
				final double v = squareDiffSum;

				double ret;

				switch (mode)
				{
				default:
					assert false;
				case UNSIGNED:
					ret = function.apply(v);
				case SIGNED:
					ret = Math.abs(v) * function.apply(Math.abs(v));
				case SYMMETRIC:
					ret = function.apply(Math.abs(v));
				}

				return ret;
			}

			private double squareDiffSum = 0.;
		};
	}

	@Override
	public String toString()
	{
		return name;
	}

	private final Function function;
	private final Mode mode;

	private final String name;

	private String buildName()
	{
		return "DistanceBased" + ("" + mode.name().charAt(2)).toLowerCase() + ":" + function;
	}

	@Override
	public boolean isSpectral()
	{
		return false;
	}

	@Override
	public double[] transformSpectrum(double[] lambda)
	    throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException();
	}
}
