package de.dailab.recommender.radar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.RelationshipSet;
import de.dailab.recommender.latent.LatentPredictorModel;
import de.dailab.recommender.recommend.RecommendationResult;
import de.dailab.recommender.recommend.RecommenderModel;
import de.dailab.recommender.recommendation.Recommendation;
import de.dailab.recommender.recommendation.RecommendationUtils;

/**
 * A radar is a set of point and lines that represent a small subset of a dataset, embedded into the plane.
 * <p>
 * A radar takes its coordinates from the left eigenvectors of a latent predictor model.
 * <p>
 * A radar is built by using a constructor from this class and passing a set of entities. The entities to display are
 * typically found by a recommender, but don't have to.
 * <p>
 * <b>History.</b> A user radar was included in the Community module, displaying users and features (words) on a radar
 * screen. This was integrated into PIA 2.
 * 
 * @author kunegis
 */
public class Radar
{
	private final List <Point> points;

	private final List <Line> lines;

	/**
	 * Compute a radar from a given entity list and a latent predictor model.
	 * <p>
	 * The resulting coordinates have no special scale; user interfaces will have to scale them individually. To switch
	 * off computation of coordinates, pass NULL as the latent predictor model. The radar is then essentially a graph.
	 * <p>
	 * Lines are chosen automatically from existing relationships between the given entities. In principle, this method
	 * could display entities that were not given, e.g. if they connect entities that were given. This is not done in
	 * this version. Therefore, entities of all types should be passed.
	 * 
	 * @param entities The entities to represent; none must be NULL
	 * @param dataset The underlying dataset; lines in the returned graph are read from this dataset
	 * @param latentPredictorModel The latent predictor model to use for computing coordinates; if NULL, no coordinates
	 *        are computed
	 * @param recommenderModel If non-NULL, use this recommender to find intermediary entities in addition to the
	 *        entities given
	 */
	public Radar(Entity entities[], Dataset dataset, LatentPredictorModel latentPredictorModel,
	    RecommenderModel recommenderModel)
	{
		/*
		 * Insert entities automatically
		 */
		if (recommenderModel != null)
		{
			final Set <EntityType> entityTypes = dataset.getEntityTypes();

			final EntityType entityTypeArray[] = new EntityType[entityTypes.size()];
			int i = 0;
			for (final EntityType entityType: entityTypes)
				entityTypeArray[i++] = entityType;

			final Map <Entity, Double> entityWeights = new HashMap <Entity, Double>();
			for (final Entity entity: entities)
				entityWeights.put(entity, 1.);
			final Iterator <Recommendation> iterator = recommenderModel.recommend(entityWeights, entityTypeArray);
			final List <Recommendation> recommendations = RecommendationUtils.read(iterator, 20);

			final Set <Entity> entitiesNew = new HashSet <Entity>();
			for (final Entity entity: entities)
				entitiesNew.add(entity);
			for (final Recommendation recommendation: recommendations)
				entitiesNew.add(recommendation.getEntity());
			entities = new Entity[entitiesNew.size()];
			i = 0;
			for (final Entity entity: entitiesNew)
				entities[i++] = entity;
		}

		/*
		 * Points
		 */
		points = new ArrayList <Point>();
		final Map <Entity, Point> entityPoints = new HashMap <Entity, Point>();
		for (final Entity entity: entities)
		{
			Point point;
			if (latentPredictorModel != null)
			{
				final double u[][] = latentPredictorModel.getU().get(entity.getType());
				point = new Point(entity, u[0][entity.getId()], u[1][entity.getId()]);
			}
			else
			{
				point = new Point(entity, 0, 0);
			}
			points.add(point);
			entityPoints.put(entity, point);
		}

		/*
		 * Lines
		 */
		lines = new ArrayList <Line>();
		final int n = entities.length;
		for (int i = 0; i < n; ++i)
			for (int j = 0; j < n; ++j)
			{
				if (i == j) continue;
				for (final RelationshipSet relationshipSet: dataset.getRelationshipSets())
				{
					if (relationshipSet.getSubject().equals(entities[i].getType())
					    && relationshipSet.getObject().equals(entities[j].getType()))
					{
						final double weight = relationshipSet.getMatrix().get(entities[i].getId(), entities[j].getId());
						if (weight == 0) continue;
						lines.add(new Line(entityPoints.get(entities[i]), entityPoints.get(entities[j]),
						    relationshipSet.getType(), weight));
					}
				}
			}
	}

	/**
	 * A radar with the given entities; no other entities are inserted automatically.
	 * 
	 * @param entities The entities to show
	 * @param dataset Lines are computed from this dataset
	 * @param latentPredictorModel The latent predictor model to use; may be NULL
	 */
	public Radar(Entity entities[], Dataset dataset, LatentPredictorModel latentPredictorModel)
	{
		this(entities, dataset, latentPredictorModel, null);
	}

	/**
	 * A radar with the given entities and no coordinates; no other entities are inserted automatically.
	 * 
	 * @param entities The entities to show
	 * @param dataset Lines are computed from this dataset
	 */
	public Radar(Entity entities[], Dataset dataset)
	{
		this(entities, dataset, null, null);
	}

	/**
	 * A radar built from a recommendation result object. The displayed entities are those that were visited during
	 * recommendations.
	 * <p>
	 * Entities must have been read from the recommendation result's entity iterator for visited entities to be present,
	 * and only those entities are shown.
	 * 
	 * @param recommendationResult The recommendation result to be visualized.
	 * @param dataset The dataset defining the network to visualize
	 */
	public Radar(RecommendationResult recommendationResult, Dataset dataset)
	{
		this(setArray(recommendationResult.getVisitedEntities()), dataset);
	}

	/**
	 * Return the points in this radar.
	 * 
	 * @return The points in this radar
	 */
	public List <Point> getPoints()
	{
		return points;
	}

	/**
	 * Return the lines in this radar
	 * 
	 * @return The lines in this radar
	 */
	public List <Line> getLines()
	{
		return lines;
	}

	@Override
	public String toString()
	{
		String ret = "";

		for (final Point point: points)
		{
			ret += String.format("%.2f %.2f %s\n", point.x, point.y, point.entity);
		}

		for (final Line line: lines)
		{
			ret += String.format("%s %f %s--%s\n", line.relationshipType, line.weight, line.subject.entity,
			    line.object.entity);
		}

		return ret;
	}

	/**
	 * Convert an entity set to an entity array.
	 * 
	 * @param entitySet An entity set
	 * @return An array with the entities from the given set, in an unspecified order
	 */
	private static Entity[] setArray(Set <Entity> entitySet)
	{
		final Entity ret[] = new Entity[entitySet.size()];
		int i = 0;
		for (final Entity entity: entitySet)
			ret[i++] = entity;
		return ret;
	}
}
