package de.dailab.recommender.recommend;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.DatasetEntry;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.latent.DefaultUnnormalizedLatentPredictor;
import de.dailab.recommender.path.AllPath2;
import de.dailab.recommender.path.Path;
import de.dailab.recommender.predict.Predictor;
import de.dailab.recommender.predict.PredictorModel;
import de.dailab.recommender.recommendation.LookaheadRecommendationIterator;
import de.dailab.recommender.recommendation.Recommendation;

/**
 * A recommender that follows a path to determine which entities to return, but uses a (possibly unrelated) predictor to
 * compute scores. The scores returned by the path are discarded.
 * <p>
 * This implementation has a builtin lookahead.
 * <p>
 * The given path should not have a lookahead, since its scores are discarded, and a lookahead is wrapped around anyway.
 * 
 * @author kunegis
 */
public class PathPredictorRecommender
    extends AbstractRecommender
{
	/**
	 * The path predictor recommender that follows the given path and then computes predictions using the given
	 * predictor.
	 * 
	 * @param path The path to follow
	 * @param predictor The predictor used for computing scores
	 * @param lookahead The lookahead to use
	 */
	public PathPredictorRecommender(Path path, Predictor predictor, int lookahead)
	{
		this.path = path;
		this.predictor = predictor;
		this.lookahead = lookahead;
	}

	/**
	 * The path predictor recommender that follows all paths and then uses the given predictor.
	 * 
	 * @param predictor The predictor to use
	 */
	public PathPredictorRecommender(Predictor predictor)
	{
		this(PATH_DEFAULT, predictor, LOOKAHEAD_DEFAULT);
	}

	/**
	 * The path predictor recommender using the full path, the default unnormalized latent predictor, and the default
	 * lookahead.
	 */
	public PathPredictorRecommender()
	{
		this(PATH_DEFAULT, new DefaultUnnormalizedLatentPredictor(), LOOKAHEAD_DEFAULT);
	}

	private final Path path;
	private final Predictor predictor;
	private final int lookahead;

	@Override
	public RecommenderModel build(final Dataset dataset, boolean update)
	{
		final PredictorModel predictorModel = predictor.build(dataset, update);

		return new AggregateRecommenderModel()
		{
			@Override
			public Iterator <Recommendation> recommend(final Entity source, EntityType[] targetEntityTypes)
			{
				final Map <Entity, Set <DatasetEntry>> trail = new HashMap <Entity, Set <DatasetEntry>>();

				final Iterator <Recommendation> iterator = path.recommend(dataset, source, trail);

				return new LookaheadRecommendationIterator(lookahead, new Iterator <Recommendation>()
				{
					@Override
					public boolean hasNext()
					{
						return iterator.hasNext();
					}

					@Override
					public Recommendation next()
					{
						final Recommendation recommendation = iterator.next();

						return new Recommendation(recommendation.getEntity(), predictorModel.predict(source,
						    recommendation.getEntity()));
					}

					@Override
					public void remove()
					{
						throw new UnsupportedOperationException();
					}
				});
			}

			@Override
			public void update()
			{
				predictorModel.update();
			}

			@Override
			public PredictorModel getPredictorModel()
			    throws UnsupportedOperationException
			{
				return predictorModel;
			}
		};
	}

	@Override
	public String toString()
	{
		return String.format("PathPrediction(%d, %s)-%s", lookahead, path, predictor);
	}

	private static final Path PATH_DEFAULT = new AllPath2();

	private static final int LOOKAHEAD_DEFAULT = 30;
}
