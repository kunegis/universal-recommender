package de.dailab.recommender.text;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

/**
 * Filter filenames with a regular expression.
 * 
 * @author kunegis
 */
class FilenameFilterRegexp
    implements FilenameFilter
{

	/**
	 * @param regexp Regular expression that matches filenames accepted by this filter
	 */
	public FilenameFilterRegexp(String regexp)
	{
		pattern = Pattern.compile(regexp);
	}

	private final Pattern pattern;

	@Override
	public boolean accept(File dir, String name)
	{

		return pattern.matcher(name).matches();
	}
}
