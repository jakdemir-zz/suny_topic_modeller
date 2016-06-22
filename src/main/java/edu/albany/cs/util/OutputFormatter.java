package edu.albany.cs.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import edu.albany.cs.mallet.FullCycler;
import edu.albany.cs.pojo.Topic;

public class OutputFormatter {

	public static String generateOutputData() {
		String dataDir = ConfigManager.getInstance().getDataDirectory();
		String prefix = ConfigManager.getInstance().getTopicFilePrefix();

		File fin = new File(dataDir);
		String[] finlist = fin.list();

		List<List<Topic>> topicListOfList = new ArrayList<List<Topic>>();

		
		for (int i = 0; i < finlist.length; i++) {
			try {
				if (finlist[i].contains(prefix)) {
					String fileName = finlist[i];
					System.out.println("File : " + fileName);
					List<Topic> topicList = TopicFileParser.getTopics(dataDir + "/" + fileName);
					topicListOfList.add(topicList);
				}
			} catch (Exception e) {
				System.out.println("ERROR : " + e);
			}
		}

		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String topicListJson = gson.toJson(topicListOfList);

		System.out.println(topicListJson);

		// List<Topic> topicList2 = gson.fromJson(json, List.class);

		return topicListJson;
	}
	
	public static void generateOutputDataSingle(String fileName) {
		String dataDir = ConfigManager.getInstance().getDataDirectory();

		
			try {
					System.out.println("Filename : "+fileName);
					List<Topic> topicList = TopicFileParser.getTopics(dataDir + "/" + fileName);
					FullCycler.topicListOfList.add(topicList);
				}
			catch (Exception e) {
				System.out.println("ERROR : " + e);
		}

		//Gson gson = new GsonBuilder().setPrettyPrinting().create();
		//String topicListJson = gson.toJson(topicListOfList);

		//System.out.println(topicListJson);

		// List<Topic> topicList2 = gson.fromJson(json, List.class);

		//return topicListJson;
	}
	
	
	

	public static void main(String[] args) throws IOException {
		String jsonData = generateOutputData();
		FileManager.generateFile(ConfigManager.getInstance().getDataDirectory()+"/"+ConfigManager.getInstance().getViewId()+".txt", jsonData);
	}
}