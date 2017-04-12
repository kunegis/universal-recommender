package de.dailab.recommender.normalize;

import de.dailab.recommender.matrix.Matrix;

/**
 * Normalize by subtraction of an approximation of the form e'a + b'e + cj.
 * 
 * @author kunegis
 */
public class AdditiveNormalizer
    implements Normalizer
{
	/**
	 * An additive normalization using given additive normalization weights.
	 * 
	 * @param additiveNormalization Normalization weights
	 */
	public AdditiveNormalizer(AdditiveNormalization additiveNormalization)
	{
		this.additiveNormalization = additiveNormalization;
	}

	/**
	 * A normalization using the default additive normalization weights.
	 */
	public AdditiveNormalizer()
	{
		this(new AdditiveNormalization());
	}

	public Model build(Matrix matrix)
	{
		final MatrixStatistics matrixStatistics = new MatrixStatistics(matrix);

		return new Model(matrixStatistics);
	}

	private final AdditiveNormalization additiveNormalization;

	private class Model
	    implements NormalizerModel
	{
		public Model(MatrixStatistics matrixStatistics)
		{
			this.matrixStatistics = matrixStatistics;
		}

		public double normalize(double value, int rowIndex, int colIndex)
		{
			return value - prediction(rowIndex, colIndex);
		}

		public double denormalize(double value, int rowIndex, int colIndex)
		{
			return value + prediction(rowIndex, colIndex);
		}

		private double prediction(int i, int j)
		{
			double ret = (additiveNormalization.weightOne + additiveNormalization.weightTotal
			    * matrixStatistics.getTotalMean() + additiveNormalization.weightRow * matrixStatistics.getRowMean(i) + additiveNormalization.weightCol
			    * matrixStatistics.getColMean(j))
			    / (additiveNormalization.weightOne + additiveNormalization.weightTotal
			        + additiveNormalization.weightRow + additiveNormalization.weightCol);
			if (Double.isNaN(ret))
			    ret = (additiveNormalization.weightOne + additiveNormalization.weightTotal
			        * matrixStatistics.getTotalMean())
			        / (additiveNormalization.weightOne + additiveNormalization.weightTotal);
			if (Double.isNaN(ret)) ret = additiveNormalization.weightOne;
			return ret;
		}

		private final MatrixStatistics matrixStatistics;
	}
}
