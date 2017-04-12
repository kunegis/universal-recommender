package de.dailab.recommender.dataset;

import java.util.ArrayList;

import de.dailab.recommender.matrix.Matrix;

/**
 * A dataset consisting of only one relationship type. Unirelational datasets can be unipartite or bipartite.
 * 
 * @see SimpleUnipartiteDataset
 * 
 * @author kunegis
 */
public class UnirelationalDataset
    extends Dataset
{
	/**
	 * Create a unirelational dataset from a given relationship set.
	 * <p>
	 * The entity sets for the subject and object types are created automatically. (Only one entity set for unipartite
	 * relationship sets.) If the given relationship set has NULL subject or object entity types, default entity types
	 * are used.
	 * <p>
	 * The matrix must be already set with the correct size in the given relationship set.
	 * 
	 * @param relationshipSet A relationship set
	 */
	public UnirelationalDataset(RelationshipSet relationshipSet)
	{
		switch (relationshipSet.getRelationshipFormat())
		{
		default:
			throw new UnsupportedOperationException(String.format("Unsupported relationship format %s", relationshipSet
			    .getRelationshipFormat()));

		case SYM:
		case ASYM:
			assert relationshipSet.getSubject().equals(relationshipSet.getObject());

			final EntitySet entitySet = new EntitySet(relationshipSet.getSubject());
			if (relationshipSet.getMatrix().rows() != relationshipSet.getMatrix().cols())
			    throw new IllegalArgumentException();
			entitySet.setSize(relationshipSet.getMatrix().rows());
			entitySet.setMetadataNames(new ArrayList <MetadataName>(), new ArrayList <Object>());

			this.addEntitySet(entitySet);
			break;

		case BIP:
			final EntitySet entitySetSubject = new EntitySet(relationshipSet.getSubject());
			final EntitySet entitySetObject = new EntitySet(relationshipSet.getObject());

			entitySetSubject.setSize(relationshipSet.getMatrix().rows());
			entitySetObject.setSize(relationshipSet.getMatrix().cols());

			entitySetSubject.setMetadataNames(new ArrayList <MetadataName>(), new ArrayList <Object>());
			entitySetObject.setMetadataNames(new ArrayList <MetadataName>(), new ArrayList <Object>());

			this.addEntitySet(entitySetSubject);
			this.addEntitySet(entitySetObject);
			break;
		}

		this.addRelationshipSet(relationshipSet);
		uniqueRelationshipType = relationshipSet.getType();
	}

	/**
	 * @return The unique relationship type present in this dataset
	 */
	public RelationshipType getUniqueRelationshipType()
	{
		return uniqueRelationshipType;
	}

	/**
	 * @return The unique relationship set in this dataset
	 */
	public RelationshipSet getUniqueRelationshipSet()
	{
		return getRelationshipSet(getUniqueRelationshipType());
	}

	/**
	 * @return The matrix of the unique relationship set in this dataset
	 */
	public Matrix getMatrix()
	{
		return getUniqueRelationshipSet().getMatrix();
	}

	/**
	 * The unique relationship type in this dataset.
	 */
	private final RelationshipType uniqueRelationshipType;
}
