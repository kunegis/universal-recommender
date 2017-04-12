package de.dailab.recommender.matrix.template;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

import de.dailab.recommender.matrix.Entry;
import de.dailab.recommender.matrix.FullEntry;
import de.dailab.recommender.matrix.Matrix; 
import de.dailab.recommender.matrix.MatrixIterable;
import de.dailab.recommender.matrix.MatrixIterator;

/**
 * A sparse, asymmetric memory-held matrix of longs, with fast indexing from both dimensions, and indexes represented
 * by ints and chars. 
 * 
 * @author autogenerated
 */
@SuppressWarnings("all")
final class MemoryMatrixIntCharLong
    implements Matrix
{
	/**
	 * New memory-held sparse asymmetric float matrix of the given size. The constructed matrix is zero.
	 * 
	 * @param m Row count
	 * @param n Column count
	 */
	public MemoryMatrixIntCharLong(int m, int n)
	{
		rows = new MemoryVectorCharLong[m];
		cols = new MemoryVectorIntLong[n];
	}

	@Override
	public int rows()
	{
		return rows.length;
	}

	@Override
	public int cols()
	{
		return cols.length;
	}

	@Override
	public String getWeightType()
	{
		return "long"; 
	}

	@Override
	public int nnz()
	{
		int ret = 0; 
		for (MemoryVectorCharLong memoryVector:  rows)
		{
			if (memoryVector != null)
			       ret += memoryVector.getSize(); 
		}
		return ret; 
	}

	@Override
	public int getRowCount(int row)
	{      
	       if (rows[row] == null)  return 0; 
	       return rows[row].getSize(); 
	}	

	@Override
	public int getColCount(int col)
	{
		if (cols[col] == null)  return 0;
		return cols[col].getSize(); 
	}

	@Override
	public void set(int i, int j, double value)
	{
		assert i >= 0 && i < rows.length && j >= 0 && j < cols.length;

		if (rows[i] == null) rows[i] = new MemoryVectorCharLong();
		rows[i].set((char) j, (long) value);

		if (cols[j] == null) cols[j] = new MemoryVectorIntLong();
		cols[j].set((int) i, (long) value);
	}

	/**
	 * Set the value using its exact type.
	 * @param i row index
	 * @param j column index
	 * @param value the value
	 */
	public void setValue(int i, int j, long value)
	{
		assert i >= 0 && i < rows.length && j >= 0 && j < cols.length;

		if (rows[i] == null) rows[i] = new MemoryVectorCharLong();
		rows[i].set((char) j, value);

		if (cols[j] == null) cols[j] = new MemoryVectorIntLong();
		cols[j].set((int) i, value);
	}

	/**
	 * Set the value as an int.
	 * @param i row index
	 * @param j column index
	 * @param value the value as an int
	 */
	public void setInt(int i, int j, int value)
	{
		assert i >= 0 && i < rows.length && j >= 0 && j < cols.length;

		if (rows[i] == null) rows[i] = new MemoryVectorCharLong();
		rows[i].set((char) j, (long) value);

		if (cols[j] == null) cols[j] = new MemoryVectorIntLong();
		cols[j].set((int) i, (long) value);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Iterate in row-by-row order
	 */
	@Override
	public MatrixIterable <FullEntry> all()
	{
		return new MatrixIterable <FullEntry>()
		{
			@Override
			public MatrixIterator <FullEntry> iterator()
			{
				return new MatrixIterator <FullEntry>()
				{
					@Override
					public boolean hasNext()
					{
						if (iterator != null && iterator.hasNext()) return true;

						if (iterator != null) ++i;

						while (i < rows.length && (rows[i] == null || !(iterator = rows[i].iterator()).hasNext()))
							++i;
						if (iterator == null) return false;
						return iterator.hasNext();
					}

					@Override
					public FullEntry next()
					{
						if (!hasNext()) throw new NoSuchElementException();
						final Entry entry = iterator.next();
						return new FullEntry(i, entry.index, entry.value);
					}

					@Override
					public void remove()
					{
						throw new UnsupportedOperationException();
					}

					@Override
					public void set(double newValue)
					{
						iterator.set(newValue);
					}

					private int i = 0;
					private MatrixIterator <Entry> iterator = null;
				};
			}
		};
	}

	@Override
	public Iterable <Entry> col(int j)
	{
		if (cols[j] == null) return Collections.emptyList();
		return cols[j];
	}

	@Override
	public Iterable <Entry> row(int i)
	{
		if (rows[i] == null) return Collections.emptyList();
		return rows[i];
	}

	@Override
	public double get(int i, int j)
	{
		if (rows.length < cols.length)
		{
			if (cols[j] == null) return 0.;
			return cols[j].get((int) i);
		}
		else
		{
			if (rows[i] == null) return 0.;
			return rows[i].get((char) j);
		}
	}

	@Override
	public boolean isSymmetric()
	{
		return false;
	}

	@Override
	public double[] mult(double[] v, double[] ret, double weight)
	{
		assert v.length == cols.length;

		if (ret == null) ret = new double[rows.length];

		assert ret.length == rows.length;

		for (int i = 0; i < rows.length; ++i)
		{
			if (rows[i] == null) continue;
			ret[i] += weight * rows[i].mult(v);
		}

		return ret;
	}

	@Override
	public double[] multT(double[] v, double[] ret, double weight)
	{
		assert v.length == rows.length;

		if (ret == null) ret = new double[cols.length];

		assert ret.length == cols.length;

		for (int j = 0; j < cols.length; ++j)
		{
			if (cols[j] == null) continue;
			ret[j] += weight * cols[j].mult(v);
		}

		return ret;
	}

	@Override
	public Iterable <Integer> getRows()
	{
		return new Iterable <Integer> ()
		{
			public Iterator <Integer> iterator()
			{
				return new Iterator <Integer> ()
				{
					public boolean hasNext()
					{
						while (next < rows.length && (rows[next] == null || rows[next].getSize() == 0))
						      ++next;
						return next < rows.length; 
					}

					public Integer next()
					{
						return next++; 
					}
					
					public void remove()
					{
						throw new UnsupportedOperationException(); 
					}

					private int next = 0; 
				}; 
			}			
		}; 
	}

	@Override
	public Iterable <Integer> getCols()
	{
		return new Iterable <Integer> ()
		{
			public Iterator <Integer> iterator()
			{
				return new Iterator <Integer> ()
				{
					public boolean hasNext()
					{
						while (next < cols.length && (cols[next] == null || cols[next].getSize() == 0))
						      ++next;
						return next < cols.length; 
					}

					public Integer next()
					{
						return next++; 
					}
					
					public void remove()
					{
						throw new UnsupportedOperationException(); 
					}

					private int next = 0; 
				}; 
			}			
		}; 
	}

	/*
	 * Empty rows and columns may be represented by NULL, but don't have to.
	 */
	private final MemoryVectorCharLong rows[];
	private final MemoryVectorIntLong cols[];
}
