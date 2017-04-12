package de.dailab.recommender.graph.unirelationaldatasets;

import java.io.IOException;

import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.MetadataName;
import de.dailab.recommender.dataset.RelationshipType;
import de.dailab.recommender.graph.UnirelationalGraphStoreDataset;
import de.dailab.recommender.text.TextSyntaxException;

/**
 * Access to the unirelational movielens-10m_rating-unweighted dataset in the Graph Store.
 * 
 * @author autogenerated
 */
@SuppressWarnings("all")
public class Movielens10mRatingUnweightedDataset
      extends UnirelationalGraphStoreDataset
{
	/**
	 * Load the unirelational movielens-10m_rating-unweighted dataset from the Graph Store.
	 * 
	 * @throws IOException on IO errors
	 * @throws TextSyntaxException syntax error in the text file
	 */
	public Movielens10mRatingUnweightedDataset()
	        throws IOException, TextSyntaxException
	{
		super("movielens-10m_rating-unweighted");		
	}

	/**
	 * The name of the underlying dataset.  
	 */
	public final static String NAME = "movielens-10m_rating-unweighted"; 

	/**
	 * The date metadata name. 
	 */
	public final static MetadataName METADATA_DATE = new MetadataName("date");

	/**
	 * The movie entity type. 
	 */
	public final static EntityType MOVIE = new EntityType("movie");

	/**
	 * The user entity type. 
	 */
	public final static EntityType USER = new EntityType("user");

	/**
	 * The rating relationship type. 
	 */
	public final static RelationshipType RATING = new RelationshipType("rating");
}	