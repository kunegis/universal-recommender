package de.dailab.recommender.path;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.DatasetEntry;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.recommend.AggregateIterator;
import de.dailab.recommender.recommendation.Recommendation;

/**
 * A path composed of multiple parallel paths.
 * 
 * @author kunegis
 */
public final class ParallelPath
    implements Path
{
	/**
	 * A path consisting of several given parallel paths
	 * 
	 * @param paths The parallel paths to follow
	 */
	public ParallelPath(Path... paths)
	{
		this.paths = paths;
	}

	/**
	 * The parallel paths.
	 */
	private final Path paths[];

	@Override
	public Iterator <Recommendation> recommend(Dataset dataset, Entity source, Map <Entity, Set <DatasetEntry>> trail)
	{
		final Collection <Iterator <Recommendation>> iterators = new ArrayList <Iterator <Recommendation>>();

		for (final Path path: paths)
			iterators.add(path.recommend(dataset, source, trail));

		return new AggregateIterator <Recommendation>(iterators);
	}

	@Override
	public Path invert()
	{
		final Path invertedPaths[] = new Path[paths.length];

		for (int i = 0; i < paths.length; ++i)
			invertedPaths[paths.length - i - 1] = paths[i].invert();

		return new ParallelPath(invertedPaths);
	}

	@Override
	public String toString()
	{
		String ret = "";
		for (final Path path: paths)
		{
			if (!ret.isEmpty()) ret += " | ";
			ret += path;
		}
		return "(" + ret + ")";
	}
}
