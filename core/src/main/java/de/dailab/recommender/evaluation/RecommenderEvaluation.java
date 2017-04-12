package de.dailab.recommender.evaluation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import de.dailab.recommender.constraint.PresentConstraintRecommenderModel;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.RelationshipType;
import de.dailab.recommender.dataset.UnirelationalDataset;
import de.dailab.recommender.ensemble.EnsembleRecommender;
import de.dailab.recommender.evaluation.recommenderperformance.Precision;
import de.dailab.recommender.evaluation.recommenderperformance.RecommenderPerformance;
import de.dailab.recommender.evaluation.recommenderperformance.RecommenderPerformanceRun;
import de.dailab.recommender.latent.LatentRecommender;
import de.dailab.recommender.path.PathRecommender;
import de.dailab.recommender.recommend.PathPredictorRecommender;
import de.dailab.recommender.recommend.Recommender;
import de.dailab.recommender.recommend.RecommenderList;
import de.dailab.recommender.recommend.RecommenderModel;
import de.dailab.recommender.recommend.SequentialRecommenderList;
import de.dailab.recommender.recommendation.Recommendation;

/**
 * Evaluate recommenders using a given split.
 * <p>
 * The evaluation is performed in the constructors.
 * 
 * @author kunegis
 */
public class RecommenderEvaluation
{
	/**
	 * Evaluate a set of recommenders using the given split dataset.
	 * 
	 * @param split The split to use
	 * @param recommenderList The recommenders to evaluate
	 * @param recommenderPerformances The performance measures to compute
	 * @param logger Log here
	 */
	public RecommenderEvaluation(Split split, RecommenderList recommenderList,
	    List <RecommenderPerformance> recommenderPerformances, Logger logger)
	{
		final EntityType targetTypes[] = new EntityType[]
		{ split.test.getObject() };

		final Iterator <Recommender> recommenderIterator = recommenderList.getRecommenders().iterator();

		logger.info(String.format("Split:  %s", split));

		for (final Iterator <RecommenderModel> i = recommenderList.build(split.training); i.hasNext();)
		{
			final Recommender recommender = recommenderIterator.next();

			logger.info(String.format("Building %s", recommender));

			final long begin = System.currentTimeMillis();
			RecommenderModel recommenderModel = i.next();
			final long end = System.currentTimeMillis();
			logger.info(String.format("   %d ms", end - begin));

			logger.info(String.format("Evaluating %s", recommender));

			final Map <RecommenderPerformance, Double> values = new HashMap <RecommenderPerformance, Double>();
			recommenderPerformanceValues.put(recommender, values);

			final Map <RecommenderPerformance, RecommenderPerformanceRun> runs = new HashMap <RecommenderPerformance, RecommenderPerformanceRun>();

			for (final RecommenderPerformance recommenderPerformance: recommenderPerformances)
			{
				runs.put(recommenderPerformance, recommenderPerformance.run());
			}

			recommenderModel = new PresentConstraintRecommenderModel(split.training, recommenderModel,
			    new RelationshipType[]
			    { split.test.getType() });

			final long begin2 = System.currentTimeMillis();

			int rowCount = 0;

			for (final int j: split.test.getMatrix().getRows())
			{
				++rowCount;

				final Iterator <de.dailab.recommender.matrix.Entry> testRow = split.test.getMatrix().row(j).iterator();
				final Set <Entity> testSet = new HashSet <Entity>();
				while (testRow.hasNext())
					testSet.add(new Entity(split.test.getObject(), testRow.next().index));

				final Iterator <Recommendation> recommendations = recommenderModel.recommend(new Entity(split.test
				    .getSubject(), j), targetTypes);

				for (final RecommenderPerformanceRun run: runs.values())
					run.add(testSet, recommendations);
			}

			final long end2 = System.currentTimeMillis();

			logger.info(String.format("   %d ms", end2 - begin2));

			assert rowCount > 0; /* the split was empty */

			for (final Map.Entry <RecommenderPerformance, RecommenderPerformanceRun> entry: runs.entrySet())
			{
				values.put(entry.getKey(), entry.getValue().get());
			}
		}
	}

	/**
	 * Evaluation using a default list of recommenders using a default list of performance measures.
	 * 
	 * @param split The split to evaluate against
	 */
	public RecommenderEvaluation(Split split)
	{
		this(split, RECOMMENDERS_DEFAULT, RECOMMENDER_PERFORMANCES_DEFAULT, LOGGER_DEFAULT);
	}

	/**
	 * Evaluate the default recommenders on the given split; log to the given logger.
	 * 
	 * @param split The split in which to evaluate
	 * @param logger Log here
	 */
	public RecommenderEvaluation(Split split, Logger logger)
	{
		this(split, RECOMMENDERS_DEFAULT, RECOMMENDER_PERFORMANCES_DEFAULT, logger);
	}

	/**
	 * Evaluation using the default list of recommender performance measures.
	 * 
	 * @param split The split dataset
	 * @param recommenderList The recommenders to evaluate
	 */
	public RecommenderEvaluation(Split split, RecommenderList recommenderList)
	{
		this(split, recommenderList, RECOMMENDER_PERFORMANCES_DEFAULT, LOGGER_DEFAULT);
	}

	/**
	 * Evaluate the recommenders on the given split.
	 * 
	 * @param split The split on which to perform evaluation
	 * @param recommenders The recommenders to evaluate
	 */
	public RecommenderEvaluation(Split split, List <Recommender> recommenders)
	{
		this(split, new SequentialRecommenderList(recommenders));
	}

	/**
	 * Evaluate the given recommenders on recommender the unique relationship type in the given dataset.
	 * 
	 * @param unirelationalDataset The unirelational dataset
	 * @param recommenders The recommenders to evaluate
	 */
	public RecommenderEvaluation(UnirelationalDataset unirelationalDataset, Recommender... recommenders)
	{
		this(new Split(unirelationalDataset, new RecommenderSplitType()), new SequentialRecommenderList(recommenders));
	}

	/**
	 * The computed performances. Written to in the constructor.
	 */
	public final Map <Recommender, Map <RecommenderPerformance, Double>> recommenderPerformanceValues = new HashMap <Recommender, Map <RecommenderPerformance, Double>>();

	/**
	 * The performances of the recommenders in multiline representation.
	 * 
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		String ret = "";

		for (final RecommenderPerformance recommenderPerformance: recommenderPerformanceValues.entrySet().iterator()
		    .next().getValue().keySet())
		{
			final SortedSet <Recommender> sortedRecommenders = new TreeSet <Recommender>(new Comparator <Recommender>()
			{
				@Override
				public int compare(Recommender o1, Recommender o2)
				{
					final double s1 = recommenderPerformanceValues.get(o1).get(recommenderPerformance);
					final double s2 = recommenderPerformanceValues.get(o2).get(recommenderPerformance);
					return Double.compare(s2, s1);
				}
			});

			for (final Entry <Recommender, Map <RecommenderPerformance, Double>> i: recommenderPerformanceValues
			    .entrySet())
			{
				final Recommender recommender = i.getKey();
				sortedRecommenders.add(recommender);
			}

			ret += recommenderPerformance + "\n";

			for (final Recommender recommender: sortedRecommenders)
				ret += String.format("  %.4f %s\n", recommenderPerformanceValues.get(recommender).get(
				    recommenderPerformance), recommender);
		}

		return ret;
	}

	/**
	 * The default list of recommenders to evaluate.
	 */
	public final static RecommenderList RECOMMENDERS_DEFAULT = new SequentialRecommenderList(

	new LatentRecommender(),

	new PathRecommender(),

	new PathPredictorRecommender(),

	new EnsembleRecommender(new LatentRecommender(), new PathRecommender(), new PathPredictorRecommender()));

	/**
	 * The default list of recommender performance measures.
	 */
	public final static List <RecommenderPerformance> RECOMMENDER_PERFORMANCES_DEFAULT = new ArrayList <RecommenderPerformance>();
	static
	{
		RECOMMENDER_PERFORMANCES_DEFAULT.add(new Precision(true, 4));
	}

	private final static Logger LOGGER_DEFAULT = Logger.getLogger(RecommenderEvaluation.class);
}
