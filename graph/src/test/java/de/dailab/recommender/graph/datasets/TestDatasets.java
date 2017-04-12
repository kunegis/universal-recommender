package de.dailab.recommender.graph.datasets;

import java.io.IOException;
import java.util.Set;

import org.junit.Test;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.text.TextSyntaxException;

/**
 * Test the semantic Graph Store datasets.
 * 
 * @author kunegis
 */
public class TestDatasets
{
	/**
	 * Test reading all names.
	 */
	@Test
	public void testDatasets()
	{
		final Set <String> datasets = Datasets.getDatasets();

		assert datasets.size() > 1;
	}

	/**
	 * Test loading specific datasets using their dedicated classes.
	 * 
	 * @throws IOException on IO errors
	 * @throws TextSyntaxException on syntax errors
	 */
	@Test
	public void testSpecificDatasets()
	    throws IOException, TextSyntaxException
	{
		@SuppressWarnings("unused")
		final Dataset movielens100k = new Movielens100kDataset();
	}
}
