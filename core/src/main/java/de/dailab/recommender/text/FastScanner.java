package de.dailab.recommender.text;

import java.util.InputMismatchException;

/**
 * Fast alternative to the java.Scanner that is fast and only has the features we need.
 * 
 * @author kunegis
 */
class FastScanner
{
	public FastScanner(String string)
	{
		assert string != null;
		this.string = string;
	}

	/**
	 * Scan the next unsigned int.
	 * 
	 * @return the scanned unsigned integer
	 * @throws InputMismatchException if end of string is reached
	 */
	public int nextUnsigned()
	{
		skipSpace();

		final int oIndex = index;

		int ret = 0;
		char nextChar;

		while (index < string.length() && (nextChar = string.charAt(index)) >= '0' && nextChar <= '9')
		{
			ret *= 10;
			ret += nextChar - '0';
			++index;
		}

		if (oIndex == index) throw new InputMismatchException("End of string");

		return ret;
	}

	/**
	 * @return The enxt nonspace sequence, or NULL
	 */
	public String nextNonspace()
	{
		skipSpace();
		final int oIndex = index;
		while (index < string.length() && !Character.isSpaceChar(string.charAt(index)))
			++index;
		if (oIndex == index) return null;
		return string.substring(oIndex, index);
	}

	public void skipSpace()
	{
		while (index < string.length() && Character.isWhitespace(string.charAt(index)))
			++index;
	}

	private int index = 0;
	private final String string;
}
