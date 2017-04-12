package de.dailab.recommender.dataset;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import de.dailab.recommender.matrix.Entry;
import de.dailab.recommender.matrix.template.MatrixFactory;

/**
 * A semantic network consisting of entities and relationships of any number of types.
 * <p>
 * Entities in the dataset are stored in {@code EntitySet} objects, one for each entity type. Relationships in the
 * dataset are stored in {@code RelationshipSet} objects, one for each relationship type.
 * <p>
 * The bulk of a dataset is stored in the matrices of relationship sets. Matrices can be held in memory or externally.
 * If matrices are held in memory, the memory requirement is as follows:
 *<p>
 *<center>r (i + j + 2 v) + n (T (3 P + 3 O + I) + M)</center>
 *<p>
 * where r is the number of relationships, i the size of left indexes, j the size of right indexes and v the size of
 * values. The size of indexes is determined by the integer type required to hold ID of entities. The size of the value
 * type is determined by the size of the datatype used to hold edge weights, counted as zero when edges are unweighted.
 * n is the number of nodes, and is generally less than the number of edges. P is the pointer size used by the JVM and M
 * is the size of all metadata. T is one for bipartite and two for unipartite entity sets. I is the size of int and O is
 * the overhead for an object on the heap, which is at least 8 bytes, but probably more in practice.
 * <p>
 * Examples for a 64 bit JVM:
 * <ul>
 * <li>An unweighted network of 100 users and 1000 contacts can be represented as byte indexes, and thus the memory
 * needed is 1000 (1B + 1B) + 100 (2 * 52B) = 2000B +10400B = 12400B = 12.4kB
 * <li>The Netflix rating matrix has 480000 users, 17000 movies and 100000000 ratings. Users can be represented with
 * 4-byte integers, movies with 2-byte integers and ratings with 1-byte integers. The memory needed is thus 100000000
 * (4B + 2B + 2 * 1B) + (480000 + 17000) (52B) = 826 MB
 * </ul>
 * These values cover only the data itself. The JVM will produce additional overhead due to memory management. These
 * values also only cover one dataset. Recommenders and predictors will use additional memory.
 * 
 * @author kunegis
 */
public class Dataset
{
	/**
	 * Creates an empty dataset. Single entity and relationship types can be added with the add*() methods.
	 */
	public Dataset()
	{}

	/**
	 * Create a memory-held copy of a dataset. The entity sets are the same but the relationship sets are copied into
	 * memory.
	 * 
	 * @param dataset The dataset to copy
	 * 
	 * @return A copy of the dataset with the same entity sets and new relationship sets
	 */
	public static Dataset copyDataset(Dataset dataset)
	{
		final Dataset ret = new Dataset();

		for (final EntitySet entitySet: dataset.entitySets.values())
			ret.addEntitySet(entitySet);

		for (final RelationshipSet relationshipSet: dataset.relationshipSets.values())
		{
			final RelationshipSet newRelationshipSet = new RelationshipSet(relationshipSet);
			newRelationshipSet.setMatrix(MatrixFactory.copyMatrix(relationshipSet.getMatrix()));
			ret.addRelationshipSet(newRelationshipSet);
		}

		return ret;
	}

	/**
	 * Add an entity set. If this dataset already contains an entity set of the same entity type, the previous entity
	 * set is removed.
	 * 
	 * @param entitySet the entity set to add
	 */
	public final void addEntitySet(EntitySet entitySet)
	{
		entitySets.put(entitySet.getType(), entitySet);
	}

	/**
	 * Add a relationship set. The two entity types must have been added before.
	 * 
	 * @param relationshipSet The relationship set to add
	 */
	public final void addRelationshipSet(RelationshipSet relationshipSet)
	{

		assert entitySets.get(relationshipSet.getSubject()) != null;
		assert entitySets.get(relationshipSet.getObject()) != null;

		relationshipSets.put(relationshipSet.getType(), relationshipSet);
	}

	/**
	 * @return The entity types of this dataset
	 */
	public final Set <EntityType> getEntityTypes()
	{
		return entitySets.keySet();
	}

	/**
	 * @return The entity sets for all entity types in this dataset
	 */
	public final Collection <EntitySet> getEntitySets()
	{
		return entitySets.values();
	}

	/**
	 * @return The relationship sets in this dataset
	 */
	public final Collection <RelationshipSet> getRelationshipSets()
	{
		return relationshipSets.values();
	}

	/**
	 * Return the relationship set of a given relationship type or NULL if it doesn't exist.
	 * 
	 * @param relationshipType The relationship type to get the relationship set of
	 * @return The relationship set of the given type, or NULL if the relationship type is not present in this dataset
	 */
	public final RelationshipSet getRelationshipSet(RelationshipType relationshipType)
	{
		return relationshipSets.get(relationshipType);
	}

	/**
	 * Return the relationship set of a given type. The relationship set must exist, or else an exception is thrown.
	 * 
	 * @param relationshipType The type of the relationship set to get
	 * @return The relationship set of the given type; not NULL
	 * 
	 * @throws NoSuchElementException when there is no relationship set of the given type
	 */
	public final RelationshipSet getExistingRelationshipSet(RelationshipType relationshipType)
	{
		final RelationshipSet ret = relationshipSets.get(relationshipType);

		if (ret == null)
		    throw new NoSuchElementException(String.format("Unknown relationship type \"%s\"", relationshipType));

		return ret;
	}

	/**
	 * @return The relationship types in this dataset
	 */
	public final Set <RelationshipType> getRelationshipTypes()
	{
		return relationshipSets.keySet();
	}

	/**
	 * The entity set of a given type.
	 * 
	 * @param entityType an entity type
	 * @return the corresponding entity set or NULL when no such entity set is present
	 */
	public final EntitySet getEntitySet(EntityType entityType)
	{
		return entitySets.get(entityType);
	}

	/**
	 * Get the entity set by entity type. The entity set must be present or an exception is thrown.
	 * 
	 * @param entityType The entity type to get the set of entities of
	 * @return The entity set; not NULL
	 * @throws NoSuchElementException There is no entity set of the given type
	 */
	public final EntitySet getExistingEntitySet(EntityType entityType)
	{
		final EntitySet ret = entitySets.get(entityType);
		if (ret == null)
		    throw new NoSuchElementException(String.format("Dataset has no entity type \"%s\"", entityType));
		return ret;
	}

	/**
	 * Return a specific metadata of a given entity.
	 * 
	 * @param entity An entity of this dataset
	 * @param metadataName The metadata to retrieve
	 * @return The metadata value. The type depends on the metadata name.
	 */
	public final Object getMetadata(Entity entity, MetadataName metadataName)
	{
		return getExistingEntitySet(entity.getType()).getMetadata(entity.getId(), metadataName);
	}

	/**
	 * Get all metadata of a given entity.
	 * 
	 * @param entity An entity of this dataset.
	 * @return an array of all metadata. The types depend on the metadata.
	 */
	public final Object[] getAllMetadata(Entity entity)
	{
		return entitySets.get(entity.getType()).getAllMetadata(entity.getId());
	}

	/**
	 * Get all neighbors of an entity.
	 * 
	 * @param entity The entity to get the neighbors of
	 * @return An iterator over all neighbors, not in any particular order
	 */
	public Iterable <DatasetEntry> getNeighbors(Entity entity)
	{
		final List <DatasetEntry> ret = new ArrayList <DatasetEntry>();

		for (final RelationshipSet relationshipSet: getRelationshipSets())
		{
			if (relationshipSet.getSubject().equals(entity.getType()))
			{
				for (final Entry entry: relationshipSet.getMatrix().row(entity.getId()))
				{
					final Entity neighbor = new Entity(relationshipSet.getObject(), entry.index);
					ret.add(new DatasetEntry(neighbor, relationshipSet.getType(), true, entry.value));
				}
			}
			if (relationshipSet.getObject().equals(entity.getType()))
			{
				for (final Entry entry: relationshipSet.getMatrix().col(entity.getId()))
				{
					final Entity neighbor = new Entity(relationshipSet.getSubject(), entry.index);
					ret.add(new DatasetEntry(neighbor, relationshipSet.getType(), false, entry.value));
				}
			}
		}

		return ret;
	}

	@Override
	public String toString()
	{
		String ret = "";

		for (final EntitySet entitySet: entitySets.values())
		{
			String metadataText = "";
			for (final MetadataName metadataName: entitySet.getMetadataNames())
			{
				if (!metadataText.equals("")) metadataText += " ";
				metadataText += metadataName.getValue();
			}
			ret += String.format("entity set %s [%s]:  %d entities\n", entitySet.getType(), metadataText, entitySet
			    .size());
		}

		for (final RelationshipSet relationshipSet: relationshipSets.values())
			ret += String.format("relationship set %s (%s -> %s):  %d relationships\n", relationshipSet.getType(),
			    relationshipSet.getSubject(), relationshipSet.getObject(), relationshipSet.getMatrix().nnz());

		return ret;
	}

	/**
	 * Get an entity by its metadata. Create the index if it is not present.
	 * 
	 * @param metadataName The metadata name to look up
	 * @param entityType The requested entity type
	 * @param object The metadata
	 * @return The entity
	 * @throws NoSuchElementException When no entity of the given type has the given object as metadata of the given
	 *         name
	 */
	public Entity getEntity(MetadataName metadataName, EntityType entityType, Object object)
	{
		MetadataIndex metadataIndex = metadataIndexes.get(metadataName);

		if (metadataIndex == null)
		{
			metadataIndex = new MetadataIndex();

			for (final EntitySet entitySet: entitySets.values())
			{
				final SingleMetadataIndex singleMetadataIndex = metadataIndex.getSingleIdCache(entitySet.getType());

				if (!entitySet.getMetadataNames().contains(metadataName)) continue;

				for (int i = 0; i < entitySet.size(); ++i)
				{
					final Object singleMetadata = entitySet.getMetadata(i, metadataName);
					if (singleMetadata != null) singleMetadataIndex.add(i, singleMetadata);
				}
			}

			metadataIndexes.put(metadataName, metadataIndex);
		}

		return metadataIndex.getEntity(entityType, object);
	}

	/**
	 * The relationships. The entities of this compound network are inferred from this collection.
	 */
	private final Map <RelationshipType, RelationshipSet> relationshipSets = new HashMap <RelationshipType, RelationshipSet>();

	/**
	 * The entity sets by their entity type.
	 */
	private final Map <EntityType, EntitySet> entitySets = new HashMap <EntityType, EntitySet>();

	private final Map <MetadataName, MetadataIndex> metadataIndexes = new HashMap <MetadataName, MetadataIndex>();
}
