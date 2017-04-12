package de.dailab.recommender.matrix.template;

import java.util.Random;

import org.junit.Test;

import de.dailab.recommender.matrix.Entry;
import de.dailab.recommender.matrix.FullEntry;
import de.dailab.recommender.matrix.Matrix;

/**
 * Test primitive template matrices.
 * 
 * @author kunegis
 */
public class TestMatrix
{
	/**
	 * Test the int/short/byte combination, which corresponds to Netflix.
	 */
	@Test
	public void testIntCharByte()
	{
		/* Sizes are about those of Netflix */
		final int M = 480189;
		final int N = 17770;

		/* 10% of Netflix for reasonable test runtime */
		final int R = 1000000;

		/*
		 * Expected memory usage is R * (4 + 2 + 2 * 1).
		 */

		final Random random = new Random();

		final Matrix matrix = new MemoryMatrixIntCharByte(M, N);

		for (int i = 0; i < R; ++i)
		{
			if (i % 10000000 == 0) System.out.println();
			if (i % 1000000 == 0) System.out.print(i / 1000000 + " ");

			final int user = random.nextInt(M);
			final int item = random.nextInt(N);

			final int rating = 1 + i % 5;

			matrix.set(user, item, rating);
		}

		System.out.println();

		int count = 0;
		double sum = 0;

		for (final FullEntry entry: matrix.all())
		{
			++count;
			sum += entry.value;
		}

		System.out.printf("count = %s\nmean rating = %s\n", count, sum / count);
	}

	private final static int M = 6000;
	private final static int N = 300;

	/**
	 * Test memory matrix.
	 */
	@Test
	public void testMemoryMatrix()
	{
		final Matrix matrix = new MemoryMatrixCharCharFloat(M, N);

		final Random random = new Random();

		for (int _i = 0; _i < .05 * M * N; ++_i)
		{
			final int i = random.nextInt(M);
			final int j = random.nextInt(N);

			matrix.set(i, j, (float) random.nextGaussian());
		}

		int rCount = 0;
		double rSquareSum = 0;
		for (int i = 0; i < M; ++i)
		{
			for (final Entry entry: matrix.row(i))
			{
				rCount += 1;
				rSquareSum += entry.value * entry.value;
			}
		}

		int cCount = 0;
		double cSquareSum = 0;
		for (int i = 0; i < M; ++i)
		{
			for (final Entry entry: matrix.row(i))
			{
				cCount += 1;
				cSquareSum += entry.value * entry.value;
			}
		}

		assert rCount == cCount;
		assert rSquareSum == cSquareSum;

		System.out.printf("count = %s\n", rCount);
	}

	/**
	 * Test getter.
	 */
	@Test
	public void testGet()
	{
		final int M = 90, N = 150;

		final Matrix matrix = new MemoryMatrixByteByteFloat(M, N);

		for (int i = 0; i < M; ++i)
			for (int j = 0; j < N; ++j)
				matrix.set(i, j, f(i, j));

		for (int i = 0; i < M; ++i)
			for (int j = 0; j < N; ++j)
			{
				final double v = matrix.get(i, j);
				final double f = f(i, j);
				final double diff = Math.abs(v - f);
				assert diff < 1e-4;
			}
	}

	/**
	 * Test multiplication.
	 */
	@Test
	public void testMult()
	{
		final Matrix matrix = new MemoryMatrixByteByteShort(3, 2);
		matrix.set(0, 0, 1);
		matrix.set(0, 1, 2);
		matrix.set(1, 0, 3);
		matrix.set(1, 1, 5);
		matrix.set(2, 0, 7);
		matrix.set(2, 1, 11);

		double ret[] = matrix.mult(new double[]
		{ .1, 1. }, null, 1.);
		assert ret.length == 3;
		assert ret[0] == 2.1;
		assert ret[1] == 5.3;
		assert ret[2] == 11.7;

		ret = matrix.mult(new double[]
		{ 2., .2 }, ret, 1.);
		assert ret.length == 3;
		assert ret[0] == 4.5;
		assert ret[1] == 12.3;
		assert ret[2] == 27.9;
	}

	private static double f(int i, int j)
	{
		return i + j * 1.2365173512312;
	}

	/**
	 * Test iteration over all entries.
	 */
	@Test
	public void testIterateAll()
	{
		final Matrix matrix = new MemoryMatrixByteCharFloat(5, 3);
		matrix.set(0, 0, 1.);
		matrix.set(1, 1, 2.);
		matrix.set(3, 1, 3.);
		matrix.set(3, 2, 4.);
		matrix.set(4, 1, 5.);
		matrix.set(4, 0, 6.);

		int count = 0;
		double sum = 0.;
		double squareSum = 0.;
		for (final FullEntry fullEntry: matrix.all())
		{
			++count;
			sum += fullEntry.value;
			squareSum += fullEntry.value * fullEntry.value;
		}

		assert count == 6;
		assert sum == 21.;
		assert squareSum == 91.;
	}

	/**
	 * Iterate over an empty matrix.
	 */
	@Test
	public void testEmptyIterateAll()
	{
		final Matrix matrix = new MemoryMatrixByteCharShort(9, 18);
		for (@SuppressWarnings("unused")
		final FullEntry entry: matrix.all())
		{
			assert false;
		}
	}
}
