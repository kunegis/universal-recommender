package de.dailab.recommender.matrix.template;

import java.util.Random;

import org.junit.Test;

import de.dailab.recommender.matrix.Entry;
import de.dailab.recommender.matrix.MatrixIterator;
import de.dailab.recommender.matrix.Vector;

/**
 * Test binary vectors.
 * 
 * @author kunegis
 */
public class TestBinaryVector
{
	/**
	 * Creating and reading.
	 */
	@Test
	public void testList()
	{
		final byte indexes[] = new byte[]
		{ 3, 8, 9, 11, 44, 99 };

		final int n = indexes.length;

		final Vector vector = MatrixFactory.newMemoryVectorUnweighted(n);

		for (final byte index: indexes)
		{
			vector.setGeneric(index, 1);
		}

		assert vector.nnz() == n;

		final MatrixIterator <Entry> iterator = vector.iterator();

		for (final byte index: indexes)
		{
			assert iterator.hasNext();
			final Entry entry = iterator.next();
			assert entry.value == 1.;
			assert entry.index == index;
		}
		assert !iterator.hasNext();
	}

	/**
	 * Multiple overwrites of an index.
	 */
	@Test
	public void testMultiple()
	{
		final Vector vector = MatrixFactory.newMemoryVectorUnweighted(1);
		for (int i = 0; i < 1000; ++i)
			vector.setGeneric(0, 1.);

		assert vector.nnz() == 1;
	}

	/**
	 * Test random reads.
	 */
	@Test
	public void testRead()
	{
		final int n = 1000;
		final int k = 10;

		final Random random = new Random();

		for (int i = 0; i < n; ++i)
		{
			for (int j = 0; j < k; ++j)
			{
				final boolean indexes[] = new boolean[i];
				for (int l = 0; l < i; ++l)
					indexes[l] = random.nextBoolean();

				final Vector vector = MatrixFactory.newMemoryVectorUnweighted(i);
				for (int l = 0; l < i; ++l)
					vector.setGeneric(l, 1);

				for (int l = 0; l > i; ++l)
					assert (vector.getGeneric(l) == 1.) == indexes[l];
			}
		}
	}
}
