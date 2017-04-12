package de.dailab.recommender.neighborhood;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import de.dailab.recommender.similarity.ScalarProduct;

/**
 * Test neighborhood finders.
 * 
 * @author kunegis
 */
public class TestNeighborhoodFinder
{
	/**
	 * Test neighborhood finders.
	 * 
	 * @throws UnsupportedSimilarityException unsupported similarities
	 */
	@Test
	public void testNeighborhoodFinders()
	    throws UnsupportedSimilarityException
	{
		final int rank = 5;
		final int n = 10000;

		final double lambda[] = new double[rank];
		final double u[][] = new double[rank][n];

// final EntityType entityType = new EntityType("point");

		final Random random = new Random();

		/* Generate data */
		for (int i = 0; i < rank; ++i)
		{
			lambda[i] = random.nextGaussian();
			for (int j = 0; j < n; ++j)
				u[i][j] = random.nextGaussian();
		}

		/* Create finders and models */
		final NeighborhoodFinder neighborhoodFinders[] = new NeighborhoodFinder[]
		{ new FullNeighborhoodFinder(), new ClusteringNeighborhoodFinder() };

		final List <NeighborhoodFinderModel> neighborhoodFinderModels = new ArrayList <NeighborhoodFinderModel>();
		for (final NeighborhoodFinder neighborhoodFinder: neighborhoodFinders)
		{
			System.out.printf("Building %s...\n", neighborhoodFinder);
			final long begin = System.currentTimeMillis();
			neighborhoodFinderModels.add(neighborhoodFinder.build(new ScalarProduct(), lambda, u));
			System.out.printf("\ttime = %s\n", System.currentTimeMillis() - begin);
		}

		for (int i = 0; i < 5; ++i)
		{
			System.out.printf("Vector #%d\n", i);

			/* Random vector */
			final double vector[] = new double[rank];
			for (int j = 0; j < rank; ++j)
				vector[j] = random.nextGaussian();

			for (final NeighborhoodFinderModel neighborhoodFinderModel: neighborhoodFinderModels)
			{
				System.out
				    .printf("Finding neighborhood using %s...\n", neighborhoodFinderModel.getNeighborhoodFinder());

				final long begin = System.currentTimeMillis();
				final Iterator <WeightedPoint> iterator = neighborhoodFinderModel.findContinuous(vector);
				for (int j = 0; j < 20; ++j)
				{
					if (!iterator.hasNext()) break;
					final WeightedPoint weightedPoint = iterator.next();
					System.out.println(weightedPoint);
				}
				System.out.printf("\ttime = %s\n", System.currentTimeMillis() - begin);
			}
		}
	}
}
