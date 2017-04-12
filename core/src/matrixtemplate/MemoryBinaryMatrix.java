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
 * Sparse asymmetric binary matrix held in memory, with indexes represented by ${index}s and ${index}s, and fast 
 * indexing from both dimensions.
 * 
 * @author kunegis
 */
@SuppressWarnings("all")
final class MemoryBinaryMatrix${Indexa}${Indexb}
    implements Matrix
{
	public MemoryBinaryMatrix${Indexa}${Indexb}(int m, int n)
	{
		rows = new ${indexb}[m][];
		cols = new ${indexa}[n][];
		rowCounts = new int[m];
		colCounts = new int[n];
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
		return boolean.class.getSimpleName(); 
	}

	@Override
	public int nnz()
	{
		int ret = 0;
		for (int countI:  rowCounts)
		    ret += countI; 
		return ret; 
	}

	@Override
	public int getRowCount(int row)
	{
		return rowCounts[row];
	}

	@Override
	public int getColCount(int col)
	{
		return colCounts[col]; 
	}

	public void set(int i, int j)
	{
		assert i >= 0;
		assert j >= 0;
		assert i < rows.length;
		assert j < cols.length;
		setA(rows, rowCounts, i, j);
		setB(cols, colCounts, j, i);
	}

	private static void setA(${indexb} vectors[][], int counts[], int i, int j)
	{
		assert i >= 0;
		assert j >= 0;
		assert i < vectors.length;

		if (vectors[i] == null)
		{
			assert counts[i] == 0;
			vectors[i] = new ${indexb}[10];
			vectors[i][0] = (${indexb}) j;
			counts[i] = 1;
			return;
		}

		assert counts[i] <= vectors[i].length;

		int min = 0;
		int max = counts[i];
		while (min < max)
		{
			final int mid = (min + max) / 2;
			assert mid >= min && mid < max;
			if (vectors[i][mid] == j) return;
			if (vectors[i][mid] > j)
				max = mid;
			else
				min = mid + 1;
		}
		assert min == max;
		assert min == counts[i] || vectors[i][min] > j;
		assert min == 0 || vectors[i][min - 1] < j;

		if (counts[i] == vectors[i].length)
		{
			final int newCapacity = (int) (1 + 1.5 * counts[i]);
			final ${indexb} newVector[] = new ${indexb}[newCapacity];
			System.arraycopy(vectors[i], 0, newVector, 0, min);
			System.arraycopy(vectors[i], min, newVector, min + 1, counts[i] - min);
			vectors[i] = newVector;
		}
		else
		{
			System.arraycopy(vectors[i], min, vectors[i], min + 1, counts[i] - min);
		}
		vectors[i][min] = (${indexb}) j;
		++counts[i];
	}

	private static void setB(${indexa} vectors[][], int counts[], int i, int j)
	{
		assert i >= 0;
		assert j >= 0;
		assert i < vectors.length;

		if (vectors[i] == null)
		{
			assert counts[i] == 0;
			vectors[i] = new ${indexa}[10];
			vectors[i][0] = (${indexa}) j;
			counts[i] = 1;
			return;
		}

		assert counts[i] <= vectors[i].length;

		int min = 0;
		int max = counts[i];
		while (min < max)
		{
			final int mid = (min + max) / 2;
			assert mid >= min && mid < max;
			if (vectors[i][mid] == j) return;
			if (vectors[i][mid] > j)
				max = mid;
			else
				min = mid + 1;
		}
		assert min == max;
		assert min == counts[i] || vectors[i][min] > j;
		assert min == 0 || vectors[i][min - 1] < j;

		if (counts[i] == vectors[i].length)
		{
			final int newCapacity = (int) (1 + 1.5 * counts[i]);
			final ${indexa} newVector[] = new ${indexa}[newCapacity];
			System.arraycopy(vectors[i], 0, newVector, 0, min);
			System.arraycopy(vectors[i], min, newVector, min + 1, counts[i] - min);
			vectors[i] = newVector;
		}
		else
		{
			System.arraycopy(vectors[i], min, vectors[i], min + 1, counts[i] - min);
		}
		vectors[i][min] = (${indexa}) j;
		++counts[i];
	}

	@Override
	public double get(int i, int j)
	{
		assert i >= 0;
		assert j >= 0;
		assert i < rows.length;
		assert j < cols.length;

		if (rows.length < cols.length)
			return getA(cols[j], colCounts[j], i);
		else
			return getB(rows[i], rowCounts[i], j);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Order: row by row.
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
						if (k < rowCounts[i]) return true;
						++i;
						while (i < rows.length && rowCounts[i] == 0)
							++i;
						if (i == rows.length) return false;
						k = 0;
						return true;
					}

					@Override
					public FullEntry next()
					{
						if (!hasNext()) throw new NoSuchElementException();
						final FullEntry ret = new FullEntry(i, rows[i][k], 1.);
						++k;
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
						if (newValue != 1.)
						   throw new UnsupportedOperationException("Trying to update a binary matrix");
					}

					private int i = 0;
					private int k = 0;
				};
			}
		};
	}

	@Override
	public Iterable <Entry> row(int i)
	{
		return iterableA(rows, i, rowCounts[i]);
	}

	@Override
	public Iterable <Entry> col(int j)
	{
		return iterableB(cols, j, colCounts[j]);
	}

	@Override
	public void set(int i, int j, double value)
	{
		set(i, j);
	}

	private static Iterable <Entry> iterableA(${indexb} vectors[][], int i, final int count)
	{
		assert i >= 0 && i < vectors.length;
		final ${indexb} vector[] = vectors[i];
		if (vector == null)
		{
			assert count == 0;
			return Collections.emptyList();
		}

		return new Iterable <Entry>()
		{
			@Override
			public Iterator <Entry> iterator()
			{
				return new Iterator <Entry>()
				{
					@Override
					public boolean hasNext()
					{
						return i < count;
					}

					@Override
					public Entry next()
					{
						if (i == count) throw new NoSuchElementException();
						return new Entry(vector[i++], 1.);
					}

					@Override
					public void remove()
					{
						throw new UnsupportedOperationException();
					}

					private int i = 0;
				};
			}
		};
	}

	private static Iterable <Entry> iterableB(${indexa} vectors[][], int i, final int count)
	{
		assert i >= 0 && i < vectors.length;
		final ${indexa} vector[] = vectors[i];
		if (vector == null)
		{
			assert count == 0;
			return Collections.emptyList();
		}

		return new Iterable <Entry>()
		{
			@Override
			public Iterator <Entry> iterator()
			{
				return new Iterator <Entry>()
				{
					@Override
					public boolean hasNext()
					{
						return i < count;
					}

					@Override
					public Entry next()
					{
						if (i == count) throw new NoSuchElementException();
						return new Entry(vector[i++], 1.);
					}

					@Override
					public void remove()
					{
						throw new UnsupportedOperationException();
					}

					private int i = 0;
				};
			}
		};
	}

	private static double getA(${indexa} vector[], int count, int i)
	{
		assert i >= 0;

		if (vector == null) return 0.;

		int min = 0;
		int max = count;

		while (min < max)
		{
			final int mid = (min + max) / 2;
			assert mid >= min && mid < max;
			if (vector[mid] == i) return 1.;
			if (vector[mid] > i)
				max = mid;
			else
				min = mid + 1;
		}

		assert min == max;
		assert min == count || i < vector[min];
		return 0.;
	}

	private static double getB(${indexb} vector[], int count, int i)
	{
		assert i >= 0;

		if (vector == null) return 0.;

		int min = 0;
		int max = count;

		while (min < max)
		{
			final int mid = (min + max) / 2;
			assert mid >= min && mid < max;
			if (vector[mid] == i) return 1.;
			if (vector[mid] > i)
				max = mid;
			else
				min = mid + 1;
		}

		assert min == max;
		assert min == count || i < vector[min];
		return 0.;
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
			for (int k = 0; k < rowCounts[i]; ++k)
				ret[i] += weight * v[rows[i][k]];
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
			for (int k = 0; k < colCounts[j]; ++k)
				ret[j] += weight * v[cols[j][k]];
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
						while (next < rowCounts.length && rowCounts[next] == 0)
						      ++next;
						return next < rowCounts.length; 
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
						while (next < colCounts.length && colCounts[next] == 0)
						      ++next;
						return next < colCounts.length; 
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

	private final ${indexb} rows[][];
	private final ${indexa} cols[][];
	private final int rowCounts[];
	private final int colCounts[];
}
