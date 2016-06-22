package edu.albany.cs.spellchecker;
import org.gauner.jSpellCorrect.spi.ToySpellingCorrector;


public class TestSpell {


	public static void main(String[] args){
		ToySpellingCorrector sc = new ToySpellingCorrector();
		// train some data from a text file
		sc.trainFile("./resources/english.0");
		// train a single word
		sc.trainSingle("some word");
		// get the best suggestion
		System.out.println(sc.correct("Cads"));
		System.out.println(sc.correct("Dok"));
		System.out.println(sc.correct("Speling"));
		System.out.println(sc.correct("helo"));
		System.out.println(sc.correct("wrold"));
	}
	
}
