package de.dailab.recommender.path;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.DatasetEntry;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntitySet;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.RelationshipSet;
import de.dailab.recommender.matrix.FullEntry;
import de.dailab.recommender.matrix.Vector;
import de.dailab.recommender.matrix.template.MatrixFactory;
import de.dailab.recommender.predict.RelationshipTypePonderation;
import de.dailab.recommender.recommendation.Recommendation;

/**
 * A path that follows all edges. The resulting recommendations are sorted by path length, then by score. Scores are
 * weighted path sums.
 * <p>
 * This path can be interpreted as a spreading-activation path. The source entity is returned first, then all neighbors,
 * then all neighbors' neighbors, etc.
 * <p>
 * Due to the way the all-path is implemented, the longest paths that can be extracted have length about 20.
 * <p>
 * By default, paths of all lengths are returned. A minimum path length can be set, in which case paths cannot
 * backtrack. A large minimal path length may lead to no returned recommendations--if the diameter of the network is
 * small.
 * <p>
 * To avoid infinite weights, all edge weights are divided by an estimated upper bound of the spectral radius of the
 * network. This is not done when a minimal length is set, since not multiple paths are used in this case.
 * <p>
 * Recommendations are weighted by path length. The decay parameter determines the weighting. A path of length n is give
 * weight n^decay. These weights can be changed for short paths explicitly. When short path weights are explicitly
 * given, the decay is applied only to longer paths.
 * <p>
 * The memory usage of this path is linear in the number of entities not further away from the sources than the last
 * recommendation.
 * 
 * @see <a href="http://en.wikipedia.org/wiki/Spreading_activation">Wikipedia: Spreading activation</a>
 * 
 * @author kunegis
 */
public class AllPath2
    implements MultiPath
{
	/**
	 * All all-path using the given relationship type ponderation and decay.
	 * 
	 * @param relationshipTypePonderation Weights for relationship types to use; NULL denotes the empty relationship
	 *        type ponderation
	 * @param decay The decay of each edge; must be between 0 and 1 (exclusive)
	 * @param shortPathWeights Weights of short paths; overrides the decay for short paths. The entry with index i
	 *        determines the weight of path length i. NULL is interpreted as the empty array. Default is the empty
	 *        array. All entries must be strictly positive.
	 * @param minLength Minimum length of paths to return. If nonzero, only shortest paths are returned. Default is
	 *        zero.
	 */
	public AllPath2(RelationshipTypePonderation relationshipTypePonderation, double decay, double[] shortPathWeights,
	    int minLength)
	{
		this.relationshipTypePonderation = relationshipTypePonderation == null ? new RelationshipTypePonderation()
		    : relationshipTypePonderation;

		assert decay > 0;
		assert decay < 1;
		this.decay = decay;

		this.shortPathWeights = shortPathWeights == null ? new double[] {} : shortPathWeights;
		for (final double shortPathWeight: this.shortPathWeights)
		{
			if (!(shortPathWeight > 0)) throw new Error("Short path weights must be positive (use 1 as default)");
		}

		assert minLength >= 0;
		this.minLength = minLength;
	}

	/**
	 * An all-path using the give relationship type ponderation, decay and minimal length.
	 * 
	 * @param relationshipTypePonderation The relationship type ponderation to use; NULL denotes the empty ponderation
	 * @param decay The decay factor
	 * @param minLength The minimal path length to return
	 */
	public AllPath2(RelationshipTypePonderation relationshipTypePonderation, double decay, int minLength)
	{
		this(relationshipTypePonderation, decay, null, minLength);
	}

	/**
	 * An all-path using the given relationship type ponderation, decay and short path weights. There is no minimal
	 * weight.
	 * <p>
	 * The inverse approximate spectral radius will be used as an additional factor to avoid divergence. This may add
	 * another very small factor to long paths. Use a minimal length larger than one to avoid this.
	 * 
	 * @param relationshipTypePonderation The relationship type ponderation to use; NULL represents the empty
	 *        ponderation
	 * @param decay The decay factor
	 * @param shortPathWeights Weights for short paths; must all be positive
	 */
	public AllPath2(RelationshipTypePonderation relationshipTypePonderation, double decay, double[] shortPathWeights)
	{
		this(relationshipTypePonderation, decay, shortPathWeights, 0);
	}

	/**
	 * The all-path with given relationship type ponderation and decay.
	 * 
	 * @param relationshipTypePonderation The relationship type ponderation to use; NULL represents the empty
	 *        ponderation
	 * @param decay The decay factor
	 */
	public AllPath2(RelationshipTypePonderation relationshipTypePonderation, double decay)
	{
		this(relationshipTypePonderation, decay, null, 0);
	}

	/**
	 * An all-path with the given relationship type ponderation and default decay.
	 * 
	 * @param relationshipTypePonderation Weighting of relationship types; NULL denotes the empty relationship type
	 *        ponderation
	 */
	public AllPath2(RelationshipTypePonderation relationshipTypePonderation)
	{
		this(relationshipTypePonderation, DECAY_DEFAULT);
	}

	/**
	 * An all-path using the given decay and unweighted relationship type weighting.
	 * 
	 * @param decay The decay to use
	 */
	public AllPath2(double decay)
	{
		this(new RelationshipTypePonderation(), decay);
	}

	/**
	 * An all-path with default parameters
	 */
	public AllPath2()
	{
		this(new RelationshipTypePonderation(), DECAY_DEFAULT);
	}

	@Override
	public Path invert()
	{
		return this;
	}

	@Override
	public String toString()
	{
		return "All2";
	}

	@Override
	public Iterator <Recommendation> recommend(final Dataset dataset, Map <Entity, Double> sources,
	    final Map <Entity, Set <DatasetEntry>> trail)
	{
		/**
		 * Maximum absolute weights by relationship type
		 */
		double maxWeightOverall = 0;
		for (final RelationshipSet relationshipSet: dataset.getRelationshipSets())
		{
			final double ponderation = relationshipTypePonderation.getWeightDefault(relationshipSet.getType());
			double maxWeightNew = 1. / ponderation;
			for (final FullEntry fullEntry: relationshipSet.getMatrix().all())
			{
				maxWeightNew = Math.max(maxWeightNew, Math.abs(fullEntry.value));
			}

			maxWeightNew *= ponderation;

			if (maxWeightNew > maxWeightOverall) maxWeightOverall = maxWeightNew;
		}

		final double maxWeight = maxWeightOverall;

		/**
		 * Maximal quotient of consecutive short path lengths.
		 */
		double _maxShortPathWeightQuotient = 1;
		for (int i = 0; i + 1 < shortPathWeights.length; ++i)
		{
			final double shortPathWeightQuotient = shortPathWeights[i + 1] / shortPathWeights[i];
			if (shortPathWeightQuotient > _maxShortPathWeightQuotient)
			    _maxShortPathWeightQuotient = shortPathWeightQuotient;
		}
		final double maxShortPathWeightQuotient = _maxShortPathWeightQuotient;

		/**
		 * Effective decay
		 */
		double _radius = 1;
		if (minLength == 0)
		{
			double weightSum = 0;
			for (final RelationshipSet relationshipSet: dataset.getRelationshipSets())
			{
				double weightSumType = 0;

				for (final FullEntry fullEntry: relationshipSet.getMatrix().all())
				{
					weightSumType += Math.abs(fullEntry.value);
				}

				final double ponderation = relationshipTypePonderation.getWeightDefault(relationshipSet.getType())
				    / maxWeightOverall;

				weightSum += weightSumType * ponderation;
			}

			int entityCount = 0;
			for (final EntitySet entitySet: dataset.getEntitySets())
				entityCount += entitySet.size();

			_radius = weightSum;
		}
		final double radius = _radius;

		/**
		 * Depth.
		 */
		final int l[] = new int[1];

		/**
		 * Vector of weights. All entity types are represented.
		 */
		final Map <EntityType, Vector> weightVectors = new HashMap <EntityType, Vector>();

		for (final EntitySet entitySet: dataset.getEntitySets())
			weightVectors.put(entitySet.getType(), MatrixFactory.newMemoryVector(entitySet.size()));

		for (final Entry <Entity, Double> entry: sources.entrySet())
		{
			double weight = entry.getValue();
			if (shortPathWeights.length > 0) weight *= shortPathWeights[0];
			weightVectors.get(entry.getKey().getType()).setGeneric(entry.getKey().getId(), weight);
		}

		/*
		 * Visited; entities in WEIGHTS are included. Only honored when there is a minimal length.
		 */
		final Map <EntityType, Vector> visitedVectors = new HashMap <EntityType, Vector>();
		for (final EntitySet entitySet: dataset.getEntitySets())
			visitedVectors.put(entitySet.getType(), MatrixFactory.newMemoryVectorUnweighted(entitySet.size()));

		if (minLength > 0)
		{
			for (final Entity source: sources.keySet())
				visitedVectors.get(source.getType()).setGeneric(source.getId(), 1);
		}

		/**
		 * Remaining entities to recommend from current WEIGHTS vector.
		 */
		final Queue <Recommendation> next = new LinkedList <Recommendation>();

		if (minLength == 0) updateNext(next, weightVectors);

		return new Iterator <Recommendation>()
		{
			@Override
			public boolean hasNext()
			{
				if (!next.isEmpty()) return true;

				if (minLength == 0)
					multiply();
				else
				{
					do
						multiply();
					while (l[0] < minLength && !next.isEmpty());
				}

				return !next.isEmpty();
			}

			@Override
			public Recommendation next()
			{
				return next.remove();
			}

			@Override
			public void remove()
			{
				throw new UnsupportedOperationException();
			}

			/**
			 * Overwrite WEIGHTS with the product of WEIGHTS and the dataset, and set NEXT to the sorted list of new
			 * elements in WEIGHTS. Add followed edges to the trail.
			 */
			private void multiply()
			{
				if (DEBUG) System.out.printf("multiply\n");

				/* Multiply */
				final Map <EntityType, Vector> newWeightVectors = new HashMap <EntityType, Vector>();
				for (final Entry <EntityType, Vector> entry: weightVectors.entrySet())
					newWeightVectors.put(entry.getKey(), MatrixFactory.newMemoryVector(entry.getValue().getIndexType(),
					    entry.getValue().getWeightType()));

				/*
				 * Effective decay
				 */
				++l[0];
				final double effectiveDecay = l[0] < shortPathWeights.length ? shortPathWeights[l[0]]
				    / shortPathWeights[l[0] - 1] : decay;

				for (final Entry <EntityType, Vector> e: weightVectors.entrySet())
					for (final de.dailab.recommender.matrix.Entry e2: e.getValue())
					{
						final double value = e2.value;

						final Entity entity = new Entity(e.getKey(), e2.index);

						for (final DatasetEntry datasetEntry: dataset.getNeighbors(entity))
						{
							if (visitedVectors.get(datasetEntry.entity.getType()).getGeneric(
							    datasetEntry.entity.getId()) != 0) continue;

							/* Add to trail */
							if (trail != null)
							{
								Set <DatasetEntry> previouses = trail.get(datasetEntry.entity);
								if (previouses == null)
								{
									previouses = new HashSet <DatasetEntry>();
									trail.put(datasetEntry.entity, previouses);
								}
								final DatasetEntry newPrevious = new DatasetEntry(entity,
								    datasetEntry.relationshipType, !datasetEntry.forward, datasetEntry.weight);
								if (!previouses.contains(newPrevious))
								{
									previouses.add(newPrevious);
								}
							}

							/* Add to NEW_WEIGHTS */
							final double ponderation = relationshipTypePonderation
							    .getWeightDefault(datasetEntry.relationshipType);
							final double factor = effectiveDecay / radius * datasetEntry.weight / maxWeight
							    * ponderation;
							assert Math.abs(factor) <= 1 * maxShortPathWeightQuotient;

							final double newValue = value * factor;

							/*
							 * Cut-off when weight is zero.
							 */
							if (newValue == 0.) continue;

							newWeightVectors.get(datasetEntry.entity.getType()).addGeneric(datasetEntry.entity.getId(),
							    newValue);
						}
					}

				for (final Entry <EntityType, Vector> entry: weightVectors.entrySet())
				{
					entry.setValue(newWeightVectors.get(entry.getKey()));
				}

				if (minLength > 0)
				{
					for (final Entry <EntityType, Vector> entry: weightVectors.entrySet())
					{
						final Vector vector = visitedVectors.get(entry.getKey());
						for (final de.dailab.recommender.matrix.Entry e2: entry.getValue())
						{
							vector.addGeneric(e2.index, 1.);
						}
					}
				}

				/* Set next */
				updateNext(next, weightVectors);

				if (DEBUG) System.out.printf("\t%d entities\n", next.size());
			}
		};
	}

	@Override
	public Iterator <Recommendation> recommend(Dataset dataset, Entity source, Map <Entity, Set <DatasetEntry>> trail)
	{
		final Map <Entity, Double> sources = new HashMap <Entity, Double>();
		sources.put(source, 1.);
		return recommend(dataset, sources, trail);
	}

	/**
	 * Clear NEXT and put all entities in WEIGHTS into NEXT from the highest-scored to the lowest.
	 */
	private void updateNext(Queue <Recommendation> next,/* final Map <Entity, Double> weights */
	final Map <EntityType, Vector> weightVectors)
	{
		next.clear();

		final SortedSet <Entity> sortedEntities = new TreeSet <Entity>(new Comparator <Entity>()
		{
			@Override
			public int compare(Entity entity_1, Entity entity_2)
			{
				final double weight_1 = weightVectors.get(entity_1.getType()).getGeneric(entity_1.getId());
				final double weight_2 = weightVectors.get(entity_2.getType()).getGeneric(entity_2.getId());

				if (weight_1 < weight_2) return +1;
				if (weight_1 > weight_2) return -1;

				return entity_1.compareTo(entity_2);
			}
		});

		for (final Entry <EntityType, Vector> e: weightVectors.entrySet())
			for (final de.dailab.recommender.matrix.Entry e2: e.getValue())
				sortedEntities.add(new Entity(e.getKey(), e2.index));

		for (final Entity entity: sortedEntities)
			next.add(new Recommendation(entity, weightVectors.get(entity.getType()).getGeneric(entity.getId())));
	}

	private final RelationshipTypePonderation relationshipTypePonderation;

	private final double decay;

	/**
	 * Not NULL.
	 */
	private final double shortPathWeights[];

	private final int minLength;

	/**
	 * Default value of the decay parameter.
	 */
	public final static double DECAY_DEFAULT = 0.85;

	private final static boolean DEBUG = false;
}
