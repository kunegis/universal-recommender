package de.dailab.recommender.similarity;

/**
 * The sign correlation, i.e. the number of agreeing minus disagreeing ratings, divided by the number of common ratings.
 * This only makes sense for signed edges. Values are in [&minus;1, +1].
 * 
 * @author kunegis
 */
public class SignCorrelation
    implements Similarity
{

	@Override
	public SimilarityRun run()
	{
		return new SimilarityRun()
		{
			private double nxy = 0;
			private double naxy = 0;

			@Override
			public double getSimilarity()
			{
				if (naxy == 0) return 0;
				return nxy / naxy;
			}

			@Override
			public void add(double x, double y, double lambda)
			{
				final double xy = x * y * lambda;

				nxy += xy;
				naxy += Math.abs(xy);
			}
		};
	}

	@Override
	public String toString()
	{
		return "SignCorrelation";
	}

	@Override
	public int hashCode()
	{
		return 0xc04b2ff4;
	}

	@Override
	public boolean equals(Object object)
	{
		return object instanceof SignCorrelation;
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
