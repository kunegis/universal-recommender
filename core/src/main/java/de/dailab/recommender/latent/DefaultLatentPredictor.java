package de.dailab.recommender.latent;

import de.dailab.recommender.predict.RelationshipTypePonderation;

/**
 * A good general-purpose parameter-free latent predictor.
 * <p>
 * The eigenvalue decomposition predictor is used with its default rank, and the default additive normalization and a
 * default spectral transformation.
 * 
 * @author kunegis
 */
public class DefaultLatentPredictor
    extends CompoundLatentPredictor
{
	/**
	 * The default latent predictor, a normalization eigenvalue decomposition predictor.
	 */
	public DefaultLatentPredictor()
	{
		super(new LatentNormalizationPredictor(), new DefaultUnnormalizedLatentPredictor());
	}

	/**
	 * The default latent predictor using a given rank.
	 * 
	 * @param rank The rank to use in the latent decomposition
	 */
	public DefaultLatentPredictor(int rank)
	{
		super(new LatentNormalizationPredictor(), new DefaultUnnormalizedLatentPredictor(rank));
	}

	/**
	 * The default latent predictor using a given relationship type ponderation.
	 * 
	 * @param relationshipTypePonderation The relationship type ponderation to use
	 */
	public DefaultLatentPredictor(RelationshipTypePonderation relationshipTypePonderation)
	{
		super(new LatentNormalizationPredictor(), new DefaultUnnormalizedLatentPredictor(relationshipTypePonderation));
	}
}
