package de.dailab.recommender.semantic;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.path.PathRecommender;
import de.dailab.recommender.recommend.RecommendationResult;
import de.dailab.recommender.recommend.Recommender;
import de.dailab.recommender.recommend.RecommenderModel;
import de.dailab.recommender.recommendation.RecommendationUtils;

/**
 * Test the semantic radar.
 * 
 * @author kunegis
 */
public class TestSemanticRadar
{
	/**
	 * Test the semantic radar.
	 */
	@Test
	@Deprecated
	public void testSemanticRadar()
	{
		final Dataset dataset = new BipartiteSyntheticSemanticDataset();

		final Recommender recommender = new PathRecommender();

		final RecommenderModel recommenderModel = recommender.build(dataset);

		final Map <Entity, Double> sources = new HashMap <Entity, Double>();
		sources.put(new Entity(BipartiteSyntheticSemanticDataset.USER, 0), 1.);

		final RecommendationResult recommendationResult = recommenderModel.recommendExt(sources, new EntityType[]
		{ BipartiteSyntheticSemanticDataset.USER });

		RecommendationUtils.read(recommendationResult.getIterator(), 5);

		final SemanticRadar semanticRadar = new SemanticRadar(recommendationResult, dataset);

		final com.hp.hpl.jena.rdf.model.Model model = semanticRadar.getModel();

		model.write(System.err);
	}
}
