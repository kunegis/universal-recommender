package de.dailab.recommender.db;

/**
 * How to proceed with entities that occur in relationship tables but not in entity tables.
 */
public enum Mode
{

	/**
	 * (Default) Throw exceptions on database inconsistencies. All entities that occur in relationship tables must occur
	 * in the appropriate entity table.
	 */
	STRICT,

	/**
	 * Ignore database inconsistencies, potentially ignoring data. Relationships between nonexisting entities are
	 * ignored. (Nonexisting in the sense of not being present in the appropriate entity table.)
	 */
	IGNORE,

	/**
	 * Integrate inconsistent data as best as possible. Allow entities that occur in a relationship table but not in the
	 * corresponding entity table.
	 */
	INTEGRATE,

	/**
	 * Build a partial dataset: Don't load entities without metadata that don't occur in relationships. Entities don't
	 * have to be present in entity tables. This mode is fast because entity table are not read when they don't contain
	 * metadata.
	 */
	PARTIAL,

	;

	/**
	 * The default mode, STRICT.
	 */
	public static final Mode DEFAULT = STRICT;
}
