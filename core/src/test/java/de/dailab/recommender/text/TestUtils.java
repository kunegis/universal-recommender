package de.dailab.recommender.text;

import org.junit.Test;

/**
 * Test the text dataset utilities.
 * 
 * @author kunegis
 */
public class TestUtils
{
	/**
	 * Escape and unescape all "special" characters.
	 */
	@Test
	public void test()
	{
		final String names[][] = new String[][]
		{
		{ "abcd", "abcd" },
		{ "abc\\abc", "abc\\\\abc" },
		{ "abc/abc", "abc\\sabc" },
		{ "abc.abc", "abc\\dabc" },
		{ "abc\nabc", "abc\\nabc" }, };

		for (int i = 0; i < names.length; ++i)
		{
			final String escaped = names[i][1];
			final String unescaped = names[i][0];

			final String escapedComputed = Utils.escapeName(unescaped);
			final String unescapedComputed = Utils.unescapeName(escaped);

			assert escaped.equals(escapedComputed);
			assert unescaped.equals(unescapedComputed);
		}
	}
}
