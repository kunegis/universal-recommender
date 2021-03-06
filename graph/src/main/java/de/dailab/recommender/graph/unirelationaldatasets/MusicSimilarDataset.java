package de.dailab.recommender.graph.unirelationaldatasets;

import java.io.IOException;

import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.MetadataName;
import de.dailab.recommender.dataset.RelationshipType;
import de.dailab.recommender.graph.UnirelationalGraphStoreDataset;
import de.dailab.recommender.text.TextSyntaxException;

/**
 * Access to the unirelational music_similar dataset in the Graph Store.
 * 
 * @author autogenerated
 */
@SuppressWarnings("all")
public class MusicSimilarDataset
      extends UnirelationalGraphStoreDataset
{
	/**
	 * Load the unirelational music_similar dataset from the Graph Store.
	 * 
	 * @throws IOException on IO errors
	 * @throws TextSyntaxException syntax error in the text file
	 */
	public MusicSimilarDataset()
	        throws IOException, TextSyntaxException
	{
		super("music_similar");		
	}

	/**
	 * The name of the underlying dataset.  
	 */
	public final static String NAME = "music_similar"; 

	/**
	 * The artist entity type. 
	 */
	public final static EntityType ARTIST = new EntityType("artist");
}	
