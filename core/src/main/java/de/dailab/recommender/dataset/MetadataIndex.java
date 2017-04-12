package de.dailab.recommender.dataset;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * A cache of the mapping between continuous integer IDs used by the data model and arbitrary object IDs used elsewhere.
 * <p>
 * Mappings are stored per entity type.
 * 
 * @author kunegis
 */
public class MetadataIndex
{

	/**
	 * Get the single ID cache of a given entity type. Add a single ID cache for the given entity type if not present.
	 * 
	 * @param entityType The entity type
	 * @return The single ID cache
	 */
	public SingleMetadataIndex getSingleIdCache(EntityType entityType)
	{
		SingleMetadataIndex ret = caches.get(entityType);

		if (ret == null)
		{
			ret = new SingleMetadataIndex();
			caches.put(entityType, ret);
		}

		return ret;
	}

	/**
	 * The object corresponding to an entity
	 * 
	 * @param entity an entity
	 * @return the corresponding object
	 */
	public Object getObject(Entity entity)
	{
		return caches.get(entity.getType()).getObject(entity.getId());
	}

	/**
	 * Get the entity of a given entity type corresponding to an object.
	 * 
	 * @param entityType The entity type of which to get the entity
	 * @param object The object corresponding to the entity
	 * @return The entity
	 * @throws IllegalArgumentException The given entity type is not contained in the index
	 * @throws NoSuchElementException When no entity has the given object as metadata
	 */
	public Entity getEntity(EntityType entityType, Object object)
	{
		final SingleMetadataIndex singleMetadataIndex = caches.get(entityType);
		if (singleMetadataIndex == null) throw new IllegalArgumentException("No such entity type:  " + entityType);
		return new Entity(entityType, singleMetadataIndex.getId(object));
	}

	private final Map <EntityType, SingleMetadataIndex> caches = new HashMap <EntityType, SingleMetadataIndex>();

	@Override
	public String toString()
	{

		String ret = "";

		for (final Map.Entry <EntityType, SingleMetadataIndex> e: caches.entrySet())
		{
			ret += String.format("%s: %s entities\n", e.getKey(), e.getValue().size());
		}

		return ret;
	}
}
