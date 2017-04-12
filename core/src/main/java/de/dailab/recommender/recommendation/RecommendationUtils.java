package de.dailab.recommender.recommendation;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import de.dailab.recommender.constraint.Constraint;
import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.EntitySet;
import de.dailab.recommender.dataset.MetadataName;

/**
 * Manage list of recommendations (static methods).
 * <p>
 * Recommendation lists are represented as {@code List <Recommendation>}, sorted by score, with highest scores first.
 * The preferred List implementation is LinkedList, because listIterator() is used.
 * <p>
 * This class also supports recommendation iterators: Objects of type {@code Iterator <Recommendation>} returned by the
 * recommend() method of recommenders.
 * 
 * @author kunegis
 * @see Recommendation
 */
public class RecommendationUtils
{
	// XXX There should be a class RecommendationList for the following reason: Some lookahead
	// recommenders may find a new score for an entity, and could want to change the score/ordering
	// of already returned recommendations.

	/**
	 * Read a given number of recommendations of an iterator, and return them as a list.
	 * 
	 * @param iterator The recommendation iterator to read recommendations from.
	 * @param k The maximum number of recommendations to read.
	 * @return The list of recommendations read from the iterator. The returned list is a LinkedList. It only contains
	 *         less than K recommendations if the iterator had less than K recommendations left.
	 */
	public static List <Recommendation> read(Iterator <Recommendation> iterator, int k)
	{
		final List <Recommendation> ret = new LinkedList <Recommendation>();

		while (ret.size() < k && iterator.hasNext())
			ret.add(iterator.next());

		return ret;
	}

	/**
	 * Read all recommendations from an iterator and return them as a list.
	 * <p>
	 * This function may return <i>many</i> recommendations. To get only a given number of recommendations, use the
	 * version of this method with an integer argument.
	 * 
	 * @param iterator A recommendations iterator
	 * @return List of all recommendations from the iterator, in the same order as returned by the iterator
	 * 
	 * @deprecated Recommendation iterators may be infinite; use the version of this method with the argument K giving
	 *             the number of recommendations to read, and only read as much as needed
	 */
	@Deprecated
	public static List <Recommendation> read(Iterator <Recommendation> iterator)
	{
		final List <Recommendation> ret = new LinkedList <Recommendation>();
		while (iterator.hasNext())
			ret.add(iterator.next());
		return ret;
	}

	/**
	 * Merge a given recommendation list into another. Recommendations from SRC are merged into DST. Both lists are
	 * assumed to be sorted by highest scores first. The resulting list is cut to <i>k</i> elements.
	 * 
	 * @param dst Destination list, modified in place. Must not be longer than K.
	 * @param src Source list, not modified
	 * @param k Maximum number of elements in result; at least 1
	 */
	public static void merge(List <Recommendation> dst, List <Recommendation> src, int k)
	{
		assert k > 0;
		assert dst.size() <= k;

		final ListIterator <Recommendation> iDst = dst.listIterator();
		final ListIterator <Recommendation> iSrc = src.listIterator();

		while (iSrc.hasNext())
		{
			final Recommendation nextSrc = iSrc.next();

			while (iDst.hasNext())
			{
				if (iDst.next().getScore() < nextSrc.getScore())
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
	 * Merge recommendations from an iterator into a given list. The iterator is only read as far as is necessary to
	 * fill the destination list.
	 * 
	 * @param dst The current recommendation list
	 * @param src The iterator read for new recommendations. This method may read one more recommendations than is added
	 *        to the destination list.
	 * @param k Maximum size of destination list
	 */
	public static void merge(List <Recommendation> dst, Iterator <Recommendation> src, int k)
	{
		assert k > 0;
		assert dst.size() <= k;

		final ListIterator <Recommendation> iDst = dst.listIterator();

		while (src.hasNext())
		{
			final Recommendation nextSrc = src.next();

			while (iDst.hasNext())
			{
				if (iDst.next().getScore() < nextSrc.getScore())
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
	 * Insert a given recommendation into a sorted recommendation list.
	 * 
	 * @param dst The recommendation list into which the recommendation is inserted. Must be sorted with highest scores
	 *        first. changed in place. Must not be longer than K.
	 * @param recommendation Recommendation to insert
	 * @param k Maximum number in the modified DST
	 */
	public static void merge(List <Recommendation> dst, Recommendation recommendation, int k)
	{
		assert k > 0;
		assert dst.size() <= k;

		if (dst.size() >= k && dst.get(dst.size() - 1).getScore() >= recommendation.getScore()) return;

		final ListIterator <Recommendation> iDst = dst.listIterator();

		while (iDst.hasNext())
		{
			if (iDst.next().getScore() < recommendation.getScore())
			{
				iDst.previous();
				break;
			}
		}

		if (iDst.nextIndex() >= k) return;

		iDst.add(recommendation);

		if (dst.size() > k) dst.remove(dst.size() - 1);
	}

	/**
	 * Filter out the recommendations that do not fulfill a given constraint.
	 * 
	 * @param recommendations List of recommendations, modified in place
	 * @param constraint A constraint
	 */
	public static void filter(List <Recommendation> recommendations, Constraint constraint)
	{
		for (final ListIterator <Recommendation> i = recommendations.listIterator(); i.hasNext();)
		{
			final Recommendation recommendation = i.next();
			if (!constraint.accept(recommendation)) i.remove();
		}
	}

	/**
	 * Format given recommendations.
	 * 
	 * @param recommendations List of recommendations
	 * @return formatted recommendations
	 */
	public static String format(List <Recommendation> recommendations)
	{
		String ret = "";
		for (final Recommendation recommendation: recommendations)
		{
			ret += String.format("(%g) %s\n", recommendation.getScore(), recommendation.getEntity());
		}
		return ret;
	}

	/**
	 * Format the given recommendation list using metadata from the given dataset.
	 * 
	 * @param recommendations A recommendation list to format
	 * @param dataset The dataset to read metadata from
	 * @return Formatted recommendation list, with metadata
	 */
	public static String format(List <Recommendation> recommendations, Dataset dataset)
	{
		String ret = "";

		for (final Recommendation recommendation: recommendations)
			ret += format(recommendation, dataset) + "\n";

		return ret;
	}

	/**
	 * Format a recommendation using metadata from a dataset.
	 * 
	 * @param recommendation a recommendation
	 * @param dataset the dataset
	 * @return the formatted recommendation
	 */
	public static String format(Recommendation recommendation, Dataset dataset)
	{
		String ret = "";

		ret += String.format("%.3f\t%s", recommendation.getScore(), recommendation.getEntity());

		final EntitySet entitySet = dataset.getEntitySet(recommendation.getEntity().getType());
		final Set <MetadataName> metadataTypes = entitySet.getMetadataNames();

		for (final MetadataName metadataName: metadataTypes)
		{
			ret += String.format(" %s", entitySet.getMetadata(recommendation.getEntity().getId(), metadataName));
		}

		return ret;
	}
}
