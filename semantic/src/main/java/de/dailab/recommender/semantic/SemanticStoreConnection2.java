package de.dailab.recommender.semantic;

import java.sql.Connection;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.StoreDesc;
import com.hp.hpl.jena.sdb.sql.SDBConnection;
import com.hp.hpl.jena.sdb.sql.SDBConnectionDesc;
import com.hp.hpl.jena.sdb.store.DatabaseType;
import com.hp.hpl.jena.sdb.store.LayoutType;

/**
 * A semantic store connection that uses Jena internally. Functionally equivalent to {@link SemanticStoreConnection}.
 * 
 * @author kunegis
 */
public class SemanticStoreConnection2
    extends JenaConnection
{
	/**
	 * The connection with the given parameters.
	 * 
	 * @param url The URL
	 * @param username The username
	 * @param password The password
	 * @param graphName The graph name in the semantic store
	 * @param databaseType The database type
	 */
	public SemanticStoreConnection2(String url, String username, String password, String graphName,
	    DatabaseType databaseType)
	{
		super(getDataset(url, username, password, databaseType), graphName);
	}

	private static Dataset getDataset(String url, String username, String password, DatabaseType databaseType)
	{
		final SDBConnectionDesc connectionDesc = SDBConnectionDesc.blank();
		connectionDesc.setJdbcURL(url);
		connectionDesc.setType(databaseType.getName());
		connectionDesc.setUser(username);
		connectionDesc.setPassword(password);

		final StoreDesc storeDesc = new StoreDesc(LayoutType.LayoutTripleNodesIndex, databaseType);

		final Connection jdbcConnection = SDBFactory.createSqlConnection(connectionDesc);

		final SDBConnection connection = SDBFactory.createConnection(jdbcConnection);
		final Store store = SDBFactory.connectStore(connection, storeDesc);
		store.getLoader().setUseThreading(true);

		return SDBFactory.connectDataset(store);
	}
}
