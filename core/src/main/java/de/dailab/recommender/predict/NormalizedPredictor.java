package de.dailab.recommender.predict;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.RelationshipSet;
import de.dailab.recommender.dataset.RelationshipType;
import de.dailab.recommender.latent.CompoundLatentPredictor;
import de.dailab.recommender.normalize.DefaultNormalizationStrategy;
import de.dailab.recommender.normalize.Normalization;
import de.dailab.recommender.normalize.NormalizationStrategy;
import de.dailab.recommender.normalize.Normalizer;
import de.dailab.recommender.normalize.NormalizerModel;

/**
 * Apply a predictor the normalization of a dataset.
 * <p>
 * This implementation is fast. However, it is not a latent predictor and can therefore not be used to implement a fast
 * recommender. To implement a fast recommender out of a normalized latent predictor, use CompoundLatentPredictor.
 * 
 * @author kunegis
 * 
 * @see CompoundLatentPredictor
 */
public class NormalizedPredictor
    extends AbstractPredictor
{
	/**
	 * A normalized version of the given predictor.
	 * <p>
	 * If the given predictor is NULL, this will only predict by normalization., for instance return item means when the
	 * normalization strategy consists of subtracting the item mean.
	 * 
	 * @param normalizationStrategy The normalization strategy to use
	 * @param predictor The original, non-normalized predictor; may be NULL
	 * @param relationshipType The relationship type to make predictions for. May be NULL, in which the first
	 *        relationship type matching the source and target entity types is used for denormalization.
	 */
	public NormalizedPredictor(NormalizationStrategy normalizationStrategy, Predictor predictor,
	    RelationshipType relationshipType)
	{
		this.normalizationStrategy = normalizationStrategy;
		this.predictor = predictor;
		this.relationshipType = relationshipType;
	}

	/**
	 * A normalized predictor using a normalization strategy and predictor, taking the first relationship type matching
	 * the source and target entities for denormalization.
	 * 
	 * @param normalizationStrategy A normalization strategy
	 * @param predictor The underlying predictor
	 */
	public NormalizedPredictor(NormalizationStrategy normalizationStrategy, Predictor predictor)
	{
		this(normalizationStrategy, predictor, null);
	}

	/**
	 * Normalize a given predictor using the default normalization.
	 * 
	 * @param predictor The underlying predictor
	 */
	public NormalizedPredictor(Predictor predictor)
	{
		this(new DefaultNormalizationStrategy(), predictor);
	}

	/**
	 * Predictor that only uses the default normalization strategy.
	 */
	public NormalizedPredictor()
	{
		this(new DefaultNormalizationStrategy(), null);
	}

	private final NormalizationStrategy normalizationStrategy;

	/**
	 * The underlying predictor. May be NULL, in which case the underlying predictor is taken to always return zero.
	 */
	private final Predictor predictor;

	private final RelationshipType relationshipType;

	@Override
	public Model build(Dataset dataset, boolean update)
	{
		return new Model(dataset, update);
	}

	@Override
	public String toString()
	{
		return String.format("Normalized(%s%s)%s", normalizationStrategy, relationshipType == null ? "" : ", "
		    + relationshipType, predictor == null ? "" : "-" + predictor);
	}

	private class Model
	    implements PredictorModel
	{
		public Model(Dataset dataset, boolean update)
		{
			this.dataset = dataset;

			normalizers = normalizationStrategy.apply(dataset);

			for (final Entry <RelationshipType, Normalizer> entry: normalizers.entrySet())
			{
				this.normalizerModels.put(entry.getKey(), entry.getValue().build(
				    dataset.getRelationshipSet(entry.getKey()).getMatrix()));
			}

			normalizedDataset = Normalization.normalize(normalizerModels, dataset);
			normalizedPredictorModel = predictor == null ? null : predictor.build(normalizedDataset, update);
		}

		@Override
		public double predict(Entity source, Entity target)
		{
			double prediction = normalizedPredictorModel == null ? 0 : normalizedPredictorModel.predict(source, target);

			RelationshipType relationshipType = NormalizedPredictor.this.relationshipType;

			if (relationshipType == null)
			{
				for (final RelationshipSet relationshipSet: dataset.getRelationshipSets())
				{
					if (relationshipSet.getSubject().equals(source.getType())
					    && relationshipSet.getObject().equals(target.getType()))
					{
						relationshipType = relationshipSet.getType();
						break;
					}
				}
			}

			if (relationshipType != null)
			{
				final NormalizerModel normalizerModel = normalizerModels.get(relationshipType);
				if (normalizerModel != null)
				    prediction = normalizerModel.denormalize(prediction, source.getId(), target.getId());
			}

			return prediction;
		}

		@Override
		public void update()
		{
			/* Update the normalizer models, then the predictor model. */

			for (final Entry <RelationshipType, NormalizerModel> entry: normalizerModels.entrySet())
			{
				entry.setValue(normalizers.get(entry.getKey()).build(
				    dataset.getRelationshipSet(entry.getKey()).getMatrix()));
			}

			/* First argument means: write normalized dataset into this dataset */
			Normalization.updateNormalizedDataset(normalizedDataset, normalizerModels, dataset);

			if (normalizedPredictorModel != null) normalizedPredictorModel.update();
		}

		@Override
		public Dataset getDataset()
		{
			return dataset;
		}

		/**
		 * The built predictor model. May be NULL to denote a predictor that always returns zero.
		 */
		private final PredictorModel normalizedPredictorModel;

		private final Map <RelationshipType, NormalizerModel> normalizerModels = new HashMap <RelationshipType, NormalizerModel>();
		private final Dataset dataset;
		private final Dataset normalizedDataset;
		private final Map <RelationshipType, Normalizer> normalizers;
	}
}
