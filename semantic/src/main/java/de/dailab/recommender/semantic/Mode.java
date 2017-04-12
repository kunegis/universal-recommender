package de.dailab.recommender.semantic;

import java.util.NoSuchElementException;

/**
 * How to handle entities mentioned in relationships that don't have their own RDF element.
 * <p>
 * All entities that appear in relationships in a Semantic Store must have a rdf:type declaration. This setting
 * determines what happens when an entity in a relationship has no declared type.
 * 
 * @author kunegis
 */
public enum Mode
{
	/**
	 * The constructor throws {@link NoSuchElementException}.
	 */
	FAIL,

	/**
	 * Relationships with unknown entities are ignored.
	 */
	IGNORE,
}
