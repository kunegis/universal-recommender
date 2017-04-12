package de.dailab.recommender.graph.unirelationaldatasets;

import java.io.IOException;

import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.MetadataName;
import de.dailab.recommender.dataset.RelationshipType;
import de.dailab.recommender.graph.UnirelationalGraphStoreDataset;
import de.dailab.recommender.text.TextSyntaxException;

/**
 * Access to the unirelational wiki-Talk dataset in the Graph Store.
 * 
 * @author autogenerated
 */
@SuppressWarnings("all")
public class WikiTalkDataset
      extends UnirelationalGraphStoreDataset
{
	/**
	 * Load the unirelational wiki-Talk dataset from the Graph Store.
	 * 
	 * @throws IOException on IO errors
	 * @throws TextSyntaxException syntax error in the text file
	 */
	public WikiTalkDataset()
	        throws IOException, TextSyntaxException
	{
		super("wiki-Talk");		
	}

	/**
	 * The name of the underlying dataset.  
	 */
	public final static String NAME = "wiki-Talk"; 
}	
