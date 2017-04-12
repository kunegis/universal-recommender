package de.dailab.recommender.pia;

import java.sql.Connection;
import java.sql.SQLException;

import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.MetadataName;
import de.dailab.recommender.dataset.RelationshipFormat;
import de.dailab.recommender.dataset.RelationshipType;
import de.dailab.recommender.dataset.WeightRange;
import de.dailab.recommender.db.Column;
import de.dailab.recommender.db.DbDataset;
import de.dailab.recommender.db.EntityTable;
import de.dailab.recommender.db.Mode;
import de.dailab.recommender.db.ModelException;
import de.dailab.recommender.db.RelationshipTable;
import de.dailab.recommender.db.Table;

/**
 * PIA 5 dataset.
 * 
 * @author kunegis
 * 
 */
public class PiaDataset
    extends DbDataset
{
	/**
	 * Build the PIA dataset using a given connection.
	 * 
	 * @param connection The connection to use
	 * @throws ModelException model error
	 * @throws SQLException database error
	 */
	public PiaDataset(Connection connection)
	    throws ModelException, SQLException
	{
		super(connection, ENTITY_TABLES, RELATIONSHIP_TABLES, Mode.PARTIAL);
	}

	/**
	 * A user.
	 */
	public static final EntityType ENTITY_TYPE_USER = new EntityType("user");

	/**
	 * An entity in the Sepia sense, i.e. something that can be returned by a search.
	 */
	public static final EntityType ENTITY_TYPE_ENTITY = new EntityType("entity");

	/**
	 * A rating.
	 */
	public static final RelationshipType RELATIONSHIP_TYPE_RATING = new RelationshipType("rating");

	/**
	 * The user table.
	 */
	public static final Table TABLE_USER = new Table("USER");

	/**
	 * The entity table.
	 */
	public static final Table TABLE_ENTITY = new Table("ENTITY");

	/**
	 * The user rating table.
	 */
	public static final Table TABLE_USER_RATING = new Table("USER_RATING");

	/**
	 * The user ID column.
	 */
	public static final Column COLUMN_USER_ID = new Column("USER_ID");

	/**
	 * The username column.
	 */
	public static final Column COLUMN_USER_NAME = new Column("USER_NAME");

	/**
	 * The ID column.
	 */
	public static final Column COLUMN_ID = new Column("ID");

	/**
	 * The entity ID column.
	 */
	public static final Column COLUMN_ENTITY_ID = new Column("ENTITY_ID");

	/**
	 * The name of a user.
	 */
	public static final MetadataName METADATA_NAME_USERNAME = new MetadataName("username");

	private final static EntityTable ENTITY_TABLES[] = new EntityTable[]
	{ new EntityTable(ENTITY_TYPE_USER, TABLE_USER, COLUMN_USER_ID, new MetadataName[]
	{ METADATA_NAME_USERNAME }, new Column[]
	{ COLUMN_USER_NAME }), new EntityTable(ENTITY_TYPE_ENTITY, TABLE_ENTITY, COLUMN_ID, null, null), };

	private final static RelationshipTable RELATIONSHIP_TABLES[] = new RelationshipTable[]
	{ new RelationshipTable(RELATIONSHIP_TYPE_RATING, ENTITY_TYPE_USER, ENTITY_TYPE_ENTITY, RelationshipFormat.BIP,
	    WeightRange.WEIGHTED, new Table("RATING"), COLUMN_USER_ID, COLUMN_ENTITY_ID, new Column("RATING_VALUE"),
	    new Column[] {}, "WHERE RATING.ID = USER_RATING.RATING_ID", TABLE_USER_RATING), };
}
