package de.dailab.recommender.graph.unirelationaldatasets;

import java.io.IOException;

import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.MetadataName;
import de.dailab.recommender.dataset.RelationshipType;
import de.dailab.recommender.graph.UnirelationalGraphStoreDataset;
import de.dailab.recommender.text.TextSyntaxException;

/**
 * Access to the unirelational wiki-edit-elwiki dataset in the Graph Store.
 * 
 * @author autogenerated
 */
@SuppressWarnings("all")
public class WikiEditElwikiDataset
      extends UnirelationalGraphStoreDataset
{
	/**
	 * Load the unirelational wiki-edit-elwiki dataset from the Graph Store.
	 * 
	 * @throws IOException on IO errors
	 * @throws TextSyntaxException syntax error in the text file
	 */
	public WikiEditElwikiDataset()
	        throws IOException, TextSyntaxException
	{
		super("wiki-edit-elwiki");		
	}

	/**
	 * The name of the underlying dataset.  
	 */
	public final static String NAME = "wiki-edit-elwiki"; 
}	
