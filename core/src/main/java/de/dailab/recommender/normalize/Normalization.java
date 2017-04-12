package de.dailab.recommender.normalize;

import java.util.Map;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.EntitySet;
import de.dailab.recommender.dataset.RelationshipSet;
import de.dailab.recommender.dataset.RelationshipType;
import de.dailab.recommender.dataset.WeightRange;
import de.dailab.recommender.matrix.FullEntry;
import de.dailab.recommender.matrix.Matrix;
import de.dailab.recommender.matrix.template.MatrixFactory;

/**
 * Utilities for normalization. This class contains static methods.
 * 
 * @author kunegis
 */
public class Normalization
{
	/**
	 * Normalize a dataset using given normalizer models.
	 * 
	 * @param normalizerModels The normalizer models built for the given dataset. Not all relationship types have to be
	 *        present.
	 * @param dataset The dataset to normalize.
	 * 
	 * @return The normalized dataset. Unnormalized relationship types are shared with the given dataset.
	 */
	public static Dataset normalize(Map <RelationshipType, NormalizerModel> normalizerModels, Dataset dataset)
	{
		final Dataset ret = new Dataset();

		for (final EntitySet entitySet: dataset.getEntitySets())
		{
			ret.addEntitySet(entitySet);
		}

		for (final RelationshipSet relationshipSet: dataset.getRelationshipSets())
		{
			final NormalizerModel normalizerModel = normalizerModels.get(relationshipSet.getType());

			if (normalizerModel == null)
				ret.addRelationshipSet(relationshipSet);
			else
				ret.addRelationshipSet(normalize(normalizerModel, relationshipSet));
		}

		return ret;
	}

	/**
	 * Update a normalized dataset according to change in the corresponding unnormalized dataset.
	 * 
	 * @param normalizedDataset The previously-normalized dataset to update
	 * @param normalizerModels The previously-compute normalized models
	 * @param dataset The initial unnormalized dataset
	 */
	public static void updateNormalizedDataset(Dataset normalizedDataset,
	    Map <RelationshipType, NormalizerModel> normalizerModels, Dataset dataset)
	{
		for (final RelationshipSet relationshipSet: dataset.getRelationshipSets())
		{
			final NormalizerModel normalizerModel = normalizerModels.get(relationshipSet.getType());

			if (normalizerModel == null) continue;

			normalizedDataset.getRelationshipSet(relationshipSet.getType()).setMatrix(
			    getNormalizedMatrix(normalizerModel, relationshipSet));
		}
	}

	/**
	 * Normalize a relationship set.
	 * 
	 * @param normalizerModel The normalizer model to use. Must have been built for the given relationship set.
	 * @param relationshipSet A relationship set to normalize. Not modified.
	 * @return A copy of RELATIONSHIP_SET with normalized values
	 */
	public static RelationshipSet normalize(NormalizerModel normalizerModel, RelationshipSet relationshipSet)
	{
		assert relationshipSet.getWeightRange() != WeightRange.UNWEIGHTED;

		final RelationshipSet ret = new RelationshipSet(relationshipSet);

		final Matrix matrix = getNormalizedMatrix(normalizerModel, relationshipSet);

		ret.setMatrix(matrix);

		return ret;
	}

	/**
	 * Return the normalized matrix corresponding to a given normalizer mode and relationship set
	 * 
	 * @param normalizerModel a normalizer model
	 * @param relationshipSet the relationship set to normalize
	 * @return a new matrix with normalized relationship weights
	 */
	public static Matrix getNormalizedMatrix(NormalizerModel normalizerModel, RelationshipSet relationshipSet)
	{
		final Matrix ret = MatrixFactory.newMemoryMatrix(relationshipSet.getMatrix().rows(), relationshipSet
		    .getMatrix().cols(), float.class.getSimpleName());

// final Matrix ret = new MemoryMatrix(relationshipSet.getMatrix().rows(), relationshipSet.getMatrix().cols());

		for (final FullEntry fullEntry: relationshipSet.getMatrix().all())
		{
			ret.set(fullEntry.rowIndex, fullEntry.colIndex, normalizerModel.normalize(fullEntry.value,
			    fullEntry.rowIndex, fullEntry.colIndex));
		}

		return ret;
	}
}
