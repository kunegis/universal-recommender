package de.dailab.recommender.semantic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.dailab.recommender.dataset.EntityType;

/**
 * An entity type in a semantic store.
 * 
 * @author kunegis
 */
public abstract class SemanticEntityType
{
	/**
	 * A semantic entity type of a given entity type and URL.
	 * 
	 * @param entityType The underlying entity type
	 * @param metadata The metadata for this entity type; NULL denotes the empty array
	 */
	public SemanticEntityType(EntityType entityType, SemanticMetadata metadata[])
	{
		this.entityType = entityType;
		this.metadata = metadata == null ? new ArrayList <SemanticMetadata>() : Arrays.asList(metadata);
	}

	/**
	 * The entity type corresponding to this semantic entity type.
	 */
	public final EntityType entityType;

	/**
	 * The metadata for this entity type. Not NULL.
	 */
	public final List <SemanticMetadata> metadata;

	/**
	 * Given an entity name, return the triple that can be used to define entities of this type.
	 * <p>
	 * The triple does not use RDF prefixes.
	 * 
	 * @param entityName The entity variable name, e.g. "artist"; a question mark is usually prepended
	 * @return The triple; usually terminated by period or semicolon
	 */
	public abstract String getTriple(String entityName);
}
