package de.dailab.recommender.average;

/**
 * The mean of the k nearest neighbors. A.k.a. the kNN algorithm.
 * <p>
 * Memory usage: O(k).
 * 
 * @author kunegis
 */
public class NearestNeighbor
    implements Average
{
	/**
	 * @param k Number of neighbors to use.
	 * @param signed Use most similar and most dissimilar neighbors (with negative edges).
	 */
	public NearestNeighbor(int k, boolean signed)
	{
		assert k > 0;

		this.k = k;
		this.signed = signed;
	}

	public AverageRun run()
	{
		return new AverageRun()
		{

			@Override
			public void add(double weight, double value)
			{
				final double effectiveWeight = effective(weight);

				if (fill == k && effective(bestWeights[k - 1]) > effectiveWeight) return;

				int i = 0;
				for (; effective(bestWeights[i]) > effectiveWeight; ++i);

				if (fill < k)
					for (int j = fill++; j > i; --j)
					{
						bestWeights[j] = bestWeights[j - 1];
						bestValues[j] = bestValues[j - 1];
					}
				else
					for (int j = k - 1; j > i; --j)
					{
						bestWeights[j] = bestWeights[j - 1];
						bestValues[j] = bestValues[j - 1];
					}

				bestWeights[i] = weight;
				bestValues[i] = value;
			}

			@Override
			public double getAverage()
			{
				double sumWeight = 0., sumValue = 0.;
				for (int i = 0; i < fill; ++i)
				{
					sumWeight += bestWeights[i];
					sumValue += bestWeights[i] * bestValues[i];
				}

				if (sumWeight == 0.) return sumValue;

				return sumValue / sumWeight;
			}

			/*
			 * Both arrays are in synch, are filled up to FILL, and are sorted from biggest to smallest weight (by
			 * absolute values when signed).
			 */
			private int fill = 0;
			private final double bestWeights[] = new double[k];
			private final double bestValues[] = new double[k];
		};
	}

	@Override
	public String toString()
	{
		return String.format("NearestNeighbor(%d %s)", k, signed ? "s" : "");
	}

	private final int k;
	private final boolean signed;

	/**
	 * Effective weight taking into account SIGNED.
	 */
	private double effective(double weight)
	{
		if (!signed || weight >= 0.) return weight;
		return -weight;
	}
}
