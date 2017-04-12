package de.dailab.recommender.semantic;

/**
 * A SPARQL exception.
 * 
 * @author kunegis
 */
public class SparqlException
    extends Exception
{
	/**
	 * A SPARQL exception with the given cause.
	 * 
	 * @param cause The underlying cause
	 */
	public SparqlException(Throwable cause)
	{
		super(cause);
	}
}
