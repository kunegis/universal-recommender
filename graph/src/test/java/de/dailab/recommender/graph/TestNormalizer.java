package de.dailab.recommender.graph;

import java.io.IOException;

import org.junit.Test;

import de.dailab.recommender.dataset.UnirelationalDataset;
import de.dailab.recommender.graph.unirelationaldatasets.Movielens100kRatingDataset;
import de.dailab.recommender.matrix.Matrix;
import de.dailab.recommender.normalize.AdditiveNormalizer;
import de.dailab.recommender.normalize.Normalizer;
import de.dailab.recommender.normalize.NormalizerModel;
import de.dailab.recommender.text.TextSyntaxException;

/**
 * Test normalization.
 * 
 * @author kunegis
 */
public class TestNormalizer
{
	/**
	 * Test the additive normalizer.
	 * 
	 * @throws IOException On IO errors.
	 * @throws TextSyntaxException Syntax error
	 */
	@Test
	public void testNormalization()
	    throws IOException, TextSyntaxException
	{
		final UnirelationalDataset movielens100k = new Movielens100kRatingDataset();
		final Matrix matrix = movielens100k.getRelationshipSet(movielens100k.getUniqueRelationshipType()).getMatrix();

		final Normalizer normalizer = new AdditiveNormalizer();

		@SuppressWarnings("unused")
		final NormalizerModel normalizerModel = normalizer.build(matrix);
	}
}
