package de.dailab.recommender.btc;

import de.dailab.recommender.semantic.Mode;
import de.dailab.recommender.semantic.SemanticEntityType;
import de.dailab.recommender.semantic.SemanticRelationshipType;
import de.dailab.recommender.semantic.SemanticStoreConnection;
import de.dailab.recommender.semantic.SemanticStoreDataset;
import de.dailab.recommender.semantic.SparqlException;

/**
 * The Billion Triple Challenge (BTC) dataset from "http://dai-labor.de/semstore/btc-2009-small"
 * 
 * @author kunegis
 * 
 * @see <a href = "http://challenge.semanticweb.org/">challenge.semanticweb.org</a>
 */
public class BtcDataset
    extends SemanticStoreDataset
{
	private final static SemanticEntityType SEMANTIC_ENTITY_TYPES[] = new SemanticEntityType[] {};

	private final static SemanticRelationshipType SEMANTIC_RELATIONSHIP_TYPES[] = new SemanticRelationshipType[] {};

	private final static String SPARQL_DEFINITIONS = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n";

	/**
	 * Load the Billion Triple Challenge dataset from the Semantic Store.
	 * 
	 * @throws SparqlException On SPARQL errors
	 */
	public BtcDataset()
	    throws SparqlException
	{
		super(createSemanticStoreConnection(), SEMANTIC_ENTITY_TYPES, SEMANTIC_RELATIONSHIP_TYPES, Mode.IGNORE,
		    SPARQL_DEFINITIONS);
	}

	/**
	 * @return A semantic connection corresponding to the BTC dataset
	 */
	public static SemanticStoreConnection createSemanticStoreConnection()
	{
		return new SemanticStoreConnection(Btc.URL, Btc.USERNAME, Btc.PASSWORD, Btc.GRAPH_NAME, Btc.DATABASE_TYPE);
	}
}
