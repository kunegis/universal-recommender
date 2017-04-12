package de.dailab.recommender.predict;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.DatasetEntry;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.similarity.Similarity;
import de.dailab.recommender.similarity.SimilarityRun;

/**
 * Prediction based on a similarity. This is a local predictor in the sense that a prediction based only on the direct
 * neighbors of two nodes.
 * <p>
 * A {@code SimilarityPredictor} gets passed a {@code Similarity} object in the constructor, which determines which
 * similarity measure is used.
 * <p>
 * Building and updating the predictor model is a no-op; all computations are online.
 * <p>
 * Only nodes present in either of two nodes are added to the similarity run.
 * <p>
 * The predictions will normally not correspond to any rating scale.
 * 
 * @author kunegis
 */
public class SimilarityPredictor
    extends AbstractPredictor
{
	/**
	 * A given similarity used for prediction.
	 * 
	 * @param similarity The similarity applied to the rows of the adjacency matrix
	 */
	public SimilarityPredictor(Similarity similarity)
	{
		this.similarity = similarity;
	}

	@Override
	public PredictorModel build(final Dataset dataset, boolean update)
	{
		return new PredictorModel()
		{
			@Override
			public Dataset getDataset()
			{
				return dataset;
			}

			@Override
			public double predict(Entity source, Entity target)
			{
				final SimilarityRun similarityRun = similarity.run();

				final Map <Entity, Double> sourceNeighbors = new HashMap <Entity, Double>();

				for (final DatasetEntry datasetEntry: dataset.getNeighbors(source))
				{
					final Double weight = sourceNeighbors.get(datasetEntry.entity);
					if (weight == null)
						sourceNeighbors.put(datasetEntry.entity, datasetEntry.weight);
					else
						sourceNeighbors.put(datasetEntry.entity, datasetEntry.weight + weight);
				}

				final Map <Entity, Double> targetNeighbors = new HashMap <Entity, Double>();

				for (final DatasetEntry datasetEntry: dataset.getNeighbors(target))
				{
					final Double weight = targetNeighbors.get(datasetEntry.entity);
					if (weight == null)
						targetNeighbors.put(datasetEntry.entity, datasetEntry.weight);
					else
						targetNeighbors.put(datasetEntry.entity, datasetEntry.weight + weight);
				}

				for (final Entry <Entity, Double> e: sourceNeighbors.entrySet())
				{
					final Entity entity = e.getKey();
					final Double targetWeight = targetNeighbors.get(entity);
					final double targetWeightActual = targetWeight == null ? 0 : targetWeight;

					similarityRun.add(e.getValue(), targetWeightActual, 1);
				}

				for (final Entry <Entity, Double> e: targetNeighbors.entrySet())
				{
					if (sourceNeighbors.containsKey(e.getKey())) continue;
					similarityRun.add(0, e.getValue(), 1);
				}

				final double ret = similarityRun.getSimilarity();
				assert !Double.isNaN(ret);
				assert !Double.isInfinite(ret);
				return ret;
			}

			@Override
			public void update()
			{
			/* Do nothing */
			}
		};
	}

	@Override
	public String toString()
	{
		return similarity.toString();
	}

	private final Similarity similarity;
}
