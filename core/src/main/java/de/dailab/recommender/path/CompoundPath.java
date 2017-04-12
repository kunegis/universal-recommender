package de.dailab.recommender.path;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Map.Entry;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.DatasetEntry;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.recommendation.Recommendation;

/**
 * Recommendation path composed of multiple paths in series.
 * 
 * @author kunegis
 */
public class CompoundPath
    implements Path
{
	/**
	 * A compound path consisting of given chained paths.
	 * 
	 * @param paths Paths to follow in a chain
	 */
	public CompoundPath(Path... paths)
	{
		this.paths = paths;
	}

	private final Path paths[];

	@Override
	public Iterator <Recommendation> recommend(Dataset dataset, Entity source, Map <Entity, Set <DatasetEntry>> trail)
	{
		Map <EntityType, Map <Integer, Double>> vectors = new HashMap <EntityType, Map <Integer, Double>>();
		final Map <Integer, Double> initialVector = new HashMap <Integer, Double>();
		initialVector.put(source.getId(), 1.);
		vectors.put(source.getType(), initialVector);

		for (int i = 0; i < paths.length; ++i)
		{
			final Map <EntityType, Map <Integer, Double>> newVectors = new HashMap <EntityType, Map <Integer, Double>>();

			for (final java.util.Map.Entry <EntityType, Map <Integer, Double>> e: vectors.entrySet())
			{
				for (final Entry <Integer, Double> entry: e.getValue().entrySet())
				{
					final Iterator <Recommendation> iterator = paths[i].recommend(dataset, new Entity(e.getKey(), entry
					    .getKey()), trail);
					while (iterator.hasNext())
					{
						final Recommendation recommendation = iterator.next();

						Map <Integer, Double> newVector = newVectors.get(recommendation.getEntity().getType());
						if (newVector == null)
						{
							newVector = new HashMap <Integer, Double>();
							newVectors.put(recommendation.getEntity().getType(), newVector);
						}

						newVector.put(recommendation.getEntity().getId(), entry.getValue() * recommendation.getScore());
					}
				}
			}

			vectors = newVectors;
		}

		final SortedSet <Recommendation> ret = new TreeSet <Recommendation>();
		for (final java.util.Map.Entry <EntityType, Map <Integer, Double>> e: vectors.entrySet())
			for (final Entry <Integer, Double> entry: e.getValue().entrySet())
				ret.add(new Recommendation(new Entity(e.getKey(), entry.getKey()), entry.getValue()));

		return ret.iterator();
	}

	@Override
	public Path invert()
	{
		final Path invertedPaths[] = new Path[paths.length];

		for (int i = 0; i < paths.length; ++i)
		{
			invertedPaths[i] = paths[i].invert();
		}

		return new CompoundPath(invertedPaths);
	}

	@Override
	public String toString()
	{
		String ret = "";
		for (final Path path: paths)
		{
			if (!ret.isEmpty()) ret += " ";
			ret += path;
		}
		return ret;
	}
}
