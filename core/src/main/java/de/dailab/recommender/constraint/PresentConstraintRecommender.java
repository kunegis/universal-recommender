package de.dailab.recommender.constraint;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.RelationshipType;
import de.dailab.recommender.recommend.AbstractRecommender;
import de.dailab.recommender.recommend.Recommender;
import de.dailab.recommender.recommend.RecommenderModel;

/**
 * A recommender that excludes entities already connected by given relationship types to the source entity.
 * 
 * @author kunegis
 */
public class PresentConstraintRecommender
    extends AbstractRecommender
{
	/**
	 * A recommender that filters out entities already present in a given relationship type.
	 * 
	 * @param relationshipTypes List of relationship types to use for filtering
	 * @param recommender The underlying recommender
	 */
	public PresentConstraintRecommender(RelationshipType relationshipTypes[], Recommender recommender)
	{
		this.relationshipTypes = relationshipTypes;
		this.recommender = recommender;
	}

	private final RelationshipType relationshipTypes[];
	private final Recommender recommender;

	@Override
	public RecommenderModel build(final Dataset dataset, boolean update)
	{
		final RecommenderModel recommenderModel = recommender.build(dataset, update);

		return new PresentConstraintRecommenderModel(dataset, recommenderModel, relationshipTypes);
	}

// @Override
// public RecommenderModel buildInitial(final Dataset dataset)
// {
// final RecommenderModel recommenderModel = recommender.buildInitial(dataset);
//
// return new PresentConstraintRecommenderModel(dataset, recommenderModel, relationshipTypes);
// }

	@Override
	public String toString()
	{
		String list = "";
		for (final RelationshipType relationshipType: relationshipTypes)
		{
			if (!list.isEmpty()) list += " ";
			list += relationshipType;
		}

		return String.format("Present(%s)-%s", list, recommender);
	}
}
