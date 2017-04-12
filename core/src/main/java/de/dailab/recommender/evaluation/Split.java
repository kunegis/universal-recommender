package de.dailab.recommender.evaluation;

import java.util.Random;

import de.dailab.recommender.dataset.Dataset;
import de.dailab.recommender.dataset.RelationshipSet;
import de.dailab.recommender.dataset.RelationshipType;
import de.dailab.recommender.dataset.UnirelationalDataset;
import de.dailab.recommender.matrix.Entry;
import de.dailab.recommender.matrix.FullEntry;
import de.dailab.recommender.matrix.Matrix;
import de.dailab.recommender.matrix.template.MatrixFactory;

/**
 * A split of a dataset into training and test sets.
 * <p>
 * There are two types of splits: prediction and recommendation splits.
 * <ul>
 * <li>Prediction splits choose edges at random
 * <li>Recommendation splits choose a fixed number of edges from users that have a minimum of neighbors.
 * </ul>
 * 
 * @author kunegis
 */
public class Split
{
	/**
	 * The training set. Contains the same entity and relationship types as the initial dataset but one relationship
	 * type has has relationships removed.
	 */
	public final Dataset training;

	/**
	 * The test set. Values in the matrix represent values to predict, or entries to predict.
	 */
	public final RelationshipSet test;

	/**
	 * Split a given dataset into training and test sets for evaluation. Only the relationship set of the given type is
	 * split. Edges are chosen randomly in this relationship set according to the given split type.
	 * <p>
	 * Evaluation will be by predicting or recommending the relationship set specified by the given relationship type.
	 * Given subjects from that set, objects have to be predicted or recommended.
	 * 
	 * @param dataset The dataset to split. The dataset is modified and saved as the training set.
	 * @param relationshipType The type of the relationship set to split
	 * @param type Type of split
	 * @param testSize Size of the test set counted by the proportion of relationships in the matrix of the given
	 *        relationship type; e.g. 0.3 for a 30% test set
	 */
	public Split(Dataset dataset, RelationshipType relationshipType, SplitType type, double testSize)
	{
		this.training = dataset;

		final RelationshipSet relationshipSet = dataset.getExistingRelationshipSet(relationshipType);

		final Matrix trainingMatrix = MatrixFactory.newMemoryMatrix(relationshipSet.getMatrix());
		final Matrix testMatrix = MatrixFactory.newMemoryMatrix(relationshipSet.getMatrix());

		final Random random = new Random();

		final Matrix oldMatrix = relationshipSet.getMatrix();

		if (type instanceof PredictorSplitType)
		{
			for (final FullEntry entry: oldMatrix.all())
			{
				(random.nextDouble() < testSize ? testMatrix : trainingMatrix).set(entry.rowIndex, entry.colIndex,
				    entry.value);
			}

			if (relationshipSet.getWeightRange().isZeroSignificant())
			{
				int zeroTestSize = testMatrix.nnz();
				while (zeroTestSize > 0)
				{
					final int i = random.nextInt(testMatrix.rows());
					final int j = random.nextInt(testMatrix.cols());
					if (testMatrix.get(i, j) != 0) continue;
					testMatrix.set(i, j, 0);
					--zeroTestSize;
				}
			}

		}
		else if (type instanceof RecommenderSplitType)
		{
			final RecommenderSplitType recommenderSplitType = (RecommenderSplitType) type;

			final int minimumEdges = recommenderSplitType.minimum;

			boolean empty = true;
			for (int i = 0; i < oldMatrix.rows(); ++i)
			{
				if (oldMatrix.getRowCount(i) < minimumEdges)
				{
					for (final Entry entry: oldMatrix.row(i))
						trainingMatrix.set(i, entry.index, entry.value);
					continue;
				}

				for (final Entry entry: oldMatrix.row(i))
				{
					int count = 0;
					if (random.nextDouble() < testSize)
					{
						empty = false;
						testMatrix.set(i, entry.index, entry.value);
						++count;
					}
					else
						trainingMatrix.set(i, entry.index, entry.value);

					while (count > 0)
					{
						final int j = random.nextInt(oldMatrix.cols());
						if (oldMatrix.get(i, j) != 0) continue;
						--count;
						testMatrix.set(i, j, 0);
					}
				}
			}
			if (empty)
			    throw new IllegalArgumentException(String.format("RecommenderSplitType.minimum = %d is too big:  "
			        + "test set is empty; most entities have less than %d neighbors", minimumEdges, minimumEdges));
		}

		relationshipSet.setMatrix(trainingMatrix);

		test = new RelationshipSet(relationshipSet);
		test.setMatrix(testMatrix);
	}

	/**
	 * Split using the default test size.
	 * 
	 * @param dataset The dataset to split
	 * @param relationshipType The relationship type to use as recommendation test set
	 * @param type The type of split
	 */
	public Split(Dataset dataset, RelationshipType relationshipType, SplitType type)
	{
		this(dataset, relationshipType, type, DEFAULT_TEST_SIZE);
	}

	/**
	 * Split a dataset for prediction using the default test size.
	 * 
	 * @param dataset The dataset to split
	 * @param relationshipType The relationship type to split off
	 */
	public Split(Dataset dataset, RelationshipType relationshipType)
	{
		this(dataset, relationshipType, new PredictorSplitType(), DEFAULT_TEST_SIZE);
	}

	/**
	 * Split the unique relationship type in the given dataset.
	 * 
	 * @param unirelationalDataset The unirelational dataset to split
	 * @param splitType The split split to use
	 */
	public Split(UnirelationalDataset unirelationalDataset, SplitType splitType)
	{
		this(unirelationalDataset, unirelationalDataset.getUniqueRelationshipType(), splitType);
	}

	/**
	 * Default size of the test set, in relation to total size.
	 */
	private final static double DEFAULT_TEST_SIZE = .2;

	@Override
	public String toString()
	{
		return String.format("%s:  %d", test.getType(), test.getMatrix().nnz());
	}
}
