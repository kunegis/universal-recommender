package de.dailab.recommender.recommendation;

import java.util.Comparator;

import de.dailab.recommender.dataset.Entity;

/**
 * A recommendation, as returned by recommenders, containing an entity and a score.
 * <p>
 * Scores represent the degree to which an entity matches a request or source entity. Higher scores denote better
 * results. In general, good results have a positive score. Negative scores denote a negative degree of matching, and
 * may be undesirable in some scenarios. There is no constraint on the range of scores, i.e. very large or very small
 * values are returned by specific recommenders.
 * <p>
 * The natural ordering of recommendations is by score (descending) and then by entities, i.e. best recommendations
 * first.
 * 
 * @author kunegis
 */
public final class Recommendation
    implements Comparable <Recommendation>
{
	/**
	 * Create a recommendation.
	 * 
	 * @param entity The entity, must not be NULL
	 * @param score The score. Must not be infinite or NaN
	 */
	public Recommendation(Entity entity, double score)
	{
		assert entity != null;
		assert !Double.isNaN(score) && !Double.isInfinite(score);
		this.entity = entity;
		this.score = score;
	}

	/**
	 * @return The entity recommended in this recommendation
	 */
	public Entity getEntity()
	{
		return entity;
	}

	/**
	 * The score of this recommendation
	 * 
	 * @return The numerical score. Not infinite or NaN.
	 */
	public double getScore()
	{
		return score;
	}

	/**
	 * The recommended entity. Not NULL.
	 */
	private final Entity entity;

	/**
	 * The score of the recommendation. Must be a number (i.e. not NaN or Inf). Higher values denote better entities.
	 */
	private final double score;

	/**
	 * A comparator that sorts recommendations in reverse natural order, i.e. worst recommendations first.
	 */
	public final static Comparator <Recommendation> REVERSE_COMPARATOR = new Comparator <Recommendation>()
	{
		@Override
		public int compare(Recommendation o1, Recommendation o2)
		{
			return -o1.compareTo(o2);
		}
	};

	@Override
	public boolean equals(Object object)
	{
		if (!(object instanceof Recommendation)) return false;

		final Recommendation recommendation = (Recommendation) object;

		return this.entity.equals(recommendation.entity) && this.score == recommendation.score;
	}

	@Override
	public String toString()
	{
		return String.format("%.4g %s", score, entity);
	}

	@Override
	public int compareTo(Recommendation recommendation)
	{
		if (recommendation.score < this.score) return -1;
		if (recommendation.score > this.score) return +1;
		return this.entity.compareTo(recommendation.entity);
	}

	@Override
	public int hashCode()
	{
		return (17 + 127 * Double.valueOf(score).hashCode()) + entity.hashCode();
	}
}
