package de.dailab.recommender.genericmatrix;

import java.util.Iterator;

/**
 * A sparse memory-held vector with generic value and index types that supports arrays of primitive types.
 * <p>
 * Size and orientation and not stored.
 * <p>
 * As far as generic arrays go, this implementation uses a minimum of memory.
 * 
 * @param <Index> Type of array indices. Must be Byte, Character, Short or Integer.
 * @param <Value> Type of values. One of Byte, Short, Integer, Long, Float, Double.
 * 
 * @author kunegis
 */
public final class GenericMemoryVector <Index extends Number & Comparable <Index>, Value extends Number>
    implements Iterable <Entry <Index, Value>>
{
	/**
	 * A memory vector with initial size of zero.
	 * 
	 * @param indexComponentType primitive index class
	 * @param valueComponentType primitive value class
	 */
	public GenericMemoryVector(Class <?> indexComponentType, Class <?> valueComponentType)
	{
		this(1, indexComponentType, valueComponentType);
	}

	/**
	 * A memory vector with given initial capacity and size of zero.
	 * 
	 * @param capacity Initial capacity
	 * @param primitiveIndexClass The primitive class corresponding to Index (e.g. byte.class or int.class)
	 * @param primitiveValueClass The primitive class corresponding to Value
	 */
	public GenericMemoryVector(int capacity, Class <?> primitiveIndexClass, Class <?> primitiveValueClass)
	{
		indexes = new GenericArray <Index>(capacity, primitiveIndexClass);
		values = new GenericArray <Value>(capacity, primitiveValueClass);
	}

	/**
	 * Set the value at a given index.
	 * 
	 * @param i The index
	 * @param value The value
	 */
	public void set(Index i, Value value)
	{
		assert indexes == null && values == null || indexes.getLength() == values.getLength();
		assert size >= 0;
		assert indexes == null && values == null && size == 0 || size <= indexes.getLength();

		int min = 0;
		int max = size;
		while (min < max)
		{
			final int mid = (min + max) / 2;
			assert mid >= min && mid < max;
			if (indexes.get(mid).equals(i))
			{
				values.set(mid, value);
				return;
			}
			if (indexes.get(mid).compareTo(i) > 0)
				max = mid;
			else
				min = mid + 1;
		}
		assert min == max;
		assert min == 0 && indexes == null || min == size || indexes.get(min).compareTo(i) > 0;
		assert min == 0 || indexes.get(min - 1).compareTo(i) < 0;

		final int newSize = 1 + size;

		if (indexes == null || indexes.getLength() < newSize)
		{
			final int allocatedSize = 10 + (int) (newSize * 1.5);
			final GenericArray <Index> newIndexes = new GenericArray <Index>(allocatedSize, indexes.getComponentType());
			final GenericArray <Value> newValues = new GenericArray <Value>(allocatedSize, values.getComponentType());
			if (indexes != null)
			{
				System.arraycopy(indexes.getArrayObject(), 0, newIndexes.getArrayObject(), 0, min);
				System.arraycopy(values.getArrayObject(), 0, newValues.getArrayObject(), 0, min);
				System.arraycopy(indexes.getArrayObject(), min, newIndexes.getArrayObject(), min + 1, size - min);
				System.arraycopy(values.getArrayObject(), min, newValues.getArrayObject(), min + 1, size - min);
			}
			indexes = newIndexes;
			values = newValues;
		}
		else
		{
			if (indexes != null)
			{
				System.arraycopy(indexes.getArrayObject(), min, indexes.getArrayObject(), min + 1, size - min);
				System.arraycopy(values.getArrayObject(), min, values.getArrayObject(), min + 1, size - min);
			}
		}
		size = newSize;
		indexes.set(min, i);
		values.set(min, value);
	}

	/**
	 * Add value to index, inserting if not present.
	 * 
	 * @param i Index
	 * @param value Value to add
	 */
	public void add(Index i, Value value)
	{
		assert indexes == null && values == null || indexes.getLength() == values.getLength();
		assert size >= 0;
		assert indexes == null && values == null && size == 0 || size <= indexes.getLength();

		int min = 0;
		int max = size;
		while (min < max)
		{
			final int mid = (min + max) / 2;
			assert mid >= min && mid < max;
			if (indexes.get(mid).equals(i))
			{
				Util.set(values, mid, values.get(mid).doubleValue() + value.doubleValue());
				return;
			}
			if (indexes.get(mid).compareTo(i) > 0)
				max = mid;
			else
				min = mid + 1;
		}
		assert min == max;
		assert min == 0 && indexes == null || min == size || indexes.get(min).compareTo(i) > 0;
		assert min == 0 || indexes.get(min - 1).compareTo(i) < 0;

		final int newSize = 1 + size;

		if (indexes == null || indexes.getLength() < newSize)
		{
			final int allocatedSize = 10 + (int) (newSize * 1.5);
			final GenericArray <Index> newIndexes = new GenericArray <Index>(allocatedSize, indexes.getComponentType());
			final GenericArray <Value> newValues = new GenericArray <Value>(allocatedSize, values.getComponentType());
			if (indexes != null)
			{
				System.arraycopy(indexes.getArrayObject(), 0, newIndexes.getArrayObject(), 0, min);
				System.arraycopy(values.getArrayObject(), 0, newValues.getArrayObject(), 0, min);
				System.arraycopy(indexes.getArrayObject(), min, newIndexes.getArrayObject(), min + 1, size - min);
				System.arraycopy(values.getArrayObject(), min, newValues.getArrayObject(), min + 1, size - min);
			}
			indexes = newIndexes;
			values = newValues;
		}
		else
		{
			if (indexes != null)
			{
				System.arraycopy(indexes.getArrayObject(), min, indexes.getArrayObject(), min + 1, size - min);
				System.arraycopy(values.getArrayObject(), min, values.getArrayObject(), min + 1, size - min);
			}
		}
		size = newSize;
		indexes.set(min, i);
		values.set(min, value);
	}

	@Override
	public Iterator <Entry <Index, Value>> iterator()
	{
		return new Iterator <Entry <Index, Value>>()
		{
			@Override
			public boolean hasNext()
			{
				return i < size;
			}

			@Override
			public Entry <Index, Value> next()
			{
				assert i < size;
				final Entry <Index, Value> ret = new Entry <Index, Value>(indexes.get(i), values.get(i));
				++i;
				return ret;
			}

			@Override
			public void remove()
			{
				throw new UnsupportedOperationException();
			}

			private int i = 0;
		};
	}

	/**
	 * Get an entry in the vector.
	 * 
	 * @param i index
	 * @return The boxed value; null for sparse entries
	 */
	public Value get(Index i)
	{
		int min = 0;
		int max = size;

		while (min < max)
		{
			final int mid = (min + max) / 2;
			assert mid >= min && mid < max;
			if (indexes.get(mid) == i) return values.get(mid);
			if (indexes.get(mid).compareTo(i) > 0)
				max = mid;
			else
				min = mid + 1;
		}
		assert min == max;
		assert min == 0 && indexes == null || min == size || indexes.get(min).compareTo(i) > 0;
		assert min == 0 || indexes.get(min - 1).compareTo(i) < 0;

		return null;
	}

	/**
	 * Scalar product.
	 * 
	 * @param v A vector
	 * @return THIS * v
	 */
	double mult(double v[])
	{
		double ret = 0.;

		for (int i = 0; i < size; ++i)
			ret += v[indexes.get(i).intValue()] * values.get(i).doubleValue();

		return ret;
	}

	/*
	 * Same length, sorted by index. May both not be NULL. No duplicate indices. SIZE is the actual used size.
	 * 
	 * Both array are of the primitive type corresponding to Index and Value.
	 */
	private int size = 0;
	private GenericArray <Index> indexes = null;
	private GenericArray <Value> values = null;
}
