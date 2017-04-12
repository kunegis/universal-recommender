package de.dailab.recommender.latent;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.predict.RelationshipTypePonderation;
import de.dailab.recommender.similarity.ScalarProduct;
import de.dailab.recommender.similarity.Similarity;
import de.dailab.recommender.similarity.SimilarityTransformation;
import de.dailab.recommender.similarity.TransformedSimilarity;

/**
 * Predictions computed from the eigenvalue decomposition of the aggregated adjacency matrix.
 * 
 * @author kunegis
 */
public class EigenvalueDecompositionPredictor
    extends AbstractLatentPredictor
{
	/**
	 * An eigenvalue decomposition predictor using a given rank and relationship type ponderation.
	 * 
	 * @param rank reduced rank
	 * @param relationshipTypePonderation Relative weights of relationshipTypes.
	 * @param similarity The similarity to use in computing the predictions
	 */
	public EigenvalueDecompositionPredictor(int rank, RelationshipTypePonderation relationshipTypePonderation,
	    Similarity similarity)
	{
		this.rank = rank;
		this.relationshipTypePonderation = relationshipTypePonderation;
		this.similarity = similarity;
	}

	/**
	 * Eigenvalue decomposition of given rank and relationship type ponderation, using the scalar product as similarity.
	 * 
	 * @param rank Rank of the decomposition
	 * @param relationshipTypePonderation The relationship type ponderation to use
	 */
	public EigenvalueDecompositionPredictor(int rank, RelationshipTypePonderation relationshipTypePonderation)
	{
		this(rank, relationshipTypePonderation, new ScalarProduct());
	}

	/**
	 * An eigenvalue decomposition predictor using a given relationship type ponderation.
	 * 
	 * @param relationshipTypePonderation A relationship type ponderation
	 */
	public EigenvalueDecompositionPredictor(RelationshipTypePonderation relationshipTypePonderation)
	{
		this(RANK_DEFAULT, relationshipTypePonderation);
	}

	/**
	 * An eigenvalue decomposition predictor using a given rank.
	 * 
	 * @param rank The rank
	 */
	public EigenvalueDecompositionPredictor(int rank)
	{
		this(rank, null);
	}

	/**
	 * The eigenvalue decomposition using the given similarity for prediction and otherwise all default parameters.
	 * 
	 * @param similarity The similarity to use for prediction
	 */
	public EigenvalueDecompositionPredictor(Similarity similarity)
	{
		this(RANK_DEFAULT, null, similarity);
	}

	/**
	 * The eigenvalue decomposition predictor using the given similarity transformation applied to the scalar product.
	 * 
	 * @param similarityTransformation The similarity transformation to apply
	 */
	public EigenvalueDecompositionPredictor(SimilarityTransformation similarityTransformation)
	{
		this(new TransformedSimilarity(new ScalarProduct(), similarityTransformation));
	}

	/**
	 * The eigenvalue decomposition predictor using the default rank and default relationship type ponderation.
	 */
	public EigenvalueDecompositionPredictor()
	{
		this(RANK_DEFAULT, null);
	}

	public EigenvalueDecompositionPredictorModel build(Dataset dataset, boolean update)
	{
		return new EigenvalueDecompositionPredictorModel(dataset, update, rank, relationshipTypePonderation, similarity);
	}

	/**
	 * The rank of the QR predictor.
	 */
	final int rank;

	/**
	 * The relationship type ponderation. May be NULL to denote the equal-weight relationship type ponderation.
	 */
	final RelationshipTypePonderation relationshipTypePonderation;

	/**
	 * The similarity to use; default is the scalar product in which case this predictor gives simple rank reduction.
	 */
	private final Similarity similarity;

	private final static int RANK_DEFAULT = 7;

	@Override
	public String toString()
	{
		return String.format("%sEigenvalueDecomposition(%s%s)", similarity.equals(new ScalarProduct()) ? "" : String
		    .format("%s-", similarity), rank, relationshipTypePonderation == null ? "" : String.format(", %s",
		    relationshipTypePonderation));
	}
}
