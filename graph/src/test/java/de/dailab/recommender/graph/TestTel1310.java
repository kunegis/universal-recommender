package de.dailab.recommender.graph;

import java.io.IOException;

import org.junit.Test;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.graph.datasets.Tel1310Dataset;
import de.dailab.recommender.text.TextSyntaxException;

/**
 * Test the "house made" dataset of TEL1310.
 * 
 * @author kunegis
 */
public class TestTel1310
{
	/**
	 * Test metadata reading.
	 * 
	 * @throws TextSyntaxException syntax error
	 * @throws IOException IO error
	 */
	@Test
	public void testMetadata()
	    throws TextSyntaxException, IOException
	{
		final Dataset dataset = new Tel1310Dataset();

		for (int i = 0; i < 9; ++i)
		{
			final Entity movie = new Entity(Tel1310Dataset.MOVIE, i);
			final String title = (String) dataset.getMetadata(movie, Tel1310Dataset.METADATA_TITLE);

			assert title != null;

			System.out.printf("[%d] = »%s«\n", i, title);
		}
	}
}
