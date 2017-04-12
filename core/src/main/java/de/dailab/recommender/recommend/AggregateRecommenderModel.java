package de.dailab.recommender.recommend;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.iterators.TransformIterator;

import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.recommendation.LookaheadRecommendationIterator;
import de.dailab.recommender.recommendation.Recommendation;

/**
 * A recommender model that implements recommend(Map) and recommendExt() in terms of recommend(Entity).
 * <p>
 * Duplicates are removed by looking ahead a given number of recommendations.
 * 
 * @author kunegis
 */
public abstract class AggregateRecommenderModel
    extends SimpleRecommenderModel
{
	/**
	 * An aggregating recommender model with the given lookahead.
	 * 
	 * @param lookahead The number of recommendations to look ahead for merging duplicates.
	 */
	public AggregateRecommenderModel(int lookahead)
	{
		this.lookahead = lookahead;
	}

	/**
	 * The aggregating recommender model with default lookahead value.
	 */
	public AggregateRecommenderModel()
	{
		this(LOOKAHEAD_DEFAULT);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator <Recommendation> recommend(Map <Entity, Double> sources, EntityType[] targetEntityTypes)
	{
		final List <Iterator <Recommendation>> iterators = new ArrayList <Iterator <Recommendation>>();
		for (final Entry <Entity, Double> e: sources.entrySet())
		{
			iterators.add(new TransformIterator(recommend(e.getKey(), targetEntityTypes), new Transformer()
			{
				@Override
				public Object transform(Object arg0)
				{
					final Recommendation recommendation = (Recommendation) arg0;
					final Recommendation ret = new Recommendation(recommendation.getEntity(), recommendation.getScore()
					    * e.getValue());
					return ret;
				}
			}));
		}

		final Iterator <Recommendation> iterator = new LookaheadRecommendationIterator(lookahead,
		    new MergeIterator <Recommendation>(iterators));

		return iterator;
	}

	private final int lookahead;

	private static final int LOOKAHEAD_DEFAULT = 20;
}
