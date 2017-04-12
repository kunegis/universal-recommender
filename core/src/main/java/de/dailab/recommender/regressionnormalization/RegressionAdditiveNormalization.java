package de.dailab.recommender.regressionnormalization;

import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.DecompositionSolver;
import org.apache.commons.math.linear.QRDecompositionImpl;
import org.apache.commons.math.linear.RealMatrix;

import de.dailab.recommender.dataset.RelationshipSet;
import de.dailab.recommender.matrix.FullEntry;
import de.dailab.recommender.matrix.Matrix;
import de.dailab.recommender.normalize.AdditiveNormalization;
import de.dailab.recommender.normalize.MatrixStatistics;

/**
 * Additive normalization parameters learned from linear regression on a weighted dataset.
 * 
 * @author kunegis
 */
public class RegressionAdditiveNormalization
    extends AdditiveNormalization
{
	/**
	 * A additive normalization strategy learned from a given relationship set by linear regression.
	 * 
	 * @param relationshipSet The relationship setto use as the basis for linear regression
	 */
	public RegressionAdditiveNormalization(RelationshipSet relationshipSet)
	{
		super(getWeights(relationshipSet));
	}

	private static double[] getWeights(RelationshipSet relationshipSet)
	{
		final Matrix matrix = relationshipSet.getMatrix();

		final MatrixStatistics matrixStatistics = new MatrixStatistics(matrix);

		final int r = matrixStatistics.getCount();

		/*
		 * Method: We solve the system Ax = b, where A = [1 m m_row m_col], where is the r by 4 matrix of one, total
		 * mean (m), row means (m_row) and column means (m_col). b is the r by 1 vector of matrix entries.
		 */

		final double a[][] = new double[r][4];
		final double b[] = new double[r];

		int i = 0;
		for (final FullEntry entry: matrix.all())
		{
			a[i][0] = 1;
			a[i][1] = matrixStatistics.getTotalMean();
			a[i][2] = matrixStatistics.getRowMean(entry.rowIndex);
			a[i][3] = matrixStatistics.getColMean(entry.colIndex);
			b[i] = entry.value;
			++i;
		}

		final RealMatrix aMatrix = new Array2DRowRealMatrix(a, false);

		final DecompositionSolver solver = new QRDecompositionImpl(aMatrix).getSolver();
		final double x[] = solver.solve(b);
		return x;
	}
}
