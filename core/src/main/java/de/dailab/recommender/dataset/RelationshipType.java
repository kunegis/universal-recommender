package de.dailab.recommender.dataset;

/**
 * A relationship type, represented by a string (their name).
 * <p>
 * Names are usually lowercase with words connected by dashes. Names are usually singular if they are nouns. They may
 * also be verbs. Some datasets however may give more complex names. Semantic stores for instance will use URIs as
 * relationship type names.
 * <p>
 * Examples are "rating", "has-seen", "movie-genre", "friend" and "foe".
 * <p>
 * Relationship types are equal when their name is equal.
 * 
 * @author kunegis
 */
public final class RelationshipType
{
	/**
	 * The string name of the relationship type. Typically all lowercase, with words separated by dash (if any). Should
	 * be a singular noun or noun group. As used e.g. in file names. Examples: "rating". May be equal to a entity type
	 * name when the entity type is connected to only this relationship type.
	 * <p>
	 * Interned.
	 */
	private final String name;

	/**
	 * A relationship type of the given name.
	 * 
	 * @param name Name of the relationship type; must not be empty
	 */
	public RelationshipType(String name)
	{
		assert name != null && name.length() != 0;
		this.name = name.intern();
	}

	/**
	 * @return The name of the relationship type; interned
	 */
	public String getName()
	{
		return name;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof RelationshipType)) return false;
		final RelationshipType relationshipType = (RelationshipType) obj;
		return relationshipType.name == name;
	}

	@Override
	public int hashCode()
	{
		return name.hashCode();
	}

	@Override
	public String toString()
	{
		return name;
	}
}
