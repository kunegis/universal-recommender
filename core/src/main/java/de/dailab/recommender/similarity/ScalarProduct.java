package de.dailab.recommender.similarity;

/**
 * The scalar product between two vectors.
 * <p>
 * As an unweighted graph node similarity measure, this counts the number of common neighbors, weighted by eigenvalues.
 * As a signed graph node similarity measure, this gives the number of agreeing ratings of neighbors minus the number of
 * disagreeing ratings of neighbors.
 * 
 * @author kunegis
 */
public class ScalarProduct
    implements Similarity
{
	@Override
	public SimilarityRun run()
	{
		return new SimilarityRun()
		{
			@Override
			public void add(double a, double b, double lambda)
			{
				sum += a * b * lambda;
			}

			@Override
			public double getSimilarity()
			{
				return sum;
			}

			private double sum = 0;
		};
	}

	@Override
	public int hashCode()
	{
		/*
		 * There is just one scalar product.
		 */
		return 238904239;
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof ScalarProduct;
	}

	@Override
	public String toString()
	{
		return "ScalarProduct";
	}

	@Override
	public boolean isSpectral()
	{
		return true;
	}

	@Override
	public double[] transformSpectrum(double[] lambda)
	    throws UnsupportedOperationException
	{
		return lambda;
	}
}
