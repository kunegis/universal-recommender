package de.dailab.recommender.ensemble;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import de.dailab.recommender.average.Average;
import de.dailab.recommender.average.AverageRun;
import de.dailab.recommender.average.Mean;
import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.predict.AbstractPredictor;
import de.dailab.recommender.predict.Predictor;
import de.dailab.recommender.predict.PredictorModel;

/**
 * The average of several predictors.
 * 
 * @author kunegis
 */
public class EnsemblePredictor
    extends AbstractPredictor
{
	/**
	 * An ensemble predictor with given weights and averaging algorithm.
	 * 
	 * @param average The averaging algorithm to use
	 * @param predictors The predictors with their weights
	 */
	public EnsemblePredictor(Average average, Map <Predictor, Double> predictors)
	{
		this.average = average;
		this.predictors = predictors;
	}

	/**
	 * The average of multiple predictors using a given averaging algorithm.
	 * 
	 * @param average The averaging algorithm to use
	 * @param predictors The predictor to average
	 */
	public EnsemblePredictor(Average average, Predictor... predictors)
	{
		this.average = average;

		this.predictors = new HashMap <Predictor, Double>();
		for (final Predictor predictor: predictors)
		{
			this.predictors.put(predictor, 1.);
		}
	}

	/**
	 * The arithmetic mean of multiple predictors.
	 * 
	 * @param predictors The predictor to average
	 */
	public EnsemblePredictor(Predictor... predictors)
	{
		average = AVERAGE_DEFAULT;

		this.predictors = new HashMap <Predictor, Double>();
		for (final Predictor predictor: predictors)
		{
			this.predictors.put(predictor, 1.);
		}
	}

	@Override
	public PredictorModel build(final Dataset dataset, boolean update)
	{
		final Map <Predictor, PredictorModel> predictorModels = new HashMap <Predictor, PredictorModel>();

		for (final Predictor predictor: predictors.keySet())
		{
			predictorModels.put(predictor, predictor.build(dataset, update));
		}

		return new EnsemblePredictorModel(dataset, predictorModels);
	}

// @Override
// public PredictorModel buildInitial(final Dataset dataset)
// {
// final Map <Predictor, PredictorModel> predictorModels = new HashMap <Predictor, PredictorModel>();
//
// for (final Predictor predictor: predictors.keySet())
// {
// predictorModels.put(predictor, predictor.buildInitial(dataset));
// }
//
// return new EnsemblePredictorModel(dataset, predictorModels);
// }

	@Override
	public String toString()
	{
		String ret = "";
		for (final Entry <Predictor, Double> entry: predictors.entrySet())
		{
			if (!ret.isEmpty()) ret += ", ";
			ret += String.format(Locale.ROOT, "%s: %.3f", entry.getKey(), entry.getValue());
		}

		ret = String.format("Ensemble(%s; %s)", average, ret);
		return ret;
	}

	/**
	 * The averaging algorithm to use.
	 */
	private final Average average;

	/**
	 * Predictors with their weights.
	 */
	private final Map <Predictor, Double> predictors;

	private static final Average AVERAGE_DEFAULT = new Mean();

	private final class EnsemblePredictorModel
	    implements PredictorModel
	{
		private final Dataset dataset;
		private final Map <Predictor, PredictorModel> predictorModels;

		private EnsemblePredictorModel(Dataset dataset, Map <Predictor, PredictorModel> predictorModels)
		{
			this.dataset = dataset;
			this.predictorModels = predictorModels;
		}

		@Override
		public Dataset getDataset()
		{
			return dataset;
		}

		@Override
		public double predict(Entity source, Entity target)
		{
			final AverageRun averageRun = average.run();

			for (final Entry <Predictor, PredictorModel> entry: predictorModels.entrySet())
			{
				averageRun.add(predictors.get(entry.getKey()), entry.getValue().predict(source, target));
			}

			return averageRun.getAverage();
		}

		@Override
		public void update()
		{
			for (final PredictorModel predictorModel: predictorModels.values())
			{
				predictorModel.update();
			}
		}
	}

}
