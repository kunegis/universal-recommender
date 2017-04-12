package de.dailab.recommender.ensemble;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.predict.PredictorModel;
import de.dailab.recommender.recommend.AbstractRecommender;
import de.dailab.recommender.recommend.MergeIterator;
import de.dailab.recommender.recommend.Recommender;
import de.dailab.recommender.recommend.RecommenderModel;
import de.dailab.recommender.recommend.SimpleRecommenderModel;
import de.dailab.recommender.recommendation.LookaheadRecommendationIterator;
import de.dailab.recommender.recommendation.Recommendation;

/**
 * An ensemble of recommenders.
 * <p>
 * Return recommendations aggregated from a given list of recommenders.
 * 
 * @author kunegis
 */
public class EnsembleRecommender
    extends AbstractRecommender
{
	/**
	 * The ensemble of the given recommenders.
	 * 
	 * @param recommenders The list of recommenders to combine
	 */
	public EnsembleRecommender(Recommender... recommenders)
	{
		this.recommenders = new ArrayList <Recommender>(recommenders.length);
		for (final Recommender recommender: recommenders)
			this.recommenders.add(recommender);
	}

	private final List <Recommender> recommenders;

	@Override
	public RecommenderModel build(Dataset dataset, boolean update)
	{
		final Map <Recommender, RecommenderModel> recommenderModels = new HashMap <Recommender, RecommenderModel>();

		for (final Recommender recommender: recommenders)
		{
			recommenderModels.put(recommender, recommender.build(dataset, update));
		}

		return new EnsembleRecommenderModel(recommenderModels);
	}

	@Override
	public String toString()
	{
		String ret = "";

		for (final Recommender recommender: recommenders)
		{
			if (!ret.isEmpty()) ret += " ";
			ret += recommender;
		}

		return String.format("Ensemble(%s)", ret);
	}

	private final class EnsembleRecommenderModel
	    extends SimpleRecommenderModel
	{
		private final Map <Recommender, RecommenderModel> recommenderModels;

		private EnsembleRecommenderModel(Map <Recommender, RecommenderModel> recommenderModels)
		{
			this.recommenderModels = recommenderModels;
		}

		@Override
		public void update()
		{
			for (final RecommenderModel recommenderModel: recommenderModels.values())
				recommenderModel.update();
		}

		@Override
		public Iterator <Recommendation> recommend(Map <Entity, Double> sources, EntityType[] targetEntityTypes)
		{
			final List <Iterator <Recommendation>> iterators = new ArrayList <Iterator <Recommendation>>();

			for (final RecommenderModel recommenderModel: recommenderModels.values())
			{
				iterators.add(recommenderModel.recommend(sources, targetEntityTypes));
			}

			return new LookaheadRecommendationIterator(1, new MergeIterator <Recommendation>(iterators));
		}

		@Override
		public Iterator <Recommendation> recommend(Entity source, EntityType[] targetEntityTypes)
		{
			final List <Iterator <Recommendation>> iterators = new ArrayList <Iterator <Recommendation>>();

			for (final RecommenderModel recommenderModel: recommenderModels.values())
			{
				iterators.add(recommenderModel.recommend(source, targetEntityTypes));
			}

			return new LookaheadRecommendationIterator(1, new MergeIterator <Recommendation>(iterators));
		}

		@Override
		public PredictorModel getPredictorModel()
		    throws UnsupportedOperationException
		{
			throw new UnsupportedOperationException();
		}
	}

}
