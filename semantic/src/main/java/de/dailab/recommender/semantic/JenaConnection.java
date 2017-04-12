package de.dailab.recommender.semantic;

import java.util.Iterator;

import com.hp.hpl.jena.graph.GraphEvents;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sdb.SDB;
import com.hp.hpl.jena.sdb.SDBException;

/**
 * A sparqlable interface base on a Jena database object.
 * <p>
 * SPARQL queries are gives as-is to a semantic store through the Jena library.
 * 
 * @author kunegis
 */
public class JenaConnection
    implements Sparqlable
{
	/**
	 * A sparqlable based on the given dataset and graph.
	 * 
	 * @param jenaDataset The Jena dataset
	 * @param graphName The graph to access
	 */
	public JenaConnection(Dataset jenaDataset, String graphName)
	{
		this.jenaDataset = jenaDataset;
		this.graphName = graphName;
	}

	final private Dataset jenaDataset;
	final private String graphName;

	@SuppressWarnings("unchecked")
	@Override
	public Iterator <QuerySolution> sparql(String sparql)
	    throws SparqlException
	{
		final Query query = QueryFactory.create(sparql);

		final QueryExecution queryExecution = QueryExecutionFactory.create(query, jenaDataset);

		jenaDataset.getDefaultModel().notifyEvent(GraphEvents.startRead);

		/* This has failed once at the Turkish Telekom with the error that the fetch size must not be negative. */
		queryExecution.getContext().set(SDB.jdbcFetchSize, Integer.MIN_VALUE);

		ResultSet resultSet = null;

		try
		{
			resultSet = queryExecution.execSelect();
		}
		catch (final SDBException sdbException)
		{
			throw new SparqlException(sdbException);
		}
		finally
		{
			jenaDataset.getDefaultModel().notifyEvent(GraphEvents.finishRead);
			queryExecution.close();
		}

		return resultSet;
	}

	@Override
	public String getGraphName()
	{
		return graphName;
	}

	/**
	 * Return the Jena dataset.
	 * 
	 * @return The Jena dataset
	 */
	public Dataset getJenaDataset()
	{
		return jenaDataset;
	}
}
