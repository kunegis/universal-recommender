package de.dailab.recommender.matrix.template;

import java.util.Random;

import org.junit.Test;

import de.dailab.recommender.matrix.Entry;

import static java.lang.Math.abs;

/**
 * Test template memory vectors.
 * 
 * @author kunegis
 */
public class TestVector
{
	/**
	 * Test memory vector.
	 */
	@Test
	public void testMemoryVector()
	{
		final MemoryVectorCharFloat vector = new MemoryVectorCharFloat();

		vector.set((char) 3, .3f);
		vector.set((char) 7, .5f);
		vector.set((char) 4, .7f);

		vector.add((char) 7, .1f);

		double sum = 0;

		for (final Entry entry: vector)
		{
			final double value = entry.value;

			sum += value;
		}

		assert abs(sum - 1.6) < 1e-6;
	}

	/**
	 * Test large memory vector.
	 */
	@Test
	public void testLarge()
	{
		final int SIZE = 5000;

		final MemoryVectorCharFloat vector = new MemoryVectorCharFloat();

		for (int i = SIZE - 1; i >= 0; --i)
		{
			vector.set((char) i, (float) Math.random());
		}

		assert vector.nnz() == SIZE;
	}

	/**
	 * Test the short-byte combination.
	 */
	@Test
	public void testShortByte()
	{
		final MemoryVectorCharByte vector = new MemoryVectorCharByte();

		final Random random = new Random();

		for (int i = 0; i < 100000; ++i)
		{
			final short index = (short) (random.nextInt());
			final byte value = (byte) (random.nextInt());
			vector.add((char) index, value);
		}

		final int counts[] = new int[0x100];

		for (final Entry entry: vector)
		{
			++counts[((byte) (int) (entry.value)) & 0xff];
		}

		for (int i = 0; i < 0x100; ++i)
			System.out.print(counts[i] + " ");
		System.out.println();
	}

	/**
	 * Test the int-byte combination.
	 */
	@Test
	public void testIntByte()
	{
		final MemoryVectorIntByte vector = new MemoryVectorIntByte();

		final Random random = new Random();

		for (int i = 0; i < 10000; ++i)
		{
			final int index = random.nextInt();
			final byte value = (byte) (random.nextInt());
			vector.add(index, value);
		}

		int count = 0;
		double sum = 0;
		for (final Entry entry: vector)
		{
			++count;
			sum += entry.value;
		}

		assert count == vector.nnz();

		System.out.printf("size = %s, sum = %s\n", count, sum);
	}

	/**
	 * Another test.
	 */
	@Test
	public void testMemoryVector2()
	{
		final MemoryVectorByteFloat memoryVector = new MemoryVectorByteFloat();

		memoryVector.set((byte) 11, .11f);
		memoryVector.set((byte) 22, .22f);
		memoryVector.set((byte) 15, .15f);
		memoryVector.set((byte) 5, .05f);
		memoryVector.set((byte) 25, .25f);

		double sum = 0.;
		for (final Entry entry: memoryVector)
		{
			sum += entry.value;
		}

		assert Math.abs(sum - .78) < 1e-6;
	}

}
