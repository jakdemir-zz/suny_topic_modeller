package edu.albany.cs.stemmer;

import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.util.Version;

public class KStemmer {
	private static String stemWords(String text, String language) {
		StringBuffer result = new StringBuffer();
		if (text != null && text.trim().length() > 0) {
			StringReader tReader = new StringReader(text);
			Analyzer analyzer = new KStemAnalyzer();
			TokenStream tStream = analyzer.tokenStream("contents", tReader);
			TermAttribute term = tStream.addAttribute(TermAttribute.class);

			try {
				while (tStream.incrementToken()) {
					result.append(term.term());
					result.append(" ");
				}
			} catch (IOException ioe) {
				System.out.println("Error: " + ioe.getMessage());
			}
		}

		if (result.length() == 0)
			result.append(text);
		return result.toString().trim();
	}

	public static String stemWords(String text) {

		return stemWords(text, "English");
	}

	public static void main(String[] args) {
		System.out
				.println(KStemmer.stemWords("bunu yazan tosun okuyana kosun hello kitty apples"));
	}
}