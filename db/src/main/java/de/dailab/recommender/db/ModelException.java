package de.dailab.recommender.db;

/**
 * An invalid data model in the database.
 * 
 * @author kunegis
 * 
 */
public class ModelException
    extends Exception
{
	ModelException(String message)
	{
		super(message);
	}

	ModelException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
