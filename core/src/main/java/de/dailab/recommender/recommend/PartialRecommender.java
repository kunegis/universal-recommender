package de.dailab.recommender.recommend;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.PartialDataset;
import de.dailab.recommender.dataset.RelationshipType;

/**
 * Apply a recommender to a partial dataset.
 * 
 * @author kunegis
 */
public class PartialRecommender
    extends AbstractRecommender
{
	/**
	 * Restrict the given recommender to the given relationship types
	 * 
	 * @param relationshipTypes List of relationship types to use
	 * @param recommender The recommender
	 */
	public PartialRecommender(RelationshipType relationshipTypes[], Recommender recommender)
	{
		this.relationshipTypes = relationshipTypes;
		this.recommender = recommender;
	}

	private final RelationshipType relationshipTypes[];
	private final Recommender recommender;

	@Override
	public RecommenderModel build(Dataset dataset, boolean update)
	{
		final Dataset partialDataset = new PartialDataset(dataset, relationshipTypes);
		return recommender.build(partialDataset, update);
	}

	@Override
	public String toString()
	{
		String list = "";
		for (final RelationshipType relationshipType: relationshipTypes)
		{
			if (!list.isEmpty()) list += " ";
			list += relationshipType;
		}

		return String.format("Partial(%s)-%s", list, recommender);
	}
}
