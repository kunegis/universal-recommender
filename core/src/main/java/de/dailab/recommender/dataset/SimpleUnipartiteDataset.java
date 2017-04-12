package de.dailab.recommender.dataset;

import de.dailab.recommender.matrix.Matrix;
import de.dailab.recommender.matrix.template.MatrixFactory;

/**
 * A unipartite unirelational dataset with default entity and relationship names, and a memory-held sparse matrix
 * representation.
 * <p>
 * This is mostly used when building datasets "by hand", e.g. in unit tests.
 * 
 * @author kunegis
 */
public class SimpleUnipartiteDataset
    extends UnirelationalDataset
{
	/**
	 * A unirelational dataset with one entity type and a single unipartite relationship type.
	 * <p>
	 * The created relationship set contains an empty sparse matrix.
	 * 
	 * @param n The number of entities
	 * @param weightRange The weight range of the unique relationship type
	 */
	public SimpleUnipartiteDataset(int n, WeightRange weightRange)
	{
		super(createRelationshipSet(n, weightRange));
	}

	/**
	 * A unirelational dataset with one entity type and one relationship type and weighted relationships.
	 * 
	 * @param n The number of entities
	 */
	public SimpleUnipartiteDataset(int n)
	{
		this(n, WEIGHT_RANGE_DEFAULT);
	}

	private static RelationshipSet createRelationshipSet(int n, WeightRange weightRange)
	{
		final RelationshipSet ret = new RelationshipSet(RELATIONSHIP, ENTITY, ENTITY, RelationshipFormat.ASYM,
		    weightRange);
		final Matrix matrix = MatrixFactory.newMemoryMatrix(n, weightRange == WeightRange.UNWEIGHTED ? boolean.class
		    .getSimpleName() : float.class.getSimpleName());
		ret.setMatrix(matrix);
		return ret;
	}

	/**
	 * The unique relationship type.
	 */
	public final static RelationshipType RELATIONSHIP = new RelationshipType("relationship");

	/**
	 * The unique entity type for the symmetric constructor.
	 */
	public final static EntityType ENTITY = new EntityType("entity");

	private final static WeightRange WEIGHT_RANGE_DEFAULT = WeightRange.WEIGHTED;
}
