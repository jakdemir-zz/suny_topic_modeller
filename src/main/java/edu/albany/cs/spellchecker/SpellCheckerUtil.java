package edu.albany.cs.spellchecker;
import org.gauner.jSpellCorrect.spi.ToySpellingCorrector;
import org.xeustechnologies.googleapi.spelling.SpellChecker;
import org.xeustechnologies.googleapi.spelling.SpellCorrection;
import org.xeustechnologies.googleapi.spelling.SpellRequest;
import org.xeustechnologies.googleapi.spelling.SpellResponse;


public class SpellCheckerUtil {

	private String _dictionary = "";
	private ToySpellingCorrector _corrector;
	private SpellChecker _correctorGoog;
	private SpellRequest _spellGoogRequest;

	private String _type = "";
	
	/*
	* Spellchecker constructer
	* 
	*@param dictionary - for now simply use enlgish dictionary provided
	*/
	public SpellCheckerUtil(String dictionary){
		this._dictionary = dictionary;
		this._corrector = new ToySpellingCorrector();
		this._corrector.trainFile("./resources/"+dictionary+".0");
		this._type = "local";
	}

	/*
	 * Secondary Google constructor
	 */
	public SpellCheckerUtil(){
		this._correctorGoog = new SpellChecker();
		this._spellGoogRequest =  new SpellRequest();
		this._spellGoogRequest.setIgnoreDuplicates(false);
		this._type = "remote";
		
	}
	
	/*
	 * Util function to correct words
	 * 
	 * @param String toCorrect - correct this word
	 */
	public String correct(String toCorrect)
	{
		return this._corrector.correct(toCorrect);
	}
	
	/*
	 * Util function to correct words full sentences
	 * 
	 * *param String toCorrect - correct this word
	 */
	public String correctFull(String toCorrect)
	{
		
		String[] toCorrectSplit = toCorrect.split(" ");
		System.out.println(toCorrectSplit.length);
		String[] corrected = new String[toCorrectSplit.length];
		String temp = "";
		int counter = 0;

		if (this._type.equals("local"))
		{
			for (int i = 0 ; i < corrected.length; ++i){
				temp = toCorrectSplit[i];
				corrected[i] =  correct(temp);
				//if not the same
				if (!corrected.equals(temp)){
					counter++;
				}
			}
		}
		//using google
		else{
			
			this._spellGoogRequest.setText(toCorrect);
			SpellResponse spellResponse= this._correctorGoog.check(this._spellGoogRequest);
			SpellCorrection[] sc = spellResponse.getCorrections();
	
			int limit = toCorrect.length();
			StringBuilder correctedSB = new StringBuilder();
			
			if(sc != null){
				int index = 0;
				for(int i = 0; i < sc.length ; ++i){
				
					String sbstrng = toCorrect.substring(index, sc[i].getOffset());
					correctedSB.append(sbstrng);
					correctedSB.append(sc[i].getWords()[0]);
					index = sc[i].getOffset() +  sc[i].getLength();
				}
				correctedSB.append(toCorrect.substring(index, limit));
				
				return correctedSB.toString();
			}
			else
			{
				return toCorrect;
			}
			
		}

			
		//debug
		System.out.println("Terms Corrrected "+counter);
		return joinString(corrected," ");
	}
	
	/*
	 * Util function to join our String array's into single Single
	 * 
	 * @param String[] toJoin -  string array we wish to join
	 * @param String joinWith -  The character we wish to use to join the array ( use space )
	 */
	public String joinString(String[] toJoin,String joinWith){
		
		StringBuilder sb = new StringBuilder();		
		
		for (int i = 0; i < toJoin.length ; i++)
		{
			sb.append(toJoin[i]);
			sb.append(joinWith);
		}
		return sb.toString();
		
	}
	
		
}
