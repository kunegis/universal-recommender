package de.dailab.recommender.text;

/**
 * Indicates a syntax error in a graph file.
 * 
 * @author kunegis
 */
public class TextSyntaxException
    extends Exception
{
	TextSyntaxException(String message)
	{
		super(message);
	}

	TextSyntaxException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
