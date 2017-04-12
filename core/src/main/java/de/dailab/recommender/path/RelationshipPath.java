package de.dailab.recommender.path;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.DatasetEntry;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.RelationshipSet;
import de.dailab.recommender.dataset.RelationshipType;
import de.dailab.recommender.matrix.Entry;
import de.dailab.recommender.recommendation.Recommendation;

/**
 * Direct recommendation over one relationship type.
 * 
 * @author kunegis
 */
public final class RelationshipPath
    implements Path
{
	/**
	 * A path following a given relationship type, optionally backwards.
	 * 
	 * @param relationshipType The relationship type to follow
	 * @param backward Follow the relationship type backwards
	 * @param weight The weight of this path; must be less than one in absolute value
	 */
	public RelationshipPath(RelationshipType relationshipType, boolean backward, double weight)
	{
		assert Math.abs(weight) < 1;

		this.relationshipType = relationshipType;
		this.backward = backward;
		this.weight = weight;
	}

	/**
	 * The relationship path over a given relationship type with default weight.
	 * 
	 * @param relationshipType The relationship type to follow
	 * @param backward Follow edges backwards
	 */
	public RelationshipPath(RelationshipType relationshipType, boolean backward)
	{
		this(relationshipType, backward, WEIGHT_DEFAULT);
	}

	/**
	 * A path following a relationship type forwards.
	 * 
	 * @param relationshipType The relationship type to follow
	 */
	public RelationshipPath(RelationshipType relationshipType)
	{
		this(relationshipType, false, WEIGHT_DEFAULT);
	}

	private final RelationshipType relationshipType;

	private final boolean backward;

	/**
	 * The weight over this edge. This weight serves as a decay, so its absolute value must be less than one.
	 */
	private final double weight;

	@Override
	public Iterator <Recommendation> recommend(Dataset dataset, final Entity source,
	    final Map <Entity, Set <DatasetEntry>> trail)
	{
		final RelationshipSet relationshipSet = dataset.getRelationshipSet(relationshipType);

		final EntityType entityTypeFrom = backward ? relationshipSet.getObject() : relationshipSet.getSubject();
		final EntityType entityTypeTo = backward ? relationshipSet.getSubject() : relationshipSet.getObject();

		if (!source.getType().equals(entityTypeFrom)) return new ArrayList <Recommendation>().iterator();

		final Iterable <Entry> iterable = backward ? relationshipSet.getMatrix().col(source.getId()) : relationshipSet
		    .getMatrix().row(source.getId());

		final Iterator <Entry> iterator = iterable.iterator();

		return new Iterator <Recommendation>()
		{
			@Override
			public boolean hasNext()
			{
				return iterator.hasNext();
			}

			@Override
			public Recommendation next()
			{
				final Entry entry = iterator.next();
				final Entity entity = new Entity(entityTypeTo, entry.index);
				final double relationshipWeight = entry.value;

				Set <DatasetEntry> datasetEntrySet = trail.get(entity);
				if (datasetEntrySet == null)
				{
					datasetEntrySet = new HashSet <DatasetEntry>();
					trail.put(entity, datasetEntrySet);
				}
				datasetEntrySet.add(new DatasetEntry(source, relationshipType, !backward, relationshipWeight));

				return new Recommendation(entity, entry.value);
			}

			@Override
			public void remove()
			{
				throw new UnsupportedOperationException();
			}
		};
	}

	/**
	 * The default weight. This value serves as a decay, so it is slightly less than one.
	 */
	public static final double WEIGHT_DEFAULT = .95;

	@Override
	public Path invert()
	{
		return new RelationshipPath(relationshipType, !backward, weight);
	}

	@Override
	public String toString()
	{
		return String.format("%s%s", backward ? "~" : "", relationshipType);
	}
}
