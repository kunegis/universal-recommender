package de.dailab.recommender.text;

import java.io.File;
import java.io.IOException;

import de.dailab.recommender.dataset.UnirelationalDataset;

/**
 * A unirelational dataset read from a file in the graph syntax.
 * 
 * @author kunegis
 */
public class UnirelationalTextDataset
    extends UnirelationalDataset
{
	/**
	 * Load a unirelational dataset from a given file in the graph syntax.
	 * 
	 * @param file The file to read. Typical names are "out.*" and "rel.*"
	 * @throws IOException on IO errors
	 * @throws TextSyntaxException on syntax errors
	 */
	public UnirelationalTextDataset(File file)
	    throws IOException, TextSyntaxException
	{
		super(TextReader.readRelationshipSet(file));
	}
}
