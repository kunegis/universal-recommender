package de.dailab.recommender.path;

import de.dailab.recommender.dataset.SimpleUnipartiteDataset;
import de.dailab.recommender.matrix.Matrix;

/**
 * Synthetic dataset used in several tests.
 * 
 * @author kunegis
 */
public class SyntheticDataset
    extends SimpleUnipartiteDataset
{
	/**
	 * The synthetic dataset.
	 */
	public SyntheticDataset()
	{
		super(5);

		final Matrix matrix = getMatrix();

		matrix.set(0, 1, 1);
		matrix.set(0, 2, 1);
		matrix.set(0, 3, 1);
		matrix.set(2, 3, 1);
		matrix.set(0, 4, .5);
	}
}
