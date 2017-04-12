package de.dailab.recommender.predict;

import de.dailab.recommender.dataset.Dataset;

/**
 * A predictor where build(Dataset) is implemented with build(Dataset, true).
 * 
 * @author kunegis
 */
public abstract class AbstractPredictor
    implements Predictor
{
	@Override
	public PredictorModel build(Dataset dataset)
	{
		return build(dataset, true);
	}
}
