package de.dailab.recommender.mask;

import java.util.HashMap;
import java.util.Map;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.EntitySet;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.RelationshipSet;
import de.dailab.recommender.latent.LatentPredictorModel;
import de.dailab.recommender.matrix.FullEntry;
import de.dailab.recommender.matrix.MatrixIterator;
import de.dailab.recommender.predict.RelationshipTypePonderation;

/**
 * The predictor model for the mask decomposition.
 * 
 * @author kunegis
 */
public class MaskDecompositionPredictorModel
    extends LatentPredictorModel
{
	// XXX implement the much better RC algorithm from [167].

	/**
	 * The mask decomposition predictor model on a given dataset with given rank and relationship type ponderation.
	 * 
	 * @param dataset The dataset to decomposition
	 * @param rank The rank of the decomposition
	 * @param relationshipTypePonderation The relationship type ponderation to use
	 * @param update Update the model immediately
	 */
	public MaskDecompositionPredictorModel(Dataset dataset, int rank,
	    RelationshipTypePonderation relationshipTypePonderation, boolean update)
	{
		super(dataset, rank);

		this.relationshipTypePonderation = relationshipTypePonderation;

		lambda = new double[rank];

		u = new HashMap <EntityType, double[][]>();
		for (final EntitySet entitySet: dataset.getEntitySets())
		{
			final double uEntity[][] = new double[rank][entitySet.size()];
			u.put(entitySet.getType(), uEntity);
		}

		v = u;

		if (update) update();
	}

	@Override
	public double iterate()
	{
		update();
		return 0;
	}

	@Override
	public void update()
	{
		final Dataset residual = Dataset.copyDataset(dataset);

		for (int k = 0; k < rank; ++k)
		{
			/*
			 * Initialize
			 */
			for (final double[][] ue: u.values())
			{
				final double uek[] = ue[k];
				for (int i = 0; i < uek.length; ++i)
					uek[i] = Math.random();
			}

			/*
			 * Iterate
			 */
			double convergence;
			do
			{
				/* u = dataset' u */

				final Map <EntityType, double[]> ukNew = new HashMap <EntityType, double[]>();

				for (final Map.Entry <EntityType, double[][]> entry: u.entrySet())
				{
					final EntityType entityType = entry.getKey();
					final double uEntity[][] = entry.getValue();
					assert uEntity.length == rank;
					assert uEntity[0].length > 0;
					ukNew.put(entityType, new double[uEntity[0].length]);
				}

				for (final RelationshipSet relationshipSet: residual.getRelationshipSets())
				{
					final double weight = relationshipTypePonderation.getWeightDefault(relationshipSet.getType());

					relationshipSet.getMatrix().multT(u.get(relationshipSet.getSubject())[k],
					    ukNew.get(relationshipSet.getObject()), weight);

					relationshipSet.getMatrix().mult(u.get(relationshipSet.getObject())[k],
					    ukNew.get(relationshipSet.getSubject()), weight);
				}

				for (final Map.Entry <EntityType, double[][]> entry: u.entrySet())
				{
					entry.getValue()[k] = ukNew.get(entry.getKey());
				}

				/* Normalize */

				double sumU = 0;
				double sumSquareU = 0;
				for (final double[][] ue: u.values())
					for (final double ueki: ue[k])
					{
						sumU += ueki;
						sumSquareU += ueki * ueki;
					}
				double normU = Math.sqrt(sumSquareU);
				if (normU == 0) normU = 1;
				double sign = Math.signum(sumU);
				if (sign == 0.) sign = 1;
				final double newLambda = normU * sign;
				for (final double[][] ue: u.values())
					for (int i = 0; i < ue[k].length; ++i)
						ue[k][i] /= newLambda;

				convergence = Math.abs(newLambda - lambda[k]);

				lambda[k] = newLambda;
			}
			while (convergence > EPSILON);

			/* residual -= u[k] * lambda[k] * v[k]' */
			for (final RelationshipSet relationshipSet: residual.getRelationshipSets())
			{
				final double uek[] = u.get(relationshipSet.getSubject())[k];
				final double vek[] = v.get(relationshipSet.getObject())[k];
				final double lambdaK = lambda[k];
				for (final MatrixIterator <FullEntry> i = relationshipSet.getMatrix().all().iterator(); i.hasNext();)
				{
					final FullEntry fullEntry = i.next();
					final double value = fullEntry.value;
					final double newValue = value - uek[fullEntry.rowIndex] * vek[fullEntry.colIndex] * lambdaK;
					i.set(newValue);
				}
			}
		}
	}

	private final RelationshipTypePonderation relationshipTypePonderation;

	private final static double EPSILON = 1e-5;
}
