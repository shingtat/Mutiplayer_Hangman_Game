package shingtat_CSCI201_Assignment4;

import java.util.Vector;

public class Game {
	int players;
	boolean fullCapacity;
	String gameName;
	Vector<ChatThread> gameThreads;
	Vector<String> usernames;
	Vector<String> guessedCharacters;
	String host;
	ChatThread hostThread;
	String word;
	String mutatedWord;
	String guesses;
	Boolean isOver;
 	
	public Game(String name, int players, ChatThread ct){
		gameThreads = new Vector<ChatThread>();
		usernames = new Vector<String>();
		this.players = players;
		gameName = name;
		guessedCharacters = new Vector<String>();
		guesses="";
	}
	
	public void setGuesses(String guess){
		StringBuilder temp = new StringBuilder(guesses);
		temp.append(guess);
		guesses = temp.toString();
	}
	
	public String getGuesses(){
		return guesses;
	}
	
	public Vector<ChatThread> getGameThreads(){
		return gameThreads;
	}
	
	public Vector<String> getUsernames(){
		return usernames;
	}
	
	public void addThread(ChatThread ct){
		gameThreads.add(ct);
	}
	
	public void setHostString(String host){
		this.host = host;
	}
	
	public String getHostString(){
		return host;
	}
	
	public void setHostThread(ChatThread ct){
		hostThread = ct;
	}
	
	public ChatThread getHostThread(){
		return hostThread;
	}
	
	public void addUsername(String username){
		usernames.add(username);
	}
	
	public boolean isFullCapacity(){
		if(gameThreads.size()==players){
			return true;
		}
		return false;
	}
	
	public boolean isValidUsername(String u){
		if(usernames.contains(u)){
			return false;
		}
		return true;
	}
	
	public String getName(){
		return gameName;
	}
	
	public int getRemainingNeededToStartGame(){
		return players - gameThreads.size();
	}
	
	public String getWord(){
		return word;
	}
	
	public void mutateWord(){
		String temp = getWord();
		StringBuilder mutate = new StringBuilder();
		for(int i=0; i<temp.length(); i++){
			if(temp.charAt(i)==' '){
				mutate.append(' ');
			}
			else{
				mutate.append('*');
			}
		}
		mutatedWord = mutate.toString();
	}
	
	public boolean mutateWordWithCharacter(String input){
		System.out.println("in method?");
		System.out.println(word);
		String temp = word.toLowerCase();
		System.out.println("temp: " + temp);
		char a = input.charAt(0);
		char lower = Character.toLowerCase(a);
		int counter = 0;
		
		StringBuilder mutate = new StringBuilder(mutatedWord);
		for(int i=0; i<temp.length(); i++){
			if(lower==temp.charAt(i)){
				mutate.setCharAt(i, word.charAt(i));
				counter++;
			}
			
		}
		mutatedWord = mutate.toString();
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
	
	public void setWord(String word){
		this.word = word;
	}
	
	public void setMutatedWord(String input){
		mutatedWord = input;
	}
	
	public String getMutatedWord(){
		return mutatedWord;
	}
	
	public void sendMessageToAllClients(ChatThread self, String guess){
		for(ChatThread ct: gameThreads){
			if(ct.equals(self)){
				GameMessage gm = new GameMessage(0,"","You guessed " + guess);
			}
			else{
				GameMessage gm = new GameMessage(0,"", self.getUsername() + " guessed " + guess);
			}
		}
	}
	


}
