package de.dailab.recommender.latent;

import de.dailab.recommender.predict.RelationshipTypePonderation;
import de.dailab.recommender.similarity.ExponentialKernel;
import de.dailab.recommender.similarity.SpectralTransformation;
import de.dailab.recommender.similarity.TransformedSimilarity;

/**
 * The default unnormalized latent predictor.
 * <p>
 * Implementation choice: the eigenvalue decomposition.
 * 
 * @author kunegis
 */
public class DefaultUnnormalizedLatentPredictor
    extends EigenvalueDecompositionPredictor
{
	/**
	 * The default unnormalized latent predictor, the eigenvalue decomposition.
	 */
	public DefaultUnnormalizedLatentPredictor()
	{
		super(new TransformedSimilarity(DEFAULT_KERNEL));
	}

	/**
	 * The default unnormalized latent predictor using the given rank.
	 * 
	 * @param rank The rank to use for decomposition
	 */
	public DefaultUnnormalizedLatentPredictor(int rank)
	{
		super(rank, null, new TransformedSimilarity(DEFAULT_KERNEL));
	}

	/**
	 * The default unnormalized latent predictor with a given relationship type ponderation.
	 * 
	 * @param relationshipTypePonderation The relationship type ponderation to use.
	 */
	public DefaultUnnormalizedLatentPredictor(RelationshipTypePonderation relationshipTypePonderation)
	{
		super(relationshipTypePonderation);
	}

	private static final SpectralTransformation DEFAULT_KERNEL = new ExponentialKernel();
}
