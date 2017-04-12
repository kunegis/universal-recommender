package de.dailab.recommender.evaluation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.UnirelationalDataset;
import de.dailab.recommender.dataset.WeightRange;
import de.dailab.recommender.ensemble.EnsemblePredictor;
import de.dailab.recommender.evaluation.predictionerror.CorrelationError;
import de.dailab.recommender.evaluation.predictionerror.Mae;
import de.dailab.recommender.evaluation.predictionerror.PredictionError;
import de.dailab.recommender.evaluation.predictionerror.PredictionErrorRun;
import de.dailab.recommender.evaluation.predictionerror.Rmse;
import de.dailab.recommender.laplacian.LaplacianPredictor;
import de.dailab.recommender.latent.EigenvalueDecompositionPredictor;
import de.dailab.recommender.latent.LatentPredictorList;
import de.dailab.recommender.matrix.FullEntry;
import de.dailab.recommender.predict.CompoundPredictorList;
import de.dailab.recommender.predict.NormalizedPredictor;
import de.dailab.recommender.predict.Predictor;
import de.dailab.recommender.predict.PredictorList;
import de.dailab.recommender.predict.PredictorModel;
import de.dailab.recommender.predict.SequentialPredictorList;
import de.dailab.recommender.predict.SimilarityPredictor;
import de.dailab.recommender.predict.WeightedMeanPredictor;
import de.dailab.recommender.similarity.CosineSimilarity;
import de.dailab.recommender.similarity.ExponentialKernel;
import de.dailab.recommender.similarity.Jaccard;
import de.dailab.recommender.similarity.SignCorrelation;
import de.dailab.recommender.similarity.TransformedSimilarity;
import de.dailab.recommender.similarity.VonNeumannKernel;

/**
 * Evaluate a predictor on a split.
 * <p>
 * The principal constructor takes a split (which should have been built with {@link PredictorSplitType}), a list of
 * predictors to evaluate and error measures to compute.
 * <p>
 * Instead of a split, a unirelational dataset can be passed. The prediction will then be made for its single
 * relationship type.
 * 
 * @author kunegis
 */
public class PredictorEvaluation
{
	/**
	 * Evaluate the performance of a predictor on a dataset split.
	 * 
	 * @param split The split to use
	 * @param predictorList The predictors to evaluate
	 * @param errors The errors to compute
	 * @param logger Log here
	 */
	public PredictorEvaluation(Split split, PredictorList predictorList, List <PredictionError> errors, Logger logger)
	{
		final Iterator <Predictor> predictorIterator = predictorList.getPredictors().iterator();

		for (final Iterator <PredictorModel> i = predictorList.build(split.training); i.hasNext();)
		{
			final Predictor predictor = predictorIterator.next();

			logger.info(String.format("Building %s", predictor));

			final PredictorModel predictorModel = i.next();

			logger.info(String.format("Evaluating %s", predictor));

			final Map <PredictionError, PredictionErrorRun> errorRuns = new HashMap <PredictionError, PredictionErrorRun>();
			for (final PredictionError error: errors)
				errorRuns.put(error, error.run());

			for (final FullEntry entry: split.test.getMatrix().all())
			{
				final double prediction = predictorModel.predict(new Entity(split.test.getSubject(), entry.rowIndex),
				    new Entity(split.test.getObject(), entry.colIndex));

				for (final PredictionErrorRun errorRun: errorRuns.values())
					errorRun.add(entry.value, prediction);
			}

			final Map <PredictionError, Double> predictorErrorValues = new HashMap <PredictionError, Double>();
			errorValues.put(predictor, predictorErrorValues);
			for (final Entry <PredictionError, PredictionErrorRun> e: errorRuns.entrySet())
				predictorErrorValues.put(e.getKey(), e.getValue().get());
		}
	}

	/**
	 * Evaluate the performance of a predictor on a dataset split.
	 * 
	 * @param split The split to use
	 * @param predictorList The predictors to evaluate
	 * @param errors The errors to compute
	 */
	public PredictorEvaluation(Split split, PredictorList predictorList, List <PredictionError> errors)
	{
		this(split, predictorList, errors, LOGGER_DEFAULT);
	}

	/**
	 * Evaluate predictors on a given dataset using the default error measures.
	 * 
	 * @param split The split dataset
	 * @param predictorList The predictors to evaluate
	 */
	public PredictorEvaluation(Split split, PredictorList predictorList)
	{
		this(split, predictorList, PREDICTOR_ERRORS_DEFAULT);
	}

	/**
	 * Evaluate the given list of predictors on the given split.
	 * 
	 * @param split The split to use for evaluation
	 * @param predictors The predictors to evaluate
	 */
	public PredictorEvaluation(Split split, List <Predictor> predictors)
	{
		this(split, new SequentialPredictorList(predictors));
	}

	/**
	 * Evaluate the default predictors on the given split using the default predictor error measures.
	 * 
	 * @param split The split to evaluate.
	 */
	public PredictorEvaluation(Split split)
	{
		this(split, getDefaultPredictors(split), PREDICTOR_ERRORS_DEFAULT);
	}

	/**
	 * Evaluate the default predictors on the given split; log into the given logger.
	 * 
	 * @param split The split for which to evaluate the predictors
	 * @param logger Log here
	 */
	public PredictorEvaluation(Split split, Logger logger)
	{
		this(split, getDefaultPredictors(split), PREDICTOR_ERRORS_DEFAULT, logger);
	}

	/**
	 * Evaluate prediction in a unirelational dataset.
	 * 
	 * @param unirelationalDataset The unirelational dataset
	 * @param predictorList The predictors to evaluate
	 */
	public PredictorEvaluation(UnirelationalDataset unirelationalDataset, PredictorList predictorList)
	{
		this(new Split(unirelationalDataset, new PredictorSplitType()), predictorList);
	}

	/**
	 * Evaluation the given predictors on prediction in the given unirelational dataset.
	 * 
	 * @param unirelationalDataset The unirelational dataset to use
	 * @param predictors The predictors to evaluate
	 */
	public PredictorEvaluation(UnirelationalDataset unirelationalDataset, List <Predictor> predictors)
	{
		this(unirelationalDataset, new SequentialPredictorList(predictors));
	}

	/**
	 * Evaluate the given predictors on the task of prediction relationship in the given unirelational dataset.
	 * 
	 * @param unirelationalDataset The dataset in which to predict links
	 * @param predictors The predictors to evaluate
	 */
	public PredictorEvaluation(UnirelationalDataset unirelationalDataset, Predictor... predictors)
	{
		this(unirelationalDataset, new SequentialPredictorList(predictors));
	}

	/**
	 * The errors by predictor in multiline representation.
	 */
	@Override
	public String toString()
	{
		String ret = "";

		for (final PredictionError predictionError: errorValues.entrySet().iterator().next().getValue().keySet())
		{
			final SortedSet <Predictor> sortedPredictors = new TreeSet <Predictor>(new Comparator <Predictor>()
			{
				@Override
				public int compare(Predictor o1, Predictor o2)
				{
					final double s1 = errorValues.get(o1).get(predictionError);
					final double s2 = errorValues.get(o2).get(predictionError);
					return Double.compare(s1, s2);
				}
			});

			for (final Entry <Predictor, Map <PredictionError, Double>> i: errorValues.entrySet())
			{
				final Predictor predictor = i.getKey();
				sortedPredictors.add(predictor);
			}

			ret += predictionError + "\n";

			for (final Predictor predictor: sortedPredictors)
				ret += String.format("  %.4f %s\n", errorValues.get(predictor).get(predictionError), predictor);

			ret += "\n";
		}

		return ret;
	}

	/**
	 * The computed error values by their error.
	 */
	public final Map <Predictor, Map <PredictionError, Double>> errorValues = new HashMap <Predictor, Map <PredictionError, Double>>();

	private final static List <PredictionError> PREDICTOR_ERRORS_DEFAULT = new ArrayList <PredictionError>();
	static
	{
		PREDICTOR_ERRORS_DEFAULT.add(new Rmse());
		PREDICTOR_ERRORS_DEFAULT.add(new Mae());
		PREDICTOR_ERRORS_DEFAULT.add(new CorrelationError());
	}

	private final static Logger LOGGER_DEFAULT = Logger.getLogger(PredictorEvaluation.class.getSimpleName());

	/**
	 * The default list of predictors to evaluate for all dataset types.
	 */
	private final static SequentialPredictorList PREDICTORS_DEFAULT = new SequentialPredictorList(

	new NormalizedPredictor(),

	new NormalizedPredictor(new EigenvalueDecompositionPredictor()),

	new NormalizedPredictor(new EigenvalueDecompositionPredictor(new TransformedSimilarity(new ExponentialKernel()))),

	new NormalizedPredictor(new LaplacianPredictor()),

	new NormalizedPredictor(new WeightedMeanPredictor(new EigenvalueDecompositionPredictor())),

	new EnsemblePredictor(new NormalizedPredictor(), new NormalizedPredictor(new EigenvalueDecompositionPredictor())));

	/**
	 * Unnormalized latent predictors.
	 */
	private final static PredictorList PREDICTORS_DEFAULT_EIGENVALUE_DECOMPOSITION = new LatentPredictorList(
	    new EigenvalueDecompositionPredictor(), null, new ExponentialKernel(), new VonNeumannKernel());

	/**
	 * The default predictors only applied to square networks.
	 */
	private final static SequentialPredictorList PREDICTORS_SQUARE_DEFAULT = new SequentialPredictorList(

	new SimilarityPredictor(new Jaccard()),

	new SimilarityPredictor(new CosineSimilarity())

	);

	/**
	 * The default predictors only applied to square signed networks.
	 */
	private final static SequentialPredictorList PREDICTORS_SQUARE_SIGNED_DEFAULT = new SequentialPredictorList(

	new SimilarityPredictor(new SignCorrelation())

	);

	private static PredictorList getDefaultPredictors(Split split)
	{
		if (split.test.getRelationshipFormat().isSquare())
		{
			if (split.test.getWeightRange() == WeightRange.SIGNED)
				return new CompoundPredictorList(PREDICTORS_DEFAULT, PREDICTORS_DEFAULT_EIGENVALUE_DECOMPOSITION,
				    PREDICTORS_SQUARE_DEFAULT, PREDICTORS_SQUARE_SIGNED_DEFAULT);
			else
				return new CompoundPredictorList(PREDICTORS_DEFAULT, PREDICTORS_SQUARE_DEFAULT,
				    PREDICTORS_DEFAULT_EIGENVALUE_DECOMPOSITION);

		}
		else
			return new CompoundPredictorList(PREDICTORS_DEFAULT, PREDICTORS_DEFAULT_EIGENVALUE_DECOMPOSITION);
	}
}
