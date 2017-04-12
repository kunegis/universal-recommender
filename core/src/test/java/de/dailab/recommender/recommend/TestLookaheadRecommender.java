package de.dailab.recommender.recommend;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.predict.PredictorModel;
import de.dailab.recommender.recommendation.Recommendation;
import de.dailab.recommender.recommendation.RecommendationUtils;

/**
 * Test the lookahead recommender.
 * 
 * @author kunegis
 */
public class TestLookaheadRecommender
{
	/**
	 * Test using a lookahead recommender on a recommender that returns less recommendations than the lookahead.
	 */
	@Test
	public void testShortList()
	{
		Recommender recommender = new Recommender()
		{
			@Override
			public RecommenderModel build(Dataset dataset, boolean update)
			{
				return build(dataset);
			}

			@Override
			public RecommenderModel build(Dataset dataset)
			{
				return new SimpleRecommenderModel()
				{
					@Override
					public void update()
					{
					/* nothing */
					}

					@Override
					public Iterator <Recommendation> recommend(Map <Entity, Double> sources,
					    EntityType[] targetEntityTypes)
					{
						return recommend(sources.keySet().iterator().next(), targetEntityTypes);
					}

					@Override
					public Iterator <Recommendation> recommend(Entity source, EntityType[] targetEntityTypes)
					{
						final List <Recommendation> recommendations = new ArrayList <Recommendation>();
						recommendations.add(new Recommendation(source, 1.));
						return recommendations.iterator();
					}

					@Override
					public PredictorModel getPredictorModel()
					    throws UnsupportedOperationException
					{
						throw new UnsupportedOperationException();
					}
				};
			}
		};

		recommender = new LookaheadRecommender(5, recommender);

		final Dataset dataset = new Dataset();
		final RecommenderModel recommenderModel = recommender.build(dataset);

		final EntityType entityType = new EntityType("asdas");

		final Iterator <Recommendation> iterator = recommenderModel.recommend(new Entity(entityType, 2),
		    new EntityType[]
		    { entityType });
		final List <Recommendation> recommendations = RecommendationUtils.read(iterator, 4);

		assert recommendations.size() == 1;
	}
}
