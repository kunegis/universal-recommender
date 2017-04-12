package de.dailab.recommender.similarity;

/**
 * A similarity based on another similarity.
 * 
 * @author kunegis
 */
public interface SimilarityTransformation
{
	/**
	 * Build a similarity run by applying this similarity transformation to the given similarity.
	 * 
	 * @param similarity The similarity to transform
	 * @return The similarity run
	 */
	SimilarityRun run(Similarity similarity);

	/**
	 * @return The name of the similarity transformation
	 */
	@Override
	String toString();

	/**
	 * Whether the transformation is purely, i.e. whether is can be expressed as a function of the spectrum.
	 * 
	 * @return Whether the transformation is spectral
	 */
	boolean isSpectral();

	/**
	 * Transform a spectrum according to this spectral transformation.
	 * 
	 * @param lambda The spectrum to transform
	 * @return The transformed spectrum
	 * @throws UnsupportedOperationException When the similarity transformation is not spectral
	 */
	double[] transformSpectrum(double lambda[])
	    throws UnsupportedOperationException;
}
