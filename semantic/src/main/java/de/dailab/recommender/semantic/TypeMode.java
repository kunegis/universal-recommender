package de.dailab.recommender.semantic;

/**
 * Determines how relationships are queried using SPARQL.
 * 
 * @author kunegis
 */
public enum TypeMode
{
	/**
	 * Load all relationships of a given RDF predicate, without specifying the RDF type of the entities. This is faster
	 * but load relationships that are immediately discarded after reading. This is the default. If the same RDF
	 * predicate connects different entity type pairs, relationships of this type will be loaded multiple times in this
	 * mode.
	 */
	UNTYPED,

	/**
	 * The RDF type of entities is specified in the SPARQL query. Each relationship will only be loaded once. The SPARQL
	 * query is typically slower in this mode.
	 */
	TYPED,

	;

	/**
	 * The default type mode, {@code UNTYPED}.
	 */
	public static final TypeMode DEFAULT = UNTYPED;
}
