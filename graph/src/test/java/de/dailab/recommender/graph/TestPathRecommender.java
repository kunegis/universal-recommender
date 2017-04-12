package de.dailab.recommender.graph;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.graph.datasets.Movielens100kDataset;
import de.dailab.recommender.path.CompoundPath;
import de.dailab.recommender.path.PathRecommender;
import de.dailab.recommender.path.RelationshipPath;
import de.dailab.recommender.recommend.Recommender;
import de.dailab.recommender.recommend.RecommenderModel;
import de.dailab.recommender.recommendation.Recommendation;
import de.dailab.recommender.text.TextSyntaxException;

/**
 * Test that the path recommender does not return duplicates.
 * 
 * @author kunegis
 */
public class TestPathRecommender
{
	/**
	 * Test that even with multiple sources, the path recommender does not return duplicates.
	 * 
	 * @throws IOException IO errors
	 * @throws TextSyntaxException syntax errors
	 */
	@Test
	public void test()
	    throws IOException, TextSyntaxException
	{
		final Dataset dataset = new Movielens100kDataset();

		final Recommender recommender = new PathRecommender(new CompoundPath(new RelationshipPath(
		    Movielens100kDataset.RATING), new RelationshipPath(Movielens100kDataset.RATING, true),
		    new RelationshipPath(Movielens100kDataset.RATING)));

		final RecommenderModel recommenderModel = recommender.build(dataset);

		final Set <Entity> seen = new HashSet <Entity>();

		final Map <Entity, Double> sources = new HashMap <Entity, Double>();
		sources.put(new Entity(Movielens100kDataset.USER, 12), 1.);
		sources.put(new Entity(Movielens100kDataset.USER, 13), 1.);

		final Iterator <Recommendation> iterator = recommenderModel.recommend(sources, new EntityType[]
		{ Movielens100kDataset.MOVIE });

		while (iterator.hasNext())
		{
			final Recommendation recommendation = iterator.next();
			final Entity entity = recommendation.getEntity();
			assert !seen.contains(entity);
			seen.add(entity);
		}

		assert seen.size() > 100;
	}
}
