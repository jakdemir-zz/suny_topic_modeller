package edu.albany.cs.pojo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import edu.albany.cs.mallet.FullCycler;

public class Topic {
	private String name;
	private HashMap<String,Double> wordList = new HashMap<String, Double>();;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/*
	public HashMap<String,Double> getWordList() {
		return wordList;
	}
	*/

	public Set<String> getWordList() {
		return wordList.keySet();
	}
	
	public void setWordList(HashMap<String,Double>wordList) {
		this.wordList = wordList;
	}
	
	
	public Topic(String name, String line){
		this.name = name;
		List<String> tempWordsArr = Arrays.asList(line.split(" "));
		
		for (String word: tempWordsArr) {
			try{
				this.wordList.put(word, FullCycler.currWordFreq.get(word));	
			}
			catch(Exception e)
			{
				this.wordList.put(word,0.0);
			}
			
		}
		
	}

}
