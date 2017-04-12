package de.dailab.recommender.semantic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntitySet;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.MetadataIndex;
import de.dailab.recommender.dataset.MetadataName;
import de.dailab.recommender.dataset.RelationshipSet;
import de.dailab.recommender.dataset.RelationshipType;
import de.dailab.recommender.dataset.SingleMetadataIndex;
import de.dailab.recommender.matrix.Matrix;

/**
 * A dataset read from a Semantic Store.
 * <p>
 * All metadata is optional, and is set to NULL if not defined in the semantic store.
 * 
 * @author kunegis
 */
public class SemanticStoreDataset
    extends Dataset
{
	/**
	 * The metadata name under which the semantic URI of entities is stored. This metadata is created for entities of
	 * all types.
	 * <p>
	 * The string contains "url" for backwards compatibility.
	 */
	public final static MetadataName METADATA_URI = new MetadataName("semantic:url");

	/**
	 * The metadata name under which the semantic URL of entities is stored. This metadata is created for entities of
	 * all types.
	 * 
	 * @deprecated use METADATA_URI, which represents the same metadata
	 */
	@Deprecated
	public final static MetadataName METADATA_URL = METADATA_URI;

	private final static String METADATA_VARIABLE = "metadata";

	/**
	 * Load a dataset from a semantic store.
	 * <p>
	 * The given sparqlable is only used during the constructor and no references to it are kept.
	 * 
	 * @param sparqlables The connections to use to access the semantic store; NULL to initialize an empty dataset
	 * @param model The data model
	 * @param options The options
	 * 
	 * @throws NoSuchElementException when relationships contains unknown entities (only when mode == FAIL)
	 * @throws SparqlException On SPARQL errors
	 */
	public SemanticStoreDataset(Sparqlable sparqlables[], Model model, Options options)
	    throws SparqlException
	{
		this.model = model;

		/*
		 * Entities
		 */

		final Map <EntityType, SemanticEntityType> semanticEntityTypes = new HashMap <EntityType, SemanticEntityType>();

		for (final SemanticEntityType semanticEntityType: model.semanticEntityTypes)
		{
			final EntitySet entitySet = new EntitySet(semanticEntityType.entityType);
			this.addEntitySet(entitySet);

			semanticEntityTypes.put(semanticEntityType.entityType, semanticEntityType);

			String metadataVariables = "";
			String metadataDeclarations = "";

			int j = 0;

			for (final SemanticMetadata semanticMetadata: semanticEntityType.metadata)
			{
				if (semanticMetadata != null)
				{
					final String variable = METADATA_VARIABLE + j++;
					metadataVariables += " ?" + variable;
					metadataDeclarations += String.format(" OPTIONAL { ?entity %s ?%s.} ", semanticMetadata.rdfName,
					    variable);
				}
			}

			if (sparqlables == null)
			{
				final List <MetadataName> metadataNames = new ArrayList <MetadataName>();
				metadataNames.add(METADATA_URI);
				final List <Object> metadataSample = new ArrayList <Object>();
				metadataSample.add("string");
				entitySet.setMetadataNames(metadataNames, metadataSample);
				continue;
			}

			final List <QuerySolution> querySolutions = new ArrayList <QuerySolution>();

			for (final Sparqlable sparqlable: sparqlables)
			{
				final String sparql = String.format("%s SELECT ?entity%s WHERE { GRAPH <%s> {%s%s}}",
				    model.sparqlDefinitions, metadataVariables, sparqlable.getGraphName(), semanticEntityType
				        .getTriple("entity"), metadataDeclarations);

				final Iterator <QuerySolution> results = sparqlable.sparql(sparql);

				while (results.hasNext())
					querySolutions.add(results.next());
			}

			entitySet.setSize(querySolutions.size());

			final List <MetadataName> metadataNames = new ArrayList <MetadataName>();
			metadataNames.add(METADATA_URI);
			final List <Object> sampleMetadata = new ArrayList <Object>();
			sampleMetadata.add("String");

			for (final SemanticMetadata semanticMetadata: semanticEntityType.metadata)
			{
				if (semanticMetadata != null)
				{
					if (semanticMetadata.name.equals(METADATA_URI))
					    throw new IllegalArgumentException("Metadata cannot be named " + METADATA_URI);
					metadataNames.add(semanticMetadata.name);
					sampleMetadata.add("String");
				}
			}

			entitySet.setMetadataNames(metadataNames, sampleMetadata);

			final SingleMetadataIndex singleMetadataIndex = metadataIndex
			    .getSingleIdCache(semanticEntityType.entityType);

			for (int i = 0; i < querySolutions.size(); ++i)
			{
				final String uri = querySolutions.get(i).get("entity").toString();

				final int id = singleMetadataIndex.add(uri);

				entitySet.setMetadata(id, METADATA_URI, uri);

				j = 0;
				for (final SemanticMetadata semanticMetadata: semanticEntityType.metadata)
				{
					/*
					 * Do nothing if the metadata was not found (using OPTIONAL).
					 */
					final RDFNode lit = querySolutions.get(i).get(METADATA_VARIABLE + j++);
					if (lit != null)
					    entitySet.setMetadata(id, semanticMetadata.name, ((Literal) lit.as(Literal.class)).getString()
					        .intern());
				}
			}
		}

		/*
		 * Relationships
		 */
		for (final SemanticRelationshipType semanticRelationshipType: model.semanticRelationshipTypes)
		{
			final RelationshipSet relationshipSet = new RelationshipSet(semanticRelationshipType.relationshipType,
			    semanticRelationshipType.subject, semanticRelationshipType.object,
			    semanticRelationshipType.relationshipFormat, semanticRelationshipType.weightRange);
			this.addRelationshipSet(relationshipSet);

			final EntitySet entitySetSubject = this.getExistingEntitySet(semanticRelationshipType.subject);
			final EntitySet entitySetObject = this.getExistingEntitySet(semanticRelationshipType.object);

			final Matrix matrix = RelationshipSet.buildMatrix(entitySetSubject.size(), entitySetObject.size(),
			    semanticRelationshipType.weightRange);

			relationshipSet.setMatrix(matrix);

			if (sparqlables == null) continue;

			for (final Sparqlable sparqlable: sparqlables)
			{
				final String sparql = String.format(
				    "%s SELECT ?subject ?object WHERE { GRAPH <%s> {?subject %s ?object.  %s  %s}}",
				    model.sparqlDefinitions, sparqlable.getGraphName(), semanticRelationshipType.rdfRelationshipType,
				    options.getTypeMode() == TypeMode.UNTYPED ? "" : semanticEntityTypes.get(
				        semanticRelationshipType.subject).getTriple("subject"),
				    options.getTypeMode() == TypeMode.UNTYPED ? "" : semanticEntityTypes.get(
				        semanticRelationshipType.object).getTriple("object"));

				final Iterator <QuerySolution> results = sparqlable.sparql(sparql);

				while (results.hasNext())
				{
					final QuerySolution querySolution = results.next();

					final String uriSubject = querySolution.get("subject").toString();
					final String uriObject = querySolution.get("object").toString();

					try
					{
						matrix.set(metadataIndex.getSingleIdCache(semanticRelationshipType.subject).getId(uriSubject),
						    metadataIndex.getSingleIdCache(semanticRelationshipType.object).getId(uriObject), 1.);
					}
					catch (final NoSuchElementException exception)
					{
						if (model.mode != Mode.IGNORE) throw exception;
					}
				}
			}
		}
	}

	/**
	 * Constructor with default options.
	 * 
	 * @param sparqlables The sparqlables
	 * @param model The data model
	 * @throws SparqlException On SPARQL errors
	 */
	public SemanticStoreDataset(Sparqlable sparqlables[], Model model)
	    throws SparqlException
	{
		this(sparqlables, model, new Options());
	}

	/**
	 * Special constructor for the case where there is just one sparqlable.
	 * 
	 * @param sparqlable The single sparqlable or NULL to initialize an empty dataset
	 * @param model The model to use
	 * @throws SparqlException SPARQL error
	 */
	public SemanticStoreDataset(Sparqlable sparqlable, Model model)
	    throws SparqlException
	{
		this(sparqlable == null ? null : new Sparqlable[]
		{ sparqlable }, model);
	}

	/**
	 * A semantic store dataset with a single sparqlable.
	 * 
	 * @param sparqlable The sparqlable from which to read the data
	 * @param model The model to use
	 * @param options Options for creation
	 * @throws SparqlException On SPARQL errors
	 */
	public SemanticStoreDataset(Sparqlable sparqlable, Model model, Options options)
	    throws SparqlException
	{
		this(new Sparqlable[]
		{ sparqlable }, model, options);
	}

	/**
	 * @param sparqlables The connections to use to access the semantic store
	 * @param semanticEntityTypes List of entity types to read
	 * @param semanticRelationshipTypes List of relationship types to read
	 * @param mode Whether unknown entities are ignored in relationships
	 * @param sparqlDefinitions The SPARQL definitions; including "rdf:"
	 * @throws NoSuchElementException when relationships contains unknown entities (only when mode == FAIL)
	 * @throws SparqlException On SPARQL errors
	 */
	public SemanticStoreDataset(Sparqlable sparqlables[], SemanticEntityType semanticEntityTypes[],
	    SemanticRelationshipType semanticRelationshipTypes[], Mode mode, String sparqlDefinitions)
	    throws SparqlException
	{
		this(sparqlables, new Model(semanticEntityTypes, semanticRelationshipTypes, mode, sparqlDefinitions));
	}

	/**
	 * @param sparqlable The connection to use to access the semantic store
	 * @param semanticEntityTypes List of entity types to read
	 * @param semanticRelationshipTypes List of relationship types to read
	 * @param mode Whether unknown entities are ignored in relationships
	 * @param sparqlDefinitions The SPARQL definitions; including "rdf:"
	 * @throws NoSuchElementException when relationships contains unknown entities (only when mode == FAIL)
	 * @throws SparqlException On SPARQL errors
	 */
	public SemanticStoreDataset(Sparqlable sparqlable, SemanticEntityType semanticEntityTypes[],
	    SemanticRelationshipType semanticRelationshipTypes[], Mode mode, String sparqlDefinitions)
	    throws SparqlException
	{
		this(new Sparqlable[]
		{ sparqlable }, new Model(semanticEntityTypes, semanticRelationshipTypes, mode, sparqlDefinitions));
	}

	/**
	 * Update the dataset from the semantic store. The model <b>must not</b> have changed since construction of this
	 * object. Only relationships may have changed, not entities, entity types or relationship types.
	 * <p>
	 * All entities keep their IDs.
	 * <p>
	 * The given sparqlable is only used during the constructor and no references to it are kept.
	 * 
	 * @param sparqlables The sparqlable interfaces to the semantic store(s)
	 * 
	 * @throws SparqlException on SPARQL errors
	 */
	public void update(Sparqlable sparqlables[])
	    throws SparqlException
	{
		for (final Sparqlable sparqlable: sparqlables)
		{
			/*
			 * Relationships
			 */
			for (final SemanticRelationshipType semanticRelationshipType: model.semanticRelationshipTypes)
			{
				final RelationshipSet relationshipSet = getRelationshipSet(semanticRelationshipType.relationshipType);

				final EntitySet entitySetSubject = this.getExistingEntitySet(semanticRelationshipType.subject);
				final EntitySet entitySetObject = this.getExistingEntitySet(semanticRelationshipType.object);

				final Matrix matrix = RelationshipSet.buildMatrix(entitySetSubject.size(), entitySetObject.size(),
				    semanticRelationshipType.weightRange);

				relationshipSet.setMatrix(matrix);

				final String sparql = String.format(
				    "%s SELECT ?subject ?object WHERE { GRAPH <%s> {?subject %s ?object.}}", model.sparqlDefinitions,
				    sparqlable.getGraphName(), semanticRelationshipType.rdfRelationshipType);

				final Iterator <QuerySolution> results = sparqlable.sparql(sparql);

				update(results, semanticRelationshipType.relationshipType);
			}
		}
	}

	/**
	 * Update the dataset from a single sparqlable.
	 * 
	 * @param sparqlable The sparqlable to use
	 * @throws SparqlException SPARQL errors
	 */
	public void update(Sparqlable sparqlable)
	    throws SparqlException
	{
		update(new Sparqlable[]
		{ sparqlable });
	}

	/**
	 * Update a relationship set, i.e. update all relationships of a given type.
	 * <p>
	 * The RESULTS iterator may contain <i>all</i> current relationships of the given type, or just those that have
	 * changed.
	 * 
	 * @param results The iterator over query results
	 * @param relationshipType The type of all relationships that are updated
	 */
	public void update(Iterator <QuerySolution> results, RelationshipType relationshipType)
	{
		final RelationshipSet relationshipSet = getRelationshipSet(relationshipType);
		final Matrix matrix = relationshipSet.getMatrix();

		while (results.hasNext())
		{
			final QuerySolution querySolution = results.next();

			final String uriSubject = querySolution.get("subject").toString();
			final String uriObject = querySolution.get("object").toString();

			try
			{
				matrix.set(metadataIndex.getSingleIdCache(relationshipSet.getSubject()).getId(uriSubject),
				    metadataIndex.getSingleIdCache(relationshipSet.getObject()).getId(uriObject), 1.);
			}
			catch (final NoSuchElementException exception)
			{
				if (model.mode != Mode.IGNORE) throw exception;
			}
		}
	}

	/**
	 * Update the dataset with a given Jena model.
	 * <p>
	 * New entities can be added by being declared using rdf:type.
	 * <p>
	 * New relationships can be added by using the corresponding predicate.
	 * 
	 * @param jenaModel The Jena model containing the new relationships
	 */
	public void update(com.hp.hpl.jena.rdf.model.Model jenaModel)
	{
		/** Relationship types by RDF name */
		final Map <RdfName, RelationshipType> relationshipTypes = new HashMap <RdfName, RelationshipType>();
		for (final SemanticRelationshipType semanticRelationshipType: model.semanticRelationshipTypes)
		{
			relationshipTypes.put(semanticRelationshipType.rdfRelationshipType,
			    semanticRelationshipType.relationshipType);
		}

		/*
		 * Add entities
		 */
		for (final StmtIterator stmtIterator = jenaModel.listStatements(); stmtIterator.hasNext();)
		{
			final Statement statement = stmtIterator.nextStatement();

			final Resource subject = statement.getSubject();
			final RDFNode object = statement.getObject();
			final Property predicate = statement.getPredicate();

			if (!(predicate.getURI().equals("rdf:type") || predicate.getURI().equals(
			    "http://www.w3.org/1999/02/22-rdf-syntax-ns#type"))) continue;

			final String uriSubject = subject.toString();
			final String uriObject = object.toString();

			final EntityType entityType = new EntityType(uriObject);
			final EntitySet entitySet = getEntitySet(entityType);
			if (entitySet == null) /* New entity type */
			continue;
			Entity entity = null;
			try
			{
				entity = getEntity(METADATA_URI, entityType, uriSubject);
			}
			catch (final NoSuchElementException noSuchElementException)
			{
				/* Keep NULL */
			}
			if (entity != null) /* already contained */
			continue;

			final int newId = entitySet.addEntity();
			entitySet.setMetadata(newId, METADATA_URI, uriSubject);
		}

		/*
		 * Add relationships
		 */
		for (final StmtIterator stmtIterator = jenaModel.listStatements(); stmtIterator.hasNext();)
		{
			final Statement statement = stmtIterator.nextStatement();

			final Resource subject = statement.getSubject();
			final RDFNode object = statement.getObject();
			final Property predicate = statement.getPredicate();

			final String uriSubject = subject.toString();
			final String uriObject = object.toString();

			RdfName rdfName = null;
			try
			{
				rdfName = new RdfName(predicate.toString());
			}
			catch (final IllegalArgumentException illegalArgumentException)
			{
				/* This is a meta-triple */
				continue;
			}

			final RelationshipType relationshipType = relationshipTypes.get(rdfName);

			final RelationshipSet relationshipSet = getRelationshipSet(relationshipType);

			if (relationshipSet == null)
			{
				/* This is not a relation triple but something else */
				continue;
			}

			final Matrix matrix = relationshipSet.getMatrix();

			try
			{
				matrix.set(metadataIndex.getSingleIdCache(relationshipSet.getSubject()).getId(uriSubject),
				    metadataIndex.getSingleIdCache(relationshipSet.getObject()).getId(uriObject), 1.);
			}
			catch (final NoSuchElementException exception)
			{
				if (model.mode != Mode.IGNORE) throw exception;
			}
		}
	}

	/**
	 * Get the entity of a given type and URI.
	 * 
	 * @param entityType The entity type
	 * @param uri The URI of the entity
	 * @return The corresponding entity in this dataset
	 * @deprecated Use Dataset.getEntity(METADATA_URI, EntityType, uri)
	 */
	@Deprecated
	public Entity getEntity(EntityType entityType, String uri)
	{
		return getEntity(METADATA_URI, entityType, uri);
	}

	/**
	 * Get the semantic URI of an entity.
	 * 
	 * @param entity The entity of which to get the semantic URI
	 * @return The URI as a string
	 * @deprecated Use (String) getMetadata(EntityType, METADATA_URI), which also works for datasets not directly loaded
	 *             from a semantic store.
	 */
	@Deprecated
	public String getUri(Entity entity)
	{
		return (String) getMetadata(entity, METADATA_URI);
	}

	/**
	 * Get the semantic URI of an entity.
	 * 
	 * @param entity The entity of which to get the semantic URI
	 * @return The URI as a string
	 * 
	 * @deprecated use getUri() which does exactly the same
	 */
	@Deprecated
	public String getUrl(Entity entity)
	{
		return getUri(entity);
	}

	/**
	 * The data model.
	 */
	private final Model model;

	/**
	 * The one-to-one entity ID&ndash;URI relationship.
	 */
	private final MetadataIndex metadataIndex = new MetadataIndex();
}
