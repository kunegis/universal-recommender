package de.dailab.recommender.db;

import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.RelationshipFormat;
import de.dailab.recommender.dataset.RelationshipType;
import de.dailab.recommender.dataset.WeightRange;

/**
 * Information about a relationship table.
 * 
 * This class hashes and compares according to the relationship type.
 * 
 * @author kunegis
 * 
 */
public class RelationshipTable
{
	// XXX support non-numerical data using converter classes.

	/**
	 * Complete constructor.
	 * 
	 * @param type The relationship type represented by this table
	 * @param entityTypeSubject The entity type of subjects
	 * @param entityTypeObject The entity type of objects
	 * @param relationshipFormat format type of relationship type
	 * @param weightRange type of weights used
	 * @param table The database table name
	 * @param columnSubjectId The field name of subject IDs
	 * @param columnObjectId The field name of object IDs
	 * @param columnWeight The field name of the weight or NULL for unweighted relationships
	 * @param columnsMetadata The list of field names containing metadata. NULL may be used instead of the empty array.
	 * @param where optional where clause
	 * @param extraTable table for joining from
	 */
	public RelationshipTable(RelationshipType type, EntityType entityTypeSubject, EntityType entityTypeObject,
	    RelationshipFormat relationshipFormat, WeightRange weightRange, Table table, Column columnSubjectId,
	    Column columnObjectId, Column columnWeight, Column columnsMetadata[], String where, Table extraTable)
	{
		this.type = type;
		this.entityTypeSubject = entityTypeSubject;
		this.entityTypeObject = entityTypeObject;
		this.relationshipFormat = relationshipFormat;
		this.weightRange = weightRange;
		this.table = table;
		this.columnSubjectId = columnSubjectId;
		this.columnObjectId = columnObjectId;
		this.columnWeight = columnWeight;
		this.columnsMetadata = columnsMetadata;
		this.where = where;
		this.extraTable = extraTable;
	}

	/**
	 * Initialize an object with relationship table info
	 * 
	 * @param type The relationship type represented by this table
	 * @param entityTypeSubject The entity type of subjects
	 * @param entityTypeObject The entity type of objects
	 * @param relationshipFormat format of relationships
	 * @param weightRange allowed weight range
	 * @param table The database table name
	 * @param columnSubjectId The field name of subject IDs
	 * @param columnObjectId The field name of object IDs
	 * @param columnWeight The field name of the weight or NULL for unweighted relationships
	 * @param columnsMetadata The list of field names containing metadata. NULL may be used instead of the empty array.
	 */
	public RelationshipTable(RelationshipType type, EntityType entityTypeSubject, EntityType entityTypeObject,
	    RelationshipFormat relationshipFormat, WeightRange weightRange, Table table, Column columnSubjectId,
	    Column columnObjectId, Column columnWeight, Column columnsMetadata[])
	{
		this.type = type;
		this.entityTypeSubject = entityTypeSubject;
		this.entityTypeObject = entityTypeObject;
		this.relationshipFormat = relationshipFormat;
		this.weightRange = weightRange;
		this.table = table;
		this.columnSubjectId = columnSubjectId;
		this.columnObjectId = columnObjectId;
		this.columnWeight = columnWeight;
		this.columnsMetadata = columnsMetadata;
		this.where = null;
		this.extraTable = null;
	}

	/**
	 * Initialize an object with relationship table info
	 * 
	 * @param type The relationship type represented by this table
	 * @param entityTypeSubject The entity type of subjects
	 * @param entityTypeObject The entity type of objects
	 * @param relationshipFormat format of the relationship
	 * @param weightRange allowed weight range
	 * @param table The database table name
	 * @param columnSubjectId The field name of subject IDs
	 * @param columnObjectId The field name of object IDs
	 */
	public RelationshipTable(RelationshipType type, EntityType entityTypeSubject, EntityType entityTypeObject,
	    RelationshipFormat relationshipFormat, WeightRange weightRange, Table table, Column columnSubjectId,
	    Column columnObjectId)
	{
		this.type = type;
		this.entityTypeSubject = entityTypeSubject;
		this.entityTypeObject = entityTypeObject;
		this.relationshipFormat = relationshipFormat;
		this.weightRange = weightRange;
		this.table = table;
		this.columnSubjectId = columnSubjectId;
		this.columnObjectId = columnObjectId;
		this.columnWeight = null;
		this.columnsMetadata = null;
		this.where = null;
		this.extraTable = null;
	}

	/**
	 * Minimal constructor.
	 * 
	 * @param type The relationship type represented by this table
	 * @param entityTypeSubject The entity type of subjects
	 * @param entityTypeObject The entity type of objects
	 * @param table The database table name
	 * @param columnSubjectId The field name of subject IDs
	 * @param columnObjectId The field name of object IDs
	 */
	public RelationshipTable(RelationshipType type, EntityType entityTypeSubject, EntityType entityTypeObject,
	    Table table, Column columnSubjectId, Column columnObjectId)
	{
		this.type = type;
		this.entityTypeSubject = entityTypeSubject;
		this.entityTypeObject = entityTypeObject;
		this.relationshipFormat = entityTypeSubject == entityTypeObject ? RelationshipFormat.ASYM
		    : RelationshipFormat.BIP;
		this.weightRange = WeightRange.UNWEIGHTED;
		this.table = table;
		this.columnSubjectId = columnSubjectId;
		this.columnObjectId = columnObjectId;
		this.columnWeight = null;
		this.columnsMetadata = null;
		this.where = null;
		this.extraTable = null;
	}

	/**
	 * @param type The relationship type represented by this table
	 * @param entityTypeSubject The entity type of subjects
	 * @param entityTypeObject The entity type of objects
	 * @param table The database table name
	 * @param columnSubjectId The field name of subject IDs
	 * @param columnObjectId The field name of object IDs
	 * @param where optional where clause
	 */
	public RelationshipTable(RelationshipType type, EntityType entityTypeSubject, EntityType entityTypeObject,
	    Table table, Column columnSubjectId, Column columnObjectId, String where)
	{
		this.type = type;
		this.entityTypeSubject = entityTypeSubject;
		this.entityTypeObject = entityTypeObject;
		this.relationshipFormat = entityTypeSubject == entityTypeObject ? RelationshipFormat.ASYM
		    : RelationshipFormat.BIP;
		this.weightRange = WeightRange.UNWEIGHTED;
		this.table = table;
		this.columnSubjectId = columnSubjectId;
		this.columnObjectId = columnObjectId;
		this.columnWeight = null;
		this.columnsMetadata = null;
		this.where = where;
		this.extraTable = null;
	}

	// XXX make "where" not a string but something secure.

	/**
	 * The relationship type represented by the table.
	 */
	public final RelationshipType type;

	/**
	 * The subject entity type.
	 */
	public final EntityType entityTypeSubject;

	/**
	 * The object entity type.
	 */
	public final EntityType entityTypeObject;

	/**
	 * The relationship format.
	 */
	public final RelationshipFormat relationshipFormat;

	/**
	 * The weight range.
	 */
	public final WeightRange weightRange;

	/**
	 * The table name.
	 */
	public final Table table;

	/**
	 * The subject column.
	 */
	public final Column columnSubjectId;

	/**
	 * The object column.
	 */
	public final Column columnObjectId;

	/**
	 * Optional WHERE clause.
	 */
	public final String where;

	/**
	 * Optional extra table.
	 */
	public final Table extraTable;

	/**
	 * The table column containing relationship weights. May be NULL to denote an unweighted relationship type. If not
	 * NULL, the column must contain numerical data.
	 */
	public final Column columnWeight;

	/**
	 * Metadata columns.
	 */
	public final Column columnsMetadata[];

	@Override
	public boolean equals(Object obj)
	{
		return type.equals(((RelationshipTable) obj).type);
	}

	@Override
	public int hashCode()
	{
		return type.hashCode();
	}
}
