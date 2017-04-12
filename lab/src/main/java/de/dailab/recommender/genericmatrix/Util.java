package de.dailab.recommender.genericmatrix;

import java.lang.reflect.Array;

/**
 * Utility functions for working with generic arrays.
 * 
 * @author kunegis
 */
public class Util
{
	/**
	 * Set a double value at a given index.
	 * 
	 * @param <T> The value type of the array
	 * @param genericArray The generic array to modify
	 * @param i The index
	 * @param value The new value
	 */
	public static <T> void set(GenericArray <T> genericArray, int i, double value)
	{
		if (genericArray.getComponentType().equals(byte.class))
			Array.set(genericArray.getArrayObject(), i, (byte) value);
		else if (genericArray.getComponentType().equals(short.class))
			Array.set(genericArray.getArrayObject(), i, (short) value);
		else if (genericArray.getComponentType().equals(char.class))
			Array.set(genericArray.getArrayObject(), i, (char) value);
		else if (genericArray.getComponentType().equals(int.class))
			Array.set(genericArray.getArrayObject(), i, (int) value);
		else if (genericArray.getComponentType().equals(long.class))
			Array.set(genericArray.getArrayObject(), i, (long) value);
		else if (genericArray.getComponentType().equals(float.class))
			Array.set(genericArray.getArrayObject(), i, (float) value);
		else if (genericArray.getComponentType().equals(double.class))
		    Array.set(genericArray.getArrayObject(), i, value);
	}
}
