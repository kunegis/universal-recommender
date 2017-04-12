package de.dailab.recommender.latent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.predict.Predictor;
import de.dailab.recommender.predict.PredictorList;
import de.dailab.recommender.predict.PredictorModel;
import de.dailab.recommender.similarity.SimilarityTransformation;

/**
 * A predictor list consisting of the same latent predictor to with a list of similarity transformations are applied.
 * <p>
 * Model building is fast because only one model is built, to which all similarity transformations are applied.
 * 
 * @author kunegis
 */
public class LatentPredictorList
    implements PredictorList
{
	/**
	 * The list of similarity transformations is applied to the given latent predictor.
	 * 
	 * @param latentPredictor The latent predictor to transform
	 * @param similarityTransformations The similarity transformations to apply; NULL denotes a latent predictor without
	 *        additional similarity transformation
	 */
	public LatentPredictorList(LatentPredictor latentPredictor,
	    List <SimilarityTransformation> similarityTransformations)
	{
		this.latentPredictor = latentPredictor;
		this.similarityTransformations = similarityTransformations;
	}

	/**
	 * The similarity transformations are applied to the given latent predictor.
	 * 
	 * @param latentPredictor The latent predictor to modify with similarity transformations
	 * @param similarityTransformations The similarity transformations to apply to the latent predictor
	 */
	public LatentPredictorList(LatentPredictor latentPredictor, SimilarityTransformation... similarityTransformations)
	{
		this.latentPredictor = latentPredictor;

		this.similarityTransformations = new ArrayList <SimilarityTransformation>(similarityTransformations.length);
		for (final SimilarityTransformation similarityTransformation: similarityTransformations)
			this.similarityTransformations.add(similarityTransformation);
	}

	private final LatentPredictor latentPredictor;

	private final List <SimilarityTransformation> similarityTransformations;

	@Override
	public List <Predictor> getPredictors()
	{
		final List <Predictor> ret = new ArrayList <Predictor>(similarityTransformations.size());

		for (final SimilarityTransformation similarityTransformation: similarityTransformations)
		{
			if (similarityTransformation == null)
				ret.add(latentPredictor);
			else
				ret.add(new TransformedLatentPredictor(similarityTransformation, latentPredictor));
		}

		return ret;
	}

	@Override
	public Iterator <PredictorModel> build(Dataset dataset)
	{
		final LatentPredictorModel latentPredictorModel = latentPredictor.build(dataset);

		final Iterator <SimilarityTransformation> iterator = similarityTransformations.iterator();

		return new Iterator <PredictorModel>()
		{
			@Override
			public boolean hasNext()
			{
				return iterator.hasNext();
			}

			@Override
			public PredictorModel next()
			{
				final SimilarityTransformation similarityTransformation = iterator.next();
				if (similarityTransformation == null)
					return latentPredictorModel;
				else
					return new TransformedLatentPredictorModel(similarityTransformation, latentPredictorModel);
			}

			@Override
			public void remove()
			{
				throw new UnsupportedOperationException();
			}
		};
	}
}
