package de.dailab.recommender.neighborhood;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

import de.dailab.recommender.similarity.ScalarProduct;
import de.dailab.recommender.similarity.Similarity;

/**
 * A fast and imprecise neighborhood finder that uses clustering.
 * <p>
 * Algorithm: For each latent dimension, produce a list of "positive" and "negative" points. To find the nearest points
 * to a point, first look at all point in the same quadrant as the given point, and then iteratively relax the
 * constraint beginning at the dimension with smallest singular value.
 * <p>
 * This neighborhood model only returns entities that match sign in at least one latent dimension.
 * <p>
 * This class only supports the ScalarProduct similarity.
 * 
 * @author kunegis
 */
public class ClusteringNeighborhoodFinder
    implements NeighborhoodFinder
{
	@Override
	public NeighborhoodFinderModel build(Similarity similarity, final double[] lambda, final double[][] u)
	    throws UnsupportedSimilarityException
	{
		if (!similarity.isSpectral())
// if (!similarity.equals(new ScalarProduct()))
		    throw new UnsupportedSimilarityException(String.format("%s only supports spectral similaritiess", this
		        .getClass().getSimpleName(), ScalarProduct.class.getSimpleName()));

		return new ClusteringNeighborhoodFinderModel(similarity.transformSpectrum(lambda), u);
	}

	private class ClusteringNeighborhoodFinderModel
	    implements NeighborhoodFinderModel
	{
		private final double lambda[];
		private final double u[][];

		private int rank, n;

		public ClusteringNeighborhoodFinderModel(final double[] lambda, final double[][] u)
		{
			this.lambda = lambda;
			this.u = u;
			update();
		}

		@Override
		public void update()
		{
			rank = lambda.length;
			assert u.length == rank;
			assert rank > 0;
			n = u[0].length;

			/*
			 * Build clusters of points
			 */
			bins = new PointSet[2][rank];
			for (int i = 0; i < rank; ++i)
			{
				bins[POS][i] = new PointSet();
				bins[NEG][i] = new PointSet();
				for (int j = 0; j < n; ++j)
					bins[u[i][j] > 0 ? POS : NEG][i].add(j);
			}
		}

		@Override
		public Iterator <WeightedPoint> findContinuous(final double[] vector)
		{
			/*
			 * Iterate over each latent dimension in the order given by VECTOR. Return only those points that agree with
			 * VECTOR in that dimension.
			 */

			/* Sort elements in the vector */
			final Integer indexes[] = new Integer[rank];
			for (int i = 0; i < rank; ++i)
				indexes[i] = i;
			Arrays.sort(indexes, new Comparator <Integer>()
			{
				@Override
				public int compare(Integer o1, Integer o2)
				{
					return -Double.compare(Math.abs(lambda[o1] * vector[o1]), Math.abs(lambda[o2] * vector[o2]));
				}
			});

			return new Iterator <WeightedPoint>()
			{
				/** Points already returned */
				private final PointSet returned = new PointSet();
				/** Current dimension */
				private int nextBin = 0;
				/** Current point iterator */
				private Iterator <Integer> pointIterator = null;

				@Override
				public boolean hasNext()
				{
					if (nextWeightedPoint == null)
					{
						while (nextBin < rank)
						{
							if (pointIterator == null)
							    pointIterator = bins[lambda[indexes[nextBin]] * vector[indexes[nextBin]] > 0 ? POS
							        : NEG][indexes[nextBin]].iterator();
							if (!pointIterator.hasNext())
							{
								pointIterator = null;
								++nextBin;
							}
							else
							{
								final int point = pointIterator.next();

								if (returned.contains(point)) continue;

								double score = 0;
								for (int i = 0; i < rank; ++i)
									score += u[indexes[i]][point] * lambda[indexes[i]] * vector[indexes[i]];

								nextWeightedPoint = new WeightedPoint(point, score);
								returned.add(point);
								break;
							}
						}
					}
					return nextWeightedPoint != null;
				}

				@Override
				public WeightedPoint next()
				{
					if (!hasNext()) throw new NoSuchElementException();
					final WeightedPoint ret = nextWeightedPoint;
					nextWeightedPoint = null;
					return ret;
				}

				@Override
				public void remove()
				{
					throw new UnsupportedOperationException();
				}

				/**
				 * Prefetched.
				 */
				private WeightedPoint nextWeightedPoint = null;
			};
		}

		@Override
		public NeighborhoodFinder getNeighborhoodFinder()
		{
			return ClusteringNeighborhoodFinder.this;
		}

		/**
		 * The bins of points by positive/negative kth eigenvector component.
		 */
		private PointSet bins[/* pos, neg */][/* k */];
	}

	private final static int POS = 0, NEG = 1;

	/**
	 * A set of points (represented by ints).
	 * 
	 * @author kunegis
	 */
	private static class PointSet
	    extends HashSet <Integer>
	{}

	@Override
	public String toString()
	{
		return "Clustering";
	}
}
