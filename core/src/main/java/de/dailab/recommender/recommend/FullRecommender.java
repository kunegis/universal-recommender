package de.dailab.recommender.recommend;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.predict.Predictor;
import de.dailab.recommender.predict.PredictorModel;
import de.dailab.recommender.recommendation.Recommendation;

/**
 * Compute recommendations based on computing predictions for all entities and picking the <i>k</i> best.
 * <p>
 * This is inefficient. Instead, use a latent predictor combined with a neighborhood finder.
 * 
 * @author kunegis
 */
public class FullRecommender
    extends AbstractRecommender
{
	/**
	 * A full recommender using a given predictor.
	 * 
	 * @param predictor The predictor to use.
	 */
	public FullRecommender(Predictor predictor)
	{
		this.predictor = predictor;
	}

	public RecommenderModel build(Dataset dataset, boolean update)
	{
		return new Model(predictor.build(dataset, update));
	}

	private class Model
	    extends SimpleRecommenderModel
	{
		public Model(PredictorModel predictorModel)
		{
			this.predictorModel = predictorModel;
		}

		private final PredictorModel predictorModel;

		@Override
		public Iterator <Recommendation> recommend(Map <Entity, Double> sources, EntityType targetEntityTypes[])
		{
			final LinkedList <Recommendation> ret = new LinkedList <Recommendation>();

			for (final EntityType entityType: targetEntityTypes)
			{
				final int n = predictorModel.getDataset().getEntitySet(entityType).size();

				for (int id = 0; id < n; ++id)
				{
					final Entity entity = new Entity(entityType, id);

					double score = 0;

					for (final Entry <Entity, Double> e: sources.entrySet())
						score += e.getValue() * predictorModel.predict(e.getKey(), entity);

					final ListIterator <Recommendation> i = ret.listIterator(0);
					double lastScore = Double.NaN;
					while (i.hasNext() && (lastScore = i.next().getScore()) > score);
					if (lastScore <= score) i.previous();
					i.add(new Recommendation(new Entity(entityType, id), score));
				}
			}

			return ret.iterator();
		}

		@Override
		public Iterator <Recommendation> recommend(Entity source, EntityType targetEntityTypes[])
		{
			final Map <Entity, Double> sources = new HashMap <Entity, Double>();
			sources.put(source, 1.);
			return recommend(sources, targetEntityTypes);
		}

		@Override
		public void update()
		{
			predictorModel.update();
		}

		@Override
		public PredictorModel getPredictorModel()
		    throws UnsupportedOperationException
		{
			return predictorModel;
		}
	}

	private final Predictor predictor;

	@Override
	public String toString()
	{
		return String.format("Full-%s", predictor);
	}
}
