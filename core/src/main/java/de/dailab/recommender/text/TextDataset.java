package de.dailab.recommender.text;

import java.io.File;
import java.io.IOException;

import de.dailab.recommender.dataset.Dataset;

/**
 * A dataset read from text files in the format used by the Graph Store.
 * <p>
 * Files represent the sparse adjacency matrix of a unipartite or bipartite network. All networks are directed. Some are
 * weighted.
 * 
 * @author kunegis
 * 
 * @see <a href = "https://wiki.dai-labor.de/Graph_Store">[[Graph Store]]</a>
 */
public class TextDataset
    extends Dataset
{
	/**
	 * Load a graph dataset from its directory.
	 * 
	 * @param dir directory containing the ent.* and rel.* files.
	 * @throws IOException while reading the files
	 * @throws TextSyntaxException in one of the files
	 */
	public TextDataset(File dir)
	    throws IOException, TextSyntaxException
	{
		/*
		 * Entities
		 */
		for (final File fileEnt: dir.listFiles(new FilenameFilterRegexp("ent\\..*[^~]")))
			addEntitySet(TextReader.loadEntitySet(fileEnt));

		/*
		 * Relationships
		 */
		for (final File fileRel: dir.listFiles(new FilenameFilterRegexp("rel\\..*[^~]")))
			addRelationshipSet(TextReader.readRelationshipSet(fileRel));
	}
}
