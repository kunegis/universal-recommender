package de.dailab.recommender.music;

import java.io.File;
import java.io.IOException;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.semantic.SparqlException;
import de.dailab.recommender.text.TextDataset;
import de.dailab.recommender.text.TextSyntaxException;
import de.dailab.recommender.text.TextWriter;

/**
 * Load the Music dataset, save it to text file and reload it from that text file.
 */
public class RunSemanticTextDataset
{
	/**
	 * Load the Music dataset from the Music Store, and then write and read it from text file.
	 * 
	 * @param args Ignore
	 * @throws IOException IO errors
	 * @throws TextSyntaxException Syntax errors
	 * @throws SparqlException SPARQL error
	 */
	public static void main(String[] args)
	    throws IOException, TextSyntaxException, SparqlException
	{
		Dataset dataset = new MusicDataset2();

		System.out.println(dataset);

		Entity entity = dataset.getEntity(MusicDataset.METADATA_URI, MusicDataset.ARTIST,
		    "http://dai-labor.de/semstore/music/thebeatles");
		assert entity != null;

		for (final Object metadata: dataset.getAllMetadata(entity))
			System.out.println("Metadata:  " + metadata);

		final File dir = File.createTempFile(RunSemanticTextDataset.class.getSimpleName(), null);

		System.out.printf("Writing to %s\n", dir);

		dir.delete();
		dir.deleteOnExit();

		if (!dir.mkdir()) throw new IOException("mkdir");

		TextWriter.write(dir, dataset);

		dataset = new TextDataset(dir);

		entity = dataset.getEntity(MusicDataset.METADATA_URI, MusicDataset.ARTIST,
		    "http://dai-labor.de/semstore/music/thebeatles");

		assert entity != null;

		for (final Object metadata: dataset.getAllMetadata(entity))
			System.out.println("Metadata:  " + metadata);
	}
}
