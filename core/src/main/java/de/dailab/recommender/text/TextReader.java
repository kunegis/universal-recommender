package de.dailab.recommender.text;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.dailab.recommender.dataset.EntitySet;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.MetadataName;
import de.dailab.recommender.dataset.RelationshipFormat;
import de.dailab.recommender.dataset.RelationshipSet;
import de.dailab.recommender.dataset.RelationshipType;
import de.dailab.recommender.dataset.WeightRange;
import de.dailab.recommender.matrix.Matrix;
import de.dailab.recommender.matrix.template.MatrixFactory;

/**
 * Utility for reading datasets in text files. These functions load single entity sets and relationship sets. To load a
 * full dataset from text files, use {@link TextDataset}.
 * <p>
 * The format is the one used in the Graph Store.
 * <p>
 * Since the relationship type names are used in filenames, they are escaped using the Utils class.
 * 
 * @see TextWriter
 * @see TextDataset
 * 
 * @author kunegis
 */
public class TextReader
{
	/**
	 * Load a relationship set from a text file.
	 * <p>
	 * Subject and object type are NULL if they are not specified in the text file.
	 * 
	 * @param file The relationship file; usually called "out.*" or rel.*"
	 * 
	 * @return The corresponding relationship set
	 * 
	 * @throws IOException IO error while reading the file
	 * @throws TextSyntaxException Syntax error in the dataset file
	 */
	public static RelationshipSet readRelationshipSet(File file)
	    throws IOException, TextSyntaxException
	{
		final String basename = file.getName();
		Matcher matcher = Pattern.compile("(?:rel|out)\\.(.*)").matcher(basename);
		if (!matcher.matches())
		    throw new IllegalArgumentException(String.format("Relationship filename does not match {rel,out}.*:  %s",
		        basename));
		final String relationshipTypeName = matcher.group(1);
		final RelationshipType relationshipType = new RelationshipType(Utils.unescapeName(relationshipTypeName));

		final TextReader graphReader = new TextReader(file);

		/*
		 * Header The first line has to be present. Lines 2 and 3 are optional.
		 */
		final int HEADER_LINE_COUNT = 3;
		final String lines[] = new String[HEADER_LINE_COUNT];

		int headerLineCount;
		for (headerLineCount = 0; headerLineCount < HEADER_LINE_COUNT; ++headerLineCount)
		{
			lines[headerLineCount] = graphReader.readLine();
			if (lines[headerLineCount] == null) break;
			if (!lines[headerLineCount].matches("% .*"))
			{
				graphReader.reset();
				break;
			}
		}

		/*
		 * Line 1
		 */
		if (headerLineCount < 1) throw graphReader.exception("Invalid header");

		final Matcher matcher1 = Pattern.compile("% ([a-z]+) ([a-z]+)").matcher(lines[0]);
		if (!matcher1.matches()) throw graphReader.exception("Invalid header line");
		final String relationshipFormatString = matcher1.group(1).toUpperCase();
		final String weightRangeString = matcher1.group(2).toUpperCase();
		RelationshipFormat relationshipFormat;
		WeightRange weightRange;
		try
		{
			relationshipFormat = Enum.valueOf(RelationshipFormat.class, relationshipFormatString);
		}
		catch (final IllegalArgumentException illegalArgumentException)
		{
			throw graphReader.exception(String.format("Invalid format type:  %s", relationshipFormatString));
		}
		try
		{
			weightRange = Enum.valueOf(WeightRange.class, weightRangeString);
		}
		catch (final IllegalArgumentException illegalArgumentException)
		{
			throw graphReader.exception(String.format("Invalid weight type:  %s", weightRangeString));
		}

		/*
		 * Line 3
		 */

		EntityType subjectType = null;
		EntityType objectType = null;

		/** Weight type or NULL is generic */
		String weightType = null;

		if (headerLineCount >= 3)
		{
			matcher = Pattern
			    .compile(
			        "%\\s+ent\\.([a-z0-9-]+)\\s+ent\\.([a-z0-9-]+)(|\\s+(weight|double|float|int|short|byte)\\.([a-z0-9-]+))(\\s+dat\\.(\\S+))*\\s*")
			    .matcher(lines[2]);
			if (!matcher.matches()) throw graphReader.exception("Invalid header line");
			final String entityTypeSubject = matcher.group(1);
			final String entityTypeObject = matcher.group(2);

			final String weightTypeString = matcher.group(4);
			if (weightTypeString != null && !weightTypeString.equals("weight")) weightType = weightTypeString;

			@SuppressWarnings("unused")
			final String weightName = matcher.group(3);
			subjectType = new EntityType(entityTypeSubject);
			objectType = new EntityType(entityTypeObject);
		}
		else
		/* Set default subject and object types */
		{
			switch (relationshipFormat)
			{
			default:
				throw new UnsupportedOperationException("Unsupported relationship format");
			case SYM:
			case ASYM:
				subjectType = DEFAULT_SINGLE_ENTITY_TYPE;
				objectType = DEFAULT_SINGLE_ENTITY_TYPE;
				break;
			case BIP:
				subjectType = DEFAULT_SUBJECT_ENTITY_TYPE;
				objectType = DEFAULT_OBJECT_ENTITY_TYPE;
			}
		}

		if (weightRange == WeightRange.UNWEIGHTED) weightType = boolean.class.getSimpleName();

		/* Line 2 */
		/* The files contains indices beginning at 1 */
		int rowMax = -1, colMax = -1;
		if (headerLineCount >= 2)
		{
			matcher = Pattern.compile("% ([0-9]+) ([0-9]+) ([0-9]+)").matcher(lines[1]);
			if (!matcher.matches()) throw graphReader.exception("Invalid counts");
			try
			{
				@SuppressWarnings("unused")
				final int relationshipCount = Integer.parseInt(matcher.group(1));
				rowMax = Integer.parseInt(matcher.group(2));
				colMax = Integer.parseInt(matcher.group(3));
			}
			catch (final NumberFormatException numberFormatException)
			{
				throw graphReader.exception(numberFormatException);
			}

		}
		else
		{
			/*
			 * If line 2 of the header is not present, parse the whole file to get the line/subject/object count.
			 */
			while (graphReader.readLine() != null)
			{
				/* ignore other header lines forward-compatibly */
				if (graphReader.getLine().charAt(0) == '%') continue;
				final FastScanner scanner = new FastScanner(graphReader.getLine());
				try
				{
					final int row = scanner.nextUnsigned();
					final int col = scanner.nextUnsigned();
					rowMax = Math.max(row, rowMax);
					colMax = Math.max(col, colMax);
				}
				catch (final InputMismatchException inputMismatchException)
				{
					throw graphReader.exception(inputMismatchException);
				}
			}
			graphReader.reset();

			if (relationshipFormat == RelationshipFormat.ASYM || relationshipFormat == RelationshipFormat.SYM)
			{
				rowMax = Math.max(rowMax, colMax);
				colMax = rowMax;
			}
		}

		final int rowCount = rowMax, colCount = colMax;

		/*
		 * Load
		 */
		final Matrix matrix = MatrixFactory.newMemoryMatrix(rowCount, colCount, weightType);

		while (graphReader.readLine() != null)
		{
			if (graphReader.getLine().charAt(0) == '%') continue;

			final FastScanner fastScanner = new FastScanner(graphReader.getLine());
			final int subject = fastScanner.nextUnsigned();
			final int object = fastScanner.nextUnsigned();

			final String weightString = fastScanner.nextNonspace();

			try
			{
				final int row = -1 + subject;
				final int col = -1 + object;

				final double weight = weightString == null ? 1. : Double.parseDouble(weightString);

				matrix.set(row, col, weight);
			}
			catch (final NumberFormatException numberFormatException)
			{
				throw graphReader.exception(String.format("Parsing weight \"%s\":  %s", weightString,
				    numberFormatException));
			}
		}

		final RelationshipSet ret = new RelationshipSet(relationshipType, subjectType, objectType, relationshipFormat,
		    weightRange);
		ret.setMatrix(matrix);

		return ret;
	}

	/**
	 * Read the header of an entity file
	 * 
	 * @param file entity file. The basename must match ent.* for the entity to be parsed.
	 * @return The entity type info
	 * @throws TextSyntaxException in the file
	 * @throws IOException on reading the file
	 */
	public static EntitySet loadEntitySet(File file)
	    throws TextSyntaxException, IOException
	{
		EntitySet ret;

		/*
		 * Filename
		 */
		{
			final String basename = file.getName();
			final Matcher matcher = Pattern.compile("ent\\.(.*)").matcher(basename);
			if (!matcher.matches())
			    throw new IllegalArgumentException(String.format("Entity filename does not match ent.*:  %s", basename));
			final String entityTypeName = matcher.group(1);
			ret = new EntitySet(new EntityType(Utils.unescapeName(entityTypeName)));
		}

		/*
		 * File
		 */
		final TextReader textReader = new TextReader(file);

		final int HEADER_LINE_COUNT = 3;
		final String lines[] = new String[HEADER_LINE_COUNT];

		for (int i = 0; i < HEADER_LINE_COUNT; ++i)
		{
			do
				lines[i] = textReader.readLine();
			while (lines[i].matches("\\s*"));
			if (lines[i] == null) throw textReader.exception("Header too short");
		}

		final Matcher matcher1 = Pattern.compile("\\s*%\\s*([0-9]+)\\s*").matcher(lines[1]);
		if (!matcher1.matches()) throw textReader.exception("Invalid entity count");
		if (matcher1.groupCount() != 1) throw textReader.exception("Invalid entity count");
		final String countString = matcher1.group(1);
		try
		{
			ret.setSize(Integer.parseInt(countString));
		}
		catch (final NumberFormatException numberFormatException)
		{
			throw textReader.exception(numberFormatException);
		}

		Matcher matcher2 = Pattern.compile("\\s*%\\s*ent(.*)").matcher(lines[2]);
		if (!matcher2.matches()) throw textReader.exception("Invalid metadata declaration");

		final List <MetadataName> metadataNames = new ArrayList <MetadataName>();

		/** The declared type or NULL. */
		final List <String> metadataTypeStrings = new ArrayList <String>();

		matcher2 = Pattern.compile("\\s+dat(?:\\.([a-z]+))?\\.(\\S+)").matcher(matcher2.group(1));

		while (matcher2.lookingAt())
		{
			metadataTypeStrings.add(matcher2.group(1));
			final String metadataNameString = matcher2.group(2);
			metadataNames.add(new MetadataName(metadataNameString));
			matcher2.region(matcher2.end(), matcher2.regionEnd());
		}

		/*
		 * Read first metadata line to get the metadata types.
		 */
		String line;
		while (true)
		{
			line = textReader.readLine();
			if (line == null) throw textReader.exception("No entities found");
			if (!(line.matches("\\s*%.*") || line.matches("\\s*"))) break;
		}

		final int id[] =
		{ 0 };
		List <Object> metadata = textReader.parseEntityLine(line, id);
		if (metadata.size() != metadataNames.size())
		    throw textReader.exception(String.format("Wrong number of metadata:  %d, needed %d", metadata.size(),
		        metadataNames.size()));

		for (int i = 0; i < metadata.size(); ++i)
		{
			final String metadataTypeString = metadataTypeStrings.get(i);
			if (metadataTypeString != null)
			{
				if (metadataTypeString.equals("string"))
					metadata.set(i, "String");
				else if (metadataTypeString.equals("int"))
					metadata.set(i, Integer.valueOf(0));
				else if (metadataTypeString.equals("double"))
					metadata.set(i, Double.valueOf(0.));
				else if (metadataTypeString.equals("date"))
					metadata.set(i, new Date());
				else
					throw textReader.exception(String.format("Invalid metadata type %s", metadataTypeString));
			}
		}

		ret.setMetadataNames(metadataNames, metadata);

		/*
		 * Read actual metadata
		 */
		textReader.reset();

		while (true)
		{
			line = textReader.readLine();
			if (line == null) break;
			if (line.matches("\\s*%.*")) continue;
			if (line.matches("\\s*")) continue;
			metadata = textReader.parseEntityLine(line, id);
			ret.setMetadataValues(id[0] - 1, metadata);
		}

		return ret;
	}

	private class MyTextSyntaxException
	    extends TextSyntaxException
	{
		public MyTextSyntaxException()
		{
			super(String.format("%s:%s:  Syntax error:  %s", file.getPath(), lineNumber, line));
		}

		public MyTextSyntaxException(Throwable cause)
		{
			super(String.format("%s:%s:  Syntax error:  %s", file.getPath(), lineNumber, line), cause);
		}

		public MyTextSyntaxException(String message)
		{
			super(String.format("%s:%s:  %s:  %s", file.getPath(), lineNumber, message, line));
		}

		public MyTextSyntaxException(String message, Throwable cause)
		{
			super(String.format("%s:%s:  %s:  %s", file.getPath(), lineNumber, message, line), cause);
		}
	}

	private static final Pattern PATTERN_METADATA = Pattern
	    .compile("\\s+(?:(-?\\p{Digit}[^\\s]*)|([^\\s\\p{Digit}\"][^\\s]*)|\"((?:[^\"\\\\]|\\\\\\\\|\\\\\")*)\")");
	private static final Pattern PATTERN_DATALINE = Pattern.compile("\\s*([0-9]+)(.*)");

	/**
	 * Parse one content line of an entity file.
	 * 
	 * @param id (out) the entity ID. As found in the file, beginning at 1.
	 * @return The metadata in order of the types prescribed, with simple types replaced with their object type
	 *         equivalent.
	 */
	private List <Object> parseEntityLine(String line, int id[/* 1 */])
	    throws TextSyntaxException
	{
		final Matcher matcher = PATTERN_DATALINE.matcher(line);

		if (!matcher.matches()) throw new MyTextSyntaxException("Invalid entity ID");

		try
		{
			id[0] = Integer.valueOf(matcher.group(1));
		}
		catch (final NumberFormatException numberFormatException)
		{
			throw new MyTextSyntaxException(numberFormatException);
		}

		final String metadata = matcher.group(2);

		return parseMetadata(metadata);
	}

	private List <Object> parseMetadata(String line)
	    throws TextSyntaxException
	{
		final List <Object> ret = new ArrayList <Object>();

		final Matcher matcher = PATTERN_METADATA.matcher(line);

		while (matcher.lookingAt())
		{

			final String numberString = matcher.group(1);
			final String unquotedString = matcher.group(2);
			final String quotedString = matcher.group(3);

			if (numberString != null)
			{
				try
				{
					final double number = Double.parseDouble(numberString);
					ret.add(Double.valueOf(number));
				}
				catch (final NumberFormatException numberFormatException)
				{
					throw new MyTextSyntaxException(numberFormatException);
				}
			}
			else if (unquotedString != null)
			{
				ret.add(unquotedString);
			}

			else if (quotedString != null)
			{
				final String string = quotedString.replaceAll("\\\\\"", "\"").replaceAll("\\\\\\\\", "\\\\");
				ret.add(string);
			}

			matcher.region(matcher.end(), matcher.regionEnd());

		}
		if (!matcher.hitEnd()) throw new MyTextSyntaxException("Syntax error");

		return ret;

	}

	/*
	 * Object usage: current file, line and line number.
	 */
	private final File file;

	/** Last line read or NULL */
	private String line = null;

	/** Number of last line read, beginning at 1, 0 when nothing was read yet. */
	private int lineNumber;

	private FileReader fileReader;
	private BufferedReader bufferedReader;

	private TextReader(File file)
	    throws FileNotFoundException
	{
		this.file = file;
		fileReader = new FileReader(file);
		bufferedReader = new BufferedReader(fileReader);
		lineNumber = 0;
	}

	/**
	 * Read next line.
	 * 
	 * @return NULL at end of file.
	 */
	private String readLine()
	    throws IOException
	{
		line = bufferedReader.readLine();
		if (line != null) ++lineNumber;
		return line;
	}

	private void reset()
	    throws FileNotFoundException
	{
		lineNumber = 0;
		fileReader = new FileReader(file);
		bufferedReader = new BufferedReader(fileReader);
	}

	/**
	 * @return The last line read or NULL when at the beginning.
	 */
	private String getLine()
	{
		return line;
	}

	private TextSyntaxException exception(String message)
	{
		return new MyTextSyntaxException(message);
	}

	private TextSyntaxException exception(Throwable cause)
	{
		return new MyTextSyntaxException(cause);
	}

	private static final EntityType DEFAULT_SINGLE_ENTITY_TYPE = new EntityType("entity");
	private static final EntityType DEFAULT_SUBJECT_ENTITY_TYPE = new EntityType("subject");
	private static final EntityType DEFAULT_OBJECT_ENTITY_TYPE = new EntityType("object");
}
