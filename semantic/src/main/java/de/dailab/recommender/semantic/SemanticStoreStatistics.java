package de.dailab.recommender.semantic;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

/**
 * Statistics about a semantic store.
 * 
 * @author kunegis
 */
public class SemanticStoreStatistics
{
	/**
	 * Compute the statistics of the graph in the given connection.
	 * 
	 * @param semanticStoreConnection The semantic store that holds the graph to make statistics of
	 */
	public SemanticStoreStatistics(SemanticStoreConnection semanticStoreConnection)
	{
		/*
		 * Entities
		 */
		{
			final String sparql = String.format("SELECT ?type WHERE {GRAPH <%s> {?a <%s> ?type.}}",
			    semanticStoreConnection.graphName, SemanticStoreConnection.RDF_TYPE_URL);

			final ResultSet resultSet = semanticStoreConnection.dao.sparqlSelect(sparql, true);

			final Map <String, Integer> typeCounts = new HashMap <String, Integer>();

			while (resultSet.hasNext())
			{
				final QuerySolution querySolution = (QuerySolution) resultSet.next();

				final String type = querySolution.get("type").toString();

				final Integer count = typeCounts.get(type);

				if (count == null)
					typeCounts.put(type, Integer.valueOf(1));
				else
				{
					final int c = count;
					assert c > 0;
					typeCounts.put(type, c + 1);
				}
			}

			for (final Entry <String, Integer> e: typeCounts.entrySet())
			{
				entityTypes.add(new SortedType(e.getKey(), e.getValue()));
			}
		}

		/*
		 * Relationships
		 */
		{
			final String sparql = String.format("SELECT ?relationship WHERE { GRAPH <%s> { ?a ?relationship ?b. }}",
			    semanticStoreConnection.graphName);

			final ResultSet resultSet = semanticStoreConnection.dao.sparqlSelect(sparql, true);

			final Map <String, Integer> relationshipCounts = new HashMap <String, Integer>();

			while (resultSet.hasNext())
			{
				final QuerySolution querySolution = (QuerySolution) resultSet.next();

				final String relationship = querySolution.get("relationship").toString();

				final Integer count = relationshipCounts.get(relationship);

				if (count == null)
				{
					relationshipCounts.put(relationship, Integer.valueOf(1));
				}
				else
				{
					final int c = count;
					assert c > 0;
					relationshipCounts.put(relationship, c + 1);
				}
			}

			for (final Entry <String, Integer> e: relationshipCounts.entrySet())
				relationshipTypes.add(new SortedType(e.getKey(), e.getValue()));
		}
	}

	/**
	 * The relationship types contained in the dataset, with their occurrence count.
	 */
	public final SortedSet <SortedType> relationshipTypes = new TreeSet <SortedType>();

	/**
	 * The entity types contained in the dataset, with their occurrence count.
	 */
	public final SortedSet <SortedType> entityTypes = new TreeSet <SortedType>();

	/**
	 * Types, sorting by decreasing count.
	 * 
	 * @author kunegis
	 */
	public static class SortedType
	    implements Comparable <SortedType>
	{
		/**
		 * A sorted type with given type string and count.
		 * 
		 * @param type The type string
		 * @param count The count
		 */
		public SortedType(String type, int count)
		{
			this.type = type;
			this.count = count;
		}

		private final String type;
		private final int count;

		@Override
		public int compareTo(SortedType o)
		{
			if (this.count > o.count) return -1;
			if (this.count < o.count) return +1;
			return this.type.compareTo(o.type);
		}

		@Override
		public String toString()
		{
			return String.format("%7d\t%s", count, type);
		}
	}
}
