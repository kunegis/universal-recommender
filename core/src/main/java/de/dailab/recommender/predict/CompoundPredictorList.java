package de.dailab.recommender.predict;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import de.dailab.recommender.dataset.Dataset;

/**
 * A predictor list consisting of several (abstract) predictor lists.
 * 
 * @author kunegis
 */
public class CompoundPredictorList
    implements PredictorList
{
	/**
	 * A compound predictor list containing the given predictor lists.
	 * 
	 * @param predictorLists The predictor lists to be contained in this compound predictor list
	 */
	public CompoundPredictorList(PredictorList... predictorLists)
	{
		this.predictorLists = new ArrayList <PredictorList>();
		for (final PredictorList predictorList: predictorLists)
		{
			this.predictorLists.add(predictorList);
		}
	}

	private final List <PredictorList> predictorLists;

	/**
	 * The list of predictors returned by getPredictors(). NULL when not generated.
	 */
	private List <Predictor> predictors = null;

	@Override
	public List <Predictor> getPredictors()
	{
		if (predictors == null)
		{
			predictors = new ArrayList <Predictor>();
			for (final PredictorList predictorList: predictorLists)
			{
				predictors.addAll(predictorList.getPredictors());
			}
		}

		return predictors;
	}

	@SuppressWarnings(
	{ "unchecked" })
	@Override
	public Iterator <PredictorModel> build(final Dataset dataset)
	{
		final Iterator <PredictorList> predictorListIterator = predictorLists.iterator();

		if (!predictorListIterator.hasNext()) return new ArrayList <PredictorModel>().iterator();

		final Iterator <PredictorModel> predictorModelIterator[] = new Iterator[]
		{ null };
		predictorModelIterator[0] = predictorListIterator.next().build(dataset);

		return new Iterator <PredictorModel>()
		{
			@Override
			public boolean hasNext()
			{
				if (predictorModelIterator[0].hasNext()) return true;
				while (predictorListIterator.hasNext())
				{
					predictorModelIterator[0] = predictorListIterator.next().build(dataset);
					if (predictorModelIterator[0].hasNext()) return true;
				}
				return false;
			}

			@Override
			public PredictorModel next()
			{
				if (!hasNext()) throw new NoSuchElementException();
				return predictorModelIterator[0].next();
			}

			@Override
			public void remove()
			{
				throw new UnsupportedOperationException();
			}
		};
	}
}
