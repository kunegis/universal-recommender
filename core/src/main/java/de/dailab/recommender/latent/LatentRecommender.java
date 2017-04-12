package de.dailab.recommender.latent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.neighborhood.DefaultNeighborhoodFinder;
import de.dailab.recommender.neighborhood.NeighborhoodFinder;
import de.dailab.recommender.neighborhood.NeighborhoodFinderModel;
import de.dailab.recommender.neighborhood.UnsupportedSimilarityException;
import de.dailab.recommender.predict.PredictorModel;
import de.dailab.recommender.predict.RelationshipTypePonderation;
import de.dailab.recommender.recommend.AggregateIterator;
import de.dailab.recommender.recommend.Recommender;
import de.dailab.recommender.recommend.WeightedPointRecommendationIterator;
import de.dailab.recommender.recommendation.LookaheadRecommendationIterator;
import de.dailab.recommender.recommendation.Recommendation;

/**
 * A recommender that works directly with a latent predictor model. A latent predictor has to be given, along with a
 * neighborhood finder.
 * <p>
 * The special structure of latent predictors is exploited to compute recommendations fast with a neighborhood finder.
 * <p>
 * A lookahead is automatically used.
 * 
 * @author kunegis
 */
public class LatentRecommender
    implements Recommender
{
	/**
	 * A latent recommender using a given latent predictor and neighborhood finder.
	 * <p>
	 * The neighborhood finder must support the similarity of the given latent predictor.
	 * 
	 * @param neighborhoodFinder the neighborhood finder to use
	 * @param latentPredictor A latent predictor
	 * @param lookahead The lookahead to use
	 */
	public LatentRecommender(NeighborhoodFinder neighborhoodFinder, LatentPredictor latentPredictor, int lookahead)
	{
		assert lookahead > 0;

		this.neighborhoodFinder = neighborhoodFinder;
		this.latentPredictor = latentPredictor;
		this.lookahead = lookahead;
	}

	/**
	 * A latent recommender with the default lookahead value.
	 * 
	 * @param neighborhoodFinder The neighborhood finder to use
	 * @param latentPredictor The latent predictor to use
	 */
	public LatentRecommender(NeighborhoodFinder neighborhoodFinder, LatentPredictor latentPredictor)
	{
		this(neighborhoodFinder, latentPredictor, LOOKAHEAD_DEFAULT);
	}

	/**
	 * A latent recommender using a given latent predictor and the default neighborhood finder.
	 * <p>
	 * The similarity of the given latent predictor must be supported by the default neighborhood finder.
	 * 
	 * @param latentPredictor The underlying latent predictor
	 */
	public LatentRecommender(LatentPredictor latentPredictor)
	{
		this(new DefaultNeighborhoodFinder(), latentPredictor);
	}

	/**
	 * A latent recommender using the default latent predictor of the given rank, and the default neighborhood finder.
	 * 
	 * @param rank The decomposition rank
	 */
	public LatentRecommender(int rank)
	{
		this(new DefaultNeighborhoodFinder(), new DefaultLatentPredictor(rank));
	}

	/**
	 * A latent recommender with the default latent predictor and the default neighborhood finder.
	 */
	public LatentRecommender()
	{
		this(new DefaultNeighborhoodFinder(), new DefaultLatentPredictor());
	}

	/**
	 * A latent recommender using a given relationship type ponderation.
	 * 
	 * @param relationshipTypePonderation The relationship type ponderation to use
	 */
	public LatentRecommender(RelationshipTypePonderation relationshipTypePonderation)
	{
		this(new DefaultLatentPredictor(relationshipTypePonderation));
	}

	public LatentRecommenderModel build(Dataset dataset, boolean update)
	{
		final LatentPredictorModel latentPredictorModel = latentPredictor.build(dataset, update);

		final Map <EntityType, NeighborhoodFinderModel> neighborhoodFinderModels = new HashMap <EntityType, NeighborhoodFinderModel>();

		try
		{
			for (final Entry <EntityType, double[][]> entry: latentPredictorModel.getV().entrySet())
			{
				neighborhoodFinderModels.put(entry.getKey(), neighborhoodFinder.build(latentPredictorModel
				    .getSimilarity(), latentPredictorModel.getLambda(), entry.getValue()));
			}
		}
		catch (final UnsupportedSimilarityException unsupportedSimilarityException)
		{
			throw new RuntimeException(unsupportedSimilarityException);
		}

		return new SimpleLatentRecommenderModel()
		{
			@Override
			public Iterator <Recommendation> recommend(Map <Entity, Double> sources, EntityType[] targetEntityTypes)
			{
				final double vector[] = new double[latentPredictorModel.getRank()];

				for (final Entry <Entity, Double> e: sources.entrySet())
				{
					final Entity entity = e.getKey();
					final double weight = e.getValue();
					assert weight != 0;
					final EntityType entityType = entity.getType();
					for (int i = 0; i < vector.length; ++i)
						vector[i] += weight * latentPredictorModel.getU().get(entityType)[i][entity.getId()];
				}

				final Collection <Iterator <Recommendation>> iterators = new ArrayList <Iterator <Recommendation>>();
				for (final EntityType entityType: targetEntityTypes)
				{
					final NeighborhoodFinderModel neighborhoodFinderModel = neighborhoodFinderModels.get(entityType);
					if (neighborhoodFinderModel == null)
					    throw new IllegalArgumentException(String.format("Invalid entity type %s", entityType));
					iterators.add(new WeightedPointRecommendationIterator(neighborhoodFinderModel
					    .findContinuous(vector), entityType));
				}
				return new LookaheadRecommendationIterator(lookahead, new AggregateIterator <Recommendation>(iterators));
			}

			@Override
			public void update()
			{
				latentPredictorModel.update();
				for (final Entry <EntityType, NeighborhoodFinderModel> entry: neighborhoodFinderModels.entrySet())
				{
					entry.getValue().update();
				}
			}

			@Override
			public LatentPredictorModel getLatentPredictorModel()
			{
				return latentPredictorModel;
			}

			@Override
			public Iterator <Recommendation> recommend(Entity source, EntityType[] targetEntityTypes)
			{
				final Map <Entity, Double> sources = new HashMap <Entity, Double>();
				sources.put(source, 1.);
				return recommend(sources, targetEntityTypes);
			}

			@Override
			public PredictorModel getPredictorModel()
			    throws UnsupportedOperationException
			{
				return latentPredictorModel;
			}
		};
	}

	@Override
	public LatentRecommenderModel build(Dataset dataset)
	{
		return build(dataset, true);
	}

	private final NeighborhoodFinder neighborhoodFinder;
	private final LatentPredictor latentPredictor;
	private final int lookahead;

	private final static int LOOKAHEAD_DEFAULT = 30;

	@Override
	public String toString()
	{
		return String.format("Latent(%d, %s)-%s", lookahead, neighborhoodFinder, latentPredictor);
	}
}
