package de.dailab.recommender.laplacian;

import de.dailab.recommender.average.Average;
import de.dailab.recommender.average.MeanSigned;
import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.latent.AbstractLatentPredictor;
import de.dailab.recommender.latent.LatentPredictorModel;
import de.dailab.recommender.predict.RelationshipTypePonderation;

/**
 * Latent prediction using the eigenvectors of a Laplacian associated with the network.
 * <p>
 * The Laplacian predictor uses the inverted Euclidean similarity.
 * 
 * @author kunegis
 */
public class LaplacianPredictor
    extends AbstractLatentPredictor
{
	/**
	 * A Laplacian predictor using a given rank and averaging algorithm.
	 * 
	 * @param rank The rank of the decomposition
	 * @param average The averaging algorithm. The averaging algorithm must support negative weights when the datasets
	 *        contains negative weights, or when the relationship type ponderation contains negative weights.
	 * @param relationshipTypePonderation The weight of individual relationship types; may be NULL
	 */
	public LaplacianPredictor(int rank, Average average, RelationshipTypePonderation relationshipTypePonderation)
	{
		this.rank = rank;
		this.average = average;
		this.relationshipTypePonderation = relationshipTypePonderation;
	}

	/**
	 * The Laplacian predictor with a given relationship type ponderation and default rank and averaging algorithm.
	 * 
	 * @param relationshipTypePonderation The relationship type ponderation to use; may be NULL to denote no ponderation
	 */
	public LaplacianPredictor(RelationshipTypePonderation relationshipTypePonderation)
	{
		this(RANK_DEFAULT, AVERAGE_DEFAULT, relationshipTypePonderation);
	}

	/**
	 * The Laplacian predictor using the default rank and averaging algorithm, and no relationship type ponderation.
	 */
	public LaplacianPredictor()
	{
		this(RANK_DEFAULT, AVERAGE_DEFAULT, null);
	}

	@Override
	public LatentPredictorModel build(Dataset dataset, boolean update)
	{
		return new LaplacianPredictorModel(dataset, rank, average, relationshipTypePonderation, update);
	}

	@Override
	public String toString()
	{
		return String.format("Laplacian(%d, %s)", rank, average);
	}

	/**
	 * The rank of the Laplacian.
	 */
	private final int rank;

	/**
	 * The averaging algorithm to use. This parameter determines the Laplacian type.
	 */
	private final Average average;

	/**
	 * Weights of different relationship types. May be NULL.
	 */
	private final RelationshipTypePonderation relationshipTypePonderation;

	/**
	 * The default rank.
	 */
	private static final int RANK_DEFAULT = 5;

	/**
	 * The default averaging algorithm.
	 * <p>
	 * The arithmetic mean with support for negative edges, which corresponds to the signed normalized Laplacian.
	 */
	private static final Average AVERAGE_DEFAULT = new MeanSigned();
}
