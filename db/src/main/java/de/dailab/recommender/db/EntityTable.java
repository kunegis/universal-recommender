package de.dailab.recommender.db;

import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.MetadataName;

/**
 * Information about a table that contains entity metadata.
 * 
 * This class hashes and compares according to the entity type.
 * 
 * The entities may be taken from a relationship table where entity IDs are included multiple times. These are read
 * using SQL DISTINCT, and should not be combined with metadata (because th metadata would not be unique.)
 * 
 * @author kunegis
 */
public class EntityTable
{
	/**
	 * Initialize an object with entity table information. metadataNames[] and columnsMetadata[] must have the same
	 * length.
	 * 
	 * @param type The entity type represented by this table
	 * @param table The database table name
	 * @param column The column name of the entity IDs
	 * @param metadataNames The metadata names corresponding to the columns.
	 * @param columnsMetadata The column names of entity metadata. NULL may be used instead of an empty list.
	 */
	public EntityTable(EntityType type, Table table, Column column, MetadataName metadataNames[],
	    Column columnsMetadata[])
	{
		this.type = type;
		this.table = table;
		this.column = column;
		this.metadataNames = metadataNames == null ? new MetadataName[] {} : metadataNames;
		this.columnsMetadata = columnsMetadata == null ? new Column[] {} : columnsMetadata;

		assert this.metadataNames.length == this.columnsMetadata.length;
	}

	/**
	 * The entity type contained in this table.
	 */
	public final EntityType type;

	/**
	 * Database table name
	 */
	public final Table table;

	/**
	 * Name of the database table column that contains the entity IDs.
	 */
	public final Column column;

	/**
	 * The metadata names. Not NULL.
	 */
	public final MetadataName metadataNames[];

	/**
	 * Names of the database table columns that contain the entity metadata. Not NULL.
	 */
	public final Column columnsMetadata[];

	@Override
	public boolean equals(Object obj)
	{
		return type.equals(((EntityTable) obj).type);
	}

	@Override
	public int hashCode()
	{
		return type.hashCode();
	}
}
