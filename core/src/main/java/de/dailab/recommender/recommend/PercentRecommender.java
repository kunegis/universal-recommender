package de.dailab.recommender.recommend;

import java.util.Iterator;
import java.util.Map;

import de.dailab.recommender.constraint.Constraint;
import de.dailab.recommender.constraint.ConstraintRecommendationIterator;
import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.predict.PredictorModel;
import de.dailab.recommender.recommendation.Recommendation;

/**
 * Wrap another recommender and scale the scores to a value where 1 represents a perfect score. A threshold can be set
 * under which recommendations are discarded.
 * 
 * @author kunegis
 */
public class PercentRecommender
    extends AbstractRecommender
{
	/**
	 * A percent recommender based on a given recommender.
	 * 
	 * @param minimumScore The minimum score of recommendations to keep
	 * @param recommender The recommender whose scores are scaled
	 */
	public PercentRecommender(double minimumScore, Recommender recommender)
	{
		this.minimumScore = minimumScore;
		this.recommender = recommender;
	}

	/**
	 * A percent recommender with the default value of the minimum score.
	 * 
	 * @param recommender The recommender whose scores are modified
	 */
	public PercentRecommender(Recommender recommender)
	{
		this(MINIMUM_SCORE_DEFAULT, recommender);
	}

	@Override
	public RecommenderModel build(Dataset dataset, boolean update)
	{
		final RecommenderModel recommenderModel = recommender.build(dataset, update);

		return new SimpleRecommenderModel()
		{
			@Override
			public void update()
			{
				recommenderModel.update();
			}

			@Override
			public Iterator <Recommendation> recommend(Map <Entity, Double> sources, EntityType[] targetEntityTypes)
			{
				return wrap(recommenderModel.recommend(sources, targetEntityTypes));
			}

			@Override
			public Iterator <Recommendation> recommend(Entity source, EntityType[] targetEntityTypes)
			{
				return wrap(recommenderModel.recommend(source, targetEntityTypes));
			}

			private Iterator <Recommendation> wrap(final Iterator <Recommendation> iterator)
			{
				return new ConstraintRecommendationIterator(new Constraint()
				{
					@Override
					public boolean accept(Recommendation recommendation)
					{
						return recommendation.getScore() >= minimumScore;
					}
				}, new Iterator <Recommendation>()
				{
					@Override
					public boolean hasNext()
					{
						return iterator.hasNext();
					}

					@Override
					public Recommendation next()
					{
						final Recommendation ret = iterator.next();
						if (reference < 0) reference = 1.01 * ret.getScore();
						return new Recommendation(ret.getEntity(), ret.getScore() / reference);
					}

					@Override
					public void remove()
					{
						iterator.remove();
					}
				});
			}

			/**
			 * The score that is mapped to 1. &lt;1 if not yet initialized.
			 */
			double reference = -1;

			@Override
			public PredictorModel getPredictorModel()
			    throws UnsupportedOperationException
			{
				// TODO implement the transformation
				throw new UnsupportedOperationException();
			}
		};

	}

	private final double minimumScore;

	private final Recommender recommender;

	private static final double MINIMUM_SCORE_DEFAULT = .1;
}
