package de.dailab.recommender.semantic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.EntitySet;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.MetadataName;
import de.dailab.recommender.dataset.RelationshipSet;
import de.dailab.recommender.dataset.RelationshipType;
import de.dailab.recommender.matrix.Matrix;
import de.dailab.recommender.matrix.template.MatrixFactory;

/**
 * A synthetic semantic dataset.
 * 
 * @author kunegis
 */
public class BipartiteSyntheticSemanticDataset
    extends Dataset
{
	/**
	 * A simple memory-held dataset.
	 */
	public BipartiteSyntheticSemanticDataset()
	{
		final int USER_COUNT = 100;
		final int MOVIE_COUNT = 30;

		final List <MetadataName> semanticMetadataNames = new ArrayList <MetadataName>();
		semanticMetadataNames.add(SemanticStoreDataset.METADATA_URI);
		final List <Object> sampleMetadata = new ArrayList <Object>();
		sampleMetadata.add("string");

		/*
		 * Users
		 */
		final EntitySet userSet = new EntitySet(USER);
		userSet.setMetadataNames(semanticMetadataNames, sampleMetadata);
		for (int i = 0; i < USER_COUNT; ++i)
		{
			userSet.addEntity();
			userSet.setMetadata(i, SemanticStoreDataset.METADATA_URI, "http://example.org/user/" + i);
		}
		this.addEntitySet(userSet);

		/*
		 * Movies
		 */
		final EntitySet movieSet = new EntitySet(MOVIE);
		movieSet.setMetadataNames(semanticMetadataNames, sampleMetadata);
		for (int j = 0; j < MOVIE_COUNT; ++j)
		{
			movieSet.addEntity();
			movieSet.setMetadata(j, SemanticStoreDataset.METADATA_URI, "http://example.org/movie/" + j);
		}
		this.addEntitySet(movieSet);

		/*
		 * Has-seen
		 */
		final RelationshipSet hasSeen = new RelationshipSet(HAS_SEEN, USER, MOVIE);
		this.addRelationshipSet(hasSeen);
		final Matrix matrix = MatrixFactory.newMemoryMatrixUnweighted(USER_COUNT, MOVIE_COUNT);
		hasSeen.setMatrix(matrix);
		final int r = (int) Math.round((USER_COUNT + MOVIE_COUNT) * Math.log(USER_COUNT + MOVIE_COUNT));
		final Random random = new Random(273462783467L);
		for (int k = 0; k < r; ++k)
			matrix.set(random.nextInt(USER_COUNT), random.nextInt(MOVIE_COUNT), 1);
	}

	/**
	 * Users.
	 */
	public static final EntityType USER = new EntityType("http://example.org/entity-types/user");

	/**
	 * Movies.
	 */
	public static final EntityType MOVIE = new EntityType("http://example.org/entity-types/movie");

	/**
	 * Has-seen relationship type.
	 */
	public static final RelationshipType HAS_SEEN = new RelationshipType(
	    "http://example.org/relationship-types/has-seen");
}
