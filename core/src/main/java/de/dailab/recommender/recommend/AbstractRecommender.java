package de.dailab.recommender.recommend;

import de.dailab.recommender.dataset.Dataset;

/**
 * A recommender where build(Dataset) calls build(Dataset, true).
 * 
 * @author kunegis
 */
public abstract class AbstractRecommender
    implements Recommender
{
	@Override
	public RecommenderModel build(Dataset dataset)
	{
		return build(dataset, true);
	}
}
