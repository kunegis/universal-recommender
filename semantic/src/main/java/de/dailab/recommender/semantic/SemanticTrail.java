package de.dailab.recommender.semantic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Map.Entry;

import de.dailab.recommender.dataset.DatasetEntry;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.recommend.RecommendationResult;

/**
 * A semantic trail is a trail rooted at one recommended entity, usable for semantic dataset.
 * <p>
 * The current dataset doesn't use semantics, and could be renamed and moved to de.dailab.recommender.core.
 * <p>
 * This trail is rooted at only one recommended entity. In addition, limits on the number of included nodes, edges and
 * paths are possible. Setting low limits on the number of nodes and edges may remove source entities from the graph.
 * 
 * @author kunegis
 */
public class SemanticTrail
{
	/**
	 * A trail from the given entity to the source entities of the recommendation.
	 * 
	 * @param recommendationResult The result of the recommendation
	 * @param recommendedEntity The recommended entity
	 * @param entityCountMax Maximum number of entities to include in the trail
	 * @param pathCountMax Maximum number of paths to include in the trail
	 * @param pathLengthMax Maximum length of paths in the trail
	 */
	public SemanticTrail(RecommendationResult recommendationResult, Entity recommendedEntity, int entityCountMax,
	    int pathCountMax, int pathLengthMax)
	{
		this.recommendedEntity = recommendedEntity;

		final Map <Entity, Set <DatasetEntry>> fullTrail = recommendationResult.getTrail();

		computeTrail(fullTrail, recommendedEntity, entityCountMax, pathCountMax, pathLengthMax);
	}

	/**
	 * A trail from the given entity to the source entities of the recommendation.
	 * 
	 * @param fullTrail The underlying trail
	 * @param recommendedEntity The recommended entity
	 * @param entityCountMax Maximum number of entities to include in the trail
	 * @param pathCountMax Maximum number of paths to include in the trail
	 * @param pathLengthMax Maximum length of paths in the trail
	 */
	public SemanticTrail(Map <Entity, Set <DatasetEntry>> fullTrail, Entity recommendedEntity, int entityCountMax,
	    int pathCountMax, int pathLengthMax)
	{
		this.recommendedEntity = recommendedEntity;
		computeTrail(fullTrail, recommendedEntity, entityCountMax, pathCountMax, pathLengthMax);
	}

	/**
	 * Compute the trail for the given recommendation result and recommended entity. The default numbers of entities and
	 * paths and path length are used.
	 * 
	 * @param fullTrail The underlying full trail
	 * @param recommendedEntity The recommended entity
	 */
	public SemanticTrail(Map <Entity, Set <DatasetEntry>> fullTrail, Entity recommendedEntity)
	{
		this(fullTrail, recommendedEntity, ENTITY_COUNT_MAX_DEFAULT, PATH_COUNT_MAX_DEFAULT, PATH_LENGTH_MAX_DEFAULT);
	}

	private void computeTrail(Map <Entity, Set <DatasetEntry>> fullTrail, Entity recommendedEntity, int entityCountMax,
	    int pathCountMax, int pathLengthMax)
	{
		/**
		 * The queue contains elements that were already put into the trail, but not yet their neighbors.
		 */
		final Queue <Entity> queue = new LinkedList <Entity>();
		queue.add(recommendedEntity);

		/**
		 * Longest found distance from the recommended entity to each entity.
		 * <p>
		 * The keys are exactly the entries in the queue.
		 */
		final Map <Entity, Integer> distances = new HashMap <Entity, Integer>();
		distances.put(recommendedEntity, 0);

		/**
		 * For each entity in the queue, the previously visited entities on all paths to that entity.
		 */
		final Map <Entity, Set <Entity>> previous = new HashMap <Entity, Set <Entity>>();
		previous.put(recommendedEntity, new HashSet <Entity>());

		int entityCount = 0;
		int pathCount = 1;

		while (!queue.isEmpty() && entityCount < entityCountMax)
		{
			final Entity next = queue.remove();

			final int distance = distances.get(next);
			distances.remove(next);
			if (distance >= pathLengthMax) continue;

			final Set <Entity> previouses = previous.remove(next);

			Set <DatasetEntry> neighbors = fullTrail.get(next);
			if (neighbors == null) neighbors = new HashSet <DatasetEntry>();

			final Set <DatasetEntry> neighborsKept = new HashSet <DatasetEntry>();

			for (final DatasetEntry neighbor: neighbors)
			{
				if (trail.containsKey(neighbor.entity)) continue;
				if (previouses.contains(neighbor.entity)) continue;

				++entityCount;
				if (entityCount > entityCountMax) break;

				if (neighborsKept.size() > 0 && pathCount >= pathCountMax) break;

				++pathCount;

				neighborsKept.add(neighbor);

				int newDistance = distance + 1;
				final Integer previousDistance = distances.get(neighbor.entity);
				if (previousDistance != null && previousDistance > newDistance) newDistance = previousDistance;
				distances.put(neighbor.entity, newDistance);

				final Set <Entity> newPreviouses = new HashSet <Entity>();
				newPreviouses.addAll(previouses);
				newPreviouses.add(next);

				previous.put(neighbor.entity, newPreviouses);

				if (!queue.contains(neighbor.entity)) queue.add(neighbor.entity);
			}

			if (neighborsKept.size() > 0) trail.put(next, neighborsKept);
		}
	}

	/**
	 * Compute the trail for the given recommendation result and recommended entity. The default numbers of entities and
	 * paths and path length are used.
	 * 
	 * @param recommendationResult The result of the recommendation
	 * @param recommendedEntity The recommended entity
	 */
	public SemanticTrail(RecommendationResult recommendationResult, Entity recommendedEntity)
	{
		this(recommendationResult, recommendedEntity, ENTITY_COUNT_MAX_DEFAULT, PATH_COUNT_MAX_DEFAULT,
		    PATH_LENGTH_MAX_DEFAULT);
	}

	/**
	 * The recommended entity, i.e. the start of this trail.
	 */
	private final Entity recommendedEntity;

	/**
	 * The filtered trail, without loops.
	 */
	private final Map <Entity, Set <DatasetEntry>> trail = new HashMap <Entity, Set <DatasetEntry>>();

	/**
	 * Default maximum number of entities in a trail.
	 */
	public final static int ENTITY_COUNT_MAX_DEFAULT = 50;

	/**
	 * Default maximum number of paths in a trail.
	 */
	public final static int PATH_COUNT_MAX_DEFAULT = 10;

	/**
	 * Default maximum path length in a trail.
	 */
	public final static int PATH_LENGTH_MAX_DEFAULT = 5;

	/**
	 * Get the trail. The trail is a directed acyclic graph rooted on the recommended entity. The returned map contains,
	 * for each entity, the set of previous entities. Paths beginning at the recommended entity lead to the source
	 * entities of the recommendation.
	 * 
	 * @return The trail
	 */
	public Map <Entity, Set <DatasetEntry>> getTrail()
	{
		return trail;
	}

	@Override
	public String toString()
	{
		String ret = String.format("Trail for recommended entity %s:\n", recommendedEntity);

		for (final Entry <Entity, Set <DatasetEntry>> entry: trail.entrySet())
		{
			ret += String.format("\t%s %s\n", entry.getKey(), entry.getValue());
		}

		return ret;
	}

	/**
	 * @return the recommendedEntity
	 */
	public Entity getRecommendedEntity()
	{
		return recommendedEntity;
	}
}
