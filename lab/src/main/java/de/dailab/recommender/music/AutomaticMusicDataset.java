package de.dailab.recommender.music;

import de.dailab.recommender.semantic.AutomaticSemanticStoreDataset;
import de.dailab.recommender.semantic.SemanticStoreConnection;
import de.dailab.recommender.semantic.SparqlException;

/**
 * The automatic Music dataset from the semantic store.
 * 
 * @author kunegis
 */
@Deprecated
public class AutomaticMusicDataset
    extends AutomaticSemanticStoreDataset
{
	/**
	 * Load the automatic music dataset from the semantic store.
	 * 
	 * @param extractEntities Whether entity types are extracted separately
	 * @throws SparqlException On SPARQL errors
	 */
	public AutomaticMusicDataset(boolean extractEntities)
	    throws SparqlException
	{
		super(createSemanticStoreConnection(), extractEntities);
	}

	/**
	 * The automatic music dataset without extracting entity types separately.
	 * 
	 * @throws SparqlException On SPARQL errors
	 */
	public AutomaticMusicDataset()
	    throws SparqlException
	{
		this(false);
	}

	private static SemanticStoreConnection createSemanticStoreConnection()
	{
		return new SemanticStoreConnection(Music.URL, Music.USERNAME, Music.PASSWORD, Music.GRAPH_NAME, Music.DB_TYPE);
	}
}
