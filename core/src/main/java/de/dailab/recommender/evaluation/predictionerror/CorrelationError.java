package de.dailab.recommender.evaluation.predictionerror;

import de.dailab.recommender.similarity.Correlation;

/**
 * One minus the Pearson correlation between predicted and real values.
 * 
 * @author kunegis
 */
public class CorrelationError
    extends SimilarityPredictionError
{
	/**
	 * One minus the Pearson correlation.
	 */
	public CorrelationError()
	{
		super(new Correlation(), 1);
	}
}
