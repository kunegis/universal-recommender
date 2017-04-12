package de.dailab.recommender.matrix;

import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * A m by n matrix where m or n is zero.
 * 
 * @author kunegis
 */
public class ZeroMatrix
    implements Matrix
{
	/**
	 * A matrix of size m by n, one of which must be zero.
	 * 
	 * @param m Row count
	 * @param n Column count
	 */
	public ZeroMatrix(int m, int n)
	{
		assert m >= 0;
		assert n >= 0;

		assert m == 0 || n == 0;

		this.m = m;
		this.n = n;
	}

	private final int m, n;

	@Override
	public boolean isSymmetric()
	{
		return m == n;
	}

	@Override
	public int rows()
	{
		return m;
	}

	@Override
	public int cols()
	{
		return n;
	}

	@Override
	public int nnz()
	{
		return 0;
	}

	@Override
	public double get(int i, int j)
	{
		throw new IllegalArgumentException();
	}

	@Override
	public void set(int i, int j, double value)
	{
		throw new IllegalArgumentException();
	}

	@Override
	public int getRowCount(int row)
	{
		throw new IllegalArgumentException();
	}

	@Override
	public int getColCount(int col)
	{
		throw new IllegalArgumentException();
	}

	@Override
	public String getWeightType()
	{
		return boolean.class.getSimpleName();
	}

	@Override
	public MatrixIterable <FullEntry> all()
	{
		return MATRIX_ITERABLE;
	}

	@Override
	public Iterable <Entry> row(int i)
	{
		return EMPTY_ARRAY;
	}

	@Override
	public Iterable <Entry> col(int j)
	{
		return EMPTY_ARRAY;
	}

	@Override
	public Iterable <Integer> getRows()
	{
		return EMPTY_ARRAY_INT;
	}

	@Override
	public Iterable <Integer> getCols()
	{
		return EMPTY_ARRAY_INT;
	}

	@Override
	public double[] mult(double[] v, double[] ret, double weight)
	{
		assert v.length == n;
		if (ret == null) ret = new double[m];
		assert ret.length == m;
		return ret;
	}

	@Override
	public double[] multT(double[] v, double[] ret, double weight)
	{
		assert v.length == m;
		if (ret == null) ret = new double[n];
		assert ret.length == n;
		return ret;
	}

	private final static Iterable <Entry> EMPTY_ARRAY = new ArrayList <Entry>();
	private final static Iterable <Integer> EMPTY_ARRAY_INT = new ArrayList <Integer>();

	private static final MatrixIterable <FullEntry> MATRIX_ITERABLE = new MatrixIterable <FullEntry>()
	{
		@Override
		public MatrixIterator <FullEntry> iterator()
		{
			return new MatrixIterator <FullEntry>()
			{
				@Override
				public void set(double newValue)
				{
					throw new IllegalArgumentException();
				}

				@Override
				public boolean hasNext()
				{
					return false;
				}

				@Override
				public FullEntry next()
				{
					throw new NoSuchElementException();
				}

				@Override
				public void remove()
				{
					throw new UnsupportedOperationException();
				}
			};
		}
	};

}
