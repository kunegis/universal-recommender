package de.dailab.recommender.semantic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.EntitySet;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.MetadataName;
import de.dailab.recommender.dataset.RelationshipFormat;
import de.dailab.recommender.dataset.RelationshipSet;
import de.dailab.recommender.dataset.RelationshipType;
import de.dailab.recommender.dataset.WeightRange;
import de.dailab.recommender.matrix.Matrix;
import de.dailab.recommender.matrix.template.MatrixFactory;

/**
 * A memory-created semantic dataset for testing.
 * 
 * @author kunegis
 */
public class UnipartiteStaticSemanticDataset
    extends Dataset
{
	/**
	 * A simple memory-held dataset.
	 */
	public UnipartiteStaticSemanticDataset()
	{
		final int n = 100;

		final EntitySet userSet = new EntitySet(USER);
		final List <MetadataName> semanticMetadataNames = new ArrayList <MetadataName>();
		semanticMetadataNames.add(SemanticStoreDataset.METADATA_URI);
		final List <Object> sampleMetadata = new ArrayList <Object>();
		sampleMetadata.add("string");
		userSet.setMetadataNames(semanticMetadataNames, sampleMetadata);
		for (int i = 0; i < n; ++i)
		{
			userSet.addEntity();
			userSet.setMetadata(i, SemanticStoreDataset.METADATA_URI, "http://example.org/user/" + i);
		}
		this.addEntitySet(userSet);

		final RelationshipSet friends = new RelationshipSet(FRIEND, USER, USER, RelationshipFormat.ASYM,
		    WeightRange.UNWEIGHTED);
		this.addRelationshipSet(friends);
		friends.setMatrix(MatrixFactory.newMemoryMatrixUnweighted(n));
		final Matrix matrix = friends.getMatrix();
		final int r = (int) Math.round(n * Math.log(n));
		final Random random = new Random();
		for (int k = 0; k < r; ++k)
			matrix.set(random.nextInt(n), random.nextInt(n), 1);
	}

	/**
	 * Users.
	 */
	public static final EntityType USER = new EntityType("http://example.org/entity-types/user");

	/**
	 * Friendship.
	 */
	public static final RelationshipType FRIEND = new RelationshipType("http://example.org/relationship-types/friend");
}
