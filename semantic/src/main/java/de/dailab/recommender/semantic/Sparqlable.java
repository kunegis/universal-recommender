package de.dailab.recommender.semantic;

import java.util.Iterator;

import com.hp.hpl.jena.query.QuerySolution;

/**
 * An interface that supports SPARQL queries in streaming mode.
 * <p>
 * Only SELECTs are supported.
 * 
 * @author kunegis
 */
public interface Sparqlable
{
	/**
	 * Run a SPARQL SELECT query in streaming mode
	 * 
	 * @param sparql The SPARQL query
	 * @return The solutions
	 * 
	 * @throws SparqlException On SPARQL errors
	 */
	Iterator <QuerySolution> sparql(String sparql)
	    throws SparqlException;

	/**
	 * @return The graph that is to be sparqled.
	 */
	String getGraphName();
}
