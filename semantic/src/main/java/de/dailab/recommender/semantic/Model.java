package de.dailab.recommender.semantic;

/**
 * A semantic data model connected a semantic ontology to a recommender dataset structure.
 * 
 * @author kunegis
 */
public class Model
{
	/**
	 * A model with the given parameters.
	 * 
	 * @param semanticEntityTypes The semantic entity types
	 * @param semanticRelationshipTypes The semantic relationship types
	 * @param mode How deal with missing data
	 * @param sparqlDefinitions Additional SPARQL definitions
	 */
	public Model(SemanticEntityType semanticEntityTypes[], SemanticRelationshipType semanticRelationshipTypes[],
	    Mode mode, String sparqlDefinitions)
	{
		this.semanticEntityTypes = semanticEntityTypes;
		this.semanticRelationshipTypes = semanticRelationshipTypes;
		this.mode = mode;
		this.sparqlDefinitions = sparqlDefinitions;
	}

	/**
	 * The entity types.
	 */
	public final SemanticEntityType semanticEntityTypes[];

	/**
	 * The relationship types.
	 */
	public final SemanticRelationshipType semanticRelationshipTypes[];

	/**
	 * How to proceed with missing data.
	 */
	public final Mode mode;

	/**
	 * SPARQL definitions used in the definitions.
	 */
	public final String sparqlDefinitions;
}
