package de.dailab.recommender.genericmatrix;

import java.util.Random;

import org.junit.Test;

/**
 * Test generic memory vectors.
 * 
 * @author kunegis
 */
public class TestGenericMemoryVector
{
	/**
	 * Test creation of memory vectors.
	 */
	@Test
	public void testCreation()
	{
		final GenericMemoryVector <Short, Byte> vector = new GenericMemoryVector <Short, Byte>(short.class, byte.class);

		final Random random = new Random();

		for (int i = 0; i < 1000; ++i)
		{
			final short index = (short) random.nextInt();
			final byte value = (byte) (1 + random.nextInt(5));

			vector.set(index, value);
		}

		int indexSum = 0;
		int valueSum = 0;

		for (final Entry <Short, Byte> entry: vector)
		{
			indexSum += entry.index;
			valueSum += entry.value;
		}
		System.out.printf("index sum = %s\nvalue sum = %s\n", indexSum, valueSum);
	}

	/**
	 * Test matrix-vector multiplication.
	 */
	@Test
	public void testMult()
	{
		final GenericMemoryVector <Byte, Byte> vector = new GenericMemoryVector <Byte, Byte>(byte.class, byte.class);

		vector.set((byte) 3, (byte) 1);
		vector.set((byte) 4, (byte) 2);
		vector.set((byte) 6, (byte) 4);

		final double x[] = new double[]
		{ .0, .1, .2, .3, .4, .5, .6, .7 };

		final double c = vector.mult(x);

		assert Math.abs(c - 3.5) < 1e-8;
	}
}
