package de.dailab.recommender.predict;

import de.dailab.recommender.average.Average;
import de.dailab.recommender.average.AverageRun;
import de.dailab.recommender.average.MeanSigned;
import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.RelationshipSet;
import de.dailab.recommender.matrix.Entry;
import de.dailab.recommender.matrix.Matrix;

/**
 * Predict ratings by taking the weighted mean of known ratings.
 * <p>
 * The neighboring entities of the source entity are taken, and their predictions are averaged with a given averaging
 * algorithm.
 * 
 * @author kunegis
 */
public class WeightedMeanPredictor
    extends AbstractPredictor
{
	// XXX this may be redundant to PathPredictor/Recommender.

	/**
	 * A predictor that computes the weighted mean of predictions between entities of the same type.
	 * 
	 * @param average The averaging measure to use.
	 * @param predictor The underlying predictor. Used for object-object relations.
	 */
	public WeightedMeanPredictor(Average average, Predictor predictor)
	{
		this.average = average;
		this.predictor = predictor;
	}

	/**
	 * A weighted mean predictor with the default averaging algorithm.
	 * 
	 * @param predictor The underlying predictor to use.
	 */
	public WeightedMeanPredictor(Predictor predictor)
	{
		this(AVERAGE_DEFAULT, predictor);
	}

	private final Average average;
	private final Predictor predictor;

	@Override
	public PredictorModel build(final Dataset dataset, boolean update)
	{
		final PredictorModel predictorModel = predictor.build(dataset, update);

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
				RelationshipSet relationshipSet = null;
				for (final RelationshipSet aRelationshipSet: dataset.getRelationshipSets())
				{
					if (aRelationshipSet.getSubject().equals(source.getType())
					    && aRelationshipSet.getObject().equals(target.getType()))
					{
						relationshipSet = aRelationshipSet;
						break;
					}
				}
				if (relationshipSet == null) { throw new IllegalArgumentException(String.format(
				    "No relationship set connecting %s and %s", source.getType(), target.getType())); }

				assert relationshipSet.getSubject().equals(source.getType());
				final Matrix matrix = relationshipSet.getMatrix();

				final AverageRun averageRun = average.run();

				for (final Entry e: matrix.row(source.getId()))
				{
					final Entity middle = new Entity(relationshipSet.getObject(), e.index);
					final double weight = e.value;
					final double prediction = predictorModel.predict(middle, target);
					averageRun.add(weight, prediction);
				}

				return averageRun.getAverage();
			}

			@Override
			public void update()
			{
				predictorModel.update();
			}
		};
	}

	private final static Average AVERAGE_DEFAULT = new MeanSigned();

	@Override
	public String toString()
	{
		return String.format("WeightedMean(%s)-%s", average, predictor);
	}
}
