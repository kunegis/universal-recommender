package de.dailab.recommender.path;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.DatasetEntry;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.SimpleUnipartiteDataset;
import de.dailab.recommender.matrix.Matrix;
import de.dailab.recommender.random.RandomGraph;
import de.dailab.recommender.recommendation.Recommendation;
import de.dailab.recommender.recommendation.RecommendationUtils;

/**
 * Test performance of the AllPath on large networks.
 * 
 * @author kunegis
 */
public class TestAllPathLarge
{
	/**
	 * Run the all-path on a large random graph. This must finish in a timely fashion.
	 * <p>
	 * Bug observed in the UCPM actor recommender: the path gets stuck in an endless recursion.
	 */
	@Test
	public void test()
	{
		final int n = 500;
		final double p = 0.05;

		final Dataset dataset = new RandomGraph(n, p);

		final Path path = new AllPath2();

		final Map <Entity, Set <DatasetEntry>> trail = new HashMap <Entity, Set <DatasetEntry>>();

		final Iterator <Recommendation> iterator = path.recommend(dataset, new Entity(RandomGraph.ENTITY_TYPE, 0),
		    trail);

		final List <Recommendation> recommendations = RecommendationUtils.read(iterator, n);

		assert recommendations.size() >= n / 2;
	}

	/**
	 * Create a 1000 by 1000 complete graph and let the all path with large max-visit parameter run on it.
	 */
	@Test
	public void testScalability()
	{
		final int n = 500;

		final SimpleUnipartiteDataset dataset = new SimpleUnipartiteDataset(n);

		final Matrix matrix = dataset.getMatrix();

		for (int i = 0; i < n; ++i)
			for (int j = i + 1; j < n; ++j)
				matrix.set(i, j, 1);

		final Path path = new AllPath2();

		final Map <Entity, Set <DatasetEntry>> trail = new HashMap <Entity, Set <DatasetEntry>>();

		final Iterator <Recommendation> iterator = path.recommend(dataset,
		    new Entity(SimpleUnipartiteDataset.ENTITY, 0), trail);

		for (int i = 0; iterator.hasNext() && i < 2 * n; ++i)
		{
			@SuppressWarnings("unused")
			final Recommendation recommendation = iterator.next();

			System.out.print(".");
		}
	}
}
