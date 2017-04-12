package de.dailab.recommender.function;

/**
 * Conversion between zero-centered doubles and ratings scales of typical recommendation systems, e.g. the famous
 * five-star scale.
 * 
 * @author kunegis
 */
public class RatingScale
{
	/**
	 * Convert a user rating to the linear scale.
	 * 
	 * @param rating User rating
	 * @param min Minimum value
	 * @param max Maximum value
	 * @return linear rating
	 */
	public static double userRatingToLinear(int rating, int min, int max)
	{
		assert (rating >= min && rating <= max);
		final double mid = (max + min) / 2.0;
		final double dist = (max - min) / 2.0;
		return (rating - mid) / dist;
	}

	/**
	 * Convert five-star rating to linear scale.
	 * 
	 * @param stars Number of starts (1 to 5)
	 * @return Rating on a linear scale
	 */
	public static double fiveStarsToLinear(int stars)
	{
		return userRatingToLinear(stars, 1, 5);
	}

	/**
	 * Convert linear rating to five-star scale.
	 * 
	 * @param rating linear rating
	 * @return rating on the five-star scale
	 */
	public static int linearToFiveStars(double rating)
	{
		int ret = (int) (3.5 + rating * 2.5);
		if (ret == 6) ret = 5;
		return ret;
	}
}
