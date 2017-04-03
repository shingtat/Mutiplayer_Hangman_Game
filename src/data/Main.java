package data;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

public class Main {
	String word;
	String mutatedWord;
	
	
	public Main(){
		word = "The GAlden Age";
		mutatedWord = "*** ****** ***";
	}
	
	
	public boolean mutateWordWithCharacter(String input){
		String temp = word.toLowerCase();
		char a = input.charAt(0);
		char lower = Character.toLowerCase(a);
		boolean mutated = true;
		int counter = 0;
		System.out.println("char: " + lower);
		
		StringBuilder mutate = new StringBuilder(mutatedWord);
		for(int i=0; i<temp.length(); i++){
			if(lower==temp.charAt(i)){
				mutate.setCharAt(i, word.charAt(i));
				mutatedWord = mutate.toString();
				counter++;
			}
			
		}
		if(counter>0){
			return true;
		}
		return false;
	}
	
	public boolean checkWholePhrase(String input){
		if(input.equals(word)){
			return true;
		}
		return false;
	}
	
	
	public static void main(String []args){
		
//		String file="/Users/Master/Documents/workspace/shingtat_CSCI201_Assignment4/valid1.xml";
//		
//		try {
//			DataStorage ds = new DataStorage(file);
//			System.out.println(ds.getRandom());
//			
//		} catch (CinemateException e) {
//			e.getMessage();
//		}
//		
		Main temp = new Main();
		boolean trial = temp.mutateWordWithCharacter("a");
		System.out.println(trial);
		System.out.println(temp.mutatedWord);
		
	}

}
