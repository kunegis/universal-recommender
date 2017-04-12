package de.dailab.recommender.semantic;

/**
 * The options for loading a semantic store.
 * <p>
 * The default constructor represents the default.
 * 
 * @author kunegis
 */
public class Options
{
	/**
	 * Constructor will all parameters.
	 * 
	 * @param typeMode The type mode
	 */
	public Options(TypeMode typeMode)
	{
		this.typeMode = typeMode;
	}

	/**
	 * Constructor with all default values
	 */
	public Options()
	{
		this(TypeMode.DEFAULT);
	}

	private final TypeMode typeMode;

	/**
	 * @return The type mode
	 */
	public TypeMode getTypeMode()
	{
		return typeMode;
	}
}
