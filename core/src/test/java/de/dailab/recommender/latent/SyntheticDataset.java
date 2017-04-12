package de.dailab.recommender.latent;

import org.junit.Ignore;

import de.dailab.recommender.dataset.SimpleUnipartiteDataset;
import de.dailab.recommender.matrix.Matrix;

/**
 * Small synthetic dataset.
 * 
 * @author kunegis
 */
@Ignore
public class SyntheticDataset
    extends SimpleUnipartiteDataset
{
	/**
	 * The synthetic dataset.
	 */
	public SyntheticDataset()
	{
		super(24);

		final Matrix matrix = getMatrix();

		/*
		 * Clique: 0..3
		 */
		matrix.set(0, 1, 1);
		matrix.set(0, 2, 1);
		matrix.set(0, 3, 1);
		matrix.set(1, 2, 1);
		matrix.set(1, 3, 1);
		matrix.set(2, 3, 1);

		/*
		 * Bipartite double path: 0 and 1..12
		 */
		matrix.set(0, 4, 1);
		matrix.set(0, 5, 1);
		matrix.set(4, 6, 1);
		matrix.set(5, 6, 1);
		matrix.set(6, 7, 1);
		matrix.set(6, 8, 1);
		matrix.set(7, 9, 1);
		matrix.set(8, 9, 1);
		matrix.set(9, 10, 1);
		matrix.set(9, 11, 1);
		matrix.set(10, 12, 1);
		matrix.set(11, 12, 1);

		/*
		 * Path: 0 and 13..23
		 */
		matrix.set(0, 13, 1);
		for (int i = 13; i < 23; ++i)
			matrix.set(i, i + 1, 1);
	}
}
