package de.dailab.recommender.neighborhood;

/**
 * When a similarity measure is not supported by a neighborhood finder.
 * 
 * @author kunegis
 */
public class UnsupportedSimilarityException
    extends Exception
{
	/**
	 * An unsupported similarity exception with a given message.
	 * 
	 * @param message The message
	 */
	public UnsupportedSimilarityException(String message)
	{
		super(message);
	}
}
