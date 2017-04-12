package de.dailab.recommender.text;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Date;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.EntitySet;
import de.dailab.recommender.dataset.MetadataName;
import de.dailab.recommender.dataset.RelationshipSet;
import de.dailab.recommender.matrix.FullEntry;

/**
 * Write datasets to text files.
 * 
 * @author kunegis
 */
public class TextWriter
{
	/**
	 * Write a given dataset into text files in a given directory.
	 * 
	 * @param dir The directory to write the files into
	 * @param dataset The dataset to dump
	 * @throws IOException on IO errors
	 */
	public static void write(File dir, Dataset dataset)
	    throws IOException
	{
		for (final EntitySet entitySet: dataset.getEntitySets())
		{
			final File file = new File(dir, String.format("ent.%s", Utils.escapeName(entitySet.getType().getName())));

			writeEntitySet(file, entitySet);
		}

		for (final RelationshipSet relationshipSet: dataset.getRelationshipSets())
		{
			final File file = new File(dir, String.format("rel.%s", Utils.escapeName(relationshipSet.getType()
			    .getName())));

			writeRelationshipSet(file, relationshipSet);
		}
	}

	/**
	 * Write an entity set to a text file.
	 * 
	 * @param file The file to write the entity set to
	 * @param entitySet The entity set to write to a text file
	 * @throws IOException on IO errors
	 */
	public static void writeEntitySet(File file, EntitySet entitySet)
	    throws IOException
	{
		final Writer writer = new BufferedWriter(new FileWriter(file));

		final MetadataName metadataNames[] = new MetadataName[entitySet.getMetadataNames().size()];
		{
			int i = 0;
			for (final MetadataName metadataName: entitySet.getMetadataNames())
				metadataNames[i++] = metadataName;
		}

		/*
		 * Header
		 */
		writer.write("%\n");

		writer.write(String.format("%% %s\n", entitySet.size()));

		writer.write("% ent");
		for (final MetadataName metadataName: metadataNames)
		{
			final Class <?> clazz = entitySet.getMetadataClass(metadataName);
			String typeString;

			if (clazz.equals(String.class))
				typeString = "string";
			else if (clazz.equals(int.class))
				typeString = "int";
			else if (clazz.equals(double.class))
				typeString = "double";
			else if (clazz.equals(Date.class))
				typeString = "date";
			else
			{
				assert false;
				typeString = null;
			}

			if (typeString == null)
				typeString = "";
			else
				typeString = "." + typeString;

			writer.write(" " + String.format("dat%s.%s", typeString, metadataName.getValue()));
		}
		writer.write("\n");

		/*
		 * Data
		 */
		for (int i = 0; i < entitySet.size(); ++i)
		{
			writer.write(Integer.toString(1 + i));
			for (final MetadataName metadataName: metadataNames)
				writer.write(" " + escapeMetadata(entitySet.getMetadata(i, metadataName)));
			writer.write("\n");
		}

		writer.close();
	}

	/**
	 * Write a relationship set to a text file.
	 * 
	 * @param file The file to write the relationship set to
	 * @param relationshipSet The relationship set to write to file
	 * @throws IOException IO errors
	 */
	public static void writeRelationshipSet(File file, RelationshipSet relationshipSet)
	    throws IOException
	{
		final Writer writer = new BufferedWriter(new FileWriter(file));

		/*
		 * Header
		 */
		writer.write(String.format("%% %s %s\n", relationshipSet.getRelationshipFormat().name().toLowerCase(),
		    relationshipSet.getWeightRange().name().toLowerCase()));

		writer.write(String.format("%% %d %d %d\n", relationshipSet.getMatrix().nnz(), relationshipSet.getMatrix()
		    .rows(), relationshipSet.getMatrix().cols()));

		final String weightType = relationshipSet.getMatrix().getWeightType();

		writer.write(String.format("%% ent.%s ent.%s%s\n", relationshipSet.getSubject().getName(), relationshipSet
		    .getObject().getName(), weightType.equals(boolean.class.getSimpleName()) ? "" : String.format(" %s.weight",
		    weightType)));

		/*
		 * Data
		 */
		for (final FullEntry fullEntry: relationshipSet.getMatrix().all())
		{
			if (weightType.equals(boolean.class.getSimpleName()))
				writer.write(String.format("%d %d\n", 1 + fullEntry.rowIndex, 1 + fullEntry.colIndex));
			else
				writer.write(String.format("%d %d %g\n", 1 + fullEntry.rowIndex, 1 + fullEntry.colIndex,
				    fullEntry.value));
		}

		writer.close();
	}

	/**
	 * Format a metadata object for writing into text files.
	 * 
	 * @param object The object to format
	 * @return The formatted representation
	 */
	public static String escapeMetadata(Object object)
	{
		if (object instanceof String)
		{
			final String string = (String) object;
			return String.format("\"%s\"", string.replaceAll("\\\\", "\\\\\\\\").replaceAll("\"", "\\\\\""));
		}

		if (object instanceof Integer) { return ((Integer) object).toString(); }

		if (object instanceof Double) { return ((Double) object).toString(); }

		if (object instanceof Date) { return Long.toString(((Date) object).getTime()); }

		assert false;
		return null;
	}

}
