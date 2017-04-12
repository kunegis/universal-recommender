package de.dailab.recommender.semantic;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.Entity;
import de.dailab.recommender.radar.Line;
import de.dailab.recommender.radar.Radar;
import de.dailab.recommender.recommend.RecommendationResult;
import de.dailab.recommender.recommend.RecommenderModel;

/**
 * Semantic wrapper around the radar.
 * <p>
 * The coordinates are discarded by the semantic radar. The result is therefore
 * just a subgraph of the original network.
 * 
 * @deprecated Use SemanticTrail
*/
@Deprecated
public class SemanticRadar
    extends Radar
{
	/**
	 * Compute a radar from a given latent predictor model and an entity list. The latent predictor model must be built
	 * from a dataset where all entities have the metadata SemanticStoreDataset.METADATA_URI, as is the case for all
	 * semantic store datasets.
	 * 
	 * @param entities The entities to show in any case
	 * @param dataset Lines are computed from this dataset
	 * @param recommenderModel If not NULL, used to find connecting entities
	 */
	public SemanticRadar(Entity[] entities, Dataset dataset, RecommenderModel recommenderModel)
	{
		super(entities, dataset, null, recommenderModel);

	this.dataset = dataset;
    }

    /**
     * A semantic radar where no other entities are shown except those given.
     * 
     * @param entities
     *            The entities to show
     * @param dataset
     *            The dataset used
     */
    public SemanticRadar(Entity[] entities, Dataset dataset) {
	this(entities, dataset, null);
    }

    /**
     * A semantic radar based on given recommendation results. The radar will
     * display the edges visited during recommendation.
     * <p>
     * The recommender used must support reporting visited edges.
     * 
     * @param recommendationResult
     *            The results of recommendation to visualize
     * @param dataset
     *            The dataset to visualize
     * 
     * @throws UnsupportedOperationException
     *             When the recommender used does not support reporting visited
     *             entities
     */
    public SemanticRadar(RecommendationResult recommendationResult,
	    Dataset dataset) {
	super(recommendationResult, dataset);

	this.dataset = dataset;
    }

    private final Dataset dataset;

    /**
     * Get the Jena model associated with this radar.
     * 
     * @return The Jena model associated with this radar
     */
    public Model getModel() {
	final Model model = ModelFactory.createDefaultModel();

	for (final Line line : getLines()) {
	    Resource r1 = model.createResource(dataset.getMetadata(
		    line.subject.entity, SemanticStoreDataset.METADATA_URI)
		    .toString(), ModelFactory.createDefaultModel()
		    .createResource(line.subject.entity.getType().toString()));

	    Resource r2 = model.createResource(dataset.getMetadata(
		    line.object.entity, SemanticStoreDataset.METADATA_URI)
		    .toString(), ModelFactory.createDefaultModel()
		    .createResource(line.object.entity.getType().toString()));
	    r1.addProperty(ModelFactory.createDefaultModel().createProperty(
		    line.relationshipType.getName()), r2);
	}

	return model;
    }
}
