package de.dailab.recommender.dataset;

import java.util.HashMap;

/**
 * A weighted set of entities, i.e. a map from Entity to Double.
 * 
 * @author kunegis
 */
public class EntityPonderation
    extends HashMap <Entity, Double>
{
	/**
	 * An entity ponderation containing a given list of entities with unit weights.
	 * 
	 * @param entities The entities to be in this map
	 */
	public EntityPonderation(Entity... entities)
	{
		for (final Entity entity: entities)
			put(entity, 1.);
	}
}
