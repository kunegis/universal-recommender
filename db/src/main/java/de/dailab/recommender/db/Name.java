package de.dailab.recommender.db;

/**
 * The name of a database table or column.
 * <p>
 * Wrapper around String that only allows safe characters.
 * <p>
 * Only the following characters are supported: All letters, underscore.
 * 
 * @author kunegis
 * 
 */
public class Name
{
	// XXX add other known-to-be-safe characters.

	/**
	 * Pattern of safe table and column names.
	 */
	private static final String PATTERN_SAFE = "([a-zA-Z_])+";

	/**
	 * Initialize with a string name
	 * 
	 * @param name the name. Must only contain safe characters.
	 * @throws IllegalArgumentException when the name contains unsafe characters
	 */
	public Name(String name)
	{
		if (!name.matches(PATTERN_SAFE))
		    throw new IllegalArgumentException(String.format("Name contains unsafe characters:  %s", name));
		this.name = name;
	}

	/**
	 * @return The name. Only contains safe characters.
	 */
	public String get()
	{
		return name;
	}

	/**
	 * The name, known to be safe.
	 */
	private final String name;

	@Override
	public String toString()
	{
		return name;
	}
}
