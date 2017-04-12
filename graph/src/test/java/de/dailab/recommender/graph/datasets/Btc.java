package de.dailab.recommender.graph.datasets;

import java.io.IOException;

import org.junit.Test;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.RelationshipSet;
import de.dailab.recommender.text.TextSyntaxException;

/**
 * Test that loading the BTC dataset from the Graph Store works. In particular, test that the relationship names with
 * slashes in them work (they are URIs).
 * 
 * @author kunegis
 */
public class Btc
{
	/**
	 * Load the BTC dataset from the Graph Store and make sure the foaf-knows relationship set is not empty.
	 * 
	 * @throws IOException On IO errors
	 * @throws TextSyntaxException On syntax errors
	 */
	@Test
	public void test()
	    throws IOException, TextSyntaxException
	{
		final Dataset dataset = new BtcDataset();

		final RelationshipSet relationshipSet = dataset.getRelationshipSet(BtcDataset.HTTP_XMLNS_COM_FOAF_KNOWS);

		final int nnz = relationshipSet.getMatrix().nnz();

		System.out.println("nnz = " + nnz);

		assert nnz > 0;
	}
}
