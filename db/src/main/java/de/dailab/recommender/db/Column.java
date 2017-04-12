package de.dailab.recommender.db;

/**
 * The name of a database column.
 * 
 * @author kunegis
 */
public class Column
    extends Name
{
	/**
	 * Create a database column name.
	 * 
	 * @param name The name. Only safe characters are allowed.
	 */
	public Column(String name)
	{
		super(name);
	}
}
