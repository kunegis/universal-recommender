package de.dailab.recommender.similarity;

/**
 * Variant of the scalar product ignoring negative-negative pairs.
 * 
 * @author kunegis
 */
public class PositiveScalarProduct
    implements SimilarityRun
{
	@Override
	public void add(double a, double b, double lambda)
	{
		if (a < 0 && b < 0) return;

		sum += a * b * lambda;
	}

	@Override
	public double getSimilarity()
	{
		return sum;
	}

	private double sum = 0;
}
