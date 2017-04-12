package de.dailab.recommender.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntitySet;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.MetadataIndex;
import de.dailab.recommender.dataset.MetadataName;
import de.dailab.recommender.dataset.RelationshipSet;
import de.dailab.recommender.dataset.SingleMetadataIndex;
import de.dailab.recommender.matrix.Matrix;
import de.dailab.recommender.matrix.template.MatrixFactory;

/**
 * A dataset read from a database.
 * <p>
 * Entities and relationships correspond to tables.
 * 
 * @author kunegis
 */
public class DbDataset
    extends Dataset
{
	/**
	 * Metadata type for orphan entity types, i.e. those that are only mentioned in a relationship table and thus have
	 * no other metadata.
	 */
	public final MetadataName METADATA_DBID = new MetadataName("dbid");

	/**
	 * Read the data from the tables using a given connection.
	 * <p>
	 * Not all entities have to be mentioned; those that are not mentioned are only attached to one relationship type
	 * and cannot have metadata (i.e. orphan entity types).
	 * 
	 * @param connection The database connection to use. The connection is only used in the constructor.
	 * @param entityTables Set of tables to extract entities from
	 * @param relationshipTables Set of tables to extract relationships from
	 * @param mode How to deal with database inconsistencies
	 * 
	 * @throws SQLException while reading the database
	 * @throws ModelException for invalid data models in the database (e.g. two tables for one entity type)
	 */
	public DbDataset(Connection connection, EntityTable entityTables[], RelationshipTable relationshipTables[],
	    Mode mode)
	    throws SQLException, ModelException
	{
		/*
		 * Generate IDs
		 */

		/* Entity tables */

		/** Entity types that have their own table */
		final Set <EntityType> explicitEntityTypes = new HashSet <EntityType>();
		for (final EntityTable entityTable: entityTables)
		{
			if (mode != Mode.PARTIAL || entityTable.metadataNames.length != 0)
			{
				addIds(connection, entityTable.table, entityTable.column, entityTable.type, null, null);
				explicitEntityTypes.add(entityTable.type);
			}
		}

		/* Relationship tables */
		for (final RelationshipTable relationshipTable: relationshipTables)
		{
			if (mode == Mode.INTEGRATE || mode == Mode.PARTIAL
			    || !explicitEntityTypes.contains(relationshipTable.entityTypeSubject))
			    addIds(connection, relationshipTable.table, relationshipTable.columnSubjectId,
			        relationshipTable.entityTypeSubject, relationshipTable.extraTable, relationshipTable.where);
			if (mode == Mode.INTEGRATE || mode == Mode.PARTIAL
			    || !explicitEntityTypes.contains(relationshipTable.entityTypeObject))
			    addIds(connection, relationshipTable.table, relationshipTable.columnObjectId,
			        relationshipTable.entityTypeObject, relationshipTable.extraTable, relationshipTable.where);
		}

		/*
		 * Add entity metadata
		 */
		for (final EntityTable entityTable: entityTables)
		{
			if (this.getEntitySet(entityTable.type) != null)
			    throw new ModelException(String.format("Duplicate entity type %s", entityTable.type));

			final EntitySet entitySet = new EntitySet(entityTable.type);
			this.addEntitySet(entitySet);
			entitySet.setSize(idCache.getSingleIdCache(entityTable.type).size());

			final SingleMetadataIndex singleIdCache = idCache.getSingleIdCache(entityTable.type);

			/*
			 * Metadata
			 */

			if (entityTable.metadataNames.length > 0)
			{
				String names = "";
				for (final Column column: entityTable.columnsMetadata)
					names += ", " + column;
				final PreparedStatement preparedStatement = connection.prepareStatement(String.format(
				    "SELECT %s%s FROM %s", entityTable.column, names, entityTable.table));

				final ResultSet resultSet = preparedStatement.executeQuery();
				boolean init = false;
				while (resultSet.next())
				{
					if (!init)
					{
						final List <MetadataName> metadataNames = new ArrayList <MetadataName>();
						final List <Object> metadataTypes = new ArrayList <Object>();
						int i = 0;
						for (final MetadataName metadataName: entityTable.metadataNames)
						{
							metadataNames.add(metadataName);

							Object object = resultSet.getObject(2 + i++);
							object = decode(object);
							metadataTypes.add(object);
						}
						entitySet.setMetadataNames(metadataNames, metadataTypes);
						init = true;
					}

					final Object object = resultSet.getObject(1);
					final int entityId = singleIdCache.getId(object);
					final Object metadataValues[] = new Object[entityTable.columnsMetadata.length];
					for (int i = 0; i < entityTable.columnsMetadata.length; ++i)
						metadataValues[i] = decode(resultSet.getObject(2 + i));
					entitySet.setMetadataValues(entityId, Arrays.asList(metadataValues));
				}
			}
		}

		for (final RelationshipTable relationshipTable: relationshipTables)
		{
			if (this.getRelationshipSet(relationshipTable.type) != null)
			    throw new ModelException(String.format("Duplicate relationship type %s", relationshipTable.type));

			/* Add orphan entity types */
			addOrphanEntityType(relationshipTable.entityTypeSubject);
			addOrphanEntityType(relationshipTable.entityTypeObject);

			final RelationshipSet relationshipSet = new RelationshipSet(relationshipTable.type,
			    relationshipTable.entityTypeSubject, relationshipTable.entityTypeObject,
			    relationshipTable.relationshipFormat, relationshipTable.weightRange);

			final SingleMetadataIndex subjectCache = idCache.getSingleIdCache(relationshipTable.entityTypeSubject);
			final SingleMetadataIndex objectCache = idCache.getSingleIdCache(relationshipTable.entityTypeObject);

			final int m = subjectCache.size();
			final int n = objectCache.size();

			Matrix matrix;
			if (relationshipTable.columnWeight == null)
				matrix = MatrixFactory.newMemoryMatrix(m, n, boolean.class.getSimpleName());
			else
				matrix = MatrixFactory.newMemoryMatrix(m, n);

			PreparedStatement preparedStatement = null;
			ResultSet resultSet = null;

			try
			{
				preparedStatement = connection.prepareStatement(String.format("SELECT %s, %s%s FROM %s%s%s",
				    relationshipTable.columnSubjectId, relationshipTable.columnObjectId,
				    relationshipTable.columnWeight == null ? "" : ", " + relationshipTable.columnWeight,
				    relationshipTable.table, relationshipTable.extraTable == null ? "" : ", "
				        + relationshipTable.extraTable, relationshipTable.where == null ? "" : " "
				        + relationshipTable.where));
				resultSet = preparedStatement.executeQuery();

				while (resultSet.next())
				{
					final Object subject = resultSet.getObject(1);
					final Object object = resultSet.getObject(2);

					int i, j;
					try
					{
						i = subjectCache.getId(subject);
						j = objectCache.getId(object);
					}
					catch (final NoSuchElementException noSuchElementException)
					{
						if (mode == Mode.IGNORE) continue;
						throw new ModelException(String.format("Inconsistent database in relationship table %s",
						    relationshipTable.type), noSuchElementException);
					}

					double weight = 1.;
					if (relationshipTable.columnWeight != null)
					{
						weight = resultSet.getDouble(3);
						assert !Double.isNaN(weight) && !Double.isInfinite(weight);
					}

					matrix.set(i, j, weight);
				}
			}
			finally
			{
				if (resultSet != null) resultSet.close();
				if (preparedStatement != null) preparedStatement.close();
			}
			relationshipSet.setMatrix(matrix);

			this.addRelationshipSet(relationshipSet);
		}

	}

	/**
	 * Build database using default mode (strict).
	 * 
	 * @param connection The connection to use
	 * @param entityTables information about entity tables
	 * @param relationshipTables information about relationship tables
	 * @throws SQLException on reading the database
	 * @throws ModelException inconsistency in the model
	 */
	public DbDataset(Connection connection, EntityTable entityTables[], RelationshipTable relationshipTables[])
	    throws SQLException, ModelException
	{
		this(connection, entityTables, relationshipTables, Mode.DEFAULT);
	}

	/**
	 * The entity representing a specific database object.
	 * 
	 * @param entityType Entity type
	 * @param object ID in database
	 * @return the entity.
	 * @throws NoSuchElementException when there is no such element. May happen in partial mode when the corresponding
	 *         entity does not occur in any relationship.
	 */
	public Entity getEntityByObject(EntityType entityType, Object object)
	{
		final int id = idCache.getSingleIdCache(entityType).getId(object);
		return new Entity(entityType, id);
	}

	/**
	 * Given an entity, return the associated database object.
	 * 
	 * @param entity an entity
	 * @return the database object
	 */
	public Object getObjectByEntity(Entity entity)
	{
		return idCache.getObject(entity);
	}

	/**
	 * Mapping of database objects against entity IDs. In partial mode, only the entities are presented that were loaded
	 * (i.e., that occur in a relationship).
	 */
	private final MetadataIndex idCache = new MetadataIndex();

	/**
	 * Add the entities from a given database table column.
	 */
	private void addIds(Connection connection, Table table, Column column, EntityType entityType, Table extraTable,
	    String where)
	    throws SQLException
	{
		final SingleMetadataIndex singleIdCache = idCache.getSingleIdCache(entityType);

		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try
		{
			preparedStatement = connection.prepareStatement(String.format("SELECT DISTINCT %s FROM %s%s%s", column,
			    table, extraTable == null ? "" : ", " + extraTable, where == null ? "" : " " + where));
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next())
			{
				final Object object = resultSet.getObject(1);
				singleIdCache.add(object);
			}
		}
		finally
		{
			if (resultSet != null) resultSet.close();
			if (preparedStatement != null) preparedStatement.close();
		}
	}

	/**
	 * If a given entity type does not have a corresponding entity set, add an entity set with metadata based on the ID
	 * cache.
	 */
	private void addOrphanEntityType(EntityType entityType)
	{
		if (this.getEntitySet(entityType) != null) return;

		final SingleMetadataIndex singleIdCache = idCache.getSingleIdCache(entityType);
		assert singleIdCache != null;

		final EntitySet entitySet = new EntitySet(entityType);
		entitySet.setSize(singleIdCache.size());
		final List <MetadataName> metadataTypes = new ArrayList <MetadataName>();
		metadataTypes.add(METADATA_DBID);
		final List <Object> sampleMetadata = new ArrayList <Object>();
		sampleMetadata.add(singleIdCache.getObjects().get(0));
		entitySet.setMetadataNames(metadataTypes, sampleMetadata);

		addEntitySet(entitySet);
	}

	/**
	 * Given a database object, return the corresponding in-memory object. Used for metadata column.
	 * 
	 * @param object Database object
	 * @return In-memory object
	 */
	private static Object decode(Object object)
	{
		if (object instanceof Timestamp)
		{
			final Timestamp timestamp = (Timestamp) object;
			object = new Date(timestamp.getTime());
		}
		return object;
	}
}
