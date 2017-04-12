package de.dailab.recommender.normalize;

import de.dailab.recommender.matrix.Matrix;

/**
 * A transformation of a relationship set, giving a relationship set with the same structure and other edge weights.
 * <p>
 * Objects of this class represent the parameters and method of normalization independently of any matrix. Calling
 * this.build(matrix) returns a normalizer model that can be used to compute normalizations for a specific matrix.
 * Normalizers are however specific to certain features of relationship sets, such as the weight range and the
 * relationship format.
 * 
 * @author kunegis
 */
public interface Normalizer
{
	/**
	 * Build a model given a matrix. The model can normalize and denormalize values according to the matrix.
	 * 
	 * @param matrix The data matrix to normalize. Should not be unweighted.
	 * @return A normalizer model that can be used to normalize and denormalize matrix weights fast
	 */
	NormalizerModel build(Matrix matrix);
}
