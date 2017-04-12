package de.dailab.recommender.radar;

import de.dailab.recommender.dataset.Entity;

/**
 * A point on a radar.
 * <p>
 * A point is represented by two-dimensional coordinates and an entity.
 * 
 * @author kunegis
 */
public class Point
{
	/**
	 * The entity represented by this point.
	 */
	public final Entity entity;

	/**
	 * The X coordinate on the radar of this point.
	 */
	public final double x;
	
	/**
	 * The Y coordinate on the radar of this point.
	 */
	public final double y;
	
	/**
	 * A point representing a given entity at given coordinates.
	 * 
	 * @param entity The entity represented by this point
	 * @param x The X coordinate of this point
	 * @param y The Y coordinate of this point
	 */
	public Point(Entity entity, double x, double y)
	{
		this.entity = entity;
		this.x = x;
		this.y = y;
	}
}
