package de.dailab.recommender.btc;

import com.hp.hpl.jena.sdb.store.DatabaseType;

/**
 * Access data to the Billion Triple Challenge (BTC) dataset.
 * 
 * @author kunegis
 */
public class Btc
{
	final static String URL = "jdbc:mysql://dai158:8886/semstore";

	final static String USERNAME = "semstore";
	final static String PASSWORD = "semstore";

	final static int POOLSIZE = 0;

	/**
	 * Name of the graph as used in SPARQL.
	 */
	final static String GRAPH_NAME = "http://dai-labor.de/semstore/btc-2009-small";

	final static DatabaseType DATABASE_TYPE = DatabaseType.MySQL;
}
