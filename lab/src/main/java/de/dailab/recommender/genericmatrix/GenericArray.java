package de.dailab.recommender.genericmatrix;

import java.lang.reflect.Array;

/**
 * Generic array class that uses primitive types.
 * <p>
 * This class has a generics parameters that represents the type, e.g. Double or Byte. The actual type used is then the
 * corresponding primitive type, e.g. double or byte.
 * <p>
 * GenericArrays have a fixed size, like native Java arrays. This class is the basis for more complex generic primitive
 * implementations of sparse vectors and matrices.
 * 
 * @param <T> The value type, e.g. Double or Byte.
 * @author kunegis
 */
public class GenericArray <T>
{
	/**
	 * Create a generic array of given size and type.
	 * 
	 * @param size Size of the array.
	 * @param componentType The primitive type to use internally. This must be the primitive type corresponding to T.
	 */
	public GenericArray(int size, Class <?> componentType)
	{
		assert size > 0;
		array = Array.newInstance(componentType, size);
	}

	/**
	 * Set the value at a given index.
	 * 
	 * @param i The index
	 * @param value The value
	 */
	public void set(int i, T value)
	{
		Array.set(array, i, value);
	}

	/**
	 * Return the value at a given index.
	 * 
	 * @param i The index
	 * @return The value
	 */
	@SuppressWarnings("unchecked")
	public T get(int i)
	{
		final Object ret = Array.get(array, i);
		return (T) ret;
	}

	/**
	 * @return The number of elements.
	 */
	public int getLength()
	{
		return Array.getLength(array);
	}

	/**
	 * @return The component type used for values in this array
	 */
	public Class <?> getComponentType()
	{
		return array.getClass().getComponentType();
	}

	/**
	 * Get the underlying array. This may be used as arguments of System.arraycopy().
	 * 
	 * @return the array object of the corresponding primitive class.
	 */
	public Object getArrayObject()
	{
		return array;
	}

	/**
	 * The array. The actual type is T_primitive[], where T_primitive is the primitive type corresponding to T.
	 */
	private final Object array;
}
