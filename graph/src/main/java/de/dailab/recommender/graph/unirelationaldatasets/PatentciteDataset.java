package de.dailab.recommender.graph.unirelationaldatasets;

import java.io.IOException;

import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.MetadataName;
import de.dailab.recommender.dataset.RelationshipType;
import de.dailab.recommender.graph.UnirelationalGraphStoreDataset;
import de.dailab.recommender.text.TextSyntaxException;

/**
 * Access to the unirelational patentcite dataset in the Graph Store.
 * 
 * @author autogenerated
 */
@SuppressWarnings("all")
public class PatentciteDataset
      extends UnirelationalGraphStoreDataset
{
	/**
	 * Load the unirelational patentcite dataset from the Graph Store.
	 * 
	 * @throws IOException on IO errors
	 * @throws TextSyntaxException syntax error in the text file
	 */
	public PatentciteDataset()
	        throws IOException, TextSyntaxException
	{
		super("patentcite");		
	}

	/**
	 * The name of the underlying dataset.  
	 */
	public final static String NAME = "patentcite"; 
}	
