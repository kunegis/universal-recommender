package de.dailab.recommender.recommend;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.dailab.recommender.dataset.Dataset;

/**
 * A list of recommenders that simply builds each recommender model separately.
 * 
 * @author kunegis
 */
public class SequentialRecommenderList
    implements RecommenderList
{
	/**
	 * A recommender list containing the given recommenders.
	 * 
	 * @param recommenders The recommenders to put in this list
	 */
	public SequentialRecommenderList(List <Recommender> recommenders)
	{
		this.recommenders = recommenders;
	}

	/**
	 * A recommender list containing the given recommenders.
	 * 
	 * @param recommenders The recommenders to put in this list
	 */
	public SequentialRecommenderList(Recommender... recommenders)
	{
		this.recommenders = new ArrayList <Recommender>(recommenders.length);

		for (final Recommender recommender: recommenders)
			this.recommenders.add(recommender);
	}

	private final List <Recommender> recommenders;

	public List <Recommender> getRecommenders()
	{
		return recommenders;
	}

	public Iterator <RecommenderModel> build(final Dataset dataset)
	{
		final Iterator <Recommender> iterator = recommenders.iterator();

		return new Iterator <RecommenderModel>()
		{
			@Override
			public boolean hasNext()
			{
				return iterator.hasNext();
			}

			@Override
			public RecommenderModel next()
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
