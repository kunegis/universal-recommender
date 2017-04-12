package de.dailab.recommender.matrix.template;

import java.util.Random;

import org.junit.Test;

import de.dailab.recommender.matrix.Entry;
import de.dailab.recommender.matrix.FullEntry;
import de.dailab.recommender.matrix.Matrix;

import static java.lang.Math.abs;

/**
 * Test binary matrices.
 * 
 * @author kunegis
 */
public class TestBinaryMatrix
{
	/**
	 * Perform all tests.
	 */
	@Test
	public void testBinaryMatrices()
	{
		testCase(19, 29);
		testCase(29, 237);
		testCase(38, 255);
		testCase(42, 256);
		testCase(100, 257);
		testCase(999, 999);
		testCase(7, 70000);
	}

	private void testCase(int m, int n)
	{
		final Random random = new Random();

		final Matrix matrix = MatrixFactory.newMemoryMatrix(m, n, boolean.class.getSimpleName());

		for (int i = 0; i < m; ++i)
		{
			matrix.set(i, random.nextInt(n), 1 + random.nextDouble());
		}

		int count = 0;
		double sum = 0;

		for (final FullEntry entry: matrix.all())
		{
			++count;
			sum += entry.value;
		}

		assert count == m;
		assert abs(sum - m) < 1e-9;
	}

	private static final int M = 1000, N = 100;

	/**
	 * Test binary matrices.
	 */
	@Test
	public void testSmallBinary()
	{
		final MemoryBinaryMatrixIntInt matrix = new MemoryBinaryMatrixIntInt(20, 1000);

		/* Full */
		matrix.set(0, 0);
		matrix.set(0, 1);
		matrix.set(1, 1);
		matrix.set(1, 2);
		matrix.set(2, 2);
		matrix.set(2, 0);
		matrix.set(2, 1);
		matrix.set(1, 0);
		matrix.set(0, 2);

		/* Permutations */
		matrix.set(10, 100);

		matrix.set(11, 100);
		matrix.set(11, 200);
		matrix.set(12, 200);
		matrix.set(12, 100);

		matrix.set(13, 100);
		matrix.set(13, 200);
		matrix.set(13, 300);
		matrix.set(14, 100);
		matrix.set(14, 300);
		matrix.set(14, 200);
		matrix.set(15, 200);
		matrix.set(15, 100);
		matrix.set(15, 300);
		matrix.set(16, 200);
		matrix.set(16, 300);
		matrix.set(16, 100);
		matrix.set(17, 300);
		matrix.set(17, 100);
		matrix.set(17, 200);
		matrix.set(18, 300);
		matrix.set(18, 200);
		matrix.set(18, 100);

		/* Multiple */
		matrix.set(19, 400);
		matrix.set(19, 400);
		matrix.set(19, 401);
		matrix.set(19, 401);
		matrix.set(19, 399);
		matrix.set(19, 399);

		int rCount = 0;
		for (int i = 0; i < 3; ++i)
		{
			for (@SuppressWarnings("unused")
			final Entry entry: matrix.row(i))
			{
				rCount += 1;
			}
		}

		int cCount = 0;
		for (int i = 0; i < 3; ++i)
		{
			for (@SuppressWarnings("unused")
			final Entry entry: matrix.col(i))
			{
				cCount += 1;
			}
		}

		assert rCount == cCount;
	}

	/**
	 * Test memory binary matrix.
	 */
	@Test
	public void testMemoryBinaryMatrix()
	{
		final MemoryBinaryMatrixCharChar matrix = new MemoryBinaryMatrixCharChar(M, N);

		final Random random = new Random();

		for (int _i = 0; _i < .05 * M * N; ++_i)
		{
			final int i = random.nextInt(M);
			final int j = random.nextInt(N);

			matrix.set(i, j);
		}

		int rCount = 0;
		for (int i = 0; i < M; ++i)
		{
			for (@SuppressWarnings("unused")
			final Entry entry: matrix.row(i))
			{
				rCount += 1;
			}
		}

		int cCount = 0;
		for (int j = 0; j < N; ++j)
		{
			for (@SuppressWarnings("unused")
			final Entry entry: matrix.col(j))
			{
				cCount += 1;
			}
		}

		assert rCount == cCount;
	}

	/**
	 * Test getter.
	 */
	@Test
	public void testGet()
	{
		final int M = 90, N = 150;

		final Matrix matrix = new MemoryBinaryMatrixByteByte(M, N);

		for (int i = 0; i < M; ++i)
			for (int j = 0; j < N; ++j)
				if (f(i, j)) matrix.set(i, j, 1.);

		for (int i = 0; i < M; ++i)
			for (int j = 0; j < N; ++j)
				assert matrix.get(i, j) == (f(i, j) ? 1. : 0.);
	}

	private static boolean f(int i, int j)
	{
		return (i + j + i / 2 + j / 3 + i / 5 + j / 7 + i / 11 + j / 13) % 2 == 0;
	}

	/**
	 * Test multiplication.
	 */
	@Test
	public void testMult()
	{
		final Matrix matrix = new MemoryBinaryMatrixByteByte(3, 2);
		matrix.set(0, 0, 1.);
		matrix.set(0, 1, 1.);
		matrix.set(1, 1, 1.);
		matrix.set(2, 0, 1.);

		final double ret[] = matrix.mult(new double[]
		{ 1., 1.01 }, null, 1.);
		assert ret.length == 3;
		assert ret[0] == 2.01;
		assert ret[1] == 1.01;
		assert ret[2] == 1.;
	}

	/**
	 * Iterate over an empty matrix.
	 */
	@Test
	public void testEmptyIterateAll()
	{
		final Matrix matrix = new MemoryBinaryMatrixByteChar(9, 18);
		for (@SuppressWarnings("unused")
		final FullEntry entry: matrix.all())
		{
			assert false;
		}
	}
}
