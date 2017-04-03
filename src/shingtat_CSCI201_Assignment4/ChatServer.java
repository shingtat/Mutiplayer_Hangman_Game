package shingtat_CSCI201_Assignment4;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.Vector;

import data.CinemateException;
import data.DataStorage;

public class ChatServer {
	
	private Vector<Game> games;
	private Vector<String> gameNames;
	private Vector<ChatThread> chatThreads; //Single threaded, use array, multithreaded, use vector
	private static DataStorage ds;
	private ServerSocket ss;
	int players = 4;
	int counter=0;
	
	public ChatServer(int port) throws IOException{
		chatThreads = new Vector<ChatThread>();
		ss= new ServerSocket(port);
		ss.setReuseAddress(true);
		games = new Vector<Game>();
		gameNames = new Vector<String>();
	}
	
	public void setDataStorage(DataStorage ds){
		this.ds = ds;
	}
	
	public String getWordForGame(){
		return ds.getRandom();
	}
	
	public void start(){
		try{
			String userConcatenate="";
			while(true){
				Socket s = ss.accept(); //Blocking method, not going to move past this line once until we have client
				ChatThread ct = new ChatThread(s,this);
				ct.start();
				chatThreads.add(ct);
			}
		} catch (IOException ioe){
			System.out.println("ioe in ChatServer: " + ioe.getMessage());
		}

	}
	
	public boolean validGameName(String gameName){
		if(gameNames.contains(gameName)){
			return false;
		}
		return true;
	}
	
	public boolean checkGameExistence(String gameName){
		if(gameNames.contains(gameName)){
			return true;
		}
		return false;
	}
	
	public Game getGame(String gameName){
		Game desiredGame = null;
		for(Game game: games){
			if(game.getName().equals(gameName)){
				desiredGame = game;
			}
		}
		return desiredGame;
	}
	
	public Game createGame(String gameName, int players, ChatThread ct, String word){
		System.out.println("Game created!");
		Game game = new Game(gameName, players, ct);
		games.add(game);
		game.setWord(word);
		game.mutateWord();
		game.addThread(ct);
		game.addUsername(ct.getUsername());
		game.setHostThread(ct);
		game.setHostString(ct.getUsername());
		gameNames.add(game.getName());
		return game;
	}
	
	public void startGame(Game game){
		String toPrint = "";
		for(String username: game.getUsernames()){
			toPrint+=" " + username;
		}
		toPrint = "all players have joined: " + toPrint;
		
		Vector<ChatThread> temp = game.getGameThreads();
		for(int i=0; i<temp.size(); i++){
			if(game.getHostThread().equals(temp.get(i))){
				continue;
			}
			else{
				temp.get(i).sendMessage("all has joined");
				temp.get(i).sendMessage(toPrint);
			}
		}
	}
	
	public void sendMessageToHostAboutGameStatus(Game game, String clientUsername){
		ChatThread lol = game.getHostThread();
		
		String c1 = clientUsername + " joined the game";
		Message m1 = new Message(c1, "test");
		lol.sendMessage(m1);
		
		String c2 = "Waiting for " + game.getRemainingNeededToStartGame() + " players to join..";
		Message m2 = new Message(c2, "test");
		lol.sendMessage(m2);
	}
	
	public String findHost(String gameName){
		String host="";
		for(Game game: games){
			if(game.getName().equals(gameName)){
				host= game.getHostString();
			}
		}
		return host;
	}
	
	public void sendMessageToAllClientsExceptSelf(String guess, ChatThread self, Game game){
		Vector<ChatThread> gameThreads = game.getGameThreads();
		GameMessage clientGuess = null;
		GameMessage updateUI = null;
		for(ChatThread gt: gameThreads){
			if(gt.equals(self)){

			}
			else{
				clientGuess = new GameMessage(-1,"", self.getUsername() + " guessed " + guess);
				gt.sendMessage(clientGuess);
				System.out.println("mutated word: " + game.getMutatedWord());
				updateUI = new GameMessage(gt.lives, game.getMutatedWord(), game.getGuesses());
				gt.sendMessage(updateUI);
			}

		}
		
	}
	
	public void sendEliminated(String loser, ChatThread self, Game game){
		Vector<ChatThread> gameThreads = game.getGameThreads();
		GameMessage eliminatedMessage = null;
		for(ChatThread gt: gameThreads){
			if(gt.equals(self)){

			}
			else{
				eliminatedMessage = new GameMessage(-1,"", loser + " has been eliminated");
				gt.sendMessage(eliminatedMessage);

			}

		}
	}
	
	public void sendGameOver(String winner, ChatThread self, Game game){
		Vector<ChatThread> gameThreads = game.getGameThreads();
		GameMessage winnerMessage = null;
		GameMessage clientQuit = null;
		for(ChatThread gt: gameThreads){
			if(gt.equals(self)){

			}
			else{
				winnerMessage = new GameMessage(-1,"","You lose :( " + winner + " wins!");
				gt.sendMessage(winnerMessage);
			}
			clientQuit = new GameMessage(-2, "", "");
			gt.sendMessage(clientQuit);
		}
		gameNames.remove(game.getName());
		
	}
	
	
	public static void main(String [] args){
		Scanner scan = new Scanner(System.in);
		System.out.println("Please enter the port to host the server");
		int port=0;
		boolean condition=true;
		while(condition){
			try{
				port = scan.nextInt();
				scan.nextLine();
				while(port<=0){
					System.out.println("Invalid port. Please enter the port to host the server");
					port = scan.nextInt();
					scan.nextLine();
				}
				ChatServer cs = new ChatServer(port);
				System.out.println("Please enter the path to the xml file used for the game phrases");
				//String file="/Users/Master/Documents/workspace/shingtat_CSCI201_Assignment4/valid1.xml";
				String file = scan.nextLine();
				DataStorage ds = new DataStorage(file);
				cs.setDataStorage(ds);
				System.out.println("Server Started");
				cs.start();
				condition=false;
			}catch(InputMismatchException e){
				scan.nextLine();
				System.out.println("Invalid port. Please enter the port to host the server");
			}catch (CinemateException e) {
				System.out.println("Invalid port. Please enter the port to host the server");
			}catch (IOException ioe){
				System.out.println("Invalid Port. Please enter the port to host the server");
			}
		}
	
	}//end of main

} //end of chatserver class
