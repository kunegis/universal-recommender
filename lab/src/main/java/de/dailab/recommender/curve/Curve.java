package de.dailab.recommender.curve;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.RelationshipType;
import de.dailab.recommender.evaluation.PredictorSplitType;
import de.dailab.recommender.evaluation.Split;
import de.dailab.recommender.function.Function;
import de.dailab.recommender.function.Polynomial;
import de.dailab.recommender.latent.EigenvalueDecompositionPredictor;
import de.dailab.recommender.latent.LatentPredictor;
import de.dailab.recommender.latent.LatentPredictorModel;
import de.dailab.recommender.matrix.FullEntry;
import de.dailab.recommender.similarity.Similarity;
import de.dailab.recommender.similarity.SpectralTransformation;
import de.dailab.recommender.similarity.TransformedSimilarity;

/**
 * Learning spectral transformations by curve fitting.
 * <p>
 * The idea is to split the dataset into training and test set A and B, and to learn a function f(A) = B which is a
 * spectral transformation. By computing the eigenvalue decomposition A = U &Lambda U', a spectral transformation can be
 * learned by curve fitting between the eigenvalues in &Lambda; and the diagonal elements of U' B U.
 * 
 * @see <a href = "http://portal.acm.org/citation.cfm?id=1553447"><i>Learning spectral graph transformations for link
 *      prediction</i>, Kunegis & Lommatzsch, Proc. Int. Conf. on Machine Learning, 2009, pp. 561&ndash;568.</a>
 * 
 * @author kunegis
 */
public class Curve
{
	/**
	 * Learn a spectral transformation based on the eigenvalue decomposition. The spectral transformation is returned as
	 * a similarity, which can be passed to the eigenvalue decomposition predictor. A relationship type has to be
	 * specified.
	 * 
	 * @param dataset The dataset for training
	 * @param relationshipType The relationship type to train for
	 * @param rank The rank to use for learning
	 * @return The learned spectral transformation
	 */
	public static Similarity learnEigenvalueSimilarity(Dataset dataset, RelationshipType relationshipType, int rank)
	{
		final Split split = new Split(dataset, relationshipType, new PredictorSplitType());

		return learnEigenvalueSimilarity(split, rank);
	}

	/**
	 * Learn a spectral transformation in the given split, and of the given rank.
	 * 
	 * @param split The split to use for learning
	 * @param rank The rank to use for decomposition
	 * @return The learned similarity
	 */
	public static Similarity learnEigenvalueSimilarity(Split split, int rank)
	{
		final LatentPredictor predictor = new EigenvalueDecompositionPredictor(rank);
		final LatentPredictorModel predictorModel = predictor.build(split.training);

		return learnSimilarity(split, predictorModel);
	}

	/**
	 * Learn a similarity using a given split and latent predictor model for the training set of the split.
	 * 
	 * @param split The split to use for learning
	 * @param latentPredictorModel The latent predictor to use
	 * @return A learned similarity
	 */
	public static Similarity learnSimilarity(Split split, LatentPredictorModel latentPredictorModel)
	{
		final int rank = latentPredictorModel.getRank();

		/*
		 * Compute diagonal elements of V'BU
		 */
		final double vbu[] = new double[rank];

		final double u[][] = latentPredictorModel.getU().get(split.test.getSubject());
		final double v[][] = latentPredictorModel.getV().get(split.test.getObject());

		for (final FullEntry fullEntry: split.test.getMatrix().all())
		{
			for (int k = 0; k < rank; ++k)
			{
				vbu[k] += fullEntry.value * v[k][fullEntry.rowIndex] * u[k][fullEntry.colIndex];
			}
		}

		/*
		 * Curve fitting
		 */
		return new TransformedSimilarity(new SpectralTransformation(fit(latentPredictorModel.getLambda(), vbu)));
	}

	/**
	 * Curve fitting itself.
	 * 
	 * @param lambda The source values
	 * @param vbu The target values
	 * @return The learned function
	 */
	public static Function fit(double lambda[], double vbu[])
	{
		assert lambda.length == vbu.length;

		/*
		 * Fit vbu = alpha * lambda
		 */

		double xy = 0, xx = 0;
		for (int i = 0; i < lambda.length; ++i)
		{
			xy += lambda[i] * vbu[i];
			xx += lambda[i] * lambda[i];
		}

		final double alpha = xy / xx;

		return new Polynomial(0, alpha);
	}
}
