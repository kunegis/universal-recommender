package de.dailab.recommender.semantic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.RDFNode;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntitySet;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.MetadataIndex;
import de.dailab.recommender.dataset.MetadataName;
import de.dailab.recommender.dataset.RelationshipFormat;
import de.dailab.recommender.dataset.RelationshipSet;
import de.dailab.recommender.dataset.RelationshipType;
import de.dailab.recommender.dataset.SingleMetadataIndex;
import de.dailab.recommender.dataset.WeightRange;
import de.dailab.recommender.matrix.template.MatrixFactory;

/**
 * An automatically extracted semantic store dataset. The entity and relationship type are determined automatically. All
 * relationship types are unweighted. The relationship type correspond to the RDF URI. The entities can be all extracted
 * into one entity set or into multiple entity sets based on rdf:type, with corresponding entity types.
 * <p>
 * The resulting dataset has a single entity type ENTITY. Relationship type names correspond to RDF relationship type
 * URIs.
 * <p>
 * This interface to the semantic store is sort of unmaintained since all projects use SemanticStoreDataset, and this
 * class lacks important features, and should be rewritten in terms of SemanticStoreDataset.
 * 
 * @deprecated Use a configuration
 * 
 * @author kunegis
 */
@Deprecated
public class AutomaticSemanticStoreDataset
    extends Dataset
{
	// XXX rewrite as a way to automatically infer SemanticEntityType's and SemanticRelationshipType's.

	private final static String SPARQL_DEFINITIONS = "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n";

	/**
	 * Load the given graph from a semantic store.
	 * 
	 * @param sparqlable The connection to use to access the graph in the semantic store
	 * @param extractEntityTypes whether entity types are to be extracted separately (has no effect yet)
	 * @throws SparqlException On SPARQL errors
	 */
	public AutomaticSemanticStoreDataset(Sparqlable sparqlable, boolean extractEntityTypes)
	    throws SparqlException
	{
		this.extractEntityTypes = extractEntityTypes;

		/*
		 * Entity types: read out the types of all entities.
		 */

		/**
		 * The entity URIs by entity type URI. The index is the entity type URI or NULL for the generic entity type
		 * ENTITY.
		 */
		final Map <String, Set <String>> entityUriSets = new HashMap <String, Set <String>>();

		if (extractEntityTypes)
		{
			final String sparql = String.format(
			    "%s SELECT ?entity ?type WHERE { GRAPH <%s> {?entity rdf:type ?type.}}", SPARQL_DEFINITIONS, sparqlable
			        .getGraphName());
			final Iterator <QuerySolution> iterator = sparqlable.sparql(sparql);

			while (iterator.hasNext())
			{
				final QuerySolution querySolution = iterator.next();

				final String entity = querySolution.get("entity").toString();
				final String type = querySolution.get("type").toString();

				types.put(entity, type);

				Set <String> entityUriSet = entityUriSets.get(type);
				if (entityUriSet == null)
				{
					entityUriSet = new HashSet <String>();
					entityUriSets.put(type, entityUriSet);
				}
				entityUriSet.add(entity);
			}
		}

		/*
		 * Relationship types: collect relationship types by their count and see which entity types must be aggregated.
		 */

		final String sparql = String.format(
		    "%s SELECT ?subject ?relationship ?object WHERE { GRAPH <%s> {?subject ?relationship ?object.}}",
		    SPARQL_DEFINITIONS, sparqlable.getGraphName());

		final Iterator <QuerySolution> iterator = sparqlable.sparql(sparql);

		/**
		 * The relationship types (by RDF type URI) with their count.
		 */
		final Map <String, Integer> relationshipTypeUriCounts = new HashMap <String, Integer>();
		int totalRelationshipCount = 0;

		/**
		 * For each relationship type (by RDF type URI), the list of subject entity types (by URI).
		 * <p>
		 * Only filled when extracting entity types.
		 * <p>
		 * NULL may be present as a value in the set denoting entities whose type has not been declared.
		 */
		final Map <String, Set <String>> subjects = new HashMap <String, Set <String>>();
		final Map <String, Set <String>> objects = new HashMap <String, Set <String>>();

		entityUriSets.put(null, new HashSet <String>());

		while (iterator.hasNext())
		{
			final QuerySolution querySolution = iterator.next();

			final String relationshipTypeUri = querySolution.get("relationship").toString();

			/* Remove "meta" relationships between relationships */
			// XXX make this configurable
			if (relationshipTypeUri.matches("http://www.w3.org/.*")) continue;

			final RDFNode nodeSubject = querySolution.get("subject");
			final RDFNode nodeObject = querySolution.get("object");

			if (nodeSubject.isURIResource() && nodeObject.isURIResource())
			{
				totalRelationshipCount += 1;
				final Integer count = relationshipTypeUriCounts.get(relationshipTypeUri);

				if (count == null)
					relationshipTypeUriCounts.put(relationshipTypeUri, 1);
				else
					relationshipTypeUriCounts.put(relationshipTypeUri, count + 1);

				if (extractEntityTypes)
				{
					Set <String> subjectsList = subjects.get(relationshipTypeUri);
					if (subjectsList == null)
					{
						subjectsList = new HashSet <String>();
						subjects.put(relationshipTypeUri, subjectsList);
					}

					Set <String> objectsList = objects.get(relationshipTypeUri);
					if (objectsList == null)
					{
						objectsList = new HashSet <String>();
						objects.put(relationshipTypeUri, objectsList);
					}

					/*
					 * The RDF type; NULL if not declared.
					 */
					final String subjectTypeUri = types.get(nodeSubject.toString());
					final String objectTypeUri = types.get(nodeObject.toString());

					subjectsList.add(subjectTypeUri);
					objectsList.add(objectTypeUri);
				}
				else
				{
					entityUriSets.get(null).add(nodeSubject.toString());
					entityUriSets.get(null).add(nodeObject.toString());
				}
			}
		}

		if (extractEntityTypes)
		{
			assert relationshipTypeUriCounts.size() == subjects.size();
			assert relationshipTypeUriCounts.size() == objects.size();
		}

		/**
		 * The URI of RDF types that were mapped to the compound entity type. Only filled when extracting entity types.
		 * NULL can be included and denotes entities without a declared RDF type.
		 */
		final Set <String> compoundEntityTypeUris = new HashSet <String>();

		/*
		 * Find which entity types are simple, i.e. only ever appear uniquely as subject and object.
		 */
		if (extractEntityTypes)
		{
			for (final Set <String> stringSet: subjects.values())
			{
				assert stringSet.size() > 0;
				if (stringSet.size() == 1)
					simpleEntityTypeUris.add(stringSet.iterator().next());
				else
					compoundEntityTypeUris.addAll(stringSet);
			}
			for (final Set <String> stringSet: objects.values())
			{
				assert stringSet.size() > 0;
				if (stringSet.size() == 1)
					simpleEntityTypeUris.add(stringSet.iterator().next());
				else
					compoundEntityTypeUris.addAll(stringSet);
			}

			for (final String compoundEntityType: compoundEntityTypeUris)
			{
				simpleEntityTypeUris.remove(compoundEntityType);
			}

			/*
			 * NULL represents untyped entities; they cannot have their own set.
			 */
			simpleEntityTypeUris.remove(null);
		}

		/*
		 * Create entity sets and fill the ID cache.
		 */
		if (extractEntityTypes)
		{
			/*
			 * Simple entity types
			 */
			for (final String entityTypeUri: simpleEntityTypeUris)
			{
				final EntityType entityType = new EntityType(entityTypeUri);
				if (entityType.equals(ENTITY))
				    throw new IllegalArgumentException("Entity type URI must not be " + entityType);
				final EntitySet entitySet = new EntitySet(entityType);
				final Set <String> entityUris = entityUriSets.get(entityTypeUri);
				entitySet.setSize(entityUris.size());
				this.addEntitySet(entitySet);
				final List <MetadataName> metadataNames = new ArrayList <MetadataName>();
				metadataNames.add(METADATA_URI);
				final List <Object> sampleMetadata = new ArrayList <Object>();
				sampleMetadata.add("String");
				entitySet.setMetadataNames(metadataNames, sampleMetadata);

				final SingleMetadataIndex singleIdCache = idCache.getSingleIdCache(entityType);

				for (final String entityUri: entityUris)
				{
					final int id = singleIdCache.add(entityUri);
					entitySet.setMetadata(id, METADATA_URI, entityUri);
				}
			}

			/*
			 * Compound entity type
			 */
			final Set <String> allEntityUris = new HashSet <String>();
			for (final String compoundEntityTypeUri: compoundEntityTypeUris)
			{
				final Set <String> entityUris = entityUriSets.get(compoundEntityTypeUri);
				assert entityUris != null;
				allEntityUris.addAll(entityUris);
			}
			final int n = allEntityUris.size();

			if (n == 0) throw new IllegalArgumentException("No COMPOUND entities found in the semantic store");
			final EntitySet entitySet = new EntitySet(ENTITY);
			this.addEntitySet(entitySet);
			final SingleMetadataIndex singleIdCache = idCache.getSingleIdCache(ENTITY);
			entitySet.setSize(n);
			final List <MetadataName> metadataNames = new ArrayList <MetadataName>();
			metadataNames.add(METADATA_URI);
			final List <Object> sampleMetadata = new ArrayList <Object>();
			sampleMetadata.add("String");
			entitySet.setMetadataNames(metadataNames, sampleMetadata);
			for (final String entityUri: allEntityUris)
			{
				final int id = singleIdCache.add(entityUri);
				entitySet.setMetadata(id, METADATA_URI, entityUri);
			}

		}
		else
		{
			/*
			 * Compound entity type
			 */

			final EntitySet entitySet = new EntitySet(ENTITY);
			this.addEntitySet(entitySet);
			final SingleMetadataIndex singleIdCache = idCache.getSingleIdCache(ENTITY);
			final Set <String> allEntityUris = new HashSet <String>();
			for (final Set <String> entityUris: entityUriSets.values())
			{
				allEntityUris.addAll(entityUris);
			}
			final int n = allEntityUris.size();
			if (n == 0) throw new IllegalArgumentException("No entities found in the semantic store");
			entitySet.setSize(n);
			final List <MetadataName> metadataNames = new ArrayList <MetadataName>();
			metadataNames.add(METADATA_URI);
			final List <Object> sampleMetadata = new ArrayList <Object>();
			sampleMetadata.add("String");
			entitySet.setMetadataNames(metadataNames, sampleMetadata);
			for (final String entityUri: allEntityUris)
			{
				final int id = singleIdCache.add(entityUri);
				entitySet.setMetadata(id, METADATA_URI, entityUri);
			}
		}

		/**
		 * The relationship sets by their relationship type URI.
		 */
		final Map <String, RelationshipSet> relationshipSets = new HashMap <String, RelationshipSet>();

		/*
		 * Relationships
		 */
		for (final Entry <String, Integer> e: relationshipTypeUriCounts.entrySet())
		{
			final String relationshipTypeUri = e.getKey();
			final int count = e.getValue();

			/*
			 * Aggregate small relationship types to avoid lots of extremely sparse matrices, which take too much
			 * memory.
			 */

			if (count < totalRelationshipCount * AGGREGATION_THRESHOLD)
			{
				/* nothing */
			}
			else
			{
				final RelationshipType relationshipType = new RelationshipType(relationshipTypeUri);

				EntityType subject, object;

				if (extractEntityTypes)
				{
					final Set <String> subjectUris = subjects.get(relationshipTypeUri);
					final String subjectUri = subjectUris.iterator().next();
					subject = getEntityType(subjectUri);

					final Set <String> objectUris = objects.get(relationshipTypeUri);
					final String objectUri = objectUris.iterator().next();
					object = getEntityType(objectUri);
				}
				else
				{
					subject = ENTITY;
					object = ENTITY;
				}

				final RelationshipSet relationshipSet = new RelationshipSet(relationshipType, subject, object, subject
				    .equals(object) ? RelationshipFormat.ASYM : RelationshipFormat.BIP, WeightRange.UNWEIGHTED);
				this.addRelationshipSet(relationshipSet);
				relationshipSet.setMatrix(MatrixFactory.newMemoryMatrixUnweighted(this.getEntitySet(subject).size(),
				    this.getEntitySet(object).size()));
				relationshipSets.put(relationshipTypeUri, relationshipSet);
			}
		}

		final Iterator <QuerySolution> iterator2 = sparqlable.sparql(sparql);

		while (iterator2.hasNext())
		{
			final QuerySolution querySolution = iterator2.next();

			final String relationshipTypeUri = querySolution.get("relationship").toString();

			final RDFNode nodeSubject = querySolution.get("subject");
			final RDFNode nodeObject = querySolution.get("object");

			if (nodeSubject.isURIResource() && nodeObject.isURIResource())
			{
				final RelationshipSet relationshipSet = relationshipSets.get(relationshipTypeUri);

				/* Relationship was excluded above (e.g. for being meta) */
				if (relationshipSet == null) continue;

				final String subjectUri = nodeSubject.toString();
				final String objectUri = nodeObject.toString();

				final int subjectId = idCache.getSingleIdCache(getEntityType(types.get(subjectUri))).getId(subjectUri);
				final int objectId = idCache.getSingleIdCache(getEntityType(types.get(objectUri))).getId(objectUri);
				relationshipSet.getMatrix().set(subjectId, objectId, 1);
			}
		}
	}

	/**
	 * A flat semantic store dataset read from the given connection. All entities are grouped into one entity type.
	 * 
	 * @param semanticStoreConnection The semantic store connection to use
	 * @throws SparqlException On SPARQL errors
	 */
	public AutomaticSemanticStoreDataset(SemanticStoreConnection semanticStoreConnection)
	    throws SparqlException
	{
		this(semanticStoreConnection, false);
	}

	/**
	 * Get an entity by its URI.
	 * 
	 * @param uri The URI of the entity to return
	 * @return The entity of that URI
	 * @throws NoSuchElementException When there is no entity with this URI
	 */
	public Entity getEntity(String uri)
	{
		// XXX use the correct entity set

		return new Entity(ENTITY, idCache.getSingleIdCache(getEntityType(types.get(uri))).getId(uri));
	}

	/**
	 * In this mode, entity types are extracted from RDF types.
	 */
	private final boolean extractEntityTypes;

	/**
	 * Cache of RDF URIs. All objects are Strings representing URIs.
	 */
	private final MetadataIndex idCache = new MetadataIndex();

	/**
	 * The entity type URIs that have their own entity set.
	 */
	private final Set <String> simpleEntityTypeUris = new HashSet <String>();

	/**
	 * The types of entities. Key: URI of entity; value: URI of RDF type.
	 * <p>
	 * Only filled when extracting entity types. Only contains those entities whose types is declared by an explicit
	 * rdf:type.
	 */
	final Map <String, String> types = new HashMap <String, String>();

	/**
	 * The single entity type for all entities in this dataset when entity types are not extracted.
	 */
	public final static EntityType ENTITY = new EntityType("entity");

	/**
	 * Relationship types with very few relationships are aggregated into this relationship type.
	 */
	public final static RelationshipType AGGREGATE_RELATION = new RelationshipType("semantic:aggregate");

	/**
	 * The full URI of any entity.
	 */
	public final static MetadataName METADATA_URI = new MetadataName("semantic:uri");

	/**
	 * Relationship types that have less than this number multiplied by the entity count of relations are aggregated in
	 * the catch-all relationship type AGGREGATE_RELATION.
	 */
	private final static double AGGREGATION_THRESHOLD = .04;

	/**
	 * Get the entity type of an entity by its URI.
	 * 
	 * @param entityTypeUri The URI of the entity
	 * @return The entity type of the entity represented by the given URI
	 */
	public EntityType getEntityType(String entityTypeUri)
	{
		if (!extractEntityTypes) { return ENTITY; }

		assert entityTypeUri != null;
		return simpleEntityTypeUris.contains(entityTypeUri) ? new EntityType(entityTypeUri) : ENTITY;
	}
}
