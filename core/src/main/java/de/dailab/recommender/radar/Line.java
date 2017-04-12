package de.dailab.recommender.radar;

import de.dailab.recommender.dataset.RelationshipType;

/**
 * A line connects two points and represents a relationship.
 * 
 * @author kunegis
 */
public class Line
{
	/**
	 * The origin of this line. Corresponds to the subject entity of the relationship represented by this line.
	 */
	public final Point subject;

	/**
	 * The target of this line. Corresponds to the object entity of the relationship represented by this line.
	 */
	public final Point object;

	/**
	 * The relationship type represented by this line.
	 */
	public final RelationshipType relationshipType;

	/**
	 * The weight of this relationship; one when the relationship type is unweighted.
	 */
	public final double weight;

	/**
	 * A line connecting the given subject and object of the given relationship type and weight.
	 * 
	 * @param subject The subject point
	 * @param object The object point
	 * @param relationshipType The underlying relationship type
	 * @param weight The relationship weight
	 */
	public Line(Point subject, Point object, RelationshipType relationshipType, double weight)
	{
		this.subject = subject;
		this.object = object;
		this.relationshipType = relationshipType;
		this.weight = weight;
	}
}
