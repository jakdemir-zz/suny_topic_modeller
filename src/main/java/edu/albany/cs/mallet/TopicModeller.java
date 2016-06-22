package edu.albany.cs.mallet;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileDeleteStrategy;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import cc.mallet.classify.tui.Text2Vectors;
import cc.mallet.topics.tui.Vectors2Topics;
import edu.albany.cs.pojo.Topic;
import edu.albany.cs.solr.SolrHandler;
import edu.albany.cs.util.FileGeneratorFromMysql;
import edu.albany.cs.util.TFIDF;
import edu.albany.cs.util.TopicFileParser;

public class TopicModeller {

	/*
	 * 
	 * import-dir) CLASS=cc.mallet.classify.tui.Text2Vectors;; import-file)
	 * CLASS=cc.mallet.classify.tui.Csv2Vectors;; import-svmlight)
	 * CLASS=cc.mallet.classify.tui.SvmLight2Vectors;; train-classifier)
	 * CLASS=cc.mallet.classify.tui.Vectors2Classify;; classify-dir)
	 * CLASS=cc.mallet.classify.tui.Text2Classify;; classify-file)
	 * CLASS=cc.mallet.classify.tui.Csv2Classify;; classify-svmlight)
	 * CLASS=cc.mallet.classify.tui.SvmLight2Classify;; train-topics)
	 * CLASS=cc.mallet.topics.tui.Vectors2Topics;; infer-topics)
	 * CLASS=cc.mallet.topics.tui.InferTopics;; evaluate-topics)
	 * CLASS=cc.mallet.topics.tui.EvaluateTopics;; hlda)
	 * CLASS=cc.mallet.topics.tui.HierarchicalLDATUI;; prune)
	 * CLASS=cc.mallet.classify.tui.Vectors2Vectors;; split)
	 * CLASS=cc.mallet.classify.tui.Vectors2Vectors;; bulk-load)
	 * CLASS=cc.mallet.util.BulkLoader;; run) CLASS=$1; shift;;
	 */

	public static void main2(String[] args) throws IOException {

		// *mallet import-dir --input raw_content/* --output
		// topic-input-everything --keep-sequence --remove-stopwords
		// *mallet train-topics --input topic-input-everything --num-topics 100
		// --output-state topic_results_everything.gzxu
		// 7 localhost builder_ikit_org_J_ICS_2012_2013_dev root
		String numberOfTopics = "1";
		String hostname = "localhost";
		String schema = "builder_ikit_org_J_ICS_2012_2013_dev";
		String username = "root";
		String password = "";
		String viewId = "";

		/*
		 * try { numberOfTopics = args[0]; hostname = args[1]; schema = args[2];
		 * username = args[3];
		 * 
		 * try { password = args[4];
		 * 
		 * } catch (Exception e) { password = ""; } System.out
		 * .println("Starting to Read Documents and Create Raw Documents...");
		 * FileGeneratorFromMysql.writeInFolders(hostname, schema, username,
		 * password, "");
		 * System.out.println("MySQL consumed and documents are created!"); }
		 * catch (Exception e) { System.out .println(
		 * "Please check usage \"java TopicModeller.jar 5 localhost builder_ikit_org_J_ICS_2012_2013 root  "
		 * ); e.printStackTrace(); }
		 */
		
		File dumpFile= new File("/tmp/output_doc_topics_spelchecked.txt");
		//Hazir miyiz epic hack geliyor
		if(!dumpFile.exists()){
			FileGeneratorFromMysql.writeInFolders(hostname, schema,
				username, password, viewId, "");
		}
		else{
			System.out.println("Warning Not doing new dump using existing dump. Delete output_doc_topics_spelchecked.txt to clean dump!");
		}
		
		//FileGeneratorFromMysql.writeInFolders(hostname, schema, username,
		//		password, viewId, "");

		File directory = new File("/tmp/");
		File tempFile = File.createTempFile("topics_", "", directory);

		System.setErr(new PrintStream(new FileOutputStream(tempFile)));

		// tempFile = File.createTempFile("stdout", "", directory);
		// System.setOut(new PrintStream(new FileOutputStream(tempFile)));

		String[] importArguments = new String[] { "--input",
				"/tmp/raw_content/", "--output",
				"/tmp/topics-input-everything", "--keep-sequence",
				"--remove-stopwords" };

		String[] importArgumentsWithSP = new String[] { "--input",
				"/tmp/raw_content_sp/", "--output",
				"/tmp/topics-input-everything-sp", "--keep-sequence",
				"--remove-stopwords" };

		String[] importArgumentsWithNGram = new String[] { "--input",
				"/tmp/raw_content_sp/", "--output",
				"/tmp/topics-input-everything-sp", "--keep-sequence",
				"--remove-stopwords", "--gram-sizes", "2" };

		String[] importArgumentsWithBigram = new String[] { "--input",
				"/tmp/raw_content_sp/", "--output",
				"/tmp/topics-input-everything-sp", "--remove-stopwords",
				"--keep-sequence-bigrams", "true" };

		//
		String[] topicModellingArgumentSP = new String[] { "--input",
				"/tmp/topics-input-everything-sp/", "--num-topics",
				numberOfTopics,
				"--output-state",
				"/tmp/topics_results_everything_spellchecked.gz",
				"--output-doc-topics",
				"/tmp/output_doc_topics_spelchecked.txt",
				"--output-topic-keys",
				"/tmp/output_topic_keys_spellchecked.txt",
				// "--optimize-interval","10",
				// "--optimize-burn-in","200",
				"--num-iterations", "2000", "--xml-topic-report",
				"/tmp/output_topic_report.xml" };
		String[] topicModellingArgumentNgram = new String[] { "--input",
				"/tmp/topics-input-everything-sp", "--num-topics",
				numberOfTopics,
				"--output-state",
				"/tmp/topics_results_everything_spellchecked.gz",
				"--output-doc-topics",
				"/tmp/output_doc_topics_spelchecked.txt",
				"--output-topic-keys",
				"/tmp/output_topic_keys_spellchecked.txt",
				// "--optimize-interval","10",
				// "--optimize-burn-in","200",
				"--num-iterations", "2000", "--use-ngrams", "true",
				"--xml-topic-report", "/tmp/output_topic_report.xml" };

		String[] topicModellingArgument = new String[] { "--input",
				"/tmp/topics-input-everything", "--num-topics", numberOfTopics,
				"--output-state", "/tmp/topics_results_everything.gz",
				"--output-doc-topics", "/tmp/output_doc_topics.txt",
				"--output-topic-keys", "/tmp/output_topic_keys.txt",
				"--num-iterations", "2000" };

		String[] topicModellingArgumentPAM = new String[] { "--input",
				"/tmp/topics-input-everything-sp", "--num-topics",
				numberOfTopics, "--output-state",
				"/tmp/topics_results_everything_spellchecked.gz",
				"--output-doc-topics",
				"/tmp/output_doc_topics_spelchecked.txt",
				"--output-topic-keys",
				"/tmp/output_topic_keys_spellchecked.txt",
				// "--optimize-interval","10",
				// "--optimize-burn-in","200",
				"--num-iterations", "2000", "--use-pam", "true" };

		try {
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

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main1(String[] args) throws FileNotFoundException,
			IOException {
		// runBigramOnDataImport("", "5");
		runUnigramTopicInferenceOnDataImport();
	}

	private static void runBigramOnDataImport(String view, String topic)
			throws FileNotFoundException, IOException {

		String numberOfTopics = topic;
		String hostname = "localhost";
		String schema = "test";
		String username = "root";
        //fakes
		String password = "root";
		// Empty for all,
		// String views="";
		String views = view;
		HashMap<String, String> corpus;

		System.out
				.println("runBigramOnDataImport: Raw document to Mallet matrix conversion started...");

		// hold the views in memory
		File dumpFile= new File("/tmp/output_doc_topics_spelchecked.txt");
		//Hazir miyiz epic hack geliyor
		if(!dumpFile.exists()){
			corpus = FileGeneratorFromMysql.writeInFolders(hostname, schema,
				username, password, views, "");
		}
		else{
			System.out.println("Warning Not doing new dump using existing dump. Delete output_doc_topics_spelchecked.txt to clean dump!");
		}
		
		//puh bana
		
		// generate TF iDF
		//TFIDF tf = new TFIDF(corpus);
		// tf.calcGlobalTermFreq();
		//tf.calcGlobalDocOccFreq();

		//tf.pprintTermFreq();
		//tf.pprintTermOcc();

		System.out
				.println("runBigramOnDataImport: Raw document to Mallet matrix conversion started...");
		String[] importArgumentsWithNGram = new String[] { "--input",
				"/tmp/raw_content_sp/", "--output",
				"/tmp/topics-input-everything-sp", "--keep-sequence",
				"--remove-stopwords", "--gram-sizes", "2" };

		Text2Vectors.main(importArgumentsWithNGram);

		System.out
				.println("runBigramOnDataImport: Mallet input format created!");

		System.out.println("runBigramOnDataImport: Topic Modelling Started...");
		String[] topicModellingArgumentNgram = new String[] { "--input",
				"/tmp/topics-input-everything-sp", "--num-topics",
				numberOfTopics, "--output-state",
				"/tmp/topics_results_everything_spellchecked.gz",
				"--output-doc-topics",
				"/tmp/output_doc_topics_spelchecked.txt",
				"--output-topic-keys",
				"/tmp/output_topic_keys_spellchecked.txt", "--num-iterations",
				"2000", "--xml-topic-report", "/tmp/output_topic_report.xml" };

		Vectors2Topics.main(topicModellingArgumentNgram);

		System.out
				.println("runBigramOnDataImport: Topic Modelling Finallized...");

		System.out.println("runBigramOnDataImport: All Process completed!");

	}

	private static void runUnigramTopicInferenceOnDataImport()
			throws FileNotFoundException, IOException {

		String numberOfTopics = "5";
		String hostname = "localhost";
		String schema = "builder_ikit_org_J_ICS_2012_2013_dev";
		// String schema = "dev";
		String username = "root";
		String password = "root";
		// String password = "Changeme321";
		String views = "170";

		HashMap<String, String> corpus;
		corpus = FileGeneratorFromMysql.writeInFolders(hostname, schema,
				username, password, views, "");

		// generate TF iDF
		TFIDF tf = new TFIDF(corpus);
		tf.calcTFIDF();
		tf.calcGlobalDocOccFreq();
		tf.calcDocVectors();

		// tf.pprintTermFreq();
		// tf.pprintTermOcc();
		tf.pprintDocVectors(80);

		String[] importArgumentsWithNGram = new String[] { "--input",
				"/tmp/raw_content_sp/", "--output",
				"/tmp/topics-input-everything-sp", "--keep-sequence",
				"--remove-stopwords" };
		Text2Vectors.main(importArgumentsWithNGram);
		String[] topicModellingArgumentNgram = new String[] { "--input",
				"/tmp/topics-input-everything-sp", "--num-topics",
				numberOfTopics, "--output-state",
				"/tmp/topics_results_everything_spellchecked.gz",
				"--output-doc-topics",
				"/tmp/output_doc_topics_spelchecked.txt",
				"--output-topic-keys",
				"/tmp/output_topic_keys_spellchecked.txt", "--num-iterations",
				"2000", "--output-model", "/tmp/output_model.mdl" };
		Vectors2Topics.main(topicModellingArgumentNgram);

	}

	private static void cleanTmp() {
		//Jak bu baya kiracak su pathleri biraz adam ediom
		File fin = new File("/tmp/");
		String[] finlist = fin.list();
		for (int n = 0; n < finlist.length; n++) {
			System.out.println(finlist[n]);
			try {
				if(!finlist[n].contains("txt"))
				{
				FileDeleteStrategy.FORCE.delete(new File("/tmp/"
						+ finlist[n]));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws IOException,
			SolrServerException {
		String numberOfTopics = "1";
		String hostname = "localhost";
		String solrHostname = "topicmodel.cloudapp.net";
		String solrPort = "8090";
		String solrColl = "collection1";
		String solrVersion = "4";
		String schema = "builder_ikit_org_J_ICS_2012_2013_dev";
		String username = "root";
		String password = "root";
		String viewId = "170";

		generateTopics(hostname, schema, username, password, viewId, "",
				numberOfTopics);

		List<Topic> topicList = TopicFileParser
				.getTopics("/tmp/output_topic_keys_spellchecked.txt");

		
		SolrDocumentList documentList = SolrHandler.sendQuery(solrHostname,solrPort,solrColl, viewId,
				topicList.get(0).getWordList().iterator().next(),solrVersion);

		// Generate noteId
		String noteList = "";

		for (SolrDocument solrDocument : documentList) {
			noteList = noteList + solrDocument.getFieldValue("noteid") + ",";
		}
		System.out.println(topicList.get(0).getWordList().iterator().next());
		System.out.println(noteList);

	}

	private static void generateTopics(String hostname, String schema,
			String username, String password, String viewId, String noteIds,
			String numberOfTopics) throws IOException {

		cleanTmp();

		File dumpFile= new File("/tmp/output_doc_topics_spelchecked.txt");
		//Hazir miyiz epic hack geliyor
		if(!dumpFile.exists()){
			 FileGeneratorFromMysql.writeInFolders(hostname, schema,
				username, password, "", noteIds);
		}
		else{
			System.out.println("WARNING: Not doing new dump using existing dump. Delete output_doc_topics_spelchecked.txt to clean dump!");
		}
		//FileGeneratorFromMysql.writeInFolders(hostname, schema, username,
		//		password, "", noteIds);

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
				"/tmp/output_doc_topics_spelchecked.txt",
				"--output-topic-keys",
				"/tmp/output_topic_keys_spellchecked.txt",
				// "--optimize-interval","10",
				// "--optimize-burn-in","200",
				"--num-iterations", "2000", "--xml-topic-report",
				"/tmp/output_topic_report.xml" };

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

	}

}
