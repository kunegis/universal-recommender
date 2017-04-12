package de.dailab.recommender.predict;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.dailab.recommender.dataset.Dataset;

/**
 * A predictor list that simply builds each predictor separately.
 * 
 * @see PredictorList
 * 
 * @author kunegis
 */
public class SequentialPredictorList
    implements PredictorList
{
	/**
	 * The given predictor list is used.
	 * 
	 * @param predictors The predictor list
	 */
	public SequentialPredictorList(List <Predictor> predictors)
	{
		this.predictors = predictors;
	}

	/**
	 * A predictor list based on several given predictors.
	 * 
	 * @param predictors The predictors to aggregate
	 */
	public SequentialPredictorList(Predictor... predictors)
	{
		this.predictors = new ArrayList <Predictor>(predictors.length);
		for (final Predictor predictor: predictors)
			this.predictors.add(predictor);
	}

	/**
	 * A sequential predictor list made from the predictors of given sequential predictor lists.
	 * 
	 * @param sequentialPredictorLists Sequential predictor lists to aggregate
	 */
	public SequentialPredictorList(SequentialPredictorList... sequentialPredictorLists)
	{
		predictors = new ArrayList <Predictor>();
		for (final SequentialPredictorList sequentialPredictorList: sequentialPredictorLists)
		{
			predictors.addAll(sequentialPredictorList.predictors);
		}
	}

	private final List <Predictor> predictors;

	@Override
	public List <Predictor> getPredictors()
	{
		return predictors;
	}

	@Override
	public Iterator <PredictorModel> build(final Dataset dataset)
	{
		return new Iterator <PredictorModel>()
		{
			private final Iterator <Predictor> iterator = predictors.iterator();

			@Override
			public boolean hasNext()
			{
				return iterator.hasNext();
			}

			@Override
			public PredictorModel next()
			{
				return iterator.next().build(dataset);
			}

			@Override
			public void remove()
			{
				throw new UnsupportedOperationException();
			}
		};
	}
}
