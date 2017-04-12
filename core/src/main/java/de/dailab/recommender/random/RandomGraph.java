package de.dailab.recommender.random;

import java.util.Random;

import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.RelationshipSet;
import de.dailab.recommender.dataset.RelationshipType;
import de.dailab.recommender.dataset.UnirelationalDataset;
import de.dailab.recommender.matrix.Matrix;
import de.dailab.recommender.matrix.template.MatrixFactory;

/**
 * A random graph in which each edge has probability of existence P. This is the Erd&#337;s&#8211;R&#233;nyi model.
 * 
 * @see <a href =
 *      "http://en.wikipedia.org/wiki/Erd%C5%91s%E2%80%93R%C3%A9nyi_model">Wikipedia:&nbsp;&nbsp;Erd&#337;s&#8211;R&#233;nyi&nbsp;model</a>
 * 
 * @author kunegis
 */
public class RandomGraph
    extends UnirelationalDataset
{
	/**
	 * A unirelational network with N nodes. Each edge is present with probability P, independently for each edge.
	 * 
	 * @param n Number of nodes
	 * @param p Edge probability
	 */
	public RandomGraph(int n, double p)
	{
		super(getRelationshipSet(n));

		final Matrix matrix = getUniqueRelationshipSet().getMatrix();

		/* This is approximate */

		final Random random = new Random();

		for (int k = 0; k < p * n * n / 2; ++k)
		{
			final int i = random.nextInt(n);
			final int j = random.nextInt(n);
			matrix.set(i, j, 1);
			matrix.set(j, i, 1);
		}
	}

	private static RelationshipSet getRelationshipSet(int n)
	{
		final RelationshipSet ret = new RelationshipSet(RELATIONSHIP_TYPE, ENTITY_TYPE, ENTITY_TYPE);
		final Matrix matrix = MatrixFactory.newMemoryMatrixUnweighted(n);

		ret.setMatrix(matrix);
		return ret;
	}

	/**
	 * The relationship type in this random graph class.
	 */
	public static final RelationshipType RELATIONSHIP_TYPE = new RelationshipType("Erd\u0151s\u2013R\u00e9nyi");

	/**
	 * The entity type in this random graph class.
	 */
	public static final EntityType ENTITY_TYPE = new EntityType("node");
}
