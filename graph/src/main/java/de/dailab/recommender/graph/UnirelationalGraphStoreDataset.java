package de.dailab.recommender.graph;

import java.io.File;
import java.io.IOException;

import de.dailab.recommender.text.TextSyntaxException;
import de.dailab.recommender.text.UnirelationalTextDataset;

/**
 * A unirelational dataset from the Graph Store.
 * 
 * @author kunegis
 */
public class UnirelationalGraphStoreDataset
    extends UnirelationalTextDataset
{
	/**
	 * Load a unirelational dataset from the Graph Store.
	 * 
	 * @param name The name of the unirelational dataset, e.g. "slashdot-zoo"
	 * @throws IOException on IO errors
	 * @throws TextSyntaxException on syntax errors in an underlying file
	 */
	public UnirelationalGraphStoreDataset(String name)
	    throws IOException, TextSyntaxException
	{
		super(new File(GraphStore.getDir() + "/scp/out." + name));
	}
}
