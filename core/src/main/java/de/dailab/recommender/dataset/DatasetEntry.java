package de.dailab.recommender.dataset;

/**
 * An entry returned by Dataset.getNeighbors(). This consists of an entity, a relationship type and a weight.
 * <p>
 * Dataset entries compare and hash by entity and relationship type.
 * 
 * @author kunegis
 */
public class DatasetEntry
{
	/**
	 * A dataset entry object with the given values.
	 * 
	 * @param entity The entity
	 * @param relationshipType The relationship type through which the entity is connected
	 * @param forward Whether the edge was followed in a forward direction
	 * @param weight The weight of the edge
	 */
	public DatasetEntry(Entity entity, RelationshipType relationshipType, boolean forward, double weight)
	{
		this.entity = entity;
		this.relationshipType = relationshipType;
		this.forward = forward;
		this.weight = weight;
	}

	/**
	 * Set only the field considered in comparisons.
	 * 
	 * @param entity The entity
	 * @param relationshipType The relationship type
	 */
	public DatasetEntry(Entity entity, RelationshipType relationshipType)
	{
		this.entity = entity;
		this.relationshipType = relationshipType;
		this.forward = true;
		this.weight = 1;
	}

	/**
	 * The connected entity.
	 */
	public final Entity entity;

	/**
	 * The connecting relationship type.
	 */
	public final RelationshipType relationshipType;

	/**
	 * Whether the edge connects in a forward direction.
	 */
	public final boolean forward;

	/**
	 * The edge weight.
	 */
	public final double weight;

	/**
	 * {@inheritDoc}
	 * 
	 * Only the entity and the relationship type is used.
	 */
	@Override
	public boolean equals(Object object)
	{
		final DatasetEntry datasetEntry = (DatasetEntry) object;

		return this.entity.equals(datasetEntry.entity) && this.relationshipType.equals(datasetEntry.relationshipType);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Only the entity and the relationship type is used.
	 */
	@Override
	public int hashCode()
	{
		return 5 * (entity.hashCode() + 3 * (relationshipType.hashCode()));
	}

	@Override
	public String toString()
	{
		return String.format("%s [%s:%.3g] %s", forward ? "\u2192" : "\u2190", relationshipType, weight, entity);
	}
}
