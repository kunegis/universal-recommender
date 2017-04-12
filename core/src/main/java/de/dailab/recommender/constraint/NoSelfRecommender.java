package de.dailab.recommender.constraint;



import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.recommend.AbstractRecommender;
import de.dailab.recommender.recommend.Recommender;
import de.dailab.recommender.recommend.RecommenderModel;

/**
 * A recommender wrapper that filters out the source of the recommendation. Only useful for user&ndash;user or
 * item&ndash;item recommendations.
 * 
 * @author kunegis
 */
public class NoSelfRecommender
    extends AbstractRecommender
{
	/**
	 * A recommender that filters out the source entities of a given recommender.
	 * 
	 * @param recommender The underlying recommender
	 */
	public NoSelfRecommender(Recommender recommender)
	{
		this.recommender = recommender;
	}

	private final Recommender recommender;

	@Override
	public RecommenderModel build(Dataset dataset, boolean update)
	{
		final RecommenderModel recommenderModel = recommender.build(dataset, update);

		return new NoSelfRecommenderModel(recommenderModel);
	}
}
