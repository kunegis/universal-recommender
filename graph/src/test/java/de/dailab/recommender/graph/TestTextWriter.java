package de.dailab.recommender.graph;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.graph.datasets.Movielens100kDataset;
import de.dailab.recommender.text.TextDataset;
import de.dailab.recommender.text.TextSyntaxException;
import de.dailab.recommender.text.TextWriter;

/**
 * Test the text writer.
 * 
 * @author kunegis
 */
public class TestTextWriter
{
	/**
	 * Read, write and re-read a small dataset.
	 * 
	 * @throws IOException IO errors
	 * @throws TextSyntaxException syntax errors
	 */
	@Test
	public void test()
	    throws IOException, TextSyntaxException
	{
		Dataset dataset = new Movielens100kDataset();

		final File dir = File.createTempFile(this.getClass().getSimpleName(), null);

		System.out.printf("Writing to %s\n", dir);

		dir.delete();
		dir.deleteOnExit();

		if (!dir.mkdir()) throw new IOException("mkdir");

		TextWriter.write(dir, dataset);

		dataset = new TextDataset(dir);
	}

}
