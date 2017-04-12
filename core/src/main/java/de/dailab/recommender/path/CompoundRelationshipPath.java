package de.dailab.recommender.path;

import de.dailab.recommender.dataset.RelationshipType;

/**
 * A compound consisting only of relationship paths. In other words, a path consisting of a fixed sequence of
 * relationship types that are traveled in sequence.
 * <p>
 * This is a thin wrapper around a CompoundPath containing RelationshipPath objects. Only forward links are supported.
 * 
 * @author kunegis
 */
public class CompoundRelationshipPath
    extends CompoundPath
{
	/**
	 * A path that follows relationships of the given types in the given order.
	 * 
	 * @param relationshipTypes The relationship types to follow
	 */
	public CompoundRelationshipPath(RelationshipType... relationshipTypes)
	{
		super(createPathArray(relationshipTypes));
	}

	private static Path[] createPathArray(RelationshipType relationshipTypes[])
	{
		final Path ret[] = new Path[relationshipTypes.length];

		for (int i = 0; i < relationshipTypes.length; ++i)
			ret[i] = new RelationshipPath(relationshipTypes[i]);

		return ret;
	}
}
