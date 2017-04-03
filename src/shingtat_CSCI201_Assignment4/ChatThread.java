package shingtat_CSCI201_Assignment4;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket; // Command Shift O to import all classes
import java.util.Scanner;

public class ChatThread extends Thread { //Always extend thread when you always can. 
	
	private Socket s;
	private BufferedReader br;
	private PrintWriter pw;
	private Scanner scan;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private ChatServer cs;
	private String username;
	private Game game;
	private boolean isEliminated;
	
	int lives;
	
	public ChatThread(Socket s, ChatServer cs){
		this.s = s;
		this.cs = cs;	
		try {
			isEliminated = false;
			game=null;
			lives = 8;
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public void sendMessage(Object obj){
		try{
			oos.writeObject(obj);
			oos.flush();
		}catch(IOException ioe){
			System.out.println("ioe in chatThread: " + ioe.getMessage());
		}

	}
	
	public String getUsername(){
		return username;
	}
	
	public void setGame(Game game){
		this.game = game;
	}
	
	public Game getGame(){
		return game;
	}
	
	
	
	public void run(){
		Message message = null;
		String action="";
		String finalGameName="";
		int finalPlayers=0;
		try{

		while((message=(Message)ois.readObject())!=null){
			action = message.getAction();
			
			switch(action){
			case("isValidName"): //checks if game name is taken
				String gameName = (String)message.getObject();
				finalGameName=gameName;
				boolean isValidName = cs.validGameName(gameName);
				sendMessage(isValidName);
				break;
			case("isValidUsername"): //checks if username is taken
				String testUsername = (String)message.getObject();
				boolean isValidUsername = game.isValidUsername(testUsername);
				sendMessage(isValidUsername);
				break;
			case("username"): //receives incoming client username
				String username = (String)message.getObject();
				this.username = username;
				System.out.println("Server received message: threads username is: " + username);
				break;
			case("createGame"): //prompts server to create a game
				finalPlayers = (int)message.getObject();
				String word = cs.getWordForGame();
				this.game = cs.createGame(finalGameName, finalPlayers, this, word);
				break;
			case("gameExist"):  //Checks to see if game exists. If game exists,
								//checks to see if game is at full capacity.
								//sends a total of three messages, boolean whether game exists,
								//boolean whether game is at full capacity and games host name
				String gameExist = (String)message.getObject(); //gameName
				boolean gameExistence = cs.checkGameExistence(gameExist);
				sendMessage(gameExistence);
				if(gameExistence){
					boolean isFullCapacity = cs.getGame(gameExist).isFullCapacity();
					sendMessage(isFullCapacity);
					sendMessage(cs.findHost(gameExist));
					setGame(cs.getGame(gameExist));
				}
				break;
			case("clientConnect"): //What happens when client successfully joins a game
				String clientInfo = (String)message.getObject();
				System.out.println("client Info: " + clientInfo);
				String gn = clientInfo.split(":")[0];
				String clientUsername = clientInfo.split(":")[1];
				this.username = clientUsername;
				Game game = cs.getGame(gn);
				this.game = game;
				game.addUsername(clientUsername);
				game.addThread(this);
				cs.sendMessageToHostAboutGameStatus(game, this.username);
				break;
			case("startGame"):
				String gm = (String)message.getObject();
				Game g = cs.getGame(gm);
				cs.startGame(g);
				break;
			case("getWord"):
				String fgn = (String)message.getObject();
				Game gameTemp = cs.getGame(fgn);
				String wordBack = gameTemp.getMutatedWord();
				sendMessage(wordBack);
				break;
			case("eliminatedMessage"):
				System.out.println("in eliminated message" );
				GameMessage eM = new GameMessage(-1,"", "You have been eliminated. You may only spectate for the rest of the game.");
				sendMessage(eM);
				break;
				//Most of the game logic. 
			case("gameMessage"):
				String mutatedWord = "";
				String guesses = "";
				String toSend ="";
				String gameMessage = (String)message.getObject();
				System.out.println("in here: " + gameMessage);
				String[] finalMessage = gameMessage.split(":");
				String guess = finalMessage[0];
				String gameFinalName = finalMessage[1];
				Game tempGame = cs.getGame(gameFinalName);
				String winner = "";
				boolean gameOver = false;
				if(guess.length()==1){
					boolean mutated = tempGame.mutateWordWithCharacter(guess);
					System.out.println("mutated: " + mutated);
					if(!mutated){
						lives--;
						tempGame.setGuesses(guess);
						guesses = tempGame.getGuesses();
						mutatedWord = tempGame.getMutatedWord();
						toSend = "You guessed wrong :(";
						System.out.println("lives: " + lives);
						if(lives==0){
							isEliminated=true;
						}
					}
					else{
						guesses = tempGame.getGuesses();
						mutatedWord = tempGame.getMutatedWord();
						toSend = "You guessed correctly :)";
					}
					GameMessage validityMessage = new GameMessage(-1, "", toSend);
					sendMessage(validityMessage);
					cs.sendMessageToAllClientsExceptSelf(guess, this, tempGame); //Sends guess that player made
					if(isEliminated){
						String eliminated = "Your hangman has been completed. You lose :(";
						GameMessage eliminatedMessage = new GameMessage(-1,"", eliminated);
						sendMessage(eliminatedMessage);
						cs.sendEliminated(this.username, this, tempGame);
						isEliminated=false;
					}
					else{
						GameMessage gameMessageObj = new GameMessage(lives, mutatedWord, guesses);
						sendMessage(gameMessageObj);	
					}

				}//end of if
				
				else if(guess.length()>1){
					if(guess.equals(tempGame.getWord().toLowerCase())){
						toSend = "You Win!";
						tempGame.setMutatedWord(tempGame.getWord());
						mutatedWord = tempGame.getMutatedWord();
						winner = this.username;
						gameOver=true;
					}
					else{
						lives--;
						toSend = "You guessed the wrong phrase :(";
						mutatedWord = tempGame.getMutatedWord();
						if(lives==0){
							isEliminated=true;
						}
						
					}
					GameMessage validityMessage = new GameMessage(-1, "", toSend);
					sendMessage(validityMessage);
					cs.sendMessageToAllClientsExceptSelf(guess, this, tempGame); //Sends guess that player made
					if(isEliminated){
						String eliminated = "Your hangman has been completed. You lose :(";
						GameMessage eliminatedMessage = new GameMessage(-1,"", eliminated);
						sendMessage(eliminatedMessage);
						cs.sendEliminated(this.username, this, tempGame);
						isEliminated=false;
					}
					else{
						GameMessage gameMessageObj = new GameMessage(lives, mutatedWord, guesses);
						sendMessage(gameMessageObj);	
					}
				}//end of else if
				
				if(gameOver==true){
					cs.sendGameOver(winner, this, tempGame);
				}
			

				break;
			}
		}
			
		} catch(IOException ioe){
			System.out.println("ioe in ChatThread: " + ioe.getMessage());
		} catch(ClassNotFoundException cnf){
			System.out.println("cnf in ChatThread: "  + cnf.getMessage());
		}
	}
}
