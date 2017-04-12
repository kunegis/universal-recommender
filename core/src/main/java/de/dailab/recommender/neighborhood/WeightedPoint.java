package de.dailab.recommender.neighborhood;

import java.util.List;
import java.util.ListIterator;

/**
 * An ID-score pair.
 * 
 * @author kunegis
 */
public class WeightedPoint
{
	/**
	 * A given point with a given score.
	 * 
	 * @param point The point, as an ID
	 * @param score The score
	 */
	public WeightedPoint(int point, double score)
	{
		this.point = point;
		this.score = score;
	}

	/**
	 * The point ID.
	 */
	public final int point;

	/**
	 * The score.
	 */
	public final double score;

	@Override
	public String toString()
	{
		return String.format("%d (%.4g)", point, score);
	}

	/**
	 * Merge a given weighted point list into another. Weighted points from SRC are merged into DST. Both lists are
	 * assumed to be sorted by highest scores first. The resulting list is cut to <i>k</i> elements.
	 * 
	 * @param dst Destination list, modified in place. Must not be longer than K.
	 * @param src Source list, not modified
	 * @param k Maximum number of elements in result; at least 1
	 */
	public static void merge(List <WeightedPoint> dst, List <WeightedPoint> src, int k)
	{
		assert k > 0;
		assert dst.size() <= k;

		final ListIterator <WeightedPoint> iDst = dst.listIterator();
		final ListIterator <WeightedPoint> iSrc = src.listIterator();

		while (iSrc.hasNext())
		{
			final WeightedPoint nextSrc = iSrc.next();

			while (iDst.hasNext())
			{
				if (iDst.next().score < nextSrc.score)
				{
					iDst.previous();
					break;
				}
			}

			if (iDst.nextIndex() >= k)
			{
				break;
			}

			iDst.add(nextSrc);

		}

		while (dst.size() > k)
			dst.remove(dst.size() - 1);
	}

	/**
	 * Insert a given weighted point into a sorted weighted point list.
	 * 
	 * @param dst The weighted point list into which the weighted point is inserted. Must be sorted with highest scores
	 *        first; changed in place; must not be longer than <i>k</i>
	 * @param weightedPoint Weighted point to insert
	 * @param k Maximum number in the modified DST
	 */
	public static void merge(List <WeightedPoint> dst, WeightedPoint weightedPoint, int k)
	{
		assert k > 0;
		assert dst.size() <= k;

		if (dst.size() >= k && dst.get(dst.size() - 1).score >= weightedPoint.score) return;

		final ListIterator <WeightedPoint> iDst = dst.listIterator();

		while (iDst.hasNext())
		{
			if (iDst.next().score < weightedPoint.score)
			{
				iDst.previous();
				break;
			}
		}

		if (iDst.nextIndex() >= k) return;

		iDst.add(weightedPoint);

		if (dst.size() > k) dst.remove(dst.size() - 1);
	}

}
