package de.dailab.recommender.constraint;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.RelationshipType;
import de.dailab.recommender.recommendation.Recommendation;

/**
 * A constraint that excludes entities known to be in relationship with a given entity.
 * 
 * @author kunegis
 * 
 */
public class PresentConstraint
    implements Constraint
{
	/**
	 * A present constraint based on given dataset, source and relationship types.
	 * 
	 * @param dataset The dataset
	 * @param source The source entity
	 * @param relationshipTypes Relationship types to consider
	 */
	public PresentConstraint(Dataset dataset, Entity source, RelationshipType relationshipTypes[])
	{
		this.dataset = dataset;
		this.source = source;
		this.relationshipTypes = relationshipTypes;
	}

	private final Dataset dataset;
	private final Entity source;
	private final RelationshipType relationshipTypes[];

	@Override
	public boolean accept(Recommendation recommendation)
	{
		for (final RelationshipType relationshipType: relationshipTypes)
			if (dataset.getRelationshipSet(relationshipType).getObject().equals(recommendation.getEntity().getType())
			    && dataset.getRelationshipSet(relationshipType).getMatrix().get(source.getId(),
			        recommendation.getEntity().getId()) != 0.) return false;

		return true;
	}
}
