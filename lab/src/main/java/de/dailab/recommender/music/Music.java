package de.dailab.recommender.music;

import com.hp.hpl.jena.sdb.store.DatabaseType;

/**
 * Location info for the semantic Music dataset.
 * 
 * @author kunegis
 */
public class Music
{
	/**
	 * The database URL.
	 */
	public final static String URL = "jdbc:mysql://dai158:8886/semstore";

	/**
	 * The database username.
	 */
	public final static String USERNAME = "semstore";

	/**
	 * The database user.
	 */
	public final static String PASSWORD = "semstore";

	/**
	 * The database type.
	 */
	public final static DatabaseType DB_TYPE = DatabaseType.MySQL;

	/**
	 * SPARQL definitions.
	 */
	public final static String SPARQL_DEFINITIONS = "PREFIX mo: <http://purl.org/ontology/mo/> \n"
	    + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
	    + "PREFIX foaf: <http://xmlns.com/foaf/0.1/> \n";

	/**
	 * The graph name.
	 */
	public static final String GRAPH_NAME = "http://dai-labor.de/semstore/music";
}
