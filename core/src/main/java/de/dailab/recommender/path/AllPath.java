package de.dailab.recommender.path;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.DatasetEntry;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.RelationshipSet;
import de.dailab.recommender.matrix.Entry;
import de.dailab.recommender.predict.RelationshipTypePonderation;
import de.dailab.recommender.recommendation.Recommendation;

/**
 * A path that follows all links. This is the default path in many applications.
 * <p>
 * This path follows paths of any length, weighted by the product of edge weights multiplied by a decay parameter
 * (between zero and one).
 * <p>
 * Each entity is recommended up to MAX_VISIT times. Use a lookahead recommended to add the scores together.
 * <p>
 * As a result, this path gives higher weight to nodes connected by many short paths.
 * <p>
 * The all-path can be describes as implementing the spreading activation model.
 * 
 * @see <a href="http://en.wikipedia.org/wiki/Spreading_activation">Wikipedia: Spreading activation</a>
 * 
 * @deprecated Use AllPath2
 * 
 * @author kunegis
 */
@Deprecated
public class AllPath
    implements Path
{
	// XXX handle edge weights greater than one, i.e. not just cutting values.

	/**
	 * A path that follows all links using a given relationship type ponderation, decay and max visit value.
	 * 
	 * @param relationshipTypePonderation The weighting of edges by relationship types
	 * @param decay The decay; a number less than one in absolute value
	 * @param maxVisit The maximum number of times a given entity shall be returned when it is reached by parallel paths
	 */
	public AllPath(RelationshipTypePonderation relationshipTypePonderation, double decay, int maxVisit)
	{
		this.relationshipTypePonderation = relationshipTypePonderation;
		this.decay = decay;
		this.maxVisit = maxVisit;
	}

	/**
	 * The full path without edge weighting and default decay and max visit value.
	 */
	public AllPath()
	{
		this(null, DECAY_DEFAULT, MAX_VISIT_DEFAULT);
	}

	/**
	 * A path that follows all links, using the given relationship type ponderation.
	 * 
	 * @param relationshipTypePonderation The importance of relationship types
	 */
	public AllPath(RelationshipTypePonderation relationshipTypePonderation)
	{
		this(relationshipTypePonderation, DECAY_DEFAULT, MAX_VISIT_DEFAULT);
	}

	/**
	 * Set only the max visit parameter to a non-default value.
	 * 
	 * @param maxVisit The maximum number of times to visit an entity
	 */
	public AllPath(int maxVisit)
	{
		this(null, DECAY_DEFAULT, maxVisit);
	}

	@Override
	public Path invert()
	{
		/*
		 * Since the full path follows all links, inverting it results in the same path.
		 */
		return this;
	}

	@Override
	public Iterator <Recommendation> recommend(final Dataset dataset, Entity source,
	    final Map <Entity, Set <DatasetEntry>> trail)
	{
		/*
		 * Use breadth-first search, effectively finding entities at a short graph distance of SOURCE. Parallel paths
		 * are supported by wrapping the AllPath in a lookahead recommendation iterator.
		 */

		/**
		 * Set of already recommended entities and those in NEXT with return counts for each.
		 */
		final Map <Entity, Integer> previous = new HashMap <Entity, Integer>();

		/**
		 * Contains the scores of all entities in NEXT.
		 */
		final Map <Entity, Double> scores = new HashMap <Entity, Double>();

		/**
		 * Contains the previouses of all entities in NEXT.
		 */
		final Map <Entity, Set <DatasetEntry>> previouses = new HashMap <Entity, Set <DatasetEntry>>();

		final Comparator <Entity> comparator = new Comparator <Entity>()
		{
			@Override
			public int compare(Entity entity_1, Entity entity_2)
			{
				final double score_1 = scores.get(entity_1);
				final double score_2 = scores.get(entity_2);

				if (score_2 > score_1) return +1;
				if (score_2 < score_1) return -1;
				return entity_1.compareTo(entity_2);
			}
		};

		/**
		 * FIFO of next entities by their current score as stored in scores and previouses. Order is by scores, so
		 * elements have to be removed and reinserted when the score changes.
		 */
		final PriorityQueue <Entity> next = new PriorityQueue <Entity>(1, comparator);

		/**
		 * Maximal weight of edges, taking into account ponderations.
		 */
		double _maxWeight = 1;

		/**
		 * The relationship type ponderation, but never NULL.
		 */
		final RelationshipTypePonderation _relationshipTypePonderation = relationshipTypePonderation == null ? new RelationshipTypePonderation()
		    : relationshipTypePonderation;

		for (final double w: _relationshipTypePonderation.getWeights().values())
		{
			_maxWeight = Math.max(_maxWeight, w);
		}

		previous.put(source, 1);
		next.add(source);
		scores.put(source, 1.);
		previouses.put(source, new HashSet <DatasetEntry>());

		final double maxWeight = _maxWeight;

		final Random random = new Random();

		return new Iterator <Recommendation>()
		{
			@Override
			public boolean hasNext()
			{

				if (nextRecommendation != null) return true;

				do
				{
					if (next.peek() == null) return false;

					final double nextScore = scores.get(next.peek());

					Iterator <Entity> nextIterator = next.iterator();
					int c = 0;
					double s;
					do
					{
						++c;
						final Entity nNextEntity = nextIterator.next();
						s = scores.get(nNextEntity);
					}
					while (s == nextScore && nextIterator.hasNext());
					assert c > 0;
					final int index = random.nextInt(c);
					nextIterator = next.iterator();
					for (int i = 0; i + 1 < index; ++i)
						nextIterator.next();
					final Entity nextEntity = nextIterator.next();
					next.remove(nextEntity);

					nextRecommendation = new RecommendationWithPrevious(nextEntity, scores.get(nextEntity), previouses
					    .get(nextEntity));
					scores.remove(nextEntity);
					previouses.remove(nextEntity);

// if (DEBUG) System.out.printf("--- %s ---\n", nextEntity);

					final Entity entity = nextRecommendation.recommendation.getEntity();

					for (final RelationshipSet relationshipSet: dataset.getRelationshipSets())
					{
						if (relationshipSet.getSubject().equals(entity.getType()))
						{
							final double factor = decay
							    * _relationshipTypePonderation.getWeightDefault(relationshipSet.getType()) / maxWeight;
							assert Math.abs(factor) < 1;
							final double weight = factor * nextRecommendation.recommendation.getScore();

							if (weight == 0) continue;

							for (final Entry entry: relationshipSet.getMatrix().row(entity.getId()))
							{
								final Entity newEntity = new Entity(relationshipSet.getObject(), entry.index);
								final Integer count = previous.get(newEntity);
								if (count == null || count < maxVisit)
								{
									previous.put(newEntity, count == null ? 1 : count + 1);
									addNext(newEntity, weight * normalize(entry.value), new DatasetEntry(entity,
									    relationshipSet.getType(), false, entry.value));
								}
							}
						}
						if (relationshipSet.getObject().equals(entity.getType()))
						{
							final double factor = decay
							    * _relationshipTypePonderation.getWeightDefault(relationshipSet.getType()) / maxWeight;
							assert Math.abs(factor) < 1;
							final double weight = factor * nextRecommendation.recommendation.getScore();

							if (weight == 0) continue;

							for (final Entry entry: relationshipSet.getMatrix().col(entity.getId()))
							{
								final Entity newEntity = new Entity(relationshipSet.getSubject(), entry.index);
								final Integer count = previous.get(newEntity);
								if (count == null || count < maxVisit)
								{
									previous.put(newEntity, count == null ? 1 : count + 1);
									addNext(newEntity, weight * normalize(entry.value), new DatasetEntry(entity,
									    relationshipSet.getType(), true, entry.value));
								}
							}
						}
					}

					output();

					/* Don't return negative recommendations */
					if (nextRecommendation.recommendation.getScore() < 0) nextRecommendation = null;
				}
				while (nextRecommendation == null);

				return true;
			}

			@Override
			public Recommendation next()
			{
				if (!hasNext()) throw new NoSuchElementException();
				final RecommendationWithPrevious recommendationWithPrevious = nextRecommendation;
				nextRecommendation = null;

				if (recommendationWithPrevious.previousDatasetEntries != null)
				{
					Set <DatasetEntry> datasetEntrySet = trail.get(recommendationWithPrevious.recommendation
					    .getEntity());
					if (datasetEntrySet == null)
					{
						datasetEntrySet = new HashSet <DatasetEntry>();
						trail.put(recommendationWithPrevious.recommendation.getEntity(), datasetEntrySet);
					}
					datasetEntrySet.addAll(recommendationWithPrevious.previousDatasetEntries);
				}

				return recommendationWithPrevious.recommendation;
			}

			@Override
			public void remove()
			{
				throw new UnsupportedOperationException();
			}

			private void addNext(Entity entity, double score, DatasetEntry previousDatasetEntry)
			{
				if (next.contains(entity))
				{
					next.remove(entity);
					score += scores.get(entity);
					final Set <DatasetEntry> previousDatasetEntries = previouses.get(entity);
					previousDatasetEntries.add(previousDatasetEntry);
					next.add(entity);
				}
				else
				{
					scores.put(entity, score);
					final Set <DatasetEntry> previousDatasetEntries = new HashSet <DatasetEntry>();
					previousDatasetEntries.add(previousDatasetEntry);
					previouses.put(entity, previousDatasetEntries);
					next.add(entity);
				}
			}

			/**
			 * The next prefetched recommendation, or NULL when not prefetched.
			 */
			private RecommendationWithPrevious nextRecommendation = null;

			private void output()
			{
				if (DEBUG)
				{
					System.err.printf("previously recommended:  %d,  next:  %s\n", previous.size(), next.size());

// System.out.printf("Previously recommended and in NEXT: %s\n", previous);
//
// System.out.print("Next: ");
// for (final Entity entity: next)
// {
// System.out.printf("[%.3g]-%s ", scores.get(entity), entity);
// }
// System.out.println();
//
// System.out.println();
				}

			}
		};

	}

	/**
	 * The relationship type ponderation; may be NULL for the unweighted ponderation.
	 */
	private final RelationshipTypePonderation relationshipTypePonderation;

	/**
	 * Penalty given for each additional link to follow. The absolute value is less than one.
	 */
	private final double decay;

	/**
	 * Maximum number of visits to consider for parallel path support.
	 */
	private final int maxVisit;

	@Override
	public String toString()
	{
		return String.format("All(%s%g, %d)", relationshipTypePonderation == null ? "" : relationshipTypePonderation
		    + ", ", decay, maxVisit);
	}

	/**
	 * Map an edge weight to the range [-1, +1].
	 */
	private static double normalize(double x)
	{
		if (x > +1.0) x = +1.0;
		if (x < -1.0) x = -1.0;

		return x;
	}

	/**
	 * The default value of the max visit parameter, as used in the constructors that don't take a max visit parameter.
	 */
	public final static int MAX_VISIT_DEFAULT = 2;

	/**
	 * Default value for the decay parameter, as used in the constructors without a decay parameter.
	 */
	public final static double DECAY_DEFAULT = .85;

	private final static boolean DEBUG = false;

	/**
	 * A recommendation along with a previous entity. Equals is implemented in terms of the recommended entity, and
	 * compareTo in terms of the score of the entities are different.
	 * 
	 * @author kunegis
	 */
	private static class RecommendationWithPrevious
	{
		public RecommendationWithPrevious(Entity entity, double score, Set <DatasetEntry> previousDatasetEntries)
		{
			this.recommendation = new Recommendation(entity, score);
			this.previousDatasetEntries = previousDatasetEntries;
		}

		public final Recommendation recommendation;
		public final Set <DatasetEntry> previousDatasetEntries;
	}
}
