package de.dailab.recommender.latent;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.predict.PredictorModel;
import de.dailab.recommender.similarity.SimilarityTransformation;

/**
 * A latent predictor model to which a similarity transformation is applied.
 * 
 * @author kunegis
 */
public class TransformedLatentPredictorModel
    implements PredictorModel
{
	/**
	 * Apply the given similarity transformation to the given latent predictor model.
	 * 
	 * @param similarityTransformation The similarity transformation to apply
	 * @param latentPredictorModel The latent predictor model to transform
	 */
	public TransformedLatentPredictorModel(SimilarityTransformation similarityTransformation,
	    LatentPredictorModel latentPredictorModel)
	{
		this.similarityTransformation = similarityTransformation;
		this.latentPredictorModel = latentPredictorModel;
	}

	private final SimilarityTransformation similarityTransformation;
	private final LatentPredictorModel latentPredictorModel;

	@Override
	public Dataset getDataset()
	{
		return latentPredictorModel.getDataset();
	}

	@Override
	public double predict(Entity source, Entity target)
	{
		return latentPredictorModel.predict(source, target, similarityTransformation);
	}

	@Override
	public void update()
	{
		latentPredictorModel.update();
	}
}
