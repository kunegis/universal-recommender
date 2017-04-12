package de.dailab.recommender.path;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.iterators.TransformIterator;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.DatasetEntry;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.recommendation.Recommendation;

/**
 * A weighted path.
 * 
 * @author kunegis
 */
public class WeightedPath
    implements Path
{
	/**
	 * Weight a given path. Passing the weight one will result in an identical path.
	 * 
	 * @param weight The weight to apply to the path
	 * @param path The path to weight
	 */
	public WeightedPath(double weight, Path path)
	{
		assert path != null;
		this.weight = weight;
		this.path = path;
	}

	@Override
	public Path invert()
	{
		return new WeightedPath(weight, path.invert());
	}

	@SuppressWarnings("unchecked")
	@Override
	public Iterator <Recommendation> recommend(Dataset dataset, Entity source, Map <Entity, Set <DatasetEntry>> trail)
	{
		final Iterator <Recommendation> iterator = path.recommend(dataset, source, trail);

		return new TransformIterator(iterator, new Transformer()
		{
			@Override
			public Object transform(Object arg0)
			{
				final Recommendation recommendation = (Recommendation) arg0;

				return new Recommendation(recommendation.getEntity(), weight * recommendation.getScore());
			}
		});
	}

	private final double weight;
	private final Path path;
}
