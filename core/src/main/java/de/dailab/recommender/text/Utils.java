package de.dailab.recommender.text;

/**
 * Static utilities for text datasets.
 * 
 * @author kunegis
 */
public class Utils
{
	/**
	 * Sanitize a relationship of entity name for usage as filename parts. Slashes, dots and NULs are escaped.
	 * 
	 * @param name The original name, possibly containing slashes, dots and NULs
	 * @return The sanitized name without slashes, dots or NULs
	 */
	public static String escapeName(String name)
	{
		return name.replaceAll("\\\\", "\\\\\\\\").replaceAll("\0", "\\\\0").replaceAll("/", "\\\\s").replaceAll("\\.",
		    "\\\\d").replaceAll("\n", "\\\\n");
	}

	/**
	 * Unescape relationship or entity names.
	 * 
	 * @param name The escaped name
	 * @return The unescaped name
	 */
	public static String unescapeName(String name)
	{
		return name.replaceAll("\\\\n", "\n").replaceAll("\\\\d", ".").replaceAll("\\\\s", "/").replaceAll("\\\\0",
		    "\0").replaceAll("\\\\\\\\", "\\\\");

	}
}
