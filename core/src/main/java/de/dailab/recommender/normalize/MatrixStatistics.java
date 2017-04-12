package de.dailab.recommender.normalize;

import de.dailab.recommender.matrix.Entry;
import de.dailab.recommender.matrix.Matrix;

/**
 * Compute sums and counts of a matrix.
 * 
 * @author kunegis
 */
public class MatrixStatistics
{
	/**
	 * Compute statistics from a given matrix.
	 * 
	 * @param matrix A matrix
	 */
	public MatrixStatistics(Matrix matrix)
	{
		final int m = matrix.rows();
		final int n = matrix.cols();

		int _count = 0;
		double _sum = 0.;

		countSubject = new int[m];
		sumSubject = new double[m];

		for (int i = 0; i < m; ++i)
		{
			for (final Entry entry: matrix.row(i))
			{
				++_count;
				_sum += entry.value;

				++countSubject[i];
				sumSubject[i] += entry.value;
			}
		}

		if (matrix.isSymmetric())
		{
			countObject = countSubject;
			sumObject = sumSubject;
		}
		else
		{
			countObject = new int[n];
			sumObject = new double[n];

			for (int j = 0; j < n; ++j)
			{
				for (final Entry entry: matrix.col(j))
				{
					++countObject[j];
					sumObject[j] += entry.value;
				}
			}
		}

		this.sum = _sum;
		this.count = _count;
	}

	/**
	 * @return the total number of entries in the matrix.
	 */
	public int getCount()
	{
		return count;
	}

	/**
	 * @return The mean value of matrix entries, excluding implicit zero entries. Returns NaN when there are no explicit
	 *         entries.
	 */
	public double getTotalMean()
	{
		return sum / count;
	}

	/**
	 * Get the row mean excluding implicit entries.
	 * 
	 * @param i Row index
	 * @return Row mean. NaN when there are no explicit entries.
	 */
	public double getRowMean(int i)
	{
		return sumSubject[i] / countSubject[i];
	}

	/**
	 * Get the column mean excluding implicit entries.
	 * 
	 * @param j Column index
	 * @return Column mean. NaN when there are no explicit entries.
	 */
	public double getColMean(int j)
	{
		return sumObject[j] / countObject[j];
	}

	private final int count;
	private final int countSubject[];
	private final int countObject[];
	private final double sum;
	private final double sumSubject[];
	private final double sumObject[];
}
