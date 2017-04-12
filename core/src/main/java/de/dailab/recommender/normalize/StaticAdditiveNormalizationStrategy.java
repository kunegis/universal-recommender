package de.dailab.recommender.normalize;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.RelationshipSet;
import de.dailab.recommender.dataset.RelationshipType;
import de.dailab.recommender.dataset.WeightRange;

/**
 * The normalization strategy that consists of normalizing those relationship sets additively that are weighted or
 * signed.
 * 
 * @author kunegis
 */
public class StaticAdditiveNormalizationStrategy
    extends AdditiveNormalizationStrategy
{
	/**
	 * The additive normalization strategy using the given weights.
	 * 
	 * @param additiveNormalization Weights to use for all weighted/signed datasets.
	 */
	public StaticAdditiveNormalizationStrategy(AdditiveNormalization additiveNormalization)
	{
		assert additiveNormalization != null;
		this.additiveNormalization = additiveNormalization;
	}

	/**
	 * Additive normalization strategy with default weights.
	 */
	public StaticAdditiveNormalizationStrategy()
	{
		this.additiveNormalization = new AdditiveNormalization();
	}

	@Override
	public Map <RelationshipType, AdditiveNormalization> applyAdditive(Dataset dataset)
	{
		final Map <RelationshipType, AdditiveNormalization> ret = new HashMap <RelationshipType, AdditiveNormalization>();

		for (final RelationshipSet relationshipSet: dataset.getRelationshipSets())
		{
			if (relationshipSet.getWeightRange() == WeightRange.WEIGHTED
			    || relationshipSet.getWeightRange() == WeightRange.SIGNED)
			{
				ret.put(relationshipSet.getType(), additiveNormalization);
			}
		}

		return ret;
	}

	/**
	 * The weights to use. May not be NULL.
	 */
	private final AdditiveNormalization additiveNormalization;

	@Override
	public String toString()
	{
		return String.format(Locale.ROOT, "StaticAdditive(%.2f, %.2f, %.2f, %.2f)", additiveNormalization.weightOne,
		    additiveNormalization.weightTotal, additiveNormalization.weightRow, additiveNormalization.weightCol);
	}
}
