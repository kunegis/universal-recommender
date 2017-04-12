package de.dailab.recommender.graph.unirelationaldatasets;

import java.io.IOException;

import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.MetadataName;
import de.dailab.recommender.dataset.RelationshipType;
import de.dailab.recommender.graph.UnirelationalGraphStoreDataset;
import de.dailab.recommender.text.TextSyntaxException;

/**
 * Access to the unirelational movielens-1m_unweighted dataset in the Graph Store.
 * 
 * @author autogenerated
 */
@SuppressWarnings("all")
public class Movielens1mUnweightedDataset
      extends UnirelationalGraphStoreDataset
{
	/**
	 * Load the unirelational movielens-1m_unweighted dataset from the Graph Store.
	 * 
	 * @throws IOException on IO errors
	 * @throws TextSyntaxException syntax error in the text file
	 */
	public Movielens1mUnweightedDataset()
	        throws IOException, TextSyntaxException
	{
		super("movielens-1m_unweighted");		
	}

	/**
	 * The name of the underlying dataset.  
	 */
	public final static String NAME = "movielens-1m_unweighted"; 
}	
