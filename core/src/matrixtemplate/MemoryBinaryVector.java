package de.dailab.recommender.matrix.template;
 
import de.dailab.recommender.matrix.Entry;
import de.dailab.recommender.matrix.MatrixIterator;
import de.dailab.recommender.matrix.Vector;

/**
 * A sparse binary memory-held vector with fast indexing, indexed by ${index}, and iterable in index order.
 * <p>
 * Size and orientation are not stored.
 * <p>
 * Indexes can be negative generally (if allowed by type ${index}). 
 * 
 * @author kunegis
 */
@SuppressWarnings("all")
final class MemoryBinaryVector${Index}
	implements Vector
{
	/**
	 * A memory vector with initial capacity of zero.
	 */
	public MemoryBinaryVector${Index}()
	{}

	/**
	 * A memory vector with given initial capacity
	 * 
	 * @param capacity Initial capacity
	 */
	public MemoryBinaryVector${Index}(int capacity)
	{
		indexes = new ${index}[capacity];
	}

	@Override
	public String getIndexType()
	{
		return "${index}"; 
	}

	@Override
	public String getWeightType()
	{
		return boolean.class.getSimpleName(); 
	}

	public void setGeneric(int i, double value)
	{
		set((${index}) i); 
	}

	/**
	 * Set an entry to a given value. 
	 * @param i The index
	 * @param value The value 
	 */
	public void set(${index} i)
	{
		assert capacity >= 0;
		assert indexes == null && capacity == 0 || capacity <= indexes.length;

		int min = 0;
		int max = capacity;
		while (min < max)
		{
			final int mid = (min + max) / 2;
			assert mid >= min && mid < max;
			if (indexes[mid] == i)
			{
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
			final ${index} newIndexes[] = new ${index}[allocatedCapacity];
			if (indexes != null)
			{
				System.arraycopy(indexes, 0, newIndexes, 0, min);
				System.arraycopy(indexes, min, newIndexes, min + 1, capacity - min);
			}
			indexes = newIndexes;
		}
		else
		{
			if (indexes != null)
			{
				System.arraycopy(indexes, min, indexes, min + 1, capacity - min);
			}
		}
		capacity = newCapacity;
		indexes[min] = i;
	}

	public void addGeneric(int i, double value)
	{
		set((${index}) i); 
	}

	/**
	 * Add value to index, inserting if not present.
	 * 
	 * @param i Index
	 * @param value Value to add
	 */
	public void add(${index} i)
	{
		set(i); 
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
				final Entry ret = new Entry(indexes[i], 1);
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
				/* Do nothing, i.e. round to one */
			}

			private int i = 0;
		};
	}

	public double getGeneric(int i)
	{
		return get((${index}) i) ? 1. : 0.; 
	}

	/**
	 * Get an entry in the vector.
	 * 
	 * @param i index
	 * @return One if the index is present, else zero
	 */
	public boolean get(${index} i)
	{
		int min = 0;
		int max = capacity;

		while (min < max)
		{
			final int mid = (min + max) / 2;
			assert mid >= min && mid < max;
			if (indexes[mid] == i) return true;
			if (indexes[mid] > i)
				max = mid;
			else
				min = mid + 1;
		}
		assert min == max;
		assert min == 0 && indexes == null || min == capacity || indexes[min] > i;
		assert min == 0 || indexes[min - 1] < i;

		return false;
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
				ret += String.format("%s", indexes[i]);
			}

		return String.format("[%s]", ret); 
	}

	/*
	 * Sorted by index. May be NULL when capacity is zero, at the same time. No duplicate indices. 
	 */
	private int capacity = 0;
	private ${index} indexes[] = null;
	
	/**
	 * Amount of extra space allocated on each resize to avoid too many resizes.
	 * <p>
	 * Higher values mean more memory used and faster adding of new entries.
	 * Lower values mean less memoty used and slower adding of new entries.
	 */
	private static final double OVERHEAD = 0.1;
}
