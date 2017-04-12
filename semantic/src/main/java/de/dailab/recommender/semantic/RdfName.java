package de.dailab.recommender.semantic;

/**
 * A name used in RDF as a resource or property. This is a wrapper around String that accepts only valid RDF names.
 * <p>
 * RDF names may be full URIs enclosed in angle brackets, or short forms using prefixes without angle brackets.
 * <p>
 * Valid RDF names are for instance "&lt;http://purl.org/ontology/po/ProgrammeItem&gt;" or "foaf:Agent".
 * <p>
 * Names starting with "http:" are not accepted, although there <i>could</i> be such a prefix.
 * 
 * @author kunegis
 */
public class RdfName
{
	/**
	 * An RDF name. May be a URI enclosed in angle brackets, or may use a prefix, without angle-brackets.
	 * 
	 * @param name The RDF name; must only contains letters, digits, underscore, dash, slash, period, colons and the
	 *        greater-than/less-than symbols.
	 * 
	 * @throws IllegalArgumentException When the name is invalid
	 */
	public RdfName(String name)
	{
		if (!name.matches("[a-zA-Z0-9_:/.<>-]+")) throw new IllegalArgumentException("Invalid RDF name " + name);

		if (name.matches("http:.*"))
		    throw new IllegalArgumentException("RdfNames that are URIs must be enclosed in angle brackets.");

		this.name = name;
	}

	@Override
	public String toString()
	{
		return name;
	}

	/**
	 * The name. In valid RDF syntax.
	 */
	public final String name;
}
