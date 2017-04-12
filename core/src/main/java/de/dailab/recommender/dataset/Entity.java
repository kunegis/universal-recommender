package de.dailab.recommender.dataset;

/**
 * An entity, a node in a semantic network. Entity objects consist of an entity type and an integer ID.
 * <p>
 * Entities are immutable.
 * 
 * @author kunegis
 */
public final class Entity
    implements Comparable <Entity>
{
	/**
	 * Create an entity of the given entity type and ID.
	 * 
	 * @param type The entity type
	 * @param id The entity ID
	 */
	public Entity(EntityType type, int id)
	{
		this.type = type;
		this.id = id;
	}

	/**
	 * @return The entity ID
	 */
	public EntityType getType()
	{
		return type;
	}

	/**
	 * @return The ID of this entity
	 */
	public int getId()
	{
		return id;
	}

	private final EntityType type;
	private final int id;

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof Entity)) return false;

		final Entity entity = (Entity) obj;

		return this.type.equals(entity.type) && this.id == entity.id;
	}

	@Override
	public int hashCode()
	{
		int ret = 29;
		ret *= 17;
		ret += id;
		ret *= 19;
		ret += type.hashCode();
		return ret;
	}

	@Override
	public String toString()
	{
		return String.format("%s-%d", type, id);
	}

	@Override
	public int compareTo(Entity entity)
	{
		final int cmp = this.type.compareTo(entity.type);
		if (cmp != 0) return cmp;
		if (this.id < entity.id) return -1;
		if (this.id > entity.id) return +1;
		return 0;
	}
}
