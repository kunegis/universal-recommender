package de.dailab.recommender.evaluation;

/**
 * A split type suitable for evaluating recommenders.
 * <p>
 * Edges are removed from entities that have a minimum number of relationships. The minimum number of relationships is a
 * parameter.
 * 
 * @author kunegis
 */
public class RecommenderSplitType
    implements SplitType
{
	/**
	 * A split in which each entity in the test set must have at least the given number of relationships.
	 * 
	 * @param minimum The minimum number of neighbors an entity must have to be in the test set.
	 */
	public RecommenderSplitType(int minimum)
	{
		this.minimum = minimum;
	}

	/**
	 * A recommender split type with the default value for the minimum.
	 */
	public RecommenderSplitType()
	{
		this.minimum = MINIMUM_DEFAULT;
	}

	/**
	 * The minimum number of relationships an entity must have to come into the test set.
	 */
	public final int minimum;

	private final static int MINIMUM_DEFAULT = 10;
}
