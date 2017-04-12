package de.dailab.recommender.graph.unirelationaldatasets;

import java.io.IOException;

import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.MetadataName;
import de.dailab.recommender.dataset.RelationshipType;
import de.dailab.recommender.graph.UnirelationalGraphStoreDataset;
import de.dailab.recommender.text.TextSyntaxException;

/**
 * Access to the unirelational hep-th-citations dataset in the Graph Store.
 * 
 * @author autogenerated
 */
@SuppressWarnings("all")
public class HepThCitationsDataset
      extends UnirelationalGraphStoreDataset
{
	/**
	 * Load the unirelational hep-th-citations dataset from the Graph Store.
	 * 
	 * @throws IOException on IO errors
	 * @throws TextSyntaxException syntax error in the text file
	 */
	public HepThCitationsDataset()
	        throws IOException, TextSyntaxException
	{
		super("hep-th-citations");		
	}

	/**
	 * The name of the underlying dataset.  
	 */
	public final static String NAME = "hep-th-citations"; 
}	
