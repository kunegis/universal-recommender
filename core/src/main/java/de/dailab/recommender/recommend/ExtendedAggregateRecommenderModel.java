package de.dailab.recommender.recommend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.iterators.TransformIterator;

import de.dailab.recommender.dataset.DatasetEntry;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.recommendation.LookaheadRecommendationIterator;
import de.dailab.recommender.recommendation.Recommendation;

/**
 * A recommender model that implements all recommendation functions in terms of one recommenExt(Entity, ...) function.
 * <p>
 * Duplicates are removed by looking ahead a given number of recommendations.
 * 
 * @author kunegis
 */
public abstract class ExtendedAggregateRecommenderModel
    implements RecommenderModel
{
	/**
	 * An aggregating recommender model with the given lookahead.
	 * 
	 * @param lookahead The number of recommendations to look ahead for merging duplicates.
	 */
	public ExtendedAggregateRecommenderModel(int lookahead)
	{
		this.lookahead = lookahead;
	}

	/**
	 * The aggregating recommender model with default lookahead value.
	 */
	public ExtendedAggregateRecommenderModel()
	{
		this(LOOKAHEAD_DEFAULT);
	}

	@SuppressWarnings("unchecked")
	@Override
	public RecommendationResult recommendExt(Map <Entity, Double> sources, EntityType[] targetEntityTypes)
	{
		final List <Iterator <Recommendation>> weightedIterators = new ArrayList <Iterator <Recommendation>>();
		final Set <RecommendationResult> recommendationResults = new HashSet <RecommendationResult>();

		for (final Entry <Entity, Double> e: sources.entrySet())
		{
			final RecommendationResult recommendationResult = recommendExt(e.getKey(), targetEntityTypes);

			recommendationResults.add(recommendationResult);

			final Iterator <Recommendation> weightedIterator = new TransformIterator(
			    recommendationResult.getIterator(), new Transformer()
			    {
				    @Override
				    public Object transform(Object arg0)
				    {
					    final Recommendation recommendation = (Recommendation) arg0;
					    final Recommendation ret = new Recommendation(recommendation.getEntity(), recommendation
					        .getScore()
					        * e.getValue());
					    return ret;
				    }
			    });
			weightedIterators.add(weightedIterator);
		}

		final Iterator <Recommendation> iterator = new LookaheadRecommendationIterator(lookahead,
		    new MergeIterator <Recommendation>(weightedIterators));

		return new RecommendationResult()
		{
			@Override
			public Iterator <Recommendation> getIterator()
			{
				return iterator;
			}

			@Override
			public Set <Entity> getVisitedEntities()
			    throws UnsupportedOperationException
			{
				/* UnsupportedOperationException is thrown through. */
				final Set <Entity> ret = new HashSet <Entity>();
				for (final RecommendationResult recommendationResult: recommendationResults)
				{
					final Set <Entity> visitedEntities = recommendationResult.getVisitedEntities();
					ret.addAll(visitedEntities);
				}
				return ret;
			}

			@Override
			public Map <Entity, Set <DatasetEntry>> getTrail()
			    throws UnsupportedOperationException
			{
				final Map <Entity, Set <DatasetEntry>> ret = new HashMap <Entity, Set <DatasetEntry>>();

				for (final RecommendationResult recommendationResult: recommendationResults)
				{
					for (final Entry <Entity, Set <DatasetEntry>> entry: recommendationResult.getTrail().entrySet())
					{
						Set <DatasetEntry> datasetEntrySet = ret.get(entry.getKey());
						if (datasetEntrySet == null)
						{
							datasetEntrySet = new HashSet <DatasetEntry>();
							ret.put(entry.getKey(), datasetEntrySet);
						}
						datasetEntrySet.addAll(entry.getValue());
					}
				}

				return ret;
			}
		};
	}

	@Override
	public Iterator <Recommendation> recommend(Entity source, EntityType targetEntityTypes[])
	{
		return recommendExt(source, targetEntityTypes).getIterator();
	}

	@Override
	public Iterator <Recommendation> recommend(Map <Entity, Double> sources, EntityType targetEntityTypes[])
	{
		return recommendExt(sources, targetEntityTypes).getIterator();
	}

	/**
	 * Implementations of ExtendedAggregateRecommenderModel must implement this function.
	 * 
	 * @param source The entity for which to recommend
	 * @param targetEntityTypes The requested entity types
	 * @return The recommendation result
	 */
	public abstract RecommendationResult recommendExt(Entity source, EntityType targetEntityTypes[]);

	private final int lookahead;

	private static final int LOOKAHEAD_DEFAULT = 20;
}
