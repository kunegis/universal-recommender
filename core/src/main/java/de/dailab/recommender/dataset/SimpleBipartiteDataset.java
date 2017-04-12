package de.dailab.recommender.dataset;

import de.dailab.recommender.matrix.Matrix;
import de.dailab.recommender.matrix.template.MatrixFactory;

/**
 * A dataset with a single bipartite relationship type with a memory-held matrix.
 * 
 * @author kunegis
 */
public class SimpleBipartiteDataset
    extends UnirelationalDataset
{
	/**
	 * A unirelational dataset with two entity types and a single bipartite relationship type.
	 * <p>
	 * The created relationship set contains an empty sparse matrix.
	 * 
	 * @param m Number of subject entities
	 * @param n Number of object entities
	 * @param weightRange The weight range of the unique relationship type
	 */
	public SimpleBipartiteDataset(int m, int n, WeightRange weightRange)
	{
		super(createRelationshipSet(m, n, weightRange));
	}

	/**
	 * A unirelational dataset with two entity types and a single weighted relationship type.
	 * 
	 * @param m The number of subject entities
	 * @param n The number of object entities
	 */
	public SimpleBipartiteDataset(int m, int n)
	{
		this(m, n, WEIGHT_RANGE_DEFAULT);
	}

	private static RelationshipSet createRelationshipSet(int m, int n, WeightRange weightRange)
	{
		final RelationshipSet ret = new RelationshipSet(RELATIONSHIP, SUBJECT, OBJECT, RelationshipFormat.BIP,
		    weightRange);
		final Matrix matrix = MatrixFactory.newMemoryMatrix(m, n, weightRange == WeightRange.UNWEIGHTED ? boolean.class
		    .getSimpleName() : float.class.getSimpleName());
		ret.setMatrix(matrix);
		return ret;
	}

	/**
	 * The unique relationship type.
	 */
	public final static RelationshipType RELATIONSHIP = new RelationshipType("relationship");

	/**
	 * Subject entity type for the bipartite case.
	 */
	public final static EntityType SUBJECT = new EntityType("subject");

	/**
	 * Object entity type for the bipartite case.
	 */
	public final static EntityType OBJECT = new EntityType("object");

	private final static WeightRange WEIGHT_RANGE_DEFAULT = WeightRange.WEIGHTED;
}
