package de.dailab.recommender.normalize;

/**
 * The set of parameters for additive normalization.
 * 
 * @author kunegis
 */
public class AdditiveNormalization
{
	/**
	 * Additive normalization with given weights.
	 * 
	 * @param weightOne The weight for the constant term
	 * @param weightTotal The weight for the global mean
	 * @param weightRow The weight for the row mean
	 * @param weightCol The weight for the column mean
	 */
	public AdditiveNormalization(double weightOne, double weightTotal, double weightRow, double weightCol)
	{
		assert weightOne >= 0;
		assert weightTotal >= 0;
		assert weightRow >= 0;
		assert weightCol >= 0;
		assert weightOne + weightTotal + weightRow + weightCol > 0;

		this.weightOne = weightOne;
		this.weightTotal = weightTotal;
		this.weightRow = weightRow;
		this.weightCol = weightCol;
	}

	/**
	 * Initialize weights from a vector.
	 * 
	 * @param weights a 4-vector of weights in this order: [one total row col].
	 */
	public AdditiveNormalization(double weights[])
	{
		assert weights.length == 4;
		weightOne = weights[0];
		weightTotal = weights[1];
		weightRow = weights[2];
		weightCol = weights[3];
	}

	/**
	 * Additive normalization with equal global, row and column weights, and no constant term.
	 */
	public AdditiveNormalization()
	{
		this(0, 1 / 3., 1 / 3., 1 / 3.);
	}

	/**
	 * The constant term.
	 */
	public final double weightOne;

	/**
	 * The weight of the total mean.
	 */
	public final double weightTotal;

	/**
	 * The weight of row means.
	 */
	public final double weightRow;

	/**
	 * The weight of column means.
	 */
	public final double weightCol;
}
