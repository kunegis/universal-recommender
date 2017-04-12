package de.dailab.recommender.graph.datasets;

import java.io.IOException;

import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.MetadataName;
import de.dailab.recommender.dataset.RelationshipType;
import de.dailab.recommender.graph.GraphStoreDataset;
import de.dailab.recommender.text.TextSyntaxException;

/**
 * Access to the semantic movielens-10m dataset in the Graph Store.
 * 
 * @author autogenerated
 */
@SuppressWarnings("all")
public class Movielens10mDataset
      extends GraphStoreDataset
{
	/**
	 * Load the semantic movielens-10m dataset from the Graph Store.
	 * 
	 * @throws IOException on IO errors
	 * @throws TextSyntaxException syntax error in the text file
	 */
	public Movielens10mDataset()
	        throws IOException, TextSyntaxException
	{
		super("movielens-10m");		
	}

	/**
	 * The date metadata name. 
	 */
	public final static MetadataName METADATA_DATE = new MetadataName("date");

	/**
	 * The name metadata name. 
	 */
	public final static MetadataName METADATA_NAME = new MetadataName("name");

	/**
	 * The movie entity type. 
	 */
	public final static EntityType MOVIE = new EntityType("movie");

	/**
	 * The tag entity type. 
	 */
	public final static EntityType TAG = new EntityType("tag");

	/**
	 * The user entity type. 
	 */
	public final static EntityType USER = new EntityType("user");

	/**
	 * The annotation relationship type. 
	 */
	public final static RelationshipType ANNOTATION = new RelationshipType("annotation");

	/**
	 * The rating relationship type. 
	 */
	public final static RelationshipType RATING = new RelationshipType("rating");
}	
