package de.dailab.recommender.btc;

import de.dailab.recommender.semantic.SemanticStoreStatistics;
import de.dailab.recommender.semantic.SemanticStoreStatistics.SortedType;

/**
 * Compute statistics about the BTC dataset.
 * 
 * @author kunegis
 */
public class BtcStatistics
{
	/**
	 * Compute statistics about the BTC dataset.
	 */
	public void btcStatistics()
	{
		final SemanticStoreStatistics semanticStoreStatistics = new SemanticStoreStatistics(BtcDataset
		    .createSemanticStoreConnection());

		System.out.println("Entity types:");

		for (final SortedType sortedType: semanticStoreStatistics.entityTypes)
		{
			System.out.println(sortedType);
		}

		System.out.println("Relationship types:");

		for (final SortedType sortedType: semanticStoreStatistics.relationshipTypes)
		{
			System.out.println(sortedType);
		}
	}
}
