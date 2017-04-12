package de.dailab.recommender.recommend;

import java.util.HashSet;
import java.util.Set;

import de.dailab.recommender.constraint.Constraint;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.recommendation.Recommendation;

/**
 * A constraint that only allows a list of given entity types.
 * 
 * @author kunegis
 */
public class EntityTypeConstraint
    implements Constraint
{
	/**
	 * A constraint that only accepts entities of the given types.
	 * 
	 * @param entityTypes The entity types to accept
	 */
	public EntityTypeConstraint(EntityType... entityTypes)
	{
		for (final EntityType entityType: entityTypes)
			this.entityTypes.add(entityType);
	}

	@Override
	public boolean accept(Recommendation recommendation)
	{
		return entityTypes.contains(recommendation.getEntity().getType());
	}

	@Override
	public String toString()
	{
		String list = "";
		for (final EntityType entityType: entityTypes)
		{
			if (!list.isEmpty()) list += " ";
			list += entityType;
		}

		return String.format("[%s]", list);
	}

	private final Set <EntityType> entityTypes = new HashSet <EntityType>();
}
