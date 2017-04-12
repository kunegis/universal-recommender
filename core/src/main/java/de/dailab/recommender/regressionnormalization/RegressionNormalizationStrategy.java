package de.dailab.recommender.regressionnormalization;

import java.util.HashMap;
import java.util.Map;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.RelationshipSet;
import de.dailab.recommender.dataset.RelationshipType;
import de.dailab.recommender.dataset.WeightRange;
import de.dailab.recommender.normalize.AdditiveNormalization;
import de.dailab.recommender.normalize.AdditiveNormalizationStrategy;

/**
 * An additive normalization strategy: compute parameters for additive normalization by linear regression.
 * 
 * @author kunegis
 * @see <a href="http://portal.acm.org/citation.cfm?id=1401944">Factorization Meets the Neighborhood: a Multifaceted
 *      Collaborative Filtering Model, Yehuda Koren (KDD 2008)</a>
 */
public class RegressionNormalizationStrategy
    extends AdditiveNormalizationStrategy
{
	@Override
	public Map <RelationshipType, AdditiveNormalization> applyAdditive(Dataset dataset)
	{
		final Map <RelationshipType, AdditiveNormalization> ret = new HashMap <RelationshipType, AdditiveNormalization>();

		for (final RelationshipSet relationshipSet: dataset.getRelationshipSets())
		{
			if (relationshipSet.getWeightRange() != WeightRange.WEIGHTED) continue;

			ret.put(relationshipSet.getType(), new RegressionAdditiveNormalization(relationshipSet));
		}

		return ret;
	}
}
