package de.dailab.recommender.semantic;

import de.dailab.recommender.dataset.EntityType;

/**
 * An entity type that is represented by literals in RDF, and can therefore only be identified by looking at its
 * presence in relations.
 * 
 * @author kunegis
 */
public class LiteralEntityType
    extends SemanticEntityType
{
	/**
	 * A literal entity type. Literals appearing as arguments of the given RDF relationship type are considered to be of
	 * this entity type. There can be no metadata for this kind of entity type.
	 * 
	 * @param entityType The represented entity type
	 * @param rdfRelationshipType The RDF relationship type.
	 */
	public LiteralEntityType(EntityType entityType, RdfName rdfRelationshipType)
	{
		super(entityType, null);

		this.rdfRelationshipType = rdfRelationshipType;
	}

	/**
	 * The RDF relationship type that has entities of this type as arguments.
	 */
	public final RdfName rdfRelationshipType;

	@Override
	public String getTriple(String entityName)
	{
		return String.format("?x%s %s ?%s.", rdfRelationshipType.name.replaceAll("_", "__").replaceAll(":", "_"),
		    rdfRelationshipType, entityName);
	}
}
