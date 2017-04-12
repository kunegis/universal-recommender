package de.dailab.recommender.graph.unirelationaldatasets;

import java.io.IOException;

import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.MetadataName;
import de.dailab.recommender.dataset.RelationshipType;
import de.dailab.recommender.graph.UnirelationalGraphStoreDataset;
import de.dailab.recommender.text.TextSyntaxException;

/**
 * Access to the unirelational amazon0601 dataset in the Graph Store.
 * 
 * @author autogenerated
 */
@SuppressWarnings("all")
public class Amazon0601Dataset
      extends UnirelationalGraphStoreDataset
{
	/**
	 * Load the unirelational amazon0601 dataset from the Graph Store.
	 * 
	 * @throws IOException on IO errors
	 * @throws TextSyntaxException syntax error in the text file
	 */
	public Amazon0601Dataset()
	        throws IOException, TextSyntaxException
	{
		super("amazon0601");		
	}

	/**
	 * The name of the underlying dataset.  
	 */
	public final static String NAME = "amazon0601"; 
}	
