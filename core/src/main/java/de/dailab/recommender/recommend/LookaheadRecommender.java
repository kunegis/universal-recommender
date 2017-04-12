package de.dailab.recommender.recommend;

import java.util.Iterator;
import java.util.Map;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.predict.PredictorModel;
import de.dailab.recommender.recommendation.LookaheadRecommendationIterator;
import de.dailab.recommender.recommendation.Recommendation;

/**
 * A recommender that uses an underlying recommender that gives nonmonotonically sorted recommendations and sorts them
 * by score, prefetching a given number of recommendations.
 * <p>
 * Some recommenders return recommendations whose scores are not decreasing. This recommender can be wrapped around such
 * recommenders to give nonincreasing recommendation scores, at least up to the number of prefetched recommendations.
 * <p>
 * Lookahead recommenders or iterators must not be nested.
 * 
 * @author kunegis
 */
public class LookaheadRecommender
    extends AbstractRecommender
{
	/**
	 * Take a given recommender and look ahead a given number of recommendations, sorting the recommendations by scores.
	 * 
	 * @param lookahead The number of recommendations to look ahead
	 * @param recommender The underlying recommender, which may returned unsorted recommendations
	 */
	public LookaheadRecommender(int lookahead, Recommender recommender)
	{
		if (recommender instanceof LookaheadRecommender) { throw new Error("Invalid nesting of lookahead recommenders"); }

		this.lookahead = lookahead;
		this.recommender = recommender;
	}

	private final int lookahead;
	private final Recommender recommender;

	@Override
	public String toString()
	{
		return String.format("Lookahead(%d)-%s", lookahead, recommender);
	}

	@Override
	public RecommenderModel build(Dataset dataset, boolean update)
	{
		final RecommenderModel recommenderModel = recommender.build(dataset, update);

		return new SimpleRecommenderModel()
		{
			@Override
			public Iterator <Recommendation> recommend(Entity source, EntityType[] targetEntityTypes)
			{
				return new LookaheadRecommendationIterator(lookahead, recommenderModel.recommend(source,
				    targetEntityTypes));
			}

			@Override
			public Iterator <Recommendation> recommend(Map <Entity, Double> sources, EntityType[] targetEntityTypes)
			{
				return new LookaheadRecommendationIterator(lookahead, recommenderModel.recommend(sources,
				    targetEntityTypes));
			}

			@Override
			public void update()
			{
				recommenderModel.update();
			}

			@Override
			public PredictorModel getPredictorModel()
			    throws UnsupportedOperationException
			{
				return recommenderModel.getPredictorModel();
			}
		};
	}
}
