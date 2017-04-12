package de.dailab.recommender.semantic;

import java.util.Iterator;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sdb.store.DatabaseType;

import de.dailab.semanticstore.persistence.GenericSDBDAO;
import de.dailab.semanticstore.persistence.interfaces.GenericStoreDAO;

/**
 * A connection to specific graph in a semantic store.
 * <p>
 * This sparqlable will be replace be one that does not depend on the
 * SemanticStore package, i.e. only on Jena.
 * 
 * @author kunegis
 */
public class SemanticStoreConnection implements Sparqlable {
    /**
     * A semantic store connection using the given DAO and graph name.
     * <p>
     * Note: it may be significant that the ends name ends in a slash.
     * 
     * @param dao
     *            The DAO to use
     * @param graphName
     *            The URL of the graph in the semantic store
     * 
     * @deprecated Use a constructor that takes a URL or Jena objects
     */
    @Deprecated
    public SemanticStoreConnection(GenericStoreDAO dao, String graphName) {
	this.dao = dao;
	this.graphName = graphName;
    }

    /**
     * The semantic store connection with the given connection data.
     * 
     * @param url
     *            The URL of the semantic store, e.g.
     *            "jdbc:mysql://dai158:8886/semstore"
     * @param username
     *            The username for connecting to the semantic store
     * @param password
     *            The password for connecting to the semantic store
     * @param graphName
     *            The URL of the graph in the semantic store, e.g.
     *            "http://dai-labor.de/semstore/btc-2009-small"
     * @param databaseType
     *            The type of the underlying database
     */
    public SemanticStoreConnection(String url, String username,
	    String password, String graphName, DatabaseType databaseType) {
	this.dao = new GenericSDBDAO(url, username, password, databaseType, 0);
	this.graphName = graphName;
    }

    /**
     * The DAO through which to access the semantic store.
     */
    public final GenericStoreDAO dao;

    /**
     * The URL of the graph in the semantic store
     */
    public final String graphName;

    public String getGraphName() {
	return graphName;
    }

    /**
     * The RDF URI denoting entity types.
     */
    public final static String RDF_TYPE_URL = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";

    @Override
    public Iterator<QuerySolution> sparql(String sparql) {
	final ResultSet resultSet = dao.sparqlSelect(sparql, true);

	return new Iterator<QuerySolution>() {
	    @Override
	    public boolean hasNext() {
		return resultSet.hasNext();
	    }

	    @Override
	    public QuerySolution next() {
		return (QuerySolution) resultSet.next();
	    }

	    @Override
	    public void remove() {
		throw new UnsupportedOperationException();
	    }
	};
    }
}
