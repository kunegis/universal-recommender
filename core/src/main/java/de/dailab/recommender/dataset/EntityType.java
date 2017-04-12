package de.dailab.recommender.dataset;

/**
 * An entity type in a compound dataset.
 * <p>
 * An entity type is represented by a string, and this class simply encapsulates a string.
 * <p>
 * Entity types are usually lowercase with words connected by dashes, but this is not always the case. Examples are
 * "user", "movie", etc. Some datasets use URIs are entity types. Entity names must not be empty.
 * 
 * @author kunegis
 */
public final class EntityType
    implements Comparable <EntityType>
{
	/**
	 * The string name of the entity type. Typically all lowercase, with words separated by dash (if any). Should be a
	 * singular noun or noun group. As used e.g. in filenames. Examples: user, tag, movie, etc.
	 * <p>
	 * Interned. (Note: that doesn't make EntityTypes comparable with operator==.)
	 */
	private final String name;

	/**
	 * An entity type of the given name.
	 * 
	 * @param name Name of the entity type
	 */
	public EntityType(String name)
	{
		assert !name.isEmpty();

		this.name = name.intern();
	}

	/**
	 * @return the name of the entity type; interned
	 */
	public String getName()
	{
		return name;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof EntityType)) return false;
		final EntityType entityType = (EntityType) obj;
		return entityType.name == name;
	}

	@Override
	public int hashCode()
	{
		return name.hashCode();
	}

	@Override
	public String toString()
	{
		return name;
	}

	@Override
	public int compareTo(EntityType entityType)
	{
		return this.name.compareTo(entityType.name);
	}
}
