package de.dailab.recommender.dataset;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * A cache of the mapping between continuous integer IDs (beginning at 0) and Object IDs for the entities of one type.
 * <p>
 * The objects must implement hashCode() and equals().
 * <p>
 * Objects cannot be removed; only added.
 * 
 * @author kunegis
 */
public class SingleMetadataIndex
{
	/**
	 * Initialize an empty single ID cache.
	 */
	public SingleMetadataIndex()
	{
	/* do nothing */
	}

	/**
	 * Add an object, assign it the next available integer ID, and return the assigned ID. If the object is already in
	 * the cache, do nothing and return its ID.
	 * 
	 * @param object An object to add. Must implement hashCode() and equals(). Must not be NULL.
	 * @return The continuous integer ID of the object, either newly assigned or already present.
	 */
	public int add(Object object)
	{
		assert object != null;

		final Integer i = ids.get(object);
		if (i != null)
		{
			assert objects.get(i).equals(object);
			return i;
		}
		final int newI = objects.size();
		ids.put(object, newI);
		objects.add(object);
		return newI;
	}

	/**
	 * Add an object with a given ID.
	 * <p>
	 * If several objects have the same ID, this may bze reflected in the content of this single metadata index.
	 * 
	 * @param id The ID
	 * @param object The object
	 */
	public void add(int id, Object object)
	{
		assert object != null;

		ids.put(object, id);

		/*
		 * There doesn't seem to be a way to do setSize().
		 */
		objects.ensureCapacity(id + 1);
		while (objects.size() <= id)
			objects.add(null);
		objects.set(id, object);
	}

	/**
	 * Get the ID of a given object.
	 * 
	 * @param object An object
	 * @return The dataset-internal ID
	 * @throws NoSuchElementException when no such entity is present
	 */
	public int getId(Object object)
	{
		final Integer ret = ids.get(object);
		if (ret == null) throw new NoSuchElementException(String.format("No entity corresponds to %s", object));
		return ret;
	}

	/**
	 * Get the object associated with a given ID.
	 * 
	 * @param id the database-internal ID
	 * @return The associated object
	 */
	public Object getObject(int id)
	{
		assert id >= 0 && id < objects.size();
		final Object ret = objects.get(id);
		assert ret != null;
		return ret;
	}

	/**
	 * @return the number of entities
	 */
	public int size()
	{
		assert ids.size() == objects.size();
		return ids.size();
	}

	/**
	 * @return the objects by ID
	 */
	public List <Object> getObjects()
	{
		return objects;
	}

	private final Map <Object, Integer> ids = new HashMap <Object, Integer>();
	private final ArrayList <Object> objects = new ArrayList <Object>();

}
