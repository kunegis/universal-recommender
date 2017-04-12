package de.dailab.recommender.constraint;

import java.util.Iterator;
import java.util.Map;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.RelationshipType;
import de.dailab.recommender.predict.PredictorModel;
import de.dailab.recommender.recommend.CompoundConstraint;
import de.dailab.recommender.recommend.RecommenderModel;
import de.dailab.recommender.recommend.SimpleRecommenderModel;
import de.dailab.recommender.recommendation.Recommendation;

/**
 * A recommender model that filters out entities already connected to the source entity.
 * 
 * @author kunegis
 */
public class PresentConstraintRecommenderModel
    extends SimpleRecommenderModel
{
	private final Dataset dataset;
	private final RecommenderModel recommenderModel;
	private final RelationshipType relationshipTypes[];

	/**
	 * A recommender model that filters out entities connected by the given relationship types to the source entity.
	 * 
	 * @param dataset The dataset to use for checking present relationships
	 * @param recommenderModel The recommender to wrap
	 * @param relationshipTypes The relationship types to follow for exclusion
	 */
	public PresentConstraintRecommenderModel(Dataset dataset, RecommenderModel recommenderModel,
	    RelationshipType relationshipTypes[])
	{
		this.dataset = dataset;
		this.recommenderModel = recommenderModel;
		this.relationshipTypes = relationshipTypes;
	}

	@Override
	public Iterator <Recommendation> recommend(Entity source, EntityType[] targetEntityTypes)
	{
		final Constraint constraint = new PresentConstraint(dataset, source, relationshipTypes);
		return new ConstraintRecommendationIterator(constraint, recommenderModel.recommend(source, targetEntityTypes));
	}

	@Override
	public Iterator <Recommendation> recommend(Map <Entity, Double> sources, EntityType[] targetEntityTypes)
	{
		final Constraint constraints[] = new Constraint[sources.size()];
		int i = 0;
		for (final Entity entity: sources.keySet())
		{
			constraints[i++] = new PresentConstraint(dataset, entity, relationshipTypes);
		}
		final Constraint constraint = new CompoundConstraint(constraints);
		return new ConstraintRecommendationIterator(constraint, recommenderModel.recommend(sources, targetEntityTypes));
	}

	@Override
	public void update()
	{
		recommenderModel.update();
	}

	@Override
	public PredictorModel getPredictorModel()
	    throws UnsupportedOperationException
	{
		return recommenderModel.getPredictorModel();
	}
}
