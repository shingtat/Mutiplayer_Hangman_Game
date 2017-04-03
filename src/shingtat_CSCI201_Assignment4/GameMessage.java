package shingtat_CSCI201_Assignment4;

import java.io.Serializable;

//Used to send messages once the game starts
public class GameMessage implements Serializable {
	private static final long serialVersionUID = -2342323432342L;
	private int lives;
	private String word;
	private String guess;
	public GameMessage(int lives, String word, String guess){
		this.lives=lives;
		this.word = word;
		this.guess=guess;
	}
	
	public int getLives(){
		return lives;
	}
	
	public String getWord(){
		return word;
	}
	
	public String getGuess(){
		return guess;
	}
}
