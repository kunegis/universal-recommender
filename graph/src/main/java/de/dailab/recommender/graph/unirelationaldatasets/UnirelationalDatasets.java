package de.dailab.recommender.graph.unirelationaldatasets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.dailab.recommender.graph.UnirelationalGraphStoreDataset;
import de.dailab.recommender.text.TextSyntaxException;

/**
 * Static methods for accessing the unirelational datasets in the Graph Store.  
 * In particular, this class contains the list of all unirelational datasets 
 * in the Graph Store. 
 */
public class UnirelationalDatasets
{
	/**
	 * Load a unirelational dataset of a given name. 
	 * 
	 * @param name Name of the dataset, e.g. "advogato"
	 * @throws IOException on IO errors
	 * @throws TextSyntaxException on syntax errors
	 * @return The loaded unirelational dataset
	 */
	public static UnirelationalGraphStoreDataset load(String name)
	       throws IOException, TextSyntaxException
	{
		return new UnirelationalGraphStoreDataset(name); 
	}

	/**
	 * @return the list of unirelational Graph Store datasets, indexed by their underlying name. 
	 */
	public static Set <String> getDatasets()
	{
		return datasets.keySet(); 
	}

	private final static Map <String, Class <? extends UnirelationalGraphStoreDataset>> datasets = 
		new HashMap <String, Class <? extends UnirelationalGraphStoreDataset>> (); 
	static 
	{
	    datasets.put("advogato", AdvogatoDataset.class); 
	    datasets.put("amazon0601", Amazon0601Dataset.class); 
	    datasets.put("bibsonomy-2ti", Bibsonomy2tiDataset.class); 
	    datasets.put("bibsonomy-2ui", Bibsonomy2uiDataset.class); 
	    datasets.put("bibsonomy-2ut", Bibsonomy2utDataset.class); 
	    datasets.put("btc_aggregate", BtcAggregateDataset.class); 
	    datasets.put("bx", BxDataset.class); 
	    datasets.put("ca-AstroPh", CaAstroPhDataset.class); 
	    datasets.put("citeseer", CiteseerDataset.class); 
	    datasets.put("citeulike-tags", CiteulikeTagsDataset.class); 
	    datasets.put("cit-HepPh", CitHepPhDataset.class); 
	    datasets.put("cit-HepTh", CitHepThDataset.class); 
	    datasets.put("dblp-author", DblpAuthorDataset.class); 
	    datasets.put("dblp-cite", DblpCiteDataset.class); 
	    datasets.put("dblp_coauthor", DblpCoauthorDataset.class); 
	    datasets.put("delicious", DeliciousDataset.class); 
	    datasets.put("delicious-it", DeliciousItDataset.class); 
	    datasets.put("delicious-ui", DeliciousUiDataset.class); 
	    datasets.put("delicious-ut", DeliciousUtDataset.class); 
	    datasets.put("email-EuAll", EmailEuAllDataset.class); 
	    datasets.put("enron", EnronDataset.class); 
	    datasets.put("epinions", EpinionsDataset.class); 
	    datasets.put("facebook-wosn-links", FacebookWosnLinksDataset.class); 
	    datasets.put("facebook-wosn-wall", FacebookWosnWallDataset.class); 
	    datasets.put("filmtipset", FilmtipsetDataset.class); 
	    datasets.put("flickr-growth", FlickrGrowthDataset.class); 
	    datasets.put("hep-th-citations", HepThCitationsDataset.class); 
	    datasets.put("internet-growth", InternetGrowthDataset.class); 
	    datasets.put("jester", JesterDataset.class); 
	    datasets.put("libimseti", LibimsetiDataset.class); 
	    datasets.put("movielens-100k_rating", Movielens100kRatingDataset.class); 
	    datasets.put("movielens-100k_rating-unweighted", Movielens100kRatingUnweightedDataset.class); 
	    datasets.put("movielens-10m_rating", Movielens10mRatingDataset.class); 
	    datasets.put("movielens-10m_rating-unweighted", Movielens10mRatingUnweightedDataset.class); 
	    datasets.put("movielens-1m", Movielens1mDataset.class); 
	    datasets.put("movielens-1m_unweighted", Movielens1mUnweightedDataset.class); 
	    datasets.put("music_similar", MusicSimilarDataset.class); 
	    datasets.put("netflix", NetflixDataset.class); 
	    datasets.put("patentcite", PatentciteDataset.class); 
	    datasets.put("reuters", ReutersDataset.class); 
	    datasets.put("slashdot-zoo", SlashdotZooDataset.class); 
	    datasets.put("soc-Slashdot0902", SocSlashdot0902Dataset.class); 
	    datasets.put("trec-wt10g", TrecWt10gDataset.class); 
	    datasets.put("wiki-de-edit", WikiDeEditDataset.class); 
	    datasets.put("wiki-edit-arwiki", WikiEditArwikiDataset.class); 
	    datasets.put("wiki-edit-bnwiki", WikiEditBnwikiDataset.class); 
	    datasets.put("wiki-edit-brwiki", WikiEditBrwikiDataset.class); 
	    datasets.put("wiki-edit-cywiki", WikiEditCywikiDataset.class); 
	    datasets.put("wiki-edit-elwiki", WikiEditElwikiDataset.class); 
	    datasets.put("wiki-edit-eowiki", WikiEditEowikiDataset.class); 
	    datasets.put("wiki-edit-euwiki", WikiEditEuwikiDataset.class); 
	    datasets.put("wiki-edit-frwikibooks", WikiEditFrwikibooksDataset.class); 
	    datasets.put("wiki-edit-glwiki", WikiEditGlwikiDataset.class); 
	    datasets.put("wiki-edit-htwiki", WikiEditHtwikiDataset.class); 
	    datasets.put("wiki-edit-lvwiki", WikiEditLvwikiDataset.class); 
	    datasets.put("wiki-edit-ocwiki", WikiEditOcwikiDataset.class); 
	    datasets.put("wiki-edit-ptwiki", WikiEditPtwikiDataset.class); 
	    datasets.put("wiki-edit-ruwiki", WikiEditRuwikiDataset.class); 
	    datasets.put("wiki-edit-skwiki", WikiEditSkwikiDataset.class); 
	    datasets.put("wiki-edit-srwiki", WikiEditSrwikiDataset.class); 
	    datasets.put("wiki-edit-svwiki", WikiEditSvwikiDataset.class); 
	    datasets.put("wiki-edit-viwiki", WikiEditViwikiDataset.class); 
	    datasets.put("wiki-en-cat", WikiEnCatDataset.class); 
	    datasets.put("wiki-en-link", WikiEnLinkDataset.class); 
	    datasets.put("wikipedia-growth", WikipediaGrowthDataset.class); 
	    datasets.put("wiki-Talk", WikiTalkDataset.class); 
	    datasets.put("wiki-Vote", WikiVoteDataset.class); 
	    datasets.put("www", WwwDataset.class); 
	    datasets.put("youtube-d-growth", YoutubeDGrowthDataset.class); 
	    datasets.put("youtube-u-growth", YoutubeUGrowthDataset.class); 
	}
}
