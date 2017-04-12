package de.dailab.recommender.btc;

import de.dailab.recommender.semantic.AutomaticSemanticStoreDataset;
import de.dailab.recommender.semantic.SemanticStoreConnection;
import de.dailab.recommender.semantic.SparqlException;

/**
 * The Billion Triple Challenge (BTC) dataset, with automatically extracted ontology.
 * 
 * @author kunegis
 */
@Deprecated
public class AutomaticBtcDataset
    extends AutomaticSemanticStoreDataset
{
	/**
	 * Load the flat Billion Triple Challenge (BTC) dataset.
	 * 
	 * @param extractEntityTypes Extract single entity types
	 * @throws SparqlException On SPARQL errors
	 */
	public AutomaticBtcDataset(boolean extractEntityTypes)
	    throws SparqlException
	{
		super(createSemanticStoreConnection(), extractEntityTypes);
	}

	/**
	 * THe Billion Triple Challenge data without specially extracted entity types.
	 * 
	 * @throws SparqlException On SPARQL errors
	 */
	public AutomaticBtcDataset()
	    throws SparqlException
	{
		this(false);
	}

	private static SemanticStoreConnection createSemanticStoreConnection()
	{
		return new SemanticStoreConnection(Btc.URL, Btc.USERNAME, Btc.PASSWORD, Btc.GRAPH_NAME, Btc.DATABASE_TYPE);
	}
}
