package de.dailab.recommender.matrix.template;

import de.dailab.recommender.matrix.Entry;
import de.dailab.recommender.matrix.MatrixIterable;
import de.dailab.recommender.matrix.MatrixIterator;
import de.dailab.recommender.matrix.Vector;

/**
 * A sparse memory-held vector of chars with fast indexing, indexed by byte.
 * <p>
 * Size and orientation are not stored.
 * <p>
 * Indexes can be negative generally (if allowed by type byte). 
 * 
 * @author autogenerated
 */
@SuppressWarnings("all")
final class MemoryVectorByteChar
	implements Vector
{
	/**
	 * A memory vector with initial capacity of zero.
	 */
	public MemoryVectorByteChar()
	{}

	/**
	 * A memory vector with given initial capacity
	 * 
	 * @param capacity Initial capacity
	 */
	public MemoryVectorByteChar(int capacity)
	{
		indexes = new byte[capacity];
		values = new char[capacity];
	}

	@Override
	public String getIndexType()
	{
		return "byte"; 
	}

	@Override
	public String getWeightType()
	{
		return "char"; 
	}

	public void setGeneric(int i, double value)
	{
		set((byte) i, (char) value); 
	}

	/**
	 * Set an entry to a given value. 
	 * @param i The index
	 * @param value The value 
	 */
	public void set(byte i, char value)
	{
		assert indexes == null && values == null || indexes.length == values.length;
		assert capacity >= 0;
		assert indexes == null && values == null && capacity == 0 || capacity <= indexes.length;

		int min = 0;
		int max = capacity;
		while (min < max)
		{
			final int mid = (min + max) / 2;
			assert mid >= min && mid < max;
			if (indexes[mid] == i)
			{
				values[mid] = value;
				return;
			}
			if (indexes[mid] > i)
				max = mid;
			else
				min = mid + 1;
		}
		assert min == max;
		assert min == 0 && indexes == null || min == capacity || indexes[min] > i;
		assert min == 0 || indexes[min - 1] < i;

		final int newCapacity = 1 + capacity;

		if (indexes == null || indexes.length < newCapacity)
		{
			final int allocatedCapacity = 10 + (int) (newCapacity * (1 + OVERHEAD));
			final byte newIndexes[] = new byte[allocatedCapacity];
			final char newValues[] = new char[allocatedCapacity];
			if (indexes != null)
			{
				System.arraycopy(indexes, 0, newIndexes, 0, min);
				System.arraycopy(values, 0, newValues, 0, min);
				System.arraycopy(indexes, min, newIndexes, min + 1, capacity - min);
				System.arraycopy(values, min, newValues, min + 1, capacity - min);
			}
			indexes = newIndexes;
			values = newValues;
		}
		else
		{
			if (indexes != null)
			{
				System.arraycopy(indexes, min, indexes, min + 1, capacity - min);
				System.arraycopy(values, min, values, min + 1, capacity - min);
			}
		}
		capacity = newCapacity;
		indexes[min] = i;
		values[min] = value;
	}

	public void addGeneric(int i, double value)
	{
		add((byte) i, (char) value); 
	}

	/**
	 * Add value to index, inserting if not present.
	 * 
	 * @param i Index
	 * @param value Value to add
	 */
	public void add(byte i, char value)
	{
		assert indexes == null && values == null || indexes.length == values.length;
		assert capacity >= 0;
		assert indexes == null && values == null && capacity == 0 || capacity <= indexes.length;

		int min = 0;
		int max = capacity;
		while (min < max)
		{
			final int mid = (min + max) / 2;
			assert mid >= min && mid < max;
			if (indexes[mid] == i)
			{
				values[mid] += value;
				return;
			}
			if (indexes[mid] > i)
				max = mid;
			else
				min = mid + 1;
		}
		assert min == max;
		assert min == 0 && indexes == null || min == capacity || indexes[min] > i;
		assert min == 0 || indexes[min - 1] < i;

		final int newCapacity = 1 + capacity;

		if (indexes == null || indexes.length < newCapacity)
		{
			final int allocatedCapacity = 10 + (int) (newCapacity * (1 + OVERHEAD));
			final byte newIndexes[] = new byte[allocatedCapacity];
			final char newValues[] = new char[allocatedCapacity];
			if (indexes != null)
			{
				System.arraycopy(indexes, 0, newIndexes, 0, min);
				System.arraycopy(values, 0, newValues, 0, min);
				System.arraycopy(indexes, min, newIndexes, min + 1, capacity - min);
				System.arraycopy(values, min, newValues, min + 1, capacity - min);
			}
			indexes = newIndexes;
			values = newValues;
		}
		else
		{
			if (indexes != null)
			{
				System.arraycopy(indexes, min, indexes, min + 1, capacity - min);
				System.arraycopy(values, min, values, min + 1, capacity - min);
			}
		}
		capacity = newCapacity;
		indexes[min] = i;
		values[min] = value;
	}

	@Override
	public MatrixIterator <Entry> iterator()
	{
		return new MatrixIterator <Entry>()
		{
			@Override
			public boolean hasNext()
			{
				return i < capacity;
			}

			@Override
			public Entry next()
			{
				assert i < capacity;
				final Entry ret = new Entry(indexes[i], values[i]);
				++i;
				return ret;
			}

			@Override
			public void remove()
			{
				throw new UnsupportedOperationException();
			}

			@Override
			public void set(double newValue)
			{
				values[i-1] = (char) newValue; 
			}

			private int i = 0;
		};
	}

	public double getGeneric(int i)
	{
		return get((byte) i); 
	}

	/**
	 * Get an entry in the vector.
	 * 
	 * @param i index
	 * @return The value; zero for sparse entries
	 */
	public char get(byte i)
	{
		int min = 0;
		int max = capacity;

		while (min < max)
		{
			final int mid = (min + max) / 2;
			assert mid >= min && mid < max;
			if (indexes[mid] == i) return values[mid];
			if (indexes[mid] > i)
				max = mid;
			else
				min = mid + 1;
		}
		assert min == max;
		assert min == 0 && indexes == null || min == capacity || indexes[min] > i;
		assert min == 0 || indexes[min - 1] < i;

		return 0;
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

		for (int i = 0; i < capacity; ++i)
			ret += v[indexes[i]] * values[i];

		return ret;
	}

	/**
	 * @return Number of nonsparse elements
	 */
	@Override
	public int nnz()
	{
		return capacity; 
	}

	@Override
	public String toString()
	{
		String ret = "";
		for (int i = 0; i < capacity;  ++i)
			{
				if (! ret.equals("")) ret += " "; 
				ret += String.format("%s:%s", indexes[i], values[i]);
			}

		return String.format("[%s]", ret); 
	}

	/**
	 * @return number of set elements in the vector.
	 * @deprecated Use nnz() 
	 */
	@Deprecated
	public int getSize()
	{
		return capacity; 
	}
	
	/*
	 * Same length, sorted by index. May both be NULL when capacity is zero, at the same time. No duplicate indices. 
	 */
	private int capacity = 0;
	private byte indexes[] = null;
	private char values[] = null;
	
	/**
	 * Amount of extra space allocated on each resize to avoid too many resizes.
	 * <p>
	 * Higher values mean more memory used and faster adding of new entries.
	 * Lower values mean less memory used and slower adding of new entries.
	 */
	private static final double OVERHEAD = 0.1;
}

