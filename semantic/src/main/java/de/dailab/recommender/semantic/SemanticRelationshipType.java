package de.dailab.recommender.semantic;

import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.RelationshipFormat;
import de.dailab.recommender.dataset.RelationshipType;
import de.dailab.recommender.dataset.WeightRange;

/**
 * A relationship type in a semantic store.
 * 
 * @author kunegis
 */
public class SemanticRelationshipType
{
	/**
	 * A semantic relationship type.
	 * 
	 * @param relationshipType The represented relationship type
	 * @param rdfRelationshipType The name of the RDF relationship type, e.g. mo:similar_to
	 * @param subject The subject entity type
	 * @param object The object entity type
	 * @param relationshipFormat The format of the relationship type
	 * @param weightRange The weight range
	 */
	public SemanticRelationshipType(RelationshipType relationshipType, RdfName rdfRelationshipType, EntityType subject,
	    EntityType object, RelationshipFormat relationshipFormat, WeightRange weightRange)
	{
		this.relationshipType = relationshipType;
		this.rdfRelationshipType = rdfRelationshipType;
		this.subject = subject;
		this.object = object;
		this.relationshipFormat = relationshipFormat;
		this.weightRange = weightRange;
	}

	/**
	 * The RDF name is inferred from the relationship type. The relationship type must be the RDF name (a URI).
	 * 
	 * @param relationshipType The relationship type; must be a URI
	 * @param subject The subject entity type
	 * @param object The object entity type
	 * @param relationshipFormat The relationship format
	 * @param weightRange The weight range
	 */
	public SemanticRelationshipType(RelationshipType relationshipType, EntityType subject, EntityType object,
	    RelationshipFormat relationshipFormat, WeightRange weightRange)
	{
		this(relationshipType, new RdfName(relationshipType.getName()), subject, object, relationshipFormat,
		    weightRange);
	}

	/**
	 * The relationship format and weight range are inferred. The relationship format is {@code RelationshipFormat.ASYM}
	 * when subject and object are equal, else {@code RelationshipFormat.BIP}. The weight range is always {@code
	 * WeightRange.UNWEIGHTED}.
	 * 
	 * @param relationshipType The relationship type
	 * @param rdfRelationshipType The RDF name URI
	 * @param subject The subject entity type
	 * @param object The object entity type
	 */
	public SemanticRelationshipType(RelationshipType relationshipType, RdfName rdfRelationshipType, EntityType subject,
	    EntityType object)
	{
		this(relationshipType, rdfRelationshipType, subject, object, subject.equals(object) ? RelationshipFormat.ASYM
		    : RelationshipFormat.BIP, WeightRange.UNWEIGHTED);
	}

	/**
	 * The relationship format, weight range and RDF name are inferred. The relationship format is {@code
	 * RelationshipFormat.ASYM} when subject and object are equal, else {@code RelationshipFormat.BIP}. The weight range
	 * is always {@code WeightRange.UNWEIGHTED}. The relationship type must be the RDF name (a URI).
	 * 
	 * @param relationshipType The relationship type; must be a URI
	 * @param subject Subject entity type
	 * @param object Object entity type
	 */
	public SemanticRelationshipType(RelationshipType relationshipType, EntityType subject, EntityType object)
	{
		this(relationshipType, new RdfName(relationshipType.getName()), subject, object);
	}

	/**
	 * The relationship type represented by this semantic relationship type.
	 */
	public final RelationshipType relationshipType;

	/**
	 * The RDF relationship that this represents, e.g. "mo:similar_to".
	 */
	public final RdfName rdfRelationshipType;

	/**
	 * The subject entity type.
	 */
	public final EntityType subject;

	/**
	 * The object entity type.
	 */
	public final EntityType object;

	/**
	 * The format of the relationship type.
	 */
	public final RelationshipFormat relationshipFormat;

	/**
	 * Weight of ranges.
	 */
	public final WeightRange weightRange;
}
