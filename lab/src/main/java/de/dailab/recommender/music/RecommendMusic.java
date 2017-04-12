package de.dailab.recommender.music;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.dailab.recommender.constraint.NoSelfRecommender;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.evaluation.RecommenderEvaluation;
import de.dailab.recommender.latent.LatentRecommender;
import de.dailab.recommender.matrix.Entry;
import de.dailab.recommender.path.PathRecommender;
import de.dailab.recommender.recommend.LookaheadRecommender;
import de.dailab.recommender.recommend.Recommender;
import de.dailab.recommender.recommend.RecommenderModel;
import de.dailab.recommender.recommendation.Recommendation;
import de.dailab.recommender.recommendation.RecommendationUtils;
import de.dailab.recommender.semantic.SparqlException;

/**
 * Demonstrate the Universal Recommender and the Semantic Store.
 * 
 * @author kunegis
 */
public class RecommendMusic
{
	/**
	 * Find artists similar to a given artist.
	 * 
	 * @throws SparqlException SPARQL error
	 */
	public void findSimilarArtist()
	    throws SparqlException
	{
		/*
		 * Load dataset
		 */
		System.out.println("Loading music dataset from the Semantic Store...");

		final MusicDataset musicDataset = new MusicDataset();

		/*
		 * List known-similar artists
		 */
		final String artistUrl = "http://dai-labor.de/semstore/music/therollingstones";

		final Entity artist = musicDataset.getEntity(MusicDataset.METADATA_URI, MusicDataset.ARTIST, artistUrl);

		final String artistName = (String) musicDataset.getMetadata(artist, MusicDataset.METADATA_FOAFNAME);

		System.out.printf("Artists known to be similar to %s:\n", artistName);

		for (final Entry entry: musicDataset.getRelationshipSet(MusicDataset.SIMILAR).getMatrix().row(artist.getId()))
		{
			final Entity otherArtist = new Entity(MusicDataset.ARTIST, entry.index);
			System.out.printf("\t%s\n", musicDataset.getMetadata(otherArtist, MusicDataset.METADATA_FOAFNAME));
		}

		/*
		 * Tags of that artist
		 */
		System.out.printf("Tags of %s:\n", musicDataset.getMetadata(artist, MusicDataset.METADATA_FOAFNAME));
		for (final Entry entry: musicDataset.getRelationshipSet(MusicDataset.ARTIST_TAG).getMatrix()
		    .row(artist.getId()))
		{
			System.out.printf("\t%s\n", musicDataset.getMetadata(new Entity(MusicDataset.TAG, entry.index),
			    MusicDataset.METADATA_URI));
		}

		/*
		 * Find similar artists
		 */

		final List <Recommender> recommenders = new ArrayList <Recommender>();
		recommenders.addAll(RecommenderEvaluation.RECOMMENDERS_DEFAULT.getRecommenders());
		recommenders.add(new PathRecommender(100));
		final Recommender bestRecommender = new LookaheadRecommender(100, new LatentRecommender());
		recommenders.add(bestRecommender);

		RecommenderModel bestRecommenderModel = null;

		for (final Recommender recommender: recommenders)
		{
			System.out.printf("Building recommender %s...\n", recommender);

			final RecommenderModel recommenderModel = new NoSelfRecommender(recommender).build(musicDataset);

			if (recommender == bestRecommender) bestRecommenderModel = recommenderModel;

			System.out.printf("Finding similar artists to %s...\n", artistName);

			final Iterator <Recommendation> iterator = recommenderModel.recommend(artist, new EntityType[]
			{ MusicDataset.ARTIST });

			final List <Recommendation> recommendations = RecommendationUtils.read(iterator, 10);

			for (final Recommendation recommendation: recommendations)
			{
				final String otherArtistName = (String) musicDataset.getMetadata(recommendation.getEntity(),
				    MusicDataset.METADATA_FOAFNAME);
				System.out.printf("%.4f\t%s\n", recommendation.getScore(), otherArtistName);
			}

			System.out.println();
		}

		/*
		 * Find similar artists to Rolling Stones & Beatles
		 */
		final Entity artistB = musicDataset.getEntity(MusicDataset.METADATA_URI, MusicDataset.ARTIST,
		    "http://dai-labor.de/semstore/music/thebeatles");
		final String artistBName = (String) musicDataset.getMetadata(artistB, MusicDataset.METADATA_FOAFNAME);
		System.out.printf(String.format("Finding artists similar to %s and not %s:\n", artistBName, artistName));
		final Map <Entity, Double> sources = new HashMap <Entity, Double>();
		sources.put(artist, -1.);
		sources.put(artistB, 1.);
		final Iterator <Recommendation> iterator = bestRecommenderModel.recommend(sources, new EntityType[]
		{ MusicDataset.ARTIST });

		final List <Recommendation> recommendations = RecommendationUtils.read(iterator, 15);

		for (final Recommendation recommendation: recommendations)
		{
			final String otherArtistName = (String) musicDataset.getMetadata(recommendation.getEntity(),
			    MusicDataset.METADATA_FOAFNAME);
			System.out.printf("%.4f\t%s\n", recommendation.getScore(), otherArtistName);
		}

		System.out.println();
	}

	/**
	 * Execute the music recommender.
	 * 
	 * @param args Ignored
	 * @throws SparqlException SPARQL error
	 */
	public static void main(String[] args)
	    throws SparqlException
	{
		new RecommendMusic().findSimilarArtist();
	}
}
