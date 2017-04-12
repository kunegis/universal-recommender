package de.dailab.recommender.matrix.template;

import java.util.Random;

import org.junit.Test;

import de.dailab.recommender.matrix.Matrix;

/**
 * Test size getters.
 * 
 * @author kunegis
 */
public class TestSize
{
	/**
	 * Test on a static matrix.
	 */
	@Test
	public void testSize()
	{
		final Matrix matrix = new MemoryBinaryMatrixByteByte(10, 9);
		matrix.set(0, 0, 1);
		matrix.set(0, 4, 1);
		matrix.set(1, 2, 1);
		matrix.set(3, 1, 1);
		matrix.set(3, 4, 1); /* this is a duplicate on purpose */
		matrix.set(3, 4, 1);
		matrix.set(4, 8, 1);
		matrix.set(4, 7, 1);
		matrix.set(4, 6, 1);
		matrix.set(4, 5, 1);
		matrix.set(5, 4, 1);
		matrix.set(5, 5, 1);
		matrix.set(5, 3, 1);
		matrix.set(5, 6, 1);
		matrix.set(5, 2, 1);
		matrix.set(9, 1, 1);
		matrix.set(9, 2, 1);
		matrix.set(9, 3, 1);

		assert matrix.getRowCount(0) == 2;
		assert matrix.getRowCount(1) == 1;
		assert matrix.getRowCount(2) == 0;
		assert matrix.getRowCount(3) == 2;
		assert matrix.getRowCount(4) == 4;
		assert matrix.getRowCount(5) == 5;
		assert matrix.getRowCount(6) == 0;
		assert matrix.getRowCount(7) == 0;
		assert matrix.getRowCount(8) == 0;
		assert matrix.getRowCount(9) == 3;

		assert matrix.getColCount(0) == 1;
		assert matrix.getColCount(1) == 2;
		assert matrix.getColCount(2) == 3;
		assert matrix.getColCount(3) == 2;
		assert matrix.getColCount(4) == 3;
		assert matrix.getColCount(5) == 2;
		assert matrix.getColCount(6) == 2;
		assert matrix.getColCount(7) == 1;
		assert matrix.getColCount(8) == 1;
	}

	/**
	 * Test on a random sparse matrix.
	 */
	@Test
	public void testRandom()
	{
		final int N = 100, D = 10, R = N * D;

		final Matrix matrix = new MemoryBinaryMatrixIntInt(N, N);
		final Random random = new Random();
		for (int r = 0; r < R;)
		{
			final int i = random.nextInt(N);
			final int j = random.nextInt(N);
			if (matrix.get(i, j) != 0) continue;
			matrix.set(i, j, 1);
			++r;
		}

		int rowR = 0;
		int colR = 0;

		for (int i = 0; i < matrix.rows(); ++i)
		{
			rowR += matrix.getRowCount(i);
			colR += matrix.getColCount(i);
		}

		assert rowR == R;
		assert colR == R;
	}
}
