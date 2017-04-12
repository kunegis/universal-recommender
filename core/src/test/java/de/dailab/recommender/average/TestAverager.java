package de.dailab.recommender.average;

import junit.framework.TestCase;

/**
 * Test averagers.
 * 
 * @author kunegis
 */
public class TestAverager
    extends TestCase
{
	/**
	 * Test the nearest neighbor with positive weights.
	 */
	public void testUnsigned()
	{
		final Average average = new NearestNeighbor(11, false);

		final AverageRun averagerRun = average.run();

		for (int i = 0; i < 9; ++i)
			averagerRun.add(2.5, 1.);

		for (int i = 0; i < 9; ++i)
			averagerRun.add(1.5, .2);

		final double avg = averagerRun.getAverage();

		assert avg == (1. * 2.5 * 9 + .2 * 1.5 * 2) / (9 * 2.5 + 2 * 1.5);
	}

	/**
	 * Test the nearest neighbor with signed weights.
	 */
	public void testSigned()
	{
		final Average average = new NearestNeighbor(17, true);

		final AverageRun averagerRun = average.run();

		for (int i = 0; i < 7; ++i)
		{
			averagerRun.add(2., -1.7);
			averagerRun.add(-1., +2.);
			averagerRun.add(.5, +2.3);
		}

		final double avg = averagerRun.getAverage();

		assert 1e-10 > Math.abs(avg - (-1.7 * 2. * 7 + +2. * -1. * 7 + +2.3 * .5 * 3) / (7 * 2. + 7 * -1 + 3 * .5));
	}

	/**
	 * Test the nearest neighbor average.
	 */
	public void testPartial()
	{
		final Average average = new NearestNeighbor(6, false);

		final AverageRun averageRun = average.run();
		averageRun.add(1., 1.);
		averageRun.add(3., 3.);
		averageRun.add(5., 5.);
		averageRun.add(4., 4.);
		averageRun.add(2., 2.);

		final double avg = averageRun.getAverage();

		assert 1e-10 > Math.abs(11. / 3 - avg);

	}

	/**
	 * Test reuse of an Average object.
	 */
	public void testReuse()
	{
		final Average average = new NearestNeighbor(3, false);

		for (int i = 0; i < 9; ++i)
		{
			final AverageRun averageRun = average.run();

			averageRun.add(1., 1.);
			averageRun.add(2., .5);

			final double avg = averageRun.getAverage();

			assert 1e-10 > Math.abs(avg - 2. / 3);
		}
	}
}
