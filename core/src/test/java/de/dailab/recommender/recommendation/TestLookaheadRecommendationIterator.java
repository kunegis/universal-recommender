package de.dailab.recommender.recommendation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.dataset.EntityType;

/**
 * Test the lookahead recommendation iterator.
 * 
 * @author kunegis
 */
public class TestLookaheadRecommendationIterator
{
	/**
	 * Test with one list.
	 */
	@Test
	public void test()
	{
		final EntityType entityType = new EntityType("item");

		final Entity e1 = new Entity(entityType, 1);
		final Entity e2 = new Entity(entityType, 2);
		final Entity e3 = new Entity(entityType, 3);
		final Entity e4 = new Entity(entityType, 4);
		final Entity e5 = new Entity(entityType, 5);
		final Entity e6 = new Entity(entityType, 6);
		final Entity e7 = new Entity(entityType, 7);
		final Entity e8 = new Entity(entityType, 8);
		final Entity e9 = new Entity(entityType, 9);
		final Entity e10 = new Entity(entityType, 10);
		final Entity e11 = new Entity(entityType, 11);
		final Entity e12 = new Entity(entityType, 12);
		final Entity e13 = new Entity(entityType, 13);
		final Entity e14 = new Entity(entityType, 14);
		final Entity e15 = new Entity(entityType, 15);

		List <Recommendation> list = new ArrayList <Recommendation>();

		list.add(new Recommendation(e1, 9));
		list.add(new Recommendation(e2, 8));
		list.add(new Recommendation(e3, 7));
		list.add(new Recommendation(e4, 5));
		list.add(new Recommendation(e5, 6));
		list.add(new Recommendation(e6, 1));
		list.add(new Recommendation(e7, .5)); /* must be summed to 1.25 */
		list.add(new Recommendation(e7, .5));
		list.add(new Recommendation(e7, .25));
		list.add(new Recommendation(e8, 0));
		list.add(new Recommendation(e9, -1));
		list.add(new Recommendation(e10, -1.1));
		list.add(new Recommendation(e11, -1.2));
		list.add(new Recommendation(e12, -1.3));
		list.add(new Recommendation(e13, -1.4));
		list.add(new Recommendation(e14, -1.5));
		list.add(new Recommendation(e15, -.5)); /* must look ahead to this one */
		list.add(new Recommendation(e7, 1)); /* must be ignored */

		final Iterator <Recommendation> iterator = new LookaheadRecommendationIterator(3, list.iterator());

		list = new ArrayList <Recommendation>();
		while (iterator.hasNext())
			list.add(iterator.next());

		assert list.size() == 15;
		assert list.get(0).getEntity().getId() == 1;
		assert list.get(1).getEntity().getId() == 2;
		assert list.get(2).getEntity().getId() == 3;
		assert list.get(3).getEntity().getId() == 5;
		assert list.get(4).getEntity().getId() == 4;
		assert list.get(5).getEntity().getId() == 7;
		assert list.get(5).getScore() == 1.25;
		assert list.get(6).getEntity().getId() == 6;
		assert list.get(7).getEntity().getId() == 8;
		assert list.get(8).getEntity().getId() == 9;
		assert list.get(9).getEntity().getId() == 10;
		assert list.get(10).getEntity().getId() == 11;
		assert list.get(11).getEntity().getId() == 12;
		assert list.get(12).getEntity().getId() == 15;
		assert list.get(13).getEntity().getId() == 13;
		assert list.get(14).getEntity().getId() == 14;
	}
}
