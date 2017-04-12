package de.dailab.recommender.matrix.template;

import java.util.Iterator;

import org.junit.Test;

import de.dailab.recommender.matrix.Matrix;

/**
 * Test getRows() and getCols().
 * 
 * @author kunegis
 */
public class TestGetRows
{
	/**
	 * Test getRows().
	 */
	@Test
	public void testGetRows()
	{
		final int N = 9;

		final Matrix matrix = new MemoryMatrixByteByteByte(N, N);

		matrix.set(2, 3, 1);
		matrix.set(2, 4, -1);
		matrix.set(7, 0, 2);

		final Iterator <Integer> i = matrix.getRows().iterator();
		assert i.hasNext();
		final int row0 = i.next();
		assert i.hasNext();
		final int row1 = i.next();
		assert !i.hasNext();
		assert (row0 == 2 && row1 == 7) || (row0 == 7 && row1 == 2);
	}
}
