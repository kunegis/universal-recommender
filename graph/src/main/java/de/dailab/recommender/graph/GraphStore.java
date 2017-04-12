package de.dailab.recommender.graph;

/**
 * The location of the Graph Store.
 * 
 * @author kunegis
 */
class GraphStore
{
	/**
	 * Location of the graph/ directory in the Graph Store. Currently on Munin. Defaults to $GRAPH_DIR.
	 * 
	 * @return the directory
	 */
	public static synchronized String getDir()
	{
		final String graphDirEnv = System.getenv("GRAPH_DIR");
		if (graphDirEnv != null) graphDir = graphDirEnv;
		return graphDir;
	}

	/**
	 * The "graph/" directory.
	 */
	private static String graphDir = "graph";
}
