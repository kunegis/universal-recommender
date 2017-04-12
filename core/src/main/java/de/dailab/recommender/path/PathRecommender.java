package de.dailab.recommender.path;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;

import de.dailab.recommender.constraint.ConstraintRecommendationIterator;
import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.DatasetEntry;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.predict.PredictorModel;
import de.dailab.recommender.predict.RelationshipTypePonderation;
import de.dailab.recommender.predict.WeightedMeanPredictor;
import de.dailab.recommender.recommend.AbstractRecommender;
import de.dailab.recommender.recommend.EntityTypeConstraint;
import de.dailab.recommender.recommend.PositiveIterator;
import de.dailab.recommender.recommend.RecommendationResult;
import de.dailab.recommender.recommend.Recommender;
import de.dailab.recommender.recommend.RecommenderModel;
import de.dailab.recommender.recommendation.LookaheadRecommendationIterator;
import de.dailab.recommender.recommendation.Recommendation;

/**
 * A recommender using the following algorithm: Follow a given path and then use a given recommender.
 * <p>
 * The typical use is to reduce a recommender between two different entity types to a recommender between the same
 * entity type.
 * <p>
 * The path to follow is represented by a Path object. It is acceptable for the path to return duplicates&mdash;these
 * are aggregated using the given lookahead value.
 * 
 * @see Path
 * @see WeightedMeanPredictor
 * 
 * @author kunegis
 */
public class PathRecommender
    extends AbstractRecommender
{
	/**
	 * A recommender that follows a chain and then optionally uses another recommender.
	 * <p>
	 * When the given path is NULL, the full path is used. Note that the full path has a max visit value which is
	 * independent of the lookahead parameter of this class. Both parameters determine how parallel paths are handled.
	 * 
	 * @param path A path; NULL denotes a FullPath, i.e. a path that follows all edges
	 * @param followingRecommender The following recommender to use; NULL for "pure" path recommenders
	 * @param lookahead The number of recommendations to look ahead
	 */
	public PathRecommender(Path path, Recommender followingRecommender, int lookahead)
	{
		this.path = path == null ? new AllPath2() : path;
		this.followingRecommender = followingRecommender;
		this.lookahead = lookahead;
	}

	/**
	 * A recommender following a given path and using a given underlying recommender, using the default lookahead and
	 * maximum number of visits.
	 * 
	 * @param path The path to follow for recommendations
	 * @param followingRecommender The underlying recommender; may be NULL to denote a pure path recommender
	 */
	public PathRecommender(Path path, Recommender followingRecommender)
	{
		this(path, followingRecommender, LOOKAHEAD_DEFAULT);
	}

	/**
	 * Recommendations following a given path and a given lookahead, without a following recommender.
	 * 
	 * @param path The path to follow
	 * @param lookahead The number of entities to look ahead
	 */
	public PathRecommender(Path path, int lookahead)
	{
		this(path, null, lookahead);
	}

	/**
	 * Create a path recommender without a following recommender.
	 * 
	 * @param path A path to follow
	 */
	public PathRecommender(Path path)
	{
		this(path, null, LOOKAHEAD_DEFAULT);
	}

	/**
	 * The pure full path recommender with the given lookahead.
	 * 
	 * @param lookahead The number of entities to look ahead
	 */
	public PathRecommender(int lookahead)
	{
		this(null, null, lookahead);
	}

	/**
	 * A path recommender using all-path with the given max-visit value, and using the given lookahead value.
	 * 
	 * @param maxVisit The maximum number of times to visit an entity
	 * @param lookahead Number of entities to look ahead
	 * 
	 * @deprecated The max-visit parameter is deprecated and is ignored by this constructor
	 */
	@Deprecated
	public PathRecommender(int maxVisit, int lookahead)
	{
		this(new AllPath2(), null, lookahead);
	}

	/**
	 * A full path recommender without a following recommender and with default lookahead.
	 */
	public PathRecommender()
	{
		this(null, null, LOOKAHEAD_DEFAULT);
	}

	/**
	 * A path recommender that follows all paths with the relationship type ponderation.
	 * 
	 * @param relationshipTypePonderation The weighting of relationship types
	 */
	public PathRecommender(RelationshipTypePonderation relationshipTypePonderation)
	{
		this(new AllPath2(relationshipTypePonderation));
	}

	/**
	 * The chain to follow
	 */
	private final Path path;

	/**
	 * The number of recommendations to look ahead.
	 */
	private final int lookahead;

	/**
	 * The following recommender at the end of the chain. May be NULL to denote a "pure" path recommender.
	 */
	private final Recommender followingRecommender;

	/**
	 * The default value for the lookahead parameter, as used in the constructors that don't take a lookahead parameter.
	 */
	public final static int LOOKAHEAD_DEFAULT = 10;

	@Override
	public RecommenderModel build(final Dataset dataset, boolean update)
	{
		/**
		 * The following recommender model; may be NULL.
		 */
		final RecommenderModel followingRecommenderModel = followingRecommender == null ? null : followingRecommender
		    .build(dataset, update);

		return new RecommenderModel()
		{
			@Override
			public RecommendationResult recommendExt(Map <Entity, Double> sources, final EntityType[] targetEntityTypes)
			{

				final Map <Entity, Set <DatasetEntry>> trail = new HashMap <Entity, Set <DatasetEntry>>();

				final MultiPath multiPath = (path instanceof MultiPath) ? (MultiPath) path
				    : new AbstractMultiPath(path);

				final Iterator <Recommendation> iterator = new PositiveIterator(multiPath.recommend(dataset, sources,
				    trail));

				final Iterator <Recommendation> ret;

				if (followingRecommender == null)
					ret = new ConstraintRecommendationIterator(new EntityTypeConstraint(targetEntityTypes), iterator);
				else
					ret = new Iterator <Recommendation>()
					{
						@Override
						public boolean hasNext()
						{
							do
							{
								if (nextIterator == null || !nextIterator.hasNext())
								{
									if (!iterator.hasNext()) return false;
									final Recommendation recommendation = iterator.next();
									nextIterator = new PositiveIterator(followingRecommenderModel.recommend(
									    recommendation.getEntity(), targetEntityTypes));
									directScore = recommendation.getScore();
								}
							}
							while (!nextIterator.hasNext());

							return true;
						}

						@Override
						public Recommendation next()
						{
							if (!hasNext()) throw new NoSuchElementException();
							final Recommendation recommendation = nextIterator.next();
							return new Recommendation(recommendation.getEntity(), directScore
							    * recommendation.getScore());
						}

						@Override
						public void remove()
						{
							throw new UnsupportedOperationException();
						}

						/**
						 * Iterator for the last entity returned by the path recommender.
						 */
						private Iterator <Recommendation> nextIterator = null;

						/**
						 * Score of direct recommendation.
						 */
						private double directScore;
					};

				final Iterator <Recommendation> lookaheadIterator = new LookaheadRecommendationIterator(lookahead, ret);

				return new RecommendationResult()
				{
					@Override
					public Iterator <Recommendation> getIterator()
					{
						return lookaheadIterator;
					}

					@Override
					public Set <Entity> getVisitedEntities()
					    throws UnsupportedOperationException
					{
						final Set <Entity> visitedEntities = new HashSet <Entity>();
						for (final Entry <Entity, Set <DatasetEntry>> entry: trail.entrySet())
						{
							visitedEntities.add(entry.getKey());
							for (final DatasetEntry datasetEntry: entry.getValue())
								visitedEntities.add(datasetEntry.entity);
						}

						return visitedEntities;
					}

					@Override
					public Map <Entity, Set <DatasetEntry>> getTrail()
					    throws UnsupportedOperationException
					{
						return trail;
					}
				};
			}

			@Override
			public void update()
			{
				if (followingRecommenderModel != null) followingRecommenderModel.update();
			}

			@Override
			public Iterator <Recommendation> recommend(Entity source, EntityType[] targetEntityTypes)
			{
				final Map <Entity, Double> sources = new HashMap <Entity, Double>();
				sources.put(source, 1.);
				return recommendExt(sources, targetEntityTypes).getIterator();
			}

			@Override
			public Iterator <Recommendation> recommend(Map <Entity, Double> sources, EntityType[] targetEntityTypes)
			{
				return recommendExt(sources, targetEntityTypes).getIterator();
			}

			@Override
			public PredictorModel getPredictorModel()
			    throws UnsupportedOperationException
			{
				throw new UnsupportedOperationException();
			}
		};
	}

	@Override
	public String toString()
	{
		return String.format("Lookahead(%d)-%s%s", lookahead, path, followingRecommender == null ? "" : "-"
		    + followingRecommender);
	}
}
