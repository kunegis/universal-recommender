package de.dailab.recommender.semantic;

import de.dailab.recommender.dataset.EntityType;

/**
 * An entity type where entities are actual RDF objects and that has a proper RDF type declared with rdf:type.
 * 
 * @author kunegis
 */
public class RdfEntityType
    extends SemanticEntityType
{
	/**
	 * An RDF entity type corresponding to a given RDF type.
	 * 
	 * @param entityType The entity type represented by the RDF type
	 * @param rdfType The RDF type corresponding to this entity type
	 * @param semanticMetadata The metadata of this entity type
	 */
	public RdfEntityType(EntityType entityType, RdfName rdfType, SemanticMetadata semanticMetadata[])
	{
		super(entityType, semanticMetadata);

		this.rdfType = rdfType;
	}

	/**
	 * An RDF entity type without metadata.
	 * 
	 * @param entityType The entity type
	 * @param rdfType The RDF type name
	 */
	public RdfEntityType(EntityType entityType, RdfName rdfType)
	{
		this(entityType, rdfType, new SemanticMetadata[] {});
	}

	/**
	 * An RDF entity type where the URI is taken from the entity type name.
	 * 
	 * @param entityType The underlying entity type; must be a URI or abbreviated name
	 * @param semanticMetadata The metadata
	 */
	public RdfEntityType(EntityType entityType, SemanticMetadata semanticMetadata[])
	{
		this(entityType, new RdfName(entityType.getName()), semanticMetadata);
	}

	private final RdfName rdfType;

	@Override
	public String getTriple(String entityName)
	{
		return String.format("?%s rdf:type %s.", entityName, rdfType);
	}
}
