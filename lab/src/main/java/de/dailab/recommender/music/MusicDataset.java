package de.dailab.recommender.music;

import com.hp.hpl.jena.sdb.store.DatabaseType;

import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.MetadataName;
import de.dailab.recommender.dataset.RelationshipFormat;
import de.dailab.recommender.dataset.RelationshipType;
import de.dailab.recommender.dataset.WeightRange;
import de.dailab.recommender.semantic.Mode;
import de.dailab.recommender.semantic.RdfEntityType;
import de.dailab.recommender.semantic.RdfName;
import de.dailab.recommender.semantic.SemanticEntityType;
import de.dailab.recommender.semantic.SemanticMetadata;
import de.dailab.recommender.semantic.SemanticRelationshipType;
import de.dailab.recommender.semantic.SemanticStoreConnection;
import de.dailab.recommender.semantic.SemanticStoreDataset;
import de.dailab.recommender.semantic.SparqlException;

/**
 * Test music dataset from Till.
 * <p>
 * The dataset is from "http://dai-labor/semstore/music".
 * 
 * @author kunegis
 */
public class MusicDataset
    extends SemanticStoreDataset
{
	private final static String URL = "jdbc:mysql://dai158:8886/semstore";
	private final static String USERNAME = "semstore";
	private final static String PASSWORD = "semstore";
	private final static DatabaseType DB_TYPE = DatabaseType.MySQL;

	private final static String SPARQL_DEFINITIONS = "PREFIX pi: <http://www.dai-labor.de/ontologies/piim/0.1/> \n "
	    + "PREFIX mo: <http://purl.org/ontology/mo/> \n"
	    + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
	    + "PREFIX foaf: <http://xmlns.com/foaf/0.1/> \n" + "PREFIX moat: <http://moat-project.org/ns#> \n"
	    + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
	    + "PREFIX location: <http://www.daml.org/experiment/ontology/location-ont#>";

	private static final String GRAPH_NAME = "http://dai-labor.de/semstore/music/";

// private static final int POOLSIZE = 0;

	/*
	 * Entity types
	 */

	/**
	 * The artist entity type.
	 */
	public static final EntityType ARTIST = new EntityType("artist");

	/**
	 * The tag entity type.
	 */
	public static final EntityType TAG = new EntityType("tag");

	/**
	 * The location (city) entity type.
	 */
	public static final EntityType LOCATION = new EntityType("location");

	/**
	 * The location (country) entity type.
	 */
	public static final EntityType COUNTRY = new EntityType("country");

	/*
	 * Relationship types
	 */

	/**
	 * The "similar to" relationship type.
	 */
	public static final RelationshipType SIMILAR = new RelationshipType("similar");

	/**
	 * The bipartite artist&ndash;tag relationship type.
	 */
	public static final RelationshipType ARTIST_TAG = new RelationshipType("artist-tag");

	/**
	 * The bipartite artist&ndash;tag relationship type.
	 */
	public static final RelationshipType ARTIST_LOC = new RelationshipType("artist-location");

	/**
	 * The location-location relationship.
	 */
	public static final RelationshipType IN_LOC = new RelationshipType("location-location");

	/*
	 * Metadata names
	 */

	/**
	 * The foaf:name of an artist.
	 */
	public static final MetadataName METADATA_FOAFNAME = new MetadataName("foafname");

	/**
	 * The music dataset loaded from the semantic store.
	 * 
	 * @throws SparqlException SPARQL error
	 */
	public MusicDataset()
	    throws SparqlException
	{
		super(createSemanticStoreConnection(), new SemanticEntityType[]
		{ new RdfEntityType(ARTIST, new RdfName("mo:MusicArtist"), new SemanticMetadata[]
		{ new SemanticMetadata(METADATA_FOAFNAME, new RdfName("foaf:name")) }),
		    new RdfEntityType(TAG, new RdfName("moat:Tag"), new SemanticMetadata[]
		    { new SemanticMetadata(METADATA_FOAFNAME, new RdfName("moat:name")) }),
		    new RdfEntityType(COUNTRY, new RdfName("location:country"), new SemanticMetadata[]
		    { new SemanticMetadata(METADATA_FOAFNAME, new RdfName("rdfs:label")) }),
		    new RdfEntityType(LOCATION, new RdfName("location:Location"), new SemanticMetadata[]
		    { new SemanticMetadata(METADATA_FOAFNAME, new RdfName("rdfs:label")) }), }, new SemanticRelationshipType[]
		{

		    new SemanticRelationshipType(SIMILAR, new RdfName("mo:similar_to"), ARTIST, ARTIST,
		        RelationshipFormat.ASYM, WeightRange.UNWEIGHTED),
		    new SemanticRelationshipType(ARTIST_LOC, new RdfName("pi:relatedLocation"), ARTIST, LOCATION,
		        RelationshipFormat.BIP, WeightRange.UNWEIGHTED),
		    new SemanticRelationshipType(IN_LOC, new RdfName("location:inLocation"), LOCATION, COUNTRY,
		        RelationshipFormat.BIP, WeightRange.UNWEIGHTED),
		    new SemanticRelationshipType(ARTIST_TAG, new RdfName("pi:hasTag"), ARTIST, TAG, RelationshipFormat.BIP,
		        WeightRange.UNWEIGHTED), }, Mode.IGNORE, SPARQL_DEFINITIONS);

		/*
		 * Note about IGNORE: the music Store contains some relationships with unknown entities.
		 */
	}

	private static SemanticStoreConnection createSemanticStoreConnection(/* final GenericSDBDAO genericSDBDAO */)
	{
		return new SemanticStoreConnection(URL, USERNAME, PASSWORD, GRAPH_NAME, DB_TYPE);

// return new SemanticStoreConnection(genericSDBDAO, GRAPH_NAME);
	}

// private static GenericSDBDAO createDao()
// {
// return new GenericSDBDAO(URL, USERNAME, PASSWORD, DB_TYPE, POOLSIZE)
// {
// @Override
// public Model mergeModels(Model model1, Model model2)
// {
// return null;
// }
// };
// }
}
