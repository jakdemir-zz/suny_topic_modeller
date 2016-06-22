package edu.albany.cs.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.albany.cs.pojo.Topic;

public class TopicFileParser {
	public static void main(String[] args) throws IOException {
		List<Topic> topicList = getTopics("/tmp/output_topic_keys_spellchecked.txt");
		
	}

	public static List<Topic> getTopics(String fileName) throws IOException {
		File file = new File(fileName);

		BufferedReader br = new BufferedReader(new FileReader(file));
		String line;
		
		List<Topic> topicList = new ArrayList<Topic>();
		
		int i = 0;
		while ((line = br.readLine()) != null) {
			String [] fileArr = fileName.split("_");
			String name = fileArr[fileArr.length-1].replace(".txt", "")+"_"+i;
			Topic topic = new Topic(name , line.split("\t")[2]);
			topicList.add(topic);
			i++;
		}
		br.close();
	
		return topicList;
	}
	
	
}
