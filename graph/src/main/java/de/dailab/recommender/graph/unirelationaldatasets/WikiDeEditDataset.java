package de.dailab.recommender.graph.unirelationaldatasets;

import java.io.IOException;

import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.MetadataName;
import de.dailab.recommender.dataset.RelationshipType;
import de.dailab.recommender.graph.UnirelationalGraphStoreDataset;
import de.dailab.recommender.text.TextSyntaxException;

/**
 * Access to the unirelational wiki-de-edit dataset in the Graph Store.
 * 
 * @author autogenerated
 */
@SuppressWarnings("all")
public class WikiDeEditDataset
      extends UnirelationalGraphStoreDataset
{
	/**
	 * Load the unirelational wiki-de-edit dataset from the Graph Store.
	 * 
	 * @throws IOException on IO errors
	 * @throws TextSyntaxException syntax error in the text file
	 */
	public WikiDeEditDataset()
	        throws IOException, TextSyntaxException
	{
		super("wiki-de-edit");		
	}

	/**
	 * The name of the underlying dataset.  
	 */
	public final static String NAME = "wiki-de-edit"; 
}	
