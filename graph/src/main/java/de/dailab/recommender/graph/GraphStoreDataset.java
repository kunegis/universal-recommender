package de.dailab.recommender.graph;

import java.io.File;
import java.io.IOException;

import de.dailab.recommender.text.TextDataset;
import de.dailab.recommender.text.TextSyntaxException;

/**
 * A dataset from the graph store.
 * 
 * @author kunegis
 */
public class GraphStoreDataset
    extends TextDataset
{
	/**
	 * Load a dataset from the graph store.
	 * 
	 * @param name Name of the dataset, e.g. "movielens-sem"
	 * @throws IOException on IO errors
	 * @throws TextSyntaxException On syntax errors in the file
	 */
	public GraphStoreDataset(String name)
	    throws IOException, TextSyntaxException
	{
		super(new File(GraphStore.getDir() + "/semantic/" + name));
	}
}
