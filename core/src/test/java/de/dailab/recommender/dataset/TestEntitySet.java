package de.dailab.recommender.dataset;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * Test entity sets.
 * 
 * @author kunegis
 */
public class TestEntitySet
{
	/**
	 * Test entity sets.
	 */
	@Test
	public void testEntitySet()
	{
		final int COUNT = 1000;
		final MetadataName METADATA_TITLE = new MetadataName("title");
		final MetadataName METADATA_AGE = new MetadataName("age");

		/* Newly created entity set has size 0 */
		final EntitySet entitySet = new EntitySet(new EntityType("thing"));
		assert entitySet.size() == 0;

		/* Set size */
		entitySet.setSize(1000);

		/* Set metadata names */
		final List <MetadataName> metadataNames = new ArrayList <MetadataName>();
		metadataNames.add(METADATA_TITLE);
		metadataNames.add(METADATA_AGE);
		final List <Object> sampleMetadata = new ArrayList <Object>();
		sampleMetadata.add("String");
		sampleMetadata.add(new Integer(123));
		entitySet.setMetadataNames(metadataNames, sampleMetadata);

		/* Set metadata */
		for (int i = 0; i < COUNT; ++i)
		{
			final List <Object> metadataValues = new ArrayList <Object>();
			metadataValues.add("number " + i);
			metadataValues.add(new Integer((i + 1) * (i + 2)));
			entitySet.setMetadataValues(i, sampleMetadata);
		}

		/* Add entity */
		final int newId = entitySet.addEntity();
		assert newId == COUNT;
		final List <Object> metadataValues = new ArrayList <Object>();
		metadataValues.add("New One");
		metadataValues.add(new Integer(4321));
		entitySet.setMetadataValues(newId, metadataValues);

		final String newTitle = (String) entitySet.getMetadata(newId, METADATA_TITLE);
		assert newTitle.equals("New One");
	}
}
