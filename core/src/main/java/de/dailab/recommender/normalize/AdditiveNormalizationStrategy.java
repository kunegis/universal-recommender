package de.dailab.recommender.normalize;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.RelationshipType;

/**
 * A normalization strategy that computes an additive normalization for each relationship set dynamically.
 * 
 * @author kunegis
 */
public abstract class AdditiveNormalizationStrategy
    implements NormalizationStrategy
{
	/**
	 * Compute the additive normalizations given a dataset.
	 * 
	 * @param dataset A dataset
	 * @return The additive normalizations for all relationship sets that have to be normalized, by relationship type.
	 */
	public abstract Map <RelationshipType, AdditiveNormalization> applyAdditive(Dataset dataset);

	@Override
	public Map <RelationshipType, Normalizer> apply(Dataset dataset)
	{
		final Map <RelationshipType, AdditiveNormalization> additiveNormalizations = applyAdditive(dataset);

		final Map <RelationshipType, Normalizer> ret = new HashMap <RelationshipType, Normalizer>();

		for (final Entry <RelationshipType, AdditiveNormalization> entry: additiveNormalizations.entrySet())
		{
			ret.put(entry.getKey(), new AdditiveNormalizer(entry.getValue()));
		}

		return ret;
	}
}
