package de.dailab.recommender.dataset;

import java.util.Iterator;

import de.dailab.recommender.matrix.Entry;
import de.dailab.recommender.matrix.Matrix;
import de.dailab.recommender.matrix.ZeroMatrix;
import de.dailab.recommender.matrix.template.MatrixFactory;

/**
 * The set of relationships of a single relationship type. Equivalently, a matrix annotated with row and column entity
 * types.
 * <p>
 * All fields are final except the data matrix.
 * <p>
 * The relationship type and subject and object entity types must not be NULL.
 * <p>
 * The matrix contains the actual relationship information. The row indexes of the matrix are the entity IDs of subject
 * entities and the column indexes are the entity IDs of the object entities.
 * 
 * @author kunegis
 */
public final class RelationshipSet
{
	private final RelationshipType type;

	/**
	 * Row and column entity types. The entries of the matrix correspond to edges between entities of these types. If
	 * this.formatType is SYM or ASYM, subject and object must be equal.
	 */
	private final EntityType subject, object;

	private final RelationshipFormat relationshipFormat;
	private final WeightRange weightRange;

	/**
	 * The relationship data. The matrix may be of any subtype of Matrix, including those backed by a remote data
	 * source. The rows and columns correspond to subject and object indices, respectively. The weights must be
	 * consistent with this.weightType. If this.formatType is ASYM, the matrix must be square. If this.formatType is
	 * SYM, the matrix must be square and symmetric. This is the only non-final field. The size of the matrix should not
	 * be changed.
	 */
	private Matrix matrix;

	/**
	 * Initialize an instance. Set the final fields only. The matrix is NULL initially.
	 * 
	 * @param type Type of the relationships
	 * @param subject The source entity type of the relationships. May be NULL.
	 * @param object The object entity type of the relationships. May be NULL.
	 * @param relationshipFormat The format type of this relationship type
	 * @param weightRange Type of allowed weights
	 */
	public RelationshipSet(RelationshipType type, EntityType subject, EntityType object,
	    RelationshipFormat relationshipFormat, WeightRange weightRange)
	{
		assert type != null;
		assert subject != null;
		assert object != null;

		this.type = type;
		this.subject = subject;
		this.object = object;
		this.relationshipFormat = relationshipFormat;
		this.weightRange = weightRange;
	}

	/**
	 * An unweighted relationship type. The relationship format is inferred from the entity types; it is asymmetric when
	 * they are equal.
	 * 
	 * @param relationshipType Type of all relationships
	 * @param subject Subject entity type
	 * @param object Object entity type
	 */
	public RelationshipSet(RelationshipType relationshipType, EntityType subject, EntityType object)
	{
		this(relationshipType, subject, object, subject.equals(object) ? RelationshipFormat.ASYM
		    : RelationshipFormat.BIP, WeightRange.UNWEIGHTED);
	}

	/**
	 * Copy the final fields from another relationship set. Everything except the matrix is copied.
	 * 
	 * @param relationshipSet Relationship set to copy from
	 */
	public RelationshipSet(RelationshipSet relationshipSet)
	{
		this.type = relationshipSet.type;
		this.subject = relationshipSet.subject;
		this.object = relationshipSet.object;
		this.relationshipFormat = relationshipSet.relationshipFormat;
		this.weightRange = relationshipSet.weightRange;
	}

	/**
	 * @return The type of relationships in this relationship set
	 */
	public RelationshipType getType()
	{
		return type;
	}

	/**
	 * Get the entity type of subject entities.
	 * 
	 * @return The subject entity type. Corresponds to the row indices in the underlying matrix.
	 */
	public EntityType getSubject()
	{
		return subject;
	}

	/**
	 * Get the entity type of object entities.
	 * 
	 * @return The object entity type. Corresponds to the column indices in the underlying matrix.
	 */
	public EntityType getObject()
	{
		return object;
	}

	/**
	 * Get the format of this relationship set.
	 * 
	 * @return The format of relationships in this set
	 */
	public RelationshipFormat getRelationshipFormat()
	{
		return relationshipFormat;
	}

	/**
	 * @return The weight range of this relationship set
	 */
	public WeightRange getWeightRange()
	{
		return weightRange;
	}

	/**
	 * @return The underlying matrix. May be modified.
	 */
	public Matrix getMatrix()
	{
		return matrix;
	}

	/**
	 * @return whether the subject and object entity types differ
	 */
	public boolean isBipartite()
	{
		return !subject.equals(object);
	}

	/**
	 * Set the relationship data.
	 * <p>
	 * If a matrix was already set, the new matrix must have the same size as the previous matrix.
	 * 
	 * @param matrix The data. May be any subclass of Matrix. The row and columns correspond to subject and object
	 *        indices, respectively. The weights must be consistent with this.weightType. If this.formatType is ASYM,
	 *        the matrix must be square. If this.formatType is SYM, the matrix must be square and symmetric.
	 */
	public void setMatrix(Matrix matrix)
	{
		if (this.matrix != null)
		{
			assert this.matrix.rows() == matrix.rows();
			assert this.matrix.cols() == matrix.cols();
		}

		this.matrix = matrix;

		if (relationshipFormat == RelationshipFormat.ASYM || relationshipFormat == RelationshipFormat.SYM)
		{
			assert matrix.rows() == matrix.rows();
		}
	}

	/**
	 * Iterator over all objects connected to the given subject. This is slower than accessing the matrix directly
	 * because types are checked.
	 * 
	 * @param subject Subject; must be of the subject entity type
	 * @return Iterable over the objects connected to the subject in this relationship set
	 */
	public Iterable <DatasetEntry> row(Entity subject)
	{
		if (!subject.getType().equals(this.subject))
		    throw new IllegalArgumentException(String.format("Entity must be of type %s", this.subject));

		final Iterable <Entry> iterable = matrix.row(subject.getId());

		return new Iterable <DatasetEntry>()
		{
			@Override
			public Iterator <DatasetEntry> iterator()
			{
				final Iterator <Entry> iterator = iterable.iterator();

				return new Iterator <DatasetEntry>()
				{
					@Override
					public boolean hasNext()
					{
						return iterator.hasNext();
					}

					@Override
					public DatasetEntry next()
					{
						final Entry next = iterator.next();

						return new DatasetEntry(new Entity(object, next.index), type, true, next.value);
					}

					@Override
					public void remove()
					{
						iterator.remove();
					}
				};
			}
		};
	}

	/**
	 * Iterator over all subjects connected to the given object. This is slower than accessing the matrix directly
	 * because types are checked.
	 * 
	 * @param object Object; must be of the object entity type
	 * @return Iterable over the subjects connected to the object in this relationship set
	 */
	public Iterable <DatasetEntry> col(Entity object)
	{
		if (!object.getType().equals(this.object))
		    throw new IllegalArgumentException(String.format("Entity must be of type %s", this.object));

		final Iterable <Entry> iterable = matrix.row(object.getId());

		return new Iterable <DatasetEntry>()
		{
			@Override
			public Iterator <DatasetEntry> iterator()
			{
				final Iterator <Entry> iterator = iterable.iterator();

				return new Iterator <DatasetEntry>()
				{
					@Override
					public boolean hasNext()
					{
						return iterator.hasNext();
					}

					@Override
					public DatasetEntry next()
					{
						final Entry next = iterator.next();

						return new DatasetEntry(new Entity(subject, next.index), type, true, next.value);
					}

					@Override
					public void remove()
					{
						iterator.remove();
					}
				};
			}
		};
	}

	@Override
	public String toString()
	{
		return type.toString();
	}

	/**
	 * Build a memory-held matrix suitable for the given size and weight range.
	 * <p>
	 * This methods wraps MatrixFactory.build() by interpreting the weight range argument.
	 * 
	 * @param m Row count
	 * @param n Column count
	 * @param weightRange The weight range of matrix entries
	 * 
	 * @return A suitable matrix for the given arguments
	 * 
	 * @see MatrixFactory
	 */
	public static Matrix buildMatrix(int m, int n, WeightRange weightRange)
	{
		assert m >= 0;
		assert n >= 0;

		if (m == 0 || n == 0) return new ZeroMatrix(m, n);

		if (weightRange == WeightRange.UNWEIGHTED)
			return MatrixFactory.newMemoryMatrixUnweighted(m, n);
		else
			return MatrixFactory.newMemoryMatrix(m, n);
	}
}
