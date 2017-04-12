package de.dailab.recommender.similarity;

/**
 * The transformation of a given similarity.
 * 
 * @author kunegis
 */
public class TransformedSimilarity
    implements Similarity
{
	/**
	 * The given transformation of the given similarity.
	 * 
	 * @param similarity The similarity to transform
	 * @param similarityTransformation The transformation to apply to the similarity
	 */
	public TransformedSimilarity(Similarity similarity, SimilarityTransformation similarityTransformation)
	{
		this.similarity = similarity;
		this.similarityTransformation = similarityTransformation;
	}

	/**
	 * The given similarity transformation applied to the scalar product.
	 * 
	 * @param similarityTransformation The similarity transformation to apply to the scalar product
	 */
	public TransformedSimilarity(SimilarityTransformation similarityTransformation)
	{
		this.similarity = new ScalarProduct();
		this.similarityTransformation = similarityTransformation;
	}

	@Override
	public SimilarityRun run()
	{
		return similarityTransformation.run(similarity);
	}

	/**
	 * The underlying similarity.
	 */
	private final Similarity similarity;
	private final SimilarityTransformation similarityTransformation;

	@Override
	public String toString()
	{
		return String.format("%s%s", similarityTransformation, similarity.equals(new ScalarProduct()) ? "" : String
		    .format("-%s", similarity));
	}

	@Override
	public boolean isSpectral()
	{
		return similarity.isSpectral() && similarityTransformation.isSpectral();
	}

	@Override
	public double[] transformSpectrum(double[] lambda)
	    throws UnsupportedOperationException
	{
		return similarityTransformation.transformSpectrum(similarity.transformSpectrum(lambda));
	}
}
