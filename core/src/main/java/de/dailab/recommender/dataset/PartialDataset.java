package de.dailab.recommender.dataset;

/**
 * A partial dataset. A view of another dataset that retains a subset of the relationship types.
 * <p>
 * The new partial dataset is backed by the present relationship and entity sets.
 * 
 * @author kunegis
 */
public class PartialDataset
    extends Dataset
{
	/**
	 * A view of a dataset retaining only the given relationship types. Retains the entity types of the given
	 * relationship types. The resulting partial dataset is backed by the given dataset.
	 * 
	 * @param dataset The underlying dataset
	 * @param relationshipTypes The relationship types to retain. All relationship types must be present in the dataset.
	 */
	public PartialDataset(Dataset dataset, RelationshipType relationshipTypes[])
	{
		for (final RelationshipType relationshipType: relationshipTypes)
		{
			final RelationshipSet relationshipSet = dataset.getRelationshipSet(relationshipType);
			assert relationshipSet != null;
			this.addEntitySet(dataset.getEntitySet(relationshipSet.getSubject()));
			this.addEntitySet(dataset.getEntitySet(relationshipSet.getObject()));
			this.addRelationshipSet(relationshipSet);
		}
	}
}
