package de.dailab.recommender.dataset;

/**
 * A string representing the metadata type.
 * <p>
 * Names are usually lowercase with words connected by dashes.
 * 
 * @author kunegis
 */
public final class MetadataName
{
	/**
	 * The value/name, interned.
	 */
	private final String value;

	/**
	 * A metadata name with a given string.
	 * <p>
	 * There is no restriction on the string (apart from it being nonempty), although it usually is lowercase with words
	 * connected by dashes.
	 * 
	 * @param value The string value.
	 */
	public MetadataName(String value)
	{
		assert value != null && value.length() > 0;
		this.value = value.intern();
	}

	/**
	 * @return The string value of this metadata name
	 */
	public String getValue()
	{
		return value;
	}

	@Override
	public int hashCode()
	{
		return value.hashCode();
	}

	@Override
	public String toString()
	{
		return value;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (!(obj instanceof MetadataName)) return false;
		final MetadataName metadataType = (MetadataName) obj;
		return this.value == metadataType.value;
	}
}
