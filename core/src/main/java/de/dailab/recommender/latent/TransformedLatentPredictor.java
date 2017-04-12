package de.dailab.recommender.latent;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.predict.AbstractPredictor;
import de.dailab.recommender.predict.PredictorModel;
import de.dailab.recommender.similarity.SimilarityTransformation;

/**
 * A latent predictor transformed by a similarity transformation.
 * 
 * @author kunegis
 */
public class TransformedLatentPredictor
    extends AbstractPredictor
{
	/**
	 * A given latent predictor transformed by a given similarity transformation.
	 * 
	 * @param similarityTransformation The similarity transformation to apply to the latent predictor
	 * @param latentPredictor The latent predictor to transform
	 */
	public TransformedLatentPredictor(SimilarityTransformation similarityTransformation, LatentPredictor latentPredictor)
	{
		this.similarityTransformation = similarityTransformation;
		this.latentPredictor = latentPredictor;
	}

	private final SimilarityTransformation similarityTransformation;
	private final LatentPredictor latentPredictor;

	/**
	 * Get the similarity transformation.
	 * 
	 * @return The similarity transformation
	 */
	public SimilarityTransformation getSimilarityTransformation()
	{
		return similarityTransformation;
	}

	@Override
	public PredictorModel build(Dataset dataset, boolean update)
	{
		return new TransformedLatentPredictorModel(similarityTransformation, latentPredictor.build(dataset, update));
	}

	@Override
	public String toString()
	{
		return String.format("%s-%s", similarityTransformation, latentPredictor);
	}
}
