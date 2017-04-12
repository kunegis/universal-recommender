package de.dailab.recommender.mask;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.RelationshipSet;
import de.dailab.recommender.dataset.WeightRange;
import de.dailab.recommender.latent.AbstractLatentPredictor;
import de.dailab.recommender.latent.LatentPredictorModel;
import de.dailab.recommender.predict.RelationshipTypePonderation;

/**
 * Prediction by mask decomposition.
 * <p>
 * What we call "mask prediction" is also know as approximation of a matrix with missing values.
 * <p>
 * This is implemented by iterative computation of a rank-1 approximation which is subtracted from the matrix.
 * <p>
 * This predictor cannot be used when the dataset is unweighted.
 * <p>
 * The current implementation does not to converge for bipartite datasets.
 * <p>
 * This implementation automatically uses the symmetric cover of asymmetric unipartite relationship sets.
 * 
 * @author kunegis
 * 
 * @see <a href="http://portal.acm.org/citation.cfm?id=1390165">Biggs et al., <i>Nonnegative matrix factorization via
 *      rank-one downdate</i></a>
 * @see <a href="http://portal.acm.org/citation.cfm?id=1087620">Srebro et al., <i>Learning with matrix
 *      factorizations</i></a>
 */
public class MaskDecompositionPredictor
    extends AbstractLatentPredictor
{
	/**
	 * The mask decomposition predictor with the given rank and relationship type ponderation.
	 * 
	 * @param rank The rank of the decomposition
	 * @param relationshipTypePonderation The relationship type ponderation; NULL denotes an empty value
	 */
	public MaskDecompositionPredictor(int rank, RelationshipTypePonderation relationshipTypePonderation)
	{
		this.rank = rank;
		this.relationshipTypePonderation = relationshipTypePonderation;
	}

	/**
	 * A mask decomposition predictor of the given rank.
	 * 
	 * @param rank The rank of the mask decomposition predictor
	 */
	public MaskDecompositionPredictor(int rank)
	{
		this(rank, null);
	}

	/**
	 * The mask decomposition predictor with default rank and empty relationship type ponderation.
	 */
	public MaskDecompositionPredictor()
	{
		this(RANK_DEFAULT, null);
	}

	@Override
	public LatentPredictorModel build(Dataset dataset, boolean update)
	{
		/*
		 * Check that at least one relationship set is not unweighted and that at least one relationship set is not
		 * bipartite.
		 */

		boolean foundWeighted = false;
		boolean foundUnipartite = false;

		for (final RelationshipSet relationshipSet: dataset.getRelationshipSets())
		{
			if (relationshipSet.getWeightRange() != WeightRange.UNWEIGHTED) foundWeighted = true;
			if (!relationshipSet.isBipartite()) foundUnipartite = true;
		}

		if (!foundWeighted)
		    throw new IllegalArgumentException(
		        "MaskDecompositionPredictor requires the dataset to contain at least one weighted relationship set");
		if (!foundUnipartite)
		    throw new IllegalArgumentException(
		        "MaskDecompositionPredictor requires the dataset to contain at least one unirelational relationship set");

		return new MaskDecompositionPredictorModel(dataset, rank,
		    relationshipTypePonderation == null ? new RelationshipTypePonderation() : relationshipTypePonderation,
		    update);
	}

	@Override
	public String toString()
	{
		return String.format("MaskDecomposition(%s%s)", relationshipTypePonderation == null ? ""
		    : relationshipTypePonderation + ", ", rank);
	}

	/**
	 * The rank of the decomposition.
	 */
	private final int rank;

	/**
	 * The relationship type ponderation. NULL denotes an empty value.
	 */
	private final RelationshipTypePonderation relationshipTypePonderation;

	private final static int RANK_DEFAULT = 4;
}
