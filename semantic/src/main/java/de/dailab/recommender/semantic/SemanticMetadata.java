package de.dailab.recommender.semantic;

import de.dailab.recommender.dataset.MetadataName;

/**
 * The name/rdfName pair of a metadata name in a Semantic Store.
 * 
 * @author kunegis
 */
public class SemanticMetadata
{
	/**
	 * A semantic metadata name/rdfName pair.
	 * 
	 * @param name The metadata name
	 * @param rdfName The name of the metadata as used in the Semantic Store
	 */
	public SemanticMetadata(MetadataName name, RdfName rdfName)
	{
		this.name = name;
		this.rdfName = rdfName;
	}

	/**
	 * The name of the metadata.
	 */
	public final MetadataName name;

	/**
	 * The RDF name of the metadata.
	 */
	public final RdfName rdfName;
}
