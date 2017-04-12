package de.dailab.recommender.matrix.template;

import org.junit.Test;

import de.dailab.recommender.matrix.Matrix;

/**
 * Test the method Matrix.nnz().
 * 
 * @author kunegis
 */
public class TestNnz
{
	/**
	 * Test nnz() in a weighted matrix.
	 */
	@Test
	public void testFloat()
	{
		final Matrix matrix = MatrixFactory.newMemoryMatrix(5);

		/* The index 2 is not used */
		matrix.set(0, 0, +1);
		matrix.set(0, 1, -1);
		matrix.set(1, 1, +1);
		matrix.set(1, 3, +1);
		matrix.set(3, 3, -1);
		matrix.set(3, 4, +1);
		matrix.set(4, 4, -1);

		final int nnz = matrix.nnz();

		assert nnz == 7;
	}

	/**
	 * Test nnz() in an unweighted matrix.
	 */
	@Test
	public void testUnweighted()
	{
		final Matrix matrix = MatrixFactory.newMemoryMatrixUnweighted(5);

		/* The index 2 is not used */
		matrix.set(0, 0, 1);
		matrix.set(0, 1, 1);
		matrix.set(1, 1, 1);
		matrix.set(1, 3, 1);
		matrix.set(3, 3, 1);
		matrix.set(3, 4, 1);
		matrix.set(4, 4, 1);

		final int nnz = matrix.nnz();

		assert nnz == 7;
	}
}
