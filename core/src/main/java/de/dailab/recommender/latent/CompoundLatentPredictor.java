package de.dailab.recommender.latent;

import java.util.HashMap;
import java.util.Map;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.RelationshipSet;
import de.dailab.recommender.dataset.RelationshipType;
import de.dailab.recommender.dataset.WeightRange;
import de.dailab.recommender.normalize.Normalization;
import de.dailab.recommender.normalize.NormalizerModel;
import de.dailab.recommender.similarity.CompoundSimilarity;

/**
 * A combination of two latent predictors. The first predictor is applied to the original dataset, then the predicted
 * approximation is subtracted from the dataset and the second latent predictor is applied. The resulting predictor
 * contains the two predictor model's data as blocks in the eigenvalues and eigenvectors.
 * <p>
 * In typical use the first latent predictor can be considered an additive normalization.
 * <p>
 * Both predictors are used with their native similarities, which are added to give the final result.
 * 
 * @author kunegis
 */
public class CompoundLatentPredictor
    extends AbstractLatentPredictor
{
	/**
	 * A compound latent predictor using two given latent predictors.
	 * 
	 * @param latentPredictor_1 The first latent predictor, corresponding to normalization
	 * @param latentPredictor_2 The underlying latent predictor
	 */
	public CompoundLatentPredictor(LatentPredictor latentPredictor_1, LatentPredictor latentPredictor_2)
	{
		this.latentPredictor_1 = latentPredictor_1;
		this.latentPredictor_2 = latentPredictor_2;
	}

	@Override
	public LatentPredictorModel build(Dataset dataset, boolean update)
	{
		/*
		 * We have to build to model in any case here because we have to normalize with the first predictor.
		 */
		final LatentPredictorModel latentPredictorModel_1 = latentPredictor_1.build(dataset, true);

		final Map <RelationshipType, NormalizerModel> normalizerModels = buildNormalizerModels(latentPredictorModel_1);

		final Dataset normalizedDataset = Normalization.normalize(normalizerModels, dataset);

		final LatentPredictorModel latentPredictorModel_2 = latentPredictor_2.build(normalizedDataset, update);

		return new CompoundLatentPredictorModel(latentPredictorModel_1, latentPredictorModel_2, dataset,
		    normalizedDataset, normalizerModels);
	}

	private final LatentPredictor latentPredictor_1;
	private final LatentPredictor latentPredictor_2;

	/**
	 * Compute normalizer models backed by a latent predictor model.
	 * 
	 * @param latentPredictorModel a latent predictor model
	 * @return normalizers corresponding to subtraction of predictions made by the given latent predictor model. The
	 *         returned normalizers are back by the predictor model.
	 */
	private static Map <RelationshipType, NormalizerModel> buildNormalizerModels(
	    final LatentPredictorModel latentPredictorModel)
	{
		final Map <RelationshipType, NormalizerModel> ret = new HashMap <RelationshipType, NormalizerModel>();

		for (final RelationshipSet relationshipSet: latentPredictorModel.dataset.getRelationshipSets())
		{
			if (relationshipSet.getWeightRange() == WeightRange.UNWEIGHTED) continue;

			final RelationshipType relationshipType = relationshipSet.getType();

			final NormalizerModel normalizerModel = new NormalizerModel()
			{
				@Override
				public double denormalize(double value, int rowIndex, int colIndex)
				{
					final double prediction = latentPredictorModel.predict(new Entity(relationshipSet.getSubject(),
					    rowIndex), new Entity(relationshipSet.getObject(), colIndex));

					return value + prediction;
				}

				@Override
				public double normalize(double value, int rowIndex, int colIndex)
				{
					final double prediction = latentPredictorModel.predict(new Entity(relationshipSet.getSubject(),
					    rowIndex), new Entity(relationshipSet.getObject(), colIndex));

					return value - prediction;
				}
			};

			ret.put(relationshipType, normalizerModel);
		}

		return ret;
	}

	@Override
	public String toString()
	{
		return String.format("%s-%s", latentPredictor_1, latentPredictor_2);
	}
}

/**
 * A latent predictor model built from the combination of two given latent predictor models.
 * 
 * @author kunegis
 */
class CompoundLatentPredictorModel
    extends LatentPredictorModel
{
	/**
	 * Create a latent predictor model backed by two given latent predictor models.
	 * <p>
	 * In the created object, U is backed by the given latent predictor models, but Lambda is copied.
	 * 
	 * @param latentPredictorModel_1 First latent predictor model
	 * @param latentPredictorModel_2 Second latent predictor model
	 * @param dataset The initial dataset
	 * @param normalizedDataset The dataset
	 * @param normalizerModels normalizer models by relationship type corresponding to the first latent predictor model.
	 */
	public CompoundLatentPredictorModel(LatentPredictorModel latentPredictorModel_1,
	    LatentPredictorModel latentPredictorModel_2, Dataset dataset, Dataset normalizedDataset,
	    Map <RelationshipType, NormalizerModel> normalizerModels)
	{
		super(normalizedDataset, latentPredictorModel_1.rank + latentPredictorModel_2.rank, new CompoundSimilarity(
		    latentPredictorModel_1.rank, latentPredictorModel_2.rank, latentPredictorModel_1.getSimilarity(),
		    latentPredictorModel_2.getSimilarity())
		// new ScalarProduct()
		);

		this.latentPredictorModel_1 = latentPredictorModel_1;
		this.latentPredictorModel_2 = latentPredictorModel_2;
		this.dataset = dataset;
		this.normalizedDataset = normalizedDataset;
		this.normalizerModels = normalizerModels;

		final int k_1 = latentPredictorModel_1.rank;
		final int k_2 = latentPredictorModel_2.rank;

		this.lambda = new double[k_1 + k_2];

		System.arraycopy(latentPredictorModel_1.lambda, 0, lambda, 0, k_1);
		System.arraycopy(latentPredictorModel_2.lambda, 0, lambda, k_1, k_2);

		this.u = new HashMap <EntityType, double[][]>();
		for (final EntityType entityType: dataset.getEntityTypes())
		{
			final double u[][] = new double[k_1 + k_2][];

			System.arraycopy(latentPredictorModel_1.u.get(entityType), 0, u, 0, k_1);
			System.arraycopy(latentPredictorModel_2.u.get(entityType), 0, u, k_1, k_2);
			this.u.put(entityType, u);
		}

		this.v = new HashMap <EntityType, double[][]>();
		for (final EntityType entityType: dataset.getEntityTypes())
		{
			final double v[][] = new double[k_1 + k_2][];

			System.arraycopy(latentPredictorModel_1.v.get(entityType), 0, v, 0, k_1);
			System.arraycopy(latentPredictorModel_2.v.get(entityType), 0, v, k_1, k_2);
			this.v.put(entityType, v);
		}
	}

	private final LatentPredictorModel latentPredictorModel_1, latentPredictorModel_2;
	private final Dataset dataset;
	private final Dataset normalizedDataset;
	private final Map <RelationshipType, NormalizerModel> normalizerModels;

	@Override
	public double iterate()
	{
		latentPredictorModel_1.iterate();
		Normalization.updateNormalizedDataset(normalizedDataset, normalizerModels, dataset);
		return latentPredictorModel_2.iterate();
	}

	@Override
	public void update()
	{
		latentPredictorModel_1.update();
		Normalization.updateNormalizedDataset(normalizedDataset, normalizerModels, dataset);
		latentPredictorModel_2.update();
	}
}
