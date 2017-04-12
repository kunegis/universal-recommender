package de.dailab.recommender.average;

import junit.framework.TestCase;

/**
 * Test the generalized mean.
 * 
 * @author kunegis
 */
public class TestGeneralizedMean
    extends TestCase
{
	/**
	 * General test.
	 */
	public void test()
	{
		final AverageRun run = new GeneralizedMean(1.).run();

		run.add(1, 3);
		run.add(1, 4);
		run.add(1, 5);

		assert 1e-1 > Math.abs(run.getAverage() - 4.);
	}

	/**
	 * Test varying weights.
	 */
	public void testWeights()
	{
		final AverageRun run = new GeneralizedMean(1.).run();

		run.add(3, 3);
		run.add(1, 4);
		run.add(1, 5);

		assert 1e-1 > Math.abs(run.getAverage() - 18. / 5);

	}

	/**
	 * Test the root mean square average.
	 */
	public void testRMS()
	{
		final AverageRun run = new GeneralizedMean(2.).run();

		run.add(3, 3);
		run.add(1, 4);
		run.add(1, 5);

		assert 1e-1 > Math.abs(run.getAverage() - Math.sqrt((3 * 9 + 1 * 16 + 1 * 25) / 5));

	}

	/**
	 * The harmonic mean.
	 */
	public void testHarmonic()
	{
		final AverageRun run = new GeneralizedMean(-1).run();

		run.add(3, 3);
		run.add(1, 4);
		run.add(1, 5);

		assert 1e-1 > Math.abs(run.getAverage() - 5. / (1 + .25 + .2));

	}
}
