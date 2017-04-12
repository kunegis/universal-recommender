package de.dailab.recommender.normalize;

import java.util.Map;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.RelationshipType;

/**
 * A normalization strategy encodes a specific way of normalizing a complete dataset.
 * <p>
 * The method apply() applies this algorithm to a given dataset and returns a normalizer for each relationship type.
 * 
 * @author kunegis
 */
public interface NormalizationStrategy
{
	/**
	 * Given a dataset, compute normalizers suited to each relationship type in a given dataset.
	 * 
	 * @param dataset A dataset
	 * @return Normalizers by relationship type. Unrepresented relationship types denote that no normalization is
	 *         needed.
	 */
	Map <RelationshipType, Normalizer> apply(Dataset dataset);

	/**
	 * @return the name of the normalization strategy.
	 */
	@Override
	public String toString();
}
