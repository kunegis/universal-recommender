package de.dailab.recommender.path;

import java.util.Iterator;
import java.util.Random;

import org.junit.Test;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntitySet;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.RelationshipFormat;
import de.dailab.recommender.dataset.RelationshipSet;
import de.dailab.recommender.dataset.RelationshipType;
import de.dailab.recommender.dataset.WeightRange;
import de.dailab.recommender.matrix.Matrix;
import de.dailab.recommender.matrix.template.MatrixFactory;
import de.dailab.recommender.recommend.Recommender;
import de.dailab.recommender.recommend.RecommenderModel;
import de.dailab.recommender.recommendation.Recommendation;

/**
 * Test the path recommender.
 * 
 * @author kunegis
 */
public class TestPathRecommender
{
	/**
	 * Test that a full PathRecommender() without underlying recommender returns only the request entity types.
	 */
	@Test
	public void testEntityType()
	{
		/*
		 * Entity types: a; b. Relationship types: r: a--a; s: b--b.
		 */

		final int n_a = 100;
		final int n_b = 150;
		final int n_rs = 1000;

		final EntityType A = new EntityType("a");
		final EntityType B = new EntityType("b");

		final RelationshipType R = new RelationshipType("r");
		final RelationshipType S = new RelationshipType("s");

		final Dataset dataset = new Dataset();

		final EntitySet entitySetA = new EntitySet(A);
		entitySetA.setSize(n_a);
		dataset.addEntitySet(entitySetA);

		final EntitySet entitySetB = new EntitySet(B);
		entitySetB.setSize(n_b);
		dataset.addEntitySet(entitySetB);

		final RelationshipSet relationshipSetR = new RelationshipSet(R, A, A, RelationshipFormat.ASYM,
		    WeightRange.WEIGHTED);
		final Matrix matrixR = MatrixFactory.newMemoryMatrix(n_a);
		relationshipSetR.setMatrix(matrixR);
		dataset.addRelationshipSet(relationshipSetR);

		final RelationshipSet relationshipSetS = new RelationshipSet(S, A, B, RelationshipFormat.BIP,
		    WeightRange.WEIGHTED);
		final Matrix matrixS = MatrixFactory.newMemoryMatrix(n_a, n_b);
		relationshipSetS.setMatrix(matrixS);
		dataset.addRelationshipSet(relationshipSetS);

		final Random random = new Random();

		for (int i = 0; i < n_rs; ++i)
		{
			matrixR.set(random.nextInt(n_a), random.nextInt(n_a), random.nextGaussian());
			matrixS.set(random.nextInt(n_a), random.nextInt(n_b), random.nextGaussian());
		}

		final Recommender recommender = new PathRecommender();

		final RecommenderModel recommenderModel = recommender.build(dataset);

		/*
		 * Recommend A
		 */
		{
			final Entity a1 = new Entity(A, 1);

			final Iterator <Recommendation> iterator = recommenderModel.recommend(a1, new EntityType[]
			{ A });

			int count = 0;
			while (iterator.hasNext())
			{
				++count;
				final Recommendation recommendation = iterator.next();
				assert recommendation.getEntity().getType().equals(A);
			}
			assert count > 10;
		}

		/*
		 * Recommend B
		 */
		{
			final Entity b1 = new Entity(B, 1);

			final Iterator <Recommendation> iterator = recommenderModel.recommend(b1, new EntityType[]
			{ B });

			int count = 0;
			while (iterator.hasNext())
			{
				++count;
				final Recommendation recommendation = iterator.next();
				assert recommendation.getEntity().getType().equals(B);
			}
			assert count > 10;
		}

	}
}
