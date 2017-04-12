package de.dailab.recommender.graph.datasets;

import java.io.IOException;

import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.MetadataName;
import de.dailab.recommender.dataset.RelationshipType;
import de.dailab.recommender.graph.GraphStoreDataset;
import de.dailab.recommender.text.TextSyntaxException;

/**
 * Access to the semantic movielens-100k dataset in the Graph Store.
 * 
 * @author autogenerated
 */
@SuppressWarnings("all")
public class Movielens100kDataset
      extends GraphStoreDataset
{
	/**
	 * Load the semantic movielens-100k dataset from the Graph Store.
	 * 
	 * @throws IOException on IO errors
	 * @throws TextSyntaxException syntax error in the text file
	 */
	public Movielens100kDataset()
	        throws IOException, TextSyntaxException
	{
		super("movielens-100k");		
	}

	/**
	 * The age metadata name. 
	 */
	public final static MetadataName METADATA_AGE = new MetadataName("age");

	/**
	 * The date metadata name. 
	 */
	public final static MetadataName METADATA_DATE = new MetadataName("date");

	/**
	 * The gender metadata name. 
	 */
	public final static MetadataName METADATA_GENDER = new MetadataName("gender");

	/**
	 * The name metadata name. 
	 */
	public final static MetadataName METADATA_NAME = new MetadataName("name");

	/**
	 * The occupation metadata name. 
	 */
	public final static MetadataName METADATA_OCCUPATION = new MetadataName("occupation");

	/**
	 * The title metadata name. 
	 */
	public final static MetadataName METADATA_TITLE = new MetadataName("title");

	/**
	 * The zip-code metadata name. 
	 */
	public final static MetadataName METADATA_ZIP_CODE = new MetadataName("zip-code");

	/**
	 * The genre entity type. 
	 */
	public final static EntityType GENRE = new EntityType("genre");

	/**
	 * The movie entity type. 
	 */
	public final static EntityType MOVIE = new EntityType("movie");

	/**
	 * The user entity type. 
	 */
	public final static EntityType USER = new EntityType("user");

	/**
	 * The genres relationship type. 
	 */
	public final static RelationshipType GENRES = new RelationshipType("genres");

	/**
	 * The rating relationship type. 
	 */
	public final static RelationshipType RATING = new RelationshipType("rating");
}	