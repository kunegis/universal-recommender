package de.dailab.recommender.graph;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import de.dailab.recommender.constraint.NoSelfRecommender;
import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.RelationshipSet;
import de.dailab.recommender.graph.datasets.BtcDataset;
import de.dailab.recommender.path.PathRecommender;
import de.dailab.recommender.recommend.Recommender;
import de.dailab.recommender.recommend.RecommenderModel;
import de.dailab.recommender.recommendation.Recommendation;
import de.dailab.recommender.recommendation.RecommendationUtils;
import de.dailab.recommender.text.TextSyntaxException;

/**
 * Test the BTC dataset from the Graph Store.
 * 
 * @author kunegis
 */
public class BtcGraphStore
{
	/**
	 * Load the flat BTC dataset.
	 * 
	 * @param args Ignored
	 * 
	 * @throws TextSyntaxException Syntax error
	 * @throws IOException IO error
	 */
	public static void main(String args[])
	    throws IOException, TextSyntaxException
	{
		System.out.println(System.currentTimeMillis());

		final Dataset btc = new BtcDataset();
		System.out.println(System.currentTimeMillis());

		final int entityCount = btc.getEntitySet(BtcDataset.ENTITY).size();

		System.out.println("entityCount = " + entityCount);

		for (final RelationshipSet relationshipSet: btc.getRelationshipSets())
		{
			System.out.printf("Relationship set:  %s\n", relationshipSet.getType());
		}

		final Recommender recommender = new NoSelfRecommender(new PathRecommender(1000));

		System.out.println(System.currentTimeMillis());
		final RecommenderModel recommenderModel = recommender.build(btc);
		System.out.println(System.currentTimeMillis());

		final Entity entity = new Entity(BtcDataset.ENTITY, 41);

		final Iterator <Recommendation> iterator = recommenderModel.recommend(entity, new EntityType[]
		{ BtcDataset.ENTITY });

		final List <Recommendation> recommendations = RecommendationUtils.read(iterator, 100);
		System.out.println(System.currentTimeMillis());

		System.out.printf("Recommendations for %s using %s:\n", btc.getMetadata(entity,
		    BtcDataset.METADATA_SEMANTIC_URI), recommender);

		System.out.println(RecommendationUtils.format(recommendations, btc));
	}
}
