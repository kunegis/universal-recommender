package de.dailab.recommender.latent;

import java.util.HashMap;
import java.util.Map;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.EntitySet;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.RelationshipSet;
import de.dailab.recommender.predict.RelationshipTypePonderation;
import de.dailab.recommender.similarity.ScalarProduct;
import de.dailab.recommender.similarity.Similarity;

/**
 * A predictor model computed from the eigendecomposition of the complex adjacency matrix. Uses the QR algorithm.
 * <p>
 * The model supports iterative updates using the iterate() method.
 * <p>
 * Since the eigenvalue decomposition is computed, unipartite relationship types are considered symmetric.
 * 
 * @author kunegis
 * @see <a href="http://en.wikipedia.org/wiki/QR_algorithm">Wikipedia: QR algorithm</a>
 */
class EigenvalueDecompositionPredictorModel
    extends LatentPredictorModel
{
	/*
	 * The eigenvalue decomposition is symmetric, so U and V are identical.
	 */

	/**
	 * Build latent predictor model by QR decomposition of complex matrix.
	 * 
	 * @param dataset dataset to use
	 * @param update whether to iterate until convergence in this constructor call. converge() can also be called later.
	 * @param rank reduced rank
	 * @param relationshipTypePonderation relative weights of relationship types. May be NULL.
	 * @param similarity The similarity to use for computing predictions
	 */
	public EigenvalueDecompositionPredictorModel(Dataset dataset, boolean update, int rank,
	    RelationshipTypePonderation relationshipTypePonderation, Similarity similarity)
	{
		super(dataset, rank, similarity);

		if (relationshipTypePonderation == null) relationshipTypePonderation = new RelationshipTypePonderation();

		this.relationshipTypePonderation = relationshipTypePonderation;

		/*
		 * Initialize U and V
		 */
		u = new HashMap <EntityType, double[][]>();
		for (final EntitySet entitySet: dataset.getEntitySets())
		{
			final double ue[][] = new double[rank][entitySet.size()];

			u.put(entitySet.getType(), ue);

			for (int i = 0; i < entitySet.size(); ++i)
				for (int j = 0; j < rank; ++j)
					ue[j][i] = Math.random();
		}

		v = u;

		/*
		 * Initialize Lambda
		 */
		lambda = new double[rank];
		for (int i = 0; i < rank; ++i)
			lambda[i] = 1.;

		/*
		 * Decomposition
		 */
		if (update) update();
	}

	public EigenvalueDecompositionPredictorModel(Dataset dataset, boolean update, int rank,
	    RelationshipTypePonderation relationshipTypePonderation)
	{
		this(dataset, update, rank, relationshipTypePonderation, new ScalarProduct());
	}

	@Override
	public double iterate()
	{
		/*
		 * One iteration of the QR algorithm multiplies the current U with the dataset matrix and then orthogonalizes U,
		 * giving new values of Lambda.
		 */

		final Map <EntityType, double[][]> uOld = u;

		u = new HashMap <EntityType, double[][]>();

		for (final Map.Entry <EntityType, double[][]> entry: uOld.entrySet())
		{
			final EntityType entityType = entry.getKey();
			final double uEntity[][] = entry.getValue();
			assert uEntity.length == rank;

			assert uEntity[0].length == dataset.getEntitySet(entityType).size();

			final double matrix[][] = new double[rank][uEntity[0].length];
			u.put(entityType, matrix);
		}

		for (final RelationshipSet relationshipSet: getDataset().getRelationshipSets())
		{
			final double weight = relationshipTypePonderation.getWeightDefault(relationshipSet.getType());

			final double uSubject[][] = u.get(relationshipSet.getSubject());
			final double uObject[][] = u.get(relationshipSet.getObject());
			final double uOldSubject[][] = uOld.get(relationshipSet.getSubject());
			final double uOldObject[][] = uOld.get(relationshipSet.getObject());

			for (int k = 0; k < rank; ++k)
			{
				relationshipSet.getMatrix().mult(uOldObject[k], uSubject[k], weight);
				relationshipSet.getMatrix().multT(uOldSubject[k], uObject[k], weight);
			}
		}

		v = u;

		return orthogonalize();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * Iterate until convergence. iterate() may also be called directly to control the rate of iteration.
	 * <p>
	 * This implementation iterates until the convergence value is smaller than a fixed epsilon.
	 */
	@Override
	public void update()
	{
		double convergence;
		do
			convergence = iterate();
		while (convergence > EPSILON);
	}

	/**
	 * Ponderation of relationship types. Not NULL.
	 */
	private final RelationshipTypePonderation relationshipTypePonderation;

	/**
	 * The maximum convergence value.
	 * <p>
	 * This should be lower for more precision, but since the QR algorithm is slow we use a large value.
	 */
	private static final double EPSILON = 1e-5;
}
