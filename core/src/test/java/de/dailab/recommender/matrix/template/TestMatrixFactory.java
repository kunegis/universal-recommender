package de.dailab.recommender.matrix.template;

import org.junit.Test;

import de.dailab.recommender.matrix.FullEntry;
import de.dailab.recommender.matrix.Matrix;

import static java.lang.Math.abs;

/**
 * Test the matrix factory.
 * 
 * @author kunegis
 */
public class TestMatrixFactory
{
	/**
	 * Test matrix construction.
	 */
	@Test
	public void testMatrixFactory()
	{
		@SuppressWarnings("unused")
		final Matrix matrix = MatrixFactory.newMemoryMatrix(480189, 17770, byte.class.getSimpleName());
	}

	/**
	 * Test that the default value type is a floating poitn type.
	 */
	@Test
	public void testDefaultValueType()
	{
		final Matrix matrix = MatrixFactory.newMemoryMatrix(9, 9);

		matrix.set(5, 5, .555);

		final double x = matrix.get(5, 5);

		assert abs(x - .555) < 1e-6;
	}

	/**
	 * Test copying matrix attributes.
	 */
	@Test
	public void testCopy()
	{
		final Matrix matrix = new MemoryMatrixByteCharShort(9, 18);

		matrix.set(5, 5, -1);

		final Matrix matrix2 = MatrixFactory.newMemoryMatrix(matrix);

		assert matrix != matrix2;
		assert matrix2.getClass().equals(MemoryMatrixByteCharShort.class);
		assert matrix.rows() == matrix2.rows();
		assert matrix.cols() == matrix2.cols();

		int count = 0;
		for (@SuppressWarnings("unused")
		final FullEntry entry: matrix2.all())
			++count;
		assert count == 0;
	}
}
