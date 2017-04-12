package de.dailab.recommender.path;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.iterators.TransformIterator;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.DatasetEntry;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.recommend.AggregateIterator;
import de.dailab.recommender.recommendation.Recommendation;

/**
 * A multipath that implements recommend(Entity[]) in terms of recommender(Entity).
 * 
 * @author kunegis
 */
public class AbstractMultiPath
    implements MultiPath
{
	/**
	 * A multipath implemented in terms of a given path.
	 * 
	 * @param path The underlying path
	 */
	public AbstractMultiPath(Path path)
	{
		this.path = path;
	}

	private final Path path;

	@SuppressWarnings("unchecked")
	@Override
	public Iterator <Recommendation> recommend(Dataset dataset, Map <Entity, Double> sources,
	    Map <Entity, Set <DatasetEntry>> trail)
	{
		final List <Iterator <Recommendation>> iterators = new ArrayList <Iterator <Recommendation>>();

		for (final Entry <Entity, Double> entry: sources.entrySet())
		{
			iterators.add(new TransformIterator(path.recommend(dataset, entry.getKey(), trail), new Transformer()
			{
				@Override
				public Object transform(Object arg0)
				{
					final Recommendation recommendation = (Recommendation) arg0;

					return new Recommendation(recommendation.getEntity(), recommendation.getScore() * entry.getValue());
				}
			}));
		}

		return new AggregateIterator <Recommendation>(iterators);
	}

	@Override
	public Path invert()
	{
		return new AbstractMultiPath(path.invert());
	}

	@Override
	public Iterator <Recommendation> recommend(Dataset dataset, Entity source, Map <Entity, Set <DatasetEntry>> trail)
	{
		return path.recommend(dataset, source, trail);
	}
}
