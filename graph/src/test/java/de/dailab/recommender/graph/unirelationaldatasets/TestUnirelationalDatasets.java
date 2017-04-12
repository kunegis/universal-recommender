package de.dailab.recommender.graph.unirelationaldatasets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.junit.Test;

import de.dailab.recommender.graph.UnirelationalGraphStoreDataset;
import de.dailab.recommender.text.TextSyntaxException;

/**
 * Test the unirelational Graph Store datasets.
 * 
 * @author kunegis
 */
public class TestUnirelationalDatasets
{
	/**
	 * Test reading all names.
	 */
	@Test
	public void testUnirelationalDatasets()
	{
		final Set <String> datasets = UnirelationalDatasets.getDatasets();

		assert datasets.size() > 10;
	}

	/**
	 * Test loading specific datasets using their dedicated classes.
	 * 
	 * @throws IOException on IO errors
	 * @throws TextSyntaxException on syntax errors
	 * @throws IllegalAccessException Must not happen
	 * @throws InstantiationException Must not happen
	 */
	@Test
	public void testSpecificDatasets()
	    throws IOException, TextSyntaxException, InstantiationException, IllegalAccessException
	{
		for (final Class <? extends UnirelationalGraphStoreDataset> datasetClass: DATASETS)
		{
			System.out.printf("Loading %s...\n", datasetClass.getSimpleName());
			final UnirelationalGraphStoreDataset dataset = datasetClass.newInstance();
			System.out.printf(Locale.ENGLISH, "\tRelationships loaded:  %d\n", dataset.getMatrix().nnz());
		}
	}

	private final static List <Class <? extends UnirelationalGraphStoreDataset>> DATASETS = new ArrayList <Class <? extends UnirelationalGraphStoreDataset>>();
	static
	{
		DATASETS.add(DblpCiteDataset.class);
		DATASETS.add(AdvogatoDataset.class);
		DATASETS.add(WikiVoteDataset.class);
		DATASETS.add(Movielens100kRatingDataset.class);
		DATASETS.add(SlashdotZooDataset.class);
	}
}
