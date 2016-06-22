package edu.albany.cs.util;
import java.util.ArrayList;
import java.util.HashMap;

import cc.mallet.types.FeatureConjunction.List;


public class TFIDF {

	//Term doc index
	private HashMap<String, ArrayList<String> > WordIdMap = new HashMap<String, ArrayList<String> >();
		
	
	//Term occurence  count global
	private HashMap<String, Double> WordFreq = new HashMap<String, Double>();

	//Term occurence  count global
	private HashMap<String,  HashMap<String, Integer>> WordFreqIndex = new HashMap<String,  HashMap<String, Integer>>();
	
	
	//Corpus Word Count per document
	private HashMap<String, Double> termIDF = new HashMap<String, Double>();
		
	//Inverse Document global
	private HashMap<String, Integer> TermOcc = new HashMap<String, Integer>();
	
	//Corpus Word Count per document
	private HashMap<String, Double> docVectors = new HashMap<String, Double>();
	
	//Corpus
	private HashMap<String, String> corpus = new HashMap<String, String>(); 

	public TFIDF(HashMap<String,String> corpus)
	{
		this.corpus = corpus;
	}
	
	public HashMap<String, Double> getTermFreq()
	{
		return WordFreq;
	}
	
	public HashMap<String, Integer > getTermOcc()
	{
		return TermOcc;
	}
	
	public HashMap<String, Double > getDocVectors()
	{
		return docVectors;
	}
	
	public void pprintTermOcc(){
		System.out.println("--------------Term Occurence in Corpus -------------");
		for(String key: TermOcc.keySet())
		{
			System.out.println(""+key+" : "+TermOcc.get(key));
		}
		System.out.println("-------------- DONE Term Occurence in Corpus -------------");
	}
	
	public void pprintTermFreq(){
		System.out.println("--------------Term Vector in Corpus -------------");
		for(String key: WordFreq.keySet())
		{
			System.out.println(""+key+" : "+WordFreq.get(key));
		}
		System.out.println("--------------Done Term Vector Frequency in Corpus -------------");
	}
	
	public void pprintDocVectors(double threshold){
		System.out.println("--------------Term Frequency in Corpus -------------");
		for(String key: docVectors.keySet())
		{
			if (docVectors.get(key) > threshold)
			{
				System.out.println(""+key+" : "+docVectors.get(key));
			}
		}
		System.out.println("--------------Done Term Frequency in Corpus -------------");
	}
	
	//TODO now that we have per document term freq and global IDF values divide	multiply TF * IDF to get TFXIDF 
	//Look for ways of incorporating global TF IDF , also keep in mind that TF per document isn't normalized on doucment length
	//Keep in mind we need to calculate this per note
	public void calcDocVectors()
	{
		for (String key : WordFreq.keySet() )
		{
			double vector =  WordFreq.get(key) * termIDF.get(key);
			docVectors.put(key, vector);
		}
		
	}
	
	//Calculate TermFreq
	//Also create term doc index.
	public void calcTFIDF()
	{
		//tokenize the set first
		HashMap<String, String[]> tokenizedCorpus = tokenizeCorpus();
	
		//iterate over documents
		for (String Key : tokenizedCorpus.keySet())
		{
			WordFreqIndex.put(Key, new HashMap<String, Integer>());
			
			for(String word : tokenizedCorpus.get(Key))
			{
			
				//have we seen this word before
				if(WordFreq.containsKey(word))
				{
					//increment count
					double temp = WordFreq.get(word);
					
					temp = temp + 1.0; //+ Math.log(temp);
					//temp++;
					WordFreq.put(word, temp);
				}
				else{
					//initialize the value in the hashmap
					WordFreq.put(word, 1.0);
				}
				//calculate per document term frequency
				if(WordFreqIndex.get(Key).containsKey(word))
				{
					//increment count
					int count = WordFreqIndex.get(Key).get(word);
					count++;
					WordFreqIndex.get(Key).put(word, count);
				}
				else{
					//initialize the value in the hashmap
					WordFreqIndex.get(Key).put(word, 1);
				}
				
				//Let's add word to our doc index
				if (WordIdMap.containsKey(word))
				{
					WordIdMap.get(word).add(Key);
				}
				else
				{
					//add a new list to store index
					ArrayList<String> init = new ArrayList<String>();
					init.add(Key);
					WordIdMap.put(word, init);
				}
				
			}
		}	
	}
	
	
	public void calcGlobalDocOccFreq()
	{
		//iterate over index
		for (String word :  WordIdMap.keySet())
		{
			//get number of times term is occurings
			TermOcc.put(word,  WordIdMap.get(word).size() );
			termIDF.put(word, Math.log(WordIdMap.size()/ WordIdMap.get(word).size() ));
		}
	}
	
	private HashMap<String, String[]> tokenizeCorpus()
	{
		HashMap<String, String[]> tokenizedCorpus = new HashMap<String, String[]>();
		
		for (String Key: this.corpus.keySet() )
		{
			String tempString = this.corpus.get(Key);
			tempString = cleanString(tempString);
			
			String[] tokenized = tempString.split(" ");
			tokenized = removeStopwordsEN(tokenized);
			tokenizedCorpus.put(Key, tokenized);
		}
		return tokenizedCorpus;
	}
	
	private String[] removeStopwordsEN(String[] toRemove)
	{
		String[] stopwords = {"a", "about", "above", "above", "across", "after", "afterwards", "again", "against", "all", "almost", "alone", "along", "already", "also","although","always","am","among", "amongst", "amoungst", "amount",  "an", "and", "another", "any","anyhow","anyone","anything","anyway", "anywhere", "are", "around", "as",  "at", "back","be","became", "because","become","becomes", "becoming", "been", "before", "beforehand", "behind", "being", "below", "beside", "besides", "between", "beyond", "bill", "both", "bottom","but", "by", "call", "can", "cannot", "cant", "co", "con", "could", "couldnt", "cry", "de", "describe", "detail", "do", "done", "down", "due", "during", "each", "eg", "eight", "either", "eleven","else", "elsewhere", "empty", "enough", "etc", "even", "ever", "every", "everyone", "everything", "everywhere", "except", "few", "fifteen", "fify", "fill", "find", "fire", "first", "five", "for", "former", "formerly", "forty", "found", "four", "from", "front", "full", "further", "get", "give", "go", "had", "has", "hasnt", "have", "he", "hence", "her", "here", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "him", "himself", "his", "how", "however","i", "hundred", "ie", "if", "in", "inc", "indeed", "interest", "into", "is", "it", "its", "itself", "keep", "last", "latter", "latterly", "least", "less", "ltd", "made", "many", "may", "me", "meanwhile", "might", "mill", "mine", "more", "moreover", "most", "mostly", "move", "much", "must", "my", "myself", "name", "namely", "neither", "never", "nevertheless", "next", "nine", "no", "nobody", "none", "noone", "nor", "not", "nothing", "now", "nowhere", "of", "off", "often", "on", "once", "one", "only", "onto", "or", "other", "others", "otherwise", "our", "ours", "ourselves", "out", "over", "own","part", "per", "perhaps", "please", "put", "rather", "re", "same", "see", "seem", "seemed", "seeming", "seems", "serious", "several", "she", "should", "show", "side", "since", "sincere", "six", "sixty", "so", "some", "somehow", "someone", "something", "sometime", "sometimes", "somewhere", "still", "such", "system", "take", "ten", "than", "that", "the", "their", "them", "themselves", "then", "thence", "there", "thereafter", "thereby", "therefore", "therein", "thereupon", "these", "they", "thickv", "thin", "third", "this", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "top", "toward", "towards", "twelve", "twenty", "two", "un", "under", "until", "up", "upon", "us", "very", "via", "was", "we", "well", "were", "what", "whatever", "when", "whence", "whenever", "where", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whoever", "whole", "whom", "whose", "why", "will", "with", "within", "without", "would", "yet", "you", "your", "yours", "yourself", "yourselves", "the"};
		ArrayList<String> result = new ArrayList<String>();
		
		for (String token : toRemove)
		{
			boolean isStopWord = false;
			
			for (String word : stopwords)
			{	
				
				if (token.equals(" ") || token.equals(word))
				{
					isStopWord = true;
				}
			}
			if(!isStopWord)
			{
				result.add(token);	
			}
			
		}
		String[] resultArr =  new String[result.size()];
		resultArr = result.toArray(resultArr);
		return resultArr;
	}
	
	private String cleanString(String toClean)
	{
		char[] badChars = {'.',',','\\','_','/','-','?','!','&','%',':',')','(','+','=','0','1','2','3','4','5','6','7','8','9'};
	
		
		for(int i = 0; i< badChars.length ; ++i)
		{
			//simple house keeping
			toClean = toClean.replace(badChars[i], ' ');
			toClean = toClean.toLowerCase();
			toClean = toClean.trim();
			
		}

		return toClean;
		
	}
	

	
}
