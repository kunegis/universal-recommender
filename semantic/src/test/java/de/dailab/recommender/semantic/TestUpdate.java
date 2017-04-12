package de.dailab.recommender.semantic;

import java.util.Random;

import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import org.junit.Test;

import de.dailab.recommender.dataset.EntityType;
import de.dailab.recommender.dataset.RelationshipType;

/**
 * Test updating a semantic store dataset.
 * 
 * @author kunegis
 */
public class TestUpdate
{
	/**
	 * Create a semantic store dataset without reading any data from the semantic store, and then adding entities and
	 * relationships from a Jena model.
	 * 
	 * @throws SparqlException on SPARQL exception; cannot happen
	 */
	@Test
	public void testUpdate()
	    throws SparqlException
	{
		/* Create empty */
		final Sparqlable sparqlable = null;
		final SemanticStoreDataset semanticStoreDataset = new SemanticStoreDataset(sparqlable, MODEL);

		/* Add entities and relationships */
		final com.hp.hpl.jena.rdf.model.Model jenaModel = ModelFactory.createDefaultModel();
		final Resource user = jenaModel.createResource(USER.getName());
		final Property friend = jenaModel.createProperty(FRIEND.getName());

		final Random random = new Random();

		final Resource resources[] = new Resource[N];
		for (int i = 0; i < N; ++i)
		{
			final Resource userI = resources[i] = jenaModel.createResource("http://example.org/user/" + i, user);
			if (i == 0) continue;
			for (int j = 0; j < Math.log(N); ++j)
			{
				userI.addProperty(friend, resources[random.nextInt(i)]);
			}
		}

		jenaModel.write(System.err);

		semanticStoreDataset.update(jenaModel);

		/* Read out */
		assert semanticStoreDataset.getEntitySet(USER).size() == N;
	}

	private static final int N = 50;

	private static final EntityType USER = new EntityType("ex:user");
	private static final RelationshipType FRIEND = new RelationshipType("ex:friend");

	private static final Model MODEL = new Model(new SemanticEntityType[]
	{ new RdfEntityType(USER, new SemanticMetadata[] {}), }, new SemanticRelationshipType[]
	{ new SemanticRelationshipType(FRIEND, USER, USER), }, Mode.FAIL, "PREFIX ex: <http://example.org/>\n");

}
