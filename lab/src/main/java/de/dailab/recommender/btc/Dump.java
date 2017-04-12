package de.dailab.recommender.btc;

import java.io.File;
import java.io.IOException;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.EntitySet;
import de.dailab.recommender.dataset.RelationshipSet;
import de.dailab.recommender.semantic.SparqlException;
import de.dailab.recommender.text.TextWriter;

/**
 * Dump the BTC dataset to text format.
 * 
 * @author kunegis
 */
@Deprecated
public class Dump
{
	/**
	 * Dump the BTC dataset to graph format in DIR.
	 * 
	 * @param args [0] DIR
	 * @throws IOException IO errors
	 * @throws SparqlException On SPARQL errors
	 */
	public static void main(String[] args)
	    throws SparqlException, IOException
	{
		if (args.length != 1) throw new IllegalArgumentException();

		final File dir = new File(args[0]);

		final Dataset dataset = new AutomaticBtcDataset();

		System.out.println("Entity types:");

		for (final EntitySet entitySet: dataset.getEntitySets())
		{
			System.out.printf("%7d %s\n", entitySet.size(), entitySet.getType());
		}

		System.out.println("Relationship types:");

		for (final RelationshipSet relationshipSet: dataset.getRelationshipSets())
		{
			System.out.printf("%7d %s\n", relationshipSet.getMatrix().nnz(), relationshipSet.getType());
		}

		TextWriter.write(dir, dataset);
	}
}
