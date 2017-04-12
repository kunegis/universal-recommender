package de.dailab.recommender.latent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.EntitySet;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.RelationshipSet;
import de.dailab.recommender.dataset.RelationshipType;
import de.dailab.recommender.dataset.WeightRange;
import de.dailab.recommender.normalize.AdditiveNormalization;
import de.dailab.recommender.normalize.AdditiveNormalizationStrategy;
import de.dailab.recommender.normalize.MatrixStatistics;
import de.dailab.recommender.similarity.ScalarProduct;

/**
 * Normalized predictor model using a latent representation. Only additive normalization strategies are supported.
 * 
 * @author kunegis
 */
public class LatentNormalizationPredictorModel
    extends LatentPredictorModel
{
	/**
	 * A latent normalization predictor model.
	 * 
	 * @param dataset The dataset
	 * @param additiveNormalizationStrategy The additive normalization strategy to use
	 * @param update Converge immediately
	 */
	public LatentNormalizationPredictorModel(Dataset dataset,
	    AdditiveNormalizationStrategy additiveNormalizationStrategy, boolean update)
	{
		super(dataset, getRank(dataset), new ScalarProduct());

		additiveNormalizations = additiveNormalizationStrategy.applyAdditive(dataset);

		lambda = new double[rank];

		u = new HashMap <EntityType, double[][]>();
		v = new HashMap <EntityType, double[][]>();
		for (final EntitySet entitySet: dataset.getEntitySets())
		{
			u.put(entitySet.getType(), new double[rank][entitySet.size()]);
			v.put(entitySet.getType(), new double[rank][entitySet.size()]);
		}

		if (update) update();
	}

	/**
	 * Compute the rank needed for normalization of a dataset.
	 * 
	 * @param dataset The dataset
	 * @return The computed rank
	 */
	private static int getRank(Dataset dataset)
	{
		/*
		 * For each relationship set that is normalized, three ranks are needed: one for global effects, one for row
		 * effects and one for column effects.
		 */

		int ret = 0;

		for (final RelationshipSet relationshipSet: dataset.getRelationshipSets())
		{
			if (relationshipSet.getWeightRange() == WeightRange.WEIGHTED
			    || relationshipSet.getWeightRange() == WeightRange.SIGNED)
			{
				ret += 3;
			}
		}

		return ret;
	}

	@Override
	public void update()
	{
		int k = 0;

		for (final RelationshipSet relationshipSet: dataset.getRelationshipSets())
		{
			final AdditiveNormalization additiveNormalization = additiveNormalizations.get(relationshipSet.getType());

			if (additiveNormalization == null) continue;

			final double weightSum = additiveNormalization.weightOne + additiveNormalization.weightTotal
			    + additiveNormalization.weightRow + additiveNormalization.weightCol;
			final double relOne = additiveNormalization.weightOne / weightSum;
			final double relTotal = additiveNormalization.weightTotal / weightSum;
			final double relRow = additiveNormalization.weightRow / weightSum;
			final double relCol = additiveNormalization.weightCol / weightSum;

			for (final EntityType entityType: dataset.getEntityTypes())
			{
				final double ue[][] = u.get(entityType);
				Arrays.fill(ue[k + 0], Double.valueOf(0.));
				Arrays.fill(ue[k + 1], Double.valueOf(0.));
				Arrays.fill(ue[k + 2], Double.valueOf(0.));
			}

			final MatrixStatistics matrixStatistics = new MatrixStatistics(relationshipSet.getMatrix());

			/*
			 * Global component
			 */
			final double u0 = relOne + relTotal * matrixStatistics.getTotalMean();
			lambda[k + 0] = u0;
			Arrays.fill(u.get(relationshipSet.getSubject())[k + 0], Double.valueOf(1.));
			Arrays.fill(v.get(relationshipSet.getObject())[k + 0], Double.valueOf(1.));

			/*
			 * Row component
			 */
			{
				lambda[k + 1] = relRow;
				Arrays.fill(v.get(relationshipSet.getObject())[k + 1], Double.valueOf(1.));
				final double ue[] = u.get(relationshipSet.getSubject())[k + 1];
				for (int i = 0; i < relationshipSet.getMatrix().rows(); ++i)
				{
					final double mean = matrixStatistics.getRowMean(i);
					if (!Double.isInfinite(mean) && !Double.isNaN(mean)) ue[i] = mean;
				}
			}

			/*
			 * Column component
			 */
			{
				lambda[k + 2] = relCol;
				Arrays.fill(u.get(relationshipSet.getSubject())[k + 2], Double.valueOf(1.));
				final double ue[] = v.get(relationshipSet.getObject())[k + 2];
				for (int i = 0; i < relationshipSet.getMatrix().cols(); ++i)
				{
					final double mean = matrixStatistics.getColMean(i);
					if (!Double.isInfinite(mean) && !Double.isNaN(mean)) ue[i] = mean;
				}
			}

			k += 3;
		}

		assert k == rank;
	}

	@Override
	public double iterate()
	{
		update();
		return 0;
	}

	private final Map <RelationshipType, AdditiveNormalization> additiveNormalizations;
}
