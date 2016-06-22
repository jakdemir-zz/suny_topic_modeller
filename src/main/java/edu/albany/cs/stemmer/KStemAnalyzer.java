package edu.albany.cs.stemmer;

import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseTokenizer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.KStemFilter;

public class KStemAnalyzer extends Analyzer {
	public final TokenStream tokenStream(String fieldName, Reader reader) {
		return new KStemFilter(new LowerCaseTokenizer(reader));
	}

}
