package edu.albany.cs.mallet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileDeleteStrategy;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cc.mallet.classify.tui.Text2Vectors;
import cc.mallet.topics.tui.Vectors2Topics;
import edu.albany.cs.pojo.Topic;
import edu.albany.cs.solr.SolrHandler;
import edu.albany.cs.util.ConfigManager;
import edu.albany.cs.util.FileGeneratorFromMysql;
import edu.albany.cs.util.FileManager;
import edu.albany.cs.util.OutputFormatter;
import edu.albany.cs.util.TFIDF;
import edu.albany.cs.util.TopicFileParser;

public class FullCycler {

	public static HashMap<String,Double> currWordFreq;
	public static List<List<Topic>> topicListOfList = new ArrayList<List<Topic>>();

	
	private static void cleanTmp(boolean isAll) {

		String dataPath = ConfigManager.getInstance().getDataDirectory();
		File fin = new File(dataPath);
		String[] finlist = fin.list();
		for (int n = 0; n < finlist.length; n++) {
			try {
				if(!finlist[n].contains("txt") || isAll)
				{
					String deletePath = dataPath + "/"+finlist[n];
					FileDeleteStrategy.FORCE.delete(new File(deletePath));
					System.out.println("deleted: "+deletePath);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("All cleaned!");
	}

	private static void generateTopics(String hostname, String schema,
			String username, String password, String viewId, String noteIds,
			String numberOfTopics,String outputSuffix) throws IOException {

		cleanTmp(false);
		HashMap<String,String> corpus;
		corpus = FileGeneratorFromMysql.writeInFolders(hostname, schema, username,
				password, viewId, noteIds);

		File directory = new File("/tmp/");
		File tempFile = File.createTempFile("topics_", "", directory);

		System.setErr(new PrintStream(new FileOutputStream(tempFile)));

		String[] importArgumentsWithSP = new String[] { "--input",
				"/tmp/raw_content_sp/", "--output",
				"/tmp/topics-input-everything-sp", "--keep-sequence",
				"--remove-stopwords" };

		String[] topicModellingArgumentSP = new String[] { "--input",
				"/tmp/topics-input-everything-sp/", "--num-topics",
				numberOfTopics,
				"--output-state",
				"/tmp/topics_results_everything_spellchecked.gz",
				"--output-doc-topics",
				"/tmp/output_doc_topics_spelchecked_"+outputSuffix+".txt",
				"--output-topic-keys",
				"/tmp/output_topic_keys_spellchecked_"+outputSuffix+".txt",
				// "--optimize-interval","10",
				// "--optimize-burn-in","200",
				"--num-iterations", ConfigManager.getInstance().getNumberOfIterations(), 
				"--xml-topic-report",
				"/tmp/output_topic_report_"+outputSuffix+".xml" };

		// FileGeneratorFromMysql.main(new String[0]);
		System.out
				.println("Raw document to Mallet matrix conversion started...");
		// Text2Vectors.main(importArguments);
		Text2Vectors.main(importArgumentsWithSP);
		// Text2Vectors.main(importArgumentsWithNGram);

		System.out.println("Mallet input format created!");

		System.out.println("Topic Modelling Started...");
		// Vectors2Topics.main(topicModellingArgument);
		Vectors2Topics.main(topicModellingArgumentSP);
		// Vectors2Topics.main(topicModellingArgumentNgram);

		System.out.println("Topic Modelling Finallized...");
		System.out.println("All Process completed!");
		
		System.out.print("---- printing term vectors for Generated topics----");
		
		getTermWeights(corpus);
		OutputFormatter.generateOutputDataSingle("output_topic_keys_spellchecked_"+outputSuffix+".txt");
		System.out.println("");
	}
	
	public static void getTermWeights(HashMap<String,String> corpus){
		
		// generate TF iDF
		TFIDF tf = new TFIDF(corpus);
		tf.calcTFIDF();
		tf.calcGlobalDocOccFreq();
		tf.calcDocVectors();

		// tf.pprintTermFreq();
		// tf.pprintTermOcc();
		tf.pprintDocVectors(80);
		currWordFreq = tf.getTermFreq();
	}

	public static void twoRoundTopicGeneration() throws IOException, SolrServerException {
		
		cleanTmp(true);
		
		generateTopics(
				ConfigManager.getInstance().getMysqlHostname(), 
				ConfigManager.getInstance().getSchema(), 
				ConfigManager.getInstance().getUsername(), 
				ConfigManager.getInstance().getPassword(), 
				ConfigManager.getInstance().getViewId(), 
				"",
				ConfigManager.getInstance().getNumberOfTopics(),
				ConfigManager.getInstance().getViewId());

		Set <String> topicWordList =  TopicFileParser
				.getTopics(ConfigManager.getInstance().getDataDirectory()
						+"/"+ConfigManager.getInstance().getTopicFilePrefix()
						+"_"+ConfigManager.getInstance().getViewId()+".txt").get(0).getWordList();

		
		for (String word : topicWordList) {
			//Send Query to SOLR
			SolrDocumentList documentList = SolrHandler.sendQuery(
					ConfigManager.getInstance().getSolrHostname(),
					ConfigManager.getInstance().getSolrPort(),
					ConfigManager.getInstance().getSolrColl(),
					ConfigManager.getInstance().getViewId(),
					word,
					ConfigManager.getInstance().getSolrVersion());

			// Generate noteId
			String noteIds="";
			for (SolrDocument solrDocument : documentList) {
				noteIds = noteIds + solrDocument.getFieldValue("noteid") + ",";
			}

			if (!noteIds.equals("")) {
				noteIds = noteIds.substring(0, noteIds.length() - 1);
				System.out.println(noteIds);
			}

			System.out.println(word);
			System.out.println(noteIds);

			generateTopics(
					ConfigManager.getInstance().getMysqlHostname(), 
					ConfigManager.getInstance().getSchema(), 
					ConfigManager.getInstance().getUsername(), 
					ConfigManager.getInstance().getPassword(), 
					ConfigManager.getInstance().getViewId(), 
					noteIds,
					ConfigManager.getInstance().getNumberOfTopics(),
					ConfigManager.getInstance().getViewId()+"_"+word);

			noteIds = "";				
		}
		

		
	}
	
	public static void main(String[] args) throws IOException, SolrServerException {
		
		twoRoundTopicGeneration();
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String jsonData = gson.toJson(topicListOfList);
		
		FileManager.generateFile(ConfigManager.getInstance().getDataDirectory()+"/"+ConfigManager.getInstance().getViewId()+".txt", jsonData);
	}

}
