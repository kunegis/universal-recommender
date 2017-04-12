package de.dailab.recommender.btc;

import java.util.Iterator;
import java.util.List;

import de.dailab.recommender.constraint.NoSelfRecommender;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.RelationshipSet;
import de.dailab.recommender.path.PathRecommender;
import de.dailab.recommender.recommend.Recommender;
import de.dailab.recommender.recommend.RecommenderModel;
import de.dailab.recommender.recommendation.Recommendation;
import de.dailab.recommender.recommendation.RecommendationUtils;
import de.dailab.recommender.semantic.SparqlException;

/**
 * Test the flat BTC dataset.
 * 
 * @author kunegis
 */
@Deprecated
public class BtcAutomatic
{
	/**
	 * Load the flat BTC dataset.
	 * 
	 * @param args Ignored
	 * 
	 * @throws SparqlException On SPARQL errors
	 */
	public static void main(String[] args)
	    throws SparqlException
	{
		System.out.println(System.currentTimeMillis());

		final AutomaticBtcDataset btc = new AutomaticBtcDataset(true);
		System.out.println(System.currentTimeMillis());

		final int entityCount = btc.getEntitySet(AutomaticBtcDataset.ENTITY).size();

		System.out.println("entityCount = " + entityCount);

		for (final RelationshipSet relationshipSet: btc.getRelationshipSets())
		{
			System.out.printf("Relationship set:  %s\n", relationshipSet.getType());
		}

		final Recommender recommender = new NoSelfRecommender(new PathRecommender(1000));

		System.out.println(System.currentTimeMillis());
		final RecommenderModel recommenderModel = recommender.build(btc);
		System.out.println(System.currentTimeMillis());

		final Entity entity = btc.getEntity("http://www.w3.org/People/Berners-Lee/card");

		final Iterator <Recommendation> iterator = recommenderModel.recommend(entity, new EntityType[]
		{ AutomaticBtcDataset.ENTITY });

		final List <Recommendation> recommendations = RecommendationUtils.read(iterator, 100);
		System.out.println(System.currentTimeMillis());

		System.out.printf("Recommendations for %s using %s:\n", btc.getMetadata(entity,
		    AutomaticBtcDataset.METADATA_URI), recommender);

		System.out.println(RecommendationUtils.format(recommendations, btc));
	}
}
