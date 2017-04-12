package de.dailab.recommender.music;

import java.util.Iterator;

import de.dailab.recommender.constraint.NoSelfRecommender;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntitySet;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.RelationshipSet;
import de.dailab.recommender.path.PathRecommender;
import de.dailab.recommender.recommend.Recommender;
import de.dailab.recommender.recommend.RecommenderModel;
import de.dailab.recommender.recommendation.Recommendation;
import de.dailab.recommender.semantic.SparqlException;

/**
 * Test the flat music dataset.
 * 
 * @author kunegis
 */
@Deprecated
public class RecommendMusicAutomatic
{
	/**
	 * Load the "music" ontology prepared by Till et al.
	 * 
	 * @param args Ignored
	 * @throws SparqlException On SPARQL errors
	 */
	public static void main(String args[])
	    throws SparqlException
	{
		final AutomaticMusicDataset dataset = new AutomaticMusicDataset(true);

		final EntityType ARTIST = AutomaticMusicDataset.ENTITY;

		for (final EntitySet entitySet: dataset.getEntitySets())
			System.out.printf("entity set %s: %d\n", entitySet.getType(), entitySet.size());
		for (final RelationshipSet relationshipSet: dataset.getRelationshipSets())
			System.out
			    .printf("relationship set %s: %d\n", relationshipSet.getType(), relationshipSet.getMatrix().nnz());

		/*
		 * Read entities
		 */
		final Entity artist = dataset.getEntity("http://dai-labor.de/semstore/music/thebeatles");

		System.out.println("artist 18:  " + dataset.getMetadata(artist, AutomaticMusicDataset.METADATA_URI));

		/*
		 * Find similar artists to artist 18
		 */
		final Recommender recommender = new NoSelfRecommender(new PathRecommender(1000));
		final RecommenderModel recommenderModel = recommender.build(dataset);
		final Iterator <Recommendation> iterator = recommenderModel.recommend(artist, new EntityType[]
		{ ARTIST });

		System.out.printf("Recommendations:\n");

		int count = 10;
		while (count > 0 && iterator.hasNext())
		{
			final Recommendation recommendation = iterator.next();

			final String uri = (String) dataset.getMetadata(recommendation.getEntity(),
			    AutomaticMusicDataset.METADATA_URI);

			System.out.printf("%.4f %s\n", recommendation.getScore(), uri);

			--count;
		}
	}
}
