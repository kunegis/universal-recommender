package de.dailab.recommender.genericmatrix;

import org.junit.Test;

/**
 * Test the GenericArray class.
 * 
 * @author kunegis
 */
public class TestGenericArray
{
	/**
	 * Test generic arrays.
	 */
	@Test
	public void testGenericArray()
	{
		final GenericArray <Double> genericArray = new GenericArray <Double>(123, double.class);

		genericArray.set(17, .7);
		final double d = genericArray.get(17);
		System.out.printf("d = %s\n", d);
	}
}
