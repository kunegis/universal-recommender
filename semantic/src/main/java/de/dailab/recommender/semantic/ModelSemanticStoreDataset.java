package de.dailab.recommender.semantic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.MetadataName;
import de.dailab.recommender.dataset.RelationshipFormat;
import de.dailab.recommender.dataset.RelationshipType;
import de.dailab.recommender.dataset.WeightRange;

/**
 * A semantic store dataset configured by a Jena model.
 * 
 * @author kunegis
 * 
 * @see com.hp.hpl.jena.rdf.model.Model
 */
public class ModelSemanticStoreDataset
    extends SemanticStoreDataset
{
	/**
	 * Load a dataset from a semantic store, using the sparqlable interface. The ontology is defined in the given Jena
	 * model.
	 * 
	 * @param sparqlable The connection to the semantic store
	 * @param jenaModel The ontology
	 * @throws SparqlException on SPARQL errors
	 */
	public ModelSemanticStoreDataset(Sparqlable sparqlable, com.hp.hpl.jena.rdf.model.Model jenaModel)
	    throws SparqlException
	{
		super(sparqlable, jenaModelToModel(jenaModel));
	}

	@SuppressWarnings("unchecked")
	private static Model jenaModelToModel(com.hp.hpl.jena.rdf.model.Model jenaModel)
	{
		final StmtIterator stmtIterator = jenaModel.listStatements();

		final Map <EntityType, SemanticEntityType> semanticEntityTypes = new HashMap <EntityType, SemanticEntityType>();

		final List <SemanticRelationshipType> semanticRelationshipTypes = new ArrayList <SemanticRelationshipType>();

		while (stmtIterator.hasNext())
		{
			final Statement statement = stmtIterator.nextStatement();

			final Property property = statement.getPredicate();

			final Resource subject = statement.getSubject();
			final RDFNode object = statement.getObject();

			System.out.println(String.format("(%s) %s %s %s", object.isResource() ? "resource" : "literal", subject,
			    property, object));

			final EntityType subjectEntityType = new EntityType(subject.getURI());

			SemanticEntityType subjectSemanticEntityType = semanticEntityTypes.get(subjectEntityType);
			if (subjectSemanticEntityType == null)
			{
				subjectSemanticEntityType = new RdfEntityType(subjectEntityType, new RdfName(String.format("<%s>",
				    subject.getURI())), null);
				semanticEntityTypes.put(subjectEntityType, subjectSemanticEntityType);
			}

			/*
			 * Distinguish between statement-object and literal-object.
			 */
			if (object.isResource())
			{
				final String relationshipUri = property.getURI();

				if (relationshipUri.matches("^http://www.w3.org/.*")) continue;

				/* Entity type */

				final String uri = ((Resource) object.as(Resource.class)).getURI();

				final EntityType objectEntityType = new EntityType(uri);

				SemanticEntityType objectSemanticEntityType = semanticEntityTypes.get(subjectEntityType);

				if (objectSemanticEntityType == null)
				{
					objectSemanticEntityType = new RdfEntityType(objectEntityType, new RdfName(uri),
					    new SemanticMetadata[] {});
					semanticEntityTypes.put(objectEntityType, objectSemanticEntityType);
				}

				/* Add object entity type and relationship type */

				final RelationshipFormat relationshipFormat = subjectEntityType.equals(objectEntityType) ? RelationshipFormat.ASYM
				    : RelationshipFormat.BIP;

				final String rdfName = jenaModel.shortForm(relationshipUri);

				final SemanticRelationshipType semanticRelationshipType = new SemanticRelationshipType(
				    new RelationshipType(relationshipUri), new RdfName(rdfName), subjectEntityType, objectEntityType,
				    relationshipFormat, WeightRange.UNWEIGHTED);

				semanticRelationshipTypes.add(semanticRelationshipType);
			}
			else if (object.isLiteral())
			{
				/* Metadata */

				final String metadataUri = property.getURI();
				if (metadataUri.matches("^http://www.w3.org/.*")) continue;

// final RDFDatatype rdfDataType = ((Literal) object.as(Literal.class)).getDatatype();

				subjectSemanticEntityType.metadata.add(new SemanticMetadata(new MetadataName(metadataUri), new RdfName(
				    String.format("<%s>", metadataUri))));
			}
		}
		stmtIterator.close();

		final SemanticEntityType semanticEntityTypeArray[] = new SemanticEntityType[semanticEntityTypes.size()];
		{
			int i = 0;
			for (final SemanticEntityType semanticEntityType: semanticEntityTypes.values())
			{
				semanticEntityTypeArray[i++] = semanticEntityType;
			}
		}

		final SemanticRelationshipType semanticRelationshipTypeArray[] = new SemanticRelationshipType[semanticRelationshipTypes
		    .size()];
		{
			int i = 0;
			for (final SemanticRelationshipType semanticRelationshipType: semanticRelationshipTypes)
			{
				semanticRelationshipTypeArray[i++] = semanticRelationshipType;
			}
		}

		String sparqlDefinitions = "";

		sparqlDefinitions += "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n";

		for (final Object o: jenaModel.getNsPrefixMap().entrySet())
		{
			final Entry <String, String> e = (Entry <String, String>) o;
			sparqlDefinitions = sparqlDefinitions + String.format("PREFIX %s: <%s>\n", e.getKey(), e.getValue());
		}

		final Model ret = new Model(semanticEntityTypeArray, semanticRelationshipTypeArray, Mode.IGNORE,
		    sparqlDefinitions);

		return ret;
	}
}
