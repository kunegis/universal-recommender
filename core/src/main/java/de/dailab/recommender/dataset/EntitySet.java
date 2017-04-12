package de.dailab.recommender.dataset;

import java.lang.reflect.Array;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A collection of entities of a single type.
 * <p>
 * In addition to the collection of entities, an entity set contains entity metadata. The IDs of entities are continuous
 * and begin at zero.
 * <p>
 * Typical object usage:
 * <ul>
 * <li>Set size
 * <li>Set metadata names
 * <li>Set metadata
 * </ul>
 * <p>
 * Metadata fields are indexed by a MetadataType, and have one of the following types:
 * <ul>
 * <li>String
 * <li>Integer
 * <li>Double
 * <li>java.util.Date (only the time is stored)
 * </ul>
 * 
 * <p>
 * The number of entities in an entity set can be zero.
 * 
 * @author kunegis
 */
public final class EntitySet
{
	/**
	 * Create an empty entity set of a given type.
	 * 
	 * @param type an entity type
	 */
	public EntitySet(EntityType type)
	{
		assert type != null;
		this.type = type;
	}

	/**
	 * Entity type
	 */
	private final EntityType type;

	/**
	 * Number of entities of this type.
	 */
	private int size = 0;

	/**
	 * The metadata. For each metadata type, an array of one of the following types: String[interned], int, double,
	 * long.
	 */
	private final Map <MetadataName, Object> metadata = new HashMap <MetadataName, Object>();

	/**
	 * The metadata type order. May be NULL when empty.
	 */
	private List <MetadataName> metadataNames = null;

	/**
	 * Get the type of entities in this entity set.
	 * 
	 * @return The type of entities in this entity set
	 */
	public EntityType getType()
	{
		return type;
	}

	/**
	 * Get the number of entities in this entity set.
	 * <p>
	 * The size must have been set.
	 * 
	 * @return The number of entities; may be zero
	 * @deprecated Use size()
	 */
	@Deprecated
	public int getCount()
	{
		return size();
	}

	/**
	 * Get the number of entities in this entity set.
	 * 
	 * @return The number of entities
	 */
	public int size()
	{
		assert size >= 0;
		return size;
	}

	/**
	 * @return The metadata types present in this entity set
	 */
	public Set <MetadataName> getMetadataNames()
	{
		return metadata.keySet();
	}

	/**
	 * Set the number of entities.
	 * 
	 * @param size The number of entities; can be zero
	 * @deprecated Use setSize(int)
	 */
	@Deprecated
	public void setCount(int size)
	{
		setSize(size);
	}

	/**
	 * Set the number of entities. There must not be metadata.
	 * 
	 * @param size The size to set
	 */
	public void setSize(int size)
	{
		assert metadataNames == null;
		assert size >= 0;
		this.size = size;
	}

	/**
	 * Set the metadata names and types. Must be called before inserting the metadata. Both arguments have the same
	 * ordering.
	 * 
	 * @param metadataNames the metadata names
	 * @param sampleMetadata Contains example objects of the corresponding Object type.
	 */
	public void setMetadataNames(List <MetadataName> metadataNames, List <Object> sampleMetadata)
	{
		assert size >= 0;
		assert metadataNames.size() == sampleMetadata.size();

		for (int i = 0; i < metadataNames.size(); ++i)
		{
			assert metadataNames.get(i) != null;
			assert sampleMetadata.get(i) != null;
		}

		this.metadataNames = metadataNames;

		for (int i = 0; i < metadataNames.size(); ++i)
		{
			final MetadataName metadataName = metadataNames.get(i);
			final Object object = sampleMetadata.get(i);

			if (object instanceof String)
			{
				metadata.put(metadataName, new String[size]);
			}
			else if (object instanceof Integer)
			{
				metadata.put(metadataName, new int[size]);
			}
			else if (object instanceof Double)
			{
				metadata.put(metadataName, new double[size]);
			}
			else if (object instanceof Date)
			{
				metadata.put(metadataName, new long[size]);
			}
			else
				throw new IllegalArgumentException(String.format("Invalid metadata type %s in metadata %s", object
				    .getClass().getSimpleName(), metadataName));
		}
	}

	/**
	 * Set the metadata values for one entity. The string metadata values must be interned.
	 * 
	 * @param entityId The ID of the entity to add metadata for; zero-based; must be between 0 (inclusive) and SIZE
	 *        (exclusive)
	 * @param metadataValues The metadata in object format, i.e. as String, Integer, Double, Date.
	 */
	public void setMetadataValues(int entityId, List <Object> metadataValues)
	{
		assert 0 <= entityId && entityId < size;
		assert metadataValues.size() == metadataNames.size();

		for (int i = 0; i < metadataNames.size(); ++i)
		{
			final Object array = metadata.get(metadataNames.get(i));

			final Object object = metadataValues.get(i);

			setMetadata(entityId, array, object);
		}
	}

	/**
	 * Set a metadata for a given entity.
	 * 
	 * @param entityId The ID of the entity of which to set the metadata
	 * @param metadataName The name of the metadata to change
	 * @param metadataValue The value to set; must be consistent with the type declared using setMetadataTypes().
	 */
	public void setMetadata(int entityId, MetadataName metadataName, Object metadataValue)
	{
		final Object array = metadata.get(metadataName);
		setMetadata(entityId, array, metadataValue);
	}

	private void setMetadata(int entityId, Object array, Object metadataValue)
	{
		if (metadataValue instanceof String)
		{
			final String string = (String) metadataValue;
			final String stringArray[] = (String[]) array;
			stringArray[entityId] = string;
		}
		else if (metadataValue instanceof Integer)
		{
			final int number = ((Integer) metadataValue).intValue();
			final int intArray[] = (int[]) array;
			intArray[entityId] = number;
		}
		else if (metadataValue instanceof Double)
		{
			final double number = ((Double) metadataValue).longValue();
			final double doubleArray[] = (double[]) array;
			doubleArray[entityId] = number;
		}
		else if (metadataValue instanceof Date)
		{
			final Date date = (Date) metadataValue;
			final long dateArray[] = (long[]) array;
			dateArray[entityId] = date.getTime();
		}
	}

	/**
	 * Retrieve a specific metadata of an entity.
	 * 
	 * @param entityId The entity in this entity set
	 * @param metadataName The metadata to retrieve
	 * @return The metadata. The type depends on the metadata name.
	 */
	public Object getMetadata(int entityId, MetadataName metadataName)
	{
		final Object array = metadata.get(metadataName);
		assert array != null;

		if (array instanceof String[])
		{
			return ((String[]) array)[entityId];
		}
		else if (array instanceof int[])
		{
			return ((int[]) array)[entityId];
		}
		else if (array instanceof double[])
		{
			return ((double[]) array)[entityId];
		}
		else if (array instanceof long[])
		{
			return new Date(((long[]) array)[entityId]);
		}
		else
		{
			throw new IllegalArgumentException(String
			    .format("Invalid metadata type for metadata name %s", metadataName));
		}
	}

	/**
	 * Get the type of the given metadata.
	 * <p>
	 * If the class is simple (e.g. double), then the class of the simple class is returned.
	 * 
	 * @param metadataName The metadata name
	 * @return The corresponding class
	 */
	public Class <?> getMetadataClass(MetadataName metadataName)
	{
		return metadata.get(metadataName).getClass().getComponentType();
	}

	/**
	 * Retrieve all metadata of an entity.
	 * 
	 * @param entityId The entity to retrieve the metadata of
	 * @return all metadata in their respective types
	 */
	public Object[] getAllMetadata(int entityId)
	{
		final Object ret[] = new Object[metadataNames.size()];

		int i = 0;
		for (final MetadataName metadataName: metadataNames)
		{
			ret[i++] = getMetadata(entityId, metadataName);
		}

		return ret;
	}

	/**
	 * Add an entity to the set. The new entity will have as ID the current size of the entity set. All metadata is NULL
	 * or zero initially.
	 * 
	 * @return The ID of the new entity
	 */
	public int addEntity()
	{
		/* Set size */
		final int ret = size++;

		/* Resize metadata arrays */
		for (final MetadataName metadataName: metadataNames)
		{
			final Object array = metadata.get(metadataName);
			final Object newArray = Array.newInstance(array.getClass().getComponentType(), size);
			System.arraycopy(array, 0, newArray, 0, size - 1);
			metadata.put(metadataName, newArray);
		}

		return ret;
	}
}
