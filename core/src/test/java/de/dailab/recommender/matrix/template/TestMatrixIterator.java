package de.dailab.recommender.matrix.template;

import org.junit.Test;

import de.dailab.recommender.matrix.FullEntry;
import de.dailab.recommender.matrix.Matrix;
import de.dailab.recommender.matrix.MatrixIterator;

/**
 * Test setting values using a matrix iterator.
 * 
 * @author kunegis
 */
public class TestMatrixIterator
{
	/**
	 * Build a matrix and modify all entries.
	 */
	@Test
	public void test()
	{
		final Matrix matrix = MatrixFactory.newMemoryMatrix(9, 9);

		matrix.set(0, 0, 1.); /* 2, 4 */
		matrix.set(2, 1, 2.); /* 5, 25 */
		matrix.set(2, 2, 3.); /* 10, 100 */
		matrix.set(3, 7, 4.); /* 17, 289 */
		matrix.set(3, 5, 5.); /* 26, 676 */

		/* Apply the function (x*x+1) to all entries */
		for (final MatrixIterator <FullEntry> i = matrix.all().iterator(); i.hasNext();)
		{
			final FullEntry entry = i.next();
			i.set(entry.value * entry.value + 1);
		}

		int count = 0;
		double sum = 0;
		double sumSquare = 0;
		for (final FullEntry entry: matrix.all())
		{
			++count;
			sum += entry.value;
			sumSquare += entry.value * entry.value;
		}

		assert count == 5;
		assert sum == 60;
		assert sumSquare == 1094;
	}
}
