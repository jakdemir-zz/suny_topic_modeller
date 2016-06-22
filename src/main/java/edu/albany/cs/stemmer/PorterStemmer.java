package edu.albany.cs.stemmer;

import org.tartarus.snowball.SnowballProgram;
import org.tartarus.snowball.ext.englishStemmer;
import org.tartarus.snowball.ext.porterStemmer;

public class PorterStemmer {
	
	public static String stemWord(String word) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		//englishStemmer
		Class stemClass = Class.forName("org.tartarus.snowball.ext.porterStemmer");
		porterStemmer stemmer = (porterStemmer) stemClass.newInstance();
		stemmer.setCurrent(word);
		stemmer.stem();
		String stemmedWord = stemmer.getCurrent();
		System.out.println(stemmedWord);
		return stemmedWord;
	}
	
	public static String stemWords(String words) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		String[] wordList = words.split(" ");
		String result  ="";
		for (int i = 0; i < wordList.length; i++) {
			result = result + " " + stemWord(wordList[i]);
		}
		return result;
	}
	
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		stemWord("a");
	}
}
