package de.dailab.recommender.predict;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import de.dailab.recommender.dataset.RelationshipType;

/**
 * Relative weights for a set of relationship types.
 * <p>
 * Objects of this class are usually passed to recommenders and predictors as parameters.
 * 
 * @author kunegis
 */
public class RelationshipTypePonderation
{
	/**
	 * A relationship type ponderation that is initially empty.
	 */
	public RelationshipTypePonderation()
	{}

	/**
	 * A relationship type ponderation with one relationship type set. Others can be added with putWeight().
	 * 
	 * @param relationshipType The relationship type to set
	 * @param weight The weight to set
	 */
	public RelationshipTypePonderation(RelationshipType relationshipType, double weight)
	{
		putWeight(relationshipType, weight);
	}

	/**
	 * The weight of a given relationship type, return NULL if the relationship type is not contained.
	 * 
	 * @param relationshipType a relationship type
	 * @return the weight of the given relationship type. NULL if no weight for this type is stored.
	 */
	public Double getWeight(RelationshipType relationshipType)
	{
		return weights.get(relationshipType);
	}

	/**
	 * The weight associated with a relationship type, returning 1 for missing relationship types.
	 * 
	 * @param relationshipType a given relationship type
	 * @return the stored weight or 1 if not weight is stored for this relationship type
	 */
	public double getWeightDefault(RelationshipType relationshipType)
	{
		final Double ret = weights.get(relationshipType);

		return ret == null ? 1. : ret;
	}

	/**
	 * Put the weight of a given relationship type.
	 * 
	 * @param relationshipType a relationship type.
	 * @param weight The weight to set. Must be strictly positive.
	 */
	public void putWeight(RelationshipType relationshipType, double weight)
	{
		assert weight != 0;
		weights.put(relationshipType, weight);
	}

	/**
	 * Remove the weight of a given relationship type.
	 * 
	 * @param relationshipType a relationship type
	 * @return the removed weight.
	 */
	public Double removeWeight(RelationshipType relationshipType)
	{
		return weights.remove(relationshipType);
	}

	/**
	 * Get the map of weights by relationship type.
	 * 
	 * @return The map of weights by relationship type
	 */
	public Map <RelationshipType, Double> getWeights()
	{
		return weights;
	}

	@Override
	public String toString()
	{
		String ret = "";

		for (final Entry <RelationshipType, Double> entry: weights.entrySet())
		{
			if (!ret.isEmpty()) ret += " ";
			ret += String.format(Locale.ROOT, "%s:%.2f", entry.getKey(), entry.getValue());
		}

		return String.format("[%s]", ret);
	}

	/**
	 * All weights. The default weight for missing relationship types is one. Weights must be nonzero.
	 */
	private final Map <RelationshipType, Double> weights = new HashMap <RelationshipType, Double>();
}
