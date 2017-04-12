package de.dailab.recommender.graph;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntitySet;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.RelationshipSet;
import de.dailab.recommender.dataset.UnirelationalDataset;
import de.dailab.recommender.graph.unirelationaldatasets.SlashdotZooDataset;
import de.dailab.recommender.latent.LatentRecommender;
import de.dailab.recommender.recommend.Recommender;
import de.dailab.recommender.recommend.RecommenderModel;
import de.dailab.recommender.recommendation.Recommendation;
import de.dailab.recommender.recommendation.RecommendationUtils;
import de.dailab.recommender.text.TextSyntaxException;

/**
 * Test updating a recommender model after an entity was added to a dataset.
 * 
 * @author kunegis
 */
public class TestNewEntity
{
	/**
	 * Load dataset, build a recommender, add an entity to the dataset, add edges to that entity in the dataset and
	 * update the recommender model.
	 * 
	 * @throws TextSyntaxException Syntax error
	 * @throws IOException IO error
	 */
	@Test
	public void testNewEntity()
	    throws IOException, TextSyntaxException
	{
		final Recommender recommender = new LatentRecommender();

		final UnirelationalDataset unirelationalDataset = new SlashdotZooDataset();

		final RecommenderModel recommenderModel = recommender.build(unirelationalDataset);

		final RelationshipSet relationshipSet = unirelationalDataset.getUniqueRelationshipSet();
		final EntitySet entitySet = unirelationalDataset.getEntitySet(relationshipSet.getSubject());

		final Entity oldEntity = new Entity(entitySet.getType(), 17);

		recommend(recommenderModel, oldEntity, unirelationalDataset);

// /*
// * Add entity
// */
// final int newEntityId = entitySet.addEntity();
// final Entity newEntity = new Entity(entitySet.getType(), newEntityId);
// relationshipSet.getMatrix().set(newEntity.getId(), oldEntity.getId(), -1);
//
// recommenderModel.update();
//
// recommend(recommenderModel, newEntity, unirelationalDataset);
	}

	/**
	 * Compute and output recommendations.
	 */
	private static void recommend(RecommenderModel recommenderModel, Entity entity, Dataset dataset)
	{
		final Iterator <Recommendation> iterator = recommenderModel.recommend(entity, new EntityType[]
		{ dataset.getEntityTypes().iterator().next() });

		final List <Recommendation> recommendations = RecommendationUtils.read(iterator, 5);

		System.err.println(RecommendationUtils.format(recommendations, dataset));
	}
}
