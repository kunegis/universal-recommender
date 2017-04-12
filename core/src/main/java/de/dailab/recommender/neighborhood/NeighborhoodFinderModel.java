package de.dailab.recommender.neighborhood;

import java.util.Iterator;

/**
 * A compiled model of a neighborhood that can be used to find the neighborhood of a vector fast.
 * 
 * @author kunegis
 */
public interface NeighborhoodFinderModel
{
	/**
	 * Find the neighborhood of a vector in a continuous fashion.
	 * 
	 * @param vector a given vector
	 * @return An iterator over near vectors by their ID
	 */
	Iterator <WeightedPoint> findContinuous(double[] vector);

	/**
	 * Update the model to changes in the latent space.
	 */
	void update();

	/**
	 * The neighborhood finder that was used to build this model.
	 * 
	 * @return the neighborhood finder used to build this model.
	 */
	NeighborhoodFinder getNeighborhoodFinder();
}
