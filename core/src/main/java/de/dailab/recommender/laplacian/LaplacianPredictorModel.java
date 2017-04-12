package de.dailab.recommender.laplacian;

import java.util.HashMap;
import java.util.Map;

import de.dailab.recommender.average.Average;
import de.dailab.recommender.average.AverageRun;
import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.EntitySet;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.RelationshipSet;
import de.dailab.recommender.latent.LatentPredictorModel;
import de.dailab.recommender.matrix.Entry;
import de.dailab.recommender.predict.RelationshipTypePonderation;
import de.dailab.recommender.similarity.EuclideanSimilarity;

/**
 * The predictor model returned by the Laplacian predictor.
 * 
 * @author kunegis
 */
class LaplacianPredictorModel
    extends LatentPredictorModel
{
	public LaplacianPredictorModel(Dataset dataset, int rank, Average average,
	    RelationshipTypePonderation relationshipTypePonderation, boolean update)
	{
		super(dataset, rank, new EuclideanSimilarity(true));

		this.average = average;
		this.relationshipTypePonderation = relationshipTypePonderation == null ? new RelationshipTypePonderation()
		    : relationshipTypePonderation;

		lambda = new double[rank];

		u = new HashMap <EntityType, double[][]>();

		for (final EntitySet entitySet: dataset.getEntitySets())
		{
			final double uEntity[][] = new double[rank][entitySet.size()];

			for (int i = 0; i < rank; ++i)
				for (int j = 0; j < entitySet.size(); ++j)
					uEntity[i][j] = Math.random();

			u.put(entitySet.getType(), uEntity);
		}

		v = u;

		if (update) update();
	}

	@Override
	public double iterate()
	{
		final Map <EntityType, double[][]> newU = new HashMap <EntityType, double[][]>();
		for (final EntitySet entitySet: dataset.getEntitySets())
		{
			newU.put(entitySet.getType(), new double[rank][entitySet.size()]);
		}

		/*
		 * For each entity, compute the average of its neighbors, weighted by the link weight, and taking into account
		 * the relationship type ponderation.
		 */
		for (final EntitySet entitySet: dataset.getEntitySets())
		{
			final EntityType entityType = entitySet.getType();
			final int n = entitySet.size();
			final double ue[][] = newU.get(entityType);

			for (int i = 0; i < n; ++i)
			{
				final AverageRun averageRuns[] = new AverageRun[rank];
				for (int k = 0; k < rank; ++k)
					averageRuns[k] = average.run();
				for (final RelationshipSet relationshipSet: dataset.getRelationshipSets())
				{
					final double ponderation = relationshipTypePonderation.getWeightDefault(relationshipSet.getType());

					if (relationshipSet.getSubject().equals(entityType))
					{
						final double uObjects[][] = u.get(relationshipSet.getObject());
						for (final Entry entry: relationshipSet.getMatrix().row(i))
						{
							for (int k = 0; k < rank; ++k)
								averageRuns[k].add(ponderation * entry.value, uObjects[k][entry.index]);
						}
					}
					if (relationshipSet.getObject().equals(entityType))
					{
						final double uSubjects[][] = u.get(relationshipSet.getSubject());
						for (final Entry entry: relationshipSet.getMatrix().col(i))
						{
							for (int k = 0; k < rank; ++k)
								averageRuns[k].add(ponderation * entry.value, uSubjects[k][entry.index]);
						}
					}
				}

				for (int k = 0; k < rank; ++k)
				{
					final double average = averageRuns[k].getAverage();
					assert !Double.isNaN(average) && !Double.isInfinite(average);
					ue[k][i] = average;
				}
			}
		}

		v = u = newU;

		return orthogonalize();
	}

	@Override
	public void update()
	{
		double convergence;
		int count = 100;
		do
			convergence = iterate();
		while (convergence > EPSILON && count-- > 0);
	}

	private final Average average;

	/**
	 * The relationship type ponderation; may not be NULL.
	 */
	private final RelationshipTypePonderation relationshipTypePonderation;

	private final static double EPSILON = 1e-5;
}
