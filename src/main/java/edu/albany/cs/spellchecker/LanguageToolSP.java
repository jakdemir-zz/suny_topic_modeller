package edu.albany.cs.spellchecker;

import java.io.IOException;
import java.util.List;

import org.languagetool.JLanguageTool;
import org.languagetool.language.AmericanEnglish;
import org.languagetool.rules.RuleMatch;

import cc.mallet.grmm.types.CPT;

public class LanguageToolSP {

	
	public static String spellCheck(String words) throws Exception {
		System.out.println("#########");
		System.out.println("DEFAULT : "+words);
		JLanguageTool langTool = new JLanguageTool(new AmericanEnglish());
		langTool.activateDefaultPatternRules();
		List<RuleMatch> matches = langTool.check(words);
		StringBuffer correctedWords = new StringBuffer(words);
		int indexDiff = 0;
		for (RuleMatch match : matches) {
			//System.out.println("Word : "+ words.substring(match.getColumn() - 1, match.getEndColumn() - 1) + ": " + match.getMessage());
			System.out.println("Suggested correction: " + match.getSuggestedReplacements());
			System.out.println("FromPos: "+match.getFromPos()+". ToPos: "+match.getToPos());

			if (match.getSuggestedReplacements().size()>0) {
				String suggest = match.getSuggestedReplacements().get(0) ;
				System.out.println("IndexDiff : "+indexDiff+", Suggested length :"+suggest.length());
				
				correctedWords.replace(indexDiff+match.getFromPos() , indexDiff+match.getToPos(), suggest);
				indexDiff = indexDiff + (suggest.length() - (match.getToPos() - match.getFromPos()));
			}
			
		}
		System.out.println("CORRECTED : "+correctedWords.toString());
		return correctedWords.toString();
	}

	public static void main(String[] args) throws Exception {
		//String words = "is this i thinc it is clean bikas ice is mostlea cleen";
		String words = "evrything out doors needs water so we need water too beacuse if ther was no water nothingasdasdasdds could live only the things that dont need water could live";
		String corrected = spellCheck(words);
		
		System.out.println("OLD :"+words );
		System.out.println("NEW :"+corrected);
		
//		StringBuffer sb = new StringBuffer("jakabadan");
//		System.out.println(sb.toString());
//		sb.replace(3, 8, "nen");
//		System.out.println(sb.toString());
//		sb.replace(4, 5, "mum");
//		System.out.println(sb.toString());
	}
	
	public static void test() throws Exception {
		JLanguageTool langTool = new JLanguageTool(new AmericanEnglish());
		langTool.activateDefaultPatternRules();
		String example = "klean wotr";
		List<RuleMatch> matches = langTool.check(example);
		// List<RuleMatch> matches =
		// langTool.check("A sentence with a error in the Hitchhiker's Guide tot he Galaxy");
		for (RuleMatch match : matches) {
			System.out.println("Potential error at line " + match.getLine() + ", column " + match.getColumn() + " . word : "
					+ example.substring(match.getColumn() - 1, match.getEndColumn() - 1) + ": " + match.getMessage());
			System.out.println("Suggested correction: " + match.getSuggestedReplacements());
		}
	}
}
