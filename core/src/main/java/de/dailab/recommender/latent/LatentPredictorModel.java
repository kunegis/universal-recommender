package de.dailab.recommender.latent;

import java.util.Collections;
import java.util.Map;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.predict.PredictorModel;
import de.dailab.recommender.similarity.ScalarProduct;
import de.dailab.recommender.similarity.Similarity;
import de.dailab.recommender.similarity.SimilarityRun;
import de.dailab.recommender.similarity.SimilarityTransformation;

/**
 * A predictor model that models entities as low-rank vectors and computes predictions using a similarity measure on
 * these vectors.
 * <p>
 * Implementations correspond to various ways of decomposing the aggregated adjacency matrix or another related matrix.
 * <p>
 * The predict() method is implemented in this abstract class and computes the scalar product. Implementations only fill
 * U, V and Lambda in the constructor. For two items i and j, the prediction is then taken as the similarity computes
 * with the given similarity measure.
 * 
 * @see Similarity
 * 
 * @author kunegis
 */
public abstract class LatentPredictorModel
    implements PredictorModel
{
	/**
	 * Initialize the final fields dataset and rank. U and Lambda will be NULL initially and have to be set by the
	 * implementing constructor.
	 * 
	 * @param dataset The dataset. This parameter is only used to implement PredictorModel.getDataset().
	 * @param rank The reduced rank. The rank may be zero, in which case all predictions will be zero.
	 * @param similarity The vector similarity to use for computing the predictions. The similarity measure is applied
	 *        to any two latent entity vectors. ScalarProduct() represents the usual matrix multiplication.
	 */
	public LatentPredictorModel(Dataset dataset, int rank, Similarity similarity)
	{
		assert rank >= 0;

		this.dataset = dataset;
		this.rank = rank;
		this.similarity = similarity;
	}

	/**
	 * A latent predictor model using the scalar product as similarity, which corresponds to the usual matrix
	 * multiplication.
	 * 
	 * @param dataset The dataset; only used to implement getDataset()
	 * @param rank The rank
	 */
	public LatentPredictorModel(Dataset dataset, int rank)
	{
		this(dataset, rank, new ScalarProduct());
	}

	@Override
	public double predict(Entity source, Entity target)
	{
		return predict(source, target, null);
	}

	/**
	 * Predict by taking into account the given similarity function.
	 * 
	 * @param source The source entity
	 * @param target The target entity
	 * @param similarityFunction The similarity function; may be NULL
	 * @return The prediction
	 */
	public double predict(Entity source, Entity target, SimilarityTransformation similarityFunction)
	{
		assert lambda.length == rank;

		final double uSource[][] = u.get(source.getType());
		final double vTarget[][] = v.get(target.getType());

		if (uSource == null || vTarget == null)
		    throw new IllegalArgumentException("Source or target entity type not contained in dataset");

		assert uSource.length == rank;
		assert vTarget.length == rank;

		if (rank > 0)
		{
			assert source.getId() >= 0 && source.getId() < uSource[0].length;
			assert target.getId() >= 0 && target.getId() < vTarget[0].length;
		}

		final SimilarityRun similarityRun = similarityFunction == null ? similarity.run() : similarityFunction
		    .run(similarity);

		for (int i = 0; i < rank; ++i)
		{
			similarityRun.add(uSource[i][source.getId()], vTarget[i][target.getId()], lambda[i]);
		}

		final double ret = similarityRun.getSimilarity();

		assert !Double.isNaN(ret);

		return ret;
	}

	/**
	 * Perform one iteration step towards updating the dataset.
	 * <p>
	 * This method exists in addition to PredictorModel.update(). In implementations without iterative decomposition,
	 * iterate() typically calls update(). Otherwise, update() calls iterate() until convergence.
	 * <p>
	 * The return value is a measure of convergence. It is nonnegative and approaches zero as the decomposition
	 * converges. The value should be independent of the size of the dataset, so an overall epsilon value can be used
	 * for convergence checking. Implementations that always converge in one iteration step return 0.
	 * 
	 * @return a measure of convergence
	 */
	public abstract double iterate();

	@Override
	public Dataset getDataset()
	{
		return dataset;
	}

	/**
	 * @return The singular values; constant
	 */
	public double[] getLambda()
	{
		return lambda;
	}

	/**
	 * @return the left eigenvectors
	 */
	public Map <EntityType, double[][]> getU()
	{
		return Collections.unmodifiableMap(u);
	}

	/**
	 * @return the right eigenvectors
	 */
	public Map <EntityType, double[][]> getV()
	{
		return Collections.unmodifiableMap(v);
	}

	/**
	 * @return The rank, the number of latent dimensions
	 */
	public int getRank()
	{
		return rank;
	}

	/**
	 * The similarity used by this latent predictor model.
	 * 
	 * @return The similarity measure
	 */
	public Similarity getSimilarity()
	{
		return similarity;
	}

	/**
	 * Orthogonalize this.u using the Gram&ndash;Schmidt process, and set this.lambda to the new singular values. This.v
	 * is not changed.
	 * <p>
	 * This procedure is common enough in iterative latent decompositions that it is included here.
	 * <p>
	 * The return value can serve as a return value for iterate().
	 * 
	 * @return The root mean sum of squared difference to the old singular values divided by the largest singular value.
	 * 
	 * @see <a
	 *      href="http://en.wikipedia.org/wiki/Gram%E2%80%93Schmidt_process">Wikipedia:&nbsp;Gram&ndash;Schmidt&nbsp;process</a>
	 */
	protected double orthogonalize()
	{
		/**
		 * Sum of squared differences between new and old lambdas.
		 */
		double sumConvergence = 0;

		for (int k = 0; k < rank; ++k)
		{
			/* Project to space orthogonal to previous eigenvectors */
			for (int m = 0; m < k; ++m)
			{
				double lm = 0.;
				for (final double ue[][]: u.values())
					for (int i = 0; i < ue[0].length; ++i)
						lm += ue[m][i] * ue[k][i];
				for (final double ue[][]: u.values())
					for (int i = 0; i < ue[0].length; ++i)
						ue[k][i] -= lm * ue[m][i];
			}

			/* Normalize */
			double squareSum = 0;
			double sum = 0;
			for (final double ue[][]: u.values())
			{
				for (int i = 0; i < ue[0].length; ++i)
				{
					final double mli = ue[k][i];
					assert !Double.isNaN(mli) && !Double.isInfinite(mli);
					squareSum += mli * mli;
					sum += mli;
				}
			}

			assert squareSum >= 0;

			final double oldLambda = lambda[k];

			final double newLambda = Math.sqrt(squareSum) * Math.signum(sum);

			lambda[k] = newLambda;

			sumConvergence += (oldLambda - lambda[k]) * (oldLambda - lambda[k]);

			final double divisor = lambda[k] == 0 ? 1 : lambda[k];

			for (final double ue[][]: u.values())
				for (int i = 0; i < ue[0].length; ++i)
				{
					ue[k][i] /= divisor;
				}
		}

		final double convergence = Math.sqrt(sumConvergence / rank / Math.abs(lambda[0]));

		return convergence;
	}

	protected final Dataset dataset;

	/**
	 * The rank of this decomposition.
	 * <p>
	 * Must be at least 1.
	 */
	protected final int rank;

	private final Similarity similarity;

	/*
	 * The compound matrix is decomposed as A = U Lambda V', stored as U, V and Lambda below.
	 */

	/**
	 * The latent vectors by entity type and entity ID. First index are columns (rank / latent dimension), second index
	 * are entity IDs.
	 * <p>
	 * All entries must be actual numbers (i.e. no infinities or NaNs).
	 */
	protected Map <EntityType, double[][]> u;

	/**
	 * The right eigenvectors.
	 */
	protected Map <EntityType, double[][]> v;

	/**
	 * The singular values. Index is (k). The values don't have to be ordered.
	 */
	protected double lambda[];
}
